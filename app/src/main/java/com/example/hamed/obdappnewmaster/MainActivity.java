package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.hamed.storage.InternalStorage;


import com.example.hamed.controller.DataController;


public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener {

    private boolean IS_RECORDING = false;

    private TextView mSwitchStatus;
    private Switch mSwitch;
    private ImageButton mBtnStartRecording;
    private ImageButton mBtnBluetooth;
    private ImageButton mBtnMap;
    private ImageButton mBtnDashboard;
    private ImageButton mBtnLoadData;
    private Spinner spinnerPid2;
    private String selected_text;
    private String pid;

    private DataController mDataController;
    private TextView switchStatus;
    private Switch mySwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mSwitchStatus = (TextView) findViewById(R.id.main_record_status_textview);
//        mSwitch = (Switch) findViewById(R.id.record_switch);
//
//        //set the switch to ON
//        mSwitch.setChecked(false);
//        //attach a listener to check for changes in state
//        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,
//                                         boolean isChecked) {
//
//                if (isChecked) {
//                    mSwitchStatus.setText("Data recording ON");
//                    startRecording();
//                } else {
//                    mSwitchStatus.setText("Data recording OFF");
//                    stopRecording();
//                }
//            }
//        });

        mBtnStartRecording = (ImageButton)findViewById(R.id.btn_start_recording);
        mBtnStartRecording.setOnClickListener(this);

        mBtnBluetooth = (ImageButton)findViewById(R.id.btn_bluetooth);
        mBtnBluetooth.setOnClickListener(this);

        mBtnMap = (ImageButton)findViewById(R.id.btn_map);
        mBtnMap.setOnClickListener(this);

        mBtnLoadData = (ImageButton)findViewById(R.id.btn_dashboard);
        mBtnLoadData.setOnClickListener(this);

        mBtnDashboard = (ImageButton)findViewById(R.id.btn_dashboard);
        mBtnDashboard.setOnClickListener(this);
        // Start location data
        mDataController = new DataController(getApplicationContext());

        spinnerPid2 = (Spinner)findViewById(R.id.spinnerPid2);
        spinnerPid2.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, getResources().getStringArray(R.array.pid2));
        spinnerPid2.setAdapter(adapter);

    }

    /**
     * Method to start data logging (saves to file)
     * The file is emptied every time you toggle (ERASES PREVIOUS DATA)
     */
    public void startRecording(){
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

//        switchStatus = (TextView) findViewById(R.id.main_record_status_textview);
//        mySwitch = (Switch) findViewById(R.id.record_switch);
        //switchButton();

    }

    @Override
    public void onClick(View view) {
        Context con = view.getContext();
        Intent intent = null;

        switch (view.getId()) {
            case R.id.btn_start_recording:
                if(IS_RECORDING){
                    IS_RECORDING = false;
                    mBtnStartRecording.setImageResource(R.drawable.rsz_car_white);
                    stopRecording();
                } else {
                    IS_RECORDING = true;
                    mBtnStartRecording.setImageResource(R.drawable.rsz_car_red);
                    startRecording();
                }
                break;
            case R.id.btn_bluetooth:
                intent = new Intent(con, BluetoothActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_map:
                intent = new Intent(con, DataActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_load:
                intent = new Intent(con, DataActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_dashboard:
                intent = new Intent(con, LiveDataActivity.class);
                startActivity(intent);
                break;
        }
    }


//    public void switchButton(){
//
//        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
//
//                if(isChecked){
//                    IS_RECORDING = true;
//                    startRecording();
//                    // flush data file
//                    InternalStorage.clearFile(getApplicationContext());
//
//                    SharedPreferences.Editor editor = getSharedPreferences("com.example.xyz", MODE_PRIVATE).edit();
//                    editor.commit();
//                    editor.putBoolean("NameOfThingToSave", true);
//                    switchStatus.setText("SpeedRecording is currently ON");
//
//                }else{
//                    IS_RECORDING = false;
//                    stopRecording();
//                    SharedPreferences.Editor editor = getSharedPreferences("com.example.xyz", MODE_PRIVATE).edit();
//                    editor.commit();
//                    editor.putBoolean("NameOfThingToSavea", false);
//                    switchStatus.setText("SpeedRecording is currently OFF");
//                }
//
//            }
//        });
//    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selected_text = spinnerPid2.getSelectedItem().toString();
        setPid(selected_text);
        mDataController.setPid(pid);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        setPid("");
    }

    public void setPid(String pid) {
        switch (pid) {
            case "Velocity":
                this.pid = "412";
                break;
            case "Watt":
                this.pid = "346";
                break;
        }
    }
}
