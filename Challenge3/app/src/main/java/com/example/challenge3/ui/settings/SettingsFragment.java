package com.example.challenge3.ui.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challenge3.R;
import com.example.challenge3.data.MainViewModel;
import com.example.challenge3.data.Sensor;
import com.example.challenge3.databinding.FragmentSettingsBinding;
import com.shawnlin.numberpicker.NumberPicker;

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
    NumberPicker thresholdHumidity,thresholdTemperature;

    ArrayList<Double> actualTempValues;
    ArrayList<Double> actualHumValues;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        switchHumidity = binding.switchHumidity;
        switchTemperature = binding.switchTemperature;
        thresholdHumidity=binding.numberPickerHum;
        thresholdTemperature=binding.numberPickerTemp;

        switchHumidity.setOnCheckedChangeListener(this);
        switchTemperature.setOnCheckedChangeListener(this);

        ArrayList<String> tempValues=new ArrayList<String>();
        actualTempValues=new ArrayList<>();
        ArrayList<String> humValues=new ArrayList<String>();
        actualHumValues=new ArrayList<>();

        for(int i=-100;i<100;i++){
            tempValues.add(String.format(Locale.ENGLISH,"%.0fºC",(float) i));
            actualTempValues.add((double) i);
            humValues.add(String.format(Locale.ENGLISH,"%.0f%%",(float) i));
            actualHumValues.add((double) i);
        }
        thresholdHumidity.setMinValue(1);
        thresholdHumidity.setMaxValue(tempValues.size());
        thresholdHumidity.setDisplayedValues(humValues.toArray(new String[0]));
        thresholdHumidity.setValue(180);

        thresholdTemperature.setMinValue(1);
        thresholdTemperature.setMaxValue(tempValues.size());
        thresholdTemperature.setDisplayedValues(tempValues.toArray(new String[0]));
        thresholdTemperature.setValue(150);

        thresholdHumidity.setOnValueChangedListener(humList);
        thresholdTemperature.setOnValueChangedListener(tempList);

        try {
            setInitialSwitchState();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
        int value=0;
        for(Sensor sensor: allSensors.get()){
            switch (sensor.getName()){
                case "Humidity":
                    switchHumidity.setChecked(sensor.isSaveData());
                    value=actualHumValues.indexOf((double)sensor.getThreshold());
                    thresholdHumidity.setValue(value);
                    //thresholdHumidity.setText(String.format(Locale.ENGLISH,"%.1f",));
                    break;
                case "Temperature":
                    switchTemperature.setChecked(sensor.isSaveData());
                    value=actualTempValues.indexOf((double)sensor.getThreshold());
                    thresholdTemperature.setValue(value);
            }
        }
    }

    private NumberPicker.OnValueChangeListener humList = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Double value=actualHumValues.get(newVal);
            viewModel.updateThreshold(value,"Humidity");
        }
    };

    private NumberPicker.OnValueChangeListener tempList = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Double value=actualTempValues.get(newVal);
            viewModel.updateThreshold(value,"Temperature");
        }
    };



}