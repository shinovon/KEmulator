package com.mascotcapsule.micro3d.v3;

import emulator.custom.CustomJarResources;
import emulator.graphics3D.micro3d.c;
import java.io.IOException;

public class ActionTable {
	c a;

	public ActionTable(byte[] paramArrayOfByte) {
		if (paramArrayOfByte == null) {
			throw new NullPointerException();
		}
		a = new c();
	}

	public ActionTable(String paramString) throws IOException {
		if (paramString == null) {
			throw new NullPointerException();
		}
		CustomJarResources.a(paramString);
		a = new c();
	}

	public final void dispose() {
		a = null;
	}

	public c getImpl() {
		return a;
	}

	public final int getNumAction() {
		return getNumActions();
	}

	public final int getNumActions() {
		if (a == null) {
			return 0;
		}
		return c.a();
	}

	public final int getNumFrame(int paramInt) {
		return getNumFrames(paramInt);
	}

	public final int getNumFrames(int paramInt) {
		if ((paramInt < 0) || (paramInt >= getNumActions())) {
			throw new IllegalArgumentException();
		}
		if (a == null) {
			return 0;
		}
		return c.b();
	}
}