package com.example.turtlegang.gbthvolunteertracking;


import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private String displayName;
    private boolean runbackground;
    private long LOCATION_INTERVAL = 20000;
    private LocationRequest mLocationRequestHighAccuracy;
    private LocationCallback locationCallback;
    private ArrayList<String> locations;
    private int MAX_LOCATION_ARRAY_SIZE = 30;
    final String KEY_SAVED_RADIO_BUTTON_INDEX = "SAVED_RADIO_BUTTON_INDEX";
    private String statusString;
    private ConnectivityManager cm;
    private ConnectivityManager.NetworkCallback nwc;
    private AlertDialog alert;
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private final String url = "https://gbthtracking.herokuapp.com/";
    private Socket socket;
    private HashMap<String, Marker> markers = new HashMap<>();
    private String usertype;


    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        //get usertype that needs to use to differentiate the UI
        usertype = LoginActivity.usertype;

        try {
            socket = IO.socket(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        setContentView(R.layout.activity_maps);
        locations = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);

        mLocationRequestHighAccuracy = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(LOCATION_INTERVAL);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy);
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. Show the
                            // user a dialog requesting locations permission.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        MainActivity.this, 0x1);
                            } catch (IntentSender.SendIntentException exception) {
                                // Ignore the error.
                            } catch (ClassCastException exception) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // write location data to SD card
                    // ...
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
                        Intent intent = new Intent(MainActivity.this, LocationSDService.class);
                        intent.putStringArrayListExtra("locations", locations);
                        startService(intent);
                    }
                }
            }
        };


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //check for internet connection
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder().build();
        nwc = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onLost(Network network){
                //add the code when the network connection is lost
                //an alert dialog is shown
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("No Internet Connection");
                builder.setMessage("Please check your internet connection");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        //if users click ok, the app will quit
                        //I don't think user wants to do that, so I just turn it off
                        //finishAndRemoveTask();
                    }
                });
                //show the dialog
                alert = builder.create();
                alert.show();
            }

            @Override
            public void onAvailable(Network network){
                alert = null;
            }
        };
        cm.registerNetworkCallback(request, nwc);

        final WorkManager mWorkManager = WorkManager.getInstance();
        final PeriodicWorkRequest mRequest = new
                PeriodicWorkRequest.Builder(LocationWorker.class, 30, TimeUnit.MINUTES).build();
        // Check if we are connected to WIFI, if so enqueue job.
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connManager != null;
        if (connManager.getActiveNetworkInfo() != null && connManager.getActiveNetworkInfo().getType() == 1) {
            mWorkManager.enqueue(mRequest);
        }
    }

    //check for run in background permission
    public boolean getSwitch(){
        runbackground = sharedPreferences.getBoolean("run_in_background", false);
        return runbackground;
    }

    @Override
    public void onStop(){
        super.onStop();
        //when user close the app, it starts the back ground service activity
        //it only works when the run in background is allowed
        if (getSwitch()){
            startService(new Intent(this, BackGroundService.class));
        }
        cm.unregisterNetworkCallback(nwc);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //once resume, stops background services
        stopService(new Intent(this, BackGroundService.class));
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy,
                locationCallback,
                Looper.getMainLooper());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add this device's location to map
        mMap = googleMap;
        setDisplayName();
        setStatusString();

        socket.connect();
        socket.on("all status", updateAllStatus);
        socket.on("status", updateStatus);
        
        this.plotMyLocation();
    }

    Emitter.Listener updateAllStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray statusArray = (JSONArray) args[0];

                    for (int i = 0; i < statusArray.length(); i++) {
                        try {
                            JSONObject device = statusArray.getJSONObject(i);

                            String deviceId = device.getString("device_id");
                            double latitude = device.getDouble("latitude");
                            double longitude = device.getDouble("longitude");
                            int statusCode = device.getInt("status");
                            String displayName = device.getString("name");
                            String status = getStatusString(statusCode);
                            LatLng location = new LatLng(latitude, longitude);
                            markers.put(deviceId, plotLocation(location, status, displayName));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    Emitter.Listener updateStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject device = (JSONObject) args[0];
                    try {
                        String deviceId = device.getString("device_id");
                        Marker marker = markers.get(deviceId);
                        if (marker != null) {
                            marker.remove();
                        }
                        double latitude = device.getDouble("latitude");
                        double longitude = device.getDouble("longitude");
                        int statusCode = device.getInt("status");
                        String displayName = device.getString("name");
                        String status = getStatusString(statusCode);
                        LatLng location = new LatLng(latitude, longitude);
                        markers.put(deviceId, plotLocation(location, status, displayName));
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private String getStatusString(int statusCode) {
        switch (statusCode) {
            case 1:
                return "Available";
            case 2:
                return "Responding";
            case 3:
                return "On Scene";
            case 4:
                return "Blocked";
            case 5:
                return "Unavailable";
            default:
                return "Do Not Disturb";
        }
    }

    public void setStatusString() {
        /* to update status */
        SharedPreferences sharedPreferences1 = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        int statusInt = sharedPreferences1.getInt(KEY_SAVED_RADIO_BUTTON_INDEX, 0);
        if (statusInt == 1) { statusString = "Available"; }
        else if (statusInt == 2) { statusString = "Responding"; }
        else if (statusInt == 3) { statusString = "On Scene"; }
        else if (statusInt == 4) { statusString = "Blocked"; }
        else if (statusInt == 5) { statusString = "Unavailable"; }
        else { statusString = "Do Not Disturb"; }

        CurrentStatus.setStatus(statusInt);
    }

    public void setDisplayName() {
        displayName = sharedPreferences.getString("display_name", "Set Display Name");
        CurrentStatus.setName(displayName);
    }

    public void plotMyLocation() {


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double currLat = location.getLatitude();
                            double currLong = location.getLongitude();

                            LatLng lastKnownLocation = new LatLng(currLat, currLong);
                            plotLocation(lastKnownLocation, statusString, displayName);

                            //center camera on user's location
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastKnownLocation));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(10.0f));

                            CurrentStatus.setLocation(currLat, currLong);
                            socket.emit("status", CurrentStatus.getCurrStatus());
                        }
                    }
                });
    }

    public Marker plotLocation(LatLng location, String status, String display) {
        float markerColour;     /* update marker colour */
        switch (status) {
            case "Available":
                markerColour = BitmapDescriptorFactory.HUE_GREEN;
                break;
            case "Responding":
                markerColour = BitmapDescriptorFactory.HUE_YELLOW;
                break;
            case "On Scene":
                markerColour = BitmapDescriptorFactory.HUE_ORANGE;
                break;
            case "Blocked":
                markerColour = BitmapDescriptorFactory.HUE_RED;
                break;
            case "Unavailable":
                markerColour = BitmapDescriptorFactory.HUE_BLUE;
                break;
            case "Do Not Disturb":
                markerColour = BitmapDescriptorFactory.HUE_VIOLET;
                break;
            default:
                // hopefully shouldn't happen
                markerColour = BitmapDescriptorFactory.HUE_CYAN;
                break;
        }

        return mMap.addMarker(new MarkerOptions().position(location)
                .title(display).snippet(status)
                .icon(BitmapDescriptorFactory.defaultMarker(markerColour)));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //for admin
        if(usertype.equals("admin")) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        //for volunteer or regular user
        else if(usertype.equals("volunteer")){
            getMenuInflater().inflate(R.menu.menu_volunteer, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.status_item) {
            Intent intent = new Intent(this, StatusActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.export){
            Intent intent = new Intent(this, ExportData.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        socket.off("all status", updateAllStatus);
        socket.off("status", updateStatus);
    }
}
