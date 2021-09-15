package com.mascotcapsule.micro3d.v3;

import emulator.custom.CustomJarResources;
import emulator.graphics3D.micro3d.a;
import java.io.IOException;

public class Texture {
	boolean jdField_a_of_type_Boolean;
	a jdField_a_of_type_EmulatorGraphics3DMicro3dA;

	public Texture(byte[] paramArrayOfByte, boolean paramBoolean) {
		if (paramArrayOfByte == null) {
			throw new NullPointerException();
		}
		jdField_a_of_type_EmulatorGraphics3DMicro3dA = new a();
		jdField_a_of_type_Boolean = paramBoolean;
	}

	public Texture(String paramString, boolean paramBoolean) throws IOException {
		if (paramString == null) {
			throw new NullPointerException();
		}
		CustomJarResources.a(paramString);
		jdField_a_of_type_EmulatorGraphics3DMicro3dA = new a();
		jdField_a_of_type_Boolean = paramBoolean;
	}

	public final void dispose() {
		jdField_a_of_type_EmulatorGraphics3DMicro3dA = null;
	}

	protected boolean isDisposed() {
		return jdField_a_of_type_EmulatorGraphics3DMicro3dA == null;
	}

	public a getImpl() {
		return jdField_a_of_type_EmulatorGraphics3DMicro3dA;
	}
}