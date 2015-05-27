package com.example.hamed.maps;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.example.hamed.obdappnewmaster.R;
import com.example.hamed.storage.InternalStorage;
import com.example.hamed.storage.Position;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Random;


/**
 * Created by Niclas on 5/20/15.
 */
public class LocationRecorder extends FragmentActivity {

    private GoogleMap mMap;
    private boolean RECORDING = false;
    private Context context;

    public LocationRecorder(Context context, GoogleMap map){
        this.context = context;
        this.mMap = map;
    }

    // Change the recording status, returns true if status is changed, false if already recording
    // or already off
    public boolean setRecording(boolean status){
        if (status != RECORDING){
            RECORDING = status;

            if (RECORDING){
                startRecording();
            } else {
                stopRecording();
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean getStatus(){
        return RECORDING;
    }

    public GoogleMap getMap(){
        setUpMapIfNeeded();
        return mMap;
    }

   private void startRecording(){
       setUpMapIfNeeded();
   }

   private void stopRecording(){

   }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            mMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {


                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        LatLng currentLocation = new LatLng(arg0.getLatitude(), arg0.getLongitude());

                        Position position = new Position(currentLocation, generateRandomSpeed());
                        InternalStorage.appendToFile(position, context);
                    }
                });

            }
        }
    }

    // Generates a random dummy speed value
    private int generateRandomSpeed(){
        int speed;
        int min = 40;
        int max = 60;

        Random random = new Random();
        speed = random.nextInt(max - min + 1) + min;

        return speed;
    }

}
