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

	public void setCulling(int mode) {
		if (mode >= 160 && mode <= 162) {
			this.culling = mode;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int getCulling() {
		return this.culling;
	}

	public void setWinding(int mode) {
		if (mode != 169 && mode != 168) {
			throw new IllegalArgumentException();
		} else {
			this.winding = mode;
		}
	}

	public int getWinding() {
		return this.winding;
	}

	public void setShading(int mode) {
		if (mode != 164 && mode != 165) {
			throw new IllegalArgumentException();
		} else {
			this.shading = mode;
		}
	}

	public int getShading() {
		return this.shading;
	}

	public void setTwoSidedLightingEnable(boolean enable) {
		this.twoSidedLighting = enable;
	}

	public boolean isTwoSidedLightingEnabled() {
		return this.twoSidedLighting;
	}

	public void setLocalCameraLightingEnable(boolean enable) {
		this.localCameraLighting = enable;
	}

	public boolean isLocalCameraLightingEnabled() {
		return this.localCameraLighting;
	}

	public void setPerspectiveCorrectionEnable(boolean enable) {
		this.perspectiveCorrection = enable;
	}

	public boolean isPerspectiveCorrectionEnabled() {
		return this.perspectiveCorrection;
	}
}
