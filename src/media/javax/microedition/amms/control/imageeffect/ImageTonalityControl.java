package javax.microedition.amms.control.imageeffect;

import javax.microedition.amms.control.EffectControl;

public abstract interface ImageTonalityControl extends EffectControl {
    public static final int AUTO = -1000;
    public static final int NEXT = -1001;
    public static final int PREVIOUS = -1002;

    public abstract int setBrightness(int paramInt);

    public abstract int getBrightness();

    public abstract int getBrightnessLevels();

    public abstract int setContrast(int paramInt);

    public abstract int getContrast();

    public abstract int getContrastLevels();

    public abstract int setGamma(int paramInt);

    public abstract int getGamma();

    public abstract int getGammaLevels();
}
