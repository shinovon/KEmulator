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
** Inc. The Original Code is Copyright (c) 1991-2000 Silicon Graphics, Inc.
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

// OpenGL ES CM 1.1
public interface GLConstants {
	// gl.h
	
	/* ClearBufferMask */
	public static final int GL_DEPTH_BUFFER_BIT   = 0x00000100;
	public static final int GL_STENCIL_BUFFER_BIT = 0x00000400;
	public static final int GL_COLOR_BUFFER_BIT   = 0x00004000;
	
	/* Boolean */
	public static final int GL_FALSE = 0;
	public static final int GL_TRUE = 1;
	
	/* BeginMode */
	public static final int GL_POINTS         = 0x0000;
	public static final int GL_LINES          = 0x0001;
	public static final int GL_LINE_LOOP      = 0x0002;
	public static final int GL_LINE_STRIP     = 0x0003;
	public static final int GL_TRIANGLES      = 0x0004;
	public static final int GL_TRIANGLE_STRIP = 0x0005;
	public static final int GL_TRIANGLE_FAN   = 0x0006;
	
	/* AlphaFunction */
	public static final int GL_NEVER          = 0x0200;
	public static final int GL_LESS           = 0x0201;
	public static final int GL_EQUAL          = 0x0202;
	public static final int GL_LEQUAL         = 0x0203;
	public static final int GL_GREATER        = 0x0204;
	public static final int GL_NOTEQUAL       = 0x0205;
	public static final int GL_GEQUAL         = 0x0206;
	public static final int GL_ALWAYS         = 0x0207;
	
	/* BlendingFactorDest */
	public static final int GL_ZERO                = 0;
	public static final int GL_ONE                 = 1;
	public static final int GL_SRC_COLOR           = 0x0300;
	public static final int GL_ONE_MINUS_SRC_COLOR = 0x0301;
	public static final int GL_SRC_ALPHA           = 0x0302;
	public static final int GL_ONE_MINUS_SRC_ALPHA = 0x0303;
	public static final int GL_DST_ALPHA           = 0x0304;
	public static final int GL_ONE_MINUS_DST_ALPHA = 0x0305;
	
	/* BlendingFactorSrc */
	/*      GL_ZERO */
	/*      GL_ONE */
	public static final int GL_DST_COLOR           = 0x0306;
	public static final int GL_ONE_MINUS_DST_COLOR = 0x0307;
	public static final int GL_SRC_ALPHA_SATURATE  = 0x0308;
	/*      GL_SRC_ALPHA */
	/*      GL_ONE_MINUS_SRC_ALPHA */
	/*      GL_DST_ALPHA */
	/*      GL_ONE_MINUS_DST_ALPHA */
	
	/* ClipPlaneName */
	public static final int GL_CLIP_PLANE0 = 0x3000;
	public static final int GL_CLIP_PLANE1 = 0x3001;
	public static final int GL_CLIP_PLANE2 = 0x3002;
	public static final int GL_CLIP_PLANE3 = 0x3003;
	public static final int GL_CLIP_PLANE4 = 0x3004;
	public static final int GL_CLIP_PLANE5 = 0x3005;
	
	/* ColorMaterialFace */
	/*      GL_FRONT_AND_BACK */

	/* ColorMaterialParameter */
	/*      GL_AMBIENT_AND_DIFFUSE */

	/* ColorPointerType */
	/*      GL_UNSIGNED_BYTE */
	/*      GL_FLOAT */
	/*      GL_FIXED */
	
	/* CullFaceMode */
	public static final int GL_FRONT          = 0x0404;
	public static final int GL_BACK           = 0x0405;
	public static final int GL_FRONT_AND_BACK = 0x0408;
	
	/* DepthFunction */
	/*      GL_NEVER */
	/*      GL_LESS */
	/*      GL_EQUAL */
	/*      GL_LEQUAL */
	/*      GL_GREATER */
	/*      GL_NOTEQUAL */
	/*      GL_GEQUAL */
	/*      GL_ALWAYS */
	
