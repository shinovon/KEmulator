/*
Copyright (c) 2026 Arman Jussupgaliyev
*/
package mmpp.media.phrase;

import emulator.custom.ResourceManager;

import java.io.IOException;

public class PhraseData {
	byte[] data;

	public PhraseData(byte[] data) {
		this.data = data;
	}

	public PhraseData(String url) throws IOException {
		this(ResourceManager.getBytes(url));
	}

	public int getSize() {
		return data == null ? 0 : data.length;
	}

	public int getUseTracks() {
		return 1;
	}
}
