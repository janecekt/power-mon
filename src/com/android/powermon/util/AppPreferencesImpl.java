package com.android.powermon.util;

import android.content.SharedPreferences;
import com.android.powermon.R;
import com.google.inject.Inject;
import roboguice.inject.InjectResource;

import java.util.ArrayList;
import java.util.List;

public class AppPreferencesImpl implements AppPreferences {
    @Inject
    public SharedPreferences sharedPreferences;

    @InjectResource(R.string.pref_key_alertPhoneNumber1)
    public String keyAlertPhoneNumber1;

    @InjectResource(R.string.pref_key_alertPhoneNumber2)
    public String keyAlertPhoneNumber2;

    @InjectResource(R.string.pref_key_alertPhoneNumber3)
    public String keyAlertPhoneNumber3;


    @InjectResource(R.string.pref_key_lowBatteryThreshold)
    public String keyLowBatteryThreshold;

    @InjectResource(R.string.pref_key_monitoringEnabled)
    public String keyMonitoringEnabled;

    @InjectResource(R.string.pref_key_onBatteryThreshold)
    public String keyOnBatteryThreshold;

    @InjectResource(R.string.pref_key_smsSendEnabled)
    public String keySmsSendEnabled;


    @Override
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    @Override
    public List<String> getAlertPhoneNumbers() {
        List<String> list = new ArrayList<String>();
        addToListIfNotNull(list, sharedPreferences.getString(keyAlertPhoneNumber1, null));
        addToListIfNotNull(list, sharedPreferences.getString(keyAlertPhoneNumber2, null));
        addToListIfNotNull(list, sharedPreferences.getString(keyAlertPhoneNumber3, null));
        return list;
    }

    @Override
    public int getLowBatteryThreshold() {
        return Integer.valueOf(sharedPreferences.getString(keyLowBatteryThreshold, "30"));
    }

    @Override
    public int getOnBatteryThreshold() {
        return Integer.valueOf(sharedPreferences.getString(keyOnBatteryThreshold, "30"));
    }

    @Override
    public boolean isMonitoringEnabled() {
        return sharedPreferences.getBoolean(keyMonitoringEnabled, false);
    }

    @Override
    public void setMonitoringEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(keyMonitoringEnabled, enabled);
        editor.commit();
    }

    @Override
    public boolean isSmsSendEnabled() {
        return sharedPreferences.getBoolean(keySmsSendEnabled, false);
    }

    private void addToListIfNotNull(List<String> list, String string) {
        if ((string != null) && (!"".equals(string.trim()))) {
            list.add(string.trim());
        }
    }
}
