package com.example.challenge2.models;

import org.json.*;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.challenge2.notesDatabase.Note;

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
    private String searchText;

    public NoteViewModel(Application application) {
        super(application);
        noteRepository = new NoteRepository(application);
        allNotes = noteRepository.getAllNotes();
        notesByTitle=noteRepository.getNotesByTitle();
        noteSelected = null;
        searchText="";
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
        MQTTHelper messageHelper = new MQTTHelper(getApplication().getApplicationContext(),"cliente",topic);

        messageHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                messageHelper.subscribeToTopic(topic);
            }

            @Override
            public void connectionLost(Throwable cause) {
                messageHelper.stop();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("message received");
                JSONObject obj = new JSONObject(message.toString());
                String title = obj.getJSONObject("message").getString("title");
                String body = obj.getJSONObject("message").getString("body");
                System.out.println(title);
                System.out.println(body);
                insert(new Note(title,body));
                System.out.println("message saved");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        messageHelper.connect();
    }



}
