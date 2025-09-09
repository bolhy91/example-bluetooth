package com.example.testbluetooh;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.converter.ArabicConverter;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.data.printer.Printer;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothViewModel viewModel;

    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    private Printing printing = null;
    private Printer customPrinter = null;

    BluetoothDeviceAdapter scannedAdapter = new BluetoothDeviceAdapter(device -> {
        Printooth.INSTANCE.setPrinter(device.getName(), device.getAddress());
        Toast.makeText(this, "Click en escaneado: " + device.getName(), Toast.LENGTH_SHORT).show();
    });
    BluetoothDeviceAdapter pairedAdapter = new BluetoothDeviceAdapter(device -> {
        Boolean isPaired = Printooth.INSTANCE.hasPairedPrinter();
        if (Printooth.INSTANCE.hasPairedPrinter()){
            ArrayList<Printable> printables = new ArrayList<>();
            Printable printable = new TextPrintable.Builder()
                    .setText("Hello World")
                    .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                    .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC852())
                    .setNewLinesAfter(5)
                    .build();
            printables.add(printable);

            Printooth.INSTANCE.printer().print(printables);
            Toast.makeText(this, "Click en escaneado: " + device.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "NO - ELSE PRINT" + device.getName(), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Click en paired: " + isPaired, Toast.LENGTH_SHORT).show();
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext()
                .getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothAdapter != null ? bluetoothManager.getAdapter() : null;
        printing = getPrinting();
        setupPermissions();
        setupViewModel();
        buildView();
        observeBluetoothState();
    }

    private Printing getPrinting() {
        if (Printooth.INSTANCE.hasPairedPrinter()) {
            if (customPrinter != null) {
                return Printooth.INSTANCE.printer(customPrinter);
            } else {
                return Printooth.INSTANCE.printer();
            }
        } else {
            return null;
        }
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