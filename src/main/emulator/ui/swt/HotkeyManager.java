package emulator.ui.swt;

import emulator.Settings;
import org.eclipse.swt.SWT;

import java.util.*;

public class HotkeyManager {

	public static class HotkeyAction {
		public final String id;
		public final String displayName;
		public final String description;
		public int defaultKeyCode;
		public int defaultStateMask;
		public int keyCode;
		public int stateMask;

		public HotkeyAction(String id, String displayName, String description, int defaultKeyCode, int defaultStateMask) {
			this.id = id;
			this.displayName = displayName;
			this.description = description;
			this.defaultKeyCode = defaultKeyCode;
			this.defaultStateMask = defaultStateMask;
			this.keyCode = defaultKeyCode;
			this.stateMask = defaultStateMask;
		}

		public String formatKey() {
			if (keyCode == 0) return "(none)";
			StringBuilder sb = new StringBuilder();
			if ((stateMask & SWT.CTRL) != 0) sb.append("Ctrl+");
			if ((stateMask & SWT.SHIFT) != 0) sb.append("Shift+");
			if ((stateMask & SWT.ALT) != 0) sb.append("Alt+");
			sb.append(keyCodeToString(keyCode));
			return sb.toString();
		}

		public static String keyCodeToString(int code) {
			if (code >= 'A' && code <= 'Z') return String.valueOf((char) code);
			if (code >= 'a' && code <= 'z') return String.valueOf((char) (code - 32));
			if (code >= '0' && code <= '9') return String.valueOf((char) code);
			if (code >= SWT.KEYPAD_0 && code <= SWT.KEYPAD_9) return "NumPad" + (code - SWT.KEYPAD_0);
			switch (code) {
				case SWT.KEYPAD_ADD: return "NumPad+";
				case SWT.KEYPAD_SUBTRACT: return "NumPad-";
				case SWT.KEYPAD_MULTIPLY: return "NumPad*";
				case SWT.KEYPAD_DIVIDE: return "NumPad/";
				case SWT.KEYPAD_DECIMAL: return "NumPad.";
				case SWT.KEYPAD_CR: return "NumPadEnter";
				case SWT.ARROW_UP: return "Up";
				case SWT.ARROW_DOWN: return "Down";
				case SWT.ARROW_LEFT: return "Left";
				case SWT.ARROW_RIGHT: return "Right";
				case SWT.F1: return "F1";
				case SWT.F2: return "F2";
				case SWT.F3: return "F3";
				case SWT.F4: return "F4";
				case SWT.F5: return "F5";
				case SWT.F6: return "F6";
				case SWT.F7: return "F7";
				case SWT.F8: return "F8";
				case SWT.F9: return "F9";
				case SWT.F10: return "F10";
				case SWT.F11: return "F11";
				case SWT.F12: return "F12";
				case SWT.BS: return "Backspace";
				case SWT.CR: return "Enter";
				case SWT.TAB: return "Tab";
				case SWT.ESC: return "Escape";
				case SWT.DEL: return "Delete";
				case SWT.INSERT: return "Insert";
				case SWT.HOME: return "Home";
				case SWT.END: return "End";
				case SWT.PAGE_UP: return "PageUp";
				case SWT.PAGE_DOWN: return "PageDown";
				case SWT.SPACE: return "Space";
				case ',': return ",";
				case '.': return ".";
				case '/': return "/";
				case ';': return ";";
				case '\'': return "'";
				case '[': return "[";
				case ']': return "]";
				case '\\': return "\\";
				case '-': return "-";
				case '=': return "=";
				case '`': return "`";
				default: return "Key(" + code + ")";
			}
		}
	}

	private static final Map<String, HotkeyAction> actions = new LinkedHashMap<>();
	private static boolean loaded;

