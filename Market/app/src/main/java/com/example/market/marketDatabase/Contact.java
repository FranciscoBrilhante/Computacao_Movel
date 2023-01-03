package com.example.market.marketDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "contact_table")
public class Contact {
    @PrimaryKey(autoGenerate = false)
    private int profileID;

    @ColumnInfo(name = "profileImage")
    private String profileImage;

    @ColumnInfo(name = "profileName")
    private String profileName;

    @ColumnInfo(name = "lastMessage")
    private String lastMessage;

    @ColumnInfo(name = "lastMessageTimestamp")
    private Calendar lastMessageTimestamp;

    public Contact( int profileID, String profileImage, String profileName, String lastMessage, Calendar lastMessageTimestamp) {
        this.profileID = profileID;
        this.profileImage = profileImage;
        this.profileName = profileName;
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp=lastMessageTimestamp;
    }

    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Calendar getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Calendar lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
}
