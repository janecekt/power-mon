package com.android.powermon.util;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

public class NotificationUtil {
    public static int NOTIFICATION_ID = 1;

    public static void statusBarNotification(Context context, String notificationMessage) {
		Notification notification = new Notification(
                R.drawable.ic_popup_reminder,
                notificationMessage,
                System.currentTimeMillis() );

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		manager.notify(NOTIFICATION_ID, notification);
    }
}
