package com.example.testbluetooh;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BluetoothDeviceReceiver extends BroadcastReceiver {

    public interface OnDeviceFoundCallback {
        void onDeviceFound(BluetoothDevice device);
    }

    private final OnDeviceFoundCallback callback;

    public BluetoothDeviceReceiver(OnDeviceFoundCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
            BluetoothDevice device;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice.class);
            } else {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            }
            if (device != null) {
                callback.onDeviceFound(device);
            }
        }
    }
}
