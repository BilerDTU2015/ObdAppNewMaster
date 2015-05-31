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

public class NetworkService extends IntentService {

    public static final int STATUS_ERROR = 0;
    public static final int STATUS_SENDING = 1;
    public static final int STATUS_SENDING_MULTI_DATA = 2;
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

    public NetworkService() {
        super(NetworkService.class.getName());
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
                receiver.send(STATUS_CONNECTED, bundle);
                Log.d(TAG, "Connected to car");
            } catch (IOException e) {
                this.socket = null; // be able to try again if first connect fails
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
                buffer = new byte[8192];
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
                String data;
                //String _pid; // used for more then one pid
                    while (is_reading) {
                        try {
                            if (pid.equals("6FA")) {
                                buffer = new byte[60];
                            }
                            if (pid.equals("418")){
                                buffer = new byte[18];
                            }
                            int bytesRead = inputStream.read(buffer);
                            if(bytesRead == 18) {
                                    data = dataHandler.gearShiftPositionRawToReal(buffer);
                                    bundle.putString("result_1", data);
                                    receiver.send(STATUS_SENDING, bundle);
                            }

                            if (bytesRead == 20) {
                                //_pid = new String(buffer, "ASCII").substring(0, 3); // used for more then one pid
                                switch (pid){
                                    case "374":
                                        data = dataHandler.stateOfChargeRawToReal(buffer);
                                        bundle.putString("result_1", data);
                                        receiver.send(STATUS_SENDING, bundle);
                                        break;
                                    case "346":
                                        data = dataHandler.evPowerRawToReal(buffer);
                                        bundle.putString("result_1", data);
                                        receiver.send(STATUS_SENDING, bundle);
                                        break;
                                    case "412":
                                        data = dataHandler.velocityRawToReal(buffer);
                                        bundle.putString("result_1", data);
                                        data = dataHandler.odometerRawToReal(buffer);
                                        bundle.putString("result_2", data);
                                        bundle.putInt("data_size", 2);
                                        receiver.send(STATUS_SENDING_MULTI_DATA, bundle);
                                        break;
                                    case "389":
                                        data = dataHandler.chargingRawToReal(buffer);
                                        bundle.putString("result_1", data);
                                        data = dataHandler.voltageRawToReal(buffer);
                                        bundle.putString("result_2", data);
                                        bundle.putInt("data_size", 2);
                                        receiver.send(STATUS_SENDING_MULTI_DATA, bundle);
                                        break;
                                    case "696":
                                        data = dataHandler.quickChargeStatusRawToReal(buffer);
                                        bundle.putString("result_1", data);
                                        receiver.send(STATUS_SENDING, bundle);
                                        break;
                                    case "3A4":
                                        data = dataHandler.airConditionRawToReal(buffer);
                                        bundle.putString("result_1", data);
                                        receiver.send(STATUS_SENDING, bundle);
                                        break;
                                    case "424":
                                        data = dataHandler.frontLightRawToReal(buffer);
                                        bundle.putString("result_1", data);
                                        data = dataHandler.leftBlinkLightRawToReal(buffer);
                                        bundle.putString("result_2", data);
                                        data = dataHandler.rightBlinkLightRawToReal(buffer);
                                        bundle.putString("result_3", data);
                                        bundle.putInt("data_size", 3);
                                        receiver.send(STATUS_SENDING_MULTI_DATA, bundle);
                                        break;
                                    case "231":
                                        data = dataHandler.breakLampRawToReal(buffer);
                                        bundle.putString("result_1", data);
                                        receiver.send(STATUS_SENDING, bundle);
                                        break;
                                }
                                buffer = new byte[20];
                            }
                            if (bytesRead == 60) {
                                    data = dataHandler.vinRawToReal(buffer);
                                    bundle.putString("result_1", data);
                                    receiver.send(STATUS_SENDING, bundle);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        };
        t.start();
    }
}
