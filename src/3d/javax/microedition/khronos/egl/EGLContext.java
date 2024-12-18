package javax.microedition.khronos.egl;

import emulator.graphics3D.egl.EGL10Impl;

import javax.microedition.khronos.opengles.GL;

public abstract class EGLContext {
	public EGLContext() {
		super();
	}

	public static synchronized EGL getEGL() {
		return EGL10Impl.getEgl();
	}

	public abstract GL getGL();
}
