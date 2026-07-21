package emulator.ui;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

public class VibraPipe {
	private static WinNT.HANDLE vibraPipe = WinNT.INVALID_HANDLE_VALUE;

	public static void send(int durationMs, int intensity) {
		try {
			Kernel32 k32 = Kernel32.INSTANCE;

			if (WinNT.INVALID_HANDLE_VALUE.equals(vibraPipe)) {
				vibraPipe = k32.CreateFile(
					"\\\\.\\pipe\\J2MEGamepadVibe",
					WinNT.GENERIC_WRITE,
					WinNT.FILE_SHARE_READ | WinNT.FILE_SHARE_WRITE,
					null, WinNT.OPEN_EXISTING, 0, null);
				if (WinNT.INVALID_HANDLE_VALUE.equals(vibraPipe))
					return;
			}

			byte[] data = new byte[8];
			data[0] = (byte) (durationMs & 0xFF);
			data[1] = (byte) ((durationMs >> 8) & 0xFF);
			data[2] = (byte) ((durationMs >> 16) & 0xFF);
			data[3] = (byte) ((durationMs >> 24) & 0xFF);
			data[4] = (byte) (intensity & 0xFF);
			data[5] = (byte) ((intensity >> 8) & 0xFF);
			data[6] = (byte) ((intensity >> 16) & 0xFF);
			data[7] = (byte) ((intensity >> 24) & 0xFF);
			IntByReference written = new IntByReference();
			if (!k32.WriteFile(vibraPipe, data, data.length, written, null) || written.getValue() != data.length) {
				k32.CloseHandle(vibraPipe);
				vibraPipe = WinNT.INVALID_HANDLE_VALUE;
			}
		} catch (Throwable ignored) {
			if (!WinNT.INVALID_HANDLE_VALUE.equals(vibraPipe)) {
				try { Kernel32.INSTANCE.CloseHandle(vibraPipe); } catch (Throwable t) { }
				vibraPipe = WinNT.INVALID_HANDLE_VALUE;
			}
		}
	}
}
