/*
 * Copyright 2023 Yury Kharchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mexa.opgl;

import emulator.AppSettings;
import emulator.Settings;
import emulator.graphics2D.IImage;
import emulator.graphics2D.awt.Graphics2DAWT;
import emulator.graphics2D.swt.Graphics2DSWT;
import emulator.graphics3D.egl.EGL10Impl;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;

import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import javax.microedition.lcdui.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.nio.ByteOrder;
import java.nio.IntBuffer;


/** @noinspection unused*/
public class OpglGraphics {
	public static final int GL_ACTIVE_TEXTURE = 34016;
	public static final int GL_ADD = 260;
	public static final int GL_ADD_SIGNED = 34164;
	public static final int GL_ALIASED_LINE_WIDTH_RANGE = 33902;
	public static final int GL_ALIASED_POINT_SIZE_RANGE = 33901;
	public static final int GL_ALPHA = 6406;
	public static final int GL_ALPHA_BITS = 3413;
	public static final int GL_ALPHA_SCALE = 3356;
	public static final int GL_ALPHA_TEST = 3008;
	public static final int GL_ALPHA_TEST_FUNC = 3009;
	public static final int GL_ALPHA_TEST_REF = 3010;
	public static final int GL_ALWAYS = 519;
	public static final int GL_AMBIENT = 4608;
	public static final int GL_AMBIENT_AND_DIFFUSE = 5634;
	public static final int GL_AND = 5377;
	public static final int GL_AND_INVERTED = 5380;
	public static final int GL_AND_REVERSE = 5378;
	public static final int GL_ARRAY_BUFFER = 34962;
	public static final int GL_ARRAY_BUFFER_BINDING = 34964;
	public static final int GL_BACK = 1029;
	public static final int GL_BLEND = 3042;
	public static final int GL_BLEND_DST = 3040;
	public static final int GL_BLEND_SRC = 3041;
	public static final int GL_BLUE_BITS = 3412;
	public static final int GL_BUFFER_SIZE = 34660;
	public static final int GL_BUFFER_USAGE = 34661;
	public static final int GL_BYTE = 5120;
	public static final int GL_CCW = 2305;
	public static final int GL_CLAMP_TO_EDGE = 33071;
	public static final int GL_CLEAR = 5376;
	public static final int GL_CLIENT_ACTIVE_TEXTURE = 34017;
	public static final int GL_CLIP_PLANE0 = 12288;
	public static final int GL_CLIP_PLANE1 = 12289;
	public static final int GL_CLIP_PLANE2 = 12290;
	public static final int GL_CLIP_PLANE3 = 12291;
	public static final int GL_CLIP_PLANE4 = 12292;
	public static final int GL_CLIP_PLANE5 = 12293;
	public static final int GL_COLOR_ARRAY = 32886;
	public static final int GL_COLOR_ARRAY_BUFFER_BINDING = 34968;
	public static final int GL_COLOR_ARRAY_POINTER = 32912;
	public static final int GL_COLOR_ARRAY_SIZE = 32897;
	public static final int GL_COLOR_ARRAY_STRIDE = 32899;
	public static final int GL_COLOR_ARRAY_TYPE = 32898;
	public static final int GL_COLOR_BUFFER_BIT = 16384;
	public static final int GL_COLOR_CLEAR_VALUE = 3106;
	public static final int GL_COLOR_LOGIC_OP = 3058;
	public static final int GL_COLOR_MATERIAL = 2903;
	public static final int GL_COLOR_WRITEMASK = 3107;
	public static final int GL_COMBINE = 34160;
	public static final int GL_COMBINE_ALPHA = 34162;
	public static final int GL_COMBINE_RGB = 34161;
	public static final int GL_COMPRESSED_TEXTURE_FORMATS = 34467;
	public static final int GL_CONSTANT = 34166;
	public static final int GL_CONSTANT_ATTENUATION = 4615;
	public static final int GL_COORD_REPLACE_OES = 34914;
	public static final int GL_COPY = 5379;
	public static final int GL_COPY_INVERTED = 5388;
	public static final int GL_CULL_FACE = 2884;
	public static final int GL_CULL_FACE_MODE = 2885;
	public static final int GL_CURRENT_COLOR = 2816;
	public static final int GL_CURRENT_NORMAL = 2818;
	public static final int GL_CURRENT_PALETTE_MATRIX_OES = 34883;
	public static final int GL_CURRENT_TEXTURE_COORDS = 2819;
	public static final int GL_CW = 2304;
	public static final int GL_DECAL = 8449;
	public static final int GL_DECR = 7683;
	public static final int GL_DEPTH_BITS = 3414;
	public static final int GL_DEPTH_BUFFER_BIT = 256;
	public static final int GL_DEPTH_CLEAR_VALUE = 2931;
	public static final int GL_DEPTH_FUNC = 2932;
	public static final int GL_DEPTH_RANGE = 2928;
	public static final int GL_DEPTH_TEST = 2929;
	public static final int GL_DEPTH_WRITEMASK = 2930;
	public static final int GL_DIFFUSE = 4609;
	public static final int GL_DITHER = 3024;
	public static final int GL_DONT_CARE = 4352;
	public static final int GL_DOT3_RGB = 34478;
	public static final int GL_DOT3_RGBA = 34479;
	public static final int GL_DST_ALPHA = 772;
	public static final int GL_DST_COLOR = 774;
	public static final int GL_DYNAMIC_DRAW = 35048;
	public static final int GL_ELEMENT_ARRAY_BUFFER = 34963;
	public static final int GL_ELEMENT_ARRAY_BUFFER_BINDING = 34965;
	public static final int GL_EMISSION = 5632;
	public static final int GL_EQUAL = 514;
	public static final int GL_EQUIV = 5385;
	public static final int GL_EXP = 2048;
	public static final int GL_EXP2 = 2049;
	public static final int GL_EXTENSIONS = 7939;
	public static final int GL_FALSE = 0;
	public static final int GL_FASTEST = 4353;
	public static final int GL_FLAT = 7424;
	public static final int GL_FLOAT = 5126;
	public static final int GL_FOG = 2912;
	public static final int GL_FOG_COLOR = 2918;
	public static final int GL_FOG_DENSITY = 2914;
	public static final int GL_FOG_END = 2916;
	public static final int GL_FOG_HINT = 3156;
	public static final int GL_FOG_MODE = 2917;
	public static final int GL_FOG_START = 2915;
	public static final int GL_FRONT = 1028;
	public static final int GL_FRONT_AND_BACK = 1032;
	public static final int GL_FRONT_FACE = 2886;
	public static final int GL_GENERATE_MIPMAP = 33169;
	public static final int GL_GENERATE_MIPMAP_HINT = 33170;
	public static final int GL_GEQUAL = 518;
	public static final int GL_GREATER = 516;
	public static final int GL_GREEN_BITS = 3411;
	public static final int GL_INCR = 7682;
	public static final int GL_INTERPOLATE = 34165;
	public static final int GL_INVALID_ENUM = 1280;
	public static final int GL_INVALID_OPERATION = 1282;
	public static final int GL_INVALID_VALUE = 1281;
	public static final int GL_INVERT = 5386;
	public static final int GL_KEEP = 7680;
	public static final int GL_LEQUAL = 515;
	public static final int GL_LESS = 513;
	public static final int GL_LIGHT_MODEL_AMBIENT = 2899;
	public static final int GL_LIGHT_MODEL_TWO_SIDE = 2898;
	public static final int GL_LIGHT0 = 16384;
	public static final int GL_LIGHT1 = 16385;
	public static final int GL_LIGHT2 = 16386;
	public static final int GL_LIGHT3 = 16387;
	public static final int GL_LIGHT4 = 16388;
	public static final int GL_LIGHT5 = 16389;
	public static final int GL_LIGHT6 = 16390;
	public static final int GL_LIGHT7 = 16391;
	public static final int GL_LIGHTING = 2896;
	public static final int GL_LINE_LOOP = 2;
	public static final int GL_LINE_SMOOTH = 2848;
	public static final int GL_LINE_SMOOTH_HINT = 3154;
	public static final int GL_LINE_STRIP = 3;
	public static final int GL_LINE_WIDTH = 2849;
	public static final int GL_LINEAR = 9729;
	public static final int GL_LINEAR_ATTENUATION = 4616;
	public static final int GL_LINEAR_MIPMAP_LINEAR = 9987;
	public static final int GL_LINEAR_MIPMAP_NEAREST = 9985;
	public static final int GL_LINES = 1;
	public static final int GL_LOGIC_OP_MODE = 3056;
	public static final int GL_LUMINANCE = 6409;
	public static final int GL_LUMINANCE_ALPHA = 6410;
	public static final int GL_MATRIX_INDEX_ARRAY_BUFFER_BINDING_OES = 35742;
	public static final int GL_MATRIX_INDEX_ARRAY_OES = 34884;
	public static final int GL_MATRIX_INDEX_ARRAY_POINTER_OES = 34889;
	public static final int GL_MATRIX_INDEX_ARRAY_SIZE_OES = 34886;
	public static final int GL_MATRIX_INDEX_ARRAY_STRIDE_OES = 34888;
	public static final int GL_MATRIX_INDEX_ARRAY_TYPE_OES = 34887;
	public static final int GL_MATRIX_MODE = 2976;
	public static final int GL_MATRIX_PALETTE_OES = 34880;
	public static final int GL_MAX_CLIP_PLANES = 3378;
	public static final int GL_MAX_LIGHTS = 3377;
	public static final int GL_MAX_MODELVIEW_STACK_DEPTH = 3382;
	public static final int GL_MAX_PALETTE_MATRICES_OES = 34882;
	public static final int GL_MAX_PROJECTION_STACK_DEPTH = 3384;
	public static final int GL_MAX_TEXTURE_SIZE = 3379;
	public static final int GL_MAX_TEXTURE_STACK_DEPTH = 3385;
	public static final int GL_MAX_TEXTURE_UNITS = 34018;
	public static final int GL_MAX_VERTEX_UNITS_OES = 34468;
	public static final int GL_MAX_VIEWPORT_DIMS = 3386;
	public static final int GL_MODELVIEW = 5888;
	public static final int GL_MODELVIEW_MATRIX = 2982;
	public static final int GL_MODELVIEW_MATRIX_FLOAT_AS_INT_BITS_OES = 35213;
	public static final int GL_MODELVIEW_STACK_DEPTH = 2979;
	public static final int GL_MODULATE = 8448;
	public static final int GL_MULTISAMPLE = 32925;
	public static final int GL_NAND = 5390;
	public static final int GL_NEAREST = 9728;
	public static final int GL_NEAREST_MIPMAP_LINEAR = 9986;
	public static final int GL_NEAREST_MIPMAP_NEAREST = 9984;
	public static final int GL_NEVER = 512;
	public static final int GL_NICEST = 4354;
	public static final int GL_NO_ERROR = 0;
	public static final int GL_NOOP = 5381;
	public static final int GL_NOR = 5384;
	public static final int GL_NORMAL_ARRAY = 32885;
	public static final int GL_NORMAL_ARRAY_BUFFER_BINDING = 34967;
	public static final int GL_NORMAL_ARRAY_POINTER = 32911;
	public static final int GL_NORMAL_ARRAY_STRIDE = 32895;
	public static final int GL_NORMAL_ARRAY_TYPE = 32894;
	public static final int GL_NORMALIZE = 2977;
	public static final int GL_NOTEQUAL = 517;
	public static final int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 34466;
	public static final int GL_ONE = 1;
	public static final int GL_ONE_MINUS_DST_ALPHA = 773;
	public static final int GL_ONE_MINUS_DST_COLOR = 775;
	public static final int GL_ONE_MINUS_SRC_ALPHA = 771;
	public static final int GL_ONE_MINUS_SRC_COLOR = 769;
	public static final int GL_OPERAND0_ALPHA = 34200;
	public static final int GL_OPERAND0_RGB = 34192;
	public static final int GL_OPERAND1_ALPHA = 34201;
	public static final int GL_OPERAND1_RGB = 34193;
	public static final int GL_OPERAND2_ALPHA = 34202;
	public static final int GL_OPERAND2_RGB = 34194;
	public static final int GL_OR = 5383;
	public static final int GL_OR_INVERTED = 5389;
	public static final int GL_OR_REVERSE = 5387;
	public static final int GL_OUT_OF_MEMORY = 1285;
	public static final int GL_PACK_ALIGNMENT = 3333;
	public static final int GL_PALETTE4_R5_G6_B5_OES = 35730;
	public static final int GL_PALETTE4_RGB5_A1_OES = 35732;
	public static final int GL_PALETTE4_RGB8_OES = 35728;
	public static final int GL_PALETTE4_RGBA4_OES = 35731;
	public static final int GL_PALETTE4_RGBA8_OES = 35729;
	public static final int GL_PALETTE8_R5_G6_B5_OES = 35735;
	public static final int GL_PALETTE8_RGB5_A1_OES = 35737;
	public static final int GL_PALETTE8_RGB8_OES = 35733;
	public static final int GL_PALETTE8_RGBA4_OES = 35736;
	public static final int GL_PALETTE8_RGBA8_OES = 35734;
	public static final int GL_PERSPECTIVE_CORRECTION_HINT = 3152;
	public static final int GL_POINT_DISTANCE_ATTENUATION = 33065;
	public static final int GL_POINT_FADE_THRESHOLD_SIZE = 33064;
	public static final int GL_POINT_SIZE = 2833;
	public static final int GL_POINT_SIZE_ARRAY_BUFFER_BINDING_OES = 35743;
	public static final int GL_POINT_SIZE_ARRAY_OES = 35740;
	public static final int GL_POINT_SIZE_ARRAY_POINTER_OES = 35212;
	public static final int GL_POINT_SIZE_ARRAY_STRIDE_OES = 35211;
	public static final int GL_POINT_SIZE_ARRAY_TYPE_OES = 35210;
	public static final int GL_POINT_SIZE_MAX = 33063;
	public static final int GL_POINT_SIZE_MIN = 33062;
	public static final int GL_POINT_SMOOTH = 2832;
	public static final int GL_POINT_SMOOTH_HINT = 3153;
	public static final int GL_POINT_SPRITE_OES = 34913;
	public static final int GL_POINTS = 0;
	public static final int GL_POLYGON_OFFSET_FACTOR = 32824;
	public static final int GL_POLYGON_OFFSET_FILL = 32823;
	public static final int GL_POLYGON_OFFSET_UNITS = 10752;
	public static final int GL_POLYGON_SMOOTH_HINT = 3155;
	public static final int GL_POSITION = 4611;
	public static final int GL_PREVIOUS = 34168;
	public static final int GL_PRIMARY_COLOR = 34167;
	public static final int GL_PROJECTION = 5889;
	public static final int GL_PROJECTION_MATRIX = 2983;
	public static final int GL_PROJECTION_MATRIX_FLOAT_AS_INT_BITS_OES = 35214;
	public static final int GL_PROJECTION_STACK_DEPTH = 2980;
	public static final int GL_QUADRATIC_ATTENUATION = 4617;
	public static final int GL_RED_BITS = 3410;
	public static final int GL_RENDERER = 7937;
	public static final int GL_REPEAT = 10497;
	public static final int GL_REPLACE = 7681;
	public static final int GL_RESCALE_NORMAL = 32826;
	public static final int GL_RGB = 6407;
	public static final int GL_RGB_SCALE = 34163;
	public static final int GL_RGBA = 6408;
	public static final int GL_SAMPLE_ALPHA_TO_COVERAGE = 32926;
	public static final int GL_SAMPLE_ALPHA_TO_ONE = 32927;
	public static final int GL_SAMPLE_BUFFERS = 32936;
	public static final int GL_SAMPLE_COVERAGE = 32928;
	public static final int GL_SAMPLE_COVERAGE_INVERT = 32939;
	public static final int GL_SAMPLE_COVERAGE_VALUE = 32938;
	public static final int GL_SAMPLES = 32937;
	public static final int GL_SCISSOR_BOX = 3088;
	public static final int GL_SCISSOR_TEST = 3089;
	public static final int GL_SET = 5391;
	public static final int GL_SHADE_MODEL = 2900;
	public static final int GL_SHININESS = 5633;
	public static final int GL_SHORT = 5122;
	public static final int GL_SMOOTH = 7425;
	public static final int GL_SMOOTH_LINE_WIDTH_RANGE = 2850;
	public static final int GL_SMOOTH_POINT_SIZE_RANGE = 2834;
	public static final int GL_SPECULAR = 4610;
	public static final int GL_SPOT_CUTOFF = 4614;
	public static final int GL_SPOT_DIRECTION = 4612;
	public static final int GL_SPOT_EXPONENT = 4613;
	public static final int GL_SRC_ALPHA = 770;
	public static final int GL_SRC_ALPHA_SATURATE = 776;
	public static final int GL_SRC_COLOR = 768;
	public static final int GL_SRC0_ALPHA = 34184;
	public static final int GL_SRC0_RGB = 34176;
	public static final int GL_SRC1_ALPHA = 34185;
	public static final int GL_SRC1_RGB = 34177;
	public static final int GL_SRC2_ALPHA = 34186;
	public static final int GL_SRC2_RGB = 34178;
	public static final int GL_STACK_OVERFLOW = 1283;
	public static final int GL_STACK_UNDERFLOW = 1284;
	public static final int GL_STATIC_DRAW = 35044;
	public static final int GL_STENCIL_BITS = 3415;
	public static final int GL_STENCIL_BUFFER_BIT = 1024;
	public static final int GL_STENCIL_CLEAR_VALUE = 2961;
	public static final int GL_STENCIL_FAIL = 2964;
	public static final int GL_STENCIL_FUNC = 2962;
	public static final int GL_STENCIL_PASS_DEPTH_FAIL = 2965;
	public static final int GL_STENCIL_PASS_DEPTH_PASS = 2966;
	public static final int GL_STENCIL_REF = 2967;
	public static final int GL_STENCIL_TEST = 2960;
	public static final int GL_STENCIL_VALUE_MASK = 2963;
	public static final int GL_STENCIL_WRITEMASK = 2968;
	public static final int GL_SUBPIXEL_BITS = 3408;
	public static final int GL_SUBTRACT = 34023;
	public static final int GL_TEXTURE = 5890;
	public static final int GL_TEXTURE_2D = 3553;
	public static final int GL_TEXTURE_BINDING_2D = 32873;
	public static final int GL_TEXTURE_COORD_ARRAY = 32888;
	public static final int GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING = 34970;
	public static final int GL_TEXTURE_COORD_ARRAY_POINTER = 32914;
	public static final int GL_TEXTURE_COORD_ARRAY_SIZE = 32904;
	public static final int GL_TEXTURE_COORD_ARRAY_STRIDE = 32906;
	public static final int GL_TEXTURE_COORD_ARRAY_TYPE = 32905;
	public static final int GL_TEXTURE_CROP_RECT_OES = 35741;
	public static final int GL_TEXTURE_ENV = 8960;
	public static final int GL_TEXTURE_ENV_COLOR = 8705;
	public static final int GL_TEXTURE_ENV_MODE = 8704;
	public static final int GL_TEXTURE_MAG_FILTER = 10240;
	public static final int GL_TEXTURE_MATRIX = 2984;
	public static final int GL_TEXTURE_MATRIX_FLOAT_AS_INT_BITS_OES = 35215;
	public static final int GL_TEXTURE_MIN_FILTER = 10241;
	public static final int GL_TEXTURE_STACK_DEPTH = 2981;
	public static final int GL_TEXTURE_WRAP_S = 10242;
	public static final int GL_TEXTURE_WRAP_T = 10243;
	public static final int GL_TEXTURE0 = 33984;
	public static final int GL_TEXTURE1 = 33985;
	public static final int GL_TEXTURE10 = 33994;
	public static final int GL_TEXTURE11 = 33995;
	public static final int GL_TEXTURE12 = 33996;
	public static final int GL_TEXTURE13 = 33997;
	public static final int GL_TEXTURE14 = 33998;
	public static final int GL_TEXTURE15 = 33999;
	public static final int GL_TEXTURE16 = 34000;
	public static final int GL_TEXTURE17 = 34001;
	public static final int GL_TEXTURE18 = 34002;
	public static final int GL_TEXTURE19 = 34003;
	public static final int GL_TEXTURE2 = 33986;
	public static final int GL_TEXTURE20 = 34004;
	public static final int GL_TEXTURE21 = 34005;
	public static final int GL_TEXTURE22 = 34006;
	public static final int GL_TEXTURE23 = 34007;
	public static final int GL_TEXTURE24 = 34008;
	public static final int GL_TEXTURE25 = 34009;
	public static final int GL_TEXTURE26 = 34010;
	public static final int GL_TEXTURE27 = 34011;
	public static final int GL_TEXTURE28 = 34012;
	public static final int GL_TEXTURE29 = 34013;
	public static final int GL_TEXTURE3 = 33987;
	public static final int GL_TEXTURE30 = 34014;
	public static final int GL_TEXTURE31 = 34015;
	public static final int GL_TEXTURE4 = 33988;
	public static final int GL_TEXTURE5 = 33989;
	public static final int GL_TEXTURE6 = 33990;
	public static final int GL_TEXTURE7 = 33991;
	public static final int GL_TEXTURE8 = 33992;
	public static final int GL_TEXTURE9 = 33993;
	public static final int GL_TRIANGLE_FAN = 6;
	public static final int GL_TRIANGLE_STRIP = 5;
	public static final int GL_TRIANGLES = 4;
	public static final int GL_TRUE = 1;
	public static final int GL_UNPACK_ALIGNMENT = 3317;
	public static final int GL_UNSIGNED_BYTE = 5121;
	public static final int GL_UNSIGNED_SHORT = 5123;
	public static final int GL_UNSIGNED_SHORT_4_4_4_4 = 32819;
	public static final int GL_UNSIGNED_SHORT_5_5_5_1 = 32820;
	public static final int GL_UNSIGNED_SHORT_5_6_5 = 33635;
	public static final int GL_VENDOR = 7936;
	public static final int GL_VERSION = 7938;
	public static final int GL_VERTEX_ARRAY = 32884;
	public static final int GL_VERTEX_ARRAY_BUFFER_BINDING = 34966;
	public static final int GL_VERTEX_ARRAY_POINTER = 32910;
	public static final int GL_VERTEX_ARRAY_SIZE = 32890;
	public static final int GL_VERTEX_ARRAY_STRIDE = 32892;
	public static final int GL_VERTEX_ARRAY_TYPE = 32891;
	public static final int GL_VIEWPORT = 2978;
	public static final int GL_WEIGHT_ARRAY_BUFFER_BINDING_OES = 34974;
	public static final int GL_WEIGHT_ARRAY_OES = 34477;
	public static final int GL_WEIGHT_ARRAY_POINTER_OES = 34476;
	public static final int GL_WEIGHT_ARRAY_SIZE_OES = 34475;
	public static final int GL_WEIGHT_ARRAY_STRIDE_OES = 34474;
	public static final int GL_WEIGHT_ARRAY_TYPE_OES = 34473;
	public static final int GL_WRITE_ONLY = 35001;
	public static final int GL_XOR = 5382;
	public static final int GL_ZERO = 0;

