package com.mascotcapsule.micro3d.v3;

import emulator.custom.CustomJarResources;
import emulator.graphics3D.micro3d.a;
import java.io.IOException;

public class Texture {
	boolean isModel;
	a impl;

	public Texture(byte[] b, boolean forModel) {
		if (b == null) {
			throw new NullPointerException();
		}
		impl = new a();
		isModel = forModel;
	}

	public Texture(String name, boolean forModel) throws IOException {
		if (name == null) {
			throw new NullPointerException();
		}
		CustomJarResources.getResourceStream(name);
		impl = new a();
		isModel = forModel;
	}

	public final void dispose() {
		impl = null;
	}

	protected boolean isDisposed() {
		return impl == null;
	}

	public a getImpl() {
		return impl;
	}
}