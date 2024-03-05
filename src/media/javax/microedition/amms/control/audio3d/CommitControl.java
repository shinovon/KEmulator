package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public abstract interface CommitControl extends Control {
    public abstract void commit();

    public abstract boolean isDeferred();

    public abstract void setDeferred(boolean paramBoolean);
}
