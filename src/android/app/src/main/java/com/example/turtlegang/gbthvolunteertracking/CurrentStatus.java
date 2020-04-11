package com.example.turtlegang.gbthvolunteertracking;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class CurrentStatus {
    private static String deviceId = "id0";
    private static double currLat, currLong;
    private static String name;
    private static int status;

    public static void setDeviceId(String deviceId) {
        CurrentStatus.deviceId = deviceId;
    }

    public static void setLocation(double currLat, double currLong) {
        CurrentStatus.currLat = currLat;
        CurrentStatus.currLong = currLong;
    }

    public static void setName(String name) {
        CurrentStatus.name = name;
    }

    public static void setStatus(int status) {
        CurrentStatus.status = status;
    }


    public static JSONObject getCurrStatus() {
        JSONObject statusJson = new JSONObject();
        try {
            statusJson.put("device_id", deviceId);
            statusJson.put("name", name);
            statusJson.put("latitude", currLat);
            statusJson.put("longitude", currLong);
            statusJson.put("status", status);
            statusJson.put("time", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return statusJson;
    }



}
