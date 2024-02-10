package net.rim.device.api.system;

import net.rim.device.api.ui.UiApplication;

public class Application {
    private static Application instance = null;
    private KeyListener keyListener = null;

    public static Application getApplication() {
        if (instance == null) {
            instance = new UiApplication();
        }
        emulator.Settings.aBoolean1274 = true;
        return instance;
    }

    public void invokeLater(Runnable paramRunnable) {
        if (paramRunnable != null) {
            new Thread(paramRunnable).start();
        }
    }

    public void addKeyListener(KeyListener paramKeyListener) {
        this.keyListener = paramKeyListener;
    }

    public static int internalKeyPress(int paramInt) {
        if (instance != null) {
            if (instance.keyListener != null) {
                instance.keyListener.keyDown(paramInt, 0);
            }
            if (paramInt == 9) {
                return 119;
            }
            if (paramInt == 10) {
                return 114;
            }
            if (paramInt == 11) {
                return 122;
            }
            if (paramInt == 12) {
                return 99;
            }
            if (paramInt == 1) {
                return 101;
            }
            if (paramInt == 2) {
                return 115;
            }
            if (paramInt == 5) {
                return 102;
            }
            if (paramInt == 8) {
                return 100;
            }
            if (paramInt == 6) {
                return 88;
            }
        }
        return paramInt;
    }

    public static int internalKeyRelease(int paramInt) {
        if ((instance != null) && (instance.keyListener != null)) {
            instance.keyListener.keyUp(paramInt, 0);
        }
        return paramInt;
    }
}
