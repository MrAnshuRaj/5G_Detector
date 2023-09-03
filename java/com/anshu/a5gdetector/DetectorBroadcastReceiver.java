package com.anshu.a5gdetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DetectorBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context,
                    DetectorBroadcastReceiver.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
