package com.locout.android.api;

import com.locout.android.LocOut;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class User {

    private long id;
    private double latitude;
    private double longitude;
    private ArrayList<Device> devices;

    public User(long id) {
        this.id = id;
        devices = new ArrayList<>();
    }

    public void parseFromJson(String jsonString) {
        try {
            devices = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject userObject = jsonObject.getJSONObject("user");

            JSONArray devicesArray = userObject.getJSONArray("deviceLocations");
            for (int i = 0; i < devicesArray.length(); i++) {
                JSONObject deviceObject = devicesArray.getJSONObject(i);

                long id = deviceObject.getLong("id");
                String name = deviceObject.getString("name");
                double latitude = deviceObject.getDouble("lat");
                double longitude = deviceObject.getDouble("long");
                float trustLevel = (float) deviceObject.getDouble("trustLevel");

                Device device = new Device(id);
                device.setName(name);
                device.setLatitude(latitude);
                device.setLongitude(longitude);
                device.setTrustLevel(trustLevel);

                devices.add(device);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateTrustLevels(LocOut app) {
        for (int i = 0; i < devices.size(); i++) {
            devices.get(i).calculateTrustLevel(app, this);
            devices.get(i).uploadTrustLevel();
        }
        sortDevicesByTrustLevel();
    }

    public void uploadTrustLevels() {
        for (int i = 0; i < devices.size(); i++) {
            devices.get(i).uploadTrustLevel();
        }
    }

    public void sortDevicesByTrustLevel() {
        Collections.sort(devices, new Comparator<Device>() {
            @Override public int compare(Device device1, Device device2) {
                return Math.round((device2.getTrustLevel() * 10) - (device1.getTrustLevel() * 10));
            }
        });
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }
}
