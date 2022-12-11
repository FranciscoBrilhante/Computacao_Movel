package com.example.challenge3.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challenge3.R;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DashboardFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private FragmentDashboardBinding binding;
    private static final String LOG_TAG = "DashboardFragment";
    LineChart chartHumidity, chartTemperature;
    private List<Date> sampleDatesHumidity, sampleDatesTemperature;
    private MainViewModel viewModel;
    private Date referenceTimestamp;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        referenceTimestamp=new Date();

        chartHumidity = binding.humidityChart;
        chartTemperature = binding.temperatureChart;

        customizeChart(chartHumidity,referenceTimestamp,"%");
        customizeChart(chartTemperature,referenceTimestamp,"ºC");
        sampleDatesHumidity = new ArrayList<>();
        sampleDatesTemperature = new ArrayList<>();

        updateCharts();

        SwitchCompat switchTemp = binding.switchLed;
        switchTemp.setOnCheckedChangeListener(this);

        ImageButton deleteHumidityButton = binding.deleteHumidityData;
        deleteHumidityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartHumidity.clear();
            }
        });

        ImageButton deleteTemperatureData = binding.deleteTemperatureData;
        deleteTemperatureData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartTemperature.clear();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void customizeChart(LineChart chart,Date referenceTimestamp, String units) {
        // enable description text
        chart.getDescription().setEnabled(false);
        // enable touch gestures
        // enable scaling and dragging
        // if disabled, scaling can be done on x- and y-axis separately
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);





        chart.getLegend().setEnabled(false);

        XAxis xl = chart.getXAxis();
        xl.setDrawGridLines(true);
        xl.enableGridDashedLine(2f, 5f, 0f);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setAxisLineColor(getResources().getColor(R.color.graph_axis));
        xl.setTextColor(getResources().getColor(R.color.graph_axis_text));
        xl.setGridColor(getResources().getColor(R.color.graph_grid));
        xl.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                Date date=new Date((long) (referenceTimestamp.getTime()+(double)value));
                DateFormat df = new SimpleDateFormat("HH:mm:ss",Locale.ENGLISH);
                return df.format(date);
            }
        });
        xl.setLabelCount(3,true);


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.enableGridDashedLine(2f, 5f, 0f);
        leftAxis.setAxisLineColor(getResources().getColor(R.color.graph_axis));
        leftAxis.setGridColor(getResources().getColor(R.color.graph_grid));
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.ENGLISH,"%.0f%s", value, units);
            }
        });

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

    }

    private void addEntry(LineChart chart, Sample sample) {
        LineData data = chart.getData();
        if(data==null){
            data=new LineData();
            chart.setData(data);
        }
        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createSet();
            data.addDataSet(set);

        }

        data.addEntry(new Entry(sample.getTimestamp().getTime()-referenceTimestamp.getTime(), (float) sample.getReadingValue()), 0);
        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();
        // limit the number of visible entries
        chart.setVisibleXRangeMaximum(20000);
        //chart.setVisibleYRange(0,30, YAxis.AxisDependency.LEFT);
        // move to the latest entry
        chart.moveViewToX(data.getEntryCount());
    }


    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "");

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setLineWidth(1.5f);
        set.setCircleRadius(2f);

        set.setColor(getResources().getColor(R.color.graph_line));
        set.setCircleColor(getResources().getColor(R.color.graph_point));

        set.setDrawValues(false);
        set.setDrawFilled(true);
        set.setFillDrawable(ContextCompat.getDrawable(getContext(), R.drawable.blue_fade));

        return set;
    }

    private void updateCharts() {
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