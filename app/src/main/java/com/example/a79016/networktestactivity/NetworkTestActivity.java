package com.example.a79016.networktestactivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NetworkTestActivity extends AppCompatActivity {


    private static final int REQUEST_LOCATION = 0;
    private static final int REQUEST_WIFI = 1;
    private static final int REQUEST_BT = 2;
    private static final int TURN_OFF_GPS = 3;

    //Instance Variables to alter the statuses
    private TextView locationStatus;
    private TextView wifiStatus;
    private TextView btStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_test);

        locationStatus = (TextView) findViewById(R.id.LOCATION_STATUS);
        wifiStatus = (TextView) findViewById(R.id.WIFI_STATUS);
        btStatus = (TextView) findViewById(R.id.BLUETOOTH_STATUS);
    }

    public void testLocation(View v) {
        boolean network = false;

        LocationManager manager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager != null) {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Intent gpsIntent =
                        new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(gpsIntent, REQUEST_LOCATION);
            }

            LocationListener listener = new NetworkTestLocationListener();

            if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                                this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                }
                manager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 0, 0, listener);

                Location location = manager.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    locationStatus.setText("Your device is using its Network" +
                            " Provider.\nYour current " +
                            "locatoin is: (" +
                            location.getLatitude() + ", " +
                            location.getLongitude() + ")");
                    network = true;
                } else {
                    locationStatus.setText("Your Network Provider is enabled," +
                            " but is unable to get your " +
                            "coordinates");
                }
            }

            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0, listener);

                Location location = manager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);

                if (location != null) {
                    locationStatus.setText("Your device is using its GPS" +
                            " Provider.\nYour current " +
                            "location is:\n(" +
                            location.getLatitude() + ", " +
                            location.getLongitude() + ")");
                } else if (!network) {
                    locationStatus.setText("Your GPS Provider is enabled," +
                            " but is unable to get your " +
                            "coordinates");
                }
            }
        } else {
            locationStatus.setText("Either your location services are off or" +
                                    " your device is unable to pinpoint your" +
                                    "current location.\n Please try again.");
        }
    }

    public void toggleWifi (View v) {

        WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
            wifiStatus.setText("Your wifi is toggled off");
        }else{
            wifiManager.setWifiEnabled(true);
            wifiStatus.setText("Your wifi is toggled on.\n You are connected to:\n" +
                                wifiInfo.getMacAddress());
        }
    }

    public void connectWifiNetwork (View v) {

        WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiManager.isWifiEnabled() && wifiInfo.getNetworkId() != -1) {
            wifiStatus.setText("Your wifi is already enabled\n You are connected to:\n" +
                                wifiInfo.getMacAddress());
        }else{
            Intent wifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivityForResult(wifiIntent, REQUEST_WIFI);
        }
    }

    public void testBluetooth (View v) {

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter != null && btAdapter.isEnabled()) {
            btStatus.setText("Passed");
        } else if (btAdapter == null) {
            btStatus.setText("Your device does not have Bluetooth Capabilities");
        } else {
            Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btIntent, REQUEST_BT);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                LocationManager manager =
                        (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationStatus.setText("Your GPS Provider is enabled");
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationStatus.setText("Your Network Provider is enabled.");
                } else {
                    locationStatus.setText("Please try again");
                }
                break;
            case REQUEST_WIFI:
                WifiManager wifi = (WifiManager)
                        getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifi.isWifiEnabled()){
                    wifiStatus.setText("Your WiFi is enabled");
                } else {
                    wifiStatus.setText("Please try again");
                }
                break;
            case REQUEST_BT:
                if (resultCode == Activity.RESULT_OK) {
                    btStatus.setText("Passed");
                } else {
                    btStatus.setText("Please try again");
                }
                break;
            case TURN_OFF_GPS:
                LocationManager locManager =
                        (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationStatus.setText("Your GPS Provider is still available.\n" +
                                            "To turn it off, press \"Refresh Statuses\"\n" +
                                            "and manually turn it off.");
                } else if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationStatus.setText("Your Network Provider is still available.\n" +
                                            "To turn it off, press \"Refresh Statuses\"\n" +
                                            "and manually turn it off.");
                } else {
                    locationStatus.setText("Location Test");
                }
                break;
        }
    }

    public void refreshStatus (View v) {

        wifiStatus.setText("Wifi Test");
        btStatus.setText("Bluetooth Test");

        WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter.disable();

        LocationManager manager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(gpsIntent, TURN_OFF_GPS);
        }
    }
}
