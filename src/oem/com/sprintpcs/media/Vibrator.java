package com.sprintpcs.media;

import emulator.Emulator;

public class Vibrator {
	public static void vibrate(int paramInt) {
		Emulator.getEmulator().getScreen().startVibra(paramInt);
	}
}
