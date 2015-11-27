package com.locout.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LoggingServiceInvoker extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, LoggingService.class);
        context.startService(service);
    }
}