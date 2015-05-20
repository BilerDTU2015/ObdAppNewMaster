package com.example.hamed.obdappnewmaster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    // Stuff from polyline tutorial
    ProgressDialog pDialog;
    List<LatLng> polyz;
    JSONArray array;
    static final LatLng DUBLIN = new LatLng(53.344103999999990000, -6.267493699999932000);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_maps);
       setUpMapIfNeeded();

       // from tutorial
       // mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

//        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
//                    .getMap();
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DUBLIN, 15));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
//        new GetDirection().execute();

    }

     class GetDirection extends AsyncTask<String, String, String> {

         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             pDialog = new ProgressDialog(MapsActivity.this);
             pDialog.setMessage("Loading route. Please wait...");
             pDialog.setIndeterminate(false);
             pDialog.setCancelable(false);
             pDialog.show();
         }

         protected String doInBackground(String... args) {
//             Intent i = getIntent();
//             String startLocation = i.getStringExtra("startLoc");
//             String endLocation = i.getStringExtra("endLoc");
//             startLocation = startLocation.replace(" ", "+");
//             endLocation = endLocation.replace(" ", "+");
             String startLocation = "Redfern Avenue, Portmarnock, Co. Dublin, Ireland";
             String endLocation = "Limetree Avenue, Portmarnock, Co. Dublin, Ireland";

             String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=" + startLocation + ",+dublin&destination=" + endLocation + ",+dublin&sensor=false";
             StringBuilder response = new StringBuilder();
             try {
                 URL url = new URL(stringUrl);
                 HttpURLConnection httpconn = (HttpURLConnection) url
                         .openConnection();
                 if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                     BufferedReader input = new BufferedReader(
                             new InputStreamReader(httpconn.getInputStream()),
                             8192);
                     String strLine = null;

                     while ((strLine = input.readLine()) != null) {
                         response.append(strLine);
                     }
                     input.close();
                 }

                 String jsonOutput = response.toString();

                 JSONObject jsonObject = new JSONObject(jsonOutput);

                 // routesArray contains ALL routes
                 JSONArray routesArray = jsonObject.getJSONArray("routes");
                 // Grab the first route
                 JSONObject route = routesArray.getJSONObject(0);

                 JSONObject poly = route.getJSONObject("overview_polyline");
                 String polyline = poly.getString("points");
                 polyz = decodePoly(polyline);

             } catch (Exception e) {

             }

             return null;

         }

         // had String file_url as param
         protected void onPostExecute(String file_url) {
            // String file_url = "//assets/direction.json";
             for (int i = 0; i < polyz.size() - 1; i++) {
                 LatLng src = polyz.get(i);
                 LatLng dest = polyz.get(i + 1);
                 Polyline line = mMap.addPolyline(new PolylineOptions()
                         .add(new LatLng(src.latitude, src.longitude),
                                 new LatLng(dest.latitude, dest.longitude))
                         .width(2).color(Color.RED).geodesic(true));

             }
             pDialog.dismiss();

         }
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
        //setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
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


                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        LatLng currentLocation = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                        MarkerOptions marker = new MarkerOptions().position(currentLocation);

                        mMap.addMarker(marker.title("It's Me!"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
                    }
                });

            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(DUBLIN).title("Marker"));

    }
}
