package com.example.challenge3.ui.dashboard;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import com.example.challenge3.data.Sensor;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DashboardFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private FragmentDashboardBinding binding;
    private static final String LOG_TAG = "DashboardFragment";
    private LineChart chartHumidity, chartTemperature;
    private MainViewModel viewModel;
    private Calendar referenceTimestamp;
    private DatePickerDialog datePickerTemp, datePickerHum;
    private Calendar humDateWindow, tempDateWindow;
    private ArrayList<Long> registeredTimestampsHum,registeredTimestampsTemp;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        referenceTimestamp = Calendar.getInstance();
        registeredTimestampsTemp=new ArrayList<>();
        registeredTimestampsHum=new ArrayList<>();

        Calendar c = Calendar.getInstance();
        setDateToMidnight(c);
        humDateWindow = c;
        tempDateWindow = (Calendar) c.clone();

        chartHumidity = binding.humidityChart;
        chartTemperature = binding.temperatureChart;

        customizeChart(chartHumidity, referenceTimestamp, "%");
        customizeChart(chartTemperature, referenceTimestamp, "ºC");

        updateCharts();

        SwitchCompat switchTemp = binding.switchLed;
        switchTemp.setOnCheckedChangeListener(this);

        ImageButton deleteHumidityButton = binding.deleteHumidityData;
        deleteHumidityButton.setOnClickListener(listenerDeleteHumidity);

        ImageButton deleteTemperatureData = binding.deleteTemperatureData;
        deleteTemperatureData.setOnClickListener(listenerDeleteTemperature);

        ImageButton datePickerButtonHum = binding.datePickerButtonHum;
        ImageButton datePickerButtonTemp = binding.datePickerButtonTemp;

        datePickerButtonHum.setOnClickListener(listenerDateButtonHumidity);
        datePickerButtonTemp.setOnClickListener(listenerDateButtonTemperature);

        datePickerTemp = new DatePickerDialog(getContext(), 0);
        datePickerHum = new DatePickerDialog(getContext(), 0);
        datePickerHum.setOnDateSetListener(listenerDateHum);
        datePickerTemp.setOnDateSetListener(listenerDateTem);

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void customizeChart(LineChart chart, Calendar referenceTimestamp, String units) {
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
        chart.setDragDecelerationEnabled(false);

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
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(referenceTimestamp.getTimeInMillis() + (long) value);
                DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                return df.format(c.getTime());
            }
        });
        xl.setLabelCount(3, true);


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.enableGridDashedLine(2f, 5f, 0f);
        leftAxis.setAxisLineColor(getResources().getColor(R.color.graph_axis));
        leftAxis.setGridColor(getResources().getColor(R.color.graph_grid));
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.ENGLISH, "%.0f%s", value, units);
            }
        });

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

    }

    private void addEntry(LineChart chart, Sample sample) {
        LineData data = chart.getData();
        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }
        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createSet();
            data.addDataSet(set);

        }

        data.addEntry(new Entry(sample.getTimestamp().getTimeInMillis() - referenceTimestamp.getTimeInMillis(), (float) sample.getReadingValue()), 0);
        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();
        // limit the number of visible entries
        chart.setVisibleXRangeMaximum(3_600_000);
        //chart.setVisibleYRange(0,30, YAxis.AxisDependency.LEFT);
        // move to the latest entry
        chart.moveViewToX(data.getEntryCount());
        //chart.moveViewTo(data.getEntryCount(),0f,YAxis.AxisDependency.LEFT);
    }


    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "");

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        set.setMode(LineDataSet.Mode.LINEAR);
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
                        if (checkSampleIsLegibleForChart(chartHumidity, sample, humDateWindow, "Humidity",registeredTimestampsHum)) {
                            registeredTimestampsHum.add(sample.getTimestamp().getTimeInMillis());
                            addEntry(chartHumidity, sample);
                        }
                        break;
                    case "Temperature":
                        if (checkSampleIsLegibleForChart(chartTemperature, sample, tempDateWindow, "Temperature",registeredTimestampsTemp)) {
                            registeredTimestampsTemp.add(sample.getTimestamp().getTimeInMillis());
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

    View.OnClickListener listenerDeleteHumidity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewModel.deleteSamplesBySensor("Humidity");
            chartHumidity.clear();
            registeredTimestampsHum.clear();
        }
    };

    View.OnClickListener listenerDeleteTemperature = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewModel.deleteSamplesBySensor("Temperature");
            chartTemperature.clear();
            registeredTimestampsTemp.clear();
        }
    };

    View.OnClickListener listenerDateButtonHumidity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            datePickerHum.show();
        }
    };

    View.OnClickListener listenerDateButtonTemperature = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            datePickerTemp.show();
        }
    };

    DatePickerDialog.OnDateSetListener listenerDateHum = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            humDateWindow = Calendar.getInstance();
            humDateWindow.set(Calendar.YEAR, year);
            humDateWindow.set(Calendar.MONTH, month);
            humDateWindow.set(Calendar.DATE, dayOfMonth);
            setDateToMidnight(humDateWindow);

            chartHumidity.clear();
            registeredTimestampsHum.clear();

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                List<Sample> samples = viewModel.getAllSamplesList();
                for (Sample sample : samples) {
                    if (checkSampleIsLegibleForChart(chartHumidity, sample, humDateWindow, "Humidity",registeredTimestampsHum)) {
                        registeredTimestampsHum.add(sample.getTimestamp().getTimeInMillis());
                        addEntry(chartHumidity, sample);
                    }
                }
            });
        }
    };

    DatePickerDialog.OnDateSetListener listenerDateTem = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            tempDateWindow = Calendar.getInstance();
            tempDateWindow.set(Calendar.YEAR, year);
            tempDateWindow.set(Calendar.MONTH, month);
            tempDateWindow.set(Calendar.DATE, dayOfMonth);
            setDateToMidnight(tempDateWindow);

            chartTemperature.clear();
            registeredTimestampsTemp.clear();

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                List<Sample> samples = viewModel.getAllSamplesList();
                for (Sample sample : samples) {
                    if (checkSampleIsLegibleForChart(chartTemperature, sample, tempDateWindow, "Temperature",registeredTimestampsTemp)) {
                        registeredTimestampsTemp.add(sample.getTimestamp().getTimeInMillis());
                        addEntry(chartTemperature, sample);
                    }
                }
            });
        }
    };

    private boolean checkSampleIsLegibleForChart(LineChart chart, Sample sample, Calendar c, String sensor,ArrayList<Long> registeredTimestamps) {
        Calendar sampleDate = sample.getTimestamp();
        Calendar c1 = (Calendar) c.clone();
        c1.add(Calendar.DATE, 1);
        boolean value=sampleDate.compareTo(c1) < 0 && sampleDate.compareTo(c) > 0 && sample.getSensor().equals(sensor) && !registeredTimestamps.contains(sampleDate.getTimeInMillis());
        return value;
    }

    public void setDateToMidnight(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }
}