	public static final HotkeyAction CYCLE_RES_PREV = add("cycleResPrev", "Previous resolution preset", "Cycle to previous resolution preset", SWT.ARROW_LEFT, SWT.ALT);
	public static final HotkeyAction CYCLE_RES_NEXT = add("cycleResNext", "Next resolution preset", "Cycle to next resolution preset", SWT.ARROW_RIGHT, SWT.ALT);
	public static final HotkeyAction ADD_TO_FAVORITES = add("addToFav", "Add to Favorites", "Add current JAR to Favorites folder", 'B', SWT.CTRL);
	public static final HotkeyAction OPEN_FAVORITES = add("openFav", "Open Favorites", "Open Favorites browser to manage saved apps", 'F', SWT.CTRL);
	public static final HotkeyAction DELETE_JAR = add("deleteJar", "Delete current JAR", "Permanently delete the current JAR file", 'W', SWT.CTRL);
	public static final HotkeyAction OPEN_APP_SETTINGS = add("openAppSettings", "Application Settings", "Open Application Settings dialog", 'A', SWT.CTRL);
	public static final HotkeyAction PREV_JAR = add("prevJar", "Previous JAR in folder", "Open the previous JAR in the same folder", '[', SWT.CTRL);
	public static final HotkeyAction NEXT_JAR = add("nextJar", "Next JAR in folder", "Open the next JAR in the same folder", ']', SWT.CTRL);
	public static final HotkeyAction TOGGLE_FULLSCREEN = add("toggleFullscreen", "Toggle fullscreen", "Switch between windowed and fullscreen mode", SWT.F11, 0);
	public static final HotkeyAction CAPTURE_TO_FILE = add("captureToFile", "Capture to File", "Save screenshot to file", 'S', SWT.ALT);
	public static final HotkeyAction CAPTURE_TO_CLIPBOARD = add("captureToClip", "Capture to Clipboard", "Copy screenshot to clipboard", 'C', SWT.ALT);
	public static final HotkeyAction SPEED_UP = add("speedUp", "Speed Up", "Increase emulation speed", '.', 0);
	public static final HotkeyAction SLOW_DOWN = add("slowDown", "Slow Down", "Decrease emulation speed", ',', 0);
	public static final HotkeyAction RESET_SPEED = add("resetSpeed", "Reset Speed", "Reset emulation speed to normal", '/', 0);
	public static final HotkeyAction RESTART_MIDLET = add("restartMidlet", "Restart MIDlet", "Restart the current MIDlet", 'R', SWT.CTRL | SWT.SHIFT);
	public static final HotkeyAction SUSPEND = add("suspend", "Suspend", "Suspend the running MIDlet", 'S', SWT.CTRL);
	public static final HotkeyAction RESUME = add("resume", "Resume", "Resume the suspended MIDlet", 'E', SWT.CTRL);
	public static final HotkeyAction PAUSE_STEP = add("pauseStep", "Pause/Step", "Pause or single-step the MIDlet", 'T', SWT.CTRL);
	public static final HotkeyAction PLAY_RESUME = add("playResume", "Play/Resume", "Resume playback", 'R', SWT.CTRL);
	public static final HotkeyAction XRAY_VIEW = add("xrayView", "X-Ray View", "Toggle X-Ray view mode", 'X', SWT.CTRL);
	public static final HotkeyAction ROTATE_SCREEN = add("rotateScreen", "Rotate Screen", "Swap width and height", 'Y', SWT.CTRL);
	public static final HotkeyAction ROTATE_90 = add("rotate90", "Rotate 90 Degrees", "Rotate screen by 90 degrees", 'Y', SWT.CTRL | SWT.SHIFT);
	public static final HotkeyAction FORCE_PAINT = add("forcePaint", "Force Paint", "Force canvas repaint", SWT.F5, 0);
	public static final HotkeyAction ZOOM_IN = add("zoomIn", "Zoom In", "Zoom in the canvas", SWT.KEYPAD_ADD, 0);
	public static final HotkeyAction ZOOM_OUT = add("zoomOut", "Zoom Out", "Zoom out the canvas", SWT.KEYPAD_SUBTRACT, 0);
	public static final HotkeyAction TOGGLE_RESOLUTION_RESTART = add("toggleResRestart", "Toggle restart on resolution change", "Toggle restarting MIDlet when resolution is changed", '\\', 0);
	public static final HotkeyAction CYCLE_BG_ANIM = add("cycleBgAnim", "Cycle BG animation mode", "Cycle background icon animation direction (1-9)", 'L', SWT.CTRL);
	public static final HotkeyAction RESET_WINDOW_SIZE = add("resetWindowSize", "Reset window size", "Reset window size to default", 0, 0);
	public static final HotkeyAction CYCLE_SCALE_MODE = add("cycleScaleMode", "(Cycling) No adaptive / Fit / Fit integer / Sync", "Cycle scaling mode: No adaptive → Fit → Fit integer → Sync", 0, 0);
	public static final HotkeyAction CYCLE_INTERP_MODE = add("cycleInterpMode", "(Cycling) Interpolation: Nearest / Low / High", "Cycle interpolation: NearestNeighbor → LowQuality → HighQuality", 0, 0);
	public static final HotkeyAction AUTO_SKIP_APP_SETTINGS = add("autoSkipAppSettings", "Auto-skip Application Settings", "Toggle auto-skipping Application Settings dialog", 0, 0);
	public static final HotkeyAction DISABLE_TOUCH_DBLCLICK = add("disableTouchDblClick", "Disable touchscreen double-click", "Toggle disabling touchscreen double-click", 0, 0);
	public static final HotkeyAction SETUP_LUCKY_FOLDER = add("setupLuckyFolder", "Setup Lucky Folder...", "Open Lucky Folder setup dialog", 0, 0);
	public static final HotkeyAction OPEN_LUCKY_JAR = add("openLuckyJar", "Open Lucky Jar", "Open a Lucky Jar", 0, 0);

