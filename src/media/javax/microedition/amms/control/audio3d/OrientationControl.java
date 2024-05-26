package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public abstract interface OrientationControl extends Control {
	public abstract int[] getOrientationVectors();

	public abstract void setOrientation(int[] paramArrayOfInt1, int[] paramArrayOfInt2) throws IllegalArgumentException;

	public abstract void setOrientation(int paramInt1, int paramInt2, int paramInt3);
}
