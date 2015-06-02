package com.example.hamed.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.example.hamed.Service.LocationService;
import com.example.hamed.Service.MyResultReceiver;
import com.example.hamed.Service.NetworkService;
import com.example.hamed.obdappnewmaster.LiveDataActivity;
import com.example.hamed.obdappnewmaster.R;
import com.example.hamed.storage.InternalStorage;
import com.example.hamed.storage.Position;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by user on 5/27/15.
 */
public class DataController extends BroadcastReceiver implements MyResultReceiver.Receiver {

    // Debug tag
    private final static String TAG = "DataController";

    private Context mContext;

    boolean recordingStatus = false;
    private final static String SEND_LOGGING = "START_LOGGING";
    private final static String RECEIVE_RESULT = "com.example.hamed.service.RECEIVE_LOCATION";

    private MyResultReceiver obdReceiver;
    private String pid;

    private String currentSpeed;


    public DataController(Context con){
        this.mContext = con;
        // Register this class as receiver programatically instead of in manifest
        mContext.registerReceiver(this, new IntentFilter(RECEIVE_RESULT));

    }

    public void startObdAndLocationLogging(){
        InternalStorage.clearFile(mContext);
        startLocationService();
      //  startObdLogging();
    }

    /**
     * Method to start obd logging currently just speed
     */
    public void startObdLogging(){
        obdReceiver = new MyResultReceiver(new Handler());
        obdReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, mContext, NetworkService.class);
        intent.putExtra("receiver", this.obdReceiver);
        intent.putExtra("requestId", NetworkService.SEND_COMMAND);
        intent.putExtra("pid", pid); // 412 = speed
        mContext.startService(intent);
    }

    public void setPid(String pid){
        this.pid = pid;
    }
    public void startLocationService() {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, mContext, LocationService.class);

        createNotification(true);

        intent.putExtra(SEND_LOGGING, recordingStatus);
        mContext.startService(intent);
    }


    private void createNotification(boolean serviceActive) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(mContext)
                .setContentTitle("Location service toggled")
                .setContentText("status : " + String.valueOf(serviceActive))
                .setSmallIcon(R.drawable.ic_action_city_car_512)
              //  .setContentIntent(pIntent)
                .setAutoCancel(true).build();
//                .addAction(R.drawable.icon, "Call", pIntent)
//                .addAction(R.drawable.icon, "More", pIntent)
//                .addAction(R.drawable.icon, "And more", pIntent).build();

        notificationManager.notify(0, n);
    }

    /**
     *  Method that stops the location service and obd service
     */
    public void stopRecording(){
        recordingStatus = false;

        // Stop location service
        Intent stopIntent = new Intent(Intent.ACTION_SYNC, null, mContext, LocationService.class);
        stopIntent.setAction(LocationService.STOP_SERVICE);
        mContext.stopService(stopIntent);

        // Stop network service
        obdReceiver = new MyResultReceiver(new Handler());
        obdReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, mContext, NetworkService.class);
        intent.putExtra("receiver", this.obdReceiver);
        intent.putExtra("requestId", NetworkService.STOP);
        mContext.startService(intent);
    }

    /**
     *
     * @param context
     * @param intent with coordinates and other data as strings
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received intent");
        Double lat = intent.getDoubleExtra("Latitude", 0);
        Double lng = intent.getDoubleExtra("Longitude", 0);

        int dummySpeed = 666;

        Position pos;
        LatLng latLng = new LatLng(lat, lng);
        if (lat != null & lng != null ) {
            if (currentSpeed != null) {
                pos = new Position(latLng, Integer.valueOf(currentSpeed));
            } else {
                pos = new Position(latLng, dummySpeed);
            }
            InternalStorage.appendToFile(pos, mContext);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
//        Log.d(TAG, "SPEED RECEIVED");
        currentSpeed = resultData.getString("result_1");
//        Log.d(TAG, currentSpeed);
    }
}

