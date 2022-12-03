package com.example.challenge3.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "sensor_table")
public class Sensor {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "save_data")
    private boolean saveData;

    @ColumnInfo(name = "threshold")
    private float threshold;

    @ColumnInfo(name="mqtt_topic")
    private String mqttTopic;

    public Sensor(String name,boolean saveData, float threshold, String mqttTopic) {
        this.name=name;
        this.saveData=saveData;
        this.threshold=threshold;
        this.mqttTopic=mqttTopic;
    }

    public String getMqttTopic() {
        return mqttTopic;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isSaveData() {
        return saveData;
    }

    public void setSaveData(boolean saveData) {
        this.saveData = saveData;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public void setMqttTopic(String mqttTopic) {
        this.mqttTopic = mqttTopic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
