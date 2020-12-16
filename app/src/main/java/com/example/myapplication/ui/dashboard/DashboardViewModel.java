package com.example.myapplication.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> input;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        input = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<String> getInput() {return input;}
}