	private static OpglGraphics INSTANCE;
	private final EGL10 egl;
	private final EGLDisplay eglDisplay;
	private final EGLConfig eglConfig;
	private final EGLContext eglContext;
	private final GL11 gl;
	private Graphics graphics;
	private EGLSurface eglWindowSurface;
	private java.nio.ByteBuffer pixelBuffer;

	private static final PaletteData swtPalleteData = new PaletteData(-16777216, 16711680, '\uff00');
	private static BufferedImage awtImageBuffer;
	private static ImageData swtImageBuffer;

	private OpglGraphics() {
		AppSettings.m3gThread = true;
		egl = (EGL10) EGLContext.getEGL();
		eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		egl.eglInitialize(eglDisplay, null);

		int[] configAttrs = {
				EGL10.EGL_RED_SIZE, 8,
				EGL10.EGL_GREEN_SIZE, 8,
				EGL10.EGL_BLUE_SIZE, 8,
				EGL10.EGL_ALPHA_SIZE, 8,
				EGL10.EGL_DEPTH_SIZE, 16,
				EGL10.EGL_STENCIL_SIZE, EGL10.EGL_DONT_CARE,
				EGL10.EGL_NONE
		};
		EGLConfig[] eglConfigs = new EGLConfig[1];
		egl.eglChooseConfig(eglDisplay, configAttrs, eglConfigs, 1, null);
		eglConfig = eglConfigs[0];

		eglContext = egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, null);
		this.gl = (GL11) eglContext.getGL();
	}

	public static synchronized OpglGraphics getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new OpglGraphics();
		}
		return INSTANCE;
	}

	public void bind(Object target) {
		if (target == null) {
			throw new NullPointerException("target is NULL");
		} else if (target == graphics) {
			throw new IllegalStateException();
		} else if (!(target instanceof Graphics)) {
			throw new IllegalArgumentException();
		}
		if (eglWindowSurface != null) {
			egl.eglDestroySurface(eglDisplay, eglWindowSurface);
		}

		eglWindowSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, target, null);

		bindEglContext();
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	}

	public void release() {
		if (graphics == null) return;
		gl.glFinish();
		egl.eglSwapBuffers(eglDisplay, eglWindowSurface);
		releaseEglContext();
		graphics = null;
	}

	private void bindEglContext() {
		egl.eglMakeCurrent(eglDisplay, eglWindowSurface, eglWindowSurface, eglContext);
	}

	private void releaseEglContext() {
		egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
	}

	public void glActiveTexture(int texture) {
		gl.glActiveTexture(texture);
	}

	public void glAlphaFunc(int func, float ref) {
		gl.glAlphaFunc(func, ref);
	}

	public void glBindTexture(int target, int texture) {
		gl.glBindTexture(target, texture);
	}

	public void glBlendFunc(int sfactor, int dfactor) {
		gl.glBlendFunc(sfactor, dfactor);
	}

	public void glClear(int mask) {
		gl.glClear(mask);
	}

	public void glClearColor(float red, float green, float blue, float alpha) {
		gl.glClearColor(red, green, blue, alpha);
	}

	public void glClearDepthf(float depth) {
		gl.glClearDepthf(depth);
	}

	public void glClearStencil(int s) {
		gl.glClearStencil(s);
	}

	public void glClientActiveTexture(int texture) {
		gl.glClientActiveTexture(texture);
	}

	public void glColor4f(float red, float green, float blue, float alpha) {
		gl.glColor4f(red, green, blue, alpha);
	}

	public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		gl.glColorMask(red, green, blue, alpha);
	}

	public void glColorPointer(int size, int type, int stride, Buffer pointer) {
		gl.glColorPointer(size, type, stride, pointer.getNioBuffer());
	}

	public void glColorPointer(int size, int type, int stride, int offset) {
		gl.glColorPointer(size, type, stride, offset);
	}

    public void glCompressedTexImage2D(int target,
                                       int level,
                                       int internalformat,
                                       int width,
                                       int height,
                                       int border,
                                       ByteBuffer data) {
		gl.glCompressedTexImage2D(target, level, internalformat, width, height, border, data.length(), data.getNioBuffer());
    }

	public void glCompressedTexSubImage2D(int target,
										  int level,
										  int xoffset,
										  int yoffset,
										  int width,
										  int height,
										  int format,
										  ByteBuffer data) {
		gl.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, data.length(), data.getNioBuffer());
	}

	public void glCopyTexImage2D(int target,
								 int level,
								 int internalformat,
								 int x,
								 int y,
								 int width,
								 int height,
								 int border) {
		gl.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
	}

	public void glCopyTexSubImage2D(int target,
									int level,
									int xoffset,
									int yoffset,
									int x,
									int y,
									int width,
									int height) {
		gl.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
	}

	public void glCullFace(int mode) {
		gl.glCullFace(mode);
	}

	public void glDeleteTextures(int[] textures) {
		gl.glDeleteTextures(textures.length, textures, 0);
	}

	public void glDepthFunc(int func) {
		gl.glDepthFunc(func);
	}

	public void glDepthMask(boolean flag) {
		gl.glDepthMask(flag);
	}

	public void glDepthRangef(float zNear, float zFar) {
		gl.glDepthRangef(zNear, zFar);
	}

	public void glDisable(int cap) {
		gl.glDisable(cap);
	}

	public void glDisableClientState(int array) {
		gl.glDisableClientState(array);
	}

	public void glDrawArrays(int mode, int first, int count) {
		gl.glDrawArrays(mode, first, count);
	}

	public void glDrawElements(int mode, int type, Buffer indices) {
		gl.glDrawElements(mode, indices.length(), type, indices.getNioBuffer());
	}

	public void glDrawElements(int mode, int count, int type, int offset) {
		gl.glDrawElements(mode, count, type, offset);
	}

	public void glEnable(int cap) {
		gl.glEnable(cap);
	}

	public void glEnableClientState(int array) {
		gl.glEnableClientState(array);
	}

	public void glFlush() {
		gl.glFlush();
	}

	public void glFogf(int pname, float param) {
		gl.glFogf(pname, param);
	}

	public void glFogfv(int pname, float[] params) {
		gl.glFogfv(pname, params, 0);
	}

	public void glFrontFace(int mode) {
		gl.glFrontFace(mode);
	}

	public void glFrustumf(float left, float right, float bottom, float top, float zNear, float zFar) {
		gl.glFrustumf(left, right, bottom, top, zNear, zFar);
	}

	public void glGenTextures(int[] textures) {
		gl.glGenTextures(textures.length, textures, 0);
	}

	public int glGetError() {
		return gl.glGetError();
	}

	public void glGetIntegerv(int pname, int[] params) {
		gl.glGetIntegerv(pname, params, 0);
	}

	public String glGetString(int name) {
		return gl.glGetString(name);
	}

	public void glHint(int target, int mode) {
		gl.glHint(target, mode);
	}

	public void glLightModelf(int pname, float param) {
		gl.glLightModelf(pname, param);
	}

	public void glLightModelfv(int pname, float[] params) {
		gl.glLightModelfv(pname, params, 0);
	}

	public void glLightf(int light, int pname, float param) {
		gl.glLightf(light, pname, param);
	}

	public void glLightfv(int light, int pname, float[] params) {
		gl.glLightfv(light, pname, params, 0);
//		EGL10Impl.g3d.sync(() -> org.lwjgl.opengl.GL11.glLightfv(light, pname, params));
	}

	public void glLineWidth(float width) {
		gl.glLineWidth(width);
	}

	public void glLoadIdentity() {
		gl.glLoadIdentity();
	}

	public void glLoadMatrixf(float[] m) {
		gl.glLoadMatrixf(m, 0);
	}

	public void glLogicOp(int opcode) {
		gl.glLogicOp(opcode);
	}

	public void glMaterialf(int face, int pname, float param) {
		gl.glMaterialf(face, pname, param);
	}

	public void glMaterialfv(int face, int pname, float[] params) {
		EGL10Impl.g3d.sync(() -> org.lwjgl.opengl.GL11.glMaterialfv(face, pname, params));
	}

	public void glMatrixMode(int mode) {
		gl.glMatrixMode(mode);
	}

	public void glMultMatrixf(float[] m) {
		EGL10Impl.g3d.sync(() -> org.lwjgl.opengl.GL11.glMultMatrixf(m));
	}

	public void glMultiTexCoord4f(int target, float s, float t, float r, float q) {
		gl.glMultiTexCoord4f(target, s, t, r, q);
	}

	public void glNormal3f(float nx, float ny, float nz) {
		gl.glNormal3f(nx, ny, nz);
	}

	public void glNormalPointer(int type, int stride, Buffer pointer) {
		gl.glNormalPointer(type, stride, pointer.getNioBuffer());
	}

	public void glNormalPointer(int type, int stride, int offset) {
		gl.glNormalPointer(type, stride, offset);
	}

	public void glOrthof(float left, float right, float bottom, float top, float zNear, float zFar) {
		gl.glOrthof(left, right, bottom, top, zNear, zFar);
	}

	public void glPixelStorei(int pname, int param) {
		gl.glPixelStorei(pname, param);
	}

	public void glPointSize(float size) {
		gl.glPointSize(size);
	}

	public void glPolygonOffset(float factor, float units) {
		gl.glPolygonOffset(factor, units);
	}

	public void glPopMatrix() {
		gl.glPopMatrix();
	}

	public void glPushMatrix() {
		gl.glPushMatrix();
	}

	public void glRotatef(float angle, float x, float y, float z) {
		gl.glRotatef(angle, x, y, z);
	}

	public void glSampleCoverage(float value, boolean invert) {
		gl.glSampleCoverage(value, invert);
	}

	public void glScalef(float x, float y, float z) {
		gl.glScalef(x, y, z);
	}

	public void glScissor(int x, int y, int width, int height) {
		gl.glScissor(x, y, width, height);
	}

	public void glShadeModel(int mode) {
		gl.glShadeModel(mode);
	}

	public void glStencilFunc(int func, int ref, int mask) {
		gl.glStencilFunc(func, ref, mask);
	}

	public void glStencilMask(int mask) {
		gl.glStencilMask(mask);
	}

	public void glStencilOp(int fail, int zfail, int zpass) {
		gl.glStencilOp(fail, zfail, zpass);
	}

	public void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {
		gl.glTexCoordPointer(size, type, stride, pointer.getNioBuffer());
	}

	public void glTexCoordPointer(int size, int type, int stride, int offset) {
		gl.glTexCoordPointer(size, type, stride, offset);
	}

	public void glTexEnvf(int target, int pname, float param) {
		gl.glTexEnvf(target, pname, param);
	}

	public void glTexEnvfv(int target, int pname, float[] params) {
		gl.glTexEnvfv(target, pname, params, 0);
	}

	public void glTexImage2D(int target,
							 int level,
							 int internalformat,
							 int width,
							 int height,
							 int border,
							 int format,
							 int type,
							 Buffer pixels) {
		gl.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels.getNioBuffer());
	}

	public void glTexParameterf(int target, int pname, float param) {
		gl.glTexParameterf(target, pname, param);
	}

	public void glTexSubImage2D(int target,
								int level,
								int xoffset,
								int yoffset,
								int width,
								int height,
								int format,
								int type,
								Buffer pixels) {
		gl.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels.getNioBuffer());
	}

	public void glTranslatef(float x, float y, float z) {
		gl.glTranslatef(x, y, z);
	}

	public void glVertexPointer(int size, int type, int stride, Buffer pointer) {
		gl.glVertexPointer(size, type, stride, pointer.getNioBuffer());
	}

	public void glVertexPointer(int size, int type, int stride, int offset) {
		gl.glVertexPointer(size, type, stride, offset);
	}

	public void glViewport(int x, int y, int width, int height) {
		gl.glViewport(x, y, width, height);
	}

	public void glBindBuffer(int target, int buffer) {
		gl.glBindBuffer(target, buffer);
	}

	public void glBufferData(int target, Buffer data, int usage) {
		gl.glBufferData(target, data.length(), data.getNioBuffer(), usage);
	}

	public void glBufferSubData(int target, int offset, Buffer data) {
		gl.glBufferSubData(target, offset, data.length(), data.getNioBuffer());
	}

	public void glClipPlanef(int plane, float[] equation) {
		gl.glClipPlanef(plane, equation, 0);
	}

	public void glColor4ub(byte red, byte green, byte blue, byte alpha) {
		gl.glColor4ub(red, green, blue, alpha);
	}

	public void glDeleteBuffers(int[] buffers) {
		gl.glDeleteBuffers(buffers.length, buffers, 0);
	}

	public void glGetBooleanv(int pname, boolean[] params) {
		gl.glGetBooleanv(pname, params, 0);
	}

	public void glGetBufferParameteriv(int target, int pname, int[] params) {
		gl.glGetBufferParameteriv(target, pname, params, 0);
	}

	public void glGetClipPlanef(int pname, float[] equation) {
		gl.glGetClipPlanef(pname, equation, 0);
	}

	public void glGetFloatv(int pname, float[] params) {
		gl.glGetFloatv(pname, params, 0);
	}

	public void glGetLightfv(int light, int pname, float[] params) {
		gl.glGetLightfv(light, pname, params, 0);
	}

	public void glGetMaterialfv(int face, int pname, float[] params) {
		gl.glGetMaterialfv(face, pname, params, 0);
	}

	public void glGetTexEnvfv(int env, int pname, float[] params) {
		EGL10Impl.g3d.sync(() -> org.lwjgl.opengl.GL11.glGetTexEnvfv(env, pname, params));
	}

	public void glGetTexParameterfv(int target, int pname, float[] params) {
		gl.glGetTexParameterfv(target, pname, params, 0);
	}

	public void glGenBuffers(int[] buffers) {
		gl.glGenBuffers(buffers.length, buffers, 0);
	}

	public void glGetTexEnviv(int env, int pname, int[] params) {
		gl.glGetTexEnviv(env, pname, params, 0);
	}

	public void glGetTexParameteriv(int target, int pname, int[] params) {
		gl.glGetTexParameteriv(target, pname, params, 0);
	}

	public boolean glIsBuffer(int buffer) {
		return gl.glIsBuffer(buffer);
	}

	public boolean glIsEnabled(int cap) {
		return gl.glIsEnabled(cap);
	}

	public boolean glIsTexture(int texture) {
		return gl.glIsTexture(texture);
	}

	public void glPointParameterf(int pname, float param) {
		gl.glPointParameterf(pname, param);
	}

	public void glPointParameterfv(int pname, float[] params) {
		gl.glPointParameterfv(pname, params, 0);
	}

	public void glTexEnvi(int target, int pname, int param) {
		gl.glTexEnvi(target, pname, param);
	}

	public void glTexEnviv(int target, int pname, int[] params) {
		gl.glTexEnviv(target, pname, params, 0);
	}

	public void glTexParameterfv(int target, int pname, float[] params) {
		gl.glTexParameterfv(target, pname, params, 0);
	}

	public void glTexParameteri(int target, int pname, int param) {

		gl.glTexParameteri(target, pname, param);

	}

	public void glTexParameteriv(int target, int pname, int[] params) {
		gl.glTexParameteriv(target, pname, params, 0);
	}

	public void glPointSizePointerOES(int type, int stride, Buffer pointer) {
		gl.glPointSizePointerOES(type, stride, pointer.getNioBuffer());
	}

	public void glPointSizePointerOES(int type, int stride, int offset) {
		gl.glPointSizePointerOES(type, stride, offset);
	}

	public void glCurrentPaletteMatrixOES(int index) {
		((GL11Ext) gl).glCurrentPaletteMatrixOES(index);
	}

	public void glLoadPaletteFromModelViewMatrixOES() {
		((GL11Ext) gl).glLoadPaletteFromModelViewMatrixOES();
	}

	public void glMatrixIndexPointerOES(int size, int type, int stride, Buffer pointer) {
		((GL11Ext) gl).glMatrixIndexPointerOES(size, type, stride, pointer.getNioBuffer());
	}

	public void glMatrixIndexPointerOES(int size, int type, int stride, int offset) {
		((GL11Ext) gl).glMatrixIndexPointerOES(size, type, stride, offset);
	}

	public void glWeightPointerOES(int size, int type, int stride, Buffer pointer) {
		((GL11Ext) gl).glWeightPointerOES(size, type, stride, pointer.getNioBuffer());
	}

	public void glWeightPointerOES(int size, int type, int stride, int offset) {
		((GL11Ext) gl).glWeightPointerOES(size, type, stride, offset);
	}

	public void glDrawTexsOES(short x, short y, short z, short width, short height) {
		((GL11Ext) gl).glDrawTexsOES(x, y, z, width, height);
	}

	public void glDrawTexiOES(int x, int y, int z, int width, int height) {
		((GL11Ext) gl).glDrawTexiOES(x, y, z, width, height);
	}

	public void glDrawTexfOES(float x, float y, float z, float width, float height) {
		((GL11Ext) gl).glDrawTexfOES(x, y, z, width, height);
	}
}