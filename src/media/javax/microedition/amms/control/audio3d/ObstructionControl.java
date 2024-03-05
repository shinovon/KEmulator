package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public abstract interface ObstructionControl extends Control {
    public abstract int getHFLevel();

    public abstract int getLevel();

    public abstract void setHFLevel(int paramInt);

    public abstract void setLevel(int paramInt);
}
