package com.locout.android.wear;

import android.util.Log;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

public class WearHelper implements MessageApi.MessageListener{

    public static final String TAG = WearHelper.class.getSimpleName();

    private float trustLevel;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent.getPath());

        if (messageEvent.getPath().equalsIgnoreCase("set_trust_level") ) {
            String trustLevelString = new String(messageEvent.getData());
            trustLevel = Float.parseFloat(trustLevelString);
        }
    }

    public float getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(float trustLevel) {
        this.trustLevel = trustLevel;
    }
}
