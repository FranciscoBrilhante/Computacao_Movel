package com.example.challenge3.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SensorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Sensor sensor);

    @Query("DELETE FROM sensor_table")
    void deleteAll();

    @Query("SELECT * FROM sensor_table")
    LiveData<List<Sensor>> getAll();

    @Delete
    void delete(Sensor sensor);

    @Query("SELECT * FROM sensor_table WHERE name=:name")
    List<Sensor> getByName(String name);

    @Query("UPDATE sensor_table SET save_data= :isSaveData WHERE name=:name")
    void updateSaveData(boolean isSaveData,String name);
}
