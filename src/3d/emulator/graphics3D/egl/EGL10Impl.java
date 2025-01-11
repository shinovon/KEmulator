package emulator.graphics3D.egl;

import org.lwjgl.opengl.GL11;

import javax.microedition.khronos.egl.*;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Image2D;

/**
 * EGL10
 */
public class EGL10Impl implements EGL10 {
	static EGL10Impl egl;
	static emulator.graphics3D.lwjgl.Emulator3D ab1332;

	public EGL10Impl() {
		super();
	}

	public static EGL10Impl getEgl() {
		if (EGL10Impl.egl == null) {
			EGL10Impl.egl = new EGL11Impl();
			EGL10Impl.ab1332 = emulator.graphics3D.lwjgl.Emulator3D.getInstance();
		}
		return EGL10Impl.egl;
	}

	public boolean eglChooseConfig(final EGLDisplay eglDisplay, final int[] attrib_list, final EGLConfig[] configs, final int config_size, final int[] num_config) {
		if (eglDisplay == null) {
			throw new IllegalArgumentException("display == null");
		}
		if (configs != null && configs.length < config_size) {
			throw new IllegalArgumentException("configs != null && configs.length < config_size");
		}
		if (!method778(attrib_list)) {
			throw new IllegalArgumentException("attrib_list not terminated with EGL.EGL_NONE");
		}
		if (num_config != null && num_config.length < 1) {
			throw new IllegalArgumentException("num_config != null && num_config.length < 1");
		}
		if (configs != null) {
			configs[0] = EGLConfigImpl.method779(1);
		}
		if (num_config != null)
			num_config[0] = EGLConfigImpl.method780(configs);
		return true;
	}

	public boolean eglCopyBuffers(final EGLDisplay eglDisplay, final EGLSurface eglSurface, final Object o) {
		return false;
	}

	public EGLContext eglCreateContext(final EGLDisplay eglDisplay, final EGLConfig eglConfig, final EGLContext share_context, final int[] attrib_list) {
		if (eglDisplay == null) {
			throw new IllegalArgumentException("display == null");
		}
		if (eglConfig == null) {
			throw new IllegalArgumentException("config == null");
		}
		if (share_context == null) {
			throw new IllegalArgumentException("share_context == null");
		}
		if (!method778(attrib_list)) {
			throw new IllegalArgumentException("attrib_list not terminated with EGL.EGL_NONE");
		}
		return EGLContextImpl.method765(1);
	}

	public EGLSurface eglCreatePbufferSurface(final EGLDisplay eglDisplay, final EGLConfig eglConfig, final int[] array) {
		if (eglDisplay == null) {
			throw new IllegalArgumentException("display == null");
		}
		if (eglConfig == null) {
			throw new IllegalArgumentException("config == null");
		}
		if (!method778(array)) {
			throw new IllegalArgumentException("attrib_list not terminated with EGL.EGL_NONE");
		}
		int n = 0;
		int n2 = 0;
		if (array != null) {
			for (int i = 0; i < array.length; i += 2) {
				if (array[i] == 12375) {
					n = array[i + 1];
				} else if (array[i] == 12374) {
					n2 = array[i + 1];
				}
			}
		}
		final EGLSurfaceImpl method781 = EGLSurfaceImpl.method781(3, n, n2);
		method781.method783(Image.createImage(n, n2).getGraphics());
		return method781;
	}

	public EGLSurface eglCreatePixmapSurface(final EGLDisplay eglDisplay, final EGLConfig eglConfig, final Object o, final int[] array) {
		if (eglDisplay == null) {
			throw new IllegalArgumentException("display == null");
		}
		if (eglConfig == null) {
			throw new IllegalArgumentException("config == null");
		}
		if (!method778(array)) {
			throw new IllegalArgumentException("attrib_list not terminated with EGL.EGL_NONE");
		}
		if (o == null) {
			throw new IllegalArgumentException("pixmap == null");
		}
		if (!(o instanceof Graphics)) {
			throw new IllegalArgumentException("pixmap is not a Graphics instance");
		}
		final Graphics graphics;
		final EGLSurfaceImpl method781;
		(method781 = EGLSurfaceImpl.method781(2, (graphics = (Graphics) o).getImage().getWidth(), graphics.getImage().getHeight())).method783(graphics);
		return method781;
	}

