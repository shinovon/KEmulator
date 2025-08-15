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
	// Software implementation of OES_matrix_palette
	private static final int OES_MATRIX_PALETTE = 34880; // GL_MATRIX_PALETTE_OES
	private static final int OES_MATRIX_INDEX_ARRAY = 34884; // GL_MATRIX_INDEX_ARRAY_OES
	private static final int OES_WEIGHT_ARRAY = 34477; // GL_WEIGHT_ARRAY_OES

	private boolean oesMatrixPaletteEnabled = false;
	private boolean oesMatrixIndexArrayEnabled = false;
	private boolean oesWeightArrayEnabled = false;

	private Buffer oesMatrixIndexBuffer = null;
	private int oesMatrixIndexSize = 0;
	private int oesMatrixIndexType = 0;
	private int oesMatrixIndexStride = 0;
	private int oesMatrixIndexOffset = 0;
	private boolean oesMatrixIndexIsOffset = false;

	private Buffer oesWeightBuffer = null;
	private int oesWeightSize = 0;
	private int oesWeightType = 0;
	private int oesWeightStride = 0;
	private int oesWeightOffset = 0;
	private boolean oesWeightIsOffset = false;

	// Simple palette storage (software): default to 32 palette matrices, each 16 floats (4x4)
	private final float[][] paletteMatrices = new float[32][16];
	private int currentPaletteMatrix = 0;

	protected int vertexSize;
	protected int vertexType;
	protected int vertexStride;
	protected int vertexOffset;
	protected boolean vertexIsOffset;
	protected FloatBuffer vertexBuffer;

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

	// Override glVertexPointer to capture client-side vertex array state for software skinning
	public synchronized void glVertexPointer(final int size, final int type, final int stride, final Buffer pointer) {
		// store only client-side float arrays for now
		this.vertexSize = size;
		this.vertexType = type;
		this.vertexStride = stride;
		this.vertexIsOffset = false;
		if (pointer instanceof FloatBuffer) {
			this.vertexBuffer = ((FloatBuffer) pointer);
		} else {
			// unsupported client-side type for skinning; null out so we fall back to GL
			this.vertexBuffer = null;
		}
		EGL10Impl.g3d.async(() -> GL11.glVertexPointer(size, type, stride, MemoryUtil.memAddress(pointer)));
	}

	public synchronized void glVertexPointer(final int size, final int type, final int stride, final int pointerOffset) {
		// VBO-backed vertex pointer; we mark as offset (unsupported for software path unless we read back)
		this.vertexSize = size;
		this.vertexType = type;
		this.vertexStride = stride;
		this.vertexOffset = pointerOffset;
		this.vertexIsOffset = true;
		this.vertexBuffer = null;
		EGL10Impl.g3d.async(() -> GL11.glVertexPointer(size, type, stride, pointerOffset));
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
		// set current palette matrix index (software emulation)
		if (n >= 0 && n < paletteMatrices.length) {
			this.currentPaletteMatrix = n;
		}
	}

	public final synchronized void glLoadPaletteFromModelViewMatrixOES() {
		if (!GLConfiguration.OES_matrix_pallete) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
		// copy current modelview matrix into the current palette matrix (software emulation)
		final FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		EGL10Impl.g3d.sync(() -> GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, fb));
		fb.get(paletteMatrices[currentPaletteMatrix]);
	}

	public final synchronized void glMatrixIndexPointerOES(final int n, final int n2, final int n3, final Buffer buffer) {
		if (!GLConfiguration.OES_matrix_pallete) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
		// store client-side matrix index pointer for software emulation
		this.oesMatrixIndexBuffer = buffer;
		this.oesMatrixIndexSize = n;
		this.oesMatrixIndexType = n2;
		this.oesMatrixIndexStride = n3;
		this.oesMatrixIndexIsOffset = false;
	}

	public final synchronized void glMatrixIndexPointerOES(final int n, final int n2, final int n3, final int n4) {
		if (!GLConfiguration.OES_matrix_pallete) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
		// store VBO offset-based index pointer (we keep offset for later use if needed)
		this.oesMatrixIndexBuffer = null;
		this.oesMatrixIndexSize = n;
		this.oesMatrixIndexType = n2;
		this.oesMatrixIndexStride = n3;
		this.oesMatrixIndexOffset = n4;
		this.oesMatrixIndexIsOffset = true;
	}

	public final synchronized void glWeightPointerOES(final int n, final int n2, final int n3, final Buffer buffer) {
		if (!GLConfiguration.OES_matrix_pallete) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
		this.oesWeightBuffer = buffer;
		this.oesWeightSize = n;
		this.oesWeightType = n2;
		this.oesWeightStride = n3;
		this.oesWeightIsOffset = false;
	}

	public final synchronized void glWeightPointerOES(final int n, final int n2, final int n3, final int n4) {
		if (!GLConfiguration.OES_matrix_pallete) {
			throw new UnsupportedOperationException("OES_matrix_palette extension not available");
		}
		this.oesWeightBuffer = null;
		this.oesWeightSize = n;
		this.oesWeightType = n2;
		this.oesWeightStride = n3;
		this.oesWeightOffset = n4;
		this.oesWeightIsOffset = true;
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
		IntBuffer intBuffer = BufferUtils.createIntBuffer(n);
		intBuffer.put(array, n2, n);
		intBuffer.position(0);
		EGL10Impl.g3d.sync(() -> GL30.glDeleteRenderbuffers(intBuffer));
	}

	public final synchronized void glDeleteRenderbuffersOES(final int n, final IntBuffer intBuffer) {
		checkFramebufferExt();
		int l = intBuffer.limit();
		intBuffer.limit(n + intBuffer.position());
		EGL10Impl.g3d.sync(() -> GL30.glDeleteRenderbuffers(intBuffer));
		intBuffer.limit(l);
	}

	public final synchronized void glGenRenderbuffersOES(final int n, final int[] array, final int n2) {
		checkFramebufferExt();
		IntBuffer intBuffer = BufferUtils.createIntBuffer(n);
		EGL10Impl.g3d.sync(() -> GL30.glGenRenderbuffers(intBuffer));
		intBuffer.get(array, n2, n);
	}

	public final synchronized void glGenRenderbuffersOES(final int n, final IntBuffer intBuffer) {
		checkFramebufferExt();
		int l = intBuffer.limit();
		intBuffer.limit(n + intBuffer.position());
		EGL10Impl.g3d.sync(() -> GL30.glGenRenderbuffers(intBuffer));
		intBuffer.limit(l);
	}

	public final synchronized void glRenderbufferStorageOES(final int n, final int n2, final int n3, final int n4) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> GL30.glRenderbufferStorage(n, n2, n3, n4));
	}

	public final synchronized void glGetRenderbufferParameterivOES(final int n, final int n2, final int[] array, final int n3) {
		checkFramebufferExt();
		int length = 1; // TODO ?
		IntBuffer intBuffer = BufferUtils.createIntBuffer(length);
		EGL10Impl.g3d.sync(() -> GL30.glGetRenderbufferParameteriv(n, n2, intBuffer));
		intBuffer.get(array, n, length);
	}

	public final synchronized void glGetRenderbufferParameterivOES(final int n, final int n2, final IntBuffer intBuffer) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> GL30.glGetRenderbufferParameteriv(n, n2, intBuffer));
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
		IntBuffer intBuffer = BufferUtils.createIntBuffer(n);
		intBuffer.put(array, n2, n);
		intBuffer.position(0);
		EGL10Impl.g3d.sync(() -> GL30.glDeleteFramebuffers(intBuffer));
	}

	public final synchronized void glDeleteFramebuffersOES(final int n, final IntBuffer intBuffer) {
		checkFramebufferExt();
		int l = intBuffer.limit();
		intBuffer.limit(n + intBuffer.position());
		EGL10Impl.g3d.sync(() -> GL30.glDeleteFramebuffers(intBuffer));
		intBuffer.limit(l);
	}

	public final synchronized void glGenFramebuffersOES(final int n, final int[] array, final int n2) {
		checkFramebufferExt();
		IntBuffer intBuffer = BufferUtils.createIntBuffer(n);
		EGL10Impl.g3d.sync(() -> GL30.glGenFramebuffers(intBuffer));
		intBuffer.get(array, n2, n);
	}

	public final synchronized void glGenFramebuffersOES(final int n, final IntBuffer intBuffer) {
		checkFramebufferExt();
		int l = intBuffer.limit();
		intBuffer.limit(n + intBuffer.position());
		EGL10Impl.g3d.sync(() -> GL30.glGenFramebuffers(intBuffer));
		intBuffer.limit(l);
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
		int length = 1; // TODO ?
		IntBuffer intBuffer = BufferUtils.createIntBuffer(length);
		EGL10Impl.g3d.sync(() -> GL30.glGetFramebufferAttachmentParameteriv(n, n2, n3, intBuffer));
		intBuffer.get(array, n, length);
	}

	public final synchronized void glGetFramebufferAttachmentParameterivOES(final int n, final int n2, final int n3, final IntBuffer intBuffer) {
		checkFramebufferExt();
		EGL10Impl.g3d.sync(() -> GL30.glGetFramebufferAttachmentParameteriv(n, n2, n3, intBuffer));
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

	// Helper: read matrix indices (unsigned byte or unsigned short) from index buffer
	private static void readIndices(final Buffer buf, final int vertexIndex, final int indexSize, final int strideBytes, final int type, final int[] out) {
		if (buf == null) return;

		if (type == GL11.GL_UNSIGNED_BYTE) {
			ByteBuffer bb = (ByteBuffer) buf;
			final int stride = (strideBytes == 0) ? indexSize : strideBytes;
			final int pos = vertexIndex * stride;
			for (int i = 0; i < indexSize; i++) {
				out[i] = bb.get(pos + i) & 0xFF;
			}
		} else if (type == GL11.GL_UNSIGNED_SHORT) {
			ShortBuffer sb = (ShortBuffer) buf;
			final int strideElems = (strideBytes == 0) ? indexSize : (strideBytes / 2);
			final int pos = vertexIndex * strideElems;
			for (int i = 0; i < indexSize; i++) {
				out[i] = sb.get(pos + i) & 0xFFFF;
			}
		}
	}

	// Helper: read weights (floats) from weight buffer
	private static void readWeights(final Buffer buf, final int vertexIndex, final int weightSize, final int strideBytes, final int type, final float[] out) {
		if (buf == null) return;

		if (type == GL11.GL_FLOAT) {
			FloatBuffer fb = (FloatBuffer) buf;
			final int strideElems = (strideBytes == 0) ? weightSize : (strideBytes / 4);
			final int pos = vertexIndex * strideElems;
			for (int i = 0; i < weightSize; i++) {
				out[i] = fb.get(pos + i);
			}
		}
	}

	// Multiply 4x4 matrix (array of 16 floats in column-major order) by vec4
	private static void mulMat4Vec4(final float[] m, final float[] v, final float[] out) {
		for (int row = 0; row < 4; row++) {
			out[row] = m[row] * v[0] + m[4 + row] * v[1] + m[8 + row] * v[2] + m[12 + row] * v[3];
		}
	}

	// Perform a software skinning pass when possible before delegating to GL draw calls
	private boolean trySoftwareSkinAndDrawArrays(final int mode, final int first, final int count) {
		if (!this.oesMatrixPaletteEnabled || !this.oesMatrixIndexArrayEnabled || !this.oesWeightArrayEnabled) return false;
		if (this.vertexBuffer == null) return false; // only support client-side float vertex arrays
		if (this.oesMatrixIndexIsOffset || this.oesWeightIsOffset || this.vertexIsOffset) {
			return false; // VBO-backed arrays not supported in this software path
		}

		// Check for supported buffer types
		if (this.vertexType != GL11.GL_FLOAT || this.oesWeightType != GL11.GL_FLOAT) return false;
		if (this.oesMatrixIndexType != GL11.GL_UNSIGNED_BYTE && this.oesMatrixIndexType != GL11.GL_UNSIGNED_SHORT) return false;

		final int vSize = this.vertexSize;
		final FloatBuffer srcV = (FloatBuffer) this.vertexBuffer;

		final FloatBuffer skinned = BufferUtils.createFloatBuffer(count * vSize);

		final int[] indices = new int[this.oesMatrixIndexSize];
		final float[] weights = new float[this.oesWeightSize];
		final float[] orig = {0, 0, 0, 1};
		final float[] tmp = new float[4];
		final float[] accum = new float[4];

		for (int vi = 0; vi < count; vi++) {
			final int vertIndex = first + vi;

			// Clear accumulator for the new vertex
			accum[0] = 0f; accum[1] = 0f; accum[2] = 0f; accum[3] = 0f;

			// Read original vertex position
			final int floatsPerVertex = (this.vertexStride == 0) ? vSize : (this.vertexStride / 4);
			final int srcPos = vertIndex * floatsPerVertex;
			for (int a = 0; a < vSize; a++) {
				orig[a] = srcV.get(srcPos + a);
			}
			orig[3] = (vSize < 4) ? 1.0f : orig[3]; // Ensure w is 1 if not provided

			// Read indices and weights for this vertex
			readIndices(this.oesMatrixIndexBuffer, vertIndex, this.oesMatrixIndexSize, this.oesMatrixIndexStride, this.oesMatrixIndexType, indices);
			readWeights(this.oesWeightBuffer, vertIndex, this.oesWeightSize, this.oesWeightStride, this.oesWeightType, weights);

			// Apply palette matrices
			for (int k = 0; k < this.oesWeightSize; k++) {
				final int mi = indices[k];
				if (mi < 0 || mi >= paletteMatrices.length) continue; // Safety check

				mulMat4Vec4(paletteMatrices[mi], orig, tmp);

				final float w = weights[k];
				accum[0] += tmp[0] * w;
				accum[1] += tmp[1] * w;
				accum[2] += tmp[2] * w;
				accum[3] += tmp[3] * w;
			}

			// Write skinned vertex to the new buffer
			for (int a = 0; a < vSize; a++) {
				skinned.put(accum[a]);
			}
		}
		skinned.position(0);

		// Replace vertex pointer, draw, and then restore
		EGL10Impl.g3d.sync(() -> {
			GL11.glVertexPointer(this.vertexSize, GL11.GL_FLOAT, 0, skinned);
			GL11.glDrawArrays(mode, 0, count);
			// Restore original pointer
			if (this.vertexIsOffset) {
				GL11.glVertexPointer(this.vertexSize, this.vertexType, this.vertexStride, this.vertexOffset);
			} else {
				GL11.glVertexPointer(this.vertexSize, this.vertexType, this.vertexStride, (FloatBuffer)this.vertexBuffer);
			}
		});

		return true;
	}

	private boolean trySoftwareSkinAndDrawElements(final int mode, final int count, final int type, final Buffer indicesBuffer) {
		if (!this.oesMatrixPaletteEnabled || !this.oesMatrixIndexArrayEnabled || !this.oesWeightArrayEnabled) return false;
		if (this.vertexBuffer == null) return false; // only support client-side float vertex arrays
		if (this.oesMatrixIndexIsOffset || this.oesWeightIsOffset || this.vertexIsOffset) {
			return false; // VBO-backed arrays not supported in this software path
		}

		if (this.vertexType != GL11.GL_FLOAT || this.oesWeightType != GL11.GL_FLOAT) return false;
		if (this.oesMatrixIndexType != GL11.GL_UNSIGNED_BYTE && this.oesMatrixIndexType != GL11.GL_UNSIGNED_SHORT) return false;

		// We will create a new, non-indexed vertex buffer by "unrolling" the elements
		final FloatBuffer skinned = BufferUtils.createFloatBuffer(count * this.vertexSize);
		final FloatBuffer srcV = (FloatBuffer) this.vertexBuffer;

		final int[] indices = new int[this.oesMatrixIndexSize];
		final float[] weights = new float[this.oesWeightSize];
		final float[] orig = {0, 0, 0, 1};
		final float[] tmp = new float[4];
		final float[] accum = new float[4];

		indicesBuffer.position(0);

		for (int ei = 0; ei < count; ei++) {
			int vertexIndex;
			if (type == GL11.GL_UNSIGNED_SHORT) {
				vertexIndex = ((ShortBuffer)indicesBuffer).get(ei) & 0xFFFF;
			} else if (type == GL11.GL_UNSIGNED_BYTE) {
				vertexIndex = ((ByteBuffer)indicesBuffer).get(ei) & 0xFF;
			} else {
				return false; // Unsupported index type
			}

			// Clear accumulator for the new vertex
			accum[0] = 0f; accum[1] = 0f; accum[2] = 0f; accum[3] = 0f;

			// Read original vertex
			final int floatsPerVertex = (this.vertexStride == 0) ? this.vertexSize : (this.vertexStride / 4);
			final int srcPos = vertexIndex * floatsPerVertex;
			for (int a = 0; a < this.vertexSize; a++) {
				orig[a] = srcV.get(srcPos + a);
			}
			orig[3] = (this.vertexSize < 4) ? 1.0f : orig[3];

			// Read skinning data for this vertex
			readIndices(this.oesMatrixIndexBuffer, vertexIndex, this.oesMatrixIndexSize, this.oesMatrixIndexStride, this.oesMatrixIndexType, indices);
			readWeights(this.oesWeightBuffer, vertexIndex, this.oesWeightSize, this.oesWeightStride, this.oesWeightType, weights);

			// Apply palette matrices
			for (int k = 0; k < this.oesWeightSize; k++) {
				final int mi = indices[k];
				if (mi < 0 || mi >= paletteMatrices.length) continue;

				mulMat4Vec4(paletteMatrices[mi], orig, tmp);

				final float w = weights[k];
				accum[0] += tmp[0] * w;
				accum[1] += tmp[1] * w;
				accum[2] += tmp[2] * w;
				accum[3] += tmp[3] * w;
			}

			// Write the final skinned vertex
			for (int a = 0; a < this.vertexSize; a++) {
				skinned.put(accum[a]);
			}
		}
		skinned.position(0);

		EGL10Impl.g3d.sync(() -> {
			GL11.glVertexPointer(this.vertexSize, GL11.GL_FLOAT, 0, skinned);
			// Draw as non-indexed arrays since we've "unrolled" the vertices
			GL11.glDrawArrays(mode, 0, count);
			// Restore original pointer
			if (this.vertexIsOffset) {
				GL11.glVertexPointer(this.vertexSize, this.vertexType, this.vertexStride, this.vertexOffset);
			} else if (this.vertexBuffer != null) {
				GL11.glVertexPointer(this.vertexSize, this.vertexType, this.vertexStride, (FloatBuffer)this.vertexBuffer);
			}
		});

		return true;
	}

	// Intercept draw calls to handle software skinning
	public synchronized void glDrawArrays(final int mode, final int first, final int count) {
		if (!trySoftwareSkinAndDrawArrays(mode, first, count)) {
			// Fallback to normal GL draw if not handled by software skinning
			EGL10Impl.g3d.async(() -> GL11.glDrawArrays(mode, first, count));
		}
	}

	public synchronized void glDrawElements(final int mode, final int count, final int type, final Buffer indices) {
		if (!trySoftwareSkinAndDrawElements(mode, count, type, indices)) {
			// Fallback to normal GL draw
			EGL10Impl.g3d.async(() -> GL11.glDrawElements(mode, count, type, MemoryUtil.memAddress(indices)));
		}
	}

	public synchronized void glDrawElements(final int mode, final int count, final int type, final int indicesOffset) {
		// Offset-based elements (VBO index buffer) not supported by the software path
		EGL10Impl.g3d.async(() -> GL11.glDrawElements(mode, count, type, indicesOffset));
	}

	public synchronized void glDisable(final int n) {
		if (n == OES_MATRIX_PALETTE) {
			this.oesMatrixPaletteEnabled = false;
		} else if (n == 2896) { // GL_LIGHTING
			GL10Impl.aBoolean1355 = true;
		} else if (n == 2912) { // GL_FOG
			GL10Impl.aBoolean1358 = true;
		}
		EGL10Impl.g3d.async(() -> GL11.glDisable(n));
	}

	public synchronized void glDisableClientState(final int n) {
		if (n == OES_MATRIX_INDEX_ARRAY) {
			this.oesMatrixIndexArrayEnabled = false;
		} else if (n == OES_WEIGHT_ARRAY) {
			this.oesWeightArrayEnabled = false;
		} else {
			EGL10Impl.g3d.async(() -> GL11.glDisableClientState(n));
		}
	}

	public synchronized void glEnable(final int n) {
		if (n == OES_MATRIX_PALETTE) {
			this.oesMatrixPaletteEnabled = true;
		} else if (n == 2896) { // GL_LIGHTING
			GL10Impl.aBoolean1355 = true;
		} else if (n == 2912) { // GL_FOG
			GL10Impl.aBoolean1358 = true;
		}
		EGL10Impl.g3d.async(() -> GL11.glEnable(n));
	}

	public synchronized void glEnableClientState(final int n) {
		if (n == OES_MATRIX_INDEX_ARRAY) {
			this.oesMatrixIndexArrayEnabled = true;
		} else if (n == OES_WEIGHT_ARRAY) {
			this.oesWeightArrayEnabled = true;
		} else {
			EGL10Impl.g3d.async(() -> GL11.glEnableClientState(n));
		}
	}

	public GL11Impl(final EGLContext eglContext) {
		super(eglContext);
	}
}