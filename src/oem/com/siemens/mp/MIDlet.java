package com.siemens.mp;

import emulator.Emulator;

import javax.microedition.io.ConnectionNotFoundException;

public class MIDlet {

	public static void notifyDestroyed() {
		Emulator.getMIDlet().notifyDestroyed();
	}

	public static void notifyPaused() {
		Emulator.getMIDlet().notifyPaused();
	}

	public static String getAppProperty(String s) {
		return Emulator.getMIDlet().getAppProperty(s);
	}

	public static final boolean platformRequest(String s)
			throws ConnectionNotFoundException, NotAllowedException {
		return Emulator.getMIDlet().platformRequest(s);
	}

	public static String[] getSupportedProtocols() {
		return new String[0];
	}
}
