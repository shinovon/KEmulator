package emulator.graphics3D.egl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryUtil;

import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL10Ext;
import java.nio.*;
import java.util.Hashtable;

/**
 * GL10
 */
public class GL10Impl implements GL10, GL10Ext {
	static int anInt1354;
	static boolean aBoolean1355;
	static boolean aBoolean1358;
	static int anInt1359;
	EGLContext eglContext;
	public static Hashtable threadToContext;
	public static Hashtable contextToThread;
	public static Hashtable displayThread;
	public static Hashtable readSurfaceThread;
	public static Hashtable drawSurfaceThread;

	final void checkThread() {
		if (Thread.currentThread() != GL10Impl.contextToThread.get(this.eglContext)) {
			throw new IllegalStateException("GL call from improper thread");
		}
	}

	public synchronized void glActiveTexture(final int n) {
		checkThread();
		GL13.glActiveTexture(n);
	}

	public synchronized void glAlphaFunc(final int n, final float n2) {
		checkThread();
		GL11.glAlphaFunc(n, n2);
	}

	public synchronized void glAlphaFuncx(final int n, final int n2) {
		checkThread();
		GL11.glAlphaFunc(n, n2 / 65536.0f);
	}

	public synchronized void glBindTexture(final int n, final int n2) {
		checkThread();
		GL11.glBindTexture(n, n2);
	}

	public synchronized void glBlendFunc(final int n, final int n2) {
		checkThread();
		GL11.glBlendFunc(n, n2);
	}

	public synchronized void glClear(final int n) {
		checkThread();
		GL11.glClear(n);
	}

	public synchronized void glClearColor(final float n, final float n2, final float n3, final float n4) {
		checkThread();
		GL10Impl.anInt1359 = ((int) (n4 * 255.0f) << 24) + ((int) (n * 255.0f) << 16) + ((int) (n2 * 255.0f) << 8) + (int) (n3 * 255.0f);
		GL11.glClearColor(n, n2, n3, n4);
	}

	public synchronized void glClearColorx(final int n, final int n2, final int n3, final int n4) {
		checkThread();
		this.glClearColor(n / 65536.0f, n2 / 65536.0f, n3 / 65536.0f, n4 / 65536.0f);
	}

	public synchronized void glClearDepthf(final float n) {
		checkThread();
		GL11.glClearDepth((double) n);
	}

	public synchronized void glClearDepthx(final int n) {
		checkThread();
		GL11.glClearDepth((double) (n / 65536.0f));
	}

	public synchronized void glClearStencil(final int n) {
		checkThread();
		GL11.glClearStencil(n);
	}

	public synchronized void glClientActiveTexture(final int n) {
		checkThread();
		GL13.glClientActiveTexture(n);
	}

	public synchronized void glColor4f(final float n, final float n2, final float n3, final float n4) {
		checkThread();
		GL11.glColor4f(n, n2, n3, n4);
	}

	public synchronized void glColor4x(final int n, final int n2, final int n3, final int n4) {
		checkThread();
		this.glColor4f(n / 65536.0f, n2 / 65536.0f, n3 / 65536.0f, n4 / 65536.0f);
	}

	public synchronized void glColor4ub(final byte b, final byte b2, final byte b3, final byte b4) {
		checkThread();
		GL11.glColor4ub(b, b2, b3, b4);
	}

	public synchronized void glColorMask(final boolean b, final boolean b2, final boolean b3, final boolean b4) {
		checkThread();
		GL11.glColorMask(b, b2, b3, b4);
	}

	public synchronized void glColorPointer(final int n, final int n2, final int n3, final Buffer buffer) {
		checkThread();
		GL11.glColorPointer(n, n2, n3, MemoryUtil.memAddress(buffer));
//		if (n2 == 5121) {
//			GL11.glColorPointer(n, n2, n3, (ByteBuffer) buffer);
//			return;
//		}
//		int n4;
//		int n5;
//		FloatBuffer floatBuffer;
//		if (n2 == 5126) {
//			n4 = n;
//			n5 = n3;
//			floatBuffer = (FloatBuffer) buffer;
//		} else {
//			if (n2 != 5132) {
//				return;
//			}
//			final IntBuffer intBuffer;
//			final int remaining = (intBuffer = (IntBuffer) buffer).remaining();
//			final int position = intBuffer.position();
//			final FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(remaining);
//			for (int i = 0; i < remaining; ++i) {
//				floatBuffer2.put(intBuffer.get(position + i) / 65536.0f);
//			}
//			floatBuffer2.position(0);
//			n4 = n;
//			n5 = n3;
//			floatBuffer = floatBuffer2;
//		}
//		GL11.glColorPointer(n4, GL_FLOAT, n5, floatBuffer);
	}

