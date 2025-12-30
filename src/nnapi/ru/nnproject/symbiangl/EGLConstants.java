/*
** License Applicability. Except to the extent portions of this file are
** made subject to an alternative license as permitted in the SGI Free
** Software License B, Version 1.0 (the "License"), the contents of this
** file are subject only to the provisions of the License. You may not use
** this file except in compliance with the License. You may obtain a copy
** of the License at Silicon Graphics, Inc., attn: Legal Services, 1600
** Amphitheatre Parkway, Mountain View, CA 94043-1351, or at:
**
** http://oss.sgi.com/projects/FreeB
**
** Note that, as provided in the License, the Software is distributed on an
** "AS IS" basis, with ALL EXPRESS AND IMPLIED WARRANTIES AND CONDITIONS
** DISCLAIMED, INCLUDING, WITHOUT LIMITATION, ANY IMPLIED WARRANTIES AND
** CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A
** PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
**
** Original Code. The Original Code is: OpenGL Sample Implementation,
** Version 1.2.1, released January 26, 2000, developed by Silicon Graphics,
** Inc. The Original Code is Copyright (c) 1991-2004 Silicon Graphics, Inc.
** Copyright in any portions created by third parties is as indicated
** elsewhere herein. All Rights Reserved.
**
** Additional Notice Provisions: The application programming interfaces
** established by SGI in conjunction with the Original Code are The
** OpenGL(R) Graphics System: A Specification (Version 1.2.1), released
** April 1, 1999; The OpenGL(R) Graphics System Utility Library (Version
** 1.3), released November 4, 1998; and OpenGL(R) Graphics with the X
** Window System(R) (Version 1.3), released October 19, 1998. This software
** was created using the OpenGL(R) version 1.2.1 Sample Implementation
** published by SGI, but has not been independently verified as being
** compliant with the OpenGL(R) version 1.2.1 Specification.
*/
package ru.nnproject.symbiangl;

// EGL 1.1
public interface EGLConstants {
	// egl.h
	
	/*
	** EGL and native handle values
	*/
	public static final int EGL_DEFAULT_DISPLAY = 0;
	public static final int EGL_NO_CONTEXT      = 0;
	public static final int EGL_NO_DISPLAY      = 0;
	public static final int EGL_NO_SURFACE      = 0;
	
	/*
	** Boolean
	*/
	public static final int EGL_FALSE = 0;
	public static final int EGL_TRUE  = 1;
	
	/*
	** Errors
	*/
	public static final int EGL_SUCCESS = 0x3000;
	public static final int EGL_NOT_INITIALIZED = 0x3001;
	public static final int EGL_BAD_ACCESS = 0x3002;
	public static final int EGL_BAD_ALLOC = 0x3003;
	public static final int EGL_BAD_ATTRIBUTE = 0x3004;
	public static final int EGL_BAD_CONFIG = 0x3005;
	public static final int EGL_BAD_CONTEXT = 0x3006;
	public static final int EGL_BAD_CURRENT_SURFACE = 0x3007;
	public static final int EGL_BAD_DISPLAY = 0x3008;
	public static final int EGL_BAD_SURFACE = 0x3009;
	public static final int EGL_BAD_MATCH = 0x300A;
	public static final int EGL_BAD_PARAMETER = 0x300B;
	public static final int EGL_BAD_NATIVE_PIXMAP = 0x300C;
	public static final int EGL_BAD_NATIVE_WINDOW = 0x300D;
	public static final int EGL_CONTEXT_LOST = 0x300E;
	/* 0x300F - 0x301F reserved for additional errors. */
	
