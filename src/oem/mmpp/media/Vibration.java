package mmpp.media;

import emulator.Emulator;

public class Vibration {
	public static int getLevelNum() {
		return 0;
	}

	public static void start(int level, int timeout) {
		Emulator.getEmulator().getScreen().startVibra(timeout);
	}

	public static void stop() {
		Emulator.getEmulator().getScreen().startVibra(0);
	}
}