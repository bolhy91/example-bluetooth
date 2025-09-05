package com.example.testbluetooh;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothViewModel viewModel;

    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;
    BluetoothDeviceAdapter scannedAdapter = new BluetoothDeviceAdapter();
    BluetoothDeviceAdapter pairedAdapter = new BluetoothDeviceAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext()
                .getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothAdapter != null ? bluetoothManager.getAdapter() : null;
        setupPermissions();
        setupViewModel();
        buildView();
        observeBluetoothState();
    }

    private boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    private void observeBluetoothState() {
        viewModel.getState().observe(this, state -> {
            scannedAdapter.updateItems(state.getScannedDevices());
            pairedAdapter.updateItems(state.getPairedDevices());
        });
    }

    private void setupViewModel() {
        BluetoothViewModelFactory factory = new BluetoothViewModelFactory(this);
        viewModel = new ViewModelProvider(this, factory).get(BluetoothViewModel.class);
    }

    private void setupPermissions() {
        enableBluetoothLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                }
        );

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                (Map<String, Boolean> perms) -> {
                    boolean canEnableBluetooth = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Boolean connectGranted = perms.get(Manifest.permission.BLUETOOTH_CONNECT);
                        canEnableBluetooth = connectGranted != null && connectGranted;
                    }

                    if (canEnableBluetooth && !isBluetoothEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        enableBluetoothLauncher.launch(enableBtIntent);
                    }
                }
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
            });
        }
    }

    private void buildView() {
        RecyclerView recyclerScanned = findViewById(R.id.recyclerScanned);
        recyclerScanned.setLayoutManager(new LinearLayoutManager(this));
        recyclerScanned.setAdapter(scannedAdapter);

        RecyclerView recyclerPaired = findViewById(R.id.recyclerPaired);
        recyclerPaired.setLayoutManager(new LinearLayoutManager(this));
        recyclerPaired.setAdapter(pairedAdapter);

        Button btnStartScan = findViewById(R.id.btnStartScan);
        Button btnStopScan = findViewById(R.id.btnStopScan);

        btnStartScan.setOnClickListener(v -> viewModel.startScan());
        btnStopScan.setOnClickListener(v -> viewModel.stopScan());
    }
}