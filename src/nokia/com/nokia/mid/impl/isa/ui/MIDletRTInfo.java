package com.nokia.mid.impl.isa.ui;

import emulator.Emulator;

final class MIDletRTInfo {
	private MIDletState ac;

	final String getName() {
		return Emulator.getMidletName();
	}

	final String getIconName() {
		return Emulator.iconPath;
	}

	final Class getCls() {
		try {
			return Class.forName(Emulator.midletClassName, true, (ClassLoader) Emulator.customClassLoader);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	final void setInstance(MIDletState paramMIDletState) {
		this.ac = paramMIDletState;
	}

	final void k() {
		this.ac = null;
	}

	final MIDletState getInstance() {
		return this.ac;
	}
}
