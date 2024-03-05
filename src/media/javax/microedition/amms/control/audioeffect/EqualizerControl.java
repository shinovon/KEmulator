package javax.microedition.amms.control.audioeffect;

import javax.microedition.amms.control.EffectControl;

public abstract interface EqualizerControl
        extends EffectControl {
    public static final int UNDEFINED = -1004;

    public abstract int getMinBandLevel();

    public abstract int getMaxBandLevel();

    public abstract void setBandLevel(int paramInt1, int paramInt2)
            throws IllegalArgumentException;

    public abstract int getBandLevel(int paramInt)
            throws IllegalArgumentException;

    public abstract int getNumberOfBands();

    public abstract int getCenterFreq(int paramInt)
            throws IllegalArgumentException;

    public abstract int getBand(int paramInt);

    public abstract int setBass(int paramInt)
            throws IllegalArgumentException;

    public abstract int setTreble(int paramInt)
            throws IllegalArgumentException;

    public abstract int getBass();

    public abstract int getTreble();
}
