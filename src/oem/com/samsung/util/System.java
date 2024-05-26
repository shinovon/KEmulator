package com.samsung.util;

import emulator.Emulator;

public class System {
	public static void setExitURI(final String s) {
		try {
			Emulator.getMIDlet().platformRequest(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void promptMasterVolume() {
	}
}
