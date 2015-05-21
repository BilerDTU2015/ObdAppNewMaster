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
    private ImageButton mBtnSupport;
    private ImageButton mBtnTemperature;
    private ImageButton mBtnSpeed;
    private ImageButton mBtnFuel;
    private ImageButton mBtnData;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnBluetooth = (ImageButton)findViewById(R.id.btn_bluetooth);
        mBtnBluetooth.setOnClickListener(this);

        mBtnMap = (ImageButton)findViewById(R.id.btn_map);
        mBtnMap.setOnClickListener(this);

        mBtnFuel = (ImageButton)findViewById(R.id.btn_fuel);
        mBtnFuel.setOnClickListener(this);

        mBtnSupport = (ImageButton)findViewById(R.id.btn_support);
        mBtnSupport.setOnClickListener(this);

        mBtnTemperature = (ImageButton)findViewById(R.id.btn_temp);
        mBtnTemperature.setOnClickListener(this);

        mBtnSpeed = (ImageButton)findViewById(R.id.btn_speed);
        mBtnSpeed.setOnClickListener(this);

        mBtnData = (ImageButton)findViewById(R.id.btn_load_data);
        mBtnData.setOnClickListener(this);

        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            case R.id.btn_fuel:
                intent = new Intent(con, CarPower.class);
                break;
            case R.id.btn_support:
                intent = new Intent(con, SupportActivity.class);
                break;
            case R.id.btn_temp:
                intent = new Intent(con, TempActivity.class);
                break;
            case R.id.btn_load_data:
               intent = new Intent(con, DataActivity.class);
               break;
        }
        startActivity(intent);
    }
}
