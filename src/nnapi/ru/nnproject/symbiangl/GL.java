/*
Copyright (c) 2025 Arman Jussupgaliyev
*/
package ru.nnproject.symbiangl;

import emulator.Emulator;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

public class GL {

	private static boolean loaded;
	private static GL instance;
	
	private static Canvas targetCanvas;
	private static Graphics targetGraphics;
	static int contextHandle;
	private static Thread contextCreator;
	private static boolean contextActive;

	private static EGL10 egl;
	private static EGLDisplay eglDisplay;
	private static EGLContext eglContext;
	private static EGLSurface eglSurface;
	private static EGLConfig eglConfig;
	private static GL11 gl;
	
	private GL() {
	}
	
	// context
	
	/**
	 * @param canvas Target
	 * @see {@link GL#create(Graphics)}
	 */
	public static void create(Canvas canvas) {
		if (canvas == null) {
			throw new IllegalArgumentException("canvas");
		}

		create(canvas, new Graphics(Emulator.getEmulator().getScreen().getBackBufferImage(), Emulator.getEmulator().getScreen().getXRayScreenImage()));
	}
	
	/**
	 * @param graphics Target
	 * @throws IllegalArgumentException if graphics is null
	 * @throws GLException if context already created
	 * @throws GLException if context creation failed
	 * @throws UnsupportedOperationException
	 */
	public static void create(Graphics graphics) {
		create(null, graphics);
	}
	
	static void create(Canvas canvas, Graphics graphics) {
		if (contextHandle != 0) {
			throw new GLException("create", "Context already exists");
		}
		if (graphics == null) {
			throw new IllegalArgumentException("graphics");
		}
		
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
		
		eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, graphics, null);
		gl = (GL11) eglContext.getGL();
		
