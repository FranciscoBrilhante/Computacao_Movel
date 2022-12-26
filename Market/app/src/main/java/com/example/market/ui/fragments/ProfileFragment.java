package com.example.market.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.example.market.R;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentProfileBinding;
import com.example.market.databinding.FragmentViewProductBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.ui.activities.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment implements View.OnClickListener, HTTTPCallback {


    private FragmentProfileBinding binding;
    private MarketViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        binding=FragmentProfileBinding.inflate(inflater,container,false);

        LinearLayout profileView =binding.profile;
        LinearLayout messagesView =binding.messages;
        LinearLayout logoutView =binding.logout;
        LinearLayout deleteView =binding.delete;

        profileView.setOnClickListener(this);
        messagesView.setOnClickListener(this);
        logoutView.setOnClickListener(this);
        deleteView.setOnClickListener(this);

        return binding.getRoot();
    }


    @Override
    public void onClick(View view) {
        if(view==binding.profile){
            NavHostFragment navHostFragment =
                    (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            NavController navController = navHostFragment.getNavController();
            NavDirections action = ProfileFragmentDirections.actionNavigationProfileToNavigationProfileDetails();
            navController.navigate(action);
        }
        else if(view==binding.messages){

        }
        else if(view==binding.logout){
            viewModel.clearCookies();
            viewModel.removeStoredCredentials();
            Intent myIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(myIntent);
        }
        else if(view==binding.delete){
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),0);
            alert.setTitle(R.string.confirm_deletion_title);
            alert.setMessage(R.string.confirm_deletion_message);
            alert.setPositiveButton(R.string.delete_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    viewModel.sendRequest("/profile/delete","GET",null,null,false, false,true,ProfileFragment.this::onComplete);
                }
            });

            alert.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alert.show();
        }
    }

    @Override
    public void onComplete(JSONObject data) {
        try {
            String url1="/profile/delete";
            int code = data.getInt("status");
            String endpoint=data.getString("endpoint");
            if(endpoint.equals(url1)){
                if(code==200){
                    viewModel.clearCookies();
                    viewModel.removeStoredCredentials();
                    Toast.makeText(getActivity().getApplicationContext(),R.string.successfull_deletion_message,Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(myIntent);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}