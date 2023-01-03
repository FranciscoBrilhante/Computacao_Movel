package com.example.market.marketDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_table")
public class Category {

    @PrimaryKey(autoGenerate = false)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "namePT")
    private String namePT;

    public Category(int id, String name,String namePT) {
        this.id = id;
        this.name = name;
        this.namePT=namePT;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePT() {
        return namePT;
    }

    public void setNamePT(String namePT) {
        this.namePT = namePT;
    }
}
