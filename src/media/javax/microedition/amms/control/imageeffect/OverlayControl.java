package javax.microedition.amms.control.imageeffect;

import javax.microedition.amms.control.EffectControl;

public abstract interface OverlayControl extends EffectControl {
    public abstract int insertImage(Object paramObject, int paramInt1, int paramInt2, int paramInt3)
            throws IllegalArgumentException;

    public abstract int insertImage(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
            throws IllegalArgumentException;

    public abstract void removeImage(Object paramObject);

    public abstract Object getImage(int paramInt);

    public abstract int numberOfImages();

    public abstract void clear();
}
