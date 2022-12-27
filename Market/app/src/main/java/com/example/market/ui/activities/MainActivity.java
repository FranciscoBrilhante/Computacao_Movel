package com.example.market.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.marketDatabase.Category;
import com.example.market.marketDatabase.Product;
import com.example.market.services.ProductsService;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.market.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements HTTTPCallback {

    private ActivityMainBinding binding;
    public MarketViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //Disable auto dark mode

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

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
    }

    @Override
    public void onComplete(JSONObject data) {
        Integer code;
        String url1 = "/profile/login";
        String url2 = "/category/all";
        String url3 = "/product/recommended";
        try {
            String endpoint = (String) data.get("endpoint");
            if (endpoint.equals(url1)) {
                code = (Integer) data.get("status");
                if (code == 200) {
                    requestLocation();
                    viewModel.sendRequest("/category/all", "GET", null, null, false, false, true, this);
                    viewModel.sendRequest("/product/recommended", "GET", null, null, false, false, true, this);
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
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            boolean allowedBefore = prefs.getBoolean("allowed_location", true);
            //if user already gave permission or never was requested permission then app can request it
            if (allowedBefore) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefs.edit();
                prefsEditor.putBoolean("allowed_location", isGranted);
                prefsEditor.apply();
            });


}