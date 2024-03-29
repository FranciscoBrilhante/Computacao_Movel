package com.example.market.ui.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.ActivityAdminBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.marketDatabase.Category;
import com.example.market.marketDatabase.Contact;
import com.example.market.marketDatabase.Message;
import com.example.market.marketDatabase.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity implements HTTTPCallback {

    private ActivityAdminBinding binding;
    public MarketViewModel viewModel;
    private BottomNavigationView bottomNav;

    private static final String MESSAGE_CHANNEL_ID = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //Disable auto dark mode
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomNav = findViewById(R.id.nav_view_admin);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_admin);
        navController.addOnDestinationChangedListener(this.onDestinationChangedListener);
        NavigationUI.setupWithNavController(binding.navViewAdmin, navController);

        //if no credentials found redirect to login screen
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        if (!viewModel.areCredentialsStored()) {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
            return;
        }

        //send login request
        Map<String, Object> params = viewModel.getStoredCredentials();
        viewModel.sendRequest("/profile/login", "POST", null, params, true, true, false, this);

        enableNotifications();
        createNotificationChannels();

        //receive new token broadcasts from service
        Intent intent = new Intent(this, AdminActivity.ExtendedBroadcastReceiver.class);
        startService(intent);
        retrieveLastTokenAndSend();
        IntentFilter filter = new IntentFilter();
        filter.addAction("sendToken");
        filter.addAction("postNotification");
        registerReceiver(new AdminActivity.ExtendedBroadcastReceiver(), filter);
    }

    @Override
    public void onComplete(JSONObject data) {
        Integer code;
        String url1 = "/profile/login";
        String url2 = "/category/all";
        String url3 = "/product/recommended";
        String url4 = "/message/users";
        String url5 = "/message/withuser";
        String url6 = "/product/myproducts";
        try {
            String endpoint = (String) data.get("endpoint");
            if (endpoint.equals(url1)) {
                code = (Integer) data.get("status");
                if (code == 200) {
                    viewModel.sendRequest("/category/all", "GET", null, null, false, false, true, this);
                    Map<String, Object> params=new LinkedHashMap<>();
                    params.put("page",Integer.toString(1));
                    viewModel.sendRequest("/product/recommended", "GET", params, null, false, false, true, this);
                    viewModel.sendRequest("/product/myproducts", "GET", null, null, false, false, true, this);
                } else {
                    viewModel.removeStoredCredentials();
                    Intent myIntent = new Intent(this, LoginActivity.class);
                    startActivity(myIntent);
                }
            } else if (endpoint.equals(url2)) {
                code = (Integer) data.get("status");
                if (code == 200) {
                    ArrayList<Category> categories = viewModel.categoriesFromJSONObject(data);
                    viewModel.addCategories(categories);
                }
            } else if (endpoint.equals(url3)) {
                code = (Integer) data.get("status");
                if (code == 200) {
                    ArrayList<Product> products = viewModel.productsFromJSONObject(data);
                    viewModel.addProducts(products);
                }
            } else if (endpoint.equals(url4)) {
                code = (Integer) data.get("status");
                if (code == 200) {
                    ArrayList<Contact> contacts = viewModel.contactsFromJSONObject(data);
                    viewModel.addContacts(contacts);
                    for (Contact contact : contacts) {
                        Map<String, Object> params = new LinkedHashMap<>();
                        params.put("profile_id", Integer.toString(contact.getProfileID()));
                        viewModel.sendRequest("/message/withuser", "GET", params, null, false, false, true, this);
                    }
                }
            }else if (endpoint.equals(url5)) {
                code = (Integer) data.get("status");
                if (code == 200) {
                    ArrayList<Message> messages = viewModel.messagesFromJSONObject(data);
                    viewModel.addMessages(messages);
                }
            }else if (endpoint.equals(url6)) {
                code = (Integer) data.get("status");
                if (code == 200) {
                    ArrayList<Product> products = viewModel.productsFromJSONObject(data);
                    viewModel.addProducts(products);
                }
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    NavController.OnDestinationChangedListener onDestinationChangedListener = new NavController.OnDestinationChangedListener() {
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            if (destination.getId() == R.id.navigation_home || destination.getId() == R.id.navigation_items ||
                    destination.getId() == R.id.navigation_messages || destination.getId() == R.id.navigation_profile_details || destination.getId() == R.id.navigation_home_admin) {
                bottomNav.setVisibility(View.VISIBLE);
            } else {
                bottomNav.setVisibility(View.GONE);
            }
        }
    };

    private class ExtendedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("sendToken")) {
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("token", intent.getExtras().get("token"));
                viewModel.sendRequest("/message/token", "POST", null, params, true, false, true, AdminActivity.this);
            } else {//received notification
                String title = intent.getExtras().getString("title");
                String body = intent.getExtras().getString("body");
                viewModel.sendRequest("/message/users", "GET", null, null, false, false, true, AdminActivity.this::onComplete);
                sendNotification(title, body, MESSAGE_CHANNEL_ID);
            }
        }
    }

    private void retrieveLastTokenAndSend() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    System.out.println("Unable to get last token");
                }

                String token = task.getResult();
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("token", token);
                viewModel.sendRequest("/message/token", "POST", null, params, true, false, true, AdminActivity.this);
            }
        });
    }

    private void createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = getString(R.string.msg_notify_name);
        String description = getString(R.string.msg_notify_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(MESSAGE_CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void enableNotifications() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            boolean allowedBefore = prefs.getBoolean("allowed_notifications", true);
            if (allowedBefore) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefs.edit();
                prefsEditor.putBoolean("allowed_notifications", isGranted);
                prefsEditor.apply(); // or commit();
            });

    private void sendNotification(String title, String contentText, String channelID) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.bell)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());
    }

    @Override
    public void onBackPressed() {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_admin);
        if(navHostFragment!=null){
            String currentFragmentLabel= Navigation.findNavController(this, R.id.nav_host_fragment_activity_admin).getCurrentDestination().getLabel().toString();
            if(!currentFragmentLabel.equals("HomeFragment")){
                super.onBackPressed();
            }
        }
    }
}