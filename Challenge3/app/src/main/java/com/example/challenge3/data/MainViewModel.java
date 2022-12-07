package com.example.challenge3.data;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final MainRoomDatabase db;
    private final SampleDao sampleDao;
    private final SensorDao sensorDao;
    private final LiveData<List<Sample>> allSamples;


    public MainViewModel(Application application) {
        super(application);
        db=MainRoomDatabase.getDatabase(application);
        sampleDao=db.sampleDao();
        sensorDao=db.sensorDao();
        allSamples=sampleDao.getAll();

    }

    public LiveData<List<Sample>> getAllSamples() {
        return allSamples;
    }

    public List<Sensor> getAllSensors(){
        return sensorDao.getAll();
    }

    public List<Sensor> getSensorsByName(String name){
        return sensorDao.getByName(name);
    }

}
