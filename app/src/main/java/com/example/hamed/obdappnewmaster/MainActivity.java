package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamed.Service.MyResultReceiver;
import com.example.hamed.Service.NetworkService;
import com.example.hamed.storage.InternalStorage;
import com.google.android.gms.maps.GoogleMap;

import com.example.hamed.controller.DataController;


public class MainActivity extends Activity implements View.OnClickListener {

    private boolean IS_RECORDING = false;

    private TextView mSwitchStatus;
    private Switch mSwitch;
    private ImageButton mBtnBluetooth;
    private ImageButton mBtnMap;
    private ImageButton mBtnDashboard;
    private ImageButton mBtnLoadData;

    private DataController mDataController;
    private TextView switchStatus;
    private Switch mySwitch;
    private GoogleMap map;

    private MyResultReceiver obdReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwitchStatus = (TextView) findViewById(R.id.main_record_status_textview);
        mSwitch = (Switch) findViewById(R.id.record_switch);

        //set the switch to ON
        mSwitch.setChecked(false);
        //attach a listener to check for changes in state
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    mSwitchStatus.setText("Data recording ON");
                    startRecording();
                } else {
                    mSwitchStatus.setText("Data recording OFF");
                    stopRecording();
                }
            }
        });

        mBtnBluetooth = (ImageButton)findViewById(R.id.btn_bluetooth);
        mBtnBluetooth.setOnClickListener(this);

        mBtnMap = (ImageButton)findViewById(R.id.btn_map);
        mBtnMap.setOnClickListener(this);

        mBtnLoadData = (ImageButton)findViewById(R.id.btn_dashboard);
        mBtnLoadData.setOnClickListener(this);

    }

    /**
     * Method to start data logging (saves to file)
     * The file is emptied every time you toggle (ERASES PREVIOUS DATA)
     */
    public void startRecording(){
        // Start location data
        mDataController = new DataController(getApplicationContext());
       // mDataController.startLocationService();
        mDataController.startObdAndLocationLogging();

//        // Start obd data
//        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NetworkService.class);
//        obdReceiver = new MyResultReceiver(new Handler());
//        obdReceiver.setReceiver(this);
//        startService(intent);
    }

    /**
     * Method to stop data logging
     */
    public void stopRecording(){
        // Stop location service
        if (mDataController != null)
            mDataController.stopRecording();

        mBtnDashboard = (ImageButton)findViewById(R.id.btn_dashboard);
        mBtnDashboard.setOnClickListener(this);

        switchStatus = (TextView) findViewById(R.id.main_record_status_textview);
        mySwitch = (Switch) findViewById(R.id.record_switch);
        switchButton();

    }

    @Override
    public void onClick(View view) {
        Context con = view.getContext();
        Intent intent = null;

        switch (view.getId()) {
            case R.id.btn_bluetooth:
                intent = new Intent(con, BluetoothActivity.class);
                break;
            case R.id.btn_map:
                intent = new Intent(con, DataActivity.class);
                break;
            case R.id.btn_load:
                intent = new Intent(con, DataActivity.class);
                break;
            case R.id.btn_dashboard:
                intent = new Intent(con, LiveDataActivity.class);
                break;
        }
        startActivity(intent);
    }


    public void switchButton(){

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if(isChecked){
                    IS_RECORDING = true;
                    startRecording();
                    // flush data file
                    InternalStorage.clearFile(getApplicationContext());

                    SharedPreferences.Editor editor = getSharedPreferences("com.example.xyz", MODE_PRIVATE).edit();
                    editor.commit();
                    editor.putBoolean("NameOfThingToSave", true);
                    switchStatus.setText("SpeedRecording is currently ON");

                }else{
                    IS_RECORDING = false;
                    stopRecording();
                    SharedPreferences.Editor editor = getSharedPreferences("com.example.xyz", MODE_PRIVATE).edit();
                    editor.commit();
                    editor.putBoolean("NameOfThingToSavea", false);
                    switchStatus.setText("SpeedRecording is currently OFF");
                }

            }
        });
    }
}