	public synchronized void glCompressedTexImage2D(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final Buffer buffer) {
		checkThread();
		GL13.glCompressedTexImage2D(n, n2, n3, n4, n5, n6, n7, MemoryUtil.memAddress(buffer));
	}

	public synchronized void glCompressedTexSubImage2D(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final Buffer buffer) {
		checkThread();
		GL13.glCompressedTexSubImage2D(n, n2, n3, n4, n5, n6, n7, n8, MemoryUtil.memAddress(buffer));
	}

	public synchronized void glCopyTexImage2D(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
		checkThread();
		GL11.glCopyTexImage2D(n, n2, n3, n4, n5, n6, n7, n8);
	}

	public synchronized void glCopyTexSubImage2D(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
		checkThread();
		GL11.glCopyTexSubImage2D(n, n2, n3, n4, n5, n6, n7, n8);
	}

	public synchronized void glCullFace(final int n) {
		checkThread();
		GL11.glCullFace(n);
	}

	public synchronized void glDeleteTextures(final int n, final int[] array, final int n2) {
		checkThread();
		final IntBuffer intBuffer;
		(intBuffer = BufferUtils.createIntBuffer(n)).put(array, n2, n);
		intBuffer.position(0);
		GL11.glDeleteTextures(intBuffer);
	}

	public synchronized void glDeleteTextures(final int n, final IntBuffer intBuffer) {
		checkThread();
		GL11.glDeleteTextures(intBuffer);
	}

	public synchronized void glDepthFunc(final int n) {
		checkThread();
		GL11.glDepthFunc(n);
	}

	public synchronized void glDepthMask(final boolean b) {
		checkThread();
		GL11.glDepthMask(b);
	}

	public synchronized void glDepthRangef(final float n, final float n2) {
		checkThread();
		GL11.glDepthRange((double) n, (double) n2);
	}

	public synchronized void glDepthRangex(final int n, final int n2) {
		checkThread();
		GL11.glDepthRange((double) (n / 65536.0f), (double) (n2 / 65536.0f));
	}

	public synchronized void glDisable(final int n) {
		checkThread();
		if (n == 2896) {
			GL10Impl.aBoolean1355 = true;
		} else if (n == 2912) {
			GL10Impl.aBoolean1358 = true;
		}
		GL11.glDisable(n);
	}

	public synchronized void glDisableClientState(final int n) {
		checkThread();
		GL11.glDisableClientState(n);
	}

	public synchronized void glDrawArrays(final int n, final int n2, final int n3) {
		checkThread();
		GL11.glDrawArrays(n, n2, n3);
	}

	public synchronized void glDrawElements(final int n, final int n2, final int n3, final Buffer buffer) {
		checkThread();
		if (n3 == 5121) {
			GL11.glDrawElements(n, (ByteBuffer) buffer);
			return;
		}
		if (n3 == 5123) {
			GL11.glDrawElements(n, (ShortBuffer) buffer);
			return;
		}
		GL11.glDrawElements(n, n2, n3, MemoryUtil.memAddress(buffer));
	}

	public synchronized void glEnable(final int n) {
		checkThread();
		if (n == 2896) {
			GL10Impl.aBoolean1355 = true;
		} else if (n == 2912) {
			GL10Impl.aBoolean1358 = true;
		}
		GL11.glEnable(n);
	}

	public synchronized void glEnableClientState(final int n) {
		checkThread();
		GL11.glEnableClientState(n);
	}

	public synchronized void glFinish() {
		checkThread();
		GL11.glFinish();
	}

	public synchronized void glFlush() {
		checkThread();
		GL11.glFlush();
	}

	public synchronized void glFogf(final int n, final float n2) {
		checkThread();
		GL11.glFogf(n, n2);
	}

