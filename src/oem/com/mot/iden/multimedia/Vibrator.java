package com.mot.iden.multimedia;

import emulator.Emulator;

public class Vibrator
		extends Thread {
	public static final int MAX_VIBRATE_TIME = 500;
	public static final int MIN_PAUSE_TIME = 50;

	public void run() {
	}

	public static void vibrateFor(int timeInMs) {
		if (timeInMs > MAX_VIBRATE_TIME) timeInMs = MAX_VIBRATE_TIME;
		Emulator.getEmulator().getScreen().startVibra(timeInMs);
	}

	public static void vibratePeriodically(int timeInMs) {
	}

	public static void vibratePeriodically(int timeOnInMs, int timeOffInMs) {
	}

	public static void vibratorOff() {
		Emulator.getEmulator().getScreen().startVibra(0);
	}

	public static void vibratorOn() {
		Emulator.getEmulator().getScreen().startVibra(3000);
	}
}
