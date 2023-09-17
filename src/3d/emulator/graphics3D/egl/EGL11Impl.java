package emulator.graphics3D.egl;

import javax.microedition.khronos.egl.*;

/**
 * EGL11
 */
public final class EGL11Impl extends EGL10Impl implements EGL11
{
    public final synchronized boolean eglSurfaceAttrib(final EGLDisplay eglDisplay, final EGLSurface eglSurface, final int n, final int n2) {
        if (eglDisplay == null) {
            throw new IllegalArgumentException("display == null");
        }
        if (eglSurface == null) {
            throw new IllegalArgumentException("surface == null (perhaps you meant EGL.EGL_NO_SURFACE)");
        }
        return true;
    }
    
    public final synchronized boolean eglBindTexImage(final EGLDisplay eglDisplay, final EGLSurface eglSurface, final int n) {
        if (eglDisplay == null) {
            throw new IllegalArgumentException("display == null");
        }
        if (eglSurface == null) {
            throw new IllegalArgumentException("surface == null (perhaps you meant EGL.EGL_NO_SURFACE)");
        }
        return true;
    }
    
    public final synchronized boolean eglReleaseTexImage(final EGLDisplay eglDisplay, final EGLSurface eglSurface, final int n) {
        if (eglDisplay == null) {
            throw new IllegalArgumentException("display == null");
        }
        if (eglSurface == null) {
            throw new IllegalArgumentException("surface == null (perhaps you meant EGL.EGL_NO_SURFACE)");
        }
        return true;
    }
    
    public final synchronized boolean eglSwapInterval(final EGLDisplay eglDisplay, final int n) {
        if (eglDisplay == null) {
            throw new IllegalArgumentException("display == null");
        }
        return true;
    }
    
    public EGL11Impl() {
        super();
    }
}
