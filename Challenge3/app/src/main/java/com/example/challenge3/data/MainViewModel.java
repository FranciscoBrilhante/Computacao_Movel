package com.example.challenge3.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<String> dashboardText;
    private final MutableLiveData<String> settingsText;


    public MainViewModel() {
        dashboardText = new MutableLiveData<>();
        settingsText = new MutableLiveData<>();

        dashboardText.setValue("This is dashboard fragment");
        settingsText.setValue("This is settings fragment");
    }

    public LiveData<String> getDashboardText() {
        return dashboardText;
    }
    public LiveData<String> getSettingsText(){
        return settingsText;}
}
