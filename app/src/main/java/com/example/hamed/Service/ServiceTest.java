package com.example.hamed.Service;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.hamed.storage.DataHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ServiceTest extends IntentService {

    public static final int STATUS_ERROR = 0;
    public static final int STATUS_SENDING = 1;
    public static final int STATUS_SENDING_ARRAY = 2;
    public static final int STATUS_CONNECTED = 3;
    public static final int STOP = 0;
    public static final int START_UP = 1;
    public static final int SEND_COMMAND = 2;

    public static final String AT_STOP = "z";
    public static final String ATMA = "atma";

    private static boolean is_reading = false;
    private String pid = "";
    private static final String TAG = "SocketService : ";
    private int requestId;

    private BluetoothDevice bluetoothDevice;
    private Bundle bundle;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    private static BluetoothSocket socket;

    public ServiceTest() {
        super(ServiceTest.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        pid = "";
        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        requestId = intent.getIntExtra("requestId", 0);
        bundle = new Bundle();
        switch (requestId) {
            case START_UP:
                Log.d(TAG, "Service Started!");
                bluetoothDevice = intent.getParcelableExtra("bluetoothDevice");
                connectToOBD(bluetoothDevice, receiver);
                receiver.send(STATUS_CONNECTED, bundle);
                break;
            case STOP:
                try {
                    is_reading = false;
                    sendCommand(AT_STOP);
                } catch (IOException e) {
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
                break;
            case SEND_COMMAND:
                try {
                    pid = intent.getStringExtra("pid");
                    setUpAtCommand();
                    if (!pid.equals("")) {
                        sendCommand("atcra " + pid);
                    }
                    sendCommand(ATMA);
                    readData(receiver);
                } catch (IOException e) {
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
                break;
        }
        Log.d(TAG, "Service Stopping");
        this.stopSelf();
    }

    // Class that takes the string with device identifier and tries to create a bluetooth socket to the device and returns the socket.
    public void connectToOBD(BluetoothDevice bluetoothDevice, ResultReceiver receiver) {
        if (this.socket == null) {
            String uuidFromString = "00001101-0000-1000-8000-00805f9b34fb";
            try {
                this.socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuidFromString));
                this.socket.connect();
                inputStream = this.socket.getInputStream();
                outputStream = this.socket.getOutputStream();
                Log.d(TAG, "Connected to car");
            } catch (IOException e) {
                Log.d(TAG, "Fail to connect to car");
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        } else {
            Log.d(TAG, "Already Connected");
        }
    }

    public void setUpAtCommand() {
        String[] commands = new String[]{"atsp6", "ate0", "ath1", "atcaf0", "atS0"};
        try {
            for (int i = 0; i < 5; i++) {
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
                DataHandler dataHandler = new DataHandler();
                String[] tests;
                String test;
                String pid;
                    while (is_reading) {
                        try {
                            int bytesRead = inputStream.read(buffer);
                            if (bytesRead == 20) {
                                pid = new String(buffer, "ASCII").substring(0, 3);
                                switch (pid){
                                    case "412":
                                        tests = dataHandler.velocityAndOdometerRawToReal(buffer);
                                        bundle.putStringArray("result", tests);
                                        receiver.send(STATUS_SENDING_ARRAY, bundle);
                                        Log.i("TAGGGGG", "byteCount: " + bytesRead + ", tests: km/t : " + tests[0] + " km : " + tests[1]);
                                        break;
                                    case "346":
                                        test = dataHandler.evPowerRawToReal(buffer);
                                        bundle.putString("result", test);
                                        receiver.send(STATUS_SENDING, bundle);
                                        Log.i("TAGGGGG", "byteCount: " + bytesRead + ", W : " + test);
                                        break;
                                    case "374":
                                        test = dataHandler.stateOfChargeRawToReal(buffer);
                                        bundle.putString("result", test);
                                        receiver.send(STATUS_SENDING, bundle);
                                        Log.i("TAGGGGG", "byteCount: " + bytesRead + ", power : " + test);
                                        break;
                                }
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
