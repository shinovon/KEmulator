package javax.microedition.amms.control;

import javax.microedition.media.Control;

public abstract interface PanControl extends Control {
    public abstract int setPan(int paramInt);

    public abstract int getPan();
}