	/*
	** Config attributes
	*/
	public static final int EGL_BUFFER_SIZE = 0x3020;
	public static final int EGL_ALPHA_SIZE = 0x3021;
	public static final int EGL_BLUE_SIZE = 0x3022;
	public static final int EGL_GREEN_SIZE = 0x3023;
	public static final int EGL_RED_SIZE = 0x3024;
	public static final int EGL_DEPTH_SIZE = 0x3025;
	public static final int EGL_STENCIL_SIZE = 0x3026;
	public static final int EGL_CONFIG_CAVEAT = 0x3027;
	public static final int EGL_CONFIG_ID = 0x3028;
	public static final int EGL_LEVEL = 0x3029;
	public static final int EGL_MAX_PBUFFER_HEIGHT = 0x302A;
	public static final int EGL_MAX_PBUFFER_PIXELS = 0x302B;
	public static final int EGL_MAX_PBUFFER_WIDTH = 0x302C;
	public static final int EGL_NATIVE_RENDERABLE = 0x302D;
	public static final int EGL_NATIVE_VISUAL_ID = 0x302E;
	public static final int EGL_NATIVE_VISUAL_TYPE = 0x302F;
	/*public static final int EGL_PRESERVED_RESOURCES = 0x3030;*/
	public static final int EGL_SAMPLES = 0x3031;
	public static final int EGL_SAMPLE_BUFFERS = 0x3032;
	public static final int EGL_SURFACE_TYPE = 0x3033;
	public static final int EGL_TRANSPARENT_TYPE = 0x3034;
	public static final int EGL_TRANSPARENT_BLUE_VALUE = 0x3035;
	public static final int EGL_TRANSPARENT_GREEN_VALUE = 0x3036;
	public static final int EGL_TRANSPARENT_RED_VALUE = 0x3037;
	
	public static final int EGL_NONE = 0x3038; /* Also a config value */
	public static final int EGL_BIND_TO_TEXTURE_RGB = 0x3039;
	public static final int EGL_BIND_TO_TEXTURE_RGBA = 0x303A;
	public static final int EGL_MIN_SWAP_INTERVAL = 0x303B;
	public static final int EGL_MAX_SWAP_INTERVAL = 0x303C;
	
	/*
	** Config values
	*/
	public static final int EGL_DONT_CARE = -1;
	
	public static final int EGL_SLOW_CONFIG = 0x3050; /* EGL_CONFIG_CAVEAT value */
	public static final int EGL_NON_CONFORMANT_CONFIG = 0x3051; /* " */
	public static final int EGL_TRANSPARENT_RGB = 0x3052; /* EGL_TRANSPARENT_TYPE value */
	public static final int EGL_NO_TEXTURE =		       0x305C;	/* EGL_TEXTURE_FORMAT/TARGET value */
	public static final int EGL_TEXTURE_RGB =		       0x305D;	/* EGL_TEXTURE_FORMAT value */
	public static final int EGL_TEXTURE_RGBA =	       0x305E;	/* " */
	public static final int EGL_TEXTURE_2D =		       0x305F;	/* EGL_TEXTURE_TARGET value */

	/*
	** Config attribute mask bits
	*/
	public static final int EGL_PBUFFER_BIT =		       0x01;	/* EGL_SURFACE_TYPE mask bit */
	public static final int EGL_PIXMAP_BIT =		       0x02;	/* " */
	public static final int EGL_WINDOW_BIT =		       0x04;	/* " */

	/*
	** String names
	*/
	public static final int EGL_VENDOR =                     0x3053;
	public static final int EGL_VERSION =                    0x3054;
	public static final int EGL_EXTENSIONS =                 0x3055;

	/*
	** Surface attributes
	*/
	public static final int EGL_HEIGHT =                    0x3056;
	public static final int EGL_WIDTH =                     0x3057;
	public static final int EGL_LARGEST_PBUFFER =           0x3058;
	public static final int EGL_TEXTURE_FORMAT =       0x3080;	/* For pbuffers bound as textures */
	public static final int EGL_TEXTURE_TARGET =	       0x3081;	/* " */
	public static final int EGL_MIPMAP_TEXTURE =	       0x3082;	/* " */
	public static final int EGL_MIPMAP_LEVEL =	       0x3083;	/* " */

	/*
	** BindTexImage / ReleaseTexImage buffer target
	*/
	public static final int EGL_BACK_BUFFER	=	       0x3084;

	/*
	** Current surfaces
	*/
	public static final int EGL_DRAW =                       0x3059;
	public static final int EGL_READ =                       0x305A;

	/*
	** Engines
	*/
	public static final int EGL_CORE_NATIVE_ENGINE =         0x305B;

	/* 0x305C-0x3FFFF reserved for future use */

}
