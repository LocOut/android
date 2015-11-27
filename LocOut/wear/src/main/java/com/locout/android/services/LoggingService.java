package com.locout.android.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.locout.android.MainActivityWear;
import com.locout.android.TrustLevelChangedListener;
import com.locout.android.TrustLevelHelper;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LoggingService extends WearableListenerService implements TrustLevelChangedListener {

    public static final String TAG = LoggingService.class.getSimpleName();
    private static final long MINIMUM_EXPORT_INTERVAL = 1000;

    private TrustLevelHelper trustLevelHelper;

    private String lastNodeId;
    private long lastExportTimestamp = 0;

    /**
     * called when the service gets invoked by an intent
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Logging service invoked on " + android.os.Build.MODEL);

        long token = Binder.clearCallingIdentity();
        try {
            if (trustLevelHelper == null) {
                trustLevelHelper = new TrustLevelHelper();
                trustLevelHelper.getTrustLevelChangedListeners().add(this);
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }

        return Service.START_NOT_STICKY;
    }

    private void startLogging() {
        Log.i(TAG, "Logging started");
        trustLevelHelper.registerAllSensors();
    }

    private void stopLogging() {
        Log.i(TAG, "Logging stopped");
        trustLevelHelper.unregisterAllSensors();
    }

    @Override
    public void onTrustLevelChanged(float trustLevel) {
        long now = (new Date()).getTime();
        if (now > lastExportTimestamp + MINIMUM_EXPORT_INTERVAL) {
            sendTrustLevel(trustLevel);
            lastExportTimestamp = now;
        }
    }

    public void sendTrustLevel(float trustLevel) {
        byte[] data = String.valueOf(trustLevel).getBytes();
        sendMessageReply("set_trust_level", data);
    }

    /**
     * called when the GoogleApiClient received a message from
     * the connected smartphoe / tablet
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent.getPath());

        lastNodeId = messageEvent.getSourceNodeId();
        if (messageEvent.getPath().equalsIgnoreCase("start_activity") ) {
            Intent intent = new Intent(this, MainActivityWear.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (messageEvent.getPath().equalsIgnoreCase("start_logging") ) {
            startLogging();
        } else if (messageEvent.getPath().equalsIgnoreCase("stop_logging") ) {
            stopLogging();
        }

    }

    /**
     * used to send message back to the last connected node (phone)
     * @param path from SharedConstants.MESSAGE_PATH
     * @param data
     */
    public void sendMessageReply(final String path, final byte[] data) {
        final Context context = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                GoogleApiClient client = new GoogleApiClient.Builder(context)
                        .addApi(Wearable.API)
                        .build();
                client.blockingConnect(5000, TimeUnit.MILLISECONDS);
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(client, lastNodeId, path, data).await();
                if (result.getStatus().isSuccess()) {
                    Log.v(TAG, "Message sent.");
                } else {
                    Log.v(TAG, "Unable to send message.");
                }
                client.disconnect();
            }
        }).start();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged: " + dataEvents);
    }
}
