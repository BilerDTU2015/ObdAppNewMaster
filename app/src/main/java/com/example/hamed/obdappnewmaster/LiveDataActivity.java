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


public class LiveDataActivity extends Activity implements OnClickListener, DownloadResultReceiver.Receiver, OnItemSelectedListener {

    private Button stopBtn;
    private Button getDataBtn;
    private Spinner spinnerPid;

    private String selected_text;
    private String pid;
    private DownloadResultReceiver mReceiver;
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
                sendToService(ServiceTest.STOP);
                break;
            case R.id.getDataBtn:
                sendToService(ServiceTest.SEND_COMMAND);
                break;
        }
    }

    public void sendToService(int requestId) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, ServiceTest.class);

        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        /* Send optional extras to Download IntentService */
        intent.putExtra("receiver", this.mReceiver);
        switch (requestId){
            case ServiceTest.STOP:
                intent.putExtra("requestId", requestId);
                break;
            case ServiceTest.SEND_COMMAND:
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
        switch (resultCode) {
            case ServiceTest.STATUS_SENDING:
                String result = resultData.getString("result");
                data = result;
                Log.i("RECIVED", selected_text + " : " + result);
                updateReadData();
                break;
            case ServiceTest.STATUS_SENDING_ARRAY:
                String[] results = resultData.getStringArray("result");
                data = results[0];
                Log.i("RECIVED", selected_text + " : " + results[0] + "Total km : " + results[1]);
                updateReadData();
                break;
            case ServiceTest.STATUS_ERROR:
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
        }
    }
}
