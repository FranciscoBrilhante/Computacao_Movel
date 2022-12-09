package com.example.challenge3.ui.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challenge3.R;
import com.example.challenge3.data.MainViewModel;
import com.example.challenge3.data.Sensor;
import com.example.challenge3.databinding.FragmentSettingsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class SettingsFragment extends Fragment implements SwitchCompat.OnCheckedChangeListener {

    private FragmentSettingsBinding binding;
    private static final String LOG_TAG = "SettingsFragment";
    private MainViewModel viewModel;
    SwitchCompat switchHumidity,switchTemperature;
    EditText thresholdHumidity,thresholdTemperature;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        switchHumidity = binding.switchHumidity;
        switchTemperature = binding.switchTemperature;
        thresholdHumidity=binding.thresholdHumidity;
        thresholdTemperature=binding.thresholdTemperature;

        try {
            setInitialSwitchState();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        switchHumidity.setOnCheckedChangeListener(this);
        switchTemperature.setOnCheckedChangeListener(this);

        thresholdHumidity.addTextChangedListener(humidityTextWatcher);
        thresholdTemperature.addTextChangedListener(temperatureTextWatcher);

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

    private void setInitialSwitchState() throws InterruptedException{
        CountDownLatch latch = new CountDownLatch(1);
        Executor executor=Executors.newSingleThreadExecutor();
        AtomicReference<List<Sensor>> allSensors=new AtomicReference<>(new ArrayList<>());
        executor.execute(() ->{
            allSensors.set(viewModel.getAllSensors());
            latch.countDown();
        });
        latch.await();
        for(Sensor sensor: allSensors.get()){
            switch (sensor.getName()){
                case "Humidity":
                    switchHumidity.setChecked(sensor.isSaveData());
                    thresholdHumidity.setText(String.format(Locale.ENGLISH,"%.1f",sensor.getThreshold()));
                    break;
                case "Temperature":
                    switchTemperature.setChecked(sensor.isSaveData());
                    thresholdTemperature.setText(String.format(Locale.ENGLISH,"%.1f",sensor.getThreshold()));
            }
        }
    }

    private final TextWatcher humidityTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            viewModel.updateThreshold(Double.parseDouble(s.toString()),"Humidity");
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher temperatureTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            viewModel.updateThreshold(Double.parseDouble(s.toString()),"Temperature");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}