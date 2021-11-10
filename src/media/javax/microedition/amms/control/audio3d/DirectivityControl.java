package javax.microedition.amms.control.audio3d;

public abstract interface DirectivityControl extends OrientationControl {
	public abstract int[] getParameters();

	public abstract void setParameters(int paramInt1, int paramInt2, int paramInt3);
}
