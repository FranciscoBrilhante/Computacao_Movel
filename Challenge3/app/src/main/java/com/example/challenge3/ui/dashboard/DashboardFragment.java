package com.example.challenge3.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challenge3.data.MainViewModel;
import com.example.challenge3.data.Sample;
import com.example.challenge3.databinding.FragmentDashboardBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DashboardFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

    private FragmentDashboardBinding binding;
    private static final String LOG_TAG="DashboardFragment";
    LineChart chartHumidity, chartTemperature;
    private List<Date> sampleDatesHumidity, sampleDatesTemperature;
    private MainViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        chartHumidity = binding.humidityChart;
        chartTemperature = binding.temperatureChart;

        customizeChart(chartHumidity);
        customizeChart(chartTemperature);
        sampleDatesHumidity = new ArrayList<>();
        sampleDatesTemperature = new ArrayList<>();

        updateCharts();

        SwitchCompat switchTemp=binding.switchLed;
        switchTemp.setOnCheckedChangeListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void customizeChart(LineChart chart) {
        // enable description text
        chart.getDescription().setEnabled(true);
        Description desc = new Description();
        desc.setText("Humidity Sensor Data");
        chart.setDescription(desc);
        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);


        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        chart.setData(data);

        XAxis xl = chart.getXAxis();
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = chart.getAxisLeft();
        //leftAxis.setAxisMaximum(100f);
        //chart.setVisibleYRangeMaximum(1, YAxis.AxisDependency.LEFT);

        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.enableGridDashedLine(2f, 5f, 0f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

    }

    private void addEntry(LineChart chart, Sample sample) {
        LineData data = chart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), (float) sample.getReadingValue()), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(120);
            //chart.setVisibleYRange(0,30, YAxis.AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(1f);
        set.setCircleRadius(3f);
        set.setFillAlpha(65);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    private void updateCharts(){
        viewModel.getAllSamples().observe(requireActivity(), samples -> {
            for (Sample sample : samples) {
                switch (sample.getSensor()) {
                    case "Humidity":
                        if (!sampleDatesHumidity.contains(sample.getTimestamp())) {
                            sampleDatesHumidity.add(sample.getTimestamp());
                            addEntry(chartHumidity, sample);
                        }
                        break;
                    case "Temperature":
                        if (!sampleDatesTemperature.contains(sample.getTimestamp())) {
                            sampleDatesTemperature.add(sample.getTimestamp());
                            addEntry(chartTemperature, sample);
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        viewModel.sendMessage("dynamic_led_topic", isChecked ? "1" : "0");
    }
}