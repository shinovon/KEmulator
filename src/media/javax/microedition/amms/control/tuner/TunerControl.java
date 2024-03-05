package javax.microedition.amms.control.tuner;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public abstract interface TunerControl extends Control {
    public static final int MONO = 1;
    public static final int STEREO = 2;
    public static final int AUTO = 3;
    public static final String MODULATION_FM = "fm";
    public static final String MODULATION_AM = "am";

    public abstract int getMinFreq(String paramString);

    public abstract int getMaxFreq(String paramString);

    public abstract int setFrequency(int paramInt, String paramString);

    public abstract int getFrequency();

    public abstract int seek(int paramInt, String paramString, boolean paramBoolean) throws MediaException;

    public abstract boolean getSquelch();

    public abstract void setSquelch(boolean paramBoolean) throws MediaException;

    public abstract String getModulation();

    public abstract int getSignalStrength() throws MediaException;

    public abstract int getStereoMode();

    public abstract void setStereoMode(int paramInt);

    public abstract int getNumberOfPresets();

    public abstract void usePreset(int paramInt);

    public abstract void setPreset(int paramInt);

    public abstract void setPreset(int paramInt1, int paramInt2, String paramString, int paramInt3);

    public abstract int getPresetFrequency(int paramInt);

    public abstract String getPresetModulation(int paramInt);

    public abstract int getPresetStereoMode(int paramInt) throws MediaException;

    public abstract String getPresetName(int paramInt);

    public abstract void setPresetName(int paramInt, String paramString);
}
