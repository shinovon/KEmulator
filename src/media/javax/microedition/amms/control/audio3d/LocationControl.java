package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public abstract interface LocationControl extends Control {
    public abstract int[] getCartesian();

    public abstract void setCartesian(int paramInt1, int paramInt2, int paramInt3);

    public abstract void setSpherical(int paramInt1, int paramInt2, int paramInt3);
}
