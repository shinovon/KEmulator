package javax.microedition.m3g;

import emulator.graphics3D.G3DUtils;

public class Light extends Node {
	public static final int AMBIENT = 128;
	public static final int DIRECTIONAL = 129;
	public static final int OMNI = 130;
	public static final int SPOT = 131;
	private int mode = 129;
	private int color = 16777215;
	private float intensity = 1.0F;
	private float constantAttenuation = 1.0F;
	private float linearAttenuation = 0.0F;
	private float quadraticAttenuation = 0.0F;
	private float spotAngle = 45.0F;
	private float spotExponent = 0.0F;

	public void setMode(int mode) {
		if (mode >= 128 && mode <= 131) {
			this.mode = mode;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int getMode() {
		return this.mode;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public float getIntensity() {
		return this.intensity;
	}

	public void setColor(int RGB) {
		this.color = RGB;
	}

	public int getColor() {
		return this.color;
	}

	public void setSpotAngle(float angle) {
		if (angle >= 0.0F && angle <= 90.0F) {
			this.spotAngle = angle;
		} else {
			throw new IllegalArgumentException("angle is not in [0, 90]");
		}
	}

	public float getSpotAngle() {
		return this.spotAngle;
	}

	public void setSpotExponent(float exponent) {
		if (exponent >= 0.0F && exponent <= 128.0F) {
			this.spotExponent = exponent;
		} else {
			throw new IllegalArgumentException("exponent is not in [0, 128]");
		}
	}

	public float getSpotExponent() {
		return this.spotExponent;
	}

	public void setAttenuation(float constant, float linear, float quadratic) {
		if (constant >= 0.0F && linear >= 0.0F && quadratic >= 0.0F) {
			if (constant == 0.0F && linear == 0.0F && quadratic == 0.0F) {
				throw new IllegalArgumentException("all of the parameter values are zero");
			} else {
				this.constantAttenuation = constant;
				this.linearAttenuation = linear;
				this.quadraticAttenuation = quadratic;
			}
		} else {
			throw new IllegalArgumentException("any of the parameter values are negative");
		}
	}

	public float getConstantAttenuation() {
		return this.constantAttenuation;
	}

	public float getLinearAttenuation() {
		return this.linearAttenuation;
	}

	public float getQuadraticAttenuation() {
		return this.quadraticAttenuation;
	}

	protected void updateProperty(int property, float[] values) {
		switch (property) {
			case 258:
				this.color = G3DUtils.getIntColor(values);
				return;
			case 265:
				this.intensity = values[0];
				return;
			case 273:
				this.spotAngle = G3DUtils.limit(values[0], 0.0F, 90.0F);
				return;
			case 274:
				this.spotExponent = G3DUtils.limit(values[0], 0.0F, 128.0F);
				return;
			default:
				super.updateProperty(property, values);
		}
	}

	protected boolean rayIntersect(int scope, float[] ray, RayIntersection ri, Transform transform) {
		return false;
	}
}
