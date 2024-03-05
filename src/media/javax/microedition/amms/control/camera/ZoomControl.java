package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;

public abstract interface ZoomControl extends Control {
    public static final int NEXT = -1001;
    public static final int PREVIOUS = -1002;
    public static final int UNKNOWN = -1004;

    public abstract int setOpticalZoom(int paramInt);

    public abstract int getOpticalZoom();

    public abstract int getMaxOpticalZoom();

    public abstract int getOpticalZoomLevels();

    public abstract int getMinFocalLength();

    public abstract int setDigitalZoom(int paramInt);

    public abstract int getDigitalZoom();

    public abstract int getMaxDigitalZoom();

    public abstract int getDigitalZoomLevels();
}
