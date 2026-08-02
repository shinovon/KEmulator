/*
 * MIT License
 * Copyright (c) 2026 Roman Lahin
 */

package com.mascotcapsule.micro3d.v3;

public class MascotME {
//	static String version = "MascotME 1.1.1";
	
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

}
