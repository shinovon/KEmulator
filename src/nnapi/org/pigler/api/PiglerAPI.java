package org.pigler.api;

import com.nokia.mid.ui.SoftNotificationImpl;

import javax.microedition.lcdui.Image;

import emulator.Emulator;
import emulator.graphics2D.awt.d;
import emulator.ui.swt.EmulatorImpl;
import emulator.ui.swt.EmulatorScreen;
import org.eclipse.swt.widgets.Shell;

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
    private boolean launch = true;
    private boolean created;
    private boolean tapped;

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
        created = true;
        SoftNotificationImpl.trayIcon.displayMessage(title, text, TrayIcon.MessageType.NONE);
        updateNotification(2100, icon);
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
            try {
                SoftNotificationImpl.trayIcon.setImage(((d) icon.getImpl()).getBufferedImage());
                SoftNotificationImpl.trayIcon.setImageAutoSize(true);
            } catch (Exception e) {
            }
    }

    public void removeNotification(int uid) throws Exception {
    }

    public int removeAllNotifications() {
        return 0;
    }

    public int getLastTappedNotification() {
        if(tapped)
            return 2100;
        return 0;
    }

    public void setRemoveNotificationOnTap(int uid, boolean remove) throws Exception {
    }

    public void setLaunchAppOnTap(int uid, boolean launch) throws Exception {
        if(uid != 2100) throw new PiglerException("Invalid uid");
        this.launch = launch;
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
        return created ? 1 : 0;
    }

    public void _callback(int i) {
        tapped = true;
        if(launch)
            try {
                EmulatorImpl.asyncExec(new Runnable() {
                    public void run() {
                        try {
                            Shell shell = ((EmulatorScreen) Emulator.getEmulator().getScreen()).getShell();
                            shell.setMinimized(false);
                            shell.setActive();
                        } catch (Exception e) {
                        }
                    }
                });
            } catch (Exception e) {}
        if(!created) return;
        created = false;
        if(listener != null)
            listener.handleNotificationTap(2100);
    }
}
