package com.example.testbluetooh;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mazenrashed.printooth.ui.ScanningActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BluetoothController implements IBluetooth {

    private final Context context;
    private final BluetoothManager bluetoothManager;
    private final BluetoothAdapter bluetoothAdapter;
    private final BluetoothDeviceReceiver foundDeviceReceiver;

    private final MutableLiveData<List<BluetoothDevice>> scannedDevices = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<BluetoothDevice>> pairedDevices = new MutableLiveData<>(new ArrayList<>());

    public BluetoothController(Context context) {
        this.context = context.getApplicationContext();
        this.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager != null ? bluetoothManager.getAdapter() : null;

        this.foundDeviceReceiver = new BluetoothDeviceReceiver(device -> {
            BluetoothDevice newDevice = BluetoothMapper.toBluetoothDevice(device);

            List<BluetoothDevice> current = scannedDevices.getValue();
            if (current == null) current = new ArrayList<>();

            Set<BluetoothDevice> set = new HashSet<>(current);
            if (set.add(newDevice)) {
                scannedDevices.postValue(new ArrayList<>(set));
            }
        });
        updatePairedDevices();
    }

    @Override
    public LiveData<List<BluetoothDevice>> getScannedDevices() {
        return scannedDevices;
    }

    @Override
    public LiveData<List<BluetoothDevice>> getPairedDevices() {
        return pairedDevices;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startDiscovery() {
        if (!hasRequiredPermissions()) return;
        context.registerReceiver(foundDeviceReceiver, new IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND));
        updatePairedDevices();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.startDiscovery();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void stopDiscovery() {
        if (!hasRequiredPermissions()) {
            return;
        }

        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public void release() {
        try {
            context.unregisterReceiver(foundDeviceReceiver);
        } catch (IllegalArgumentException exception) {
            Log.e("Bluetooth Error: ", exception.toString());
        }
    }

    @SuppressLint("MissingPermission")
    private void updatePairedDevices() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return;
        }
        if (bluetoothAdapter != null) {
            Set<android.bluetooth.BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
            List<BluetoothDevice> deviceList = new ArrayList<>();
            for (android.bluetooth.BluetoothDevice device : bondedDevices) {
                Log.i("DEEEEVICE 2", device.getBondState() + "");

                deviceList.add(BluetoothMapper.toBluetoothDevice(device));
            }

            pairedDevices.postValue(deviceList);
        }
    }

    private boolean hasPermission(String permission) {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasRequiredPermissions() {
        // Para Android 10, necesitamos permisos de ubicación
        boolean hasLocationPermission = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
                                     hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        // Para Android 12 y superior, necesitamos permisos Bluetooth específicos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return hasLocationPermission &&
                   hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                   hasPermission(Manifest.permission.BLUETOOTH_CONNECT);
        }

        // Para Android 10 y 11, solo necesitamos ubicación
        return hasLocationPermission;
    }
}
