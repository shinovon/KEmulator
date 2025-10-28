package emulator.media.mmf;

import emulator.Emulator;

public class MMFPlayer {
	static boolean initialized;

	public MMFPlayer() {
		super();
	}

	public static boolean initialize() {
		if (MMFPlayer.initialized) {
			return true;
		}
		if (Emulator.isX64()) {
			return false;
		}
		try {
			initMMFLibrary(Emulator.getAbsolutePath() + "/M5_EmuSmw5.dll");
			return MMFPlayer.initialized = true;
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
		return MMFPlayer.initialized = false;
	}

	public static void close() {
		if (MMFPlayer.initialized) {
			stop();
			destroy();
			MMFPlayer.initialized = false;
		}
	}

	public static void initMMFLibrary(final String dll) {

	}

	public static void initPlayer(final byte[] data) {

	}

	public static void play(final int p0, final int p1) {

	}

	public static void destroy() {

	}

	public static boolean isPlaying() {
		return false;
	}

	public static void stop() {

	}

	public static void pause() {

	}

	public static void resume() {

	}
}
