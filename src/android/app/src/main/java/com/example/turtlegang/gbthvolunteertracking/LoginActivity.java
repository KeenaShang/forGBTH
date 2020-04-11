package com.example.turtlegang.gbthvolunteertracking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    public static String usertype;
    public static double token;
    public static String userId;
    private EditText Username;
    private EditText Password;
    private Button Login;
    private TextView LoginMsg;
    private int counter = 5;
    private final int REQUEST_LOCATION = 1000;
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        checkPermission();

        queue = Volley.newRequestQueue(this);

        setContentView(R.layout.activity_login);
        Username = (EditText)findViewById(R.id.username);
        Password = (EditText)findViewById(R.id.password);
        Login = (Button)findViewById(R.id.loginbutton);
        LoginMsg = (TextView)findViewById(R.id.login_msg);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                validate(Username.getText().toString(), Password.getText().toString());
            }
        });
    }


    private void checkPermission() {
        boolean locationPermission = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
        boolean storagePermission = (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED);
        // Ask permission if it is not granted
        if (locationPermission) {
            // We don't have  location permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }else if (storagePermission) {
            // We don't have storage permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
                    checkPermission();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                    finishAndRemoveTask();
                }
                break;
            }
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
                    checkPermission();
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                    finishAndRemoveTask();
                }
                break;
            }
        }
    }

    private void validate(final String userName, String userPassword){
        if (userName.isEmpty()) {
            Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPassword.isEmpty()) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://gbthtracking.herokuapp.com/auth/login";
        JSONObject jo = new JSONObject();
        try {
            jo.put("user_id", userName);
            jo.put("password", userPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Request a string response from the provided URL.
        JsonObjectRequest jsonOjbectRequest = new JsonObjectRequest(Request.Method.POST, url, jo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LoginMsg.setText("");
                        boolean is_admin;
                        try {
                            is_admin = response.getBoolean("is_admin");
                            token = response.getDouble("token");
                            userId = userName;
                            if(is_admin){ //replace with
                                // if(Auth.isValid(userName, userPassword) {
                                // lead to the page that allows export
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                // set the global value for differentiation
                                usertype = "admin";
                                startActivity(intent);
                            } else {
                                // lead to the page that does not allow export
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                usertype = "volunteer";
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                LoginMsg.setText("Login failed");

            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonOjbectRequest);
    }
}
