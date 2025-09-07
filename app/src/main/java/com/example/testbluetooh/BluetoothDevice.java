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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BluetoothDevice that = (BluetoothDevice) o;
        return address != null && address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "BluetoothDevice{name='" + name + "', address='" + address + "'}";
    }
}
