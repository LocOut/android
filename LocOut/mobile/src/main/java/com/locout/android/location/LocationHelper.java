package com.locout.android.location;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Date;

public class LocationHelper implements com.google.android.gms.location.LocationListener {

    public static final String TAG = LocationHelper.class.getSimpleName();

    public static final int LOCATION_MAXIMUM_UPDATE_INTERVAL = 500;
    public static final int LOCATION_MINUMUM_UPDATE_INTERVAL = 1000;

    private boolean updatingLocation = false;

    private long lastLocationUpdate;
    private Location lastLocation;

    private ArrayList<LocationListener> locationListeners;

    public LocationHelper() {
        locationListeners = new ArrayList<>();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_MINUMUM_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_MAXIMUM_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        lastLocationUpdate = (new Date()).getTime();
        for (LocationListener locationListener : locationListeners) {
            locationListener.onLocationChanged(lastLocation);
        }
    }

    public void startLocationUpdates(GoogleApiClient googleApiClient) {
        Log.d(TAG, "Starting location updates");
        updatingLocation = true;
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(), this);
    }

    public void stopLocationUpdates(GoogleApiClient googleApiClient) {
        Log.d(TAG, "Stopping location updates");
        updatingLocation = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    /**
     * Getter & Setter
     */
    public boolean isUpdatingLocation() {
        return updatingLocation;
    }

    public void setUpdatingLocation(boolean updatingLocation) {
        this.updatingLocation = updatingLocation;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public long getLastLocationUpdate() {
        return lastLocationUpdate;
    }

    public void setLastLocationUpdate(long lastLocationUpdate) {
        this.lastLocationUpdate = lastLocationUpdate;
    }

    public ArrayList<LocationListener> getLocationListeners() {
        return locationListeners;
    }

    public void setLocationListeners(ArrayList<LocationListener> locationListeners) {
        this.locationListeners = locationListeners;
    }
}
