package com.example.hamed.obdappnewmaster;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

public class CarPower extends Activity implements OnClickListener, DownloadResultReceiver.Receiver {

    private DownloadResultReceiver mReceiver;
    private String pid = "";
    private Button stopBtn;
    private Button powerBtn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuel_el);

        stopBtn = (Button)findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(this);

        powerBtn = (Button)findViewById(R.id.powerBtn);
        powerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stopBtn:
                sendToService(ServiceTest.STOP);
                break;
            case R.id.powerBtn:
                this.pid = "374";
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

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
