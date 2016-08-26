package com.automaticVolumeControl.florian.automaticVolumeControl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Florian on 20/08/2016.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, BluetoothWatchService.class);
        context.startService(i);
    }
}
