package com.example.challenge2.models;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.challenge2.interfaces.AlertInterface;
import com.example.challenge2.models.MQTTHelper;
import com.example.challenge2.models.NoteViewModel;

public class NoteViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private AlertInterface alertInterface;


    public NoteViewModelFactory(Application application, AlertInterface alertInterface) {
        this.application = application;
        this.alertInterface=alertInterface;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new NoteViewModel(application, alertInterface);
    }
}
