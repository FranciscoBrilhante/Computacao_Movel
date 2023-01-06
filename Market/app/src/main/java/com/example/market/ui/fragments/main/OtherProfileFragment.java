package com.example.market.ui.fragments.main;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentOtherProfileBinding;
import com.example.market.databinding.FragmentProfileDetailsBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.marketDatabase.Image;
import com.example.market.ui.activities.LoginActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OtherProfileFragment extends Fragment implements View.OnClickListener, HTTTPCallback {
    private FragmentOtherProfileBinding binding;
    private MarketViewModel viewModel;
    private int profileID;
    private GoogleMap map;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileID = OtherProfileFragmentArgs.fromBundle(getArguments()).getProfileId();

        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding = FragmentOtherProfileBinding.inflate(inflater, container, false);
        binding.backButton.setOnClickListener(this);
        binding.ratingBar.setNumStars(5);

        Map<String, Object> params=new LinkedHashMap<>();
        params.put("profile_id",Integer.toString(profileID));
        viewModel.sendRequest("/profile/info", "GET", params, null, false, false, true, this);

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        if(view==binding.backButton){
            NavHostFragment navHostFragment =
                    (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            NavController navController = navHostFragment.getNavController();
            navController.navigateUp();
        }
    }

    @Override
    public void onComplete(JSONObject data) {
        String url1 = "/profile/info";
        String url2 = "/profile/setlocation";
        String url3 = "/profile/setphoto";
        try {
            int code = data.getInt("status");
            String endpoint = data.getString("endpoint");
            if (endpoint.equals(url1)) {
                if (code == 200) {
                    populateProfileInfo(data);
                }
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void populateProfileInfo(JSONObject data) throws JSONException, NullPointerException {
        String username = data.getString("username");
        String photoURL = data.getString("image");
        String city= data.getString("location");
        Double cityX = data.getDouble("cityX");
        Double cityY = data.getDouble("cityY");
        Double rating =data.getDouble("rating");

        if (city.equals("null")) {
            city = getActivity().getResources().getString(R.string.location_undefined);
        }

        binding.locationLabel.setText(city);
        binding.nameView.setText(username);
        binding.ratingLabel.setText(Double.toString(rating));

        if (photoURL.equals("null")) {
            binding.profilePhoto.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.placeholder_avatar));
        } else {
            String fullURL = "https://" + BuildConfig.API_ADDRESS + photoURL;
            Glide.with(getActivity().getApplicationContext())
                    .load(fullURL)
                    .override(1000, 1000) //give resize dimension, you could calculate those
                    .centerCrop() // scale to fill the ImageView
                    .error(R.drawable.placeholder_avatar)
                    .placeholder(R.drawable.placeholder_avatar)
                    .into(binding.profilePhoto);
        }
    }
}