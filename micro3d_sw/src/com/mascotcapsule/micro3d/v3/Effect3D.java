/*
 * MIT License
 * Copyright (c) 2026 Yury Kharchenko
 */

package com.mascotcapsule.micro3d.v3;

public class Effect3D {
	public static final int NORMAL_SHADING = 0;
	public static final int TOON_SHADING = 1;

	Light light;
	int shadingType;
	boolean transparency;
	Texture sphereTexture;

	int toonThreshold;
	int toonLow, toonHigh;

	public Effect3D() {
		light = null;
		shadingType = NORMAL_SHADING;
		transparency = true;
	}

	public Effect3D(Light light, int shading, boolean isEnableTrans, Texture tex) {
		setShadingType(shading);
		setSphereTexture(tex);
		
		this.light = light;
		this.shadingType = shading;
		this.transparency = isEnableTrans;
		this.sphereTexture = tex;
	}

	public final Light getLight() { return light; }
	public final void setLight(Light light) { this.light = light; }

	public final int getShadingType() { return shadingType; }
	public final int getShading() { return shadingType; }

	public final void setShadingType(int shading) {
		if ((shading & ~TOON_SHADING) != 0) {
			throw new IllegalArgumentException();
		}
		
		this.shadingType = shading;
	}
	
	public final void setShading(int shading) { setShadingType(shading); }

	public final int getToonThreshold() { return toonThreshold; }
	public final int getThreshold() { return toonThreshold; }
	public final int getToonHigh() { return toonHigh; }
	public final int getThresholdHigh() { return toonHigh; }
	public final int getToonLow() { return toonLow; }
	public final int getThresholdLow() { return toonLow; }

	public final void setToonParams(int threshold, int high, int low) {
		if (
			threshold < 0 || threshold > 255 ||
			high < 0 || high > 255 ||
			low < 0 || low > 255
		) {
			throw new IllegalArgumentException();
		}
		
		this.toonThreshold = threshold;
		this.toonHigh = high;
		this.toonLow = low;
	}
	
	public final void setThreshold(int threshold, int high, int low) {
		setToonParams(threshold, high, low);
	}

	public final boolean isTransparency() { return transparency; }
	public final boolean isSemiTransparentEnabled() { return transparency; }

	public final void setTransparency(boolean isEnable) { transparency = isEnable; }
	public final void setSemiTransparentEnabled(boolean isEnable) { transparency = isEnable; }

	public final Texture getSphereTexture() { return sphereTexture; }
	public final Texture getSphereMap() { return sphereTexture; }

	public final void setSphereTexture(Texture tex) {
		if (tex != null && tex.isForModel) {
			throw new IllegalArgumentException();
		}
		
		this.sphereTexture = tex;
	}
	
	public final void setSphereMap(Texture tex) { setSphereTexture(tex); }
}