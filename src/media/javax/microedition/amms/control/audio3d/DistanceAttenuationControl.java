package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public abstract interface DistanceAttenuationControl extends Control {
    public abstract int getMaxDistance();

    public abstract int getMinDistance();

    public abstract boolean getMuteAfterMax();

    public abstract int getRolloffFactor();

    public abstract void setParameters(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3);
}
