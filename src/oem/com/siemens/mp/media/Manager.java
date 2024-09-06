package com.siemens.mp.media;

import com.siemens.mp.media.protocol.DataSource;

import java.io.IOException;
import java.io.InputStream;

public final class Manager {
	public static final String TONE_DEVICE_LOCATOR = "device://tone";
	public static final String MIDI_DEVICE_LOCATOR = "device://midi";

	public static String[] getSupportedContentTypes(String s) {
		return new String[]{
				s};
	}

	public static String[] getSupportedProtocols(String s) {
		return new String[]{
				s};
	}

	public static Player createPlayer(String s)
			throws IOException, MediaException {
		return new NativePlayer();
	}

	public static Player createPlayer(InputStream inputstream, String s)
			throws IOException, MediaException {
		return new NativePlayer();
	}

	public static Player createPlayer(DataSource datasource)
			throws IOException, MediaException {
		return new NativePlayer();
	}

	public static void playTone(int i, int j, int k)
			throws MediaException {
	}

	public static TimeBase getSystemTimeBase() {
		return null;
	}
}
