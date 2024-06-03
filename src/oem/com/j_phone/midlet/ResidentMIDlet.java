package com.j_phone.midlet;

import com.j_phone.system.MailListener;
import com.j_phone.system.RingStateListener;
import com.j_phone.system.ScheduledAlarmListener;
import com.j_phone.system.TelephonyListener;
import javax.microedition.midlet.MIDlet;

public abstract class ResidentMIDlet extends MIDlet implements TelephonyListener, MailListener, ScheduledAlarmListener, RingStateListener
{
	public abstract void ring(String paramString1, String paramString2);

	public abstract void ignored();

	public abstract void received(String paramString1, String paramString2, int paramInt);

	public abstract void notice(String paramString);

	public abstract void ringStarted();

	public abstract void ringStopped();
}
