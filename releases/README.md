# Hotel MDU Android App

## Installation Instructions

### Download APK
1. Download `hotel-mdu-v1.0.apk` from the releases folder
2. Transfer to your Android device

### Install on Android Device
1. Enable "Install from Unknown Sources" in Settings > Security
2. Open the APK file using a file manager
3. Tap "Install" when prompted
4. Grant BLE permissions when app launches

### BLE Testing
- App requires Bluetooth Low Energy (BLE) support
- Enable Bluetooth and Location services
- Grant location permissions for BLE scanning
- Look for hotel beacons (Gate, Kiosk, Elevator, Room zones)

### Features
- Hotel management interface via React WebView
- Native Android BLE scanning for RSSI detection
- Zone-based beacon detection and mapping
- Real-time beacon signal strength monitoring

## Version History
- v1.0: Initial release with BLE integration