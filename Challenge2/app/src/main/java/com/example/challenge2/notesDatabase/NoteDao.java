package com.example.challenge2.notesDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);

    @Query("DELETE FROM note_table")
    void deleteAll();

    @Query("SELECT * FROM note_table WHERE title LIKE '%'||:title||'%' ")
    LiveData<List<Note>> getNotesByTitle(String title);

    @Query("SELECT * FROM note_table")
    LiveData<List<Note>> getAllNotes();

    @Delete
    void delete(Note note);
}
