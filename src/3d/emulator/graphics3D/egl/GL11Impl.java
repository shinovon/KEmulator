package emulator.graphics3D.egl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;

import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import javax.microedition.khronos.opengles.GL11ExtensionPack;
import java.nio.*;

/**
 * GL11
 */
public final class GL11Impl extends GL10Impl implements GL11, GL11Ext, GL11ExtensionPack {
	public final synchronized boolean glIsBuffer(final int n) {
		this.checkThread();
		return GL15.glIsBuffer(n);
	}

	public final synchronized boolean glIsEnabled(final int n) {
		this.checkThread();
		return org.lwjgl.opengl.GL11.glIsEnabled(n);
	}

	public final synchronized boolean glIsTexture(final int n) {
		this.checkThread();
		return org.lwjgl.opengl.GL11.glIsTexture(n);
	}

	public final synchronized void glGenBuffers(final int n, final int[] array, final int n2) {
		this.checkThread();
		final IntBuffer intBuffer;
		GL15.glGenBuffers(intBuffer = BufferUtils.createIntBuffer(n));
		intBuffer.get(array, n2, n);
	}

	public final synchronized void glGenBuffers(final int n, final IntBuffer intBuffer) {
		this.checkThread();
		GL15.glGenBuffers(intBuffer);
	}

	public final synchronized void glDeleteBuffers(final int n, final int[] array, final int n2) {
		this.checkThread();
		final IntBuffer intBuffer;
		(intBuffer = BufferUtils.createIntBuffer(n)).put(array, n2, n);
		intBuffer.position(0);
		GL15.glDeleteBuffers(intBuffer);
	}

	public final synchronized void glDeleteBuffers(final int n, final IntBuffer intBuffer) {
		this.checkThread();
		GL15.glDeleteBuffers(intBuffer);
	}

	public final synchronized void glBindBuffer(final int n, final int n2) {
		this.checkThread();
		GL15.glBindBuffer(n, n2);
	}

	public final synchronized void glBufferData(final int n, final int n2, final Buffer buffer, final int n3) {
		this.checkThread();
		if (buffer instanceof ByteBuffer) {
			GL15.glBufferData(n, (ByteBuffer) buffer, n3);
			return;
		}
		if (buffer instanceof ShortBuffer) {
			GL15.glBufferData(n, (ShortBuffer) buffer, n3);
			return;
		}
		if (buffer instanceof IntBuffer) {
			GL15.glBufferData(n, (IntBuffer) buffer, n3);
			return;
		}
		if (buffer instanceof FloatBuffer) {
			GL15.glBufferData(n, (FloatBuffer) buffer, n3);
		}
	}

	public final synchronized void glBufferSubData(final int n, final int n2, final int n3, final Buffer buffer) {
		this.checkThread();
		if (buffer instanceof ByteBuffer) {
			GL15.glBufferSubData(n, (long) n2, (ByteBuffer) buffer);
			return;
		}
		if (buffer instanceof ShortBuffer) {
			GL15.glBufferSubData(n, (long) n2, (ShortBuffer) buffer);
			return;
		}
		if (buffer instanceof IntBuffer) {
			GL15.glBufferSubData(n, (long) n2, (IntBuffer) buffer);
			return;
		}
		if (buffer instanceof FloatBuffer) {
			GL15.glBufferSubData(n, (long) n2, (FloatBuffer) buffer);
		}
	}

	public final synchronized void glGetBufferParameteriv(final int n, final int n2, final int[] array, final int n3) {
		this.checkThread();
		final IntBuffer intBuffer;
		(intBuffer = BufferUtils.createIntBuffer(n2)).put(array, n3, GLConfiguration.method769());
		intBuffer.position(0);
		GL15.glGetBufferParameteriv(n, n2, intBuffer);
	}

	public final synchronized void glGetBufferParameteriv(final int n, final int n2, final IntBuffer intBuffer) {
		this.checkThread();
		GL15.glGetBufferParameteriv(n, n2, intBuffer);
	}

