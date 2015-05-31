package com.example.hamed.obdappnewmaster;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hamed.controller.DataController;
import com.example.hamed.maps.MapViewer;
import com.example.hamed.storage.Position;
import com.example.hamed.storage.InternalStorage;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class DataActivity extends FragmentActivity implements View.OnClickListener {

    DataController mDataController;

    private static final String TAG = "DataActivity";

    private ProgressDialog progress;
    private int mProgressStatus = 0;
    private boolean isLoading = false;

    private TextView text;
    private Button mBtnShowMap;
    private Button mBtnLoadData;
    public ArrayList<Position> loadedPositions;

    private GoogleMap mMap;

    private int minSpeed = 99999;
    private int maxSpeed = 0;
    private int colorRange = 10;
    private int colorTock = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_activity);

        text = (TextView) findViewById(R.id.txt_view_data);

        mBtnShowMap = (Button)findViewById(R.id.btn_load_map);
        mBtnShowMap.setOnClickListener(this);
        mBtnLoadData = (Button)findViewById(R.id.btn_load);
        mBtnLoadData.setOnClickListener(this);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

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
            ArrayList<Position> loadedPositions = InternalStorage.loadData(getApplicationContext());
            //text.setText(data);
                for (Position pos : loadedPositions){
                    text.append(pos.toString());
                }
                break;
            case R.id.btn_load_map:
                isLoading = true;
                loadedPositions = new ArrayList<>();

                progress = new ProgressDialog(DataActivity.this, R.style.MyTheme);
//               progress = (ProgressBar) findViewById(R.id.progressBar);

//                progress.setProgress(0);
                progress.setCancelable(false);
//                progress.setMessage("Loading location data");
                progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//                progress.setIndeterminate(false);
//                progress.setProgress(0);
//                progress.setMax(100);
//                progress = ProgressDialog.show(this, "", "Please wait... Loading List...");
//                 progress.setVisibility(View.VISIBLE);
              //   new ProgressTask().execute();
//                progress.setMax(100);
//                progress.setIndeterminate(false);
//                progress.setVisibility(View.VISIBLE);
                progress.show();
                // Start lengthy operation in a background thread

                new Thread(new Runnable() {
                    public void run() {
                        progress.show();
                        ArrayList<Position> moarPositions = InternalStorage.loadData(getApplicationContext());
                        setPositions(moarPositions);
                        progress.dismiss();
                        isLoading = false;
                       setUpMapIfNeeded();
                       // drawLines(moarPositions);
                    }
                }).start();

                // Wait for thread to stop loading from file
//            while(isLoading){
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

            if (loadedPositions.size() > 0) {
                drawLines(loadedPositions);
            }
             //  startMapActivity();
             //   setUpMapIfNeeded();

                break;
        }
    }

    public void setPositions(ArrayList<Position> positions){
        loadedPositions = positions;
    }

//    private class ProgressTask extends AsyncTask <Void,Void,Void>{
//        int jumpTime = 0;
//        int totalProgressTime = 100;
//
//        @Override
//        protected void onPreExecute(){
//            progress.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected Void doInBackground(Void... arg0) {
//            while (jumpTime < totalProgressTime)
//            try {
//                Thread.sleep(200);
//                jumpTime += 5;
//                progress.setProgress(jumpTime);
//            }
//            catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            progress.setVisibility(View.GONE);
//        }
//    }

    private void startMapActivity(){
        ArrayList<Position> loadedPositions = InternalStorage.loadData(getApplicationContext());
        String dummy = "wat";

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        //settings.edit().putString(INTENT_MESSAGE, data).commit();

        MapViewer mapViewer = new MapViewer();
        Intent intent = new Intent(DataActivity.this, MapViewer.class);
        //intent.putExtra(MESSAGE, loadedPositions); // dont send map data as intent but load it in mapViewer
        startActivity(intent);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();

//            GoogleMap mMap = getMapFragment().getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
    /**
     *
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(DUBLIN).title("Marker"));
        //Location location = mMap.getMyLocation();
        //LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (loadedPositions != null){
            Log.d(TAG, "positions not null");
            LatLng startPosition = loadedPositions.get(0).getLatLng();
            mMap.addMarker(new MarkerOptions().position(startPosition).title("Start Point"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPosition, 20));
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Show Zoom buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void drawLines(ArrayList<Position> positions) {
        Log.d("MapViewer", "Drawing lines");

        for (Position pos : positions) {
            int currentSpeed = pos.getSpeed();

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
     //   int colorNumber = (meanSpeed-minSpeed) / colorTock;
        int color = getResources().getColor(R.color.a);
     //   Log.d("MapViewer", "choosing color " + String.valueOf(colorNumber));
     //   int color = getColor(colorNumber);

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

    class MyTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {

                    try {
                        loadedPositions = InternalStorage.loadData(getApplicationContext());
                    } catch (Exception e) {
                        Log.e("ASYNC", "Failed reading");
                        e.printStackTrace();
                    }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
           setUpMapIfNeeded();
           // drawLines(loadedPositions);
        }
    }
}
