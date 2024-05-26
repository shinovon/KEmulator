package ru.nnproject.kemulator.notificationapi;

/**
 * @deprecated Replaced by NokiaUI
 */
public interface AbstractNotification {

	public void createTrayIcon(String tooltip, String imgpath) throws NotificationException;

	public void displayMessage(String title, String subtitle, int type) throws NotificationException;

	public void displayMessage(String text, int type) throws NotificationException;

}
