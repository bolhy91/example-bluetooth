package com.example.testbluetooh;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class BluetoothViewModelFactory implements ViewModelProvider.Factory {
    private final Context appContext;

    public BluetoothViewModelFactory(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BluetoothViewModel.class)) {
            return (T) new BluetoothViewModel(appContext);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
