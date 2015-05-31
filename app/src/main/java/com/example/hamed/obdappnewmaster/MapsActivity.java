package com.example.hamed.obdappnewmaster;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.hamed.storage.Position;
import com.example.hamed.storage.InternalStorage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    // Stuff from polyline tutorial
    ProgressDialog pDialog;
    List<LatLng> polyz;
    JSONArray array;
    static final LatLng DUBLIN = new LatLng(53.344103999999990000, -6.267493699999932000);

    InternalStorage storage = new InternalStorage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_maps);
       setUpMapIfNeeded();
    }

    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


     /* call {@link #setUpMap()} once when {@link #} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                // clear the data file so we don't append dat to old file
                try {
                    InternalStorage.save("", "current_recording.txt", getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        LatLng currentLocation = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                        MarkerOptions marker = new MarkerOptions().position(currentLocation);

                        // Record location
                        Position pos = new Position(currentLocation, generateRandomSpeed());
                        String locationString = pos.toString();
                        try {
                            //InternalStorage.save(locationString, "current_recording.txt", getApplicationContext());
                            InternalStorage.appendToFile(pos, getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mMap.addMarker(marker.title("It's Me!"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
                    }
                });

            }
        }
    }

    /**
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(DUBLIN).title("Marker"));
        Location location = mMap.getMyLocation();
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Place dot on current location
        mMap.setMyLocationEnabled(true);

        // Turns traffic layer on
        mMap.setTrafficEnabled(true);

        // Enables indoor maps
        mMap.setIndoorEnabled(true);

        // Turns on 3D buildings
        mMap.setBuildingsEnabled(true);

        // Show Zoom buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Create a marker in the map at a given position with a title
        Marker marker = mMap.addMarker(new MarkerOptions().
                position(currentLocation).title("Hello i'm here"));
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
