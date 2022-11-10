package com.example.challenge2.models;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.challenge2.notesDatabase.Note;
import com.example.challenge2.notesDatabase.NoteDao;
import com.example.challenge2.notesDatabase.NoteRoomDatabase;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    NoteRepository(Application application){
        NoteRoomDatabase db=NoteRoomDatabase.getDatabase(application);
        noteDao=db.noteDao();
        allNotes=noteDao.getAllNotes();
    }

    LiveData<List<Note>> getAllNotes(){
        return this.allNotes;
    }

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
}
