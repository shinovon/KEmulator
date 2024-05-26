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

	public void setBlending(int var1) {
		if (var1 >= 64 && var1 <= 68) {
			this.blending = var1;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int getBlending() {
		return this.blending;
	}

	public void setAlphaThreshold(float var1) {
		if (var1 >= 0.0F && var1 <= 1.0F) {
			this.alphaThreshold = var1;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public float getAlphaThreshold() {
		return this.alphaThreshold;
	}

	public void setAlphaWriteEnable(boolean var1) {
		this.alphaWrite = var1;
	}

	public boolean isAlphaWriteEnabled() {
		return this.alphaWrite;
	}

	public void setColorWriteEnable(boolean var1) {
		this.colorWrite = var1;
	}

	public boolean isColorWriteEnabled() {
		return this.colorWrite;
	}

	public void setDepthWriteEnable(boolean var1) {
		this.depthWrite = var1;
	}

	public boolean isDepthWriteEnabled() {
		return this.depthWrite;
	}

	public void setDepthTestEnable(boolean var1) {
		this.depthTest = var1;
	}

	public boolean isDepthTestEnabled() {
		return this.depthTest;
	}

	public void setDepthOffset(float var1, float var2) {
		this.depthOffsetFactor = var1;
		this.depthOffsetUnits = var2;
	}

	public float getDepthOffsetFactor() {
		return this.depthOffsetFactor;
	}

	public float getDepthOffsetUnits() {
		return this.depthOffsetUnits;
	}
}
