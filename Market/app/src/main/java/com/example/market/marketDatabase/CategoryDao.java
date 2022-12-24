package com.example.market.marketDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Query("SELECT * FROM category_table")
    LiveData<List<Category>> getAll();

    @Query("SELECT * FROM category_table")
    List<Category> getAllList();

    @Query("DELETE FROM category_table")
    void delete();

    @Query("SELECT * FROM category_table WHERE name=:name")
    List<Category> getByName(String name);

    @Query("SELECT * FROM category_table WHERE id=:id")
    List<Category> getByID(int id);

}