	// Numpad UI navigation keys
	public static final HotkeyAction UI_UP = add("uiUp", "UI Up", "Navigate up in UI", SWT.KEYPAD_8, 0);
	public static final HotkeyAction UI_DOWN = add("uiDown", "UI Down", "Navigate down in UI", SWT.KEYPAD_2, 0);
	public static final HotkeyAction UI_LEFT = add("uiLeft", "UI Left", "Navigate left in UI", SWT.KEYPAD_4, 0);
	public static final HotkeyAction UI_RIGHT = add("uiRight", "UI Right", "Navigate right in UI", SWT.KEYPAD_6, 0);
	public static final HotkeyAction UI_CONFIRM = add("uiConfirm", "UI Confirm", "Confirm/select in UI", SWT.KEYPAD_5, 0);
	public static final HotkeyAction UI_CONFIRM_2 = add("uiConfirm2", "UI Confirm 2", "Secondary confirm/select (gamepad)", SWT.F3, 0);
	public static final HotkeyAction UI_SWITCH_LEFT = add("uiSwitchLeft", "UI Switch Left", "Switch focus left in UI", SWT.KEYPAD_MULTIPLY, 0);
	public static final HotkeyAction UI_SWITCH_RIGHT = add("uiSwitchRight", "UI Switch Right", "Switch focus right in UI", SWT.KEYPAD_DIVIDE, 0);
	public static final HotkeyAction UI_CANCEL = add("uiCancel", "UI Cancel", "Cancel/back in UI", SWT.F2, 0);

	private static HotkeyAction add(String id, String displayName, String description, int defaultKeyCode, int defaultStateMask) {
		HotkeyAction a = new HotkeyAction(id, displayName, description, defaultKeyCode, defaultStateMask);
		actions.put(id, a);
		return a;
	}

	public static Collection<HotkeyAction> getAll() {
		return actions.values();
	}

	public static HotkeyAction get(String id) {
		return actions.get(id);
	}

	public static void loadFromProperties(Properties props) {
		for (HotkeyAction a : actions.values()) {
			a.keyCode = a.defaultKeyCode;
			a.stateMask = a.defaultStateMask;
			String val = props.getProperty("Hotkey_" + a.id);
			if (val != null && !val.isEmpty()) {
				String[] parts = val.split("_", 2);
				try {
					a.stateMask = Integer.parseInt(parts[0]);
					a.keyCode = Integer.parseInt(parts[1]);
				} catch (Exception ignored) {}
			}
		}
		loaded = true;
	}

	public static void saveToProperties(Properties props) {
		for (HotkeyAction a : actions.values()) {
			props.setProperty("Hotkey_" + a.id, a.stateMask + "_" + a.keyCode);
		}
	}

	public static boolean isLoaded() {
		return loaded;
	}

	public static String checkConflict(HotkeyAction exclude, int stateMask, int keyCode) {
		for (HotkeyAction a : actions.values()) {
			if (a == exclude) continue;
			if (a.keyCode == keyCode && a.stateMask == stateMask) {
				return a.displayName;
			}
		}
		return null;
	}
}