		GL.targetCanvas = canvas;
		GL.targetGraphics = graphics;
		contextCreator = Thread.currentThread();
		contextHandle = 1;
	}
	
	/**
	 * @throws GLException if context already created
	 * @throws GLException if context creation failed
	 * @throws UnsupportedOperationException
	 */
	public static void createPbuffer(int[] eglConfigAttribs, int[] eglSurfaceAttribs) {
		if (contextHandle != 0) {
			throw new GLException("createPbufferContext", "Context already exists");
		}
		if (eglConfigAttribs != null
				&& (eglConfigAttribs.length % 2 != 1
				|| eglConfigAttribs[eglConfigAttribs.length - 1] != EGLConstants.EGL_NONE)) {
			throw new IllegalArgumentException("eglConfigAttribs");
		}
		if (eglSurfaceAttribs != null
				&& (eglSurfaceAttribs.length % 2 != 1
				|| eglSurfaceAttribs[eglSurfaceAttribs.length - 1] != EGLConstants.EGL_NONE)) {
			throw new IllegalArgumentException("eglSurfaceAttribs");
		}

		eglContext = egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, null);
		
		eglSurface = egl.eglCreatePbufferSurface(eglDisplay, eglConfig, eglSurfaceAttribs);
		gl = (GL11) eglContext.getGL();
		
		contextCreator = Thread.currentThread();
		contextHandle = 1;
		
		targetCanvas = null;
		targetGraphics = null;
	}
	
	/**
	 * @throws GLException if context is already active or is not created yet
	 */
	public static void makeCurrent() {
		if (contextHandle == 0) {
			throw new GLException("makeCurrent", "Context not created");
		}
		if (contextActive) {
			throw new GLException("makeCurrent", "Context already active");
		}
		if (Thread.currentThread() != contextCreator) {
			throw new GLException("makeCurrent", "Called from different thread");
		}

		egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
		
		contextActive = true;
	}
	
	/**
	 * @throws GLException if context is not active or is not created yet
	 */
	public static void release() {
		checkContext("release");

		egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
		
		contextActive = false;
	}

	
	/**
	 * @throws GLException if context is not created yet 
	 */
	public static void destroy() {
		if (contextHandle == 0) {
			throw new GLException("destroyContext", "No context");
		}
		if (Thread.currentThread() != contextCreator) {
			throw new GLException("destroy", "Called from different thread");
		}
		
		if (contextActive) {
			release();
		}
		
		egl.eglDestroyContext(eglDisplay, eglContext);
		egl.eglDestroySurface(eglDisplay, eglSurface);

		contextCreator = null;
		targetGraphics = null;
		contextHandle = 0;
	}
	
	public static boolean swapBuffers() {
		checkContext("swapBuffers");
		if (egl.eglSwapBuffers(eglDisplay, eglSurface)) {
			if (targetCanvas instanceof GameCanvas) {
				((GameCanvas) targetCanvas).flushGraphics();
			}
			return true;
		}

		return false;
	}
	
	public static boolean setVsync(boolean vsync) {
		checkContext("setVsync");
		
		if (targetGraphics != null && targetCanvas == null) {
			throw new GLException("create", "Invalid operation");
		}

		return ((EGL11) egl).eglSwapInterval(eglDisplay, vsync ? EGLConstants.EGL_TRUE : EGLConstants.EGL_FALSE);
	}
	
	// gles
	
	public static void glAlphaFunc(int func, float ref) {
		checkContext("glAlphaFunc");
		
		gl.glAlphaFunc(func, ref);
	}

	public static void glClearColor(float r, float g, float b, float a) {
		checkContext("glClearColor");
		
		gl.glClearColor(r, g, b, a);
	}

	public static void glClearDepthf(float depth) {
		checkContext("glClearDepthf");
		
		gl.glClearDepthf(depth);
	}

	public static void glClipPlanef(int plane, FloatBuffer equation) {
		checkContext("glClipPlanef");
		if (equation == null) {
			throw new IllegalArgumentException("equation = null");
		}
		
		gl.glClipPlanef(plane, equation);
	}

	public static void glColor4f(float red, float green, float blue, float alpha) {
		checkContext("glColor4f");
		
		gl.glColor4f(red, green, blue, alpha);
	}

	public static void glDepthRangef(float zNear, float zFar) {
		checkContext("glDepthRangef");
		
		gl.glDepthRangef(zNear, zFar);
	}

	public static void glFogf(int pname, float param) {
		checkContext("glFogf");
		
		gl.glFogf(pname, param);
	}

	public static void glFogfv(int pname, FloatBuffer params) {
		checkContext("glFogfv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glFogfv(pname, params);
	}

	public static void glFrustumf(float left, float right, float bottom, float top, float zNear, float zFar) {
		checkContext("glFrustumf");
		
		gl.glFrustumf(left, right, bottom, top, zNear, zFar);
	}

	public static void glGetClipPlanef(int pname, FloatBuffer eqn) {
		checkContext("glGetClipPlanef");
		if (eqn == null) {
			throw new IllegalArgumentException("eqn = null");
		}
		if (eqn.remaining() < 4) {
			throw new IllegalArgumentException("eqn.remaining() < 4");
		}
		gl.glGetClipPlanef(pname, eqn);
	}

	public static void glGetFloatv(int pname, FloatBuffer params) {
		checkContext("glGetFloatv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetFloatv(pname, params);
	}

	public static void glGetLightfv(int light, int pname, FloatBuffer params) {
		checkContext("glGetLightfv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetLightfv(light, pname, params);
	}

	public static void glGetMaterialfv(int face, int pname, FloatBuffer params) {
		checkContext("glGetMaterialfv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetMaterialfv(face, pname, params);
	}

	public static void glGetTexEnvfv(int env, int pname, FloatBuffer params) {
		checkContext("glGetTexEnvfv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetTexEnvfv(env, pname, params);
	}

	public static void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
		checkContext("glGetTexParameterfv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetTexParameterfv(target, pname, params);
	}

	public static void glLightModelf(int pname, float param) {
		checkContext("glLightModelf");
		
		gl.glLightModelf(pname, param);
	}

	public static void glLightModelfv(int pname, FloatBuffer params) {
		checkContext("glLightModelfv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glLightModelfv(pname, params);
	}

	public static void glLightf(int light, int pname, float param) {
		checkContext("glLightf");
		
		gl.glLightf(light, pname, param);
	}

	public static void glLightfv(int light, int pname, FloatBuffer params) {
		checkContext("glLightfv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glLightfv(light, pname, params);
	}

	public static void glLineWidth(float width) {
		checkContext("glLineWidth");
		
		gl.glLineWidth(width);
	}

	public static void glLoadMatrixf(FloatBuffer m) {
		checkContext("glLoadMatrixf");
		if (m == null) {
			throw new IllegalArgumentException("m = null");
		}
		
		gl.glLoadMatrixf(m);
	}

	public static void glMaterialf(int face, int pname, float param) {
		checkContext("glMaterialf");
		
		gl.glMaterialf(face, pname, param);
	}

	public static void glMaterialfv(int face, int pname, FloatBuffer params) {
		checkContext("glMaterialfv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glMaterialfv(face, pname, params);
	}

	public static void glMultMatrixf(FloatBuffer m) {
		checkContext("glMultMatrixf");
		
		gl.glMultMatrixf(m);
	}

	public static void glMultiTexCoord4f(int target, float s, float t, float r, float q) {
		checkContext("glMultiTexCoord4f");
		
		gl.glMultiTexCoord4f(target, s, t, r, q);
	}

	public static void glNormal3f(float nx, float ny, float nz) {
		checkContext("glNormal3f");
		
		gl.glNormal3f(nx, ny, nz);
	}

	public static void glOrthof(float left, float right, float bottom, float top, float zNear, float zFar) {
		checkContext("glOrthof");
		
		gl.glOrthof(left, right, bottom, top, zNear, zFar);
	}

	public static void glPointParameterf(int pname, float param) {
		checkContext("glPointParameterf");
		
		gl.glPointParameterf(pname, param);
	}

	public static void glPointParameterfv(int pname, FloatBuffer params) {
		checkContext("glPointParameterfv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glPointParameterfv(pname, params);
	}

	public static void glPointSize(float size) {
		checkContext("glPointSize");
		
		gl.glPointSize(size);
	}

	public static void glPolygonOffset(float factor, float units) {
		checkContext("glPolygonOffset");
		
		gl.glPolygonOffset(factor, units);
	}

	public static void glRotatef(float angle, float x, float y, float z) {
		checkContext("glRotatef");
		
		gl.glRotatef(angle, x, y, z);
	}

	public static void glScalef(float x, float y, float z) {
		checkContext("glScalef");
		
		gl.glScalef(x, y, z);
	}

	public static void glTexEnvf(int target, int pname, float param) {
		checkContext("glTexEnvf");
		
		gl.glTexEnvf(target, pname, param);
	}

	public static void glTexEnvfv(int target, int pname, FloatBuffer params) {
		checkContext("glTexEnvfv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glTexEnvfv(target, pname, params);
	}

	public static void glTexParameterf(int target, int pname, float param) {
		checkContext("glTexParameterf");
		
		gl.glTexParameterf(target, pname, param);
	}

	public static void glTexParameterfv(int target, int pname, FloatBuffer params) {
		checkContext("glTexParameterfv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glTexParameterfv(target, pname, params);
	}

	public static void glTranslatef(float x, float y, float z) {
		checkContext("glTranslatef");
		
		gl.glTranslatef(x, y, z);
	}

	public static void glActiveTexture(int texture) {
		checkContext("glActiveTexture");
		
		gl.glActiveTexture(texture);
	}

	public static void glAlphaFuncx(int func, int ref) {
		checkContext("glAlphaFuncx");
		
		gl.glAlphaFuncx(func, ref);
	}

	public static void glBindBuffer(int target, int buffer) {
		checkContext("glBindBuffer");
		
		gl.glBindBuffer(target, buffer);
	}

	public static void glBindTexture(int target, int texture) {
		checkContext("glBindTexture");
		
		gl.glBindTexture(target, texture);
	}

	public static void glBlendFunc(int sfactor, int dfactor) {
		checkContext("glBlendFunc");
		
		gl.glBlendFunc(sfactor, dfactor);
	}

	public static void glBufferData(int target, int size, Buffer data, int usage) {
		checkContext("glBufferData");
		if (data == null) {
			throw new IllegalArgumentException("data = null");
		}
		
		gl.glBufferData(target, size, data, usage);
	}

	public static void glBufferSubData(int target, int offset, int size, Buffer data) {
		checkContext("glBufferSubData");
		if (data == null) {
			throw new IllegalArgumentException("data = null");
		}
		
		gl.glBufferSubData(target, offset, size, data);
	}

	public static void glClear(int mask) {
		checkContext("glClear");
		
		gl.glClear(mask);
	}

	public static void glClearColorx(int red, int green, int blue, int alpha) {
		checkContext("glClearColorx");
		
		gl.glClearColorx(red, green, blue, alpha);
	}

	public static void glClearDepthx(int depth) {
		checkContext("glClearDepthx");
		
		gl.glClearDepthx(depth);
	}

	public static void glClearStencil(int s) {
		checkContext("glClearStencil");
		
		gl.glClearStencil(s);
	}

	public static void glClientActiveTexture(int texture) {
		checkContext("glClientActiveTexture");
		
		gl.glClientActiveTexture(texture);
	}

	public static void glClipPlanex(int plane, IntBuffer equation) {
		checkContext("glClipPlanex");
		if (equation == null) {
			throw new IllegalArgumentException("equation = null");
		}
		
		gl.glClipPlanex(plane, equation);
	}

	public static void glColor4ub(byte red, byte green, byte blue, byte alpha) {
		checkContext("glColor4ub");
		
		gl.glColor4ub(red, green, blue, alpha);
	}

	public static void glColor4x(int red, int green, int blue, int alpha) {
		checkContext("glColor4x");
		
		gl.glColor4x(red, green, blue, alpha);
	}

	public static void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		checkContext("glColorMask");
		
		gl.glColorMask(red, green, blue, alpha);
	}

	public static void glColorPointer(int size, int type, int stride, Buffer pointer) {
		checkContext("glColorPointer");
		if (pointer == null) {
			throw new IllegalArgumentException("pointer = null");
		}
		
		gl.glColorPointer(size, type, stride, pointer);
	}

	// VBO variant
	public static void glColorPointer(int size, int type, int stride, int pointer) {
		checkContext("glColorPointer");
		if (pointer < 0) {
			throw new IllegalArgumentException("pointer < 0");
		}
		
		gl.glColorPointer(size, type, stride, pointer);
	}

	public static void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, Buffer data) {
		checkContext("glCompressedTexImage2D");
		if (data == null) {
			throw new IllegalArgumentException("data = null");
		}
		
		gl.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
	}

	public static void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, Buffer data) {
		checkContext("glCompressedTexSubImage2D");
		if (data == null) {
			throw new IllegalArgumentException("data = null");
		}
		
		gl.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
	}

	public static void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
		checkContext("glCopyTexImage2D");
		
		gl.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
	}

	public static void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
		checkContext("glCopyTexSubImage2D");
		
		gl.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
	}

	public static void glCullFace(int mode) {
		checkContext("glCullFace");
		
		gl.glCullFace(mode);
	}

	public static void glDeleteBuffers(int n, IntBuffer buffers) {
		checkContext("glDeleteBuffers");
		if (buffers == null) {
			throw new IllegalArgumentException("buffer = null");
		}
		
		gl.glDeleteBuffers(n, buffers);
	}

	public static void glDeleteTextures(int n, IntBuffer textures) {
		checkContext("glDeleteTextures");
		if (textures == null) {
			throw new IllegalArgumentException("textures = null");
		}
		
		gl.glDeleteTextures(n, textures);
	}

	public static void glDepthFunc(int func) {
		checkContext("glDepthFunc");
		
		gl.glDepthFunc(func);
	}

	public static void glDepthMask(boolean flag) {
		checkContext("glDepthMask");
		
		gl.glDepthMask(flag);
	}

	public static void glDepthRangex(int zNear, int zFar) {
		checkContext("glDepthRangex");
		
		gl.glDepthRangex(zNear, zFar);
	}

	public static void glDisable(int cap) {
		checkContext("glDisable");
		
		gl.glDisable(cap);
	}

	public static void glDisableClientState(int array) {
		checkContext("glDisableClientState");
		
		gl.glDisableClientState(array);
	}

	public static void glDrawArrays(int mode, int first, int count) {
		checkContext("glDrawArrays");
		
		gl.glDrawArrays(mode, first, count);
	}

	public static void glDrawElements(int mode, int count, int type, Buffer indices) {
		checkContext("glDrawElements");
		if (indices == null) {
			throw new IllegalArgumentException("indices = null");
		}
		
		gl.glDrawElements(mode, count, type, indices);
	}

	public static void glDrawElements(int mode, int count, int type, int indices) {
		checkContext("glDrawElements");
		
		gl.glDrawElements(mode, count, type, indices);
	}

	public static void glEnable(int cap) {
		checkContext("glEnable");
		
		gl.glEnable(cap);
	}

	public static void glEnableClientState(int array) {
		checkContext("glEnableClientState");
		
		gl.glEnableClientState(array);
	}

	public static void glFinish() {
		checkContext("glFinish");
		
		gl.glFinish();
	}

	public static void glFlush() {
		checkContext("glFlush");
		
		gl.glFlush();
	}

	public static void glFogx(int pname, int param) {
		checkContext("glFogx");
		
		gl.glFogx(pname, param);
	}

	public static void glFogxv(int pname, IntBuffer params) {
		checkContext("glFogxv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glFogxv(pname, params);
	}

	public static void glFrontFace(int mode) {
		checkContext("glFrontFace");
		
		gl.glFrontFace(mode);
	}

	public static void glFrustumx(int left, int right, int bottom, int top, int zNear, int zFar) {
		checkContext("glFrustumx");
		
		gl.glFrustumx(left, right, bottom, top, zNear, zFar);
	}

	public static void glGetBooleanv(int pname, IntBuffer params) {
		checkContext("glGetBooleanv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetBooleanv(pname, params);
	}

	public static void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
		checkContext("glGetBufferParameteriv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetBufferParameteriv(target, pname, params);
	}

	public static void glGetClipPlanex(int pname, IntBuffer eqn) {
		checkContext("glGetClipPlanex");
		if (eqn == null) {
			throw new IllegalArgumentException("eqn = null");
		}
		if (eqn.remaining() < 4) {
			throw new IllegalArgumentException("eqn.remaining() < 4");
		}
		gl.glGetClipPlanex(pname, eqn);
	}

	public static void glGenBuffers(int n, IntBuffer buffers) {
		checkContext("glGenBuffers");
		if (buffers == null) {
			throw new IllegalArgumentException("buffers = null");
		}
		
		gl.glGenBuffers(n, buffers);
	}

	public static void glGenTextures(int n, IntBuffer textures) {
		checkContext("glGenTextures");
		if (textures == null) {
			throw new IllegalArgumentException("textures = null");
		}
		
		gl.glGenTextures(n, textures);
	}

	public static int glGetError() {
		checkContext("glGetError");
		
		return gl.glGetError();
	}

	public static void glGetFixedv(int pname, IntBuffer params) {
		checkContext("glGetFixedv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetFixedv(pname, params);
	}

	public static void glGetIntegerv(int pname, IntBuffer params) {
		checkContext("glGetIntegerv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetIntegerv(pname, params);
	}

	public static void glGetLightxv(int light, int pname, IntBuffer params) {
		checkContext("glGetLightxv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetLightxv(light, pname, params);
	}

	public static void glGetMaterialxv(int face, int pname, IntBuffer params) {
		checkContext("glGetMaterialxv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetMaterialxv(face, pname, params);
	}

	public static void glGetPointerv(int pname, Buffer[] params) {
		checkContext("glGetPointerv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetPointerv(pname, params);
	}

	public static String glGetString(int name) {
		checkContext("glGetString");
		
		return gl.glGetString(name);
	}

	public static void glGetTexEnviv(int env, int pname, IntBuffer params) {
		checkContext("glGetTexEnviv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetTexEnviv(env, pname, params);
	}

	public static void glGetTexEnvxv(int env, int pname, IntBuffer params) {
		checkContext("glGetTexEnvxv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetTexEnvxv(env, pname, params);
	}

	public static void glGetTexParameteriv(int target, int pname, IntBuffer params) {
		checkContext("glGetTexParameteriv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetTexParameteriv(target, pname, params);
	}

	public static void glGetTexParameterxv(int target, int pname, IntBuffer params) {
		checkContext("glGetTexParameterxv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glGetTexParameterxv(target, pname, params);
	}

	public static void glHint(int target, int mode) {
		checkContext("glHint");
		
		gl.glHint(target, mode);
	}

	public static boolean glIsBuffer(int buffer) {
		checkContext("glIsBuffer");
		
		return gl.glIsBuffer(buffer);
	}

	public static boolean glIsEnabled(int cap) {
		checkContext("glIsEnabled");
		
		return gl.glIsEnabled(cap);
	}

	public static boolean glIsTexture(int texture) {
		checkContext("glIsTexture");
		
		return gl.glIsTexture(texture);
	}

	public static void glLightModelx(int pname, int param) {
		checkContext("glLightModelx");
		
		gl.glLightModelx(pname, param);
	}

	public static void glLightModelxv(int pname, IntBuffer params) {
		checkContext("glLightModelxv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glLightModelxv(pname, params);
	}

	public static void glLightx(int light, int pname, int param) {
		checkContext("glLightx");
		
		gl.glLightx(light, pname, param);
	}

	public static void glLightxv(int light, int pname, IntBuffer params) {
		checkContext("glLightxv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glLightxv(light, pname, params);
	}

	public static void glLineWidthx(int width) {
		checkContext("glLineWidthx");
		
		gl.glLineWidthx(width);
	}

	public static void glLoadIdentity() {
		checkContext("glLoadIdentity");
		
		gl.glLoadIdentity();
	}

	public static void glLoadMatrixx(IntBuffer m) {
		checkContext("glLoadMatrixx");
		if (m == null) {
			throw new IllegalArgumentException("m = null");
		}
		
		gl.glLoadMatrixx(m);
	}

	public static void glLogicOp(int opcode) {
		checkContext("glLogicOp");
		
		gl.glLogicOp(opcode);
	}

	public static void glMaterialx(int face, int pname, int param) {
		checkContext("glMaterialx");
		
		gl.glMaterialx(face, pname, param);
	}

	public static void glMaterialxv(int face, int pname, IntBuffer params) {
		checkContext("glMaterialxv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glMaterialxv(face, pname, params);
	}

	public static void glMatrixMode(int mode) {
		checkContext("glMatrixMode");
		
		gl.glMatrixMode(mode);
	}

	public static void glMultMatrixx(IntBuffer m) {
		checkContext("glMultMatrixx");
		if (m == null) {
			throw new IllegalArgumentException("m = null");
		}
		
		gl.glMultMatrixx(m);
	}

	public static void glMultiTexCoord4x(int target, int s, int t, int r, int q) {
		checkContext("glMultiTexCoord4x");
		
		gl.glMultiTexCoord4x(target, s, t, r, q);
	}

	public static void glNormal3x(int nx, int ny, int nz) {
		checkContext("glNormal3x");
		
		gl.glNormal3x(nx, ny, nz);
	}

	public static void glNormalPointer(int type, int stride, Buffer pointer) {
		checkContext("glNormalPointer");
		if (pointer == null) {
			throw new IllegalArgumentException("pointer = null");
		}
		
		gl.glNormalPointer(type, stride, pointer);
	}
	
	// VBO variant
	public static void glNormalPointer(int type, int stride, int pointer) {
		checkContext("glNormalPointer");
		if (pointer < 0) {
			throw new IllegalArgumentException("pointer < 0");
		}
		
		gl.glNormalPointer(type, stride, pointer);
	}

	public static void glOrthox(int left, int right, int bottom, int top, int zNear, int zFar) {
		checkContext("glOrthox");
		
		gl.glOrthox(left, right, bottom, top, zNear, zFar);
	}

	public static void glPixelStorei(int pname, int param) {
		checkContext("glPixelStorei");
		
		gl.glPixelStorei(pname, param);
	}

	public static void glPointParameterx(int pname, int param) {
		checkContext("glPointParameterx");
		
		gl.glPointParameterx(pname, param);
	}

	public static void glPointParameterxv(int pname, IntBuffer params) {
		checkContext("glPointParameterxv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glPointParameterxv(pname, params);
	}

	public static void glPointSizex(int size) {
		checkContext("glPointSizex");
		
		gl.glPointSizex(size);
	}

	public static void glPolygonOffsetx(int factor, int units) {
		checkContext("glPolygonOffsetx");
		
		gl.glPolygonOffsetx(factor, units);
	}

	public static void glPopMatrix() {
		checkContext("glPopMatrix");
		
		gl.glPopMatrix();
	}

	public static void glPushMatrix() {
		checkContext("glPushMatrix");
		
		gl.glPushMatrix();
	}

	public static void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
		checkContext("glReadPixels");
		if (pixels == null) {
			throw new IllegalArgumentException("pixels = null");
		}
		
		gl.glReadPixels(x, y, width, height, format, type, pixels);
	}

	public static void glRotatex(int angle, int x, int y, int z) {
		checkContext("glRotatex");
		
		gl.glRotatex(angle, x, y, z);
	}

	public static void glSampleCoverage(float value, boolean invert) {
		checkContext("glSampleCoverage");
		
		gl.glSampleCoverage(value, invert);
	}

	public static void glSampleCoveragex(int value, boolean invert) {
		checkContext("glSampleCoveragex");
		
		gl.glSampleCoveragex(value, invert);
	}

	public static void glScalex(int x, int y, int z) {
		checkContext("glScalex");
		
		gl.glScalex(x, y, z);
	}

	public static void glScissor(int x, int y, int width, int height) {
		checkContext("glScissor");
		
		gl.glScissor(x, y, width, height);
	}

	public static void glShadeModel(int mode) {
		checkContext("glShadeModel");
		
		gl.glShadeModel(mode);
	}

	public static void glStencilFunc(int func, int ref, int mask) {
		checkContext("glStencilFunc");
		
		gl.glStencilFunc(func, ref, mask);
	}

	public static void glStencilMask(int mask) {
		checkContext("glStencilMask");
		
		gl.glStencilMask(mask);
	}

	public static void glStencilOp(int fail, int zfail, int zpass) {
		checkContext("glStencilOp");
		
		gl.glStencilOp(fail, zfail, zpass);
	}

	public static void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {
		checkContext("glTexCoordPointer");
		if (pointer == null) {
			throw new IllegalArgumentException("pointer = null");
		}
		
		gl.glTexCoordPointer(size, type, stride, pointer);
	}

	// VBO variant
	public static void glTexCoordPointer(int size, int type, int stride, int pointer) {
		checkContext("glTexCoordPointer");
		if (pointer < 0) {
			throw new IllegalArgumentException("pointer < 0");
		}
		
		gl.glTexCoordPointer(size, type, stride, pointer);
	}

	public static void glTexEnvi(int target, int pname, int param) {
		checkContext("glTexEnvi");
		
		gl.glTexEnvi(target, pname, param);
	}

	public static void glTexEnvx(int target, int pname, int param) {
		checkContext("glTexEnvx");
		
		gl.glTexEnvx(target, pname, param);
	}

	public static void glTexEnviv(int target, int pname, IntBuffer params) {
		checkContext("glTexEnviv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glTexEnviv(target, pname, params);
	}

	public static void glTexEnvxv(int target, int pname, IntBuffer params) {
		checkContext("glTexEnvxv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glTexEnvxv(target, pname, params);
	}

	public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels) {
		checkContext("glTexImage2D");
		if (pixels == null) {
			throw new IllegalArgumentException("pixels = null");
		}
		
		gl.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
	}

	public static void glTexParameteri(int target, int pname, int param) {
		checkContext("glTexParameteri");
		
		gl.glTexParameteri(target, pname, param);
	}

	public static void glTexParameterx(int target, int pname, int param) {
		checkContext("glTexParameterx");
		
		gl.glTexParameterx(target, pname, param);
	}

	public static void glTexParameteriv(int target, int pname, IntBuffer params) {
		checkContext("glTexParameteriv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glTexParameteriv(target, pname, params);
	}

	public static void glTexParameterxv(int target, int pname, IntBuffer params) {
		checkContext("glTexParameterxv");
		if (params == null) {
			throw new IllegalArgumentException("params = null");
		}
		
		gl.glTexParameterxv(target, pname, params);
	}

	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, Buffer pixels) {
		checkContext("glTexSubImage2D");
		if (pixels == null) {
			throw new IllegalArgumentException("pixels = null");
		}
		
		gl.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
	}

	public static void glTranslatex(int x, int y, int z) {
		checkContext("glTranslatex");
		
		gl.glTranslatex(x, y, z);
	}

	public static void glVertexPointer(int size, int type, int stride, Buffer pointer) {
		checkContext("glVertexPointer");
		if (pointer == null) {
			throw new IllegalArgumentException("pointer = null");
		}
		
		gl.glVertexPointer(size, type, stride, pointer);
	}

	public static void glVertexPointer(int size, int type, int stride, int pointer) {
		checkContext("glVertexPointer");
		if (pointer < 0) {
			throw new IllegalArgumentException("pointer < 0");
		}
		
		gl.glVertexPointer(size, type, stride, pointer);
	}

	public static void glViewport(int x, int y, int width, int height) {
		checkContext("glViewport");
		
		gl.glViewport(x, y, width, height);
	}
	
	
	// utils
	
	static void checkContext(String func) {
		if (contextHandle == 0) {
			throw new GLException(func, "No context");
		}
		if (!contextActive) {
			throw new GLException(func, "Context not active");
		}
		if (Thread.currentThread() != contextCreator) {
			throw new GLException(func, "Called from different thread");
		}
	}
	
	static {
		try {
			loaded = true;
			instance = new GL();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
