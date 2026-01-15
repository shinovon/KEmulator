package com.j_phone.amuse;

import emulator.custom.ResourceManager;

import java.io.IOException;

public class Phrase {
	final byte[] data;

	public Phrase(String paramString)
			throws IOException {
		this(ResourceManager.getBytes(paramString));
	}

	public Phrase(byte[] data) {
		this.data = data;
	}

	public int getSize() {
		return data != null ? data.length : 0;
	}

	public int getUseTracks() {
		return 1;
	}
}
