package emulator.ui.swt;

import emulator.Settings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.eclipse.swt.SWT.ALT;
import static org.eclipse.swt.SWT.SHIFT;
import static org.eclipse.swt.SWT.CONTROL;

public class Accelerators {

	public static Map<String, Integer> accelerators = new LinkedHashMap<>();

	static {
		setDefaults();
	}

	public static void setDefaults() {
		set("Load jar...", CONTROL | 'O');
		set("Open JAD with Notepad", CONTROL | 'D');
		set("Application Settings...", 0);
		set("Pause/Step", CONTROL | 'T');
		set("Play/Resume", CONTROL | 'R');
		set("Suspend", CONTROL | 'S');
		set("Resume", CONTROL | 'E');
		set("Restart", ALT | 'R');
		set("Exit", 0);

		set("Set Screen Size...", 0);
		set("Reset Window Size", 0);

		set("Speed Up", Settings.altLessSpeedShortcuts ? '>' : (ALT | '>'));
		set("Slow Down", Settings.altLessSpeedShortcuts ? '<' : (ALT | '<'));
		set("Reset Speed", Settings.altLessSpeedShortcuts ? '?' : (ALT | '?'));
		set("Capture to File", ALT | 'S');
		set("Capture to Clipboard", ALT | 'C');
		set("Enable Trace", CONTROL | SWT.F3);
		set("QWERTY Mode", 0);
		set("Disable Network Access", 0);

		set("Infos", CONTROL | 'I');
		set("X-Ray View", ALT | 'X');
		set("Rotate Screen", CONTROL | 'Y');
		set("Rotate 90 Degrees", ALT | 'Y');
		set("Force Paint", CONTROL | 'F');

		set("Keypad", 0);
		set("Watcher", 0);
		set("Profiler", 0);
		set("Methods", 0);
		set("Memory View", 0);
		set("Media View", 0);
		set("M3G View", 0);
		set("SMS Console", 0);
		set("Sensor Simulator", 0);
		set("Global Settings...", 0);
	}

	static void set(String text, int a) {
		accelerators.put(text, a);
	}

	public static void set(String text, String s) {
		try {
			int a = Integer.parseInt(s);
			if (accelerators.containsKey(text)) {
				accelerators.put(text, a);
			}
		} catch (Exception ignored) {}
	}

	public static String get(String text) {
		return getAcceleratorString(accelerators.get(text));
	}

	public static void menuItem(MenuItem item, String text) {
		menuItem(item, text, 0);
	}

	public static void menuItem(MenuItem item, String text, int def) {
		int accelerator = def;

		if (accelerators.containsKey(text)) {
			accelerator = accelerators.get(text);
		}

		if (accelerator == 0) {
			item.setText(text);
			item.setAccelerator(0);
			return;
		}

		item.setText(text + " \t" + getAcceleratorString(accelerator));
		item.setAccelerator(accelerator);
	}

	public static String getAcceleratorString(int a) {
		if (a == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();

		if ((a & CONTROL) != 0) {
			sb.append("Ctrl+");
		}

		if ((a & ALT) != 0) {
			sb.append("Alt+");
		}

		if ((a & SHIFT) != 0) {
			sb.append("Shift+");
		}

		int key = a & SWT.KEY_MASK;

		switch (key) {
		case SWT.ARROW_UP:
			sb.append("Up");
			break;
		case SWT.ARROW_DOWN:
			sb.append("Down");
			break;
		case SWT.ARROW_LEFT:
			sb.append("Left");
			break;
		case SWT.ARROW_RIGHT:
			sb.append("Right");
			break;
		case SWT.PAGE_UP:
			sb.append("PageUp");
			break;
		case SWT.PAGE_DOWN:
			sb.append("PageDown");
			break;
		case SWT.HOME:
			sb.append("Home");
			break;
		case SWT.END:
			sb.append("End");
			break;
		case SWT.INSERT:
			sb.append("Insert");
			break;
		case SWT.F1:
			sb.append("F1");
			break;
		case SWT.F2:
			sb.append("F2");
			break;
		case SWT.F3:
			sb.append("F3");
			break;
		case SWT.F4:
			sb.append("F4");
			break;
		case SWT.F5:
			sb.append("F5");
			break;
		case SWT.F6:
			sb.append("F6");
			break;
		case SWT.F7:
			sb.append("F7");
			break;
		case SWT.F8:
			sb.append("F8");
			break;
		case SWT.F9:
			sb.append("F9");
			break;
		case SWT.F10:
			sb.append("F10");
			break;
		case SWT.F11:
			sb.append("F11");
			break;
		case SWT.F12:
			sb.append("F12");
			break;
		default:
			sb.append((char) (a & 0xFFFF));
			break;
		}

		return sb.toString();
	}

}
