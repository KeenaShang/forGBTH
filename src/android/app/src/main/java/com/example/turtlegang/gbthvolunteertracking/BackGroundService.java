package com.example.turtlegang.gbthvolunteertracking;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;

public class BackGroundService extends Service {
    private LocationCallback locationCallback;
    private ArrayList<String> locations;
    private int MAX_LOCATION_ARRAY_SIZE = 5;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //the function for recording locations
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // write location data to SD card
                    String latitude = String.valueOf(location.getLatitude()).concat(",");
                    String longitude = String.valueOf(location.getLongitude()).concat(",");
                    String timestamp = String.valueOf(location.getTime());
                    ConcurrencyManagerSingleton concurrencyManagerSingleton =
                            ConcurrencyManagerSingleton.getInstance();
                    try {
                        concurrencyManagerSingleton.locationArrMutex.acquire();
                        locations.add(latitude.concat(longitude).concat(timestamp).concat("\n"));

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    } finally {
//                        semaphore.release();
                        concurrencyManagerSingleton.locationArrMutex.release();
                    }
                    // write locations to external file in batches of MAX_LOCATION_ARRAY_SIZE
                    if (locations.size() > MAX_LOCATION_ARRAY_SIZE) {
                        Intent intent = new Intent(BackGroundService.this, LocationSDService.class);
                        intent.putStringArrayListExtra("locations", locations);
                        startService(intent);
                    }
                }
            }
        };
        //this is for returning the state of this program
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
