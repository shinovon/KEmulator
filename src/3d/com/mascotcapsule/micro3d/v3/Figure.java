package com.mascotcapsule.micro3d.v3;

import java.io.IOException;

import emulator.custom.CustomJarResources;

public class Figure {
	emulator.graphics3D.micro3d.b impl;
	private Texture[] textureArray;
	private Texture textureNow;

	public Figure(byte[] paramArrayOfByte) {
		if (paramArrayOfByte == null) {
			throw new NullPointerException();
		}
		impl = new emulator.graphics3D.micro3d.b();
	}

	public Figure(String paramString) throws IOException {
		if (paramString == null) {
			throw new NullPointerException();
		}
		CustomJarResources.getResourceStream(paramString);
		impl = new emulator.graphics3D.micro3d.b();
	}

	public final void dispose() {
		impl = null;
	}

	public emulator.graphics3D.micro3d.b getImpl() {
		return impl;
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
		return textureNow;
	}

	public final void setTexture(Texture var1) {
		if (var1 == null) {
			throw new NullPointerException();
		}
		if (!var1.isModel) {
			throw new IllegalArgumentException();
		}
		textureArray = new Texture[1];
		textureArray[0] = var1;
		textureNow = var1;
	}

	public final void setTexture(Texture[] var1) {
		if ((var1 == null) || (var1.length == 0)) {
			throw new NullPointerException();
		}
		for (int i = 0; i < var1.length; i++) {
			if (var1[i] == null) {
				throw new NullPointerException();
			}
			if (!var1[i].isModel) {
				throw new IllegalArgumentException();
			}
		}
		textureArray = var1;
	}

	public final int getNumTextures() {
		if (textureArray == null) {
			return 0;
		}
		return textureArray.length;
	}

	public final void selectTexture(int paramInt) {
		if ((paramInt < 0) || (paramInt >= getNumTextures())) {
			throw new IllegalArgumentException();
		}
		textureNow = textureArray[paramInt];
	}

	public final int getNumPattern() {
		return emulator.graphics3D.micro3d.b.getNumPattern();
	}

	public final void setPattern(int paramInt) {
	}
}