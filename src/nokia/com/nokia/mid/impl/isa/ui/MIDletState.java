package com.nokia.mid.impl.isa.ui;

import javax.microedition.midlet.MIDlet;

public abstract class MIDletState {
	private MIDletRTInfo u;
	private MIDletManager v;
	protected MIDlet midlet;

	protected MIDletState(MIDlet paramMIDlet) {
		this.midlet = paramMIDlet;
		this.v = MIDletManager.inst;
		this.u = this.v.c(this);
	}

	public final String getAppProperty(String paramString) {
		return MIDletManager.s_getAppProperty(paramString);
	}

	MIDlet getMIDlet() {
		return this.midlet;
	}

	MIDletRTInfo getMIDletInfo() {
		return this.u;
	}

	public final String getMIDletName() {
		return this.u.getName();
	}
}
