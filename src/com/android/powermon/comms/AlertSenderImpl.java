package com.android.powermon.comms;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;
import com.android.powermon.util.AppPreferences;
import com.google.inject.Inject;

import java.util.List;

public class AlertSenderImpl implements AlertSender {
    private Context context;
    private AppPreferences appPreferences;
    private SmsManager smsManager;

    @Inject
    public AlertSenderImpl(Context context, AppPreferences appPreferences, SmsManager smsManager) {
        this.context = context;
        this.appPreferences = appPreferences;
        this.smsManager = smsManager;
    }

    @Override
    public void sendAlert(List<String> phoneNumbers, String message) {
        // Show popup
        Toast.makeText(context, "ALERT SMS: \n" + message, Toast.LENGTH_LONG).show();

        if (appPreferences.isSmsSendEnabled()){
            for (String phoneNumber : phoneNumbers) {
                // Send SMS
                smsManager.sendTextMessage(
                        phoneNumber,
                        null,  // use default service center
                        message,
                        null,
                        null);
            }
        }
    }
}
