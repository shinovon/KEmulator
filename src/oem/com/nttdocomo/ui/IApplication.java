package com.nttdocomo.ui;

import java.util.*;

import emulator.*;
import com.nttdocomo.ui.maker.*;

public abstract class IApplication {
	public static final int LAUNCHED_FROM_MENU = 0;
	public static final int LAUNCHED_AFTER_DOWNLOAD = 1;
	public static final int LAUNCHED_FROM_TIMER = 2;
	public static final int LAUNCHED_AS_CONCIERGE = 3;
	public static final int LAUNCHED_FROM_EXT = 4;
	public static final int LAUNCHED_FROM_BROWSER = 5;
	public static final int LAUNCHED_FROM_MAILER = 6;
	public static final int LAUNCHED_FROM_IAPPLI = 7;
	public static final int LAUNCHED_FROM_LAUNCHER = 8;
	public static final int LAUNCHED_AS_ILET = 9;
	public static final int LAUNCHED_FROM_SMS = 10;
	public static final int SUSPEND_BY_NATIVE = 1;
	public static final int SUSPEND_BY_IAPP = 2;
	public static final int SUSPEND_PACKETIN = 256;
	public static final int SUSPEND_CALL_OUT = 512;
	public static final int SUSPEND_CALL_IN = 1024;
	public static final int SUSPEND_MAIL_SEND = 2048;
	public static final int SUSPEND_MAIL_RECEIVE = 4096;
	public static final int SUSPEND_SCHEDULE_NOTIFY = 16384;
	public static final int LAUNCH_BROWSER = 1;
	public static final int LAUNCH_VERSIONUP = 2;
	public static final int LAUNCH_IAPPLI = 3;
	public static final int LAUNCH_AS_LAUNCHER = 4;

	public final String[] getArgs() {
		final String appProperty = Emulator.getEmulator().getAppProperty("AppParam");
		if (appProperty == null) {
			return new String[0];
		}
		final Vector vector = new Vector<String>();
		boolean b = false;
		int n = 0;
		for (int i = 0; i < appProperty.length(); ++i) {
			if (appProperty.charAt(i) == '\"' || appProperty.charAt(i) == '[' || appProperty.charAt(i) == ']') {
				b = !b;
			} else if (appProperty.charAt(i) == ' ' && !b) {
				vector.add(appProperty.substring(n, i));
				n = i + 1;
			}
		}
		vector.add(appProperty.substring(n, appProperty.length()));
		final String[] array = new String[vector.size()];
		for (int j = 0; j < vector.size(); ++j) {
			array[j] = (String) vector.get(j);
			System.out.print("'" + array[j] + "', ");
		}
		System.out.println();
		return array;
	}

	public final String getParameter(final String s) {
		return Emulator.getEmulator().getAppProperty(s);
	}

	public final String getSourceURL() {
		return null;
	}

	public final int getLaunchType() {
		return 0;
	}

	public static final IApplication getCurrentApp() {
		return IApplicationMIDlet.pIApplication;
	}

	public abstract void start();

	public void resume() {
	}

	public final void terminate() {
		Emulator.getMIDlet().notifyDestroyed();
	}

	public final void launch(final int n, final String[] array) {
	}

	public int getSuspendInfo() {
		return 0;
	}
}
