package emulator.graphics3D.egl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL11Ext;
import javax.microedition.khronos.opengles.GL11ExtensionPack;
import java.nio.*;

/**
 * GL11
 */
public final class GL11Impl extends GL10Impl implements javax.microedition.khronos.opengles.GL11, GL11Ext, GL11ExtensionPack {
	public final synchronized boolean glIsBuffer(final int n) {
		EGL10Impl.g3d.sync(() -> temp = GL15.glIsBuffer(n));
		return (boolean) temp;
	}

	public final synchronized boolean glIsEnabled(final int n) {
		EGL10Impl.g3d.sync(() -> temp = GL11.glIsEnabled(n));
		return (boolean) temp;
	}

	public final synchronized boolean glIsTexture(final int n) {
		EGL10Impl.g3d.sync(() -> temp = GL11.glIsTexture(n));
		return (boolean) temp;
	}

	public final synchronized void glGenBuffers(final int n, final int[] array, final int n2) {
		final IntBuffer intBuffer = BufferUtils.createIntBuffer(n);
		EGL10Impl.g3d.sync(() -> GL15.glGenBuffers(intBuffer));
		intBuffer.get(array, n2, n);
	}

	public final synchronized void glGenBuffers(final int n, final IntBuffer intBuffer) {
		EGL10Impl.g3d.sync(() -> GL15.glGenBuffers(intBuffer));
	}

	public final synchronized void glDeleteBuffers(final int n, final int[] array, final int n2) {
		final IntBuffer intBuffer;
		(intBuffer = BufferUtils.createIntBuffer(n)).put(array, n2, n);
		intBuffer.position(0);
		EGL10Impl.g3d.sync(() -> GL15.glDeleteBuffers(intBuffer));
	}

	public final synchronized void glDeleteBuffers(final int n, final IntBuffer intBuffer) {
		EGL10Impl.g3d.sync(() -> GL15.glDeleteBuffers(intBuffer));
	}

	public final synchronized void glBindBuffer(final int n, final int n2) {
		EGL10Impl.g3d.sync(() -> GL15.glBindBuffer(n, n2));
	}

	public final synchronized void glBufferData(final int n, final int n2, final Buffer buffer, final int n3) {
		if (buffer instanceof ByteBuffer) {
			EGL10Impl.g3d.sync(() -> GL15.glBufferData(n, (ByteBuffer) buffer, n3));
			return;
		}
		if (buffer instanceof ShortBuffer) {
			EGL10Impl.g3d.sync(() -> GL15.glBufferData(n, (ShortBuffer) buffer, n3));
			return;
		}
		if (buffer instanceof IntBuffer) {
			EGL10Impl.g3d.sync(() -> GL15.glBufferData(n, (IntBuffer) buffer, n3));
			return;
		}
		if (buffer instanceof FloatBuffer) {
			EGL10Impl.g3d.sync(() -> GL15.glBufferData(n, (FloatBuffer) buffer, n3));
		}
	}

	public final synchronized void glBufferSubData(final int n, final int n2, final int n3, final Buffer buffer) {
		if (buffer instanceof ByteBuffer) {
			EGL10Impl.g3d.sync(() -> GL15.glBufferSubData(n, (long) n2, (ByteBuffer) buffer));
			return;
		}
		if (buffer instanceof ShortBuffer) {
			EGL10Impl.g3d.sync(() -> GL15.glBufferSubData(n, (long) n2, (ShortBuffer) buffer));
			return;
		}
		if (buffer instanceof IntBuffer) {
			EGL10Impl.g3d.sync(() -> GL15.glBufferSubData(n, (long) n2, (IntBuffer) buffer));
			return;
		}
		if (buffer instanceof FloatBuffer) {
			EGL10Impl.g3d.sync(() -> GL15.glBufferSubData(n, (long) n2, (FloatBuffer) buffer));
		}
	}

