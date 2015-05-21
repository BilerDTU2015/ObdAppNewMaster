package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

/**
 * Created by Hamed on 18-May-15.
 */
public class SupportActivity extends Activity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support);
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
