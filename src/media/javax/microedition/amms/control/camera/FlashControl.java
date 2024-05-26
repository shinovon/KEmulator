package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;

public abstract interface FlashControl extends Control {
	public static final int OFF = 1;
	public static final int AUTO = 2;
	public static final int AUTO_WITH_REDEYEREDUCE = 3;
	public static final int FORCE = 4;
	public static final int FORCE_WITH_REDEYEREDUCE = 5;
	public static final int FILLIN = 6;

	public abstract int[] getSupportedModes();

	public abstract void setMode(int paramInt);

	public abstract int getMode();

	public abstract boolean isFlashReady();
}
