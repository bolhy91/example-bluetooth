package com.example.testbluetooh;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.PairedPrinter;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildView();
        setupPermissions();
        setupViewModel();
        observeBluetoothState();
        
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager != null ? bluetoothManager.getAdapter() : null;
    }

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothViewModel viewModel;
    private boolean isScanning = false;

    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;
    
    private Button btnSearch;
    private Button btnStop;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private RecyclerView recyclerDevices;

    BluetoothDeviceAdapter scannedAdapter = new BluetoothDeviceAdapter(device -> {
        Printooth.INSTANCE.removeCurrentPrinter();
        //Printooth.INSTANCE.setPrinter(device.getName(), device.getAddress());
        Boolean isPaired = Printooth.INSTANCE.hasPairedPrinter();
        PairedPrinter pairedPrinter = Printooth.INSTANCE.getPairedPrinter();
        //Toast.makeText(this, "Click en escaneado: " + pairedPrinter.getName(), Toast.LENGTH_SHORT).show();

        if (Printooth.INSTANCE.hasPairedPrinter()){
            //Printooth.INSTANCE.removeCurrentPrinter();
//            var printer = new PairedPrinter(device.getName(), device.getAddress());
//            ArrayList<Printable> printables = new ArrayList<>();
//            Printable printable = new TextPrintable.Builder()
//                    .setText("Hello World")
//                    .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
//                    .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC852())
//                    .setNewLinesAfter(5)
//                    .build();
//            printables.add(printable);
//
//            Printooth.INSTANCE.printer(printer).print(printables);
            Toast.makeText(this, getString(R.string.msg_printer_selected, device.getName()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.msg_printer_not_configured, Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Click en paired: " + isPaired, Toast.LENGTH_SHORT).show();
    });
    BluetoothDeviceAdapter pairedAdapter = new BluetoothDeviceAdapter(device -> {
    });

    private boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    private void observeBluetoothState() {
        viewModel.getState().observe(this, state -> {
            // Combinar dispositivos escaneados y emparejados
            List<BluetoothDevice> allDevices = new ArrayList<>();
            Map<String, BluetoothDevice> deviceMap = new HashMap<>();

            // Agregar dispositivos emparejados
            for (BluetoothDevice device : state.getPairedDevices()) {
                deviceMap.put(device.getAddress(), device);
            }

            // Agregar o actualizar con dispositivos escaneados
            for (BluetoothDevice device : state.getScannedDevices()) {
                deviceMap.put(device.getAddress(), device);
            }

            allDevices.addAll(deviceMap.values());

            // Actualizar UI
            if (allDevices.isEmpty()) {
                if (isScanning) {
                    tvEmptyState.setText(R.string.searching_devices);
                } else {
                    tvEmptyState.setText(R.string.no_hay_dispositivos_disponibles);
                }
                tvEmptyState.setVisibility(View.VISIBLE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
            }

            scannedAdapter.updateItems(allDevices);
        });
    }

    private void setupViewModel() {
        BluetoothViewModelFactory factory = new BluetoothViewModelFactory(this);
        viewModel = new ViewModelProvider(this, factory).get(BluetoothViewModel.class);
    }

    private void setupPermissions() {
        // Registrar callback para activación de Bluetooth
        enableBluetoothLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Bluetooth activado, verificar permisos
                    checkAndRequestPermissions();
                }
            }
        );

        // Registrar callback para permisos
        permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    allGranted &= isGranted;
                }

                if (allGranted) {
                    // Todos los permisos concedidos, verificar Bluetooth
                    if (!isBluetoothEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        enableBluetoothLauncher.launch(enableBtIntent);
                    }
                } else {
                    Toast.makeText(this, "Se requieren permisos para escanear dispositivos", Toast.LENGTH_LONG).show();
                }
            }
        );

        // Iniciar verificación de permisos
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        ArrayList<String> permissionsToRequest = new ArrayList<>();

        // Permisos necesarios para Android 10 (API 29)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        // Permisos adicionales para Android 12 (API 31) y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) 
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) 
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        } else if (!isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBtIntent);
        }
    }

    private void buildView() {
        // Inicializar vistas
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        recyclerDevices = findViewById(R.id.recyclerDevices);
        btnSearch = findViewById(R.id.btnSearch);
        btnStop = findViewById(R.id.btnStop);

        // Configurar RecyclerView
        recyclerDevices.setLayoutManager(new LinearLayoutManager(this));
        recyclerDevices.setAdapter(scannedAdapter);

        // Configurar botones
        btnSearch.setOnClickListener(v -> startScanning());
        btnStop.setOnClickListener(v -> stopScanning());
    }

    private void startScanning() {
        isScanning = true;
        btnSearch.setVisibility(View.GONE);
        btnStop.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
        viewModel.startScan();
    }

    private void stopScanning() {
        isScanning = false;
        btnSearch.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        viewModel.stopScan();
    }
}