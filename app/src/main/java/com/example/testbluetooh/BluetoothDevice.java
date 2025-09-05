package com.example.testbluetooh;

import androidx.annotation.NonNull;

public class BluetoothDevice {
    private final String name;
    private final String address;


    public BluetoothDevice(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return "BluetoothDevice{name='" + name + "', address='" + address + "'}";
    }
}
