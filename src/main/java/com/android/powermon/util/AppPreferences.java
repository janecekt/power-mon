package com.android.powermon.util;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.SharedPreferences;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 11/16/11
 * Time: 8:01 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AppPreferences {
    SharedPreferences getSharedPreferences();

    List<String> getAlertPhoneNumbers();

    int getLowBatteryThreshold();

    int getOnBatteryThreshold();

    boolean isMonitoringEnabled();

    void setMonitoringEnabled(boolean enabled);

    boolean isSmsSendEnabled();
}
