package com.nokia.mid.impl.isa.ui;

import emulator.Emulator;

import java.util.Timer;
import java.util.Vector;
import javax.microedition.midlet.MIDlet;

final class MIDletManager implements MIDletAccess {
	static MIDletManager inst = new MIDletManager();
	private static DisplayAccess iI;
	private Vector timers;
	private short iO;
	private MIDletRTInfo iL;

	private MIDletManager() {
		this.timers = new Vector(10);
	}

	static void s_setDisplayAccessor(DisplayAccess paramDisplayAccess) {
		if (iI == null) {
			iI = paramDisplayAccess;
			return;
		}
		throw new SecurityException();
	}

	public final DisplayAccess getDisplayAccessor() {
		return iI;
	}

	static MIDlet getCurrentMIDlet() {
		return Emulator.getMIDlet();
	}

	public final void exit() {
		System.exit(0);
	}

	public final void registerTimer(MIDlet paramMIDlet, Timer paramTimer) {
		if (!this.timers.contains(paramTimer)) {
			this.timers.addElement(paramTimer);
		}
	}

	public final void deregisterTimer(MIDlet paramMIDlet, Timer paramTimer) {
		if (this.timers.contains(paramTimer)) {
			this.timers.removeElement(paramTimer);
			if ((this.iO = (short) (this.iO + 1)) > 10) {
				this.iO = 0;
				if (this.timers.capacity() > 10) {
					this.timers.trimToSize();
					this.timers.ensureCapacity(10);
				}
			}
		}
	}

	@Override
	public void destroyMIDlet(MIDlet paramMIDlet) {
		paramMIDlet.notifyDestroyed();

	}

	@Override
	public String getMIDletName(MIDlet paramMIDlet) {
		return Emulator.getMidletName();
	}

	public static String s_getAppProperty(String paramString) {
		return Emulator.getEmulator().getAppProperty(paramString);
	}

	public final MIDletRTInfo c(MIDletState paramMIDletState) {
		if (this.iL != null) {
			Class localClass;
			if ((localClass = paramMIDletState.getMIDlet().getClass()).equals(this.iL.getCls())) {
				if (this.iL.getInstance() == null) {
					this.iL.setInstance(paramMIDletState);
					return this.iL;
				}
			}
		}
		throw new SecurityException("MIDletManager ERROR: Illegal attempt to construct "
				+ (paramMIDletState == null ? "NULL" : paramMIDletState.getMIDlet().toString()));
	}
}
