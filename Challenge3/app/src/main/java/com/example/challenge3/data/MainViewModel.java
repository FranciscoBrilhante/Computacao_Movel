package com.example.challenge3.data;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.challenge3.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainViewModel extends AndroidViewModel implements MqttCallbackExtended {
    private final MainRoomDatabase db;
    private final SampleDao sampleDao;
    private final SensorDao sensorDao;
    private static String LOG_TAG;
    private final MQTT mqttClient;

    public MainViewModel(Application application) {
        super(application);
        LOG_TAG = "viewModel";
        db = MainRoomDatabase.getDatabase(application);
        sampleDao = db.sampleDao();
        sensorDao = db.sensorDao();

        String androidID = Settings.Secure.getString(application.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        mqttClient = new MQTT(application.getApplicationContext(), androidID);
        mqttClient.setCallback(this);
        mqttClient.connect();
    }

    public LiveData<List<Sample>> getAllSamples() {
        return sampleDao.getAll();
    }

    public List<Sensor> getAllSensors() {
        return sensorDao.getAll();
    }

    public LiveData<List<Sensor>> getAllSensorsLiveData(){
        return sensorDao.getAllLiveData();
    }

    public  List<Sensor> getSensorsByName(String name){
        return sensorDao.getByName(name);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mqttClient.stop();
    }

    public void insertSample(Sample sample) {
        MainRoomDatabase.databaseWriteExecutor.execute(() -> {
            String sensorName=sample.getSensor();
            if(sensorDao.getByName(sensorName).get(0).isSaveData()){
                sampleDao.insert(sample);
            }
        });
    }

    public void updateSaveData(boolean isSaveData, String sensorName) {
        MainRoomDatabase.databaseWriteExecutor.execute(() -> {
            sensorDao.updateSaveData(isSaveData, sensorName);
        });
    }

    public void updateThreshold(double threshold, String sensorName) {
        MainRoomDatabase.databaseWriteExecutor.execute(() -> {
            sensorDao.updateThreshold(threshold, sensorName);
        });
    }

    public void deleteSamplesBySensor(String sensorName){
        MainRoomDatabase.databaseWriteExecutor.execute(() -> {
            sampleDao.deleteBySensor(sensorName);
        });
    }

    public void sendMessage(String topic, String message){
        mqttClient.sendToTopic(topic,message);
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.w(LOG_TAG, "reconnected to mqtt");
        this.sendMessage("dynamic_led_topic", "0");
        getAllSensorsLiveData().observeForever(sensors -> {
            for (Sensor sensor : sensors) {
                if (sensor.isSaveData()) {
                    mqttClient.subscribeToTopic(sensor.getMqttTopic());
                } else {
                    mqttClient.unsubscribeFromTopic(sensor.getMqttTopic());
                }
            }
        });
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.w(LOG_TAG, "Lost connection");

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.w(LOG_TAG, "message arrived: " + message.toString());
        Double value=Double.parseDouble(message.toString());
        switch (topic){
            case "dynamic_humidity_topic":
                insertSample(new Sample(Calendar.getInstance(),value , "Humidity"));
                break;
            case "dynamic_temperature_topic":
                insertSample(new Sample(Calendar.getInstance(), Double.parseDouble(message.toString()), "Temperature"));
                break;
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public List<Sample> getAllSamplesList(){
        return sampleDao.getAllList();
    }
}
