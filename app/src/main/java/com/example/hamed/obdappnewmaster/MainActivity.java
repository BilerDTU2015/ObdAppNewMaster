package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener {

    private ImageButton mBtnBluetooth;
    private ImageButton mBtnMap;
    private ImageButton mBtnDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnBluetooth = (ImageButton)findViewById(R.id.btn_bluetooth);
        mBtnBluetooth.setOnClickListener(this);

        mBtnMap = (ImageButton)findViewById(R.id.btn_map);
        mBtnMap.setOnClickListener(this);

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
        }
        startActivity(intent);
    }
}
