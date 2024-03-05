package javax.microedition.amms.control.audioeffect;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public abstract interface ReverbSourceControl
        extends Control {
    public static final int DISCONNECT = Integer.MAX_VALUE;

    public abstract void setRoomLevel(int paramInt)
            throws MediaException;

    public abstract int getRoomLevel();
}
