package com.android.powermon.guice;

import com.android.powermon.monitor.LowBatteryMonitor;
import com.android.powermon.monitor.Monitor;
import com.android.powermon.monitor.PowerStateMonitor;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.ArrayList;
import java.util.List;

public class MonitorListProvider implements Provider<List<Monitor>> {
    @Inject
    public PowerStateMonitor powerStateMonitor;

    @Inject
    public LowBatteryMonitor lowBatteryMonitor;

    @Override
    public List<Monitor> get() {
        List<Monitor> list = new ArrayList<Monitor>();
        list.add(powerStateMonitor);
        list.add(lowBatteryMonitor);
        return list;
    }
}
