package com.locout.android;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.locout.android.api.RequestCallback;
import com.locout.android.api.RequestHelper;
import com.locout.android.api.User;
import com.locout.android.location.LocationHelper;
import com.locout.android.location.LocationListener;
import com.locout.android.wear.WearHelper;

public class LocOut extends Application implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    public static final String TAG = LocOut.class.getSimpleName();

    public boolean isInitialized = false;
    private Activity contextActivity;

    private GoogleApiClient googleApiClient;
    private User user;
    private WearHelper wearHelper;
    private LocationHelper locationHelper;

    public void initialize(Activity contextActivity) {
        Log.d(TAG, "Initializing app");

        this.contextActivity = contextActivity;

        try	{
            user = new User(1l);

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addApiIfAvailable(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            wearHelper = new WearHelper();
            Wearable.MessageApi.addListener(googleApiClient, wearHelper);

            locationHelper = new LocationHelper();
            locationHelper.getLocationListeners().add(this);

            updateUser();

            Log.d(TAG, "Initialization done");
            isInitialized = true;
        } catch (Exception ex) {
            Log.e(TAG, "Error during initialization!");
            ex.printStackTrace();
            isInitialized = false;
        }
    }

    public void updateUser() {
        RequestHelper.getUserJson(RequestHelper.REQUEST_GET_USER, user.getId(), new RequestCallback() {

            @Override
            public void onRequestSuccess(int requestId, String response) {
                switch (requestId) {
                    case RequestHelper.REQUEST_GET_USER: {
                        user.parseFromJson(response);
                        user.updateTrustLevels(LocOut.this);
                        ((MainActivity) contextActivity).updateDevices();
                        break;
                    }
                }
            }

            @Override
            public void onRequestFailed(int requestId, String response) {

            }

        });
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location updated: " + location.getLatitude() + "," + location.getLongitude());
        user.setLatitude(location.getLatitude());
        user.setLongitude(location.getLongitude());
        user.updateTrustLevels(LocOut.this);
        ((MainActivity) contextActivity).updateDevices();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Google API client connected");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationHelper.startLocationUpdates(googleApiClient);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google API client connection suspended");
        locationHelper.stopLocationUpdates(googleApiClient);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Google API client connection failed");

    }

    /**
     * sends a message to WearableListenerServices running on this device
     */
    public void sendMessageToLocalNode(final String path, final byte[] data) {
        Log.v(TAG, "Sending message with path: " + path);
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    NodeApi.GetLocalNodeResult nodes = Wearable.NodeApi.getLocalNode(googleApiClient).await();
                    Node node = nodes.getNode();

                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            googleApiClient, node.getId(), path, data).await();

                    if (result.getStatus().isSuccess()) {
                        Log.v(TAG, "Message sent to " + node.getDisplayName());
                    } else {
                        Log.v(TAG, "Unable to send message to " + node.getDisplayName());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * sends a message to WearableListenerServices running on connected devices
     */
    public void sendMessageToRemoteNodes(final String path, final byte[] data) {
        Log.v(TAG, "Sending message with path: " + path);
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
                    for(Node node : nodes.getNodes()) {
                        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                                googleApiClient, node.getId(), path, data).await();
                        if (result.getStatus().isSuccess()) {
                            Log.v(TAG, "Message sent to " + node.getDisplayName());
                        } else {
                            Log.v(TAG, "Unable to send message to " + node.getDisplayName());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
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
