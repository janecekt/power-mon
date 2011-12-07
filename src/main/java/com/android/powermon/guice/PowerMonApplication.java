package com.android.powermon.guice;

import com.google.inject.Module;
import roboguice.application.RoboApplication;

import java.util.List;

public class PowerMonApplication extends RoboApplication {
    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(new PowerMonGuiceConfigModule());
    }
}
