package com.android.powermon.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.android.powermon.R;
import com.android.powermon.comms.AlertSender;
import com.android.powermon.event.EventBroker;
import com.android.powermon.event.StatusChangedEvent;
import com.android.powermon.util.AppPreferences;
import com.google.inject.Inject;
import roboguice.event.EventManager;

public class LowBatteryMonitor implements Monitor {
    public static enum State {
        BATTERY_OK(R.string.lowBatteryMonitor_batteryOk),
        BATTERY_LOW(R.string.lowBatteryMonitor_batteryLow);

        private int resId;

        State(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }
    }

    private static final String TAG = LowBatteryMonitor.class.getSimpleName();
    private State state = null;
    private Integer batteryPercentage = null;
    private Context context;
    private AppPreferences appPreferences;
    private AlertSender alertSender;
    private EventBroker eventBroker;
    private BroadcastReceiver broadcastReceiver;


    @Inject
    public LowBatteryMonitor(Context context,
                AppPreferences appPreferences,
                AlertSender alertSender,
                EventBroker eventBroker) {

        this.context = context;
        this.appPreferences = appPreferences;
        this.alertSender = alertSender;
        this.eventBroker = eventBroker;
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    onBatteryStatusChanged(new BatteryStatus(intent));
                }
            }
        };
    }


    @Override
    public void enable() {
        // Clear state
        setState(null, null);

        // Register battery status listener
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void disable() {
        // Unregister listener
        context.unregisterReceiver(broadcastReceiver);

        // Clear state
        setState(null, null);
    }

    @Override
    public String getState() {
      return  context.getString(R.string.lowBatteryMonitor_status,
                  (batteryPercentage != null) ? batteryPercentage : "??",
                  ((state != null) ? context.getString(state.getResId()) : "??")
                );
    }



    public void onBatteryStatusChanged(BatteryStatus batteryStatus) {
        // Initialization
        if (state == null) {
            setState(isOnBatteryAndLow(batteryStatus) ? State.BATTERY_LOW : State.BATTERY_OK,
                    batteryStatus.getBatteryPercentage());
            return;
        }

        switch (state) {
            case BATTERY_OK:
                // Battery became low
                // * state = BATTERY_LOW
                // * action = send message
                if (isOnBatteryAndLow(batteryStatus)) {
                    setState(State.BATTERY_LOW, batteryStatus.getBatteryPercentage());
                    alertSender.sendAlert(appPreferences.getAlertPhoneNumbers(),
                            context.getString(R.string.sms_lowBattery, batteryStatus.getBatteryPercentage()) );
                    return;
                }
                break;


            case BATTERY_LOW:
                // Battery became high
                // * state = BATTERY_HIGH
                if (!isOnBatteryAndLow(batteryStatus)) {
                    setState(State.BATTERY_OK, batteryStatus.getBatteryPercentage());
                    alertSender.sendAlert(appPreferences.getAlertPhoneNumbers(),
                            context.getString(R.string.sms_batteryOk, batteryStatus.getBatteryPercentage()) );
                    return;
                }
                break;
        }

        // Refresh battery percentage
        setState(state, batteryStatus.getBatteryPercentage());
    }

    private void setState(State newState, Integer batteryPercentage) {
        this.state = newState;
        this.batteryPercentage = batteryPercentage;
        Log.i(TAG, "Status changed to " + newState );
        eventBroker.publish(new StatusChangedEvent());
    }


    private boolean isOnBatteryAndLow(BatteryStatus batteryStatus) {
        return batteryStatus.isOnBattery() && (batteryStatus.getBatteryPercentage() < appPreferences.getLowBatteryThreshold());
    }
}
