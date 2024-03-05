package javax.microedition.amms.control;

import javax.microedition.media.Control;

public abstract interface EffectOrderControl extends Control {
    public abstract int setEffectOrder(EffectControl paramEffectControl, int paramInt);

    public abstract int getEffectOrder(EffectControl paramEffectControl);

    public abstract EffectControl[] getEffectOrders();
}