	public final synchronized void glColorPointer(final int n, final int n2, final int n3, final int n4) {
		this.checkThread();
		org.lwjgl.opengl.GL11.glColorPointer(n, n2, n3, (long) n4);
	}

	public final synchronized void glNormalPointer(final int n, final int n2, final int n3) {
		this.checkThread();
		org.lwjgl.opengl.GL11.glNormalPointer(n, n2, (long) n3);
	}

	public final synchronized void glTexCoordPointer(final int n, final int n2, final int n3, final int n4) {
		this.checkThread();
		org.lwjgl.opengl.GL11.glTexCoordPointer(n, n2, n3, (long) n4);
	}

	public final synchronized void glVertexPointer(final int n, final int n2, final int n3, final int n4) {
		this.checkThread();
		org.lwjgl.opengl.GL11.glVertexPointer(n, n2, n3, (long) n4);
	}

	public final synchronized void glDrawElements(final int n, final int n2, final int n3, final int n4) {
		this.checkThread();
		org.lwjgl.opengl.GL11.glDrawElements(n, n2, n3, (long) n4);
	}

	public final synchronized void glClipPlanef(final int n, final float[] array, final int n2) {
		this.checkThread();
		final DoubleBuffer doubleBuffer;
		(doubleBuffer = BufferUtils.createDoubleBuffer(4)).put(array[n2]);
		doubleBuffer.put(array[n2 + 1]);
		doubleBuffer.put(array[n2 + 2]);
		doubleBuffer.put(array[n2 + 3]);
		doubleBuffer.position(0);
		org.lwjgl.opengl.GL11.glClipPlane(n, doubleBuffer);
	}

	public final synchronized void glClipPlanef(final int n, final FloatBuffer floatBuffer) {
		this.checkThread();
		final DoubleBuffer doubleBuffer;
		(doubleBuffer = BufferUtils.createDoubleBuffer(4)).put(floatBuffer.get());
		doubleBuffer.put(floatBuffer.get());
		doubleBuffer.put(floatBuffer.get());
		doubleBuffer.put(floatBuffer.get());
		doubleBuffer.position(0);
		org.lwjgl.opengl.GL11.glClipPlane(n, doubleBuffer);
	}

	public final synchronized void glClipPlanex(final int n, final int[] array, final int n2) {
		this.checkThread();
		final DoubleBuffer doubleBuffer;
		(doubleBuffer = BufferUtils.createDoubleBuffer(4)).put(array[n2] / 65536.0f);
		doubleBuffer.put(array[n2 + 1] / 65536.0f);
		doubleBuffer.put(array[n2 + 2] / 65536.0f);
		doubleBuffer.put(array[n2 + 3] / 65536.0f);
		doubleBuffer.position(0);
		org.lwjgl.opengl.GL11.glClipPlane(n, doubleBuffer);
	}

	public final synchronized void glClipPlanex(final int n, final IntBuffer intBuffer) {
		this.checkThread();
		final DoubleBuffer doubleBuffer;
		(doubleBuffer = BufferUtils.createDoubleBuffer(4)).put(intBuffer.get() / 65536.0f);
		doubleBuffer.put(intBuffer.get() / 65536.0f);
		doubleBuffer.put(intBuffer.get() / 65536.0f);
		doubleBuffer.put(intBuffer.get() / 65536.0f);
		doubleBuffer.position(0);
		org.lwjgl.opengl.GL11.glClipPlane(n, doubleBuffer);
	}

	public final synchronized void glGetClipPlanef(final int n, final float[] array, final int n2) {
		this.checkThread();
		final DoubleBuffer doubleBuffer = BufferUtils.createDoubleBuffer(4);
		org.lwjgl.opengl.GL11.glGetClipPlane(n, doubleBuffer);
		array[n2] = (float) doubleBuffer.get(0);
		array[n2 + 1] = (float) doubleBuffer.get(1);
		array[n2 + 2] = (float) doubleBuffer.get(2);
		array[n2 + 3] = (float) doubleBuffer.get(3);
	}

