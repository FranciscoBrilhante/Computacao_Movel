package com.example.market.marketDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contact_table")
public class Contact {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "profileID")
    private int profileID;

    @ColumnInfo(name = "profileImage")
    private String profileImage;

    @ColumnInfo(name = "profileName")
    private String profileName;

    @ColumnInfo(name = "lastMessage")
    private String lastMessage;

    public Contact( int profileID, String profileImage, String profileName, String lastMessage) {
        this.profileID = profileID;
        this.profileImage = profileImage;
        this.profileName = profileName;
        this.lastMessage = lastMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
