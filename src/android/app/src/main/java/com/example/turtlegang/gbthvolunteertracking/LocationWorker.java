package com.example.turtlegang.gbthvolunteertracking;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Settings.Secure;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class LocationWorker extends Worker {
    private String filename;

    public LocationWorker(Context context, WorkerParameters wparams) {
        super(context, wparams);
        this.filename = "location_history.txt";
    }

    @NonNull
    @Override
    public Result doWork(){
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        uploadLocations(file);
        emptyLocationsFile(file);
        return Result.success();
    }

    private void uploadLocations(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String dev_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String url = "https://gbthtracking.herokuapp.com/api/api/status/";
            url.concat(dev_id);

            while((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 3) {
                    HashMap<String, String> json_dict = new HashMap<String, String>();
                    json_dict.put("latitude", values[0]);
                    json_dict.put("longitude", values[1]);
                    json_dict.put("time", values[2]);
                    json_dict.put("status", "1"); //Status is deprecated by no-one told backend.
                    JSONObject json_data = new JSONObject(json_dict);

                    URI put_url = new URI(url);
                    HttpURLConnection conn = (HttpURLConnection) put_url.toURL().openConnection();
                    conn.setDoInput(true);
                    conn.setRequestMethod("PUT");
                    conn.addRequestProperty("Content-Type", "application/json");
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(json_data.toString());
                    out.close();
                }
                br.close();
            }
        }
        catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void emptyLocationsFile(File file){
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
