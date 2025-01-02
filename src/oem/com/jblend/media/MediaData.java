package com.jblend.media;

import java.io.IOException;

public abstract class MediaData {
	public MediaData() {

	}

	public MediaData(String name) throws IOException {

	}

	public MediaData(byte[] data) {

	}

	public abstract String getMediaType();

	public abstract void setData(byte[] data);
}