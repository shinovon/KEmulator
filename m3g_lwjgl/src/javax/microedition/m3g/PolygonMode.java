package javax.microedition.m3g;

public class PolygonMode extends Object3D {
	public static final int CULL_BACK = 160;
	public static final int CULL_FRONT = 161;
	public static final int CULL_NONE = 162;
	public static final int SHADE_FLAT = 164;
	public static final int SHADE_SMOOTH = 165;
	public static final int WINDING_CCW = 168;
	public static final int WINDING_CW = 169;
	private int culling = 160;
	private int shading = 165;
	private int winding = 168;
	private boolean twoSidedLighting = false;
	private boolean localCameraLighting = false;
	private boolean perspectiveCorrection = false;

	public void setCulling(int var1) {
		if (var1 >= 160 && var1 <= 162) {
			this.culling = var1;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int getCulling() {
		return this.culling;
	}

	public void setWinding(int var1) {
		if (var1 != 169 && var1 != 168) {
			throw new IllegalArgumentException();
		} else {
			this.winding = var1;
		}
	}

	public int getWinding() {
		return this.winding;
	}

	public void setShading(int var1) {
		if (var1 != 164 && var1 != 165) {
			throw new IllegalArgumentException();
		} else {
			this.shading = var1;
		}
	}

	public int getShading() {
		return this.shading;
	}

	public void setTwoSidedLightingEnable(boolean var1) {
		this.twoSidedLighting = var1;
	}

	public boolean isTwoSidedLightingEnabled() {
		return this.twoSidedLighting;
	}

	public void setLocalCameraLightingEnable(boolean var1) {
		this.localCameraLighting = var1;
	}

	public boolean isLocalCameraLightingEnabled() {
		return this.localCameraLighting;
	}

	public void setPerspectiveCorrectionEnable(boolean var1) {
		this.perspectiveCorrection = var1;
	}

	public boolean isPerspectiveCorrectionEnabled() {
		return this.perspectiveCorrection;
	}
}