	public synchronized void glFogfv(final int n, final float[] array, final int n2) {
		checkThread();
		final int method768;
		final FloatBuffer floatBuffer;
		(floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n))).put(array, n2, method768);
		floatBuffer.position(0);
		GL11.glFogfv(n, floatBuffer);
	}

	public synchronized void glFogfv(final int n, final FloatBuffer floatBuffer) {
		checkThread();
		GL11.glFogfv(n, floatBuffer);
	}

	public synchronized void glFogx(final int n, final int n2) {
		checkThread();
		GL11.glFogi(n, n2);
	}

	public synchronized void glFogxv(final int n, final int[] array, final int n2) {
		checkThread();
		final int method768;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768 = GLConfiguration.method768(n));
		for (int i = 0; i < method768; ++i) {
			floatBuffer.put(array[n2 + i] / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glFogfv(n, floatBuffer);
	}

	public synchronized void glFogxv(final int n, final IntBuffer intBuffer) {
		checkThread();
		final int method768 = GLConfiguration.method768(n);
		final int position = intBuffer.position();
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768);
		for (int i = 0; i < method768; ++i) {
			floatBuffer.put(intBuffer.get(position + i) / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glFogfv(n, floatBuffer);
	}

	public synchronized void glFrontFace(final int n) {
		checkThread();
		GL11.glFrontFace(n);
	}

	public synchronized void glFrustumf(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
		checkThread();
		GL11.glFrustum((double) n, (double) n2, (double) n3, (double) n4, (double) n5, (double) n6);
	}

	public synchronized void glFrustumx(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
		checkThread();
		this.glFrustumf(n / 65536.0f, n2 / 65536.0f, n3 / 65536.0f, n4 / 65536.0f, n5 / 65536.0f, n6 / 65536.0f);
	}

	public synchronized void glGenTextures(final int n, final int[] array, final int n2) {
		checkThread();
		final IntBuffer intBuffer;
		GL11.glGenTextures(intBuffer = BufferUtils.createIntBuffer(n));
		intBuffer.get(array, n2, n);
	}

	public synchronized void glGenTextures(final int n, final IntBuffer intBuffer) {
		checkThread();
		GL11.glGenTextures(intBuffer);
	}

	public synchronized void glGetBooleanv(final int n, final boolean[] array, final int n2) {
		checkThread();
		final int method768 = GLConfiguration.method768(n);
		final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(16);
		GL11.glGetBooleanv(n, byteBuffer);
		for (int i = 0; i < method768; ++i) {
			array[n2 + i] = (byteBuffer.get(i) == 1);
		}
	}

	public synchronized void glGetBooleanv(final int n, final IntBuffer intBuffer) {
		checkThread();
		final int method768 = GLConfiguration.method768(n);
		final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(16);
		GL11.glGetBooleanv(n, byteBuffer);
		for (int i = 0; i < method768; ++i) {
			intBuffer.put(byteBuffer.get(i));
		}
	}

	public synchronized void glGetIntegerv(final int n, final int[] array, final int n2) {
		checkThread();
		final int method768 = GLConfiguration.method768(n);
		final IntBuffer intBuffer = BufferUtils.createIntBuffer(16);
		GL11.glGetIntegerv(n, intBuffer);
		intBuffer.get(array, n2, method768);
	}

	public synchronized void glGetIntegerv(final int n, final IntBuffer intBuffer) {
		checkThread();
		final int method768 = GLConfiguration.method768(n);
		final IntBuffer intBuffer2 = BufferUtils.createIntBuffer(16);
		GL11.glGetIntegerv(n, intBuffer2);
		for (int i = 0; i < method768; ++i) {
			intBuffer.put(intBuffer2.get(i));
		}
	}

	public synchronized int glGetError() {
		checkThread();
		return GL11.glGetError();
	}

	public synchronized String glGetString(final int n) {
		checkThread();
		return GL11.glGetString(n);
	}

	public synchronized void glHint(final int n, final int n2) {
		checkThread();
		GL11.glHint(n, n2);
	}

	public synchronized void glLightModelf(final int n, final float n2) {
		checkThread();
		GL11.glLightModelf(n, n2);
	}

	public synchronized void glLightModelfv(final int n, final float[] array, final int n2) {
		checkThread();
		final int method772;
		final FloatBuffer floatBuffer;
		(floatBuffer = BufferUtils.createFloatBuffer(method772 = GLConfiguration.method772(n))).put(array, n2, method772);
		floatBuffer.position(0);
		GL11.glLightModelfv(n, floatBuffer);
	}

	public synchronized void glLightModelfv(final int n, final FloatBuffer floatBuffer) {
		checkThread();
		GL11.glLightModelfv(n, floatBuffer);
	}

	public synchronized void glLightModelx(final int n, final int n2) {
		checkThread();
		GL11.glLightModelf(n, n2 / 65536.0f);
	}

	public synchronized void glLightModelxv(final int n, final int[] array, final int n2) {
		checkThread();
		final int method772;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method772 = GLConfiguration.method772(n));
		for (int i = 0; i < method772; ++i) {
			floatBuffer.put(array[n2 + i] / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glLightModelfv(n, floatBuffer);
	}

	public synchronized void glLightModelxv(final int n, final IntBuffer intBuffer) {
		checkThread();
		final int method768 = GLConfiguration.method768(n);
		final int position = intBuffer.position();
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768);
		for (int i = 0; i < method768; ++i) {
			floatBuffer.put(intBuffer.get(position + i) / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glLightModelfv(n, floatBuffer);
	}

	public synchronized void glLightf(final int n, final int n2, final float n3) {
		checkThread();
		GL11.glLightf(n, n2, n3);
	}

	public synchronized void glLightfv(final int n, final int n2, final float[] array, final int n3) {
		checkThread();
		final int method770 = GLConfiguration.method770(n2);
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(4);
		floatBuffer.put(array, n3, method770);
		floatBuffer.position(0);
		GL11.glLightfv(n, n2, floatBuffer);
	}

	public synchronized void glLightfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		checkThread();
		GL11.glLightfv(n, n2, floatBuffer);
	}

	public synchronized void glLightx(final int n, final int n2, final int n3) {
		checkThread();
		GL11.glLightf(n, n2, n3 / 65536.0f);
	}

	public synchronized void glLightxv(final int n, final int n2, final int[] array, final int n3) {
		checkThread();
		final int method770;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method770 = GLConfiguration.method770(n2));
		for (int i = 0; i < method770; ++i) {
			floatBuffer.put(array[n3 + i] / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glLightfv(n, n2, floatBuffer);
	}

	public synchronized void glLightxv(final int n, final int n2, final IntBuffer intBuffer) {
		checkThread();
		final int method768 = GLConfiguration.method768(n2);
		final int position = intBuffer.position();
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768);
		for (int i = 0; i < method768; ++i) {
			floatBuffer.put(intBuffer.get(position + i) / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glLightfv(n, n2, floatBuffer);
	}

	public synchronized void glLineWidth(final float n) {
		checkThread();
		GL11.glLineWidth(n);
	}

	public synchronized void glLineWidthx(final int n) {
		checkThread();
		GL11.glLineWidth(n / 65536.0f);
	}

	public synchronized void glLoadIdentity() {
		checkThread();
		GL11.glLoadIdentity();
	}

	public synchronized void glLoadMatrixf(final float[] array, final int n) {
		checkThread();
		final FloatBuffer floatBuffer;
		(floatBuffer = BufferUtils.createFloatBuffer(16)).put(array, n, 16);
		floatBuffer.position(0);
		GL11.glLoadMatrixf(floatBuffer);
	}

	public synchronized void glLoadMatrixf(final FloatBuffer floatBuffer) {
		checkThread();
		GL11.glLoadMatrixf(floatBuffer);
	}

	public synchronized void glLoadMatrixx(final int[] array, final int n) {
		checkThread();
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		for (int i = 0; i < 16; ++i) {
			floatBuffer.put(array[n + i] / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glLoadMatrixf(floatBuffer);
	}

	public synchronized void glLoadMatrixx(final IntBuffer intBuffer) {
		checkThread();
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		for (int i = 0; i < 16; ++i) {
			floatBuffer.put(intBuffer.get() / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glLoadMatrixf(floatBuffer);
	}

	public synchronized void glLogicOp(final int n) {
		checkThread();
		GL11.glLogicOp(n);
	}

	public synchronized void glMaterialf(final int n, final int n2, final float n3) {
		checkThread();
		GL11.glMaterialf(n, n2, n3);
	}

	public synchronized void glMaterialfv(final int n, final int n2, final float[] array, final int n3) {
		checkThread();
		final int method773;
		final FloatBuffer floatBuffer;
		(floatBuffer = BufferUtils.createFloatBuffer(method773 = GLConfiguration.method773(n2))).put(array, n3, method773);
		floatBuffer.position(0);
		GL11.glMaterialfv(n, n2, floatBuffer);
	}

	public synchronized void glMaterialfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		checkThread();
		GL11.glMaterialfv(n, n2, floatBuffer);
	}

	public synchronized void glMaterialx(final int n, final int n2, final int n3) {
		checkThread();
		GL11.glMaterialf(n, n2, n3 / 65536.0f);
	}

	public synchronized void glMaterialxv(final int n, final int n2, final int[] array, final int n3) {
		checkThread();
		final int method773;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method773 = GLConfiguration.method773(n2));
		for (int i = 0; i < method773; ++i) {
			floatBuffer.put(array[n3 + i] / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glMaterialfv(n, n2, floatBuffer);
	}

	public synchronized void glMaterialxv(final int n, final int n2, final IntBuffer intBuffer) {
		checkThread();
		final int method768 = GLConfiguration.method768(n2);
		final int position = intBuffer.position();
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method768);
		for (int i = 0; i < method768; ++i) {
			floatBuffer.put(intBuffer.get(position + i) / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glMaterialfv(n, n2, floatBuffer);
	}

	public synchronized void glMatrixMode(final int anInt1354) {
		checkThread();
		GL11.glMatrixMode(GL10Impl.anInt1354 = anInt1354);
	}

	public synchronized void glMultMatrixf(final float[] array, final int n) {
		checkThread();
		final FloatBuffer floatBuffer;
		(floatBuffer = BufferUtils.createFloatBuffer(16)).put(array, n, 16);
		floatBuffer.position(0);
		GL11.glMultMatrixf(floatBuffer);
	}

	public synchronized void glMultMatrixf(final FloatBuffer floatBuffer) {
		checkThread();
		GL11.glMultMatrixf(floatBuffer);
	}

	public synchronized void glMultMatrixx(final int[] array, final int n) {
		checkThread();
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		for (int i = 0; i < 16; ++i) {
			floatBuffer.put(array[i] / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glMultMatrixf(floatBuffer);
	}

	public synchronized void glMultMatrixx(final IntBuffer intBuffer) {
		checkThread();
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		for (int i = 0; i < 16; ++i) {
			floatBuffer.put(intBuffer.get() / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glMultMatrixf(floatBuffer);
	}

	public synchronized void glMultiTexCoord4f(final int n, final float n2, final float n3, final float n4, final float n5) {
		checkThread();
		GL13.glMultiTexCoord4f(n, n2, n3, n4, n5);
	}

	public synchronized void glMultiTexCoord4x(final int n, final int n2, final int n3, final int n4, final int n5) {
		checkThread();
		this.glMultiTexCoord4f(n, n2 / 65536.0f, n3 / 65536.0f, n4 / 65536.0f, n5 / 65536.0f);
	}

	public synchronized void glNormal3f(final float n, final float n2, final float n3) {
		checkThread();
		GL11.glNormal3f(n, n2, n3);
	}

	public synchronized void glNormal3x(final int n, final int n2, final int n3) {
		checkThread();
		GL11.glNormal3f(n / 65536.0f, n2 / 65536.0f, n3 / 65536.0f);
	}

	public synchronized void glNormalPointer(final int n, final int n2, final Buffer buffer) {
		checkThread();
		GL11.glNormalPointer(n, n2, MemoryUtil.memAddress(buffer));
//		if (n == 5120) {
//			GL11.glNormalPointer(n, n2, (ByteBuffer) buffer);
//			return;
//		}
//		if (n == 5122) {
//			GL11.glNormalPointer(n, n2, (ShortBuffer) buffer);
//			return;
//		}
//		int n3;
//		FloatBuffer floatBuffer;
//		if (n == 5126) {
//			n3 = n2;
//			floatBuffer = (FloatBuffer) buffer;
//		} else {
//			if (n != 5132) {
//				return;
//			}
//			final IntBuffer intBuffer;
//			final int remaining = (intBuffer = (IntBuffer) buffer).remaining();
//			final int position = intBuffer.position();
//			final FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(remaining);
//			for (int i = 0; i < remaining; ++i) {
//				floatBuffer2.put(intBuffer.get(position + i) / 65536.0f);
//			}
//			floatBuffer2.position(0);
//			n3 = n2;
//			floatBuffer = floatBuffer2;
//		}
//		GL11.glNormalPointer(GL_FLOAT, n3, floatBuffer);
	}

	public synchronized void glOrthof(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
		checkThread();
		GL11.glOrtho((double) n, (double) n2, (double) n3, (double) n4, (double) n5, (double) n6);
	}

	public synchronized void glOrthox(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
		checkThread();
		this.glOrthof(n / 65536.0f, n2 / 65536.0f, n3 / 65536.0f, n4 / 65536.0f, n5 / 65536.0f, n6 / 65536.0f);
	}

	public synchronized void glPixelStorei(final int n, final int n2) {
		checkThread();
		GL11.glPixelStorei(n, n2);
	}

	public synchronized void glPointParameterf(final int n, final float n2) {
		checkThread();
		GL14.glPointParameterf(n, n2);
	}

	public synchronized void glPointParameterfv(final int n, final float[] array, final int n2) {
		checkThread();
		final int method774;
		final FloatBuffer floatBuffer;
		(floatBuffer = BufferUtils.createFloatBuffer(method774 = GLConfiguration.method774(n))).put(array, n2, method774);
		floatBuffer.position(0);
		GL14.glPointParameterfv(n, floatBuffer);
	}

	public synchronized void glPointParameterfv(final int n, final FloatBuffer floatBuffer) {
		checkThread();
		GL14.glPointParameterfv(n, floatBuffer);
	}

	public synchronized void glPointParameterx(final int n, final int n2) {
		checkThread();
		GL14.glPointParameteri(n, n2);
	}

	public synchronized void glPointParameterxv(final int n, final int[] array, final int n2) {
		checkThread();
		final int method774;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method774 = GLConfiguration.method774(n));
		for (int i = 0; i < method774; ++i) {
			floatBuffer.put(array[n2 + i] / 65536.0f);
		}
		floatBuffer.position(0);
		GL14.glPointParameterfv(n, floatBuffer);
	}

	public synchronized void glPointParameterxv(final int n, final IntBuffer intBuffer) {
		checkThread();
		final int method774 = GLConfiguration.method774(n);
		final int position = intBuffer.position();
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method774);
		for (int i = 0; i < method774; ++i) {
			floatBuffer.put(intBuffer.get(position + i) / 65536.0f);
		}
		floatBuffer.position(0);
		GL14.glPointParameterfv(n, floatBuffer);
	}

	public synchronized void glPointSize(final float n) {
		checkThread();
		GL11.glPointSize(n);
	}

	public synchronized void glPointSizex(final int n) {
		checkThread();
		GL11.glPointSize(n / 65536.0f);
	}

	public synchronized void glPolygonOffset(final float n, final float n2) {
		checkThread();
		GL11.glPolygonOffset(n, n2);
	}

	public synchronized void glPolygonOffsetx(final int n, final int n2) {
		checkThread();
		GL11.glPolygonOffset(n / 65536.0f, n2 / 65536.0f);
	}

	public synchronized void glPopMatrix() {
		checkThread();
		GL11.glPopMatrix();
	}

	public synchronized void glPushMatrix() {
		checkThread();
		GL11.glPushMatrix();
	}

	public synchronized void glReadPixels(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Buffer buffer) {
		checkThread();
		if (buffer instanceof ByteBuffer) {
			GL11.glReadPixels(n, n2, n3, n4, n5, n6, (ByteBuffer) buffer);
			return;
		}
		if (buffer instanceof ShortBuffer) {
			GL11.glReadPixels(n, n2, n3, n4, n5, n6, (ShortBuffer) buffer);
			return;
		}
		if (buffer instanceof IntBuffer) {
			GL11.glReadPixels(n, n2, n3, n4, n5, n6, (IntBuffer) buffer);
			return;
		}
		if (buffer instanceof FloatBuffer) {
			GL11.glReadPixels(n, n2, n3, n4, n5, n6, (FloatBuffer) buffer);
		}
	}

	public synchronized void glRotatef(final float n, final float n2, final float n3, final float n4) {
		checkThread();
		GL11.glRotatef(n, n2, n3, n4);
	}

	public synchronized void glRotatex(final int n, final int n2, final int n3, final int n4) {
		checkThread();
		GL11.glRotatef(n / 65536.0f, n2 / 65536.0f, n3 / 65536.0f, n4 / 65536.0f);
	}

	public synchronized void glSampleCoverage(final float n, final boolean b) {
		checkThread();
		GL13.glSampleCoverage(n, b);
	}

	public synchronized void glSampleCoveragex(final int n, final boolean b) {
		checkThread();
		GL13.glSampleCoverage(n / 65536.0f, b);
	}

	public synchronized void glScalef(final float n, final float n2, final float n3) {
		checkThread();
		GL11.glScalef(n, n2, n3);
	}

	public synchronized void glScalex(final int n, final int n2, final int n3) {
		checkThread();
		GL11.glScalef(n / 65536.0f, n2 / 65536.0f, n3 / 65536.0f);
	}

	public synchronized void glScissor(final int n, final int n2, final int n3, final int n4) {
		checkThread();
		GL11.glScissor(n, n2, n3, n4);
	}

	public synchronized void glShadeModel(final int n) {
		checkThread();
		GL11.glShadeModel(n);
	}

	public synchronized void glStencilFunc(final int n, final int n2, final int n3) {
		checkThread();
		GL11.glStencilFunc(n, n2, n3);
	}

	public synchronized void glStencilMask(final int n) {
		checkThread();
		GL11.glStencilMask(n);
	}

	public synchronized void glStencilOp(final int n, final int n2, final int n3) {
		checkThread();
		GL11.glStencilOp(n, n2, n3);
	}

	public synchronized void glTexCoordPointer(final int n, final int n2, final int n3, final Buffer buffer) {
		checkThread();

		GL11.glTexCoordPointer(n, n2, n3, MemoryUtil.memAddress(buffer));

//		if (n2 == 5126) {
//			final FloatBuffer floatBuffer;
//			final int remaining = (floatBuffer = (FloatBuffer) buffer).remaining();
//			final int position = floatBuffer.position();
//			final DoubleBuffer doubleBuffer = BufferUtils.createDoubleBuffer(remaining);
//			for (int i = 0; i < remaining; ++i) {
//				doubleBuffer.put(floatBuffer.get(position + i));
//			}
//			doubleBuffer.rewind();
//			GL11.glTexCoordPointer(n, GL_DOUBLE, n3, MemoryUtil.memAddress(doubleBuffer));
////			GL11.glTexCoordPointer(n, n2, n3, (FloatBuffer) buffer);
//			return;
//		}
//		if (n2 == 5122) {
//			GL11.glTexCoordPointer(n, n2, n3, (ShortBuffer) buffer);
//			return;
//		}
//		FloatBuffer floatBuffer2;
//		if (n2 == 5120) {
//			final ByteBuffer byteBuffer;
//			final int remaining2 = (byteBuffer = (ByteBuffer) buffer).remaining();
//			final int position2 = byteBuffer.position();
//			floatBuffer2 = BufferUtils.createFloatBuffer(remaining2);
//			for (int j = 0; j < remaining2; ++j) {
//				floatBuffer2.put(byteBuffer.get(position2 + j));
//			}
//			floatBuffer2.rewind();
//		} else {
//			if (n2 != 5132) {
//				return;
//			}
//			final IntBuffer intBuffer;
//			final int remaining3 = (intBuffer = (IntBuffer) buffer).remaining();
//			final int position3 = intBuffer.position();
//			floatBuffer2 = BufferUtils.createFloatBuffer(remaining3);
//			for (int k = 0; k < remaining3; ++k) {
//				floatBuffer2.put(intBuffer.get(position3 + k) / 65536.0f);
//			}
//			floatBuffer2.position(0);
//		}
//		GL11.glTexCoordPointer(n, GL_FLOAT, n3, floatBuffer2);
	}

	public synchronized void glTexEnvi(final int n, final int n2, final int n3) {
		checkThread();
		GL11.glTexEnvi(n, n2, n3);
	}

	public synchronized void glTexEnviv(final int n, final int n2, final int[] array, final int n3) {
		checkThread();
		final int method775;
		final IntBuffer intBuffer;
		(intBuffer = BufferUtils.createIntBuffer(method775 = GLConfiguration.method775(n2))).put(array, n3, method775);
		intBuffer.position(0);
		GL11.glTexEnviv(n, n2, intBuffer);
	}

	public synchronized void glTexEnviv(final int n, final int n2, final IntBuffer intBuffer) {
		checkThread();
		GL11.glTexEnviv(n, n2, intBuffer);
	}

	public synchronized void glTexEnvf(final int n, final int n2, final float n3) {
		checkThread();
		GL11.glTexEnvf(n, n2, n3);
	}

	public synchronized void glTexEnvfv(final int n, final int n2, final float[] array, final int n3) {
		checkThread();
		final int method775;
		final FloatBuffer floatBuffer;
		(floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2))).put(array, n3, method775);
		floatBuffer.position(0);
		GL11.glTexEnvfv(n, n2, floatBuffer);
	}

	public synchronized void glTexEnvfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		checkThread();
		GL11.glTexEnvfv(n, n2, floatBuffer);
	}

	public synchronized void glTexEnvx(final int n, final int n2, final int n3) {
		checkThread();
		this.glTexEnvi(n, n2, n3);
	}

	public synchronized void glTexEnvxv(final int n, final int n2, final int[] array, final int n3) {
		checkThread();
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		for (int i = 0; i < method775; ++i) {
			floatBuffer.put(array[n3 + i] / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glTexEnvfv(n, n2, floatBuffer);
	}

	public synchronized void glTexEnvxv(final int n, final int n2, final IntBuffer intBuffer) {
		checkThread();
		final int method775;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method775 = GLConfiguration.method775(n2));
		for (int i = 0; i < method775; ++i) {
			floatBuffer.put(intBuffer.get() / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glTexEnvfv(n, n2, floatBuffer);
	}

	public synchronized void glTexImage2D(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final Buffer buffer) {
		checkThread();
		if (buffer instanceof ByteBuffer) {
			GL11.glTexImage2D(n, n2, n3, n4, n5, n6, n7, n8, (ByteBuffer) buffer);
			return;
		}
		if (buffer instanceof ShortBuffer) {
			GL11.glTexImage2D(n, n2, n3, n4, n5, n6, n7, n8, (ShortBuffer) buffer);
			return;
		}
		if (buffer instanceof IntBuffer) {
			GL11.glTexImage2D(n, n2, n3, n4, n5, n6, n7, n8, (IntBuffer) buffer);
		}
	}

	public synchronized void glTexParameterf(final int n, final int n2, final float n3) {
		checkThread();
		GL11.glTexParameterf(n, n2, n3);
	}

	public synchronized void glTexParameterx(final int n, final int n2, final int n3) {
		checkThread();
		this.glTexParameterf(n, n2, n3);
	}

	public synchronized void glTexParameterfv(final int n, final int n2, final float[] array, final int n3) {
		checkThread();
		final int method776;
		final FloatBuffer floatBuffer;
		(floatBuffer = BufferUtils.createFloatBuffer(method776 = GLConfiguration.method776(n2))).put(array, n3, method776);
		floatBuffer.position(0);
		GL11.glTexParameterfv(n, n2, floatBuffer);
	}

	public synchronized void glTexParameterfv(final int n, final int n2, final FloatBuffer floatBuffer) {
		checkThread();
		GL11.glTexParameterfv(n, n2, floatBuffer);
	}

	public synchronized void glTexParameteri(final int n, final int n2, final int n3) {
		checkThread();
		GL11.glTexParameteri(n, n2, n3);
	}

	public synchronized void glTexParameteriv(final int n, final int n2, final int[] array, final int n3) {
		checkThread();
		final int method776;
		final IntBuffer intBuffer;
		(intBuffer = BufferUtils.createIntBuffer(method776 = GLConfiguration.method776(n2))).put(array, n3, method776);
		intBuffer.position(0);
		GL11.glTexParameteriv(n, n2, intBuffer);
	}

	public synchronized void glTexParameteriv(final int n, final int n2, final IntBuffer intBuffer) {
		checkThread();
		GL11.glTexParameteriv(n, n2, intBuffer);
	}

	public synchronized void glTexParameterxv(final int n, final int n2, final int[] array, final int n3) {
		checkThread();
		final int method776;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method776 = GLConfiguration.method776(n2));
		for (int i = 0; i < method776; ++i) {
			floatBuffer.put(array[n3 + i] / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glTexParameterfv(n, n2, floatBuffer);
	}

	public synchronized void glTexParameterxv(final int n, final int n2, final IntBuffer intBuffer) {
		checkThread();
		final int method776;
		final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(method776 = GLConfiguration.method776(n2));
		for (int i = 0; i < method776; ++i) {
			floatBuffer.put(intBuffer.get() / 65536.0f);
		}
		floatBuffer.position(0);
		GL11.glTexParameterfv(n, n2, floatBuffer);
	}

	public synchronized void glTexSubImage2D(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final Buffer buffer) {
		checkThread();
		if (buffer instanceof ByteBuffer) {
			GL11.glTexSubImage2D(n, n2, n3, n4, n5, n6, n7, n8, (ByteBuffer) buffer);
			return;
		}
		if (buffer instanceof ShortBuffer) {
			GL11.glTexSubImage2D(n, n2, n3, n4, n5, n6, n7, n8, (ShortBuffer) buffer);
			return;
		}
		if (buffer instanceof IntBuffer) {
			GL11.glTexSubImage2D(n, n2, n3, n4, n5, n6, n7, n8, (IntBuffer) buffer);
		}
	}

	public synchronized void glTranslatef(final float n, final float n2, final float n3) {
		checkThread();
		GL11.glTranslatef(n, n2, n3);
	}

	public synchronized void glTranslatex(final int n, final int n2, final int n3) {
		checkThread();
		GL11.glTranslatef(n / 65536.0f, n2 / 65536.0f, n3 / 65536.0f);
	}

	public synchronized void glVertexPointer(final int n, final int n2, final int n3, final Buffer buffer) {
		checkThread();
		GL11.glVertexPointer(n, n2, n3, MemoryUtil.memAddress(buffer));
//		if (n2 == 5120) {
//			final ByteBuffer byteBuffer;
//			final int remaining = (byteBuffer = (ByteBuffer) buffer).remaining();
//			final int position = byteBuffer.position();
//			final ShortBuffer shortBuffer = BufferUtils.createShortBuffer(remaining);
//			for (int i = 0; i < remaining; ++i) {
//				shortBuffer.put(i, byteBuffer.get(position + i));
//			}
//			GL11.glVertexPointer(n, GL_SHORT, n3, shortBuffer);
//			return;
//		}
//		if (n2 == 5122) {
//			GL11.glVertexPointer(n, GL_SHORT, n3, (ShortBuffer) buffer);
//			return;
//		}
//		int n4;
//		int n5;
//		FloatBuffer floatBuffer;
//		if (n2 == 5126) {
//			n4 = n;
//			n5 = n3;
//			floatBuffer = (FloatBuffer) buffer;
//		} else {
//			if (n2 != 5132) {
//				return;
//			}
//			final IntBuffer intBuffer;
//			final int remaining2 = (intBuffer = (IntBuffer) buffer).remaining();
//			final int position2 = intBuffer.position();
//			final FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(remaining2);
//			for (int j = 0; j < remaining2; ++j) {
//				floatBuffer2.put(intBuffer.get(position2 + j) / 65536.0f);
//			}
//			floatBuffer2.position(0);
//			n4 = n;
//			n5 = n3;
//			floatBuffer = floatBuffer2;
//		}
//		GL11.glVertexPointer(n4, GL_FLOAT, n5, floatBuffer);
	}

	public synchronized void glViewport(final int n, final int n2, final int n3, final int n4) {
		checkThread();
		GL11.glViewport(n, n2, n3, n4);
	}

	public synchronized int glQueryMatrixxOES(final int[] array, final int n, final int[] array2, final int n2) {
		checkThread();
		System.out.println("GL10Ext.glQueryMatrixxOES is not implemented.");
		return 0;
	}

	public GL10Impl(final EGLContext anEGLContext1356) {
		super();
		this.eglContext = null;
		this.eglContext = anEGLContext1356;
	}

	static {
		EGLContext.getEGL();
		GL10Impl.threadToContext = new Hashtable();
		GL10Impl.contextToThread = new Hashtable();
		GL10Impl.displayThread = new Hashtable();
		GL10Impl.readSurfaceThread = new Hashtable();
		GL10Impl.drawSurfaceThread = new Hashtable();
	}
}
