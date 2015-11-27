package com.locout.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class MainActivityWear extends WearableActivity {

    private TextView mTextView;

    private LocOut app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_wear);

        // get reference to the global application class
        app = (LocOut) getApplication();
        // initilize with context activity if needed
        if (!app.isInitialized()) {
            app.initialize(this);
        }

        setAmbientEnabled();



        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();

        // Update the content
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        mTextView.setTextColor(Color.WHITE);
        mTextView.getPaint().setAntiAlias(false);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        mTextView.setTextColor(Color.GREEN);
        mTextView.getPaint().setAntiAlias(true);
    }

    public void setStatusText(String text) {
        mTextView.setText(text);
    }
}
