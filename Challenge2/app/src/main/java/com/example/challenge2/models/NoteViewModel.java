package com.example.challenge2.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.challenge2.notesDatabase.Note;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository noteRepository;
    private final LiveData<List<Note>> allNotes;
    private Note noteSelected;

    public NoteViewModel(Application application){
        super(application);
        noteRepository=new NoteRepository(application);
        allNotes= noteRepository.getAllNotes();
        noteSelected=null;
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    public void insert(Note note){
        noteRepository.insert(note);
    }

    public Note getNoteSelected(){
        return this.noteSelected;
    }

    public void setNoteSelected(Note note){
        this.noteSelected=note;
    }

    public void updateNoteSelected(String title,String body){
        noteSelected.setTitle(title);
        noteSelected.setBody(body);
        noteRepository.insert(noteSelected);
    }

    public void deleteNote(Note note){
        noteRepository.delete(note);
    }



}
