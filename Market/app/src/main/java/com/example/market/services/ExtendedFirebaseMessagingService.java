package com.example.market.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.market.BuildConfig;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.ui.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ExtendedFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Intent intent=new Intent();
        intent.setAction("sendToken");
        intent.putExtra("token",token);
        sendBroadcast(intent);

        System.out.println("-----------------Token----------------");
        System.out.println(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if(message.getNotification()!=null){
            System.out.println("received notification");

            Intent intent=new Intent();
            intent.setAction("postNotification");
            intent.putExtra("title",message.getNotification().getTitle());
            intent.putExtra("body",message.getNotification().getBody());
            sendBroadcast(intent);
        }
    }
}
