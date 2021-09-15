package ru.nnproject.kemulator.notificationapi;

import java.awt.SystemTray;

import emulator.NotificationImpl;

/**
 * @deprecated Replaced by NokiaUI
 */
public class NotificationAPI {
	
	public final static int INFO = 0;
	public final static int WARNING = 1;
	public final static int ERROR = 2;
	public final static int NONE = 3;

	public static AbstractNotification getInstance(String name) {
		return new NotificationImpl(name);
	}
	
	public static boolean isSupported() {
		return SystemTray.isSupported();
	}
}