	/* EnableCap */
	public static final int GL_FOG =                           0x0B60;
	public static final int GL_LIGHTING =                      0x0B50;
	public static final int GL_TEXTURE_2D =                    0x0DE1;
	public static final int GL_CULL_FACE =                     0x0B44;
	public static final int GL_ALPHA_TEST =                    0x0BC0;
	public static final int GL_BLEND =                         0x0BE2;
	public static final int GL_COLOR_LOGIC_OP =                0x0BF2;
	public static final int GL_DITHER =                        0x0BD0;
	public static final int GL_STENCIL_TEST =                  0x0B90;
	public static final int GL_DEPTH_TEST =                    0x0B71;
	/*      GL_LIGHT0 */
	/*      GL_LIGHT1 */
	/*      GL_LIGHT2 */
	/*      GL_LIGHT3 */
	/*      GL_LIGHT4 */
	/*      GL_LIGHT5 */
	/*      GL_LIGHT6 */
	/*      GL_LIGHT7 */
	public static final int GL_POINT_SMOOTH =                  0x0B10;
	public static final int GL_LINE_SMOOTH =                   0x0B20;
//	public static final int GL_SCISSOR_TEST =                  0x0C11;
	public static final int GL_COLOR_MATERIAL =                0x0B57;
	public static final int GL_NORMALIZE =                     0x0BA1;
	public static final int GL_RESCALE_NORMAL =                0x803A;
//	public static final int GL_POLYGON_OFFSET_FILL =           0x8037;
	public static final int GL_VERTEX_ARRAY =                  0x8074;
	public static final int GL_NORMAL_ARRAY =                  0x8075;
	public static final int GL_COLOR_ARRAY =                   0x8076;
	public static final int GL_TEXTURE_COORD_ARRAY =           0x8078;
	public static final int GL_MULTISAMPLE =                   0x809D;
	public static final int GL_SAMPLE_ALPHA_TO_COVERAGE =      0x809E;
	public static final int GL_SAMPLE_ALPHA_TO_ONE =           0x809F;
	public static final int GL_SAMPLE_COVERAGE =               0x80A0;
	
	/* ErrorCode */
	public static final int GL_NO_ERROR = 0;
	public static final int GL_INVALID_ENUM = 0x0500;
	public static final int GL_INVALID_VALUE = 0x0501;
	public static final int GL_INVALID_OPERATION = 0x0502;
	public static final int GL_STACK_OVERFLOW = 0x0503;
	public static final int GL_STACK_UNDERFLOW = 0x0504;
	public static final int GL_OUT_OF_MEMORY = 0x0505;
	
	/* FogMode */
	/*      GL_LINEAR */
	public static final int GL_EXP = 0x0800;
	public static final int GL_EXP2 = 0x0801;
	
	/* FogParameter */
	public static final int GL_FOG_DENSITY = 0x0B62;
	public static final int GL_FOG_START = 0x0B63;
	public static final int GL_FOG_END = 0x0B64;
	public static final int GL_FOG_MODE = 0x0B65;
	public static final int GL_FOG_COLOR = 0x0B66;
	
	/* FrontFaceDirection */
	public static final int GL_CW = 0x0900;
	public static final int GL_CCW = 0x0901;
	
