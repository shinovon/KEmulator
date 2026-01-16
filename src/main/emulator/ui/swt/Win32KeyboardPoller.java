/*
Copyright (c) 2025 Arman Jussupgaliyev
*/
package emulator.ui.swt;

import com.sun.jna.platform.win32.User32;
import emulator.Emulator;
import emulator.ReflectUtil;
import emulator.Utils;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;

import java.lang.reflect.Method;

public class Win32KeyboardPoller {

	private EmulatorScreen screen;

	public Win32KeyboardPoller(EmulatorScreen screen) {
		this.screen = screen;
		try {
			if (win32OS == null) {
				win32OS = Class.forName("org.eclipse.swt.internal.win32.OS");
			}
			if (win32OSGetKeyState == null) {
				win32OSGetKeyState = ReflectUtil.getMethod(win32OS, "GetAsyncKeyState", int.class);
			}
		} catch (Throwable ignored) {}
	}

	private long lastPollTime;
	boolean win = Utils.win;
	private final boolean[] lastKeyboardButtonStates = new boolean[256];
	private final boolean[] keyboardButtonStates = new boolean[lastKeyboardButtonStates.length];
	private final long[] keyboardButtonDownTimes = new long[keyboardButtonStates.length];
	private final long[] keyboardButtonHoldTimes = new long[keyboardButtonStates.length];
	private static Class win32OS;
	private static Method win32OSGetKeyState;

	public synchronized void pollKeyboard(Canvas canvas) {
		if (!win || canvas == null || canvas.isDisposed()) return;
		long now = System.currentTimeMillis();
		Shell shell = canvas.getShell();
		if (now - lastPollTime < 10) return;
		lastPollTime = now;

		final boolean active = canvas.getDisplay().getActiveShell() == shell &&
				shell.isVisible() &&
				canvas.isFocusControl();
//		if (!active) return;
		try {

			for (int i = 0; i < keyboardButtonStates.length; i++) {
				lastKeyboardButtonStates[i] = keyboardButtonStates[i];
				short keyState;
				if (win32OSGetKeyState != null) {
					keyState = (Short) win32OSGetKeyState.invoke(null, i);
				} else {
					keyState = User32.INSTANCE.GetAsyncKeyState(i);
				}
				boolean pressed = active && ((keyState & 0x8000) == 0x8000 || ((keyState & 0x1) == 0x1));
				if (!keyboardButtonStates[i]) {
					if (pressed) {
						keyboardButtonStates[i] = true;
						keyboardButtonHoldTimes[i] = 0;
						keyboardButtonDownTimes[i] = now;
//                        onKeyDown(i);
					}
				} else if (!pressed) {
					keyboardButtonStates[i] = false;
					keyboardButtonHoldTimes[i] = 0;
					screen.onKeyUp(convertKey(i), shell);
				}
				if (lastKeyboardButtonStates[i] && pressed && now - keyboardButtonDownTimes[i] >= 460) {
					if (keyboardButtonHoldTimes[i] == 0 || keyboardButtonDownTimes[i] > keyboardButtonHoldTimes[i]) {
						keyboardButtonHoldTimes[i] = now;
					}
					if (now - keyboardButtonHoldTimes[i] >= 40) {
						keyboardButtonHoldTimes[i] = now;
//                        onKeyHeld(i);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int convertKey(int n) {
		if (n <= 7) return -1;
		if (n >= 14 && n <= 31) return -1;
		if (n >= 91 && n <= 95) return -1;
		if (n >= 41 && n <= 47) return -1;
		if (n >= 124 && n <= 186) return -1;
		if (n > 190) return -1;
		if (n >= 'A' && n <= 'Z') n -= 'A' - 'a';
		else if (n >= 96 && n <= 105) n = n - 96 + '0';
		else if (n >= 112 && n <= 123) n = n - 112 + 10;
		else switch (n) {
				case 33:
					n = 5;
					break;
				case 34:
					n = 6;
					break;
				case 35:
					n = 8;
					break;
				case 36:
					n = 7;
					break;
				case 37:
					n = 3;
					break;
				case 38:
					n = 1;
					break;
				case 39:
					n = 4;
					break;
				case 40:
					n = 2;
					break;
				case 106:
					n = '*';
					break;
				case 107:
					n = '+';
					break;
				case 109:
					n = '-';
					break;
				case 110:
					n = '.';
					break;
				case 111:
					n = '/';
					break;
				case 187:
					n = '=';
					break;
				case 188:
					n = ',';
					break;
				case 189:
					n = '-';
					break;
				case 190:
					n = '.';
					break;
			}
		return n;
	}

}
