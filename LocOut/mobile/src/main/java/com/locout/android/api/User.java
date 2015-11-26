package com.locout.android.api;

import java.util.ArrayList;

public class User {

    private long id;
    private ArrayList<Device> devices;

    public User(long id) {
        this.id = id;
        devices = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }
}
