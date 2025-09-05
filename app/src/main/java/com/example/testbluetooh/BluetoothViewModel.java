package com.example.testbluetooh;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BluetoothViewModel extends ViewModel {
    private final BluetoothController bluetoothController;
    private final MutableLiveData<BluetoothState> _state = new MutableLiveData<>(new BluetoothState());
    private final MediatorLiveData<BluetoothState> state = new MediatorLiveData<>();

    public BluetoothViewModel(Context context) {
        bluetoothController = new BluetoothController(context);
        state.setValue(_state.getValue());
        state.addSource(bluetoothController.getScannedDevices(), scanned -> {
            BluetoothState current = state.getValue();
            if (current == null) current = new BluetoothState();
            state.setValue(new BluetoothState(
                    scanned,
                    current.getPairedDevices()
            ));
        });

        state.addSource(bluetoothController.getPairedDevices(), paired -> {
            BluetoothState current = state.getValue();
            if (current == null) current = new BluetoothState();

            state.setValue(new BluetoothState(
                    current.getScannedDevices(),
                    paired
            ));
        });
    }

    public LiveData<BluetoothState> getState() {
        return state;
    }

    public void startScan() {
        bluetoothController.startDiscovery();
    }

    public void stopScan() {
        bluetoothController.stopDiscovery();
    }
}
