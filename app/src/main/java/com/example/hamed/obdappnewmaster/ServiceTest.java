package com.example.hamed.obdappnewmaster;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ServiceTest extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private String pid = "";

    private static final String TAG = "DownloadService";

    private boolean is_reading = false;
    private Bundle bundle;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ServiceTest() {
        super(ServiceTest.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        Log.d(TAG, "Service Started!");

        String pid = intent.getStringExtra("pid");
        BluetoothDevice bluetoothDevice = intent.getParcelableExtra("bluetoothDevice");

        bundle = new Bundle();

            /* Update UI: Download Service is Running */
            //receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            try {

                /* Sending result back to activity */
                connectToOBD(bluetoothDevice);
                setUpAtCommand(pid);
                sendCommand();
                readData(receiver);

            } catch (Exception e) {

                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }

        Log.d(TAG, "Service Stopping");
        this.stopSelf();
    }

    // Class that takes the string with device identifier and tries to create a bluetooth socket to the device and returns the socket.
    public void connectToOBD(BluetoothDevice bluetoothDevice) {
        String uuidFromString = "00001101-0000-1000-8000-00805f9b34fb";
        BluetoothSocket socket;
        try {
            socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuidFromString));
            socket.connect();
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void setUpAtCommand(String pid) {
        String[] commands = new String[]{"atsp6", "ate0", "ath1", "atcaf0", "atS0", "atcra " + pid};
        try {
            for (int i = 0; i < 6; i++) {
                outputStream.write((commands[i] + "\r").getBytes());
                outputStream.flush();
            }
            clearInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearInput() {
        byte[] buffer = new byte[8192];
        try {
            while (inputStream.available() > 0) {
                int bytesRead = inputStream.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand() throws IOException {
        outputStream.write(("atma" + "\r").getBytes());
        outputStream.flush();
        clearInput();
    }

    protected void readData(final ResultReceiver receiver) {
        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
                is_reading = true;
                byte[] buffer = new byte[20];
                String[] tests;
                DataHandler dataHandler = new DataHandler();
                while (is_reading = true) {
                    try {
                        int bytesRead = inputStream.read(buffer);
                        if (bytesRead == 20) {
                            tests = dataHandler.velocityAndOdometerRawToReal(buffer);
                            bundle.putStringArray("result", tests);
                            receiver.send(STATUS_FINISHED, bundle);
                            //kmt = tests[0];
                            Log.i("TAGGGGG", "byteCount: " + bytesRead + ", tests: km/t: " + tests[0] + " km: " + tests[1]);
                        }
                        buffer = new byte[20];
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }
}
