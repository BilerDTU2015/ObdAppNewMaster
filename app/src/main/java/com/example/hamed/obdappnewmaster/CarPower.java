package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

public class CarPower extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuel_el);
    }


    private  BluetoothSocket mBtSocket;

    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //unregisterReceiver(bReceiver);
        if (mBtSocket == null) {

        }
    }

}
