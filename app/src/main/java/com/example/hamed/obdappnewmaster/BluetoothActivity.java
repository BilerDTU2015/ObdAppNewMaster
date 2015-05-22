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
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends Activity implements OnClickListener,DownloadResultReceiver.Receiver {

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
    private DownloadResultReceiver mReceiver;
    private BluetoothDevice bluetoothDevice;

    private String mDeviceIdentifier;
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
                    bluetoothDevice = myBluetoothAdapter.getRemoteDevice(getUUID());
                }
            });

            BTArrayAdapter = createAdapter(values);
            myListView.setAdapter(BTArrayAdapter);


            mReceiver = new DownloadResultReceiver(new Handler());
            mReceiver.setReceiver(this);
        }
    }
    public String getUUID() {
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
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("uuid : " + uuidString)
                .setTitle("Connect to " + name + "?");
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        builder.setPositiveButton("Yes mein Führer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
//                try {
//                    mBtSocket = connectToOBD(uuidString);
//                    Toast.makeText(getApplicationContext(),
//                            "Connected to bt socket : " + mBtSocket.isConnected(), Toast.LENGTH_LONG)
//                            .show();
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(),
//                            "fail", Toast.LENGTH_LONG)
//                            .show();
//                    e.printStackTrace();
//                }
            }
        });
        builder.setNegativeButton("NEIN NEIN NEIN!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.show();
        return uuidString;
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
            /*
            case R.id.setAtParameters:
              //  setUpAtCommand();
                break;
            //case R.id.stopData:
                //try {
                   // sendCommand("z");
                   // clearInput();
                   // is_reading = false;
                //} catch (IOException e) {
                   // e.printStackTrace();
                //}
                break;
                */
            case R.id.sendCommand:
                    //sendCommand("atma");
                    //startLongRunningOperation();
                this.pid = "412";
                    runService();
                break;

        }
    }

    public void runService() {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, ServiceTest.class);

        /* Send optional extras to Download IntentService */
        intent.putExtra("pid", this.pid);
        intent.putExtra("bluetoothDevice", this.bluetoothDevice);
        intent.putExtra("receiver", this.mReceiver);
        intent.putExtra("requestId", 101);

        startService(intent);
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

    public void off() {
        myBluetoothAdapter.disable();
        text.setText("Status: Disconnected");

        Toast.makeText(getApplicationContext(), "Bluetooth turned off",
                Toast.LENGTH_LONG).show();
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

    private ArrayAdapter<String> createAdapter(ArrayList<String> data) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data.toArray(new String[data.size()]));
    }

    // Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();
    // Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };

    protected void updateReadData() {
        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
                mHandler.post(mUpdateResults);
            }
        };
        t.start();
    }

    private void updateResultsInUi() {
        live_data.setText(this.kmt);
    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case ServiceTest.STATUS_RUNNING:

                setProgressBarIndeterminateVisibility(true);
                break;
            case ServiceTest.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */

                String[] results = resultData.getStringArray("result");
                Log.i("RECIVED", "speed : " + results[0] + "total : " + results[1]);
                updateReadData();

                /* Update ListView with result */

                break;
            case ServiceTest.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
