package com.example.market.marketDatabase;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "message_table")
public class Message {
    @PrimaryKey(autoGenerate = false)
    private int id;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "timestamp")
    private Calendar timestamp;

    @ColumnInfo(name = "sender")
    private int sender;

    @ColumnInfo(name = "receiver")
    private int receiver;

    public Message(int id, String content, Calendar timestamp, int sender, int receiver) {
        this.id=id;
        this.content = content;
        this.timestamp = timestamp;
        this.sender = sender;
        this.receiver = receiver;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }
}
