package com.locout.android.api;

public class Device {

    private long id;
    private String name;
    private float trustLevel;
    private double latitude;
    private double longitude;

    public Device(long id) {
        this.id = id;
    }

    public int getTrustLevelPercentage() {
        return Math.round(trustLevel * 100);
    }

    /**
     * Getter & Setter
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(float trustLevel) {
        this.trustLevel = trustLevel;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
