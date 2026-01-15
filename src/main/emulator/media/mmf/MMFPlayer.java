package emulator.media.mmf;

import emulator.Emulator;

import java.io.File;

public class MMFPlayer {
	static boolean initialized;
	static MaDll maDll;
	static int currentSound = -1;

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
			if (new File(Emulator.getAbsolutePath() + "/M5_EmuSmw5.dll").exists()
					&& new File(Emulator.getAbsolutePath() + "/M5_EmuHw.dll").exists()
					&& new File(Emulator.getAbsolutePath() + "/SMAFMMS5EMU.dll").exists()) {
				maDll = new MaDll(Emulator.getAbsolutePath() + "/M5_EmuSmw5.dll", MaDll.MODE_MA5);
			} else {
				maDll = new MaDll(Emulator.getAbsolutePath() + "/ma3smwemu.dll", MaDll.MODE_MA3);
			}
			maDll.init();
			return MMFPlayer.initialized = true;
		} catch (Throwable e) {
			e.printStackTrace();
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

	public static void initPlayer(final byte[] data) {
		if (currentSound != -1) {
			maDll.close(currentSound);
		}

		currentSound = maDll.load(data);
	}

	public static void play(final int p0, final int p1) {
		maDll.setVolume(currentSound, (p1 * 100) / 5);
		maDll.start(currentSound, p0);
	}

	public static void destroy() {
		if (maDll == null) return;

		if (currentSound != -1) maDll.stop(currentSound);
		currentSound = -1;
		maDll.phraseTerminate();
		maDll.destroy();
	}

	public static boolean isPlaying() {
		return maDll.getStatus(currentSound) == 4;
	}

	public static void stop() {
		if (currentSound != -1) {
			maDll.stop(currentSound);
		}
	}

	public static void pause() {
		maDll.pause(currentSound);
	}

	public static int getStatus() {
		return maDll.getStatus(currentSound);
	}

	public static void resume() {
		maDll.resume(currentSound);
	}

	public static MaDll getMaDll() {
		return maDll;
	}
}
