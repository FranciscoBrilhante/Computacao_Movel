package com.example.market.services;

import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.market.BuildConfig;
import com.example.market.interfaces.HTTTPCallback;
import com.google.firebase.messaging.FirebaseMessagingService;

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
}
