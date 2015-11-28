package com.locout.android;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.locout.android.api.Device;
import com.locout.android.api.RequestCallback;
import com.locout.android.api.RequestHelper;
import com.locout.android.ui.DeviceListAdapter;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_LOCATION = 1;

    DeviceListAdapter deviceListAdapter;
    ListView deviceListView;

    LocOut app;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setupUi() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDeviceDialog();
            }
        });

        deviceListAdapter = new DeviceListAdapter(this, R.id.deviceListView, app.getUser().getDevices());
        deviceListView = (ListView) findViewById(R.id.deviceListView);
        deviceListView.setAdapter(deviceListAdapter);
    }

    public void updateDevices() {
        deviceListAdapter.setDevices(app.getUser().getDevices());
        deviceListAdapter.notifyDataSetChanged();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!app.getLocationHelper().isUpdatingLocation()) {
                        app.getLocationHelper().startLocationUpdates(app.getGoogleApiClient());
                    }
                }
                return;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        app.getGoogleApiClient().connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.locout.android/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestPermissions();
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

    public void showAddDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a new device");
        builder.setMessage("A new device will be created at your current location. Please specify a name:");

        // Set up the input
        LinearLayout linearLayout = new LinearLayout(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int horizontalMargin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        int verticalMargin = Math.round(horizontalMargin / 2);
        layoutParams.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);

        linearLayout.setLayoutParams(layoutParams);

        final EditText input = new EditText(this);
        input.setLayoutParams(layoutParams);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        linearLayout.addView(input);

        builder.setView(linearLayout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                String deviceName = input.getText().toString();

                final Device newDevice = new Device(0);
                newDevice.setName(deviceName);
                newDevice.setTrustLevel(1f);
                newDevice.setLatitude(app.getUser().getLatitude());
                newDevice.setLongitude(app.getUser().getLongitude());


                RequestHelper.addDevice(RequestHelper.REQUEST_ADD_DEVICE, app.getUser().getId(), newDevice, new RequestCallback() {

                            @Override
                            public void onRequestSuccess(int requestId, String response) {
                                Handler mainHandler = new Handler(MainActivity.this.getMainLooper());

                                Runnable uiRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar snackbar = Snackbar.make(deviceListView, "Device added", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        app.getUser().getDevices().add(newDevice);

                                        Location currentLocation = new Location("");
                                        currentLocation.setLatitude(app.getUser().getLatitude());
                                        currentLocation.setLongitude(app.getUser().getLongitude());

                                        //app.onLocationChanged(currentLocation);
                                        app.updateUser();
                                    }
                                };

                                mainHandler.post(uiRunnable);
                            }

                            @Override
                            public void onRequestFailed(int requestId, String response) {
                                Log.e(LocOut.TAG, "Unable to create device: " + response);
                                Snackbar snackbar = Snackbar.make(deviceListView, "Unable to create device", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }

                        });

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
