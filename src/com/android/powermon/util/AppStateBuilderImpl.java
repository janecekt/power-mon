package com.android.powermon.util;

import android.content.Context;
import com.android.powermon.R;
import com.android.powermon.guice.GuiceConstants;
import com.android.powermon.monitor.Monitor;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.List;

public class AppStateBuilderImpl implements AppStateBuilder {
    private Context context;
    private AppPreferences appPreferences;
    private List<Monitor> monitorList;

    @Inject
    public AppStateBuilderImpl(Context context,
            AppPreferences appPreferences,
            @Named(GuiceConstants.MONITOR_LIST) List<Monitor> monitorList) {
        this.context = context;
        this.appPreferences = appPreferences;
        this.monitorList = monitorList;
    }

    @Override
    public String buildScreenState() {
        StringBuilder builder = new StringBuilder();
        appendOverallState(builder);
        builder.append("\n");
        appendMonitorState(builder);
        builder.append("\n\n");
        appendConfigState(builder);
        return builder.toString();

    }

    @Override
    public String buildSmsState() {
        StringBuilder builder = new StringBuilder();
        appendOverallState(builder);
        builder.append("\n");
        appendMonitorState(builder);
        return builder.toString();
    }

    private String booleanToOnOffString(boolean bool) {
        return context.getString(bool ? R.string.status_on : R.string.status_off);
    }

    private void appendOverallState(StringBuilder builder) {
        builder.append(context.getString(R.string.status_overallState,
                booleanToOnOffString(appPreferences.isMonitoringEnabled()),
                booleanToOnOffString(appPreferences.isSmsSendEnabled())));
    }

    private void appendMonitorState(StringBuilder builder) {
        for (Monitor monitor : monitorList) {
            if (monitor != monitorList.get(0)){
                builder.append("\n");
            }
            builder.append("* ").append(monitor.getState());
        }
    }

    private void appendConfigState(StringBuilder builder) {
        builder.append(context.getString(R.string.status_overallAlertSettings,
                appPreferences.getLowBatteryThreshold(),
                appPreferences.getOnBatteryThreshold()));

        for (String phoneNumber : appPreferences.getAlertPhoneNumbers()) {
            builder.append("\n");
            builder.append(context.getString(R.string.status_overallPhoneSettings, phoneNumber));
        }


    }
}
