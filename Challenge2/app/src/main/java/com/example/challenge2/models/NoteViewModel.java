package com.example.challenge2.models;

import org.json.*;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.challenge2.notesDatabase.Note;
import com.example.challenge2.notesDatabase.Topic;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private final NoteRepository noteRepository;
    private final MutableLiveData<List<Note>> notesByTitle;
    private final LiveData<List<Note>> allNotes;

    private Note noteSelected;

    private final TopicRepository topicRepository;
    private final MutableLiveData<List<Topic>> topicsByTitle;
    private final LiveData<List<Topic>> allTopics;
    private Topic topicSelected;

    private String searchText;

    private final MQTTHelper client;

    public NoteViewModel(Application application) {
        super(application);
        noteRepository = new NoteRepository(application);
        allNotes = noteRepository.getAllNotes();
        notesByTitle=noteRepository.getNotesByTitle();
        noteSelected = null;
        searchText="";

        topicRepository = new TopicRepository(application);
        allTopics = topicRepository.getAllTopics();
        topicsByTitle = topicRepository.getTopicsByTitle();
        topicSelected = null;

        client = new MQTTHelper(application.getApplicationContext(),"client");
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
            }

            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("message received");

                JSONObject obj = new JSONObject(message.toString());
                String title = obj.getJSONObject("message").getString("title");
                String body = obj.getJSONObject("message").getString("body");

                noteRepository.insert(new Note(title,body));
                System.out.println("message saved");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

        client.connect();



        //TODO garantir que a base de dados já está carregada (senao isto devolve null às vezes)
        List<Topic> topicList = getAllTopics().getValue();
        System.out.println(topicList);
        if(topicList != null)
            for (Topic t : topicList)
                subscribeToTopic(t.getTitle());

    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public MutableLiveData<List<Note>> getNotesByTitle(){
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
        noteRepository.updateNotesByTitle(this.searchText);
    }

    public String getSearchText() {
        return this.searchText;
    }

    public  void setSearchText(String text){
        this.searchText=text;
    }



    public void subscribeToTopic(String topic){
        client.subscribeToTopic(topic);
    }

    public void unsubscribeFromTopic(String topic){
        client.unsubscribeFromTopic(topic);
    }


    public LiveData<List<Topic>> getAllTopics() {
        return allTopics;
    }

    public MutableLiveData<List<Topic>> getTopicsByTitle(){
        return topicsByTitle;
    }

    public void insertTopic(Topic topic) {
        topicRepository.insert(topic);
        this.subscribeToTopic(topic.getTitle());
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
        if(title != topicSelected.getTitle()){
            topicSelected.setTitle(title);
            topicRepository.insert(topicSelected);
            this.subscribeToTopic(title);
        }
    }

    public void updateTopicsByTitle() {
        topicRepository.updateTopicsByTitle(this.searchText);
    }


}
