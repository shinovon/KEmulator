package javax.microedition.amms.control;

import javax.microedition.media.Control;

public abstract interface PriorityControl extends Control {
    public abstract int getPriority();

    public abstract void setPriority(int paramInt);
}
