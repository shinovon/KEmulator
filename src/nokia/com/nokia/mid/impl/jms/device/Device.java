package com.nokia.mid.impl.jms.device;

import emulator.Settings;

public class Device {
    public static short getRemainingBatteryPowerBars() {
        return 2;
    }

    public static short getMaxBatteryPowerBars() {
        return 4;
    }

    public static boolean isInFlightMode() {
        return Settings.networkNotAvailable;
    }
}