	/* GetPName */
	public static final int GL_CURRENT_COLOR =                 0x0B00;
	public static final int GL_CURRENT_NORMAL =                0x0B02;
	public static final int GL_CURRENT_TEXTURE_COORDS =        0x0B03;
	public static final int GL_POINT_SIZE =                    0x0B11;
	public static final int GL_POINT_SIZE_MIN =                0x8126;
	public static final int GL_POINT_SIZE_MAX =                0x8127;
	public static final int GL_POINT_FADE_THRESHOLD_SIZE =     0x8128;
	public static final int GL_POINT_DISTANCE_ATTENUATION =    0x8129;
	public static final int GL_SMOOTH_POINT_SIZE_RANGE =       0x0B12;
	public static final int GL_LINE_WIDTH =                    0x0B21;
	public static final int GL_SMOOTH_LINE_WIDTH_RANGE =       0x0B22;
	public static final int GL_ALIASED_POINT_SIZE_RANGE =      0x846D;
	public static final int GL_ALIASED_LINE_WIDTH_RANGE =      0x846E;
	public static final int GL_CULL_FACE_MODE =                0x0B45;
	public static final int GL_FRONT_FACE =                    0x0B46;
	public static final int GL_SHADE_MODEL =                   0x0B54;
	public static final int GL_DEPTH_RANGE =                   0x0B70;
	public static final int GL_DEPTH_WRITEMASK =               0x0B72;
	public static final int GL_DEPTH_CLEAR_VALUE =             0x0B73;
	public static final int GL_DEPTH_FUNC =                    0x0B74;
	public static final int GL_STENCIL_CLEAR_VALUE =           0x0B91;
	public static final int GL_STENCIL_FUNC =                  0x0B92;
	public static final int GL_STENCIL_VALUE_MASK =            0x0B93;
	public static final int GL_STENCIL_FAIL =                  0x0B94;
	public static final int GL_STENCIL_PASS_DEPTH_FAIL =       0x0B95;
	public static final int GL_STENCIL_PASS_DEPTH_PASS =       0x0B96;
	public static final int GL_STENCIL_REF =                   0x0B97;
	public static final int GL_STENCIL_WRITEMASK =             0x0B98;
	public static final int GL_MATRIX_MODE =                   0x0BA0;
	public static final int GL_VIEWPORT =                      0x0BA2;
	public static final int GL_MODELVIEW_STACK_DEPTH =         0x0BA3;
	public static final int GL_PROJECTION_STACK_DEPTH =        0x0BA4;
	public static final int GL_TEXTURE_STACK_DEPTH =           0x0BA5;
	public static final int GL_MODELVIEW_MATRIX =              0x0BA6;
	public static final int GL_PROJECTION_MATRIX =             0x0BA7;
	public static final int GL_TEXTURE_MATRIX =                0x0BA8;
	public static final int GL_ALPHA_TEST_FUNC =               0x0BC1;
	public static final int GL_ALPHA_TEST_REF =                0x0BC2;
	public static final int GL_BLEND_DST =                     0x0BE0;
	public static final int GL_BLEND_SRC =                     0x0BE1;
	public static final int GL_LOGIC_OP_MODE =                 0x0BF0;
	public static final int GL_SCISSOR_BOX =                   0x0C10;
	public static final int GL_SCISSOR_TEST =                  0x0C11;
	public static final int GL_COLOR_CLEAR_VALUE =             0x0C22;
	public static final int GL_COLOR_WRITEMASK =               0x0C23;
	public static final int GL_UNPACK_ALIGNMENT =              0x0CF5;
	public static final int GL_PACK_ALIGNMENT =                0x0D05;
	public static final int GL_MAX_LIGHTS =                    0x0D31;
	public static final int GL_MAX_CLIP_PLANES =               0x0D32;
	public static final int GL_MAX_TEXTURE_SIZE =              0x0D33;
	public static final int GL_MAX_MODELVIEW_STACK_DEPTH =     0x0D36;
	public static final int GL_MAX_PROJECTION_STACK_DEPTH =    0x0D38;
	public static final int GL_MAX_TEXTURE_STACK_DEPTH =       0x0D39;
	public static final int GL_MAX_VIEWPORT_DIMS =             0x0D3A;
	public static final int GL_MAX_TEXTURE_UNITS =             0x84E2;
	public static final int GL_SUBPIXEL_BITS =                 0x0D50;
	public static final int GL_RED_BITS =                      0x0D52;
	public static final int GL_GREEN_BITS =                    0x0D53;
	public static final int GL_BLUE_BITS =                     0x0D54;
	public static final int GL_ALPHA_BITS =                    0x0D55;
	public static final int GL_DEPTH_BITS =                    0x0D56;
	public static final int GL_STENCIL_BITS =                  0x0D57;
	public static final int GL_POLYGON_OFFSET_UNITS =          0x2A00;
	public static final int GL_POLYGON_OFFSET_FILL =           0x8037;
	public static final int GL_POLYGON_OFFSET_FACTOR =         0x8038;
	public static final int GL_TEXTURE_BINDING_2D =            0x8069;
	public static final int GL_VERTEX_ARRAY_SIZE =             0x807A;
	public static final int GL_VERTEX_ARRAY_TYPE =             0x807B;
	public static final int GL_VERTEX_ARRAY_STRIDE =           0x807C;
	public static final int GL_NORMAL_ARRAY_TYPE =             0x807E;
	public static final int GL_NORMAL_ARRAY_STRIDE =           0x807F;
	public static final int GL_COLOR_ARRAY_SIZE =              0x8081;
	public static final int GL_COLOR_ARRAY_TYPE =              0x8082;
	public static final int GL_COLOR_ARRAY_STRIDE =            0x8083;
	public static final int GL_TEXTURE_COORD_ARRAY_SIZE =      0x8088;
	public static final int GL_TEXTURE_COORD_ARRAY_TYPE =      0x8089;
	public static final int GL_TEXTURE_COORD_ARRAY_STRIDE =    0x808A;
	public static final int GL_VERTEX_ARRAY_POINTER =          0x808E;
	public static final int GL_NORMAL_ARRAY_POINTER =          0x808F;
	public static final int GL_COLOR_ARRAY_POINTER =           0x8090;
	public static final int GL_TEXTURE_COORD_ARRAY_POINTER =   0x8092;
	public static final int GL_SAMPLE_BUFFERS =                0x80A8;
	public static final int GL_SAMPLES =                       0x80A9;
	public static final int GL_SAMPLE_COVERAGE_VALUE =         0x80AA;
	public static final int GL_SAMPLE_COVERAGE_INVERT =        0x80AB;
	
