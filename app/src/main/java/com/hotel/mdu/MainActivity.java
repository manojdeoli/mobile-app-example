package com.hotel.mdu;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private WebView webView;
    private static final String TAG = "HotelMDU";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private BluetoothLeScanner bleScanner;
    private boolean isScanning = false;
    
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            try {
                String deviceName = result.getDevice().getName();
                if (deviceName == null) deviceName = "Unknown";
                
                // Filter for hotel beacons
                if (deviceName.contains("Hotel") || deviceName.contains("Gate") || 
                    deviceName.contains("Kiosk") || deviceName.contains("Elevator") || 
                    deviceName.contains("Room")) {
                    
                    JSONObject bleEvent = new JSONObject();
                    bleEvent.put("deviceId", result.getDevice().getAddress());
                    bleEvent.put("deviceName", deviceName);
                    bleEvent.put("rssi", result.getRssi());
                    bleEvent.put("timestamp", System.currentTimeMillis());
                    
                    runOnUiThread(() -> {
                        webView.evaluateJavascript(
                            "window.onBleEvent && window.onBleEvent('" + bleEvent.toString() + "');", 
                            null
                        );
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "BLE scan error: " + e.getMessage());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setupWebView();
        setupBluetooth();
        requestPermissions();
    }

    private void setupWebView() {
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        
        WebView.setWebContentsDebuggingEnabled(true);
        webView.addJavascriptInterface(new AndroidBLE(), "AndroidBLE");
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "Page loaded: " + url);
            }
        });
        
        webView.setWebChromeClient(new WebChromeClient());
        
        String url = "file:///android_asset/build/index.html";
        Log.d(TAG, "Loading URL: " + url);
        webView.loadUrl(url);
    }
    
    private void setupBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null) {
            bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }
    
    private void requestPermissions() {
        String[] permissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        };
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    public class AndroidBLE {
        @JavascriptInterface
        public void startScan() {
            if (bleScanner != null && !isScanning && hasPermissions()) {
                ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
                bleScanner.startScan(null, settings, scanCallback);
                isScanning = true;
                Log.d(TAG, "BLE scan started");
            }
        }

        @JavascriptInterface
        public void stopScan() {
            if (bleScanner != null && isScanning) {
                bleScanner.stopScan(scanCallback);
                isScanning = false;
                Log.d(TAG, "BLE scan stopped");
            }
        }

        @JavascriptInterface
        public void requestScan() {
            startScan();
            new android.os.Handler().postDelayed(() -> stopScan(), 3000);
        }
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) 
               == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onDestroy() {
        if (isScanning) {
            bleScanner.stopScan(scanCallback);
        }
        super.onDestroy();
    }
}