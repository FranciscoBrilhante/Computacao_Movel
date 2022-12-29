package com.example.market.marketDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Contact contact);

    @Query("SELECT * FROM contact_table")
    LiveData<List<Contact>> getAll();

    @Query("SELECT * FROM contact_table")
    List<Contact> getAllList();

    @Query("DELETE FROM contact_table")
    void deleteAll();

    @Query("DELETE FROM contact_table WHERE id=:id")
    void deleteByID(int id);
}
