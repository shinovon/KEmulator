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
