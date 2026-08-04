/*
Copyright (c) 2025-2026 Arman Jussupgaliyev
*/
package com.jblend.media.smaf;

import com.jblend.media.MediaData;
import emulator.custom.ResourceManager;

import java.io.IOException;

public class SmafData extends MediaData {

	public static final String type = "SMAF";
	byte[] data;

	public SmafData() {
		data = null;
	}

	public SmafData(String name) throws IOException {
		this(ResourceManager.getBytes(name));
	}

	public SmafData(byte[] data) {
		this.data = data;
	}

	public String getMediaType() {
		return type;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getContentType() {
		// TODO
		return 0;
	}

	public int getTagStart(int tag) {
		// TODO
		return -1;
	}

	public int getTagEnd(int tag) {
		// TODO
		return -1;
	}

	public int getWidth() {
		return 0;
	}

	public int getHeight() {
		return 0;
	}
}
