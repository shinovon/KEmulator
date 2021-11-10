package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public abstract interface CameraControl extends Control {
	public static final int ROTATE_LEFT = 2;
	public static final int ROTATE_RIGHT = 3;
	public static final int ROTATE_NONE = 1;
	public static final int UNKNOWN = -1004;

	public abstract int getCameraRotation();

	public abstract void enableShutterFeedback(boolean paramBoolean) throws MediaException;

	public abstract boolean isShutterFeedbackEnabled();

	public abstract String[] getSupportedExposureModes();

	public abstract void setExposureMode(String paramString);

	public abstract String getExposureMode();

	public abstract int[] getSupportedVideoResolutions();

	public abstract int[] getSupportedStillResolutions();

	public abstract void setVideoResolution(int paramInt);

	public abstract void setStillResolution(int paramInt);

	public abstract int getVideoResolution();

	public abstract int getStillResolution();
}
