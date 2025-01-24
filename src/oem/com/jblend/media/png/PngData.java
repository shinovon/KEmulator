package com.jblend.media.png;

import com.jblend.media.MediaData;
import emulator.custom.CustomJarResources;

import java.io.IOException;

public class PngData
		extends MediaData {
	public static final String type = "PNG";

	public PngData() {
	}

	public PngData(String paramString)
			throws IOException {
		this(CustomJarResources.getBytes(paramString));
	}

	public PngData(byte[] paramArrayOfByte) {
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
