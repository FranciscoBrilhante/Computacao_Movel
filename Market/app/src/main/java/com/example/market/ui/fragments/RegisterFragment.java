package com.example.market.ui.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.market.R;
import com.example.market.data.LoginViewModel;
import com.example.market.databinding.FragmentRegisterBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.ui.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class RegisterFragment extends Fragment implements HTTTPCallback{

    private FragmentRegisterBinding binding;
    private LoginViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        Button registerButton = binding.registerButton;
        registerButton.setOnClickListener(this.registerListener);

        TextView loginLink=binding.loginLink;
        loginLink.setOnClickListener(this.loginLinkListener);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onComplete(JSONObject data) {
        Integer code;
        try {
            String endpoint = (String) data.get("endpoint");
            switch (endpoint) {
                case "/profile/register":
                    code = (Integer) data.get("status");
                    if (code == 200) {
                        String usernameInput = binding.usernameInput.getText().toString();
                        String passwordInput = binding.passwordInput.getText().toString();
                        Map<String, Object> params = new LinkedHashMap<>();
                        params.put("username", usernameInput);
                        params.put("password", passwordInput);

                        viewModel.sendPOSTRequest("/profile/login", params, true, false, this);
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), R.string.register_fail_message, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "/profile/login":
                    code = (Integer) data.get("status");
                    if (code == 200) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.register_success_message, Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("credentials",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("username", binding.usernameInput.getText().toString());
                        editor.putString("password", binding.passwordInput.getText().toString());
                        editor.apply();

                        Intent myIntent = new Intent(getActivity(), MainActivity.class);
                        startActivity(myIntent);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.login_fail_message, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener registerListener =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String usernameInput = binding.usernameInput.getText().toString();
            String emailInput = binding.emailInput.getText().toString();
            String passwordInput = binding.passwordInput.getText().toString();
            String passwordInput2 = binding.confirmPasswordInput.getText().toString();

            if (!passwordInput.equals(passwordInput2)) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.password_missmatch_message, Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("username", usernameInput);
            params.put("email", emailInput);
            params.put("password", passwordInput);

            viewModel.sendPOSTRequest("/profile/register", params, false, false, RegisterFragment.this);
        }
    };

    private View.OnClickListener loginLinkListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            NavController navController = navHostFragment.getNavController();
            NavDirections action = RegisterFragmentDirections.actionNavigationRegisterToNavigationLogin();
            navController.navigate(action);
        }
    };
}