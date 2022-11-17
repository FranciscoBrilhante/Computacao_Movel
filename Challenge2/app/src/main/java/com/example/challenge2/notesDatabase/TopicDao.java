package com.example.challenge2.notesDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TopicDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Topic topic);

    @Query("DELETE FROM topic_table")
    void deleteAll();

    @Query("SELECT * FROM topic_table WHERE title LIKE '%'||:title||'%' ")
    List<Topic> getTopicsByTitle(String title);

    @Query("SELECT * FROM topic_table")
    LiveData<List<Topic>> getAllTopics();

    @Delete
    void delete(Topic topic);
}
