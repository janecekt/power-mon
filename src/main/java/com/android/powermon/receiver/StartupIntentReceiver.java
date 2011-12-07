package com.android.powermon.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.google.inject.Inject;
import roboguice.receiver.RoboBroadcastReceiver;


public class StartupIntentReceiver extends RoboBroadcastReceiver {
    @Inject
    Context context;

    @Override
    public void handleReceive(Context context, Intent intent) {
        Toast.makeText(context, "BroadCast receiver called !", Toast.LENGTH_LONG).show();
    }
}