	/* GetTextureParameter */
	/*      GL_TEXTURE_MAG_FILTER */
	/*      GL_TEXTURE_MIN_FILTER */
	/*      GL_TEXTURE_WRAP_S */
	/*      GL_TEXTURE_WRAP_T */
	
	public static final int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 0x86A2;
	public static final int GL_COMPRESSED_TEXTURE_FORMATS =    0x86A3;
	
	/* HintMode */
	public static final int GL_DONT_CARE =                     0x1100;
	public static final int GL_FASTEST =                       0x1101;
	public static final int GL_NICEST =                        0x1102;
	
	/* HintTarget */
	public static final int GL_PERSPECTIVE_CORRECTION_HINT =   0x0C50;
	public static final int GL_POINT_SMOOTH_HINT =             0x0C51;
	public static final int GL_LINE_SMOOTH_HINT =              0x0C52;
	public static final int GL_FOG_HINT =                      0x0C54;
	public static final int GL_GENERATE_MIPMAP_HINT =          0x8192;
	
	/* LightModelParameter */
	public static final int GL_LIGHT_MODEL_AMBIENT =           0x0B53;
	public static final int GL_LIGHT_MODEL_TWO_SIDE =          0x0B52;
	
	/* LightParameter */
	public static final int GL_AMBIENT =                       0x1200;
	public static final int GL_DIFFUSE =                       0x1201;
	public static final int GL_SPECULAR =                      0x1202;
	public static final int GL_POSITION =                      0x1203;
	public static final int GL_SPOT_DIRECTION =                0x1204;
	public static final int GL_SPOT_EXPONENT =                 0x1205;
	public static final int GL_SPOT_CUTOFF =                   0x1206;
	public static final int GL_CONSTANT_ATTENUATION =          0x1207;
	public static final int GL_LINEAR_ATTENUATION =            0x1208;
	public static final int GL_QUADRATIC_ATTENUATION =         0x1209;
	
	/* DataType */
	public static final int GL_BYTE =                          0x1400;
	public static final int GL_UNSIGNED_BYTE =                 0x1401;
	public static final int GL_SHORT =                         0x1402;
	public static final int GL_UNSIGNED_SHORT =                0x1403;
	public static final int GL_FLOAT =                         0x1406;
	public static final int GL_FIXED =                         0x140C;
	
