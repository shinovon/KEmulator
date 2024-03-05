package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public abstract interface ExposureControl extends Control {
    public abstract int[] getSupportedFStops();

    public abstract int getFStop();

    public abstract void setFStop(int paramInt) throws MediaException;

    public abstract int getMinExposureTime();

    public abstract int getMaxExposureTime();

    public abstract int getExposureTime();

    public abstract int setExposureTime(int paramInt) throws MediaException;

    public abstract int[] getSupportedISOs();

    public abstract int getISO();

    public abstract void setISO(int paramInt) throws MediaException;

    public abstract int[] getSupportedExposureCompensations();

    public abstract int getExposureCompensation();

    public abstract void setExposureCompensation(int paramInt) throws MediaException;

    public abstract int getExposureValue();

    public abstract String[] getSupportedLightMeterings();

    public abstract void setLightMetering(String paramString);

    public abstract String getLightMetering();
}
