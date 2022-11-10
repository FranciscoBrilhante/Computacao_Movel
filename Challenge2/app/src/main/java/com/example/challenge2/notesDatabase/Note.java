package com.example.challenge2.notesDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "body")
    private String body;


    public Note(String title, String body){
        this.title=title;
        this.body=body;
    }

    public String getTitle(){
        return  this.title;
    }

    public String getBody() {
        return this.body;
    }

    public int getUid() {
        return this.uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public void setBody(String body){
        this.body=body;
    }
}
