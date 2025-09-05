package com.example.testbluetooh;

import java.util.ArrayList;
import java.util.List;

public class BluetoothState {
    private final List<BluetoothDevice> scannedDevices;
    private final List<BluetoothDevice> pairedDevices;

    public BluetoothState() {
        this.scannedDevices = new ArrayList<>();
        this.pairedDevices = new ArrayList<>();
    }

    public BluetoothState(List<BluetoothDevice> scannedDevices, List<BluetoothDevice> pairedDevices) {
        this.scannedDevices = scannedDevices != null ? scannedDevices : new ArrayList<>();
        this.pairedDevices = pairedDevices != null ? pairedDevices : new ArrayList<>();
    }

    public List<BluetoothDevice> getPairedDevices() {
        return pairedDevices;
    }

    public List<BluetoothDevice> getScannedDevices() {
        return scannedDevices;
    }
}
