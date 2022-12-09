package com.example.challenge3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.challenge3.data.MainViewModel;
import com.example.challenge3.data.Sample;
import com.example.challenge3.data.Sensor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.challenge3.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    private static final String TEMP_CHANNEL_ID = "1";
    private static final String HUMI_CHANNEL_ID = "2";
    private final String LOG_TAG = "mainActivity";
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private int notificationCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        notificationCount = 1;
        createNotificationChannels();
        watchForPotentialNewNotifications();
    }

    private void createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = getString(R.string.humidity_channel_name);
        String description = getString(R.string.humidity_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(TEMP_CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        name = getString(R.string.temperature_channel_name);
        description = getString(R.string.temperature_channel_description);
        NotificationChannel channel1 = new NotificationChannel(HUMI_CHANNEL_ID, name, importance);
        channel1.setDescription(description);
        notificationManager.createNotificationChannel(channel1);
    }

    private void sendNotification(String title, String contentText, String channelID) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationCount, builder.build());
        notificationCount += 1;
    }

    private void watchForPotentialNewNotifications() {
        ArrayList<Date> oldSamplesDates = new ArrayList<>();
        viewModel.getAllSamples().observe(this, samples -> {
            if(!samples.isEmpty() && oldSamplesDates.isEmpty()){
                for(Sample sample: samples){
                    oldSamplesDates.add(sample.getTimestamp());
                }
                return;
            }
            for (Sample sample : samples) {
                if (!oldSamplesDates.contains(sample.getTimestamp())) {
                    oldSamplesDates.add(sample.getTimestamp());

                    Executor executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        Sensor sensor = viewModel.getSensorsByName(sample.getSensor()).get(0);

                        if (sample.getReadingValue() >= sensor.getThreshold()) {
                            if (sample.getSensor().equals("Humidity")) {
                                sendNotification("Humidity threshold reached", String.valueOf(sample.getReadingValue()), HUMI_CHANNEL_ID);
                            } else {
                                sendNotification("Temperature threshold reached", String.valueOf(sample.getReadingValue()), TEMP_CHANNEL_ID);
                            }
                            Log.w(LOG_TAG, "Sending notification");
                        }
                    });
                }
            }
        });
    }
}