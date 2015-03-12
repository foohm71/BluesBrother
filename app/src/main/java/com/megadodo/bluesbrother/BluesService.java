package com.megadodo.bluesbrother;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.OutputStream;
import java.util.UUID;

public class BluesService extends Service {

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice btdevice = null;
    BluetoothSocket bsocket;
    OutputStream bos = null; //Bluetooth output stream
    // private String MACAddress = "BC:F5:AC:47:33:A2";
    private String MACAddress = "00:1A:7D:DA:71:15";
    private String TAG = "BlueService";
    // private String device_uuid = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
    // private String device_uuid = "00001101-0000-1000-8000-00805F9B34FB";
    private String device_uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee";
    // private String device_uuid = "94f39d29-7d6d-437d-973b-fba39e49d4e2";
    private UUID uuid = null;

    public BluesService() {
    }

    public Runnable ping = new Runnable() {
        @Override
        public void run() {
            Log.v(TAG, "UUID is " + device_uuid);
            Log.v(TAG, "Blues Service Started");

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) { //No adapter. Fail
                Log.e(TAG, "getDefaultAdapter returned null");
            } else {
                try {
                    if (!mBluetoothAdapter.isEnabled()) { //Bluetooth disabled
                        Log.e(TAG, "Bluetooth is Disabled");
                    } else {
                        mBluetoothAdapter.cancelDiscovery();
                        Log.i(TAG, "Connecting to Device: " + MACAddress);
                        btdevice = mBluetoothAdapter.getRemoteDevice(MACAddress);
                        if (btdevice == null) {
                            Log.v(TAG, "Unable to connect");
                        } else {
                            /*
                            if( btdevice.fetchUuidsWithSdp() ){
                                uuid = btdevice.getUuids()[0].getUuid();
                            }
                            Log.i(TAG, "UUID: " + uuid.toString());
                            */

                            Log.i(TAG, "Device: " + btdevice.getName());
                            Log.i(TAG, "Trying to Connect...");
                            bsocket = btdevice.createRfcommSocketToServiceRecord(UUID.fromString(device_uuid));
                            // bsocket = btdevice.createRfcommSocketToServiceRecord(uuid);
                            /*
                            if (uuid == null)
                                bsocket = btdevice.createRfcommSocketToServiceRecord(UUID.fromString(device_uuid));
                            else
                                bsocket = btdevice.createRfcommSocketToServiceRecord(uuid);
                            */
                            bsocket.connect();
                            bos = bsocket.getOutputStream();
                            Log.v(TAG, "Blues Service - Ping");
                            bos.write("Ping".getBytes());
                            bsocket.close();
                        }
                    }
                } catch (Exception e) {
                    Log.v(TAG, "Blues Service Stopped");
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        this.ping.run();
        this.stopSelf();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
