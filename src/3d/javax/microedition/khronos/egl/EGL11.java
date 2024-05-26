package javax.microedition.khronos.egl;

public interface EGL11 extends EGL10 {
	public static final int EGL_CONTEXT_LOST = 12302;
	public static final int EGL_BIND_TO_TEXTURE_RGB = 12345;
	public static final int EGL_BIND_TO_TEXTURE_RGBA = 12346;
	public static final int EGL_MIN_SWAP_INTERVAL = 12347;
	public static final int EGL_MAX_SWAP_INTERVAL = 12348;
	public static final int EGL_NO_TEXTURE = 12380;
	public static final int EGL_TEXTURE_RGB = 12381;
	public static final int EGL_TEXTURE_RGBA = 12382;
	public static final int EGL_TEXTURE_2D = 12383;
	public static final int EGL_TEXTURE_FORMAT = 12416;
	public static final int EGL_TEXTURE_TARGET = 12417;
	public static final int EGL_MIPMAP_TEXTURE = 12418;
	public static final int EGL_MIPMAP_LEVEL = 12419;
	public static final int EGL_BACK_BUFFER = 12420;

	boolean eglSurfaceAttrib(final EGLDisplay p0, final EGLSurface p1, final int p2, final int p3);

	boolean eglBindTexImage(final EGLDisplay p0, final EGLSurface p1, final int p2);

	boolean eglReleaseTexImage(final EGLDisplay p0, final EGLSurface p1, final int p2);

	boolean eglSwapInterval(final EGLDisplay p0, final int p1);
}
