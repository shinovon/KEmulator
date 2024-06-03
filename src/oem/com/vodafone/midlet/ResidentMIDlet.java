package com.vodafone.midlet;

import javax.microedition.midlet.MIDlet;

public abstract class ResidentMIDlet extends MIDlet {
	public static final int CBS = 1;
	public static final int MMS = 2;
	public static final int SMS = 3;
	public static final int WAP_PUSH = 4;
	public static final int DELIVERY_CONF = 5;

	public abstract void dropped();

	public abstract void notice(String paramString);

	public abstract void received(String paramString1, String paramString2, int paramInt);

	public abstract void ring(String paramString1, String paramString2);
}