	public final synchronized void glGetClipPlanef(final int n, final FloatBuffer floatBuffer) {
		this.checkThread();
		final DoubleBuffer doubleBuffer = BufferUtils.createDoubleBuffer(4);
		org.lwjgl.opengl.GL11.glGetClipPlane(n, doubleBuffer);
		floatBuffer.put((float) doubleBuffer.get(0));
		floatBuffer.put((float) doubleBuffer.get(1));
		floatBuffer.put((float) doubleBuffer.get(2));
		floatBuffer.put((float) doubleBuffer.get(3));
	}

	public final synchronized void glGetClipPlanex(final int n, final int[] array, final int n2) {
		this.checkThread();
		final DoubleBuffer doubleBuffer = BufferUtils.createDoubleBuffer(4);
		org.lwjgl.opengl.GL11.glGetClipPlane(n, doubleBuffer);
		array[n2] = (int) (doubleBuffer.get(0) * 65536.0);
		array[n2 + 1] = (int) (doubleBuffer.get(1) * 65536.0);
		array[n2 + 2] = (int) (doubleBuffer.get(2) * 65536.0);
		array[n2 + 3] = (int) (doubleBuffer.get(3) * 65536.0);
	}

	public final synchronized void glGetClipPlanex(final int n, final IntBuffer intBuffer) {
		this.checkThread();
		final DoubleBuffer doubleBuffer = BufferUtils.createDoubleBuffer(4);
		org.lwjgl.opengl.GL11.glGetClipPlane(n, doubleBuffer);
		intBuffer.put((int) (doubleBuffer.get(0) * 65536.0));
		intBuffer.put((int) (doubleBuffer.get(1) * 65536.0));
		intBuffer.put((int) (doubleBuffer.get(2) * 65536.0));
		intBuffer.put((int) (doubleBuffer.get(3) * 65536.0));
	}

	public final synchronized void glGetFixedv(final int n, final int[] array, final int n2) {
		this.checkThread();
		final int method768 = GLConfiguration.method768(n);
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		org.lwjgl.opengl.GL11.glGetFloatv(n, floatBuffer);
		for (int i = 0; i < method768; ++i) {
			array[n2 + i] = (int) (floatBuffer.get(i) * 65536.0f);
		}
	}

	public final synchronized void glGetFixedv(final int n, final IntBuffer intBuffer) {
		this.checkThread();
		final int method768 = GLConfiguration.method768(n);
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		org.lwjgl.opengl.GL11.glGetFloatv(n, floatBuffer);
		for (int i = 0; i < method768; ++i) {
			intBuffer.put((int) (floatBuffer.get(i) * 65536.0f));
		}
	}

	public final synchronized void glGetFloatv(final int n, final float[] array, final int n2) {
		this.checkThread();
		final int method768 = GLConfiguration.method768(n);
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		org.lwjgl.opengl.GL11.glGetFloatv(n, floatBuffer);
		for (int i = 0; i < method768; ++i) {
			array[n2 + i] = floatBuffer.get(i);
		}
	}

