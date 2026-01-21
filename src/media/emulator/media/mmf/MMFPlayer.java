package emulator.media.mmf;

import emulator.Emulator;

import java.io.File;

public class MMFPlayer {
	private static boolean initialized;
	private static MaDll maDll;

	public static boolean initialize() {
		if (MMFPlayer.initialized) {
			return true;
		}
		if (Emulator.isX64()) {
			return false;
		}
		try {
			File f = new File(Emulator.getAbsolutePath(), "M7_EmuSmw7.dll");
			if (f.exists()
					&& new File(Emulator.getAbsolutePath(), "M7_EmuHw.dll").exists()) {
				Emulator.getEmulator().getLogStream().println("Loading MA-7 emulator");
				maDll = new MaDll(f.getAbsolutePath(), MaDll.MODE_MA7);
			} else if ((f = new File(Emulator.getAbsolutePath(), "M5_EmuSmw5.dll")).exists()
					&& new File(Emulator.getAbsolutePath(), "M5_EmuHw.dll").exists()) {
				Emulator.getEmulator().getLogStream().println("Loading MA-5 emulator");
				maDll = new MaDll(f.getAbsolutePath(), MaDll.MODE_MA5);
			} else if ((f = new File(Emulator.getAbsolutePath(), "ma3smwemu.dll")).exists()) {
				Emulator.getEmulator().getLogStream().println("Loading MA-3 emulator");
				maDll = new MaDll(f.getAbsolutePath(), MaDll.MODE_MA3);
			} else {
				throw new Exception("No smw emulator found");
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
			destroy();
			MMFPlayer.initialized = false;
		}
	}

	public static void destroy() {
		if (maDll == null) return;
		maDll.destroy();
	}

	public static MaDll getMaDll() {
		return maDll;
	}
}
