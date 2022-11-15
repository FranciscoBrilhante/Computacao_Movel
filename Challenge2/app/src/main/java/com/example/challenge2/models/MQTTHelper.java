package com.example.challenge2.models;


import android.content.Context;
import android.util.Log;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;



public class MQTTHelper {
    public MqttAndroidClient mqttAndroidClient;

    final String server = "tcp://broker.hivemq.com:1883";
    final String TAG = "TAG";
    private String name;


    public MQTTHelper(Context context, String name, String topic) {
        this.name = name;

        mqttAndroidClient = new MqttAndroidClient(context, server, name, Ack.AUTO_ACK);
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);

        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {

                //Adjusting the set of options that govern the behaviour of Offline (or Disconnected) buffering of messages
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                Log.w(TAG,"Connected to b broker");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w(TAG, "Failed to connect to: " + server + exception.toString());
            }
        });


    }

    public void stop() {
        mqttAndroidClient.disconnect();
    }

    public void subscribeToTopic(String topic) {
        mqttAndroidClient.subscribe(topic, 2, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.w(TAG, "Subscribed!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w(TAG, "Subscribed fail!");
            }
        });

    }

    public String getName() {
        return name;
    }
}
