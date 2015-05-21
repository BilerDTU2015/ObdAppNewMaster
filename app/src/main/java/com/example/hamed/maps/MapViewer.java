package com.example.hamed.maps;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.hamed.obdappnewmaster.R;
import com.example.hamed.storage.InternalStorage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapViewer extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ArrayList<Position> positions;

    // Map data formatted as specified in InternalStorage.load();
    private final static String INTENT_MESSAGE = "map_data";
    private final static String MESSAGE = "com.example.hamed.maps.DATA";

    private int totalSpeed;
    private int minSpeed = 99999;
    private int maxSpeed = 0;
    private int colorRange = 10;
    private int colorTock = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        loadMapData();
        setUpMapIfNeeded();
        drawLines(positions);
        //final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        //String message = settings.getString(INTENT_MESSAGE, null);
        // mapData = message;
        //Log.d("intent message ", message);
    }

    /* Method to decode polyline points */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
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
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(DUBLIN).title("Marker"));
        //Location location = mMap.getMyLocation();
        //LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (positions != null){
            LatLng startPosition = positions.get(0).getLatLng();
            mMap.addMarker(new MarkerOptions().position(startPosition).title("Start Point"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPosition, 20));
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Show Zoom buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void loadMapData() {
        positions = InternalStorage.loadData(getApplicationContext());
        Log.d("MapViewer", "length  " + String.valueOf(positions.size()));
    }

    private void drawLines(ArrayList<Position> positions) {
        Log.d("MapViewer", "Drawing lines");

        for (Position pos : positions) {
            int currentSpeed = pos.getSpeed();
            totalSpeed += currentSpeed;

            if (currentSpeed > maxSpeed) {
                maxSpeed = currentSpeed;
            }
            if (currentSpeed < minSpeed) {
                minSpeed = currentSpeed;
            }
        }
        colorTock = (maxSpeed - minSpeed)/ colorRange;


        if (positions.size() >= 2) {
            for (int i = 0; i < positions.size() - 1; i++) {
                Position point1 = positions.get(i);
                Position point2 = positions.get(i + 1);
                drawPolyLine(point1, point2);
            }
        }
    }

    private void drawPolyLine(Position start, Position end) {
        int meanSpeed = (end.getSpeed() + start.getSpeed()) / 2;
        // tock
        int colorNumber = (meanSpeed-minSpeed) / colorTock;
        Log.d("MapViewer", "choosing color " + String.valueOf(colorNumber));
        int color = getColor(colorNumber);

        Polyline line1 = mMap.addPolyline(new PolylineOptions()
                .add(start.getLatLng(), end.getLatLng())
                .width(6)
                .color(color));
    }

    private int getColor(int colorNumber) {
        int color;
        switch (colorNumber) {
            default:
            case 1:
                color = getResources().getColor(R.color.a);
                break;
            case 2:
                color = getResources().getColor(R.color.b);
                break;
            case 3:
                color = getResources().getColor(R.color.c);
                break;
            case 4:
                color = getResources().getColor(R.color.d);
                break;
            case 5:
                color = getResources().getColor(R.color.e);
                break;
            case 6:
                color = getResources().getColor(R.color.f);
                break;
            case 7:
                color = getResources().getColor(R.color.g);
                break;
            case 8:
                color = getResources().getColor(R.color.h);
                break;
            case 9:
                color = getResources().getColor(R.color.i);
                break;
            case 10:
                color = getResources().getColor(R.color.j);
                break;
        }
        return color;
    }

}

