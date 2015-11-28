package com.locout.android.wear;

import android.os.Handler;
import android.util.Log;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.locout.android.LocOut;

import java.util.Date;

public class WearHelper implements MessageApi.MessageListener{

    public static final String TAG = WearHelper.class.getSimpleName();

    private static final int UPDATE_INTERVAL = 10000;

    private Handler updateHandler = new Handler();
    private Runnable updateRunnable;

    private float trustLevel;
    private long lastTrustLevelUpdate;

    private LocOut app;

    public WearHelper(LocOut app) {
        this.app = app;

        updateRunnable = new Runnable(){
            public void run(){
                requestTrustLevel();
                updateHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent.getPath());
        //Toast.makeText(app.getContextActivity(), "onMessageReceived", Toast.LENGTH_SHORT).show();

        if (messageEvent.getPath().equalsIgnoreCase("set_trust_level") ) {
            String trustLevelString = new String(messageEvent.getData());
            trustLevel = Float.parseFloat(trustLevelString);
            lastTrustLevelUpdate = (new Date()).getTime();
        }
    }

    public void startUpdating() {
        if (app.getGoogleApiClient().isConnected()) {
            app.sendMessageToRemoteNodes("start_logging", null);
        }
        updateHandler.postDelayed(updateRunnable, UPDATE_INTERVAL);
    }

    public void stopUpdating() {
        if (app.getGoogleApiClient().isConnected()) {
            app.sendMessageToRemoteNodes("stop_logging", null);
        }
        updateHandler.removeCallbacks(updateRunnable);
    }

    public void requestTrustLevel() {
        if (app.getGoogleApiClient().isConnected()) {
            app.sendMessageToRemoteNodes("get_trust_level", null);
        } else {
            trustLevel = 0f;
            lastTrustLevelUpdate = (new Date()).getTime();
        }
    }

    public float getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(float trustLevel) {
        this.trustLevel = trustLevel;
    }
}
