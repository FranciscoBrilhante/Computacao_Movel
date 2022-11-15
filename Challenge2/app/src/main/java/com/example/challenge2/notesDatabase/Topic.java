package com.example.challenge2.notesDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "topic_table")
public class Topic {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "title")
    private String title;

    public Topic(String title) {
        this.title = title;

    }
    public String getTitle() {
        return this.title;
    }

    public int getUid() {
        return this.uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
