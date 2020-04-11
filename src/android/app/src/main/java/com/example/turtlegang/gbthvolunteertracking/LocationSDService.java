package com.example.turtlegang.gbthvolunteertracking;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import androidx.core.content.ContextCompat;

public class LocationSDService extends IntentService {
    private boolean canWriteToExternal;
    private boolean permissionGranted;
    private String fileName;

    static Semaphore semaphore = new Semaphore(1);

    @Override
    public void onCreate() {
        super.onCreate();
        canWriteToExternal = isExternalStorageWritable();
        permissionGranted = hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        fileName = "location_history.txt";
    }

    public LocationSDService() {
        super("SaveToSDService");
    }


    public LocationSDService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (canWriteToExternal && permissionGranted) {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            ConcurrencyManagerSingleton concurrencyManagerSingleton =
                    ConcurrencyManagerSingleton.getInstance();
            FileOutputStream outputStream;
            try {
                if (file.exists()) {
                    outputStream = new FileOutputStream(file, true);
                } else {
                    outputStream = new FileOutputStream(file);
                }
                concurrencyManagerSingleton.locationArrMutex.acquire();
                ArrayList<String> locations = intent.getStringArrayListExtra("locations");
              
                for (int i = 0; i < locations.size(); i++) {
                    outputStream.write(locations.get(i).getBytes());
                }
                outputStream.close();
                locations.clear();
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                concurrencyManagerSingleton.locationArrMutex.release();
            }
        }
    }

    protected boolean hasPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    // Checks if a volume containing external storage is available for read and write.
    protected boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
