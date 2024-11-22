package com.nokia.mid.ui;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import emulator.Emulator;
import org.pigler.api.PiglerAPI;

public class SoftNotificationImpl extends SoftNotification {

	private static PiglerAPI pigler;
	private SoftNotificationListener[] iListener;
	private String groupText;
	private String text;
	private boolean hasImage;
	public static TrayIcon trayIcon;
	public static SystemTray tray;
	private static SoftNotificationImpl lastInst;

	public SoftNotificationImpl(int aNotificationId) {
		initialize(aNotificationId);
	}

	public SoftNotificationImpl() {
		initialize(-1);
	}

	public static void setPigler(PiglerAPI piglerAPI) {
		if (pigler != null && piglerAPI != null) throw new IllegalStateException();
		pigler = piglerAPI;
	}

	protected void initialize(int aNotificationId) {
		lastInst = this;
		this.iListener = new SoftNotificationListener[1];
	}

	private void notificationCallback(int aEventArg) {
		synchronized (this.iListener) {
			SoftNotificationListener listener = this.iListener[0];
			if (listener != null) {
				if (aEventArg == 1) {
					listener.notificationSelected(this);
				} else if (aEventArg == 2) {
					listener.notificationDismissed(this);
				}
			}
		}
	}

	public int getId() {
		return -1;
	}

	public void post() throws SoftNotificationException {
		try {
			trayIcon.displayMessage(groupText, text == null ? "" : text, hasImage ? MessageType.NONE : MessageType.INFO);
		} catch (Exception e) {
			throw new SoftNotificationException(e.toString());
		}
	}

	public void remove() throws SoftNotificationException {
	}

	public void setListener(SoftNotificationListener aListener) {
		synchronized (this.iListener) {
			this.iListener[0] = aListener;
		}
	}

	public void setText(String aText, String aGroupText) throws SoftNotificationException {
		text = aText;
		groupText = aGroupText;
	}

	public void setSoftkeyLabels(String aSoftkey1Label, String aSoftkey2Label) throws SoftNotificationException {

	}

	public void setImage(byte[] aImageData) throws SoftNotificationException {
		try {
			trayIcon.setImage(ImageIO.read(new ByteArrayInputStream(aImageData)));
		} catch (IOException e) {
			throw new SoftNotificationException(e.toString());
		}
		hasImage = true;
		trayIcon.setImageAutoSize(true);
	}

	static {
		try {
			tray = SystemTray.getSystemTray();
			trayIcon = new TrayIcon(ImageIO.read(SoftNotification.class.getResourceAsStream("/res/icon")), Emulator.getMidletName());
			trayIcon.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					action(1);
				}
			});
			tray.add(trayIcon);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected static void action(int i) {
		if (lastInst != null)
			lastInst.notificationCallback(i);
		else if (pigler != null)
			pigler._callback(i);
	}
}
