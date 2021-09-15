package com.mascotcapsule.micro3d.v3;

import java.io.IOException;

public class Figure {
	emulator.graphics3D.micro3d.b jdField_a_of_type_EmulatorGraphics3DMicro3dB;
	private Texture[] jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3Texture;
	private Texture jdField_a_of_type_ComMascotcapsuleMicro3dV3Texture;

	public Figure(byte[] paramArrayOfByte) {
		if (paramArrayOfByte == null) {
			throw new NullPointerException();
		}
		jdField_a_of_type_EmulatorGraphics3DMicro3dB = new emulator.graphics3D.micro3d.b();
	}

	public Figure(String paramString) throws IOException {
		if (paramString == null) {
			throw new NullPointerException();
		}
		emulator.custom.CustomJarResources.a(paramString);
		jdField_a_of_type_EmulatorGraphics3DMicro3dB = new emulator.graphics3D.micro3d.b();
	}

	public final void dispose() {
		jdField_a_of_type_EmulatorGraphics3DMicro3dB = null;
	}

	public emulator.graphics3D.micro3d.b getImpl() {
		return jdField_a_of_type_EmulatorGraphics3DMicro3dB;
	}

	public final void setPosture(ActionTable paramActionTable, int paramInt1, int paramInt2) {
		if ((paramInt1 < 0) || (paramActionTable == null)) {
			throw new NullPointerException();
		}
		if (paramInt1 >= paramActionTable.getNumActions()) {
			throw new IllegalArgumentException();
		}
	}

	public final Texture getTexture() {
		return jdField_a_of_type_ComMascotcapsuleMicro3dV3Texture;
	}

	public final void setTexture(Texture var1) {
		if (var1 == null) {
			throw new NullPointerException();
		}
		if (!var1.jdField_a_of_type_Boolean) {
			throw new IllegalArgumentException();
		}
		jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3Texture = new Texture[1];
		jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3Texture[0] = var1;
		jdField_a_of_type_ComMascotcapsuleMicro3dV3Texture = var1;
	}

	public final void setTexture(Texture[] var1) {
		if ((var1 == null) || (var1.length == 0)) {
			throw new NullPointerException();
		}
		for (int i = 0; i < var1.length; i++) {
			if (var1[i] == null) {
				throw new NullPointerException();
			}
			if (!var1[i].jdField_a_of_type_Boolean) {
				throw new IllegalArgumentException();
			}
		}
		jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3Texture = var1;
	}

	public final int getNumTextures() {
		if (jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3Texture == null) {
			return 0;
		}
		return jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3Texture.length;
	}

	public final void selectTexture(int paramInt) {
		if ((paramInt < 0) || (paramInt >= getNumTextures())) {
			throw new IllegalArgumentException();
		}
		jdField_a_of_type_ComMascotcapsuleMicro3dV3Texture = jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3Texture[paramInt];
	}

	public final int getNumPattern() {
		return emulator.graphics3D.micro3d.b.a();
	}

	public final void setPattern(int paramInt) {
	}
}