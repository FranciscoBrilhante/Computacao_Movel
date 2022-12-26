package com.example.market.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.example.market.databinding.FragmentProfileBinding;
import com.example.market.databinding.FragmentProfileDetailsBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.ui.activities.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileDetailsFragment extends Fragment implements View.OnClickListener, HTTTPCallback {


    private FragmentProfileDetailsBinding binding;
    private MarketViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding=FragmentProfileDetailsBinding.inflate(inflater,container,false);

        binding.backButton.setOnClickListener(this);

        viewModel.sendRequest("/profile/personalinfo","GET",null, null, false,false,true,this);
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
        try {
            String url1="/profile/personalinfo";
            int code = data.getInt("status");
            String endpoint=data.getString("endpoint");
            if(endpoint.equals(url1)){
                if(code==200){
                    populateProfileInfo(data);
                }
                else {
                    NavHostFragment navHostFragment =
                            (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
                    NavController navController = navHostFragment.getNavController();
                    navController.navigateUp();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void populateProfileInfo(JSONObject data) throws JSONException{
        String username=data.getString("username");
        String email=data.getString("email");
        String photoURL= data.getString("photo");
        String city=Double.toString(data.getDouble("cityX"));

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
}