package com.example.testbluetooh;

import android.annotation.SuppressLint;

public class BluetoothMapper {
    @SuppressLint("MissingPermission")
    public static BluetoothDevice toBluetoothDevice(android.bluetooth.BluetoothDevice device) {
        return new BluetoothDevice(device.getName(), device.getAddress());
    }
}
