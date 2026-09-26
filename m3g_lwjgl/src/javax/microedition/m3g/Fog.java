package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;

public class Fog extends Object3D {
	public static final int EXPONENTIAL = 80;
	public static final int LINEAR = 81;
	private int mode = 81;
	private float density = 1.0F;
	private float near = 0.0F;
	private float far = 1.0F;
	private int color = 0;

	public void setMode(int mode) {
		if (mode != 80 && mode != 81) {
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
		this.color = RGB;
	}

	public int getColor() {
		return this.color;
	}

	protected void updateProperty(int property, float[] value) {
		switch (property) {
			case 258:
				this.color = G3DUtils.getIntColor(value);
				return;
			case 259:
			case 261:
			case 262:
			case 264:
			case 265:
			case 266:
			default:
				super.updateProperty(property, value);
				return;
			case 260:
				this.density = G3DUtils.limitPositive(value[0]);
				return;
			case 263:
				this.far = value[0];
				return;
			case 267:
				this.near = value[0];
		}
	}
}
