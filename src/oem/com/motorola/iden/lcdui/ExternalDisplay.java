package com.motorola.iden.lcdui;

import javax.microedition.midlet.MIDlet;

public class ExternalDisplay {

	private static ExternalDisplay instance;

	public static ExternalDisplay getDisplay(MIDlet m) {
		if (instance == null) {
			return new ExternalDisplay();
		}
		return instance;
	}

	public boolean getFlipState() {
		return false;
	}

	public void releaseDisplay() {}

	public void reqiestDisplay() {}

}
