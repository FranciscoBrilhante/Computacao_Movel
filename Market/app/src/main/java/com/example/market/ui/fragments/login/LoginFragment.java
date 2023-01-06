package com.example.market.ui.fragments.login;

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
import com.example.market.databinding.FragmentLoginBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.ui.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoginFragment extends Fragment implements HTTTPCallback, View.OnClickListener {

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.setRetainInstance(true);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        binding.loginButton.setOnClickListener(this);
        binding.registerLink.setOnClickListener(this);
        binding.closeLoadingPopup.setOnClickListener(this);
        binding.loadingPopup.setVisibility(View.GONE);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onComplete(JSONObject data) {
        binding.loadingPopup.setVisibility((View.GONE));
        try {
            int code = (Integer) data.get("status");
            if (code == 200) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.login_success_message, Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPref = getActivity().getSharedPreferences("credentials", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("username", binding.usernameInput.getText().toString());
                editor.putString("password", binding.passwordInput.getText().toString());
                editor.putInt("profile_id", data.getInt("profile_id"));
                editor.apply();

                Intent myIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(myIntent);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), R.string.login_fail_message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == binding.registerLink) {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            NavController navController = navHostFragment.getNavController();
            NavDirections action = LoginFragmentDirections.actionNavigationLoginToNavigationRegister();
            navController.navigate(action);
        } else if (view == binding.loginButton) {
            TextView usernameInput = binding.usernameInput;
            TextView passwordInput = binding.passwordInput;

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("username", usernameInput.getText().toString());
            params.put("password", passwordInput.getText().toString());

            viewModel.sendPOSTRequest("/profile/login", params, true, false, LoginFragment.this);
            binding.loadingPopup.setVisibility(View.VISIBLE);

        } else if (view == binding.closeLoadingPopup) {
            binding.loadingPopup.setVisibility((View.GONE));
        }
    }
}