package com.example.challenge2.models;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.challenge2.notesDatabase.Note;
import com.example.challenge2.notesDatabase.NoteDao;
import com.example.challenge2.notesDatabase.NoteRoomDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteRepository {
    private final NoteDao noteDao;
    private final LiveData<List<Note>> allNotes;
    private final MutableLiveData<List<Note>> notesByTitle;

    NoteRepository(Application application){
        NoteRoomDatabase db=NoteRoomDatabase.getDatabase(application);
        noteDao=db.noteDao();
        allNotes=noteDao.getAllNotes();
        notesByTitle=new MutableLiveData<>();
    }

    LiveData<List<Note>> getAllNotes(){
        return this.allNotes;
    }

    MutableLiveData<List<Note>> getNotesByTitle(){return this.notesByTitle;}

    void insert(Note note){
        NoteRoomDatabase.databaseWriteExecutor.execute(()->{
            noteDao.insert(note);
        });
    }

    void delete(Note note){
        NoteRoomDatabase.databaseWriteExecutor.execute(()->{
            noteDao.delete(note);
        });
    }

    void updateNotesByTitle(String title){

        NoteRoomDatabase.databaseWriteExecutor.execute(()->{
            notesByTitle.postValue(noteDao.getNotesByTitle(title));
        });
    }


}