	/* LogicOp */
	public static final int GL_CLEAR =                         0x1500;
	public static final int GL_AND =                           0x1501;
	public static final int GL_AND_REVERSE =                   0x1502;
	public static final int GL_COPY =                          0x1503;
	public static final int GL_AND_INVERTED =                  0x1504;
	public static final int GL_NOOP =                          0x1505;
	public static final int GL_XOR =                           0x1506;
	public static final int GL_OR =                            0x1507;
	public static final int GL_NOR =                           0x1508;
	public static final int GL_EQUIV =                         0x1509;
	public static final int GL_INVERT =                        0x150A;
	public static final int GL_OR_REVERSE =                    0x150B;
	public static final int GL_COPY_INVERTED =                 0x150C;
	public static final int GL_OR_INVERTED =                   0x150D;
	public static final int GL_NAND =                          0x150E;
	public static final int GL_SET =                           0x150F;
	
	/* MaterialFace */
	/*      GL_FRONT_AND_BACK */
	
	/* MaterialParameter */
	public static final int GL_EMISSION =                      0x1600;
	public static final int GL_SHININESS =                     0x1601;
	public static final int GL_AMBIENT_AND_DIFFUSE =           0x1602;
	/*      GL_AMBIENT */
	/*      GL_DIFFUSE */
	/*      GL_SPECULAR */
	
	/* MatrixMode */
	public static final int GL_MODELVIEW =                     0x1700;
	public static final int GL_PROJECTION =                    0x1701;
	public static final int GL_TEXTURE =                       0x1702;
	
	/* NormalPointerType */
	/*      GL_BYTE */
	/*      GL_SHORT */
	/*      GL_FLOAT */
	/*      GL_FIXED */
	
	/* PixelFormat */
	public static final int GL_ALPHA =                         0x1906;
	public static final int GL_RGB =                           0x1907;
	public static final int GL_RGBA =                          0x1908;
	public static final int GL_LUMINANCE =                     0x1909;
	public static final int GL_LUMINANCE_ALPHA =               0x190A;
	
	/* PixelStoreParameter */
//	public static final int GL_UNPACK_ALIGNMENT =              0x0CF5;
//	public static final int GL_PACK_ALIGNMENT =                0x0D05;
	
	/* PixelType */
	/*      GL_UNSIGNED_BYTE */
	public static final int GL_UNSIGNED_SHORT_4_4_4_4 =        0x8033;
	public static final int GL_UNSIGNED_SHORT_5_5_5_1 =        0x8034;
	public static final int GL_UNSIGNED_SHORT_5_6_5 =          0x8363;
	
	/* ShadingModel */
	public static final int GL_FLAT =                          0x1D00;
	public static final int GL_SMOOTH =                        0x1D01;
	
	/* StencilFunction */
	/*      GL_NEVER */
	/*      GL_LESS */
	/*      GL_EQUAL */
	/*      GL_LEQUAL */
	/*      GL_GREATER */
	/*      GL_NOTEQUAL */
	/*      GL_GEQUAL */
	/*      GL_ALWAYS */
	
	/* StencilOp */
	/*      GL_ZERO */
	public static final int GL_KEEP =                          0x1E00;
	public static final int GL_REPLACE =                       0x1E01;
	public static final int GL_INCR =                          0x1E02;
	public static final int GL_DECR =                          0x1E03;
	/*      GL_INVERT */
	
	/* StringName */
	public static final int GL_VENDOR =                        0x1F00;
	public static final int GL_RENDERER =                      0x1F01;
	public static final int GL_VERSION =                       0x1F02;
	public static final int GL_EXTENSIONS =                    0x1F03;
	
	/* TexCoordPointerType */
	/*      GL_SHORT */
	/*      GL_FLOAT */
	/*      GL_FIXED */
	/*      GL_BYTE */
	
