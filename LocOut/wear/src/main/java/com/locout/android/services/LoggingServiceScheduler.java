package com.locout.android.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class LoggingServiceScheduler extends BroadcastReceiver {

    public static final String TAG = LoggingServiceScheduler.class.getSimpleName();
    public static final String ACTION_SERVICE_START = "service_start";

    // restart service every REPEAT_TIME milliseconds
    private static final long REPEAT_TIME = 1000 * 60 * 5;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Scheduling logging service every " + (REPEAT_TIME / 1000) + " seconds");

        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, LoggingServiceInvoker.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();

        // start 60 seconds after boot completed
        cal.add(Calendar.SECOND, 60);

        // fetch every REPEAT_TIME seconds
        // InexactRepeating allows Android to optimize the energy consumption
        service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pending);
    }
}
