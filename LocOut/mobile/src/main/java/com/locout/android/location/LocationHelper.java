package com.locout.android.location;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.locout.android.R;

import java.util.ArrayList;
import java.util.Date;

public class LocationHelper implements com.google.android.gms.location.LocationListener {

    public static final String TAG = LocationHelper.class.getSimpleName();

    public static final int LOCATION_MAXIMUM_UPDATE_INTERVAL = 1000;
    public static final int LOCATION_MINUMUM_UPDATE_INTERVAL = 2000;

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

    public static float getDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public String getReadableDistanceTo(double sourceLatitude, double sourceLongitude, double destinationLatitude, double destinationLongitude, Context context) {
        String readableDistance;

        float distance = getDistance(sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude);
        int roundedDistance;
        String unit = context.getString(R.string.unit_meters);

        if (distance < 15) {
            return context.getString(R.string.distance_on_spot);
        } else if (distance < 50) {
            return context.getString(R.string.distance_super_close);
        } else if (distance < 100) {
            return context.getString(R.string.distance_close);
        } else if (distance < 200) {
            roundedDistance = Math.round(distance / 5) * 5;
        } else if (distance < 200) {
            roundedDistance = Math.round(distance / 10) * 10;
        } else if (distance < 1000) {
            roundedDistance = Math.round(distance / 25) * 25;
        } else {
            roundedDistance = Math.round(distance / 1000);
            if (roundedDistance == 1) {
                unit = context.getString(R.string.unit_kilometer);
            } else {
                unit = context.getString(R.string.unit_kilometers);
            }
        }

        readableDistance = String.valueOf(roundedDistance) + " " + unit;
        return context.getString(R.string.distance_from_place).replace("[VALUE]", readableDistance);
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
