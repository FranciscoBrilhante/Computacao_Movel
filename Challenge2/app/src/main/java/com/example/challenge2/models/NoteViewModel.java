package com.example.challenge2.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.challenge2.notesDatabase.Note;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository noteRepository;
    private final LiveData<List<Note>> allNotes;

    public NoteViewModel(Application application){
        super(application);
        noteRepository=new NoteRepository(application);
        allNotes= noteRepository.getAllNotes();
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    public void insert(Note note){
        noteRepository.insert(note);
    }

}
