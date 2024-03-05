package javax.microedition.amms.control.imageeffect;

import javax.microedition.amms.control.EffectControl;

public abstract interface ImageTransformControl extends EffectControl {
    public abstract int getSourceWidth();

    public abstract int getSourceHeight();

    public abstract void setSourceRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

    public abstract void setTargetSize(int paramInt1, int paramInt2, int paramInt3);
}
