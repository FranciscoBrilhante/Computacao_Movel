package com.example.market.marketDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Message message);

    @Query("SELECT * FROM message_table")
    LiveData<List<Message>> getAll();

    @Query("SELECT * FROM message_table")
    List<Message> getAllList();

    @Query("DELETE FROM message_table")
    void deleteAll();

    @Query("DELETE FROM message_table WHERE id=:id")
    void deleteByID(int id);

}
