package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;

public abstract interface SnapshotControl extends Control {
    public static final String SHOOTING_STOPPED = "SHOOTING_STOPPED";
    public static final String STORAGE_ERROR = "STORAGE_ERROR";
    public static final String WAITING_UNFREEZE = "WAITING_UNFREEZE";
    public static final int FREEZE = -2;
    public static final int FREEZE_AND_CONFIRM = -1;

    public abstract void setDirectory(String paramString);

    public abstract String getDirectory();

    public abstract void setFilePrefix(String paramString);

    public abstract String getFilePrefix();

    public abstract void setFileSuffix(String paramString);

    public abstract String getFileSuffix();

    public abstract void start(int paramInt) throws SecurityException;

    public abstract void stop();

    public abstract void unfreeze(boolean paramBoolean);
}
