package com.mascotcapsule.micro3d.v3;

public class Light {
	private Vector3D mDirVector;
	private int mDirIntensity;
	private int mAmbIntensity;

	public Light() {
		mDirVector = new Vector3D(0, 0, 4096);
		mDirIntensity = 4096;
		mAmbIntensity = 0;
	}

	public Light(Vector3D paramVector3D, int paramInt1, int paramInt2) {
		if (paramVector3D == null) {
			throw new NullPointerException();
		}
		mDirVector = paramVector3D;
		mDirIntensity = paramInt1;
		mAmbIntensity = paramInt2;
	}

	public final int getDirIntensity() {
		return getParallelLightIntensity();
	}

	public final int getParallelLightIntensity() {
		return mDirIntensity;
	}

	public final void setDirIntensity(int paramInt) {
		setParallelLightIntensity(paramInt);
	}

	public final void setParallelLightIntensity(int paramInt) {
		mDirIntensity = paramInt;
	}

	public final int getAmbIntensity() {
		return getAmbientIntensity();
	}

	public final int getAmbientIntensity() {
		return mAmbIntensity;
	}

	public final void setAmbIntensity(int paramInt) {
		setAmbientIntensity(paramInt);
	}

	public final void setAmbientIntensity(int paramInt) {
		mAmbIntensity = paramInt;
	}

	public final Vector3D getDirection() {
		return getParallelLightDirection();
	}

	public final Vector3D getParallelLightDirection() {
		return mDirVector;
	}

	public final void setDirection(Vector3D paramVector3D) {
		setParallelLightDirection(paramVector3D);
	}

	public final void setParallelLightDirection(Vector3D paramVector3D) {
		if (paramVector3D == null) {
			throw new NullPointerException();
		}
		mDirVector = paramVector3D;
	}
}