package com.locout.android;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.locout.android.api.User;
import com.locout.android.location.LocationHelper;

public class LocOut extends Application implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static final String TAG = LocOut.class.getSimpleName();

    public boolean isInitialized = false;
    private Activity contextActivity;

    private GoogleApiClient googleApiClient;
    private User user;
    private LocationHelper locationHelper;

    public void initialize(Activity contextActivity) {
        Log.d(TAG, "Initializing app");

        this.contextActivity = contextActivity;

        try	{
            user = new User(1234l);
            locationHelper = new LocationHelper();

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            Log.d(TAG, "Initialization done");
            isInitialized = true;
        } catch (Exception ex) {
            Log.e(TAG, "Error during initialization!");
            ex.printStackTrace();
            isInitialized = false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Google API client connected");
        if (!locationHelper.isUpdatingLocation()) {
            locationHelper.startLocationUpdates(googleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google API client connection suspended");
        if (locationHelper.isUpdatingLocation()) {
            locationHelper.stopLocationUpdates(googleApiClient);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Google API client connection failed");
    }

    /**
     * Getter & Setter
     */
    public Activity getContextActivity() {
        return contextActivity;
    }

    public void setContextActivity(Activity contextActivity) {
        this.contextActivity = contextActivity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public LocationHelper getLocationHelper() {
        return locationHelper;
    }

    public void setLocationHelper(LocationHelper locationHelper) {
        this.locationHelper = locationHelper;
    }
}