	public final synchronized void glGetFloatv(final int n, final FloatBuffer floatBuffer) {
		this.checkThread();
		final int method768 = GLConfiguration.method768(n);
		final FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);
		org.lwjgl.opengl.GL11.glGetFloatv(n, floatBuffer2);
		for (int i = 0; i < method768; ++i) {
			floatBuffer.put(floatBuffer2.get(i));
		}
	}

	public final synchronized void glGetLightfv(final int n, final int n2, final float[] array, final int n3) {
		this.checkThread();
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n2));
		org.lwjgl.opengl.GL11.glGetLightfv(n, n2, floatBuffer);
		for (int i = 0; i < method768; ++i) {
			array[n3 + i] = floatBuffer.get(i);
		}
	}

	public final synchronized void glGetLightfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		this.checkThread();
		org.lwjgl.opengl.GL11.glGetLightfv(n, n2, floatBuffer);
	}

	public final synchronized void glGetLightxv(final int n, final int n2, final int[] array, final int n3) {
		this.checkThread();
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n2));
		org.lwjgl.opengl.GL11.glGetLightfv(n, n2, floatBuffer);
		for (int i = 0; i < method768; ++i) {
			array[n3 + i] = (int) (floatBuffer.get(i) * 65536.0f);
		}
	}

	public final synchronized void glGetLightxv(final int n, final int n2, final IntBuffer intBuffer) {
		this.checkThread();
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n2));
		org.lwjgl.opengl.GL11.glGetLightfv(n, n2, floatBuffer);
		for (int i = 0; i < method768; ++i) {
			intBuffer.put((int) (floatBuffer.get(i) * 65536.0f));
		}
	}

	public final synchronized void glGetMaterialfv(final int n, final int n2, final float[] array, final int n3) {
		this.checkThread();
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n2));
		org.lwjgl.opengl.GL11.glGetMaterialfv(n, n2, floatBuffer);
		for (int i = 0; i < method768; ++i) {
			array[n3 + i] = floatBuffer.get(i);
		}
	}

	public final synchronized void glGetMaterialfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		this.checkThread();
		org.lwjgl.opengl.GL11.glGetMaterialfv(n, n2, floatBuffer);
	}

	public final synchronized void glGetMaterialxv(final int n, final int n2, final int[] array, final int n3) {
		this.checkThread();
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n2));
		org.lwjgl.opengl.GL11.glGetMaterialfv(n, n2, floatBuffer);
		for (int i = 0; i < method768; ++i) {
			array[n3 + i] = (int) (floatBuffer.get(i) * 65536.0f);
		}
	}

	public final synchronized void glGetMaterialxv(final int n, final int n2, final IntBuffer intBuffer) {
		this.checkThread();
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n2));
		org.lwjgl.opengl.GL11.glGetMaterialfv(n, n2, floatBuffer);
		for (int i = 0; i < method768; ++i) {
			intBuffer.put((int) (floatBuffer.get(i) * 65536.0f));
		}
	}

	public final synchronized void glGetPointerv(final int n, final Buffer[] array) {
		if (array == null || array.length < 1) {
			throw new IllegalArgumentException();
		}
		// TODO
//		array[0] = org.lwjgl.opengl.GL11.glGetPointerv(n, (long) array[0].remaining());
	}

	public final synchronized void glGetTexEnvfv(final int n, final int n2, final float[] array, final int n3) {
		this.checkThread();
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		org.lwjgl.opengl.GL11.glGetTexEnvfv(n, n2, floatBuffer);
		floatBuffer.get(array, n3, method775);
	}

	public final synchronized void glGetTexEnvfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		this.checkThread();
		org.lwjgl.opengl.GL11.glGetTexEnvfv(n, n2, floatBuffer);
	}

	public final synchronized void glGetTexEnviv(final int n, final int n2, final int[] array, final int n3) {
		this.checkThread();
		final int method775;
		final IntBuffer intBuffer = BufferUtils.createIntBuffer(method775 = GLConfiguration.method775(n2));
		org.lwjgl.opengl.GL11.glGetTexEnviv(n, n2, intBuffer);
		intBuffer.get(array, n3, method775);
	}

	public final synchronized void glGetTexEnviv(final int n, final int n2, final IntBuffer intBuffer) {
		this.checkThread();
		org.lwjgl.opengl.GL11.glGetTexEnviv(n, n2, intBuffer);
	}

	public final synchronized void glGetTexEnvxv(final int n, final int n2, final int[] array, final int n3) {
		this.checkThread();
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		org.lwjgl.opengl.GL11.glGetTexEnvfv(n, n2, floatBuffer);
		for (int i = 0; i < method775; ++i) {
			array[n3 + i] = (int) (floatBuffer.get(i) * 65536.0f);
		}
	}

	public final synchronized void glGetTexEnvxv(final int n, final int n2, final IntBuffer intBuffer) {
		this.checkThread();
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		org.lwjgl.opengl.GL11.glGetTexEnvfv(n, n2, floatBuffer);
		for (int i = 0; i < method775; ++i) {
			intBuffer.put((int) (floatBuffer.get(i) * 65536.0f));
		}
	}

	public final synchronized void glGetTexParameterfv(final int n, final int n2, final float[] array, final int n3) {
		this.checkThread();
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		org.lwjgl.opengl.GL11.glGetTexParameterfv(n, n2, floatBuffer);
		floatBuffer.get(array, n3, method775);
	}

	public final synchronized void glGetTexParameterfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		this.checkThread();
		org.lwjgl.opengl.GL11.glGetTexParameterfv(n, n2, floatBuffer);
	}

	public final synchronized void glGetTexParameteriv(final int n, final int n2, final int[] array, final int n3) {
		this.checkThread();
		final int method775;
		final IntBuffer intBuffer = BufferUtils.createIntBuffer(method775 = GLConfiguration.method775(n2));
		org.lwjgl.opengl.GL11.glGetTexParameteriv(n, n2, intBuffer);
		intBuffer.get(array, n3, method775);
	}

	public final synchronized void glGetTexParameteriv(final int n, final int n2, final IntBuffer intBuffer) {
		this.checkThread();
		org.lwjgl.opengl.GL11.glGetTexParameteriv(n, n2, intBuffer);
	}

	public final synchronized void glGetTexParameterxv(final int n, final int n2, final int[] array, final int n3) {
		this.checkThread();
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		org.lwjgl.opengl.GL11.glGetTexParameterfv(n, n2, floatBuffer);
		for (int i = 0; i < method775; ++i) {
			array[n3 + i] = (int) (floatBuffer.get(i) * 65536.0f);
		}
	}

	public final synchronized void glGetTexParameterxv(final int n, final int n2, final IntBuffer intBuffer) {
		this.checkThread();
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		org.lwjgl.opengl.GL11.glGetTexParameterfv(n, n2, floatBuffer);
		for (int i = 0; i < method775; ++i) {
			intBuffer.put((int) (floatBuffer.get(i) * 65536.0f));
		}
	}

	public final synchronized void glDrawTexsOES(final short n, final short n2, final short n3, final short n4, final short n5) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1325) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glDrawTexiOES(final int n, final int n2, final int n3, final int n4, final int n5) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1325) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glDrawTexfOES(final float n, final float n2, final float n3, final float n4, final float n5) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1325) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glDrawTexxOES(final int n, final int n2, final int n3, final int n4, final int n5) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1325) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glDrawTexsvOES(final short[] array, final int n) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1325) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glDrawTexsvOES(final ShortBuffer shortBuffer) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1325) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glDrawTexivOES(final int[] array, final int n) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1325) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glDrawTexivOES(final IntBuffer intBuffer) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1325) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glDrawTexxvOES(final int[] array, final int n) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1325) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glDrawTexxvOES(final IntBuffer intBuffer) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1325) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glDrawTexfvOES(final float[] array, final int n) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1325) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glDrawTexfvOES(final FloatBuffer floatBuffer) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1325) {
			throw new UnsupportedOperationException("OES_draw_texture extension not available");
		}
	}

	public final synchronized void glCurrentPaletteMatrixOES(final int n) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1326) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
	}

	public final synchronized void glLoadPaletteFromModelViewMatrixOES() {
		this.checkThread();
		if (!GLConfiguration.aBoolean1326) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
	}

	public final synchronized void glMatrixIndexPointerOES(final int n, final int n2, final int n3, final Buffer buffer) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1326) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
	}

	public final synchronized void glMatrixIndexPointerOES(final int n, final int n2, final int n3, final int n4) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1326) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
	}

	public final synchronized void glWeightPointerOES(final int n, final int n2, final int n3, final Buffer buffer) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1326) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
	}

	public final synchronized void glWeightPointerOES(final int n, final int n2, final int n3, final int n4) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1326) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
	}

	private static void method805() {
		if (!GLConfiguration.aBoolean1327) {
			throw new UnsupportedOperationException("OES_texture_cube_map extension not available");
		}
	}

	public final synchronized void glTexGenf(final int n, final int n2, final float n3) {
		this.checkThread();
		method805();
		org.lwjgl.opengl.GL11.glTexGenf(n, n2, n3);
	}

	public final synchronized void glTexGeni(final int n, final int n2, final int n3) {
		this.checkThread();
		method805();
		org.lwjgl.opengl.GL11.glTexGeni(n, n2, n3);
	}

	public final synchronized void glTexGenx(final int n, final int n2, final int n3) {
		this.checkThread();
		method805();
		org.lwjgl.opengl.GL11.glTexGenf(n, n2, n3 / 65536.0f);
	}

	public final synchronized void glTexGenfv(final int n, final int n2, final float[] array, final int n3) {
		this.checkThread();
		method805();
		final int method771;
		final FloatBuffer floatBuffer;
		(floatBuffer = BufferUtils.createFloatBuffer(method771 = GLConfiguration.method771())).put(array, n3, method771);
		floatBuffer.position(0);
		org.lwjgl.opengl.GL11.glTexGenfv(n, n2, floatBuffer);
	}

	public final synchronized void glTexGenfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		this.checkThread();
		method805();
		org.lwjgl.opengl.GL11.glTexGenfv(n, n2, floatBuffer);
	}

	public final synchronized void glTexGeniv(final int n, final int n2, final int[] array, final int n3) {
		this.checkThread();
		method805();
		final int method771;
		final IntBuffer intBuffer;
		(intBuffer = BufferUtils.createIntBuffer(method771 = GLConfiguration.method771())).put(array, n3, method771);
		intBuffer.position(0);
		org.lwjgl.opengl.GL11.glTexGeniv(n, n2, intBuffer);
	}

	public final synchronized void glTexGeniv(final int n, final int n2, final IntBuffer intBuffer) {
		this.checkThread();
		method805();
		org.lwjgl.opengl.GL11.glTexGeniv(n, n2, intBuffer);
	}

	public final synchronized void glTexGenxv(final int n, final int n2, final int[] array, final int n3) {
		this.checkThread();
		method805();
		final int method771;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method771 = GLConfiguration.method771());
		for (int i = 0; i < method771; ++i) {
			floatBuffer.put(array[i] / 65536.0f);
		}
		floatBuffer.position(0);
		org.lwjgl.opengl.GL11.glTexGenfv(n, n2, floatBuffer);
	}

	public final synchronized void glTexGenxv(final int n, final int n2, final IntBuffer intBuffer) {
		this.checkThread();
		method805();
		final int method771;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method771 = GLConfiguration.method771());
		for (int i = 0; i < method771; ++i) {
			floatBuffer.put(intBuffer.get() / 65536.0f);
		}
		floatBuffer.position(0);
		org.lwjgl.opengl.GL11.glTexGenfv(n, n2, floatBuffer);
	}

	public final synchronized void glGetTexGenfv(final int n, final int n2, final float[] array, final int n3) {
		this.checkThread();
		method805();
		final int method771;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method771 = GLConfiguration.method771());
		org.lwjgl.opengl.GL11.glGetTexGenfv(n, n2, floatBuffer);
		floatBuffer.get(array, n3, method771);
	}

	public final synchronized void glGetTexGenfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		this.checkThread();
		method805();
		org.lwjgl.opengl.GL11.glGetTexGenfv(n, n2, floatBuffer);
	}

	public final synchronized void glGetTexGeniv(final int n, final int n2, final int[] array, final int n3) {
		this.checkThread();
		method805();
		final int method771;
		final IntBuffer intBuffer = BufferUtils.createIntBuffer(method771 = GLConfiguration.method771());
		org.lwjgl.opengl.GL11.glGetTexGeniv(n, n2, intBuffer);
		intBuffer.get(array, n3, method771);
	}

	public final synchronized void glGetTexGeniv(final int n, final int n2, final IntBuffer intBuffer) {
		this.checkThread();
		method805();
		org.lwjgl.opengl.GL11.glGetTexGeniv(n, n2, intBuffer);
	}

	public final synchronized void glGetTexGenxv(final int n, final int n2, final int[] array, final int n3) {
		this.checkThread();
		method805();
		final int method771;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method771 = GLConfiguration.method771());
		org.lwjgl.opengl.GL11.glGetTexGenfv(n, n2, floatBuffer);
		for (int i = 0; i < method771; ++i) {
			array[i + n3] = (int) (floatBuffer.get(i) * 65536.0f);
		}
	}

	public final synchronized void glGetTexGenxv(final int n, final int n2, final IntBuffer intBuffer) {
		this.checkThread();
		method805();
		final int method771;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method771 = GLConfiguration.method771());
		org.lwjgl.opengl.GL11.glGetTexGenfv(n, n2, floatBuffer);
		for (int i = 0; i < method771; ++i) {
			intBuffer.put((int) (floatBuffer.get(i) * 65536.0f));
		}
	}

	public final synchronized void glBlendEquation(final int n) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1328) {
			throw new UnsupportedOperationException("OES_blend_subtract extension not available");
		}
		GL14.glBlendEquation(n);
	}

	public final synchronized void glBlendFuncSeparate(final int n, final int n2, final int n3, final int n4) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1329) {
			throw new UnsupportedOperationException("OES_blend_func_separate extension not available");
		}
		GL14.glBlendFuncSeparate(n, n2, n3, n4);
	}

	public final synchronized void glBlendEquationSeparate(final int n, final int n2) {
		this.checkThread();
		if (!GLConfiguration.aBoolean1330) {
			throw new UnsupportedOperationException("OES_blend_equations_separate extension not available");
		}
	}

	public final synchronized boolean glIsRenderbufferOES(final int n) {
		this.checkThread();
		System.out.println("OES is not implemented.");
		return true;
	}

	public final synchronized void glBindRenderbufferOES(final int n, final int n2) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glDeleteRenderbuffersOES(final int n, final int[] array, final int n2) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glDeleteRenderbuffersOES(final int n, final IntBuffer intBuffer) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGenRenderbuffersOES(final int n, final int[] array, final int n2) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGenRenderbuffersOES(final int n, final IntBuffer intBuffer) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glRenderbufferStorageOES(final int n, final int n2, final int n3, final int n4) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGetRenderbufferParameterivOES(final int n, final int n2, final int[] array, final int n3) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGetRenderbufferParameterivOES(final int n, final int n2, final IntBuffer intBuffer) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized boolean glIsFramebufferOES(final int n) {
		this.checkThread();
		System.out.println("OES is not implemented.");
		return true;
	}

	public final synchronized void glBindFramebufferOES(final int n, final int n2) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glDeleteFramebuffersOES(final int n, final int[] array, final int n2) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glDeleteFramebuffersOES(final int n, final IntBuffer intBuffer) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGenFramebuffersOES(final int n, final int[] array, final int n2) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGenFramebuffersOES(final int n, final IntBuffer intBuffer) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized int glCheckFramebufferStatusOES(final int n) {
		this.checkThread();
		System.out.println("OES is not implemented.");
		return 0;
	}

	public final synchronized void glFramebufferTexture2DOES(final int n, final int n2, final int n3, final int n4, final int n5) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glFramebufferRenderbufferOES(final int n, final int n2, final int n3, final int n4) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGetFramebufferAttachmentParameterivOES(final int n, final int n2, final int n3, final int[] array, final int n4) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGetFramebufferAttachmentParameterivOES(final int n, final int n2, final int n3, final IntBuffer intBuffer) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glGenerateMipmapOES(final int n) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glPointSizePointerOES(final int n, final int n2, final Buffer buffer) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public final synchronized void glPointSizePointerOES(final int n, final int n2, final int n3) {
		this.checkThread();
		System.out.println("OES is not implemented.");
	}

	public GL11Impl(final EGLContext eglContext) {
		super(eglContext);
	}
}
