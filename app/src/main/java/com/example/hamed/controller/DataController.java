package com.example.hamed.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.hamed.Service.LocationService;
import com.example.hamed.obdappnewmaster.R;
import com.example.hamed.storage.InternalStorage;
import com.example.hamed.storage.Position;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by user on 5/27/15.
 */
public class DataController extends BroadcastReceiver {

    // Debug tag
    private final static String TAG = "DataController";

    private Context mContext;

    boolean recordingStatus = false;
    private final static String SEND_LOGGING = "START_LOGGING";
    private final static String RECEIVE_RESULT = "com.example.hamed.service.RECEIVE_LOCATION";

    public DataController(Context con){
        this.mContext = con;
        // Register this class as receiver programatically instead of in manifest
        mContext.registerReceiver(this, new IntentFilter(RECEIVE_RESULT));

        // Clear data file
        InternalStorage.clearFile(mContext);

        recordingStatus = true;
        //startLocationService();
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
     *  Method that stops the location service
     */
    public void stopRecording(){
        recordingStatus = false;

        Intent stopIntent = new Intent(Intent.ACTION_SYNC, null, mContext, LocationService.class);
        stopIntent.setAction(LocationService.STOP_SERVICE);
        mContext.stopService(stopIntent);
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

        int dummySpeed = 45;

        LatLng latLng = new LatLng(lat, lng);
        if (lat != null & lng != null ) {
            Position pos = new Position(latLng, dummySpeed);
            InternalStorage.appendToFile(pos, mContext);
        }
    }
}
