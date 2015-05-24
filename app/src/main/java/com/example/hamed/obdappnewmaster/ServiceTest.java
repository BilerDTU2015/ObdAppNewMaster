package com.example.hamed.obdappnewmaster;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ServiceTest extends IntentService {

    public static final int STATUS_SENDING = 0;
    public static final int STATUS_ERROR = 1;
    public static final int STOP = 0;
    public static final int START_UP = 1;
    public static final int SEND_COMMAND = 2;

    public static final String AT_STOP = "z";
    public static final String ATMA = "atma";

    public static boolean is_reading = false;
    private String pid = "";
    private static final String TAG = "SocketService";
    private int requestId;

    private BluetoothDevice bluetoothDevice;
    private Bundle bundle;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ServiceTest() {
        super(ServiceTest.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        requestId = intent.getIntExtra("requestId", 0);
        switch (requestId) {
            case START_UP:
                bluetoothDevice = intent.getParcelableExtra("bluetoothDevice");
                connectToOBD(bluetoothDevice);
                Log.d(TAG, "Service Started!");
                break;
            case STOP:
                try {
                    is_reading = false;
                    sendCommand(AT_STOP);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SEND_COMMAND:
                try {
                    this.pid = intent.getStringExtra("pid");
                    setUpAtCommand(pid);
                    sendCommand(ATMA);
                    readData(receiver);
                } catch (IOException e) {
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
                break;
        }

        bundle = new Bundle();



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

    public void sendCommand(String at_command) throws IOException {
        outputStream.write((at_command + "\r").getBytes());
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
                            receiver.send(STATUS_SENDING, bundle);
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
