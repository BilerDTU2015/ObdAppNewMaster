package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hamed.maps.Position;
import com.example.hamed.storage.InternalStorage;

public class DataActivity extends Activity implements View.OnClickListener {

    private TextView text;
    private Button mBtnLoadData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_activity);

        text = (TextView) findViewById(R.id.txt_view_data);

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
        String data = InternalStorage.loadData("current_recording.txt", getApplicationContext());
        Log.d("data", data);
        Log.d("data len ", String.valueOf(data.length()));
        //text.setText(data);
        text.append(data);
    }
}
