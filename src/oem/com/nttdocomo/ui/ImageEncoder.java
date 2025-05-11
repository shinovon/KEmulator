package com.nttdocomo.ui;

public class ImageEncoder {
	public static final int QUALITY = 0;
	public static final int ATTR_QUALITY_HIGH = 0;
	public static final int ATTR_QUALITY_STANDARD = 1;
	public static final int ATTR_QUALITY_LOW = 2;

	private ImageEncoder() {
	}

	public static ImageEncoder getEncoder(final String s) {
		return null;
	}

	public EncodedImage encode(final Image image, final int n, final int n2, final int n3, final int n4) {
		return null;
	}

	public EncodedImage encode(final Canvas canvas, final int n, final int n2, final int n3, final int n4) {
		return null;
	}

	public void setAttribute(final int n, final int n2) {
	}

	public final boolean isAvailable(final int n) {
		return false;
	}
}
