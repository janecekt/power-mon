package com.android.powermon.guice;

import android.telephony.SmsManager;
import com.android.powermon.comms.AlertSender;
import com.android.powermon.comms.AlertSenderImpl;
import com.android.powermon.event.EventBroker;
import com.android.powermon.monitor.LowBatteryMonitor;
import com.android.powermon.monitor.Monitor;
import com.android.powermon.monitor.MonitoringManager;
import com.android.powermon.monitor.PowerStateMonitor;
import com.android.powermon.util.*;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import roboguice.config.AbstractAndroidModule;
import roboguice.inject.SharedPreferencesName;

import java.util.List;

public class PowerMonGuiceConfigModule extends AbstractAndroidModule {
    @Override
    protected void configure() {
       // The manager is initialized eagerly - it in turn initializes the monitors
       bind(MonitoringManager.class).asEagerSingleton();

       bind(AppPreferences.class).to(AppPreferencesImpl.class).in(Singleton.class);

       bind(AppStateBuilder.class).to(AppStateBuilderImpl.class).in(Singleton.class);

       bind(AlertSender.class).to(AlertSenderImpl.class).in(Singleton.class);

       bind(PowerStateMonitor.class).in(Singleton.class);

       bind(LowBatteryMonitor.class).in(Singleton.class);

       bind(EventBroker.class).in(Singleton.class);

       bind(SmsManager.class).toInstance(SmsManager.getDefault());

       bind(new TypeLiteral<List<Monitor>>(){})
               .annotatedWith(Names.named(GuiceConstants.MONITOR_LIST))
               .toProvider(MonitorListProvider.class)
               .in(Singleton.class);

       // Name taken from the implementation of PreferenceManager
       bindConstant()
                .annotatedWith(SharedPreferencesName.class)
                .to("com.android.powermon_preferences");
    }
}
