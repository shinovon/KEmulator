package javax.microedition.khronos.opengles;

import java.nio.*;

public interface GL11ExtensionPack extends GL {
	public static final int GL_MIRRORED_REPEAT = 33648;
	public static final int GL_NORMAL_MAP = 34065;
	public static final int GL_REFLECTION_MAP = 34066;
	public static final int GL_TEXTURE_CUBE_MAP = 34067;
	public static final int GL_TEXTURE_BINDING_CUBE_MAP = 34068;
	public static final int GL_STR = -1;
	public static final int GL_TEXTURE_GEN_MODE = 9472;
	public static final int GL_TEXTURE_GEN_STR = 36192;
	public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_X = 8317;
	public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 8318;
	public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 8319;
	public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 8320;
	public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 8321;
	public static final int GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 8322;
	public static final int GL_MAX_CUBE_MAP_TEXTURE_SIZE = 34076;
	public static final int GL_FUNC_ADD = 32774;
	public static final int GL_FUNC_SUBTRACT = 32778;
	public static final int GL_FUNC_REVERSE_SUBTRACT = 32779;
	public static final int GL_BLEND_DST_RGB = 32968;
	public static final int GL_BLEND_SRC_RGB = 32969;
	public static final int GL_BLEND_DST_ALPHA = 32970;
	public static final int GL_BLEND_SRC_ALPHA = 32971;
	public static final int GL_BLEND_EQUATION = 32777;
	public static final int GL_BLEND_EQUATION_RGB = 32777;
	public static final int GL_BLEND_EQUATION_ALPHA = 34877;
	public static final int GL_INCR_WRAP = 34055;
	public static final int GL_DECR_WRAP = 34056;
	public static final int GL_FRAMEBUFFER_OES = 36160;
	public static final int GL_RENDERBUFFER_OES = 36161;
	public static final int GL_RENDERBUFFER_WIDTH_OES = 36162;
	public static final int GL_RENDERBUFFER_HEIGHT_OES = 36163;
	public static final int GL_RENDERBUFFER_INTERNAL_FORMAT_OES = 36164;
	public static final int GL_RENDERBUFFER_RED_SIZE_OES = 36176;
	public static final int GL_RENDERBUFFER_GREEN_SIZE_OES = 36177;
	public static final int GL_RENDERBUFFER_BLUE_SIZE_OES = 36178;
	public static final int GL_RENDERBUFFER_ALPHA_SIZE_OES = 36179;
	public static final int GL_RENDERBUFFER_DEPTH_SIZE_OES = 36180;
	public static final int GL_RENDERBUFFER_STENCIL_SIZE_OES = 36181;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE_OES = 36048;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME_OES = 36049;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL_OES = 36050;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE_OES = 36051;
	public static final int GL_COLOR_ATTACHMENT0_OES = 36064;
	public static final int GL_COLOR_ATTACHMENT1_OES = 36065;
	public static final int GL_COLOR_ATTACHMENT2_OES = 36066;
	public static final int GL_COLOR_ATTACHMENT3_OES = 36067;
	public static final int GL_COLOR_ATTACHMENT4_OES = 36068;
	public static final int GL_COLOR_ATTACHMENT5_OES = 36069;
	public static final int GL_COLOR_ATTACHMENT6_OES = 36070;
	public static final int GL_COLOR_ATTACHMENT7_OES = 36071;
	public static final int GL_COLOR_ATTACHMENT8_OES = 36072;
	public static final int GL_COLOR_ATTACHMENT9_OES = 36073;
	public static final int GL_COLOR_ATTACHMENT10_OES = 36074;
	public static final int GL_COLOR_ATTACHMENT11_OES = 36075;
	public static final int GL_COLOR_ATTACHMENT12_OES = 36076;
	public static final int GL_COLOR_ATTACHMENT13_OES = 36077;
	public static final int GL_COLOR_ATTACHMENT14_OES = 36078;
	public static final int GL_COLOR_ATTACHMENT15_OES = 36079;
	public static final int GL_DEPTH_ATTACHMENT_OES = 36096;
	public static final int GL_STENCIL_ATTACHMENT_OES = 36128;
	public static final int GL_FRAMEBUFFER_COMPLETE_OES = 36053;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_OES = 36054;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_OES = 36055;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_OES = 36057;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_FORMATS_OES = 36058;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_OES = 36059;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_OES = 36060;
	public static final int GL_FRAMEBUFFER_UNSUPPORTED_OES = 36061;
	public static final int GL_FRAMEBUFFER_BINDING_OES = 36006;
	public static final int GL_RENDERBUFFER_BINDING_OES = 36007;
	public static final int GL_MAX_COLOR_ATTACHMENTS_OES = 36063;
	public static final int GL_MAX_RENDERBUFFER_SIZE_OES = 34024;
	public static final int GL_INVALID_FRAMEBUFFER_OPERATION_OES = 1286;
	public static final int GL_RGB565_OES = 36194;
	public static final int GL_RGBA4 = 32854;
	public static final int GL_RGB5_A1 = 32855;
	public static final int GL_DEPTH_COMPONENT16 = 33189;
	public static final int GL_RGBA8 = 32856;
	public static final int GL_RGB8 = 32849;
	public static final int GL_DEPTH_COMPONENT24 = 33190;
	public static final int GL_DEPTH_COMPONENT32 = 33191;
	public static final int GL_STENCIL_INDEX1_OES = 36166;
	public static final int GL_STENCIL_INDEX4_OES = 36167;
	public static final int GL_STENCIL_INDEX8_OES = 36168;
	public static final int GL_STENCIL_INDEX = 6401;
	public static final int GL_DEPTH_COMPONENT = 6402;

