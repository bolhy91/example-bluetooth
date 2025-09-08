package com.example.testbluetooh;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface IBluetooth {
    LiveData<List<BluetoothDevice>> getScannedDevices();

    LiveData<List<BluetoothDevice>> getPairedDevices();

    void startDiscovery();

    void stopDiscovery();

    void release();

    LiveData<Boolean> isConnected();

    LiveData<String> getErrors();

    LiveData<ConnectResult> startServer();
    LiveData<ConnectResult> connectDevice(BluetoothDevice device);

    void closeConnection();
}