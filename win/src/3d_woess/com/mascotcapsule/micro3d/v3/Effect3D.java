package com.mascotcapsule.micro3d.v3;

public class Effect3D {
	public static final int NORMAL_SHADING = 0;
	public static final int TOON_SHADING = 1;
	private boolean isBlendEnable;
	private int mShading;
	private int mHigh;
	private int mLow;
	private int mThreshold;
	private Light mLight;
	private Texture mTexure;

	public Effect3D(Light light, int shading, boolean isEnableTrans, Texture tex) {
		if ((shading != 0) && (shading != 1)) {
			throw new IllegalArgumentException();
		}
		if (tex != null) {
			if (tex.isForModel) {
				throw new IllegalArgumentException();
			}
			if (tex.nPointer == 0) {
				throw new RuntimeException("Texture already disposed");
			}
		}
		this.mLight = light;
		this.mShading = shading;
		this.isBlendEnable = isEnableTrans;
		this.mTexure = tex;
	}

	public final Light getLight() {
		return this.mLight;
	}

	public final int getShading() {
		return this.mShading;
	}

	public final int getShadingType() {
		return this.mShading;
	}

	public final Texture getSphereMap() {
		return this.mTexure;
	}

	public final Texture getSphereTexture() {
		return this.mTexure;
	}

	public final int getThreshold() {
		return this.mThreshold;
	}

	public final int getThresholdHigh() {
		return this.mHigh;
	}

	public final int getThresholdLow() {
		return this.mLow;
	}

	public final int getToonHigh() {
		return this.mHigh;
	}

	public final int getToonLow() {
		return this.mLow;
	}

	public final int getToonThreshold() {
		return this.mThreshold;
	}

	public final boolean isSemiTransparentEnabled() {
		return this.isBlendEnable;
	}

	public final boolean isTransparency() {
		return this.isBlendEnable;
	}

	public final void setLight(Light light) {
		this.mLight = light;
	}

	public final void setSemiTransparentEnabled(boolean isEnable) {
		this.isBlendEnable = isEnable;
	}

	public final void setShading(int shading) {
		setShadingType(shading);
	}

	public final void setShadingType(int shading) {
		if ((shading != 0) && (shading != 1)) {
			throw new IllegalArgumentException();
		}
		this.mShading = shading;
	}

	public final void setSphereMap(Texture tex) {
		setSphereTexture(tex);
	}

	public final void setSphereTexture(Texture tex) {
		if (tex != null) {
			if (tex.isForModel) {
				throw new IllegalArgumentException();
			}
			if (tex.nPointer == 0) {
				throw new RuntimeException("Texture already disposed");
			}
		}
		this.mTexure = tex;
	}

	public final void setThreshold(int threshold, int high, int low) {
		setToonParams(threshold, high, low);
	}

	public final void setToonParams(int threshold, int high, int low) {
		if (((threshold | high | low) & 0xFF00) != 0) {
			throw new IllegalArgumentException();
		}
		this.mThreshold = threshold;
		this.mHigh = high;
		this.mLow = low;
	}

	public final void setTransparency(boolean isEnable) {
		this.isBlendEnable = isEnable;
	}

	static {
		System.loadLibrary("java_micro3d_v3_32");
		nInitClass();
	}

	public Effect3D() {
	}

	private static native void nInitClass();
}
