package com.locout.android;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.locout.android.services.LoggingService;
import com.locout.android.services.LoggingServiceInvoker;
import com.locout.android.services.LoggingServiceScheduler;

public class LocOut extends Application implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    public static final String TAG = LocOut.class.getSimpleName();

    private boolean initialized = false;

    private Activity contextActivity;
    private GoogleApiClient googleApiClient;

    public void initialize(Activity contextActivity) {
        Log.d(TAG, "Initializing app");
        this.contextActivity = contextActivity;

        initializeGoogleApiClient();
        startServices();

        initialized = true;
    }

    /**
     * Service connection handling
     */
    public void startServices() {
        // start logging service
        Intent loggingService = new Intent(contextActivity, LoggingServiceInvoker.class);
        sendBroadcast(loggingService);

        // start logging service scheduler
        Intent loggingServiceScheduler = new Intent(contextActivity, LoggingServiceScheduler.class);
        loggingServiceScheduler.setAction(LoggingServiceScheduler.ACTION_SERVICE_START);
        sendBroadcast(loggingServiceScheduler);
    }

    public void stopServices() {
        // stop logging service
        Intent loggingService= new Intent(contextActivity, LoggingService.class);
        contextActivity.stopService(loggingService);
    }

    public void initializeGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();
        Log.v(TAG,"Connecting to GoogleApiClient");

        Wearable.MessageApi.addListener(googleApiClient, this);

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "GoogleApiClient connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient connection failed");
    }

    /**
     * called when the GoogleApiClient received a message from
     * the connected service
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent.getPath());

        if (messageEvent.getPath().equalsIgnoreCase("set_trust_level") ) {
            String data = new String(messageEvent.getData());
            ((MainActivityWear) contextActivity).setStatusText(data);
        }
    }

    /**
     * sends a message to WearableListenerServices running on this device
     * @param path
     * @param data
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
     * @param path
     * @param data
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

    public void testServiceConnection() {
        // just for debugging
        if (googleApiClient.isConnected()) {
            Toast.makeText(contextActivity, "Connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(contextActivity, "Not connected", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Getter & Setter
     */
    public boolean isInitialized() {
        return initialized;
    }

}
