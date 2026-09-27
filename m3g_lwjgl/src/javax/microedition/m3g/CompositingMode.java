package javax.microedition.m3g;

public class CompositingMode extends Object3D {
	public static final int ALPHA = 64;
	public static final int ALPHA_ADD = 65;
	public static final int MODULATE = 66;
	public static final int MODULATE_X2 = 67;
	public static final int REPLACE = 68;
	private int blending = 68;
	private float alphaThreshold = 0.0F;
	private float depthOffsetFactor = 0.0F;
	private float depthOffsetUnits = 0.0F;
	private boolean depthTest = true;
	private boolean depthWrite = true;
	private boolean colorWrite = true;
	private boolean alphaWrite = true;

	public void setBlending(int mode) {
		if (mode >= 64 && mode <= 68) {
			this.blending = mode;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int getBlending() {
		return this.blending;
	}

	public void setAlphaThreshold(float threshold) {
		if (threshold >= 0.0F && threshold <= 1.0F) {
			this.alphaThreshold = threshold;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public float getAlphaThreshold() {
		return this.alphaThreshold;
	}

	public void setAlphaWriteEnable(boolean enable) {
		this.alphaWrite = enable;
	}

	public boolean isAlphaWriteEnabled() {
		return this.alphaWrite;
	}

	public void setColorWriteEnable(boolean enable) {
		this.colorWrite = enable;
	}

	public boolean isColorWriteEnabled() {
		return this.colorWrite;
	}

	public void setDepthWriteEnable(boolean enable) {
		this.depthWrite = enable;
	}

	public boolean isDepthWriteEnabled() {
		return this.depthWrite;
	}

	public void setDepthTestEnable(boolean enable) {
		this.depthTest = enable;
	}

	public boolean isDepthTestEnabled() {
		return this.depthTest;
	}

	public void setDepthOffset(float factor, float units) {
		this.depthOffsetFactor = factor;
		this.depthOffsetUnits = units;
	}

	public float getDepthOffsetFactor() {
		return this.depthOffsetFactor;
	}

	public float getDepthOffsetUnits() {
		return this.depthOffsetUnits;
	}
}
