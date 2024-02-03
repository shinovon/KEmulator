package javax.microedition.m3g;

public class Camera extends Node {
	public static final int GENERIC = 48;
	public static final int PARALLEL = 49;
	public static final int PERSPECTIVE = 50;

	Camera(int n) {
		super(n);
	}

	public Camera() {
		this(create());
		Engine.addJavaPeer(this.swerveHandle, this);
		this.ii = (getClass() != Camera.class);
	}

	private static native int create();

	public int getProjection(float[] array) {
		return getProjectionParams(array);
	}

	private native int getProjectionParams(float[] paramArrayOfFloat);

	public int getProjection(Transform transform) {
		return getProjectionTransform(transform);
	}

	private native int getProjectionTransform(Transform paramTransform);

	public native void setGeneric(Transform paramTransform);

	public native void setParallel(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

	public native void setPerspective(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
}
