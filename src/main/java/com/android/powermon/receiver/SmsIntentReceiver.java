package com.android.powermon.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsMessage;
import android.util.Log;
import com.android.powermon.R;
import com.android.powermon.comms.AlertSender;
import com.android.powermon.util.AppPreferences;
import com.android.powermon.util.AppStateBuilder;
import com.google.inject.Inject;
import roboguice.receiver.RoboBroadcastReceiver;

import java.util.Arrays;
import java.util.Locale;

public class SmsIntentReceiver extends RoboBroadcastReceiver {
    private static final String TAG = SmsIntentReceiver.class.getSimpleName();
    public static final String ACTION_SEND_STATUS = "com.android.powermon.SmsIntentReceiver_SENDSTATUS";
    public static final String COMMAND_ENABLE = "POWERMON ENABLE";
    public static final String COMMAND_DISABLE = "POWERMON DISABLE";
    public static final String COMMAND_STATUS = "POWERMON STATUS";


    @Inject
    public Context context;

    @Inject
    AppStateBuilder appStateBuilder;

    @Inject
    public AlarmManager alarmManager;

    @Inject
    public AlertSender alertSender;

    @Inject
    public AppPreferences appPreferences;

    @Override
    protected void handleReceive(Context context, Intent intent) {
        if (ACTION_SEND_STATUS.equals(intent.getAction())) {
            Log.i(TAG, "Sending status via SMS");
            sendStatus(intent.getStringExtra("phoneNumber"));
        }
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            // Get SMS message from the intent
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // Extract and process SMS Message
                for (Object pdu : (Object[]) bundle.get("pdus")){
                    if (pdu instanceof byte[]) {
                        handleSMSMessage(SmsMessage.createFromPdu((byte[]) pdu));
                    } else {
                        Log.e(TAG, "Unexpected PDUs type " + pdu.getClass());
                    }
                }
            }
        }
    }

    private void handleSMSMessage(SmsMessage smsMessage) {
        if (!isNumberAuthorized(smsMessage.getOriginatingAddress())) {
            Log.i(TAG, "Ignoring SMS: SMS from " + smsMessage.getOriginatingAddress() + " registed phone is "
                    + Arrays.toString(appPreferences.getAlertPhoneNumbers().toArray()));
            return;
        }

        String msgBody = smsMessage.getMessageBody().trim().toUpperCase();
        Log.i(TAG, "SMS from registered phone: " + msgBody );

        // Process commands
        if (msgBody.equals(COMMAND_ENABLE)) {
            Log.i(TAG, "SMS Trigger - enabling monitoring" );
            // Start service
            appPreferences.setMonitoringEnabled(true);
            // Schedule send status in 5s
            scheduleSendStatus(smsMessage.getOriginatingAddress(), 5000);

        } else if (msgBody.equals(COMMAND_DISABLE)) {
            Log.i(TAG, "SMS Trigger - disabling monitoring" );
            // Start service
            appPreferences.setMonitoringEnabled(false);
            // Schedule send status in 5s
            scheduleSendStatus(smsMessage.getOriginatingAddress(), 5000);

        } else if (msgBody.equals(COMMAND_STATUS)) {
            // Send status
            sendStatus(smsMessage.getOriginatingAddress());

        } else {
            // Invalid command send help and status
            alertSender.sendAlert(Arrays.asList(smsMessage.getOriginatingAddress()),
                    context.getString(R.string.sms_errorReply,
                    COMMAND_ENABLE + "\n" + COMMAND_DISABLE + "\n" + COMMAND_STATUS));
        }
    }


    private boolean isNumberAuthorized(String originatingPhoneNumber) {
        for (String alertPhoneNumber : appPreferences.getAlertPhoneNumbers()) {
            if (originatingPhoneNumber.endsWith(alertPhoneNumber)) {
                return true;
            }
        }
        return false;
    }


    private void scheduleSendStatus(String alertPhoneNumber, int delayInMs) {
        Intent intent = new Intent(ACTION_SEND_STATUS);
        intent.putExtra("phoneNumber", alertPhoneNumber);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + delayInMs,
                PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_ONE_SHOT));
    }


    private void sendStatus(String phoneNumber) {
        alertSender.sendAlert(Arrays.asList(phoneNumber),
                context.getString(R.string.sms_statusReply, appStateBuilder.buildSmsState()));
    }
}
