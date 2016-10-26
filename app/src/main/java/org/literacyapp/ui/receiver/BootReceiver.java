package org.literacyapp.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.literacyapp.ui.service.StatusBarService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "onReceive");

        Intent serviceIntent = new Intent(context, StatusBarService.class);
        context.startService(serviceIntent);
    }
}
