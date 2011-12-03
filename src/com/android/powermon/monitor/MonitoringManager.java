package com.android.powermon.monitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.android.powermon.R;
import com.android.powermon.activity.MainActivity;
import com.android.powermon.guice.GuiceConstants;
import com.android.powermon.util.AppPreferences;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.List;

public class MonitoringManager implements SharedPreferences.OnSharedPreferenceChangeListener  {
    private static final String TAG = MonitoringManager.class.getSimpleName();

    private boolean isEnabled = false;
    private Context context;
    private AppPreferences appPreferences;
    private NotificationManager notificationManager;
    private List<Monitor> monitors;


    @Inject
    public MonitoringManager(Context context,
            AppPreferences appPreferences,
            NotificationManager notificationManager,
            @Named(GuiceConstants.MONITOR_LIST) List<Monitor> monitors) {

        // Initialize fields
        this.context = context;
        this.appPreferences = appPreferences;
        this.notificationManager = notificationManager;
        this.monitors = monitors;

        // Initialize monitors
        initialize();

        // Subscribed to changes in shared preferences
        appPreferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "Shared preferences changed");
        initialize();
    }


    private void initialize() {
        if (!isEnabled && appPreferences.isMonitoringEnabled()) {
            isEnabled = true;
            // Enable monitors
            for (Monitor monitor : monitors) {
                monitor.enable();
            }
            addStatusBarNotification();
        }

        if (isEnabled && !appPreferences.isMonitoringEnabled()) {
            isEnabled = false;
            // Enable monitors
            for (Monitor monitor : monitors) {
                monitor.disable();
            }
            removeStatusBarNotification();
        }
    }


    private void addStatusBarNotification() {
        // Create pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        // Create notification
        Notification notification = new Notification(
                android.R.drawable.btn_star_big_on,
                context.getString(R.string.notification),
                System.currentTimeMillis());

        notification.setLatestEventInfo(context,
                context.getString(R.string.app_name),
                context.getString(R.string.notification),
                pendingIntent);

        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(0, notification);
    }


    private void removeStatusBarNotification() {
        notificationManager.cancel(0);
    }
}