	public final synchronized void glGetBufferParameteriv(final int n, final int n2, final int[] array, final int n3) {
		final IntBuffer intBuffer;
		(intBuffer = BufferUtils.createIntBuffer(n2)).put(array, n3, GLConfiguration.method769());
		intBuffer.position(0);
		EGL10Impl.g3d.sync(() -> GL15.glGetBufferParameteriv(n, n2, intBuffer));
	}

	public final synchronized void glGetBufferParameteriv(final int n, final int n2, final IntBuffer intBuffer) {
		EGL10Impl.g3d.sync(() -> GL15.glGetBufferParameteriv(n, n2, intBuffer));
	}

	public final synchronized void glColorPointer(final int n, final int n2, final int n3, final int n4) {
		EGL10Impl.g3d.sync(() -> GL11.glColorPointer(n, n2, n3, (long) n4));
	}

	public final synchronized void glNormalPointer(final int n, final int n2, final int n3) {
		EGL10Impl.g3d.sync(() -> GL11.glNormalPointer(n, n2, (long) n3));
	}

	public final synchronized void glTexCoordPointer(final int n, final int n2, final int n3, final int n4) {
		EGL10Impl.g3d.sync(() -> GL11.glTexCoordPointer(n, n2, n3, (long) n4));
	}

	public final synchronized void glVertexPointer(final int n, final int n2, final int n3, final int n4) {
		EGL10Impl.g3d.sync(() -> GL11.glVertexPointer(n, n2, n3, (long) n4));
	}

	public final synchronized void glDrawElements(final int n, final int n2, final int n3, final int n4) {
		EGL10Impl.g3d.sync(() -> GL11.glDrawElements(n, n2, n3, (long) n4));
	}

	public final synchronized void glClipPlanef(final int n, final float[] array, final int n2) {
		final DoubleBuffer doubleBuffer;
		(doubleBuffer = BufferUtils.createDoubleBuffer(4)).put(array[n2]);
		doubleBuffer.put(array[n2 + 1]);
		doubleBuffer.put(array[n2 + 2]);
		doubleBuffer.put(array[n2 + 3]);
		doubleBuffer.position(0);
		EGL10Impl.g3d.sync(() -> GL11.glClipPlane(n, doubleBuffer));
	}

	public final synchronized void glClipPlanef(final int n, final FloatBuffer floatBuffer) {
		final DoubleBuffer doubleBuffer;
		(doubleBuffer = BufferUtils.createDoubleBuffer(4)).put(floatBuffer.get());
		doubleBuffer.put(floatBuffer.get());
		doubleBuffer.put(floatBuffer.get());
		doubleBuffer.put(floatBuffer.get());
		doubleBuffer.position(0);
		EGL10Impl.g3d.sync(() -> GL11.glClipPlane(n, doubleBuffer));
	}

	public final synchronized void glClipPlanex(final int n, final int[] array, final int n2) {
		final DoubleBuffer doubleBuffer;
		(doubleBuffer = BufferUtils.createDoubleBuffer(4)).put(array[n2] / 65536.0f);
		doubleBuffer.put(array[n2 + 1] / 65536.0f);
		doubleBuffer.put(array[n2 + 2] / 65536.0f);
		doubleBuffer.put(array[n2 + 3] / 65536.0f);
		doubleBuffer.position(0);
		EGL10Impl.g3d.sync(() -> GL11.glClipPlane(n, doubleBuffer));
	}

	public final synchronized void glClipPlanex(final int n, final IntBuffer intBuffer) {
		final DoubleBuffer doubleBuffer;
		(doubleBuffer = BufferUtils.createDoubleBuffer(4)).put(intBuffer.get() / 65536.0f);
		doubleBuffer.put(intBuffer.get() / 65536.0f);
		doubleBuffer.put(intBuffer.get() / 65536.0f);
		doubleBuffer.put(intBuffer.get() / 65536.0f);
		doubleBuffer.position(0);
		EGL10Impl.g3d.sync(() -> GL11.glClipPlane(n, doubleBuffer));
	}

