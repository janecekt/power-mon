package com.android.powermon.monitor;

import android.content.Intent;
import android.os.BatteryManager;

public class BatteryStatus {
    private final boolean isOnBattery;
    private final int batteryPercentage;

    public BatteryStatus(Intent intent) {
        this(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) == 0,
            intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1));

    }

    public BatteryStatus(boolean onBattery, int batteryPercentage) {
        isOnBattery = onBattery;
        this.batteryPercentage = batteryPercentage;
    }

    public boolean isOnBattery() {
        return isOnBattery;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BatteryStatus that = (BatteryStatus) o;

        if (batteryPercentage != that.batteryPercentage) return false;
        if (isOnBattery != that.isOnBattery) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (isOnBattery ? 1 : 0);
        result = 31 * result + batteryPercentage;
        return result;
    }
}
