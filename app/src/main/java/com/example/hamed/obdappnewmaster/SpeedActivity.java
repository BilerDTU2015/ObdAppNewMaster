package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Hamed on 18-May-15.
 */
public class SpeedActivity extends Activity {

    private TextView switchStatus;
    private Switch mySwitch;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed);

        switchStatus = (TextView) findViewById(R.id.switchStatus);
        mySwitch = (Switch) findViewById(R.id.mySwitch);

        //set the switch to ON
        mySwitch.setChecked(true);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    switchStatus.setText("SpeedRecording is currently ON");
                }else{
                    switchStatus.setText("SpeedRecording is currently OFF");
                }

            }
        });

        //check the current state before we display the screen
        if(mySwitch.isChecked()){
            switchStatus.setText("SpeedRecording is currently ON");
        }
        else {
            switchStatus.setText("SpeedRecording is currently OFF");
        }
    }



    private BluetoothSocket mBtSocket;

    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //unregisterReceiver(bReceiver);
        if (mBtSocket == null) {

        }
    }
}
