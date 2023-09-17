package javax.microedition.m3g;

import java.io.IOException;

public class CompositingMode extends Object3D {

	public static final int ALPHA = 64;
	public static final int ALPHA_ADD = 65;
	public static final int MODULATE = 66;
	public static final int MODULATE_X2 = 67;
	public static final int REPLACE = 68;

	boolean depthTestEnabled  = true;
	boolean depthWriteEnabled = true;
	boolean colorWriteEnabled = true;
	boolean alphaWriteEnabled = true;

	int blending = REPLACE;
	float alphaThreshold;
	float depthOffsetFactor;
	float depthOffsetUnits;

	public float getAlphaThreshold() {
		// Retrieves the current alpha testing threshold.
		return alphaThreshold;
	}

	public int getBlending() {
		// Retrieves the current frame buffer blending mode.
		return blending;
	}

	public float getDepthOffsetFactor() {
		// Retrieves the current depth offset slope factor.
		return depthOffsetFactor;
	}

	public float getDepthOffsetUnits() {
		// Retrieves the current constant depth offset.
		return depthOffsetUnits;
	}

	public boolean isAlphaWriteEnabled() {
		// Queries whether alpha writing is enabled.
		return alphaWriteEnabled;
	}

	public boolean isColorWriteEnabled() {
		// Queries whether color writing is enabled.
		return colorWriteEnabled;
	}

	public boolean isDepthTestEnabled() {
		// Queries whether depth testing is enabled.
		return depthTestEnabled;
	}

	public boolean isDepthWriteEnabled() {
		// Queries whether depth writing is enabled.
		return depthWriteEnabled;
	}

	public void setAlphaThreshold(float threshold) {
		// Sets the threshold value for alpha testing.
		alphaThreshold = threshold;
	}

	public void setAlphaWriteEnable(boolean enable) {
		// Enables or disables writing of fragment alpha values into the color
		// buffer.
		alphaWriteEnabled = enable;
	}

	public void setBlending(int mode) {
		// Selects a method of combining the pixel to be rendered with the pixel
		// already in the frame buffer.
		blending = mode;
	}

	public void setColorWriteEnable(boolean enable) {
		// Enables or disables writing of fragment color values into the color
		// buffer.
		colorWriteEnabled = enable;
	}

	public void setDepthOffset(float factor, float units) {
		// Defines a value that is added to the screen space Z coordinate of a
		// pixel immediately before depth test and depth write.

		depthOffsetFactor = factor;
		depthOffsetUnits = units;
	}

	public void setDepthTestEnable(boolean enable) {
		// Enables or disables depth testing.
		depthTestEnabled = enable;
	}

	public void setDepthWriteEnable(boolean enable) {
		// Enables or disables writing of fragment depth values into the depth
		// buffer.
		depthWriteEnabled = enable;
	}


	void load(M3gInputStream is) throws IOException {
		// Boolean depthTestEnabled;
		// Boolean depthWriteEnabled;
		// Boolean colorWriteEnabled;
		// Boolean alphaWriteEnabled;
		// Byte blending;
		// Byte alphaThreshold;
		// Float32 depthOffsetFactor;
		// Float32 depthOffsetUnits;

		_load(is);
		depthTestEnabled = is.readBoolean();
		depthWriteEnabled = is.readBoolean();
		colorWriteEnabled = is.readBoolean();
		alphaWriteEnabled = is.readBoolean();
		blending = is.read();
		alphaThreshold = is.read() / 255.0f;
		depthOffsetFactor = is.readFloat32();
		depthOffsetUnits = is.readFloat32();

	}


	public int getReferences(Object3D[] refs) {
		return _getReferences(refs);
	}


	public Object3D duplicate() {
		CompositingMode target = new CompositingMode();
		target.depthTestEnabled = depthTestEnabled;
		target.depthWriteEnabled = depthWriteEnabled;
		target.colorWriteEnabled = colorWriteEnabled;
		target.alphaWriteEnabled = alphaWriteEnabled;
		target.blending = blending;
		target.alphaThreshold = alphaThreshold;
		target.depthOffsetFactor = depthOffsetFactor;
		target.depthOffsetUnits = depthOffsetUnits;
		return _duplicate(target);
	}

}
