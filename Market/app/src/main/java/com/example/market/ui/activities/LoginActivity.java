package com.example.market.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.market.R;
import com.example.market.databinding.ActivityLoginBinding;
import com.example.market.ui.fragments.LoginFragment;
import com.example.market.ui.fragments.LoginFragmentDirections;
import com.example.market.ui.fragments.RegisterFragment;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_login);
        NavController navController = navHostFragment.getNavController();
        NavInflater inflater=navController.getNavInflater();
        NavGraph graph=inflater.inflate(R.navigation.login_navigation);
        navController.setGraph(graph);


        //para ir para o fragmento de registo
        //NavDirections action = LoginFragmentDirections.actionNavigationLoginToNavigationRegister();
        //navController.navigate(action);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}