	public EGLSurface eglCreateWindowSurface(final EGLDisplay eglDisplay, final EGLConfig eglConfig, final Object o, final int[] array) {
		if (eglDisplay == null) {
			throw new IllegalArgumentException("display == null");
		}
		if (eglConfig == null) {
			throw new IllegalArgumentException("config == null");
		}
		if (!method778(array)) {
			throw new IllegalArgumentException("attrib_list not terminated with EGL.EGL_NONE");
		}
		if (!(o instanceof Graphics)) {
			throw new IllegalArgumentException("win is not a Canvas, GameCanvas, EGLCanvas, or Graphics");
		}
		final Graphics graphics;
		final EGLSurfaceImpl method781;
		(method781 = EGLSurfaceImpl.method781(1, (graphics = (Graphics) o).getImage().getWidth(), graphics.getImage().getHeight())).method783(graphics);
		return method781;
	}

	public boolean eglDestroyContext(final EGLDisplay eglDisplay, final EGLContext eglContext) {
		return false;
	}

	public boolean eglDestroySurface(final EGLDisplay eglDisplay, final EGLSurface eglSurface) {
		return false;
	}

	public boolean eglGetConfigAttrib(final EGLDisplay eglDisplay, final EGLConfig eglConfig, final int n, final int[] array) {
		return false;
	}

	public boolean eglGetConfigs(final EGLDisplay eglDisplay, final EGLConfig[] array, final int n, final int[] array2) {
		if (eglDisplay == null) {
			throw new IllegalArgumentException("display == null");
		}
		if (array != null && array.length < n) {
			throw new IllegalArgumentException("configs != null && configs.length < config_size");
		}
		if (array2 != null && array2.length < 1) {
			throw new IllegalArgumentException("num_config != null && num_config.length < 1");
		}
		array2[0] = EGLConfigImpl.method780(array);
		return true;
	}

	public EGLContext eglGetCurrentContext() {
		final EGLContext value;
		if ((value = (EGLContext) GL10Impl.threadToContext.get(Thread.currentThread())) == null) {
			return EGL10.EGL_NO_CONTEXT;
		}
		return value;
	}

	public EGLDisplay eglGetCurrentDisplay() {
		final EGLContextImpl c;
		if ((c = (EGLContextImpl) this.eglGetCurrentContext()) != EGL10.EGL_NO_CONTEXT) {
			return c.getDisplay();
		}
		return EGL10.EGL_NO_DISPLAY;
	}

	public EGLSurface eglGetCurrentSurface(final int n) {
		if (n != 12378 && n != 12377) {
			throw new IllegalArgumentException("readdraw not one of EGL.EGL_READ or EGL.EGL_DRAW");
		}
		final EGLContextImpl c;
		if ((c = (EGLContextImpl) this.eglGetCurrentContext()) == EGL10.EGL_NO_CONTEXT) {
			return EGL10.EGL_NO_SURFACE;
		}
		if (n == 12378) {
			return c.method766();
		}
		return c.method762();
	}

	public EGLDisplay eglGetDisplay(final Object display) {
		if (display == EGL10.EGL_DEFAULT_DISPLAY) {
			return EGLDisplayImpl.method803(1);
		}
		throw new IllegalArgumentException("display != EGL.EGL_DEFAULT_DISPLAY");
	}

	public int eglGetError() {
		return 0;
	}

	public boolean eglInitialize(final EGLDisplay display, final int[] maj_min) {
		if (display == null) {
			throw new IllegalArgumentException("display == null");
		}
		if (maj_min != null && maj_min.length < 2) {
			throw new IllegalArgumentException("major_minor != null && major_minor.length < 2");
		}
		EGLConfigImpl.method779(1);
		EGLConfigImpl.method779(2);
		EGLConfigImpl.method779(3);
		if (maj_min != null) {
			maj_min[maj_min[0] = 1] = 1;
		}
		return true;
	}

