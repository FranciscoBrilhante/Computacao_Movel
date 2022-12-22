package com.example.market.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.market.BuildConfig;
import com.example.market.R;
import com.example.market.data.LoginViewModel;
import com.example.market.data.MarketViewModel;
import com.example.market.databinding.FragmentDashboardBinding;
import com.example.market.databinding.FragmentLoginBinding;
import com.example.market.interfaces.HTTTPCallback;
import com.example.market.ui.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class LoginFragment extends Fragment implements View.OnClickListener, HTTTPCallback {

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel =  new ViewModelProvider(this).get(LoginViewModel.class);
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        Button loginButton = binding.loginButton;
        loginButton.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        TextView usernameInput=binding.usernameInput;
        TextView passwordInput=binding.passwordInput;

        Map<String,Object> params = new LinkedHashMap<>();
        params.put("username", usernameInput.getText().toString());
        params.put("password", passwordInput.getText().toString());

        viewModel.sendPOSTRequest("/profile/login",params,true,true, this);
    }

    @Override
    public void onComplete(JSONObject data) {
        Integer code=400;
        try {
            code = (Integer) data.get("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(code==200){
            Toast.makeText(getActivity().getApplicationContext(), "Login com sucesso",Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPref = getActivity().getPreferences(getActivity().getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", binding.usernameInput.getText().toString());
            editor.putString("password", binding.passwordInput.getText().toString());
            editor.apply();

            Intent myIntent = new Intent(getActivity(), MainActivity.class);
            startActivity(myIntent);

        }
        else{
            Toast.makeText(getActivity().getApplicationContext(),"Credenciais Inválidas",Toast.LENGTH_SHORT).show();
        }

    }
}