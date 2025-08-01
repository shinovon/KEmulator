package com.j_phone.util;

public class ImageEncoder extends com.vodafone.util.ImageEncoder {

	public ImageEncoder(int format) {
		super(format);
	}

	public static ImageEncoder createEncoder(int format) {
		if (format != FORMAT_PNG && format != FORMAT_JPEG) {
			throw new IllegalArgumentException();
		}
		return new ImageEncoder(format);
	}

}