	public final synchronized void glGetClipPlanef(final int n, final float[] array, final int n2) {
		final DoubleBuffer doubleBuffer = BufferUtils.createDoubleBuffer(4);
		EGL10Impl.g3d.sync(() -> GL11.glGetClipPlane(n, doubleBuffer));
		array[n2] = (float) doubleBuffer.get(0);
		array[n2 + 1] = (float) doubleBuffer.get(1);
		array[n2 + 2] = (float) doubleBuffer.get(2);
		array[n2 + 3] = (float) doubleBuffer.get(3);
	}

	public final synchronized void glGetClipPlanef(final int n, final FloatBuffer floatBuffer) {
		final DoubleBuffer doubleBuffer = BufferUtils.createDoubleBuffer(4);
		EGL10Impl.g3d.sync(() -> GL11.glGetClipPlane(n, doubleBuffer));
		floatBuffer.put((float) doubleBuffer.get(0));
		floatBuffer.put((float) doubleBuffer.get(1));
		floatBuffer.put((float) doubleBuffer.get(2));
		floatBuffer.put((float) doubleBuffer.get(3));
	}

	public final synchronized void glGetClipPlanex(final int n, final int[] array, final int n2) {
		final DoubleBuffer doubleBuffer = BufferUtils.createDoubleBuffer(4);
		EGL10Impl.g3d.sync(() -> GL11.glGetClipPlane(n, doubleBuffer));
		array[n2] = (int) (doubleBuffer.get(0) * 65536.0);
		array[n2 + 1] = (int) (doubleBuffer.get(1) * 65536.0);
		array[n2 + 2] = (int) (doubleBuffer.get(2) * 65536.0);
		array[n2 + 3] = (int) (doubleBuffer.get(3) * 65536.0);
	}

	public final synchronized void glGetClipPlanex(final int n, final IntBuffer intBuffer) {
		final DoubleBuffer doubleBuffer = BufferUtils.createDoubleBuffer(4);
		EGL10Impl.g3d.sync(() -> GL11.glGetClipPlane(n, doubleBuffer));
		intBuffer.put((int) (doubleBuffer.get(0) * 65536.0));
		intBuffer.put((int) (doubleBuffer.get(1) * 65536.0));
		intBuffer.put((int) (doubleBuffer.get(2) * 65536.0));
		intBuffer.put((int) (doubleBuffer.get(3) * 65536.0));
	}

	public final synchronized void glGetFixedv(final int n, final int[] array, final int n2) {
		final int method768 = GLConfiguration.method768(n);
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		EGL10Impl.g3d.sync(() -> GL11.glGetFloatv(n, floatBuffer));
		for (int i = 0; i < method768; ++i) {
			array[n2 + i] = (int) (floatBuffer.get(i) * 65536.0f);
		}
	}

	public final synchronized void glGetFixedv(final int n, final IntBuffer intBuffer) {
		final int method768 = GLConfiguration.method768(n);
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		EGL10Impl.g3d.sync(() -> GL11.glGetFloatv(n, floatBuffer));
		for (int i = 0; i < method768; ++i) {
			intBuffer.put((int) (floatBuffer.get(i) * 65536.0f));
		}
	}

	public final synchronized void glGetFloatv(final int n, final float[] array, final int n2) {
		final int method768 = GLConfiguration.method768(n);
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		EGL10Impl.g3d.sync(() -> GL11.glGetFloatv(n, floatBuffer));
		for (int i = 0; i < method768; ++i) {
			array[n2 + i] = floatBuffer.get(i);
		}
	}

