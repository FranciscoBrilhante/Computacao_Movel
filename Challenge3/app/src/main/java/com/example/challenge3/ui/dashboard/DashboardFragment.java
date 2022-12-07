package com.example.challenge3.ui.dashboard;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challenge3.data.MainViewModel;
import com.example.challenge3.data.Sample;
import com.example.challenge3.databinding.FragmentDashboardBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainViewModel dashboardViewModel =
                new ViewModelProvider(this).get(MainViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LineChart chartHumidity = (LineChart) binding.humidityChart;
        LineChart chartTemperature = (LineChart) binding.temperatureChart;
        customizeChart(chartHumidity);
        customizeChart(chartTemperature);

        dashboardViewModel.getAllSamples().observe(requireActivity(), samples -> {
            List<Entry> entriesHumidity=new ArrayList<>();
            List<Entry> entriesTemperature=new ArrayList<>();
            for (Sample sample: samples){
                Integer sensorID=sample.getSensor();
                if(dashboardViewModel.getSensorsByName("Humidity").get(0).getUid()==sensorID){
                    entriesHumidity.add(new Entry(sample.getTimestamp().getTime(), (float) sample.getReadingValue()));
                }
                else{
                    entriesTemperature.add(new Entry(sample.getTimestamp().getTime(),(float) sample.getReadingValue()));
                }
            }

            LineDataSet dataSetHumidity= new LineDataSet(entriesHumidity,"Humidity Values");
            LineData lineDataHumidity= new LineData(dataSetHumidity);
            customizeLineData(lineDataHumidity);
            chartHumidity.setData(lineDataHumidity);
            chartHumidity.invalidate();

            LineDataSet dataSetTemperature= new LineDataSet(entriesTemperature,"Temperature Values");
            LineData lineDataTemperature= new LineData(dataSetTemperature);
            customizeLineData(lineDataTemperature);
            chartTemperature.setData(lineDataTemperature);
            chartTemperature.invalidate();
            //chart.notifyDataSetChanged();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void customizeChart(LineChart chart){
        chart.setDescription(null);

        chart.setTouchEnabled(true);

        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(3);
        ValueFormatter formatter = new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);
            @Override
            public String getFormattedValue(float value) {
                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        };
        xAxis.setValueFormatter(formatter);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        YAxis leftAxis=chart.getAxisLeft();
        leftAxis.enableGridDashedLine(1f,10f,0f);


    }

    private void customizeLineData(LineData data){
        data.setValueTextSize(9f);
    }
}