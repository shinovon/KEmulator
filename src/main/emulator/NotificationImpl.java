package emulator;

import emulator.custom.ResourceManager;
import ru.nnproject.kemulator.notificationapi.AbstractNotification;
import ru.nnproject.kemulator.notificationapi.NotificationException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class NotificationImpl implements AbstractNotification {

	private SystemTray tray;
	private TrayIcon trayIcon;
	private String name;

	public NotificationImpl(String name) {
		this.name = name;
	}

	@Override
	public void createTrayIcon(String tooltip, String imgpath) throws NotificationException {
		try {
			tray = SystemTray.getSystemTray();
			Image image;
			if (imgpath != null) {
				image = ImageIO.read(ResourceManager.getResourceAsStream(imgpath));
			} else {
				image = ImageIO.read(getClass().getResourceAsStream("/res/icon"));
			}
			trayIcon = new TrayIcon(image, tooltip);
			trayIcon.setImageAutoSize(true);
			tray.add(trayIcon);
		} catch (Exception ex) {
			throw new NotificationException("Failed to create tray icon " + ex.toString());
		}
	}

	@Override
	public void displayMessage(String title, String subtitle, int type) throws NotificationException {
		if (title == null) {
			title = name;
		}
		if (subtitle == null) {
			throw new NotificationException("Subtitle cannot be null");
		}
		MessageType mt;
		switch (type) {
			case 0:
				mt = MessageType.INFO;
				break;
			case 1:
				mt = MessageType.WARNING;
				break;
			case 2:
				mt = MessageType.ERROR;
				break;
			case 3:
				mt = MessageType.NONE;
				break;
			default:
				throw new NotificationException("Invalid message type");
		}
		trayIcon.displayMessage(title, subtitle, mt);
	}

	@Override
	public void displayMessage(String text, int type) throws NotificationException {
		displayMessage(null, text, type);
	}

}
