package org.pigler.api;

import com.nokia.mid.ui.SoftNotificationImpl;

import javax.microedition.lcdui.Image;
import emulator.graphics2D.awt.d;
import java.awt.*;

/**
 * Pigler Notifications Java API
 *
 * @version 1.2, API level 3
 *
 * @author Shinovon
 */
public class PiglerAPI {

    private String appName;
    private IPiglerTapHandler listener;

    public PiglerAPI() {
    }

    public int init() {
        if(!SystemTray.isSupported()) {
            throw new PiglerException("Not supported");
        }
        appName = "KEmulator";
        SoftNotificationImpl.setPigler(this);
        return 0;
    }

    public int init(String appName) throws Exception {
        init();
        this.appName = appName;
        return 0;
    }

    public int getAPIVersion() {
        return 3;
    }

    public int createNotification(String title, String text, Image icon, boolean removeOnTap) throws Exception {
        if(icon != null)
            SoftNotificationImpl.trayIcon.setImage(((d) icon.getImpl()).getBufferedImage());
        SoftNotificationImpl.trayIcon.displayMessage(title, text, TrayIcon.MessageType.NONE);
        return 2100;
    }

    public void updateNotification(int uid, String title, String text, Image icon) throws Exception {
        updateNotification(uid, title, text);
        updateNotification(uid, icon);
    }

    public void updateNotification(int uid, String title, String text) throws Exception {
        SoftNotificationImpl.trayIcon.displayMessage(title, text, TrayIcon.MessageType.NONE);
    }

    public void updateNotification(int uid, Image icon) throws Exception {
        if(icon != null)
            SoftNotificationImpl.trayIcon.setImage(((d) icon.getImpl()).getBufferedImage());
    }

    public void removeNotification(int uid) throws Exception {
    }

    public int removeAllNotifications() {
        return 0;
    }

    public int getLastTappedNotification() {
        return 0;
    }

    public void setRemoveNotificationOnTap(int uid, boolean remove) throws Exception {
    }

    public void setLaunchAppOnTap(int uid, boolean launch) throws Exception {
    }

    public void setListener(IPiglerTapHandler listener) {
        this.listener = listener;
    }

    public void close() {
        SoftNotificationImpl.setPigler(null);
    }

    public String getAppName() {
        return appName;
    }

    public int getMaxNotificationsCount() {
        return 1;
    }

    public int getNotificationsCount() {
        return 0;
    }

    public void _callback(int i) {
        if(listener != null)
            listener.handleNotificationTap(2100);
    }
}
