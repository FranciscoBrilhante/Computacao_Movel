package com.example.challenge3.data;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "sample_table")
public class Sample {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "timestamp")
    private Date timestamp;

    @ColumnInfo(name = "reading_value")
    private double readingValue;

    @ColumnInfo(name = "sensor")
    private int sensor;

    public Sample(Date timestamp, double readingValue, int sensor) {
        this.timestamp = timestamp;
        this.readingValue = readingValue;
        this.sensor = sensor;
    }

    public int getUid() {
        return uid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public double getReadingValue() {
        return readingValue;
    }

    public int getSensor() {
        return sensor;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setReadingValue(float readingValue) {
        this.readingValue = readingValue;
    }

    public void setSensorName(int sensor) {
        this.sensor = sensor;
    }
}
