package com.example.market.marketDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Query("SELECT * FROM product_table")
    LiveData<List<Product>> getAll();

    @Query("SELECT * FROM product_table")
    List<Product> getAllList();

    @Query("DELETE FROM product_table")
    void deleteAll();

    @Query("DELETE FROM product_table WHERE id=:id")
    void deleteByID(int id);

}
