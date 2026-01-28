package com.hotel.beacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BeaconActivity extends AppCompatActivity {
    private BluetoothLeAdvertiser advertiser;
    private boolean isAdvertising = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Button toggleButton = new Button(this);
        toggleButton.setText("Start Hotel Beacon");
        toggleButton.setOnClickListener(v -> toggleBeacon());
        setContentView(toggleButton);
        
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        advertiser = adapter.getBluetoothLeAdvertiser();
    }

    private void toggleBeacon() {
        if (isAdvertising) {
            advertiser.stopAdvertising(advertiseCallback);
            isAdvertising = false;
            Toast.makeText(this, "Beacon Stopped", Toast.LENGTH_SHORT).show();
        } else {
            startAdvertising();
        }
    }

    private void startAdvertising() {
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setConnectable(false)
            .build();

        AdvertiseData data = new AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .setIncludeTxPowerLevel(true)
            .addServiceUuid(android.os.ParcelUuid.fromString("0000180F-0000-1000-8000-00805F9B34FB"))
            .build();

        advertiser.startAdvertising(settings, data, advertiseCallback);
    }

    private AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            isAdvertising = true;
            Toast.makeText(BeaconActivity.this, "HotelGate Beacon Started", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStartFailure(int errorCode) {
            Toast.makeText(BeaconActivity.this, "Beacon Failed: " + errorCode, Toast.LENGTH_SHORT).show();
        }
    };
}