	/* TextureEnvMode */
	public static final int GL_MODULATE =                      0x2100;
	public static final int GL_DECAL =                         0x2101;
	/*      GL_BLEND */
	public static final int GL_ADD =                           0x0104;
	/*      GL_REPLACE */
	
	/* TextureEnvParameter */
	public static final int GL_TEXTURE_ENV_MODE =              0x2200;
	public static final int GL_TEXTURE_ENV_COLOR =             0x2201;
	
	/* TextureEnvTarget */
	public static final int GL_TEXTURE_ENV =                   0x2300;
	
	/* TextureMagFilter */
	public static final int GL_NEAREST =                       0x2600;
	public static final int GL_LINEAR =                        0x2601;
	
	/* TextureMinFilter */
	/*      GL_NEAREST */
	/*      GL_LINEAR */
	public static final int GL_NEAREST_MIPMAP_NEAREST =        0x2700;
	public static final int GL_LINEAR_MIPMAP_NEAREST =         0x2701;
	public static final int GL_NEAREST_MIPMAP_LINEAR =         0x2702;
	public static final int GL_LINEAR_MIPMAP_LINEAR =          0x2703;
	
	/* TextureParameterName */
	public static final int GL_TEXTURE_MAG_FILTER =            0x2800;
	public static final int GL_TEXTURE_MIN_FILTER =            0x2801;
	public static final int GL_TEXTURE_WRAP_S =                0x2802;
	public static final int GL_TEXTURE_WRAP_T =                0x2803;
	public static final int GL_GENERATE_MIPMAP =               0x8191;
	
	/* TextureTarget */
	/*      GL_TEXTURE_2D */
	
	/* TextureUnit */
	public static final int GL_TEXTURE0 =                      0x84C0;
	public static final int GL_TEXTURE1 =                      0x84C1;
	public static final int GL_TEXTURE2 =                      0x84C2;
	public static final int GL_TEXTURE3 =                      0x84C3;
	public static final int GL_TEXTURE4 =                      0x84C4;
	public static final int GL_TEXTURE5 =                      0x84C5;
	public static final int GL_TEXTURE6 =                      0x84C6;
	public static final int GL_TEXTURE7 =                      0x84C7;
	public static final int GL_TEXTURE8 =                      0x84C8;
	public static final int GL_TEXTURE9 =                      0x84C9;
	public static final int GL_TEXTURE10 =                     0x84CA;
	public static final int GL_TEXTURE11 =                     0x84CB;
	public static final int GL_TEXTURE12 =                     0x84CC;
	public static final int GL_TEXTURE13 =                     0x84CD;
	public static final int GL_TEXTURE14 =                     0x84CE;
	public static final int GL_TEXTURE15 =                     0x84CF;
	public static final int GL_TEXTURE16 =                     0x84D0;
	public static final int GL_TEXTURE17 =                     0x84D1;
	public static final int GL_TEXTURE18 =                     0x84D2;
	public static final int GL_TEXTURE19 =                     0x84D3;
	public static final int GL_TEXTURE20 =                     0x84D4;
	public static final int GL_TEXTURE21 =                     0x84D5;
	public static final int GL_TEXTURE22 =                     0x84D6;
	public static final int GL_TEXTURE23 =                     0x84D7;
	public static final int GL_TEXTURE24 =                     0x84D8;
	public static final int GL_TEXTURE25 =                     0x84D9;
	public static final int GL_TEXTURE26 =                     0x84DA;
	public static final int GL_TEXTURE27 =                     0x84DB;
	public static final int GL_TEXTURE28 =                     0x84DC;
	public static final int GL_TEXTURE29 =                     0x84DD;
	public static final int GL_TEXTURE30 =                     0x84DE;
	public static final int GL_TEXTURE31 =                     0x84DF;
	public static final int GL_ACTIVE_TEXTURE =                0x84E0;
	public static final int GL_CLIENT_ACTIVE_TEXTURE =         0x84E1;
	
	/* TextureWrapMode */
	public static final int GL_REPEAT =                        0x2901;
	public static final int GL_CLAMP_TO_EDGE =                 0x812F;
	