	public boolean eglMakeCurrent(final EGLDisplay display, final EGLSurface draw, final EGLSurface read, final EGLContext context) {
		if (display == null) {
			throw new IllegalArgumentException("display == null");
		}
		if (draw == null) {
			throw new IllegalArgumentException("draw == null (perhaps you meant EGL.EGL_NO_SURFACE)");
		}
		if (read == null) {
			throw new IllegalArgumentException("read == null (perhaps you meant EGL.EGL_NO_SURFACE)");
		}
		if (context == null) {
			throw new IllegalArgumentException("context == null (perhaps you meant EGL.EGL_NO_CONTEXT)");
		}
		final Thread currentThread = Thread.currentThread();
		if (context == EGL10.EGL_NO_CONTEXT) {
			final Object value;
			if ((value = GL10Impl.threadToContext.get(currentThread)) != null) {
				final EGLContextImpl c;
				if ((c = (EGLContextImpl) value).method764()) {
					this.eglDestroyContext(c.getDisplay(), c);
				}
				c.setDisplay(null);
				c.method763(null);
				c.method767(null);
				GL10Impl.contextToThread.remove(c);
				GL10Impl.displayThread.remove(c);
				GL10Impl.drawSurfaceThread.remove(c);
				GL10Impl.readSurfaceThread.remove(c);
				GL10Impl.threadToContext.remove(currentThread);
				EGL10Impl.ab1332.releaseTarget();
			}
		} else {
			final EGLContextImpl c2;
			(c2 = (EGLContextImpl) context).setDisplay((EGLDisplayImpl) display);
			c2.method763((EGLSurfaceImpl) draw);
			c2.method767((EGLSurfaceImpl) read);
			GL10Impl.contextToThread.put(c2, currentThread);
			GL10Impl.displayThread.put(c2, display);
			GL10Impl.drawSurfaceThread.put(c2, draw);
			GL10Impl.readSurfaceThread.put(c2, read);
			GL10Impl.threadToContext.put(currentThread, context);
			EGL10Impl.ab1332.bindTarget(c2.method762().method784(), true);
			GL11.glEnable(2884);
		}
		return true;
	}

	public boolean eglQueryContext(final EGLDisplay eglDisplay, final EGLContext eglContext, final int n, final int[] array) {
		return true;
	}

	public String eglQueryString(final EGLDisplay eglDisplay, final int n) {
		return null;
	}

	public boolean eglQuerySurface(final EGLDisplay eglDisplay, final EGLSurface eglSurface, final int n, final int[] array) {
		return true;
	}

	public boolean eglSwapBuffers(final EGLDisplay eglDisplay, final EGLSurface eglSurface) {
		try {
			EGL10Impl.ab1332.swapBuffers();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean eglTerminate(final EGLDisplay eglDisplay) {
		return true;
	}

	public boolean eglWaitGL() {
		try {
			EGL10Impl.ab1332.swapBuffers();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean eglWaitNative(final int n, final Object o) {
		try {
			final Graphics graphics = (Graphics) o;
			EGL10Impl.ab1332.setViewport(graphics.getClipX(), graphics.getClipY(), graphics.getClipWidth(), graphics.getClipHeight());
			final Background background;
			(background = new Background()).setColor(GL10Impl.anInt1359);
			background.setImage(new Image2D(99, new Image(graphics.getImage())));
			background.setCrop(graphics.getClipX(), graphics.getClipY(), graphics.getClipWidth(), graphics.getClipHeight());
			EGL10Impl.ab1332.clearBackgound(background);
			if (GL10Impl.aBoolean1355) {
				GL11.glEnable(2896);
			} else {
				GL11.glDisable(2896);
			}
			if (GL10Impl.aBoolean1358) {
				GL11.glEnable(2912);
			} else {
				GL11.glDisable(2912);
			}
			GL11.glMatrixMode(GL10Impl.anInt1354);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	static boolean method778(final int[] array) {
		if (array == null) {
			return true;
		}
		for (int i = 0; i < array.length; i += 2) {
			if (array[i] == 12344) {
				return true;
			}
		}
		return false;
	}
}
