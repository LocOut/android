package com.locout.android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

public class TrustLevelHelper implements SensorEventListener {

    public static final String TAG = TrustLevelHelper.class.getSimpleName();

    private Context context;
    private SensorManager hardwareSensorManager;

    private float trustLevel;

    private ArrayList<Integer> sensorTypes;
    private ArrayList<TrustLevelChangedListener> trustLevelChangedListeners;

    public TrustLevelHelper(Context context) {
        this.context = context;

        trustLevelChangedListeners = new ArrayList<>();
        hardwareSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        sensorTypes = new ArrayList<>();
        sensorTypes.add(Sensor.TYPE_SIGNIFICANT_MOTION);
        sensorTypes.add(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorTypes.add(Sensor.TYPE_STEP_DETECTOR);
    }

    public void registerAllSensors() {
        Log.d(TAG, "Registering all sensors");
        for (Integer sensorType : sensorTypes) {
            try {
                Sensor hardwareSensor = hardwareSensorManager.getDefaultSensor(sensorType);
                if (hardwareSensor == null) {
                    throw new Exception("Sensor with type " + sensorType + " is not available on this device");
                }
                hardwareSensorManager.registerListener(this, hardwareSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void unregisterAllSensors() {
        Log.d(TAG, "Unregistering all sensors");
        for (Integer sensorType : sensorTypes) {
            try {
                Sensor hardwareSensor = hardwareSensorManager.getDefaultSensor(sensorType);
                hardwareSensorManager.unregisterListener(this, hardwareSensor);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void notifyTrustLevelChangedListeners() {
        for (TrustLevelChangedListener trustLevelChangedListener : trustLevelChangedListeners) {
            try {
                trustLevelChangedListener.onTrustLevelChanged(trustLevel);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        trustLevel = 0.5f;
        notifyTrustLevelChangedListeners();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public SensorManager getHardwareSensorManager() {
        return hardwareSensorManager;
    }

    public void setHardwareSensorManager(SensorManager hardwareSensorManager) {
        this.hardwareSensorManager = hardwareSensorManager;
    }

    public float getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(float trustLevel) {
        this.trustLevel = trustLevel;
    }

    public ArrayList<Integer> getSensorTypes() {
        return sensorTypes;
    }

    public void setSensorTypes(ArrayList<Integer> sensorTypes) {
        this.sensorTypes = sensorTypes;
    }

    public ArrayList<TrustLevelChangedListener> getTrustLevelChangedListeners() {
        return trustLevelChangedListeners;
    }

    public void setTrustLevelChangedListeners(ArrayList<TrustLevelChangedListener> trustLevelChangedListeners) {
        this.trustLevelChangedListeners = trustLevelChangedListeners;
    }
}
