package com.jagwarrx.coolmap;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String BASE_URL="https://spider.nitt.edu/lateral/appdev";

    ListView firstlistview;
    ArrayList<String> listitems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listitems=new ArrayList<>();
        firstlistview= (ListView) findViewById(R.id.listView);
        new AsyncGetdata().execute("google.com", "maps");
    }
    class AsyncGetdata extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            String result = "";
            try {
                String link = BASE_URL;
                URL URL=new URL(link);
                HttpURLConnection urlConnection= (HttpURLConnection) URL.openConnection();

                InputStream inputStream=urlConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    Log.d("line",line);
                    result+=line;
                }

                JSONObject object=new JSONObject(result);
                JSONArray array=object.getJSONArray("categories");



                for(int i=0;i<array.length();i++){
                    listitems.add((String) array.get(i));
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
            ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.whitetext,R.id.list_content,listitems);
            firstlistview.setAdapter(adapter);
            firstlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                    intent.putExtra("category",listitems.get(position));
                    startActivity(intent);
                }
            });
        }

        //R.layout.list_black_text,
    }
}

