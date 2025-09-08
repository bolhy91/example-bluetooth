package com.example.testbluetooh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.DeviceViewHolder> {

    private final List<BluetoothDevice> devices = new ArrayList<>();
    private final OnDeviceClickListener onDeviceClickListener;
    public interface OnDeviceClickListener {
        void onDeviceClick(BluetoothDevice device);
    }

    public BluetoothDeviceAdapter(OnDeviceClickListener listener) {
        this.onDeviceClickListener = listener;
    }

    public void updateItems(List<BluetoothDevice> newDevices) {
        devices.clear();
        if (newDevices != null){
            devices.addAll(newDevices);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bluetooth_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        BluetoothDevice device = devices.get(position);
        holder.bind(device, onDeviceClickListener);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtDeviceName;
        private final TextView txtDeviceAddress;
        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDeviceName = itemView.findViewById(R.id.txtDeviceName);
            txtDeviceAddress = itemView.findViewById(R.id.txtDeviceAddress);
        }

        public void bind(BluetoothDevice device, OnDeviceClickListener listener) {
            txtDeviceName.setText(device.getName() != null ? device.getName() : "Unknown Device");
            txtDeviceAddress.setText(device.getAddress());
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onDeviceClick(device);
            });
        }

    }
}