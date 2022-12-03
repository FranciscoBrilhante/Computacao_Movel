package com.example.challenge3.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SampleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Sample sample);

    @Query("DELETE FROM sample_table")
    void deleteAll();

    @Query("SELECT * FROM sample_table")
    LiveData<List<Sample>> getAll();

    @Delete
    void delete(Sample sample);

    @Query("SELECT * FROM sample_table WHERE sensor= :sensorID")
    LiveData<List<Sample>> getBySensorID(int sensorID);
}
