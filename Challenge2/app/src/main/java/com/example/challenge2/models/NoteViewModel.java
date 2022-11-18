package com.example.challenge2.models;

import org.json.*;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.challenge2.interfaces.AlertInterface;
import com.example.challenge2.notesDatabase.Note;
import com.example.challenge2.notesDatabase.Topic;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteViewModel extends AndroidViewModel {
    private final NoteRepository noteRepository;
    private final MutableLiveData<List<Note>> notesByTitle;
    private final LiveData<List<Note>> allNotes;

    private Note noteSelected;

    private final TopicRepository topicRepository;
    private final MutableLiveData<List<Topic>> topicsByTitle;
    private final LiveData<List<Topic>> allTopics;
    private Topic topicSelected;

    private String searchTextNote;
    private String searchTextTopic;

    private MQTTHelper client;

    private List<Topic> topicsSubscribedAndSaved;

    public NoteViewModel(Application application, AlertInterface alertInterface) {
        super(application);
        noteRepository = new NoteRepository(application);
        allNotes = noteRepository.getAllNotes();
        notesByTitle = noteRepository.getNotesByTitle();
        noteSelected = null;
        searchTextNote = "";
        searchTextTopic = "";

        topicRepository = new TopicRepository(application);
        allTopics = topicRepository.getAllTopics();
        topicsByTitle = topicRepository.getTopicsByTitle();
        topicSelected = null;
        topicsSubscribedAndSaved=new ArrayList<>();

        String android_id = Settings.Secure.getString(application.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        client = new MQTTHelper(application.getApplicationContext(), android_id);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                alertInterface.showToast("Connection with MQTT established.");
                getAllTopics().observeForever( topics -> {
                    for (Topic topic: topics){
                        if (!topicsSubscribedAndSaved.contains(topic)){
                            subscribeToTopic(topic.getTitle());
                            topicsSubscribedAndSaved.add(topic);
                        }
                    }
                });
            }
            @Override
            public void connectionLost(Throwable cause) {
                alertInterface.showToast("Connection with MQTT Broker lost. Restart the app please.");
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.w("NoteViewModel", "message received");
                try {
                    JSONObject obj = new JSONObject(message.toString());
                    String title = obj.getJSONObject("message").getString("title");
                    String body = obj.getJSONObject("message").getString("body");
                    alertInterface.onMessageReceive(new Note(title, body));
                } catch (Exception e){
                    Log.w("NoteViewModel",e);
                    Log.w("NoteViewModel", "Message content format not valid.");
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                alertInterface.showToast("Message delivered successfully");
            }
        });
        client.connect();

    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public MutableLiveData<List<Note>> getNotesByTitle() {
        return notesByTitle;
    }

    public void insert(Note note) {
        noteRepository.insert(note);
    }

    public void deleteNote(Note note) {
        noteRepository.delete(note);
    }

    public Note getNoteSelected() {
        return this.noteSelected;
    }

    public void setNoteSelected(Note note) {
        this.noteSelected = note;
    }

    public void updateNoteSelected(String title, String body) {
        noteSelected.setTitle(title);
        noteSelected.setBody(body);
        noteRepository.insert(noteSelected);
    }

    public void updateNotesByTitle() {
        noteRepository.updateNotesByTitle(this.searchTextNote);
    }

    public String getSearchTextNote() {
        return this.searchTextNote;
    }

    public void setSearchTextNote(String text) {
        this.searchTextNote = text;
    }

    public String getSearchTextTopic() {
        return this.searchTextTopic;
    }

    public void setSearchTextTopic(String text) {
        this.searchTextTopic = text;
    }



    public void subscribeToTopic(String topic) {
        client.subscribeToTopic(topic);
    }

    public void unsubscribeFromTopic(String topic) {
        client.unsubscribeFromTopic(topic);
    }

    public void sendToTopic(Note note, String topic) {
        client.sendToTopic(note, topic);
    }


    public LiveData<List<Topic>> getAllTopics() {
        return allTopics;
    }

    public MutableLiveData<List<Topic>> getTopicsByTitle() {
        return topicsByTitle;
    }

    public void insertTopic(Topic topic) {
        topicRepository.insert(topic);
    }
    public void deleteTopic(Topic topic) {
        topicRepository.delete(topic);
    }

    public Topic getTopicSelected() {
        return this.topicSelected;
    }

    public void setTopicSelected(Topic topic) {
        this.topicSelected = topic;
    }

    public void updateTopicSelected(String title) {
        if (!Objects.equals(title, topicSelected.getTitle())) {
            unsubscribeFromTopic(topicSelected.getTitle());
            topicSelected.setTitle(title);
            topicRepository.insert(topicSelected);
        }
    }
    public void updateTopicsByTitle() {
        topicRepository.updateTopicsByTitle(this.searchTextTopic);
    }

}
