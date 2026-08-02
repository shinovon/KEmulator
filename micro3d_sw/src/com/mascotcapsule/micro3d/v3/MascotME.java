/*
 * MIT License
 * Copyright (c) 2026 Roman Lahin
 */

package com.mascotcapsule.micro3d.v3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MascotME {
	static String version = "MascotME 1.1.1";
	
	// Debug options:
	
	// FPS counter.
	static boolean showFPS = false;
	
	// Detailed frametime metrics.
	// Includes Graphic3D.bind, .flush, .release calls, and figure, primitives, command lists submitting.
	static boolean showTimeMetrics = false;
	
	// Heap memory usage. Warning, can reduce performance.
	static boolean showHeapUsage = false;
	
	// Enhancements:
	
	// Converts horizonal fov to vertical in order to fix narrow camera when portrait mode games are launched in landscape mode.
	static boolean horizontalFovFix;
	
	// Frame buffer related hacks:
	
	// Compatibility hack, does not affect performance.
	// Color used to clear framebuffer when Graphics3D is bind.
	// Can be used to reduce graphical artifacts in games that draw semitransparent geometry over 2D graphics.
	
	// Flag, specifying to clear framebuffer with last 2D color used on screen.
	static final int CLEAR_WITH_LAST_USED_COLOR = -1;
	static int fbClearColor = CLEAR_WITH_LAST_USED_COLOR;
	
	// Compatibility hack, does not affect performance.
	// Uses dummy Canvas object to detect framebuffer size.
	// Can reduce graphical artifacts in games that use viewport clipping.
	static boolean fbSizeWorkaround = false;
	
	// Performance hack, high performance impact.
	// Reduces framebuffer resolution.
	static boolean halfResRender = false;
	
	// Performance hack, high performance impact.
	// Disables support of 2D graphics inbetween 3D geometry.
	// Framebuffer will be drawn on screen only when Graphics3D is released (by API design 3D graphics should be drawn on each flush).
	static boolean no2DInbetween = true;
	// Performance hack, low performance impact.
	// Overwrites existing 2D screen content by disabling alpha blending when framebuffer is drawn on the screen for the first time.
	static boolean overwrite2D = false;
	// Performance hack, medium performance impact.
	// Disables framebuffer clearing, useful when game fully overwrites framebuffer with geometry.
	// Please use with overwrite2D for bigger performance win.
	static boolean doNotClear = false;
	// Performance hack, medium performance impact.
	// Enables alternative method to clear framebuffer using System.arraycopy.
	// Can be faster on Series 40 cellphones.
	static boolean useArrayCopyClear = false;
	
	// Clipping related hacks:
	
	// Performance hack, medium performance impact.
	// Disables clipping of polygons intersecting camera's near plane.
	// Can lead to high polygon warping near camera.
	static boolean noNearClipping = false;
	// Performance hack, medium performance impact.
	// Disables far plane polygon clipping.
	// Can lead to polygons abruptly disappearing beyound maximum view distance.
	static boolean noFarClipping = false;
	// Performance hack, medium performance impact.
	// Disables toon shading polygon splitting.
	// Can lead to reduced toon shading quality.
	static boolean noToonSplitting = false;
	
	// Performance hacks related to various rasterization features:
	
	// Performance hacks, high performance impact
	// noLighting disables vertex lighting. Also disables environment mapping due to technical reasons.
	// noEnvMapping disables environment mapping.
	// noBlending hides polygons and sprites with blending enabled.
	static boolean noLighting = false, noEnvMapping = false, noBlending = false;
	
	private static final void parseKey(String key, String value) {
		try {
			if (key.equals("showFPS")) {
				showFPS = Integer.parseInt(value) == 1;
			} else if (key.equals("showTimeMetrics")) {
				showTimeMetrics = Integer.parseInt(value) == 1;
			} else if (key.equals("showHeapUsage")) {
				showHeapUsage = Integer.parseInt(value) == 1;
			} else if (key.equals("horizontalFovFix")) {
				horizontalFovFix = Integer.parseInt(value) == 1;
			} else if (key.equals("fbClearColor")) {
				fbClearColor = Integer.parseInt(value, 16);
			} else if (key.equals("fbSizeWorkaround")) {
				fbSizeWorkaround = Integer.parseInt(value) == 1;
			} else if (key.equals("halfResRender")) {
				halfResRender = Integer.parseInt(value) == 1;
			} else if (key.equals("no2DInbetween")) {
				no2DInbetween = Integer.parseInt(value) == 1;
			} else if (key.equals("overwrite2D")) {
				overwrite2D = Integer.parseInt(value) == 1;
			} else if (key.equals("doNotClear")) {
				doNotClear = Integer.parseInt(value) == 1;
			} else if (key.equals("useArrayCopyClear")) {
				useArrayCopyClear = Integer.parseInt(value) == 1;
			} else if (key.equals("noNearClipping")) {
				noNearClipping = Integer.parseInt(value) == 1;
			} else if (key.equals("noFarClipping")) {
				noFarClipping = Integer.parseInt(value) == 1;
			} else if (key.equals("noToonSplitting")) {
				noToonSplitting = Integer.parseInt(value) == 1;
			} else if (key.equals("noLighting")) {
				noLighting = Integer.parseInt(value) == 1;
			} else if (key.equals("noEnvMapping")) {
				noEnvMapping = Integer.parseInt(value) == 1;
			} else if (key.equals("noBlending")) {
				noBlending = Integer.parseInt(value) == 1;
			} else {
				throw new IllegalArgumentException(key);
			}
		} catch (NumberFormatException e) {
			throw new NumberFormatException(key + " = " + value);
		}
	}

}
