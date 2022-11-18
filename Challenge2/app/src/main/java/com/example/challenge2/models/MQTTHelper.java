package com.example.challenge2.models;


import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.*;

import com.example.challenge2.notesDatabase.Note;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;


import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;


public class MQTTHelper {
    public MqttAndroidClient client;
    final String server = "tcp://broker.hivemq.com:1883";
    final String TAG = "MQTTHelper";
    private final String name;

    public MQTTHelper(Context context, String name) {
        this.name = name;
        client = new MqttAndroidClient(context, server, name, Ack.AUTO_ACK);
    }

    public void setCallback(MqttCallbackExtended callback) {
        client.setCallback(callback);
    }

    public void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);

        client.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                client.setBufferOpts(disconnectedBufferOptions);
                Log.w(TAG, "Connected to broker");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w(TAG, "Failed to connect to: " + server + exception.toString());
            }
        });
    }

    public void stop() {
        client.disconnect();
    }

    public void subscribeToTopic(String topic) {
        client.subscribe(topic, 2, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.w(TAG, "Subscribed to topic " + topic + "!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w(TAG, "Subscribed fail!");
                Log.w(TAG, exception.toString());
            }
        });
    }

    public void unsubscribeFromTopic(String topic) {
        client.unsubscribe(topic);
    }

    public void sendToTopic(Note note, String topic) {
        try {
            byte[] encodedPayload;
            String msg = "{\"message\":{\"title\":\"" + note.getTitle() + "\",\"body\":\"" + note.getBody() + "\"}}";
            encodedPayload = msg.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setQos(2);
            client.publish(topic, message);

        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "Encoding Exception");
        }
    }

    public String getName() {
        return name;
    }

}
