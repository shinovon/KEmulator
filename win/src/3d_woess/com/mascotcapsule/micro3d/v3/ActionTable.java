package com.mascotcapsule.micro3d.v3;

import java.io.IOException;
import ru.woesss.micro3d.PlatformHelper;

public class ActionTable {
	int nPointer;

	public ActionTable(byte[] b) {
		if (b == null) {
			throw new NullPointerException();
		}
		this.nPointer = nInit(b);
		if (this.nPointer == 0) {
			throw new RuntimeException();
		}
	}

	public ActionTable(String name) throws IOException {
		if (name == null) {
			throw new NullPointerException();
		}
		byte[] b = PlatformHelper.getResourceBytes(name);
		if (b == null) {
			throw new IOException("Resource \"" + name + "\" not found");
		}
		this.nPointer = nInit(b);
		if (this.nPointer == 0) {
			throw new RuntimeException();
		}
	}

	public final int getNumAction() {
		return getNumActions();
	}

	public final int getNumActions() {
		if (this.nPointer == 0) {
			throw new RuntimeException("already disposed");
		}
		return nGetNumActions();
	}

	public final int getNumFrame(int idx) {
		return getNumFrames(idx);
	}

	public final int getNumFrames(int idx) {
		if (this.nPointer == 0) {
			throw new RuntimeException("already disposed");
		}
		if ((idx < 0) || (idx >= nGetNumActions())) {
			throw new IllegalArgumentException();
		}
		return nGetNumFrames(idx);
	}

	public final void dispose() {
		if (this.nPointer != 0) {
			nFinalize();
			this.nPointer = 0;
		}
	}

	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}

	static {
		System.loadLibrary("java_micro3d_v3_32");
		nInitClass();
	}

	private native int nInit(byte[] paramArrayOfByte);

	private native int nGetNumActions();

	private native int nGetNumFrames(int paramInt);

	private native void nFinalize();

	private static native void nInitClass();
}
