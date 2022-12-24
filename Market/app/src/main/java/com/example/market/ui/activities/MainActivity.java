package com.example.market.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.services.ProductsService;
import com.example.market.ui.fragments.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.market.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements HTTTPCallback {

    private ActivityMainBinding binding;
    public MarketViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_login);
        NavigationUI.setupWithNavController(binding.navView, navController);

        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        if (!viewModel.areCredentialsStored()) {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
            return;
        }

        Map<String, Object> params = viewModel.getStoredCredentials();
        viewModel.sendPOSTRequest("/profile/login", params, true, false, this);
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
}