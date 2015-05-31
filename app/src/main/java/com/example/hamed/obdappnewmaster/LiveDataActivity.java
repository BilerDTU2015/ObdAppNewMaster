package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamed.Service.MyResultReceiver;
import com.example.hamed.Service.NetworkService;


public class  LiveDataActivity extends Activity implements OnClickListener, MyResultReceiver.Receiver, OnItemSelectedListener {

    private Button stopBtn;
    private Button getDataBtn;
    private Spinner spinnerPid;

    private String selected_text;
    private String pid;
    private MyResultReceiver mReceiver;
    private TextView textViewOutput;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);

        textViewOutput = (TextView) findViewById(R.id.textViewOutput);

        stopBtn = (Button)findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(this);

        getDataBtn = (Button)findViewById(R.id.getDataBtn);
        getDataBtn.setOnClickListener(this);

        spinnerPid = (Spinner)findViewById(R.id.spinnerPid);
        spinnerPid.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stopBtn:
                sendToService(NetworkService.STOP);
                break;
            case R.id.getDataBtn:
                sendToService(NetworkService.SEND_COMMAND);
                break;
        }
    }

    public void sendToService(int requestId) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NetworkService.class);

        mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        /* Send optional extras to Download IntentService */
        intent.putExtra("receiver", this.mReceiver);
        switch (requestId){
            case NetworkService.STOP:
                intent.putExtra("requestId", requestId);
                break;
            case NetworkService.SEND_COMMAND:
                intent.putExtra("requestId", requestId);
                intent.putExtra("pid", this.pid);
                break;
        }
        startService(intent);
    }

    // Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();
    // Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };

    protected void updateReadData() {
        // Fire off a thread to do some work that we shouldn't do directly in the UI thread
        Thread t = new Thread() {
            public void run() {
                mHandler.post(mUpdateResults);
            }
        };
        t.start();
    }

    private void updateResultsInUi() {
        textViewOutput.setText(this.data);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        String result = "";
        switch (resultCode) {
            case NetworkService.STATUS_SENDING:
                result = resultData.getString("result_1");
                data = result;
                Log.i("RECIVED", selected_text + " : " + result);
                updateReadData();
                break;
            case NetworkService.STATUS_SENDING_MULTI_DATA:
                int data_size = resultData.getInt("data_size");
                for (int i = 0; i <data_size; i++) {
                    result = result + resultData.getString("result_" + (i + 1)) + "     ";
                }
                data = result;
                Log.i("RECIVED", selected_text + " : " + result);
                updateReadData();
                break;
            case NetworkService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selected_text = spinnerPid.getSelectedItem().toString();
        setPid(selected_text);
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
            case "Power":
                this.pid = "374";
                break;
            case "Watt":
                this.pid = "346";
                break;
            case "Vin":
                this.pid = "6FA";
                break;
            case "Charging":
                this.pid = "389";
                break;
            case "Quick Charging":
                this.pid = "696";
                break;
            case "Gear Shift Position":
                this.pid = "418";
                break;
            case "Air Condition":
                this.pid = "3A4";
                break;
            case "Light Status":
                this.pid = "424";
                break;
            case "Break Lamp":
                this.pid = "231";
                break;
            case "All":
                this.pid = "";
                break;
        }
    }
}
