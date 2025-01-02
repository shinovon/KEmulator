package com.jblend.media.smaf;

import com.jblend.media.MediaData;

import java.io.IOException;

public class SmafData extends MediaData {

	public static final String type = "SMAF";

	public SmafData() {

	}

	public SmafData(String name) throws IOException {

	}

	public SmafData(byte[] data) {

	}

	public String getMediaType() {
		return null;
	}

	public void setData(byte[] data) {

	}

	public int getContentType() {
		return 0;
	}

	public int getTagStart(int tag) {
		return 0;
	}

	public int getTagEnd(int tag) {
		return 0;
	}

	public int getWidth() {
		return 0;
	}

	public int getHeight() {
		return 0;
	}
}
