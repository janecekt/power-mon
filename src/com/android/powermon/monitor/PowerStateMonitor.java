package com.android.powermon.monitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;
import com.android.powermon.R;
import com.android.powermon.comms.AlertSender;
import com.android.powermon.event.EventBroker;
import com.android.powermon.event.StatusChangedEvent;
import com.android.powermon.util.AppPreferences;
import com.google.inject.Inject;
import roboguice.event.EventManager;

public class PowerStateMonitor implements Monitor {
    public static enum Event { TO_BATTERY, TO_AC, ON_BATTERY_TIMEOUT }

    public static enum State {
        AC(R.string.powerStateMonitor_ac),
        BATTERY_MESSAGE_PENDING(R.string.powerStateMonitor_batteryMessagePending),
        BATTERY_MESSAGE_SENT(R.string.powerStateMonitor_batteryMessageSent);

        private int resId;

        State(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }
    }


    public static final String ACTION_ON_BATTERY_TIMEOUT = "com.android.powermon.onBatteryTimeout";
    private static final String TAG = PowerStateMonitor.class.getSimpleName();
    private State state = null;
    private Context context;
    private AppPreferences appPreferences;
    private AlertSender alertSender;
    private AlarmManager alarmManager;
    private EventBroker eventBroker;
    private BroadcastReceiver broadcastReceiver;

    @Inject
    public PowerStateMonitor(Context context,
            AppPreferences appPreferences,
            AlertSender alertSender,
            AlarmManager alarmManager,
            EventBroker eventBroker) {

        this.context = context;
        this.appPreferences = appPreferences;
        this.alertSender = alertSender;
        this.alarmManager = alarmManager;
        this.eventBroker = eventBroker;
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Battery event
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    onBatteryStatusChanged(new BatteryStatus(intent));
                }
                // Alarm event
                if (ACTION_ON_BATTERY_TIMEOUT.equals(intent.getAction())) {
                    handleEvent(Event.ON_BATTERY_TIMEOUT);
                }
            }
        };
    }


    @Override
    public void enable() {
        // Clear state
        setState(null);

        // Register battery status listener
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(ACTION_ON_BATTERY_TIMEOUT);
        context.registerReceiver(broadcastReceiver, filter);

    }

    @Override
    public void disable() {
        // Unregister receiver
        context.unregisterReceiver(broadcastReceiver);

        // Clear state
        setState(null);
    }

    @Override
    public String getState() {
        return context.getString(R.string.powerStateMonitor_status,
                    ((state != null) ? context.getString(state.getResId()) : "??")
                );
    }

    private void setState(State newState) {
        this.state = newState;
        Log.i(TAG, "Status changed to " + newState);
        eventBroker.publish(new StatusChangedEvent());
    }


    public void onBatteryStatusChanged(BatteryStatus batteryStatus) {
        // Initialization
        if (state == null) {
            setState(batteryStatus.isOnBattery() ? State.BATTERY_MESSAGE_SENT : State.AC);
            return;
        }

        // Call handleEvent appropriately
        if (batteryStatus.isOnBattery()) {
            handleEvent(Event.TO_BATTERY);
        } else {
            handleEvent(Event.TO_AC);
        }

    }


    public void handleEvent(Event event) {
        // Handle change
        switch (state) {
            case AC:
                // Was AC now switch to battery
                // - State = BATTERY_MESSAGE_PENDING;
                // - Action = schedule alarm
                if (Event.TO_BATTERY.equals(event)) {
                    setState(State.BATTERY_MESSAGE_PENDING);
                    scheduleBatteryTimeout();
                }
                break;

            case BATTERY_MESSAGE_PENDING:
                // Was on battery now is on AC (before timeout expired)
                // - State = AC
                // - Action = cancel scheduling
                if (Event.TO_AC.equals(event)) {
                    setState(State.AC);
                    cancelBatteryTimeout();
                }
                // Was on battery and timeout expired
                // - State = BATTERY_MESSAGE_SENT
                // - Action = send message
                if (Event.ON_BATTERY_TIMEOUT.equals(event)) {
                    setState(State.BATTERY_MESSAGE_SENT);
                    alertSender.sendAlert(appPreferences.getAlertPhoneNumbers(),
                            context.getString(R.string.sms_runningOnBattery));
                }

                break;

            case BATTERY_MESSAGE_SENT:
                // Was on battery after message sent and AC power was restored
                // - State = AC
                // - Action = send message
                if (Event.TO_AC.equals(event)) {
                    setState(State.AC);
                    alertSender.sendAlert(
                            appPreferences.getAlertPhoneNumbers(),
                            context.getString(R.string.sms_runningOnAC));
                }
                break;
        }
    }


    private void scheduleBatteryTimeout() {
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + appPreferences.getOnBatteryThreshold() * 1000,
                PendingIntent.getBroadcast(context, 1, new Intent(ACTION_ON_BATTERY_TIMEOUT), 0));
    }

    private void cancelBatteryTimeout() {
        alarmManager.cancel(PendingIntent.getBroadcast(context, 1, new Intent(ACTION_ON_BATTERY_TIMEOUT), 0));
    }
}
