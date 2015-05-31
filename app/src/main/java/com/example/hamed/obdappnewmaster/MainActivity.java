package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamed.controller.DataController;


public class MainActivity extends Activity implements View.OnClickListener {

    private TextView mSwitchStatus;
    private Switch mSwitch;
    private ImageButton mBtnBluetooth;
    private ImageButton mBtnMap;
    private ImageButton mBtnDashboard;
    private ImageButton mBtnLoadData;

    private DataController mDataController;

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

        mBtnLoadData = (ImageButton)findViewById(R.id.btn_load_data);
        mBtnLoadData.setOnClickListener(this);

    }

    /**
     * Method to start data logging (saves to file)
     * The file is emptied every time you toggle (ERASES PREVIOUS DATA)
     */
    public void startRecording(){
        mDataController = new DataController(getApplicationContext());
        mDataController.startLocationService();
    }

    /**
     * Method to stop data logging
     */
    public void stopRecording(){
        if (mDataController != null)
            mDataController.stopRecording();
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
                intent = new Intent(con, MapsActivity.class);
                break;
            case R.id.btn_load_data:
                intent = new Intent(con, DataActivity.class);
                break;
        }
        startActivity(intent);
    }
}
