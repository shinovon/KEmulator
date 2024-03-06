package javax.microedition.lcdui;

import java.util.*;
import javax.microedition.midlet.*;

import emulator.*;

public class Display {
    public static final int LIST_ELEMENT = 1;
    public static final int CHOICE_GROUP_ELEMENT = 2;
    public static final int ALERT = 3;
    public static final int COLOR_BACKGROUND = 0;
    public static final int COLOR_FOREGROUND = 1;
    public static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;
    public static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;
    public static final int COLOR_BORDER = 4;
    public static final int COLOR_HIGHLIGHTED_BORDER = 5;
    static Displayable current;
    static Hashtable displays;
    private MIDlet midlet;

    public Display() {
        super();
    }

    public Display(MIDlet midlet) {
        this.midlet = midlet;
    }

    public boolean isColor() {
        return true;
    }

    public int numColors() {
        return Integer.MAX_VALUE;
    }

    public int numAlphaLevels() {
        return 256;
    }

    public Displayable getCurrent() {
        return Display.current;
    }

    public void setCurrent(Displayable d) {
        if (d == Display.current) {
            return;
        }
        if (Display.current != null) {
            Display.current.defocus();
        }
        if ((Display.current = d) != null) {
            if (d instanceof Canvas) {
                if (Settings.aBoolean1274) {
                    ((Canvas) d).setFullScreenMode(true);
                }
                Emulator.setCanvas((Canvas) d);
                Emulator.getEventQueue().queue(15);
                Emulator.getEventQueue().queueRepaint();
            } else if (d instanceof Screen) {
                Emulator.setScreen((Screen) d);
                Emulator.getEventQueue().queue(4);
                ((Screen) d).shown();
                if (d instanceof TextBox) {
                    ((TextBox) d).focusCaret();
                }
            }
            d.updateCommands();
        }
    }

    public static Display getDisplay(final MIDlet midlet) {
        if (Display.displays.get(midlet) == null) {
            Display.displays.put(midlet, new Display(midlet));
        }
        return (Display) Display.displays.get(midlet);
    }

    public void callSerially(final Runnable run) {
        Emulator.getEventQueue().callSerially(run);
    }

    public boolean flashBacklight(int n) {
        return false;
    }

    public boolean flashBacklight(final long n) {
        return false;
    }

    public boolean vibrate(final int n) {
        if (n == 0)
            Emulator.getEmulator().getScreen().stopVibra();
        else
            Emulator.getEmulator().getScreen().startVibra(n);
        return true;
    }

    public int getBestImageWidth(final int n) {
        switch (n) {
            case Display.LIST_ELEMENT:
            case Display.CHOICE_GROUP_ELEMENT:
            case Display.ALERT:
                return 16;
        }
        return 0;
    }

    public int getBestImageHeight(final int n) {
        switch (n) {
            case Display.LIST_ELEMENT:
            case Display.CHOICE_GROUP_ELEMENT:
            case Display.ALERT:
                return 16;
        }
        return 0;
    }

    public int getBorderStyle(final boolean b) {
        return 0;
    }

    public int getColor(final int n) {
        return 0;
    }

    public void dispose() {
        Display.displays.put(midlet, null);
    }

    public void setCurrent(final Alert alert, final Displayable ret) {
        if (alert == null || ret == null) {
            throw new NullPointerException();
        }
        if (ret instanceof Alert) {
            throw new IllegalArgumentException();
        }
        if (Display.current != null) {
            Display.current.defocus();
        }
        Display.current = alert;
        alert.lastDisplayed = ret;
        Emulator.setScreen(alert);
        Emulator.getEventQueue().queue(4);
        alert.updateCommands();
    }

    public void setCurrentItem(final Item item) {
        if (item == null) {
            throw new NullPointerException();
        }
        if (item.screen == null || item.screen instanceof Alert) {
            throw new IllegalArgumentException();
        }
        this.setCurrent(item.screen);
    }

    static {
        Display.displays = new Hashtable();
    }
}
