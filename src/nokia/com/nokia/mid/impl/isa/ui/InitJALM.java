package com.nokia.mid.impl.isa.ui;

import javax.microedition.midlet.MIDlet;

public final class InitJALM {

	public static MIDletAccess s_getMIDletAccessor() {
		return MIDletManager.inst;
	}

	public static void s_setDisplayAccessor(DisplayAccess paramDisplayAccess) {
		MIDletManager.s_setDisplayAccessor(paramDisplayAccess);
	}

	public static MIDlet getCurrentMIDlet() {
		return MIDletManager.getCurrentMIDlet();
	}
}
