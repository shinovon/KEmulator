package emulator;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

import javax.imageio.ImageIO;

import ru.nnproject.kemulator.notificationapi.AbstractNotification;
import ru.nnproject.kemulator.notificationapi.NotificationException;

public class NotificationImpl implements AbstractNotification {

	private String name;

	public NotificationImpl(String name) {
		this.name = name;
	}

	public void createTrayIcon(String tooltip, String imgpath) throws NotificationException {
	}

	public void displayMessage(String title, String subtitle, int type) throws NotificationException {
	}

	public void displayMessage(String text, int type) throws NotificationException {
		displayMessage(null, text, type);
	}

}
