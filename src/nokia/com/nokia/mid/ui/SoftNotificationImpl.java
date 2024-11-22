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
	}

	static {
	}

	protected static void action(int i) {
		if (lastInst != null)
			lastInst.notificationCallback(i);
		else if (pigler != null)
			pigler._callback(i);
	}
}
