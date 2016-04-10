package com.jagwarrx.coolmap;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    LocationManager mLocationManager;

    String categ;
    //GPSTracker gps;
    private GoogleMap mMap;
    ArrayList<String> namelist;
    ArrayList<Double> latlist,longlist;
    String BASE_URL="https://spider.nitt.edu/lateral/appdev/coordinates?category=";
    double lat;
    double longi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        namelist=new ArrayList<>();
        latlist=new ArrayList<>();
        longlist=new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        categ=getIntent().getStringExtra("category");
        mLocationManager= (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,this);
        Location location=mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location!=null){
            onLocationChanged(location);
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        lat=location.getLatitude();
        longi=location.getLongitude();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class AsyncGetdata2 extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... params) {
            String result = "";
            try {
                String link = BASE_URL+params[0];
                URL URL=new URL(link);
                HttpURLConnection urlConnection= (HttpURLConnection) URL.openConnection();

                InputStream inputStream=urlConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    Log.d("line", line);
                    result+=line;
                }



                JSONArray array1=new JSONArray(result);

                for(int i=0;i<array1.length();i++) {
                    JSONObject object = array1.getJSONObject(i);
                    namelist.add(object.getString("name"));
                    latlist.add(object.getDouble("latitude"));
                    longlist.add(object.getDouble("longitude"));
                }
                Log.d("DATA", result);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }





            @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mMap.clear();
            for(int i=0;i<namelist.size();i++){
                LatLng sydney = new LatLng(latlist.get(i), longlist.get(i));
                mMap.addMarker(new MarkerOptions().position(sydney).title(namelist.get(i)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            }
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                LatLng currLoc = new LatLng(lat, longi);
                mMap.addMarker(new MarkerOptions().position(currLoc).title("You're Here!")).showInfoWindow();
        }
    }


    public void onSearch(View view) {
        boolean flag = false;
        EditText location = (EditText) findViewById(R.id.searchBar);
        String loc = location.getText().toString();
        if (loc != null || !loc.equals("")) {
            for(int i=0;i<namelist.size();i++){
                if( loc.equalsIgnoreCase( namelist.get(i))) {
                    flag = true;
                    LatLng sydney = new LatLng(latlist.get(i), longlist.get(i));
                    mMap.addMarker(new MarkerOptions().position(sydney).title(namelist.get(i))).showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                }
            }

            if(flag==false)
                Toast.makeText(MapsActivity.this, "Sorry, " + loc + " not found in "+ categ+ "!", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new AsyncGetdata2().execute(categ);

        /*/gps = new GPSTracker(MapsActivity.this);
        // check if GPS enabled
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("I'm here!")); /*/
        }

        /*// Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }


