package net.rim.device.api.ui;

import net.rim.device.api.system.Application;

public class UiApplication
        extends Application {
    public static UiApplication getUiApplication() {
        return (UiApplication) Application.getApplication();
    }
}
