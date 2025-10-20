package emulator.media;

import emulator.Emulator;
import emulator.i;

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
			i.a("mmfplayer");
			initMMFLibrary(Emulator.getAbsolutePath() + "/ma3smwemu.dll");
			return MMFPlayer.initialized = true;
		} catch (Throwable ignored) {}
		return MMFPlayer.initialized = false;
	}

	public static void close() {
		if (MMFPlayer.initialized) {
			stop();
			destroy();
			MMFPlayer.initialized = false;
		}
	}

	public static native int initMMFLibrary(final String p0);

	public static native void initPlayer(final byte[] p0);

	public static native void play(final int p0, final int p1);

	public static native void destroy();

	public static native boolean isPlaying();

	public static native void stop();

	public static native void pause();

	public static native void resume();
}
