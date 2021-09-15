package com.nokia.mid.impl.isa.ui;

import java.util.Timer;
import javax.microedition.midlet.MIDlet;

public abstract interface MIDletAccess {
	public abstract void registerTimer(MIDlet paramMIDlet, Timer paramTimer);

	public abstract void deregisterTimer(MIDlet paramMIDlet, Timer paramTimer);

	public abstract void destroyMIDlet(MIDlet paramMIDlet);

	public abstract String getMIDletName(MIDlet paramMIDlet);

	public abstract DisplayAccess getDisplayAccessor();
}
