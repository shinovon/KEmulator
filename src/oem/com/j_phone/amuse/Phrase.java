package com.j_phone.amuse;

import emulator.custom.CustomJarResources;

import java.io.IOException;

public class Phrase {
	public Phrase(String paramString)
			throws IOException {
		this(CustomJarResources.getBytes(paramString));
	}

	public Phrase(byte[] paramArrayOfByte) {
	}

	public int getSize() {
		return 0;
	}

	public int getUseTracks() {
		return 0;
	}
}
