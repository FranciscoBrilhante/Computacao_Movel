package com.example.market.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentProfileBinding;
import com.example.market.databinding.FragmentProfileDetailsBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.ui.activities.LoginActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class ProfileDetailsFragment extends Fragment implements View.OnClickListener, HTTTPCallback, LocationListener {


    private FragmentProfileDetailsBinding binding;
    private MarketViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentProfileDetailsBinding.inflate(inflater, container, false);

        binding.backButton.setOnClickListener(this);
        binding.updateLocationButton.setOnClickListener(this);

        viewModel.sendRequest("/profile/personalinfo", "GET", null, null, false, false, true, this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        if (view == binding.backButton) {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            NavController navController = navHostFragment.getNavController();
            navController.navigateUp();
        }
        if (view == binding.updateLocationButton) {
            requestLocation();
        }
    }

    @Override
    public void onComplete(JSONObject data) {
        String url1 = "/profile/personalinfo";
        String url2 = "/profile/setlocation";
        try {
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                if (code == 200) {
                    populateProfileInfo(data);
                } else {
                    NavHostFragment navHostFragment =
                            (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
                    NavController navController = navHostFragment.getNavController();
                    navController.navigateUp();
                }
            }
            if (endpoint.equals(url2)) {
                if (code == 200) {
                    viewModel.sendRequest("/profile/personalinfo", "GET", null, null, false, false, true, this);
                } else {
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void populateProfileInfo(JSONObject data) throws JSONException {
        String username = data.getString("username");
        String email = data.getString("email");
        String photoURL = data.getString("photo");
        String city = data.getString("location");

        if(city.equals("null")){
            city=getString(R.string.location_undefined);
        }

        binding.usernameInput.setText(username);
        binding.emailInput.setText(email);
        binding.locationInput.setText(city);

        if (photoURL.equals("null")) {
            binding.profilePhoto.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_avatar));
        } else {
            String fullURL = "https://" + BuildConfig.API_ADDRESS + photoURL;
            Glide.with(getContext())
                    .load(fullURL)
                    .override(1000, 1000) //give resize dimension, you could calculate those
                    .centerCrop() // scale to fill the ImageView
                    .into(binding.profilePhoto);
            //Picasso.get().load(fullURL).into(photoView);
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocation() {
        LocationManager locationManager=(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000L,500.0f, this);
        CancellationSignal cancellationSignal=new CancellationSignal();
        locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, cancellationSignal, getActivity().getApplication().getMainExecutor(), new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                if(location!=null){
                    System.out.println(location.getLatitude());
                    System.out.println(location.getLongitude());
                    Map<String,Object> params=new LinkedHashMap<>();
                    params.put("cityX",location.getLongitude());
                    params.put("cityY",location.getLatitude());
                    viewModel.sendRequest("/profile/setlocation","POST",null,params,true,false,true,ProfileDetailsFragment.this::onComplete);
                }
                else{
                    System.out.println("Location unavailable");
                }
            }
        });
    }

    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            String[] permissions= {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissionLauncher.launch(permissions);
        } else {
            updateLocation();
        }
    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                boolean hasPermission=true;
                for(boolean value: isGranted.values()){
                    if (!value) {
                        hasPermission=false;

                    }
                }
                if(hasPermission){
                    updateLocation();
                }
            });

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}