package com.example.testbluetooh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.DeviceViewHolder> {
    private static final String UNKNOWN_DEVICE = "Unknown Device";
    
    private final List<BluetoothDevice> devices = new ArrayList<>();
    private final OnDeviceClickListener onDeviceClickListener;
    public interface OnDeviceClickListener {
        void onDeviceClick(BluetoothDevice device);
    }

    public BluetoothDeviceAdapter(OnDeviceClickListener deviceListener) {
        this.onDeviceClickListener = deviceListener;
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
        private final ImageView ivDeviceType;
        private final TextView tvDeviceName;
        private final TextView tvDeviceAddress;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDeviceType = itemView.findViewById(R.id.ivDeviceType);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            tvDeviceAddress = itemView.findViewById(R.id.tvDeviceAddress);
        }

        public void bind(BluetoothDevice device, OnDeviceClickListener deviceListener) {
            String deviceName = device.getName();
            if (deviceName == null || deviceName.equals(UNKNOWN_DEVICE)) {
                tvDeviceName.setText(device.getAddress());
                tvDeviceAddress.setVisibility(View.GONE);
            } else {
                tvDeviceName.setText(deviceName);
                tvDeviceAddress.setText(device.getAddress());
                tvDeviceAddress.setVisibility(View.VISIBLE);
            }
            ivDeviceType.setImageResource(R.drawable.ic_bluetooth_device);
            itemView.setOnClickListener(v -> {
                if (deviceListener != null) deviceListener.onDeviceClick(device);
            });
        }

    }
}