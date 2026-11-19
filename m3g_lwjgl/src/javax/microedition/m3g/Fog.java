package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;

public class Fog extends Object3D {
	public static final int EXPONENTIAL = 80;
	public static final int LINEAR = 81;
	private int mode = LINEAR;
	private float density = 1.0F;
	private float near = 0.0F;
	private float far = 1.0F;
	private int color = 0;

	public void setMode(int mode) {
		if (mode != EXPONENTIAL && mode != LINEAR) {
			throw new IllegalArgumentException();
		} else {
			this.mode = mode;
		}
	}

	public int getMode() {
		return this.mode;
	}

	public void setLinear(float near, float far) {
		this.near = near;
		this.far = far;
	}

	public float getNearDistance() {
		return this.near;
	}

	public float getFarDistance() {
		return this.far;
	}

	public void setDensity(float density) {
		if (density < 0.0F) {
			throw new IllegalArgumentException();
		} else {
			this.density = density;
		}
	}

	public float getDensity() {
		return this.density;
	}

	public void setColor(int RGB) {
		this.color = RGB & 0xffffff;
	}

	public int getColor() {
		return this.color;
	}

	protected void updateProperty(int property, float[] value) {
		switch (property) {
		case AnimationTrack.ALPHA:
			this.color = G3DUtils.getIntColor(value) & 0xffffff;
			return;
		case AnimationTrack.CROP:
		case AnimationTrack.DIFFUSE_COLOR:
		case AnimationTrack.EMISSIVE_COLOR:
		case AnimationTrack.FIELD_OF_VIEW:
		case AnimationTrack.INTENSITY:
		case AnimationTrack.MORPH_WEIGHTS:
		default:
			super.updateProperty(property, value);
			return;
		case AnimationTrack.DENSITY:
			this.density = G3DUtils.limitPositive(value[0]);
			return;
		case AnimationTrack.FAR_DISTANCE:
			this.far = value[0];
			return;
		case AnimationTrack.NEAR_DISTANCE:
			this.near = value[0];
		}
	}
}
