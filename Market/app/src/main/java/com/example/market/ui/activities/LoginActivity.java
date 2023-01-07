package com.example.market.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;

import com.example.market.R;
import com.example.market.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Disable auto dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_login);
        NavController navController = navHostFragment.getNavController();
        NavInflater inflater=navController.getNavInflater();
        NavGraph graph=inflater.inflate(R.navigation.login_navigation);
        navController.setGraph(graph);

        //hide default toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_login);
        if(navHostFragment!=null){
            String currentFragmentLabel= Navigation.findNavController(this, R.id.nav_host_fragment_activity_login).getCurrentDestination().getLabel().toString();
            if(!currentFragmentLabel.equals("LoginFragment")){
                super.onBackPressed();
            }
        }
    }
}