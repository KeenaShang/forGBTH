package com.example.turtlegang.gbthvolunteertracking;


import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;



public class ExportData extends AppCompatActivity {

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export);

        queue = Volley.newRequestQueue(this);
    }

    public void export(View view){


        // get locations and statuses of other active devices
        String url = "https://gbthtracking.herokuapp.com/api/locations";

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        write_to_file(response);
                        System.out.println("aaaaa");
                        System.out.println(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    private void write_to_file(JSONArray response) {
        //write data
        StringBuilder data = new StringBuilder();
        data.append("device, latitude, longitude, timestamp");
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject location = response.getJSONObject(i);
                String device = location.getString("user_id");
                double latitude = location.getDouble("latitude");
                double longitude = location.getDouble("longitude");
                long time = location.getLong("time");

                data.append("\n").append(device + "," + latitude + "," + longitude + "," + df.format(time));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try{
            //saving the file into device
            FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            //exporting
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "data.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.turtlegang.gbthvolunteertracking.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
