package com.locout.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.locout.android.api.Device;
import com.locout.android.ui.DeviceListAdapter;

public class MainActivity extends AppCompatActivity {

    DeviceListAdapter deviceListAdapter;
    ListView deviceListView;

    LocOut app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (LocOut) getApplication();
        if (!app.isInitialized) {
            app.initialize(this);
        } else {
            app.setContextActivity(this);
        }

        setupUi();
    }

    private void setupUi() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Device sampleDevice = new Device(4567l);
        sampleDevice.setName("Test device");
        sampleDevice.setTrustLevel(0.5f);
        app.getUser().getDevices().add(sampleDevice);

        sampleDevice = new Device(4567l);
        sampleDevice.setName("Test device");
        sampleDevice.setTrustLevel(0.5f);
        app.getUser().getDevices().add(sampleDevice);

        sampleDevice = new Device(4567l);
        sampleDevice.setName("Test device");
        sampleDevice.setTrustLevel(0.5f);
        app.getUser().getDevices().add(sampleDevice);

        sampleDevice = new Device(4567l);
        sampleDevice.setName("Test device");
        sampleDevice.setTrustLevel(0.5f);
        app.getUser().getDevices().add(sampleDevice);

        sampleDevice = new Device(4567l);
        sampleDevice.setName("Test device");
        sampleDevice.setTrustLevel(0.5f);
        app.getUser().getDevices().add(sampleDevice);

        sampleDevice = new Device(4567l);
        sampleDevice.setName("Test device");
        sampleDevice.setTrustLevel(0.5f);
        app.getUser().getDevices().add(sampleDevice);

        deviceListAdapter = new DeviceListAdapter(this, R.id.deviceListView, app.getUser().getDevices());
        deviceListView.setAdapter(deviceListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        app.getGoogleApiClient().connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        app.getGoogleApiClient().disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
