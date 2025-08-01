package com.nttdocomo.ui;

import java.io.*;

public final class MediaManager {
	private MediaManager() {
	}

	public static final MediaData getData(final String s) {
		return (MediaData) new com.nttdocomo.ui.maker.MediaData(s);
	}

	public static final MediaImage getImage(final String s) {
		return (MediaImage) new com.nttdocomo.ui.maker.MediaImage(s);
	}

	public static final MediaSound getSound(final String s) {
		return (MediaSound) new com.nttdocomo.ui.maker.MediaSound(s);
	}

	public static final MediaData getData(final InputStream inputStream) {
		return (MediaData) new com.nttdocomo.ui.maker.MediaData(inputStream);
	}

	public static final MediaImage getImage(final InputStream inputStream) {
		return (MediaImage) new com.nttdocomo.ui.maker.MediaImage(inputStream);
	}

	public static final MediaSound getSound(final InputStream inputStream) {
		return (MediaSound) new com.nttdocomo.ui.maker.MediaSound(inputStream);
	}

	public static final MediaData getData(final byte[] array) {
		return (MediaData) new com.nttdocomo.ui.maker.MediaData(array);
	}

	public static final MediaImage getImage(final byte[] array) {
		return (MediaImage) new com.nttdocomo.ui.maker.MediaImage(array);
	}

	public static final MediaSound getSound(final byte[] array) {
		return (MediaSound) new com.nttdocomo.ui.maker.MediaSound(array);
	}
}