	public final synchronized void glGetFloatv(final int n, final FloatBuffer floatBuffer) {
		final int method768 = GLConfiguration.method768(n);
		final FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);
		EGL10Impl.g3d.sync(() -> GL11.glGetFloatv(n, floatBuffer2));
		for (int i = 0; i < method768; ++i) {
			floatBuffer.put(floatBuffer2.get(i));
		}
	}

	public final synchronized void glGetLightfv(final int n, final int n2, final float[] array, final int n3) {
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetLightfv(n, n2, floatBuffer));
		for (int i = 0; i < method768; ++i) {
			array[n3 + i] = floatBuffer.get(i);
		}
	}

	public final synchronized void glGetLightfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		EGL10Impl.g3d.sync(() -> GL11.glGetLightfv(n, n2, floatBuffer));
	}

	public final synchronized void glGetLightxv(final int n, final int n2, final int[] array, final int n3) {
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetLightfv(n, n2, floatBuffer));
		for (int i = 0; i < method768; ++i) {
			array[n3 + i] = (int) (floatBuffer.get(i) * 65536.0f);
		}
	}

	public final synchronized void glGetLightxv(final int n, final int n2, final IntBuffer intBuffer) {
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetLightfv(n, n2, floatBuffer));
		for (int i = 0; i < method768; ++i) {
			intBuffer.put((int) (floatBuffer.get(i) * 65536.0f));
		}
	}

	public final synchronized void glGetMaterialfv(final int n, final int n2, final float[] array, final int n3) {
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetMaterialfv(n, n2, floatBuffer));
		for (int i = 0; i < method768; ++i) {
			array[n3 + i] = floatBuffer.get(i);
		}
	}

	public final synchronized void glGetMaterialfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		EGL10Impl.g3d.sync(() -> GL11.glGetMaterialfv(n, n2, floatBuffer));
	}

	public final synchronized void glGetMaterialxv(final int n, final int n2, final int[] array, final int n3) {
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetMaterialfv(n, n2, floatBuffer));
		for (int i = 0; i < method768; ++i) {
			array[n3 + i] = (int) (floatBuffer.get(i) * 65536.0f);
		}
	}

	public final synchronized void glGetMaterialxv(final int n, final int n2, final IntBuffer intBuffer) {
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetMaterialfv(n, n2, floatBuffer));
		for (int i = 0; i < method768; ++i) {
			intBuffer.put((int) (floatBuffer.get(i) * 65536.0f));
		}
	}

	public final synchronized void glGetPointerv(final int n, final Buffer[] array) {
		if (array == null || array.length < 1) {
			throw new IllegalArgumentException();
		}
		// TODO
//		PointerBuffer pointerBuffer = BufferUtils.createPointerBuffer(1);
//		EGL10Impl.g3d.sync(() -> GL11.glGetPointerv(n, pointerBuffer));
//		array[0] = pointerBuffer.get();
	}

	public final synchronized void glGetTexEnvfv(final int n, final int n2, final float[] array, final int n3) {
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetTexEnvfv(n, n2, floatBuffer));
		floatBuffer.get(array, n3, method775);
	}

	public final synchronized void glGetTexEnvfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		EGL10Impl.g3d.sync(() -> GL11.glGetTexEnvfv(n, n2, floatBuffer));
	}

	public final synchronized void glGetTexEnviv(final int n, final int n2, final int[] array, final int n3) {
		final int method775;
		final IntBuffer intBuffer = BufferUtils.createIntBuffer(method775 = GLConfiguration.method775(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetTexEnviv(n, n2, intBuffer));
		intBuffer.get(array, n3, method775);
	}

	public final synchronized void glGetTexEnviv(final int n, final int n2, final IntBuffer intBuffer) {
		EGL10Impl.g3d.sync(() -> GL11.glGetTexEnviv(n, n2, intBuffer));
	}

	public final synchronized void glGetTexEnvxv(final int n, final int n2, final int[] array, final int n3) {
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetTexEnvfv(n, n2, floatBuffer));
		for (int i = 0; i < method775; ++i) {
			array[n3 + i] = (int) (floatBuffer.get(i) * 65536.0f);
		}
	}

	public final synchronized void glGetTexEnvxv(final int n, final int n2, final IntBuffer intBuffer) {
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetTexEnvfv(n, n2, floatBuffer));
		for (int i = 0; i < method775; ++i) {
			intBuffer.put((int) (floatBuffer.get(i) * 65536.0f));
		}
	}

	public final synchronized void glGetTexParameterfv(final int n, final int n2, final float[] array, final int n3) {
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetTexParameterfv(n, n2, floatBuffer));
		floatBuffer.get(array, n3, method775);
	}

	public final synchronized void glGetTexParameterfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		EGL10Impl.g3d.sync(() -> GL11.glGetTexParameterfv(n, n2, floatBuffer));
	}

	public final synchronized void glGetTexParameteriv(final int n, final int n2, final int[] array, final int n3) {
		final int method775;
		final IntBuffer intBuffer = BufferUtils.createIntBuffer(method775 = GLConfiguration.method775(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetTexParameteriv(n, n2, intBuffer));
		intBuffer.get(array, n3, method775);
	}

	public final synchronized void glGetTexParameteriv(final int n, final int n2, final IntBuffer intBuffer) {
		EGL10Impl.g3d.sync(() -> GL11.glGetTexParameteriv(n, n2, intBuffer));
	}

	public final synchronized void glGetTexParameterxv(final int n, final int n2, final int[] array, final int n3) {
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetTexParameterfv(n, n2, floatBuffer));
		for (int i = 0; i < method775; ++i) {
			array[n3 + i] = (int) (floatBuffer.get(i) * 65536.0f);
		}
	}

	public final synchronized void glGetTexParameterxv(final int n, final int n2, final IntBuffer intBuffer) {
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		EGL10Impl.g3d.sync(() -> GL11.glGetTexParameterfv(n, n2, floatBuffer));
		for (int i = 0; i < method775; ++i) {
			intBuffer.put((int) (floatBuffer.get(i) * 65536.0f));
		}
	}

	public final synchronized void glDrawTexsOES(final short n, final short n2, final short n3, final short n4, final short n5) {
		this.glDrawTexfOES(n, n2, n3, n4, n5);
	}

	public final synchronized void glDrawTexiOES(final int n, final int n2, final int n3, final int n4, final int n5) {
		this.glDrawTexfOES(n, n2, n3, n4, n5);
	}

	public final synchronized void glDrawTexfOES(final float n, final float n2, final float n3, final float n4, final float n5) {
		if (!GLConfiguration.OES_draw_texture) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glDrawTexxOES(final int n, final int n2, final int n3, final int n4, final int n5) {
		this.glDrawTexfOES(n / 65536.0f, n2 / 65536.0f, n3 / 65536.0f, n4 / 65536.0f, n5 / 65536.0f);
	}

	public final synchronized void glDrawTexsvOES(final short[] array, final int n) {
		this.glDrawTexfOES(array[n], array[n + 1], array[n + 2], array[n + 3], array[n + 4]);
	}

	public final synchronized void glDrawTexsvOES(final ShortBuffer shortBuffer) {
		short[] a = new short[5];
		shortBuffer.get(a);
		this.glDrawTexsvOES(a, 0);
	}

	public final synchronized void glDrawTexivOES(final int[] array, final int n) {
		this.glDrawTexfOES(array[n], array[n + 1], array[n + 2], array[n + 3], array[n + 4]);
	}

	public final synchronized void glDrawTexivOES(final IntBuffer intBuffer) {
		int[] a = new int[5];
		intBuffer.get(a);
		this.glDrawTexivOES(a, 0);
	}

	public final synchronized void glDrawTexxvOES(final int[] array, final int n) {
		this.glDrawTexfOES(array[n] / 65536.0f, array[n + 1] / 65536.0f, array[n + 2] / 65536.0f, array[n + 3] / 65536.0f, array[n + 4] / 65536.0f);
	}

	public final synchronized void glDrawTexxvOES(final IntBuffer intBuffer) {
		int[] a = new int[5];
		intBuffer.get(a);
		this.glDrawTexxvOES(a, 0);
	}

	public final synchronized void glDrawTexfvOES(final float[] array, final int n) {
		this.glDrawTexfOES(array[n], array[n + 1], array[n + 2], array[n + 3], array[n + 4]);
	}

	public final synchronized void glDrawTexfvOES(final FloatBuffer floatBuffer) {
		float[] a = new float[5];
		floatBuffer.get(a);
		this.glDrawTexfvOES(a, 0);
	}

	public final synchronized void glCurrentPaletteMatrixOES(final int n) {
		if (!GLConfiguration.OES_matrix_pallete) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
		EGL10Impl.g3d.sync(() -> ARBMatrixPalette.glCurrentPaletteMatrixARB(n));
	}

	public final synchronized void glLoadPaletteFromModelViewMatrixOES() {
		if (!GLConfiguration.OES_matrix_pallete) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
	}

	public final synchronized void glMatrixIndexPointerOES(final int n, final int n2, final int n3, final Buffer buffer) {
		if (!GLConfiguration.OES_matrix_pallete) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
		EGL10Impl.g3d.sync(() -> ARBMatrixPalette.glMatrixIndexPointerARB(n, n2, n3, MemoryUtil.memAddress(buffer)));
	}

	public final synchronized void glMatrixIndexPointerOES(final int n, final int n2, final int n3, final int n4) {
		if (!GLConfiguration.OES_matrix_pallete) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
		EGL10Impl.g3d.sync(() -> ARBMatrixPalette.glMatrixIndexPointerARB(n, n2, n3, (long) n4));
	}

	public final synchronized void glWeightPointerOES(final int n, final int n2, final int n3, final Buffer buffer) {
		if (!GLConfiguration.OES_matrix_pallete) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
		EGL10Impl.g3d.sync(() -> ARBVertexBlend.glWeightPointerARB(n, n2, n3, MemoryUtil.memAddress(buffer)));
	}

	public final synchronized void glWeightPointerOES(final int n, final int n2, final int n3, final int n4) {
		if (!GLConfiguration.OES_matrix_pallete) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
		EGL10Impl.g3d.sync(() -> ARBVertexBlend.glWeightPointerARB(n, n2, n3, (long) n4));
	}

	private static void checkTextureCubeMapExt() {
		if (!GLConfiguration.OES_texture_cube_map) {
			throw new UnsupportedOperationException("OES_texture_cube_map extension not available");
		}
	}

	public final synchronized void glTexGenf(final int n, final int n2, final float n3) {
		checkTextureCubeMapExt();
		EGL10Impl.g3d.sync(() -> GL11.glTexGenf(n, n2, n3));
	}

	public final synchronized void glTexGeni(final int n, final int n2, final int n3) {
		checkTextureCubeMapExt();
		EGL10Impl.g3d.sync(() -> GL11.glTexGeni(n, n2, n3));
	}

	public final synchronized void glTexGenx(final int n, final int n2, final int n3) {
		checkTextureCubeMapExt();
		EGL10Impl.g3d.sync(() -> GL11.glTexGenf(n, n2, n3 / 65536.0f));
	}

	public final synchronized void glTexGenfv(final int n, final int n2, final float[] array, final int n3) {
		checkTextureCubeMapExt();
		final int method771;
		final FloatBuffer floatBuffer;
		(floatBuffer = BufferUtils.createFloatBuffer(method771 = GLConfiguration.method771())).put(array, n3, method771);
		floatBuffer.position(0);
		EGL10Impl.g3d.sync(() -> GL11.glTexGenfv(n, n2, floatBuffer));
	}

	public final synchronized void glTexGenfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		checkTextureCubeMapExt();
		EGL10Impl.g3d.sync(() -> GL11.glTexGenfv(n, n2, floatBuffer));
	}

	public final synchronized void glTexGeniv(final int n, final int n2, final int[] array, final int n3) {
		checkTextureCubeMapExt();
		final int method771;
		final IntBuffer intBuffer;
		(intBuffer = BufferUtils.createIntBuffer(method771 = GLConfiguration.method771())).put(array, n3, method771);
		intBuffer.position(0);
		EGL10Impl.g3d.sync(() -> GL11.glTexGeniv(n, n2, intBuffer));
	}

	public final synchronized void glTexGeniv(final int n, final int n2, final IntBuffer intBuffer) {
		checkTextureCubeMapExt();
		EGL10Impl.g3d.sync(() -> GL11.glTexGeniv(n, n2, intBuffer));
	}

	public final synchronized void glTexGenxv(final int n, final int n2, final int[] array, final int n3) {
		checkTextureCubeMapExt();
		final int method771;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method771 = GLConfiguration.method771());
		for (int i = 0; i < method771; ++i) {
			floatBuffer.put(array[i] / 65536.0f);
		}
		floatBuffer.position(0);
		EGL10Impl.g3d.sync(() -> GL11.glTexGenfv(n, n2, floatBuffer));
	}

	public final synchronized void glTexGenxv(final int n, final int n2, final IntBuffer intBuffer) {
		checkTextureCubeMapExt();
		final int method771;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method771 = GLConfiguration.method771());
		for (int i = 0; i < method771; ++i) {
			floatBuffer.put(intBuffer.get() / 65536.0f);
		}
		floatBuffer.position(0);
		EGL10Impl.g3d.sync(() -> GL11.glTexGenfv(n, n2, floatBuffer));
	}

	public final synchronized void glGetTexGenfv(final int n, final int n2, final float[] array, final int n3) {
		checkTextureCubeMapExt();
		final int method771;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method771 = GLConfiguration.method771());
		EGL10Impl.g3d.sync(() -> GL11.glGetTexGenfv(n, n2, floatBuffer));
		floatBuffer.get(array, n3, method771);
	}

	public final synchronized void glGetTexGenfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		checkTextureCubeMapExt();
		EGL10Impl.g3d.sync(() -> GL11.glGetTexGenfv(n, n2, floatBuffer));
	}

	public final synchronized void glGetTexGeniv(final int n, final int n2, final int[] array, final int n3) {
		checkTextureCubeMapExt();
		final int method771;
		final IntBuffer intBuffer = BufferUtils.createIntBuffer(method771 = GLConfiguration.method771());
		EGL10Impl.g3d.sync(() -> GL11.glGetTexGeniv(n, n2, intBuffer));
		intBuffer.get(array, n3, method771);
	}

	public final synchronized void glGetTexGeniv(final int n, final int n2, final IntBuffer intBuffer) {
		checkTextureCubeMapExt();
		EGL10Impl.g3d.sync(() -> GL11.glGetTexGeniv(n, n2, intBuffer));
	}

	public final synchronized void glGetTexGenxv(final int n, final int n2, final int[] array, final int n3) {
		checkTextureCubeMapExt();
		final int method771;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method771 = GLConfiguration.method771());
		EGL10Impl.g3d.sync(() ->GL11.glGetTexGenfv(n, n2, floatBuffer));
		for (int i = 0; i < method771; ++i) {
			array[i + n3] = (int) (floatBuffer.get(i) * 65536.0f);
		}
	}

	public final synchronized void glGetTexGenxv(final int n, final int n2, final IntBuffer intBuffer) {
		checkTextureCubeMapExt();
		final int method771;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method771 = GLConfiguration.method771());
		EGL10Impl.g3d.sync(() ->GL11.glGetTexGenfv(n, n2, floatBuffer));
		for (int i = 0; i < method771; ++i) {
			intBuffer.put((int) (floatBuffer.get(i) * 65536.0f));
		}
	}

	public final synchronized void glBlendEquation(final int n) {
		if (!GLConfiguration.OES_blend_subtract) {
			throw new UnsupportedOperationException("OES_blend_subtract extension not available");
		}
		EGL10Impl.g3d.sync(() -> GL14.glBlendEquation(n));
	}

	public final synchronized void glBlendFuncSeparate(final int n, final int n2, final int n3, final int n4) {
		if (!GLConfiguration.OES_blend_func_separate) {
			throw new UnsupportedOperationException("OES_blend_func_separate extension not available");
		}
		EGL10Impl.g3d.sync(() -> GL14.glBlendFuncSeparate(n, n2, n3, n4));
	}

	public final synchronized void glBlendEquationSeparate(final int n, final int n2) {
		if (!GLConfiguration.OES_blend_equations_separate) {
			throw new UnsupportedOperationException("OES_blend_equations_separate extension not available");
		}
		EGL10Impl.g3d.sync(() -> GL20.glBlendEquationSeparate(n, n2));
	}

	private void checkFramebufferExt() {
		if (!GLConfiguration.OES_framebuffer_object) {
			throw new UnsupportedOperationException("OES_framebuffer_object extension not available");
		}
	}

	public final synchronized boolean glIsRenderbufferOES(final int n) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> temp = GL30.glIsRenderbuffer(n));
		return (boolean) temp;
	}

	public final synchronized void glBindRenderbufferOES(final int n, final int n2) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> GL30.glBindRenderbuffer(n, n2));
	}

	public final synchronized void glDeleteRenderbuffersOES(final int n, final int[] array, final int n2) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> GL30.glDeleteRenderbuffers(array));
	}

	public final synchronized void glDeleteRenderbuffersOES(final int n, final IntBuffer intBuffer) {
		checkFramebufferExt();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGenRenderbuffersOES(final int n, final int[] array, final int n2) {
		checkFramebufferExt();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGenRenderbuffersOES(final int n, final IntBuffer intBuffer) {
		checkFramebufferExt();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glRenderbufferStorageOES(final int n, final int n2, final int n3, final int n4) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> GL30.glRenderbufferStorage(n, n2, n3, n4));
	}

	public final synchronized void glGetRenderbufferParameterivOES(final int n, final int n2, final int[] array, final int n3) {
		checkFramebufferExt();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGetRenderbufferParameterivOES(final int n, final int n2, final IntBuffer intBuffer) {
		checkFramebufferExt();
		System.out.println("OES is not implemented.");
	}

	public final synchronized boolean glIsFramebufferOES(final int n) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> temp = GL30.glIsFramebuffer(n));
		return (boolean) temp;
	}

	public final synchronized void glBindFramebufferOES(final int n, final int n2) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> GL30.glBindFramebuffer(n, n2));
	}

	public final synchronized void glDeleteFramebuffersOES(final int n, final int[] array, final int n2) {
		checkFramebufferExt();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glDeleteFramebuffersOES(final int n, final IntBuffer intBuffer) {
		checkFramebufferExt();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGenFramebuffersOES(final int n, final int[] array, final int n2) {
		checkFramebufferExt();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGenFramebuffersOES(final int n, final IntBuffer intBuffer) {
		checkFramebufferExt();
		System.out.println("OES is not implemented.");
	}

	public final synchronized int glCheckFramebufferStatusOES(final int n) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> temp = GL30.glCheckFramebufferStatus(n));
		return (int) temp;
	}

	public final synchronized void glFramebufferTexture2DOES(final int n, final int n2, final int n3, final int n4, final int n5) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> GL30.glFramebufferTexture2D(n, n2, n3, n4, n5));
	}

	public final synchronized void glFramebufferRenderbufferOES(final int n, final int n2, final int n3, final int n4) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> GL30.glFramebufferRenderbuffer(n, n2, n3, n4));
	}

	public final synchronized void glGetFramebufferAttachmentParameterivOES(final int n, final int n2, final int n3, final int[] array, final int n4) {
		checkFramebufferExt();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGetFramebufferAttachmentParameterivOES(final int n, final int n2, final int n3, final IntBuffer intBuffer) {
		checkFramebufferExt();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGenerateMipmapOES(final int n) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> GL30.glGenerateMipmap(n));
	}

	public final synchronized void glPointSizePointerOES(final int n, final int n2, final Buffer buffer) {
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glPointSizePointerOES(final int n, final int n2, final int n3) {
		System.out.println("OES is not implemented.");
	}

	public GL11Impl(final EGLContext eglContext) {
		super(eglContext);
	}
}
