package com.nokia.mid.ui;

import emulator.Emulator;

public class DeviceControl {

    public static int getUserInactivityTime() {
        Emulator.getEmulator().getLogStream().println("*** com.nokia.mid.ui.DeviceControl.getUserInactivityTime() ***");
        return 0;
    }

    public static void resetUserInactivityTime() {
        Emulator.getEmulator().getLogStream().println("*** com.nokia.mid.ui.DeviceControl.resetUserInactivityTime() ***");
    }

    public static void flashLights(long paramLong) {
        Emulator.getEmulator().getLogStream().println("*** com.nokia.mid.ui.DeviceControl.flashLights() ***");
        DirectGraphicsInvoker.getDisplay().flashBacklight(paramLong);
    }

    public static void setLights(int paramInt1, int paramInt2) {
		/*
		if (paramInt2 != Emulator.screenBrightness) {
			Emulator.getEmulator().getLogStream()
			.println("*** com.nokia.mid.ui.DeviceControl.setLights(" + paramInt1 + "," + paramInt2 + ") ***");
		}
		 */
        //Emulator.screenBrightness = paramInt2;
    }

    public static void startVibra(int paramInt, long paramLong) {
        Emulator.getEmulator().getScreen().startVibra(paramLong);
    }

    public static void stopVibra() {
        Emulator.getEmulator().getScreen().stopVibra();
    }
}