	/* VertexPointerType */
	/*      GL_SHORT */
	/*      GL_FLOAT */
	/*      GL_FIXED */
	/*      GL_BYTE */
	
	/* LightName */
	public static final int GL_LIGHT0 =                        0x4000;
	public static final int GL_LIGHT1 =                        0x4001;
	public static final int GL_LIGHT2 =                        0x4002;
	public static final int GL_LIGHT3 =                        0x4003;
	public static final int GL_LIGHT4 =                        0x4004;
	public static final int GL_LIGHT5 =                        0x4005;
	public static final int GL_LIGHT6 =                        0x4006;
	public static final int GL_LIGHT7 =                        0x4007;
	
	/* Buffer Objects */
	public static final int GL_ARRAY_BUFFER =                  0x8892;
	public static final int GL_ELEMENT_ARRAY_BUFFER =          0x8893;
	
	public static final int GL_ARRAY_BUFFER_BINDING =              0x8894;
	public static final int GL_ELEMENT_ARRAY_BUFFER_BINDING =      0x8895;
	public static final int GL_VERTEX_ARRAY_BUFFER_BINDING =       0x8896;
	public static final int GL_NORMAL_ARRAY_BUFFER_BINDING =       0x8897;
	public static final int GL_COLOR_ARRAY_BUFFER_BINDING =        0x8898;
	public static final int GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING = 0x889A;
	
	public static final int GL_STATIC_DRAW =                   0x88E4;
	public static final int GL_DYNAMIC_DRAW =                  0x88E8;
	
	public static final int GL_BUFFER_SIZE =                   0x8764;
	public static final int GL_BUFFER_USAGE =                  0x8765;
	
	/* Texture combine + dot3 */
	public static final int GL_SUBTRACT =                      0x84E7;
	public static final int GL_COMBINE =                       0x8570;
	public static final int GL_COMBINE_RGB =                   0x8571;
	public static final int GL_COMBINE_ALPHA =                 0x8572;
	public static final int GL_RGB_SCALE =                     0x8573;
	public static final int GL_ADD_SIGNED =                    0x8574;
	public static final int GL_INTERPOLATE =                   0x8575;
	public static final int GL_CONSTANT =                      0x8576;
	public static final int GL_PRIMARY_COLOR =                 0x8577;
	public static final int GL_PREVIOUS =                      0x8578;
	public static final int GL_OPERAND0_RGB =                  0x8590;
	public static final int GL_OPERAND1_RGB =                  0x8591;
	public static final int GL_OPERAND2_RGB =                  0x8592;
	public static final int GL_OPERAND0_ALPHA =                0x8598;
	public static final int GL_OPERAND1_ALPHA =                0x8599;
	public static final int GL_OPERAND2_ALPHA =                0x859A;
	
	public static final int GL_ALPHA_SCALE =                   0x0D1C;
	
	public static final int GL_SRC0_RGB =                      0x8580;
	public static final int GL_SRC1_RGB =                      0x8581;
	public static final int GL_SRC2_RGB =                      0x8582;
	public static final int GL_SRC0_ALPHA =                    0x8588;
	public static final int GL_SRC1_ALPHA =                    0x8589;
	public static final int GL_SRC2_ALPHA =                    0x858A;
	
	public static final int GL_DOT3_RGB =                      0x86AE;
	public static final int GL_DOT3_RGBA =                     0x86AF;
	
	// glextplatform.h
	
	/* Renamed for OpenGL ES 1.1 */
	
	public static final int GL_WRITE_ONLY =                              0x88B9;
	public static final int GL_BUFFER_ACCESS =                           0x88BB;
	
	/* BeginMode */
	public static final int GL_MAX_ELEMENTS_VERTICES =                   0x80E8;
	public static final int GL_MAX_ELEMENTS_INDICES =                    0x80E9;

	/* HintTarget */
	public static final int GL_POLYGON_SMOOTH_HINT =                     0x0C53;
	
}
