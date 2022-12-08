package com.example.challenge3.ui.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challenge3.R;
import com.example.challenge3.data.MainViewModel;
import com.example.challenge3.data.Sensor;
import com.example.challenge3.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment implements SwitchCompat.OnCheckedChangeListener {

    private FragmentSettingsBinding binding;
    private static final String LOG_TAG = "SettingsFragment";
    private MainViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SwitchCompat switchHumidity = binding.switchHumidity;
        SwitchCompat switchTemperature = binding.switchTemperature;
        viewModel.getAllSensors().observe(requireActivity(),sensors -> {
            for(Sensor sensor: sensors){
                switch (sensor.getName()){
                    case "Humidity":
                        switchHumidity.setChecked(sensor.isSaveData());
                        break;
                    case "Temperature":
                        switchTemperature.setChecked(sensor.isSaveData());
                }
            }
        });

        switchHumidity.setOnCheckedChangeListener(this);
        switchTemperature.setOnCheckedChangeListener(this);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_humidity:
                viewModel.updateSaveData(isChecked,"Humidity");
                break;
            case R.id.switch_temperature:
                viewModel.updateSaveData(isChecked,"Temperature");
                break;
        }
    }
}