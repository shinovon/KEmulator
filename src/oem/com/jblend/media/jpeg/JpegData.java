package com.jblend.media.jpeg;

import com.jblend.media.MediaData;

import java.io.IOException;

public class JpegData
		extends MediaData {
	public static final String type = "JPEG";

	public JpegData() {
	}

	public JpegData(String paramString)
			throws IOException {
		super();
	}

	public JpegData(byte[] paramArrayOfByte) {
		super();
	}

	public int getWidth() {
		return 0;
	}

	public int getHeight() {
		return 0;
	}

	public String getMediaType() {
		return null;
	}

	public void setData(byte[] paramArrayOfByte) {
	}
}
