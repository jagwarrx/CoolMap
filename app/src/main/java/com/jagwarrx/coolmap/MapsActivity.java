package com.jagwarrx.coolmap;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    String categ;
    private GoogleMap mMap;
    ArrayList<String> namelist;
    ArrayList<Double> latlist,longlist;
    String BASE_URL="https://spider.nitt.edu/lateral/appdev/coordinates?category=";
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
            for(int i=0;i<namelist.size();i++){
                LatLng sydney = new LatLng(latlist.get(i), longlist.get(i));
                mMap.addMarker(new MarkerOptions().position(sydney).title(namelist.get(i)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            }
            mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
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

        /*// Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
}