	void glTexEnvf(final int p0, final int p1, final float p2);

	void glTexEnvfv(final int p0, final int p1, final float[] p2, final int p3);

	void glTexEnvfv(final int p0, final int p1, final FloatBuffer p2);

	void glTexEnvx(final int p0, final int p1, final int p2);

	void glTexEnvxv(final int p0, final int p1, final int[] p2, final int p3);

	void glTexEnvxv(final int p0, final int p1, final IntBuffer p2);

	void glTexParameterf(final int p0, final int p1, final float p2);

	void glBindTexture(final int p0, final int p1);

	void glCompressedTexImage2D(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final Buffer p7);

	void glCopyTexImage2D(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7);

	void glEnable(final int p0);

	void glGetIntegerv(final int p0, final int[] p1, final int p2);

	void glGetIntegerv(final int p0, final IntBuffer p1);

	void glTexGeni(final int p0, final int p1, final int p2);

	void glTexGenf(final int p0, final int p1, final float p2);

	void glTexGenx(final int p0, final int p1, final int p2);

	void glTexGenfv(final int p0, final int p1, final float[] p2, final int p3);

	void glTexGenfv(final int p0, final int p1, final FloatBuffer p2);

	void glTexGeniv(final int p0, final int p1, final int[] p2, final int p3);

	void glTexGeniv(final int p0, final int p1, final IntBuffer p2);

	void glTexGenxv(final int p0, final int p1, final int[] p2, final int p3);

	void glTexGenxv(final int p0, final int p1, final IntBuffer p2);

	void glGetTexGeniv(final int p0, final int p1, final int[] p2, final int p3);

	void glGetTexGeniv(final int p0, final int p1, final IntBuffer p2);

	void glGetTexGenfv(final int p0, final int p1, final float[] p2, final int p3);

	void glGetTexGenxv(final int p0, final int p1, final int[] p2, final int p3);

	void glGetTexGenxv(final int p0, final int p1, final IntBuffer p2);

	void glGetTexGenfv(final int p0, final int p1, final FloatBuffer p2);

	void glBlendEquation(final int p0);

	void glBlendFuncSeparate(final int p0, final int p1, final int p2, final int p3);

	void glBlendEquationSeparate(final int p0, final int p1);

	void glStencilOp(final int p0, final int p1, final int p2);

	boolean glIsRenderbufferOES(final int p0);

	void glBindRenderbufferOES(final int p0, final int p1);

	void glDeleteRenderbuffersOES(final int p0, final int[] p1, final int p2);

	void glDeleteRenderbuffersOES(final int p0, final IntBuffer p1);

	void glGenRenderbuffersOES(final int p0, final int[] p1, final int p2);

	void glGenRenderbuffersOES(final int p0, final IntBuffer p1);

	void glRenderbufferStorageOES(final int p0, final int p1, final int p2, final int p3);

	void glGetRenderbufferParameterivOES(final int p0, final int p1, final int[] p2, final int p3);

	void glGetRenderbufferParameterivOES(final int p0, final int p1, final IntBuffer p2);

	boolean glIsFramebufferOES(final int p0);

	void glBindFramebufferOES(final int p0, final int p1);

	void glDeleteFramebuffersOES(final int p0, final int[] p1, final int p2);

	void glDeleteFramebuffersOES(final int p0, final IntBuffer p1);

	void glGenFramebuffersOES(final int p0, final int[] p1, final int p2);

	void glGenFramebuffersOES(final int p0, final IntBuffer p1);

	int glCheckFramebufferStatusOES(final int p0);

	void glFramebufferTexture2DOES(final int p0, final int p1, final int p2, final int p3, final int p4);

	void glFramebufferRenderbufferOES(final int p0, final int p1, final int p2, final int p3);

	void glGetFramebufferAttachmentParameterivOES(final int p0, final int p1, final int p2, final int[] p3, final int p4);

	void glGetFramebufferAttachmentParameterivOES(final int p0, final int p1, final int p2, final IntBuffer p3);

	void glGenerateMipmapOES(final int p0);
}
