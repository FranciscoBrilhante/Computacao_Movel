package com.example.market.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.services.ProductsService;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.market.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements HTTTPCallback {

    private ActivityMainBinding binding;
    public MarketViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        if (!viewModel.areCredentialsStored()) {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
            return;
        }

        Map<String, Object> params = viewModel.getStoredCredentials();
        viewModel.sendRequest("/profile/login","POST",null, params,true, true, false, this);

    }

    @Override
    public void onComplete(JSONObject data) {
        Integer code;
        try {
            String endpoint = (String) data.get("endpoint");
            switch (endpoint) {
                case "/profile/login":
                    code = (Integer) data.get("status");
                    if (code == 200) {
                        String sessionID = viewModel.getSessionID();
                        Intent intent = new Intent(this, ProductsService.class);
                        intent.putExtra("sessionID",sessionID);
                        startService(intent);

                        requestLocation();
                    }
                    else{
                        viewModel.removeStoredCredentials();
                        Intent myIntent = new Intent(this, LoginActivity.class);
                        startActivity(myIntent);
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestLocation(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_DENIED){
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            boolean allowedBefore=prefs.getBoolean("allowed_location",true);
            if(allowedBefore){
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefs.edit();
                prefsEditor.putBoolean("allowed_location", isGranted);
                prefsEditor.apply(); // or commit();
            });
}