package com.locout.android.api;

import android.content.Context;

import com.locout.android.LocOut;
import com.locout.android.R;
import com.locout.android.location.LocationHelper;

import java.util.Date;

public class Device {

    private static final int MINIMUM_UPLOAD_INTERVAL = 10000;

    private static final float TRUST_DISTANCE_WEIGHT = 0.8f;
    private static final float TRUST_WEAR_WEIGHT = 0.1f;

    public static final float TRUST_LEVEL_HIGH = 0.75f;
    public static final float TRUST_LEVEL_MEDIUM = 0.5f;
    public static final float TRUST_LEVEL_LOW = 0.25f;

    private long id;
    private String name;
    private float trustLevel;
    private double latitude;
    private double longitude;
    private long lastTrustLevelUpload;

    public Device(long id) {
        this.id = id;
    }

    public String getReadableTrustLevel(Context context) {
        int percentage = getTrustLevelPercentage();
        String result = context.getString(R.string.readable_trust_level);
        result = result.replace("[VALUE]", String.valueOf(percentage));
        return result;
    }

    public int getTrustLevelPercentage() {
        return Math.round(trustLevel * 100);
    }

    public void calculateTrustLevel(LocOut app, User user) {
        float newTrustLevel = 0;

        float distance = LocationHelper.getDistance(latitude, longitude, user.getLatitude(), user.getLongitude());
        //Log.d(LocOut.TAG, "Distance: " + distance);

        newTrustLevel = TRUST_DISTANCE_WEIGHT * calculateDistanceTrustLevel(distance);
        newTrustLevel += TRUST_WEAR_WEIGHT * calculateWearTrustLevel(app);

        trustLevel = Math.max(0, Math.min(1, newTrustLevel));
        //Log.d(LocOut.TAG, "Trust level: " + trustLevel);
    }

    private float calculateDistanceTrustLevel(float distance) {
        if (distance <= 0) {
            return 1;
        } else if (distance > 500) {
            return 0;
        } else {
            return (float) (60 / ((0.5 * distance) + 60));
        }
    }

    private float calculateWearTrustLevel(LocOut app) {
        return app.getWearHelper().getTrustLevel();
    }

    public void uploadTrustLevel() {
        long now = (new Date()).getTime();
        if (now > lastTrustLevelUpload + MINIMUM_UPLOAD_INTERVAL) {
            RequestHelper.setTrustLevel(RequestHelper.REQUEST_SET_TRUST_LEVEL, id, trustLevel, null);
            lastTrustLevelUpload = now;
        }
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
