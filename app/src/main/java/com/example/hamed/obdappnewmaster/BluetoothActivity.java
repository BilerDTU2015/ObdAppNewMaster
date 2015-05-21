package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends Activity implements OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    public static boolean is_reading = false;

    private Button onBtn;
    private Button offBtn;
    private Button listBtn;
    private Button sendCMD;
    private Button setAtBtn;
    private Button stopBtn;

    private TextView text;
    private TextView live_data;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;
    private BluetoothSocket mBtSocket;

    private String mDeviceIdentifier;
    private String mDeviceName;
    private String pid;
    private String kmt;

    private InputStream is;
    private OutputStream os;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);

        // take an instance of BluetoothAdapter - Bluetooth radio
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            onBtn.setEnabled(false);
            offBtn.setEnabled(false);
            listBtn.setEnabled(false);
            text.setText("Status: not supported");

            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {

            text = (TextView) findViewById(R.id.text);
            live_data = (TextView) findViewById(R.id.live_data);

            onBtn = (Button) findViewById(R.id.turnOn);
            onBtn.setOnClickListener(this);

            offBtn = (Button) findViewById(R.id.turnOff);
            offBtn.setOnClickListener(this);

            listBtn = (Button) findViewById(R.id.paired);
            listBtn.setOnClickListener(this);

            sendCMD = (Button) findViewById(R.id.sendCommand);
            sendCMD.setOnClickListener(this);

            setAtBtn = (Button) findViewById(R.id.setAtParameters);
            setAtBtn.setOnClickListener(this);

            stopBtn = (Button) findViewById(R.id.stopData);
            stopBtn.setOnClickListener(this);

            myListView = (ListView) findViewById(R.id.pairedListView);
            ArrayList<String> values = new ArrayList();
            //values.add("Paired devices");

            // ListView Item Click Listener
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    int itemPosition = position;

                    // ListView Clicked item value
                    String itemValue = (String) myListView.getItemAtPosition(position);
                    // Save device identifier for connection
                    mDeviceIdentifier = itemValue;

                    // Show Alert
                    Toast.makeText(getApplicationContext(),
                            "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                            .show();

                    createConnectDialog();

                }


            });

            BTArrayAdapter = createAdapter(values);
            myListView.setAdapter(BTArrayAdapter);
        }
    }


    public void createConnectDialog() {

        // split string into device name and device identifier
        String[] splitted = mDeviceIdentifier.split("\\s+");
        int len = splitted.length;
        final String uuidString = splitted[len - 1];
        String name = "";
        // Add every part of splitted except the last one which is uuid
        for (String str : splitted) {
            if (str == uuidString) {
                break;
            }
            name += str;
        }

        // Connect to clicked device in list
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
        //AlertDialog alert = new AlertDialog.Builder(getParent().this).create();

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("uuid : " + uuidString)
                .setTitle("Connect to " + name + "?");

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        builder.setPositiveButton("Yes mein Führer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                try {
                    mBtSocket = connectToOBD(uuidString);
                    Toast.makeText(getApplicationContext(),
                            "Connected to bt socket : " + mBtSocket.isConnected(), Toast.LENGTH_LONG)
                            .show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "fail", Toast.LENGTH_LONG)
                            .show();
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("NEIN NEIN NEIN!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.turnOn:
                on();
                break;
            case R.id.turnOff:
                off();
                break;
            case R.id.paired:
                listPairedDevices();
                break;
            case R.id.setAtParameters:
                setUpAtCommand();
                break;
            case R.id.stopData:
                try {
                    sendCommand("z");
                    is_reading = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.sendCommand:
                String command01 = "atsp6";
                String command02 = "ate0";
                String command03 = "ath1";
                String command04 = "atcra 412";
                String command05 = "atS0";
                String command06 = "atma";
                try {
                    sendCommand(command06);
                    startLongRunningOperation();
                    //readResult();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void on() {
        if (!myBluetoothAdapter.isEnabled()) {

            Toast.makeText(getApplicationContext(), "Mah method",
                    Toast.LENGTH_LONG).show();

            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(), "Bluetooth turned on",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_ENABLE_BT) {
            if (myBluetoothAdapter.isEnabled()) {
                text.setText("Status: Enabled");
            } else {
                text.setText("Status: Disabled");
            }
        }
    }

    public void listPairedDevices() {

        Toast.makeText(getApplicationContext(), "Paired Devices",
                Toast.LENGTH_LONG).show();

        if (!myBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

//    	// see if there is all ready paired devices
        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        ArrayList<String> deviceInfos = new ArrayList();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                deviceInfos.add(device.getName() + "\n" + device.getAddress());
                //  mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            myListView.setAdapter(createAdapter(deviceInfos));

        }
    }

    public void discoverDevices() {
        if (myBluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            myBluetoothAdapter.cancelDiscovery();
        } else {
            BTArrayAdapter.clear();
            myBluetoothAdapter.startDiscovery();
            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    private ArrayAdapter<String> createAdapter(ArrayList<String> data) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data.toArray(new String[data.size()]));
    }

    public void list(View view) {
        // get paired devices
        pairedDevices = myBluetoothAdapter.getBondedDevices();

        // put it's one to the adapter
        for (BluetoothDevice device : pairedDevices)
            BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

        Toast.makeText(getApplicationContext(), "Show Paired Devices",
                Toast.LENGTH_SHORT).show();

    }

    /*
        Class that takes the string with device identifier and tries to create a bluetooth socket to the device and returns the socket.
     */
    public BluetoothSocket connectToOBD(String deviceAddress) {

        String uuidFromString = "00001101-0000-1000-8000-00805f9b34fb";

        BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(deviceAddress);
        BluetoothSocket socket;
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuidFromString));
            socket.connect();
            is = socket.getInputStream();
            os = socket.getOutputStream();

            return socket;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public void setUpAtCommand() {
        String[] commands = new String[]{"atsp6", "ate0", "ath1", "atcaf0", "atS0", "atcra 412"};
        byte[] buffer = new byte[128];
        try {
            for (int i = 0; i < 6; i++) {

                os.write((commands[i] + "\r").getBytes());
                os.flush();
            }
            int bytesRead = is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String command) throws IOException {
        os.write((command + "\r").getBytes());
        os.flush();

    }

    // Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();
    // Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };

    protected void startLongRunningOperation() {

        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
                is_reading = true;
                byte[] buffer = new byte[20];
                String test = "";
                String[] tests = new String[2];
                DataHandler dataHandler = new DataHandler();
                while (is_reading = true) {
                    try {
                        int bytesRead = is.read(buffer);
                        if (bytesRead == 20) {
                            tests = dataHandler.velocityAndOdometerRawToReal(buffer);
                            kmt = tests[0];
                            Log.i("TAGGGGG", "byteCount: " + bytesRead + ", tests: km/t: " + tests[0] + " km: " + tests[1]);
                            mHandler.post(mUpdateResults);
                        }
                        buffer = new byte[20];
                        test = "";

                        //is.read(buffer);
                        //return buffer.toString();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //mResults = doSomethingExpensive();
                mHandler.post(mUpdateResults);
            }
        };
        t.start();
    }

    private void updateResultsInUi() {
        live_data.setText(this.kmt);
    }
//
//        public String readResult() throws IOException {
//        byte[] buffer = new byte[20];
//        String test="";
//        String[] tests = new String[2];
//        DataHandler dataHandler = new DataHandler();
//        while(is_reading=true) {
//            try {
//                int bytesRead = is.read(buffer);
//                if(bytesRead == 20) {
//                    tests = dataHandler.velocityAndOdometerRawToReal(buffer);
//                    Log.i("TAGGGGG", "byteCount: " + bytesRead + ", tests: km/t: " + tests[0] + " km: " + tests[1]);
//                }
//                buffer = new byte[20];
//                test = "";
//
//                //is.read(buffer);
//                //return buffer.toString();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return "";
//    }

    // Requires inputstream is has been successfully initialized
    public ArrayList<Integer> formatRawData(String data) {

        // Example of raw data, String str = "[B@42fbb420"

        // read string each two chars
        ArrayList<Integer> buffer = new ArrayList<>();
        buffer.clear();
        int begin = 0;
        int end = 2;
        while (end <= data.length()) {
            buffer.add(Integer.decode("0x" + data.substring(begin, end)));
            begin = end;
            end += 2;
        }
        return buffer;
    }


    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void find(View view) {
        if (myBluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            myBluetoothAdapter.cancelDiscovery();
        } else {
            BTArrayAdapter.clear();
            myBluetoothAdapter.startDiscovery();

            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    public void off() {
        myBluetoothAdapter.disable();
        text.setText("Status: Disconnected");

        Toast.makeText(getApplicationContext(), "Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }


    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}

