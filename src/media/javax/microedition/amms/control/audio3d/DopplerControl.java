package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public abstract interface DopplerControl extends Control {
    public abstract int[] getVelocityCartesian();

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean paramBoolean);

    public abstract void setVelocityCartesian(int paramInt1, int paramInt2, int paramInt3);

    public abstract void setVelocitySpherical(int paramInt1, int paramInt2, int paramInt3);
}
