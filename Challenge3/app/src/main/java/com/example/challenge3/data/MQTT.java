package com.example.challenge3.data;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;


import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


public class MQTT {
    public MqttAndroidClient client;
    final String serverAddress = "tcp://broker.hivemq.com:1883";
    final String LOG_TAG = "MQTTHelper";
    private final String clientName;
    private final Context context;

    public MQTT(Context context, String clientName) {
        this.clientName = clientName;
        this.context=context;

        client = new MqttAndroidClient(context, serverAddress, clientName, Ack.AUTO_ACK);
        Log.w(LOG_TAG,"Created this time");
    }

    public void setCallback(MqttCallbackExtended callback) {
        client.setCallback(callback);
    }

    public void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);

        client.connect(mqttConnectOptions, context, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                client.setBufferOpts(disconnectedBufferOptions);
                Log.w(LOG_TAG, "Connected to broker");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w(LOG_TAG, "Failed to connect to: " + serverAddress + exception.toString());
            }
        });
    }

    public void stop() {
        client.disconnect();
    }

    public void subscribeToTopic(String topic) {
        client.subscribe(topic, 2, context, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.w(LOG_TAG, "Subscribed to topic " + topic + "!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w(LOG_TAG, "Subscribed fail!");
                Log.w(LOG_TAG, exception.toString());
            }
        });
    }

    public void unsubscribeFromTopic(String topic) {
        client.unsubscribe(topic);
        Log.w(LOG_TAG, "Unsubscribed to topic " + topic + "!");
    }

    public void sendToTopic(String topic, String messageContent) {
        MqttMessage message = new MqttMessage(messageContent.getBytes(StandardCharsets.UTF_8));
        message.setQos(2);
        client.publish(topic, message);
    }


}
