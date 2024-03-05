package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public abstract interface FocusControl extends Control {
    public static final int AUTO = -1000;
    public static final int AUTO_LOCK = -1005;
    public static final int NEXT = -1001;
    public static final int PREVIOUS = -1002;
    public static final int UNKNOWN = -1004;

    public abstract int setFocus(int paramInt) throws MediaException;

    public abstract int getFocus();

    public abstract int getMinFocus();

    public abstract int getFocusSteps();

    public abstract boolean isManualFocusSupported();

    public abstract boolean isAutoFocusSupported();

    public abstract boolean isMacroSupported();

    public abstract void setMacro(boolean paramBoolean) throws MediaException;

    public abstract boolean getMacro();
}
