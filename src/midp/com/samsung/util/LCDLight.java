package com.samsung.util;

import com.nokia.mid.ui.DeviceControl;

import emulator.Emulator;

public class LCDLight {
	public static boolean isSupported() {
		return false;
	}

	public static void on(int paramInt) {
		//Emulator.screenBrightness = paramInt;
	}

	public static void off() {
		//Emulator.screenBrightness = 15;
	}
}