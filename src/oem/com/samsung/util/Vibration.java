package com.samsung.util;

import emulator.Emulator;

public class Vibration {
	public static boolean isSupportedbyDevice() {
		return false;
	}

	public static void startVibration(int paramInt1, int paramInt2) {
	}

	public static void stopVibration() {
	}

	public static boolean isSupported() {
		return true;
	}

	public static void start(int paramInt1, int paramInt2) {
		Emulator.getEmulator().getScreen().startVibra(paramInt1);
	}

	public static void stop() {
		Emulator.getEmulator().getScreen().stopVibra();
	}
}
