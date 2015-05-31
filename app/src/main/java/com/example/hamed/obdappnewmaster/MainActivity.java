package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;


public class MainActivity extends Activity implements View.OnClickListener {

    private ImageButton mBtnBluetooth;
    private ImageButton mBtnMap;
    private ImageButton mBtnDashboard;
    private TextView switchStatus;
    private Switch mySwitch;
    private GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnBluetooth = (ImageButton)findViewById(R.id.btn_bluetooth);
        mBtnBluetooth.setOnClickListener(this);

        mBtnMap = (ImageButton)findViewById(R.id.btn_map);
        mBtnMap.setOnClickListener(this);


        mBtnDashboard = (ImageButton)findViewById(R.id.btn_dashboard);
        mBtnDashboard.setOnClickListener(this);

        switchStatus = (TextView) findViewById(R.id.switchStatus);
        mySwitch = (Switch) findViewById(R.id.mySwitch);
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
                intent = new Intent(con, MapsActivity.class);
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
                    SharedPreferences.Editor editor = getSharedPreferences("com.example.xyz", MODE_PRIVATE).edit();
                    editor.commit();
                    editor.putBoolean("NameOfThingToSave", true);
                    switchStatus.setText("SpeedRecording is currently ON");

                }else{

                    SharedPreferences.Editor editor = getSharedPreferences("com.example.xyz", MODE_PRIVATE).edit();
                    editor.commit();
                    editor.putBoolean("NameOfThingToSavea", false);
                    switchStatus.setText("SpeedRecording is currently OFF");
                }

            }
        });

        if(mySwitch.isChecked()){
            SharedPreferences.Editor editor = getSharedPreferences("com.example.xyz", MODE_PRIVATE).edit();
            editor.commit();
            switchStatus.setText("SpeedRecording is currently ON");
        }
        else {
            SharedPreferences.Editor editor = getSharedPreferences("com.example.xyz", MODE_PRIVATE).edit();
            editor.commit();
            switchStatus.setText("SpeedRecording is currently OFF");
        }



    }


}
