package com.mascotcapsule.micro3d.v3;

import emulator.custom.CustomJarResources;
import emulator.graphics3D.micro3d.a;
import java.io.IOException;

public class Texture {
	boolean isModel;
	a impl;
	private byte[] bytes;

	public Texture(byte[] b, boolean forModel) {
		if (b == null) {
			throw new NullPointerException();
		}
		impl = new a();
		bytes = b;
		isModel = forModel;
		init();
	}

	private void init() {
		
	}

	public Texture(String name, boolean forModel) throws IOException {
		if (name == null) {
			throw new NullPointerException();
		}
		bytes = CustomJarResources.getBytes(CustomJarResources.getResourceStream(name));
		impl = new a();
		isModel = forModel;
		init();
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