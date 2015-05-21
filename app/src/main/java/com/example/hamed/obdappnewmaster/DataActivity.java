package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hamed.maps.MapViewer;
import com.example.hamed.maps.Position;
import com.example.hamed.storage.InternalStorage;

public class DataActivity extends Activity implements View.OnClickListener {

    private TextView text;
    private Button mBtnShowMap;
    private Button mBtnLoadData;

    private final static String INTENT_MESSAGE = "map_data";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_activity);

        text = (TextView) findViewById(R.id.txt_view_data);

        mBtnShowMap = (Button)findViewById(R.id.btn_load_map);
        mBtnShowMap.setOnClickListener(this);
        mBtnLoadData = (Button)findViewById(R.id.btn_load);
        mBtnLoadData.setOnClickListener(this);

    }


    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //unregisterReceiver(bReceiver);

        }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load:
            String data = InternalStorage.loadData("current_recording.txt", getApplicationContext());
            Log.d("data", data);
            Log.d("data len ", String.valueOf(data.length()));
            //text.setText(data);
            text.append(data);
                break;
            case R.id.btn_load_map:
                startMapActivity();
                break;

        }
    }

    private void startMapActivity(){
        //String data = InternalStorage.loadData("current_recording.txt", getApplicationContext());
        String data = "hello world";

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        //settings.edit().putString(INTENT_MESSAGE, data).commit();

        MapViewer mapViewer = new MapViewer();
        Intent intent = new Intent(DataActivity.this, MapViewer.class);
        intent.putExtra(INTENT_MESSAGE, data);
        startActivity(intent);
    }
}
