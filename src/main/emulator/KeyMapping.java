package emulator;

import javax.microedition.lcdui.Canvas;
import java.util.*;

/*
 * Keyboard
 */
public final class KeyMapping {
	private static Hashtable deviceKeyToStr;
	private static Hashtable strToPCKey;
	private static Hashtable keysTable;
	private static Hashtable aHashtable1067;
	public static Stack keyCacheStack;
	public static String[] deviceKeycodes;

	public KeyMapping() {
		super();
	}

	public static String get(final int n) {
		return keyToString(KeyMapping.deviceKeycodes[n]);
	}

	public static String keyToString(final String s) {
		if (s.isEmpty()) return "";
		final String s2;
		if ((s2 = (String) KeyMapping.keysTable.get(s)) != null) {
			return s2;
		}
		final int int1;
		if ((int1 = Integer.parseInt(s)) < 32 || int1 > 122) {
			return s2;
		}
		return new String(new char[]{(char) int1}).toUpperCase();
	}

	public static String method601(final String s) {
		if (s.isEmpty()) return "";
		final Enumeration<String> keys = KeyMapping.keysTable.keys();
		String value = null;
		while (keys.hasMoreElements()) {
			final String s2 = keys.nextElement();
			if (((String) KeyMapping.keysTable.get(s2)).equalsIgnoreCase(s)) {
				value = s2;
				break;
			}
		}
		if (value == "80") {
			value = "13";
		}
		if (value == null && s.length() == 1) {
			value = String.valueOf((int) s.toLowerCase().charAt(0));
		}
		return value;
	}

	public static int getArrowKeyFromDevice(final int n) {
		int n2 = 0;
		switch (n) {
			case 1: {
				n2 = ((Devices.getPropertyInt("KEY_UP") != 0) ? Devices.getPropertyInt("KEY_UP") : Canvas.UP);
				break;
			}
			case 6: {
				n2 = ((Devices.getPropertyInt("KEY_DOWN") != 0) ? Devices.getPropertyInt("KEY_DOWN") : Canvas.DOWN);
				break;
			}
			case 2: {
				n2 = ((Devices.getPropertyInt("KEY_LEFT") != 0) ? Devices.getPropertyInt("KEY_LEFT") : Canvas.LEFT);
				break;
			}
			case 5: {
				n2 = ((Devices.getPropertyInt("KEY_RIGHT") != 0) ? Devices.getPropertyInt("KEY_RIGHT") : Canvas.RIGHT);
				break;
			}
			case 8: {
				n2 = ((Devices.getPropertyInt("KEY_FIRE") != 0) ? Devices.getPropertyInt("KEY_FIRE") : Canvas.FIRE);
				break;
			}
		}
		return n2;
	}

	private static void method602() {
		final int mid = getArrowKeyFromDevice(Canvas.FIRE);
		final int up = getArrowKeyFromDevice(Canvas.UP);
		final int left = getArrowKeyFromDevice(Canvas.LEFT);
		final int down = getArrowKeyFromDevice(Canvas.DOWN);
		final int right = getArrowKeyFromDevice(Canvas.RIGHT);
		final int s1 = (Devices.getPropertyInt("KEY_S1") != 0) ? Devices.getPropertyInt("KEY_S1") : 21;
		final int s2 = (Devices.getPropertyInt("KEY_S2") != 0) ? Devices.getPropertyInt("KEY_S2") : 22;
		KeyMapping.strToPCKey.clear();
		KeyMapping.strToPCKey.put("NUM_0", "48");
		KeyMapping.strToPCKey.put("NUM_1", "49");
		KeyMapping.strToPCKey.put("NUM_2", "50");
		KeyMapping.strToPCKey.put("NUM_3", "51");
		KeyMapping.strToPCKey.put("NUM_4", "52");
		KeyMapping.strToPCKey.put("NUM_5", "53");
		KeyMapping.strToPCKey.put("NUM_6", "54");
		KeyMapping.strToPCKey.put("NUM_7", "55");
		KeyMapping.strToPCKey.put("NUM_8", "56");
		KeyMapping.strToPCKey.put("NUM_9", "57");
		KeyMapping.strToPCKey.put("*", "42");
		KeyMapping.strToPCKey.put("#", "35");
		KeyMapping.strToPCKey.put("UP", String.valueOf(up));
		KeyMapping.strToPCKey.put("DOWN", String.valueOf(down));
		KeyMapping.strToPCKey.put("LEFT", String.valueOf(left));
		KeyMapping.strToPCKey.put("RIGHT", String.valueOf(right));
		KeyMapping.strToPCKey.put("MIDDLE", String.valueOf(mid));
		KeyMapping.strToPCKey.put("S1", String.valueOf(s1));
		KeyMapping.strToPCKey.put("S2", String.valueOf(s2));
	}

	private static void method606() {
		KeyMapping.deviceKeyToStr.clear();
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[0], "NUM_0");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[1], "NUM_1");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[2], "NUM_2");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[3], "NUM_3");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[4], "NUM_4");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[5], "NUM_5");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[6], "NUM_6");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[7], "NUM_7");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[8], "NUM_8");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[9], "NUM_9");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[10], "*");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[11], "#");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[12], "UP");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[13], "DOWN");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[14], "LEFT");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[15], "RIGHT");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[16], "MIDDLE");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[17], "S1");
		KeyMapping.deviceKeyToStr.put(KeyMapping.deviceKeycodes[18], "S2");
	}

	public static void mapDeviceKey(final int n, final String s) {
		if (s != null) {
			KeyMapping.deviceKeycodes[n] = s;
		}
	}

	public static boolean isLeftSoft(final int n) {
		if (KeyMapping.deviceKeycodes[17].isEmpty()) return false;
		return Integer.parseInt(replaceKey(Integer.parseInt(KeyMapping.deviceKeycodes[17]))) == n;
	}

	public static boolean isRightSoft(final int n) {
		if (KeyMapping.deviceKeycodes[18].isEmpty()) return false;
		return Integer.parseInt(replaceKey(Integer.parseInt(KeyMapping.deviceKeycodes[18]))) == n;
	}

	public static int soft1() {
		return Integer.parseInt((String) KeyMapping.strToPCKey.get("S1"));
	}

	public static int soft2() {
		return Integer.parseInt((String) KeyMapping.strToPCKey.get("S2"));
	}

	public static void init() {
		method602();
		method606();
	}

	public static int fpsKey(int n) {
		if (Settings.fpsGame == 2) {
			//up
			if (n == 'w') return getArrowKeyFromDevice(Canvas.UP);
			//down
			if (n == 's') return getArrowKeyFromDevice(Canvas.DOWN);

			//num1
			if (n == 'a') return 49;
			//num3
			if (n == 'd') return 51;
			//num7
			if (n == 'q') return 55;
			//num9
			if (n == 'e') return 57;
			//num0
			if (n == 'c') return 48;
			//star
			if (n == 'x') return 42;
			//hash
			if (n == 'z') return 35;
		} else if (Settings.fpsGame == 3) {
			//up
			if (n == 'w') return getArrowKeyFromDevice(Canvas.UP);
			//down
			if (n == 's') return getArrowKeyFromDevice(Canvas.DOWN);

			//num1
			//if(n == 'a') return 49;
			//num3
			//if(n == 'd') return 51;
			//num7
			if (n == 'a' || n == 3) return 55;
			//num9
			if (n == 'd' || n == 4) return 57;
			//num0
			if (n == 'c') return 48;
			//star
			if (n == 'x') return 42;
			//hash
			if (n == 'z') return 35;
		}
		return 0;
	}

	public static String replaceKey(int n) {
		if (n == 80) {
			n = 13;
		}

		if (Settings.canvasKeyboard && (n >= '0' && n <= '9')) {
			return String.valueOf(n);
		}
		final Object value;
		if ((value = KeyMapping.deviceKeyToStr.get(String.valueOf(n))) != null) {
			return (String) KeyMapping.strToPCKey.get(value);
		}
		if (Settings.fpsMode) {
			int g = fpsKey(n);
			if (g != 0) return String.valueOf(g);
		}
		final String method594;
		if ((method594 = keyToString(String.valueOf(n))) == null) {
			return null;
		}
		final Object value2;
		if ((value2 = KeyMapping.aHashtable1067.get(method594)) == null) {
			if (Settings.canvasKeyboard) {
				if ((n >= 32 && n <= 126) || n == 8) {
					return String.valueOf(n);
				}
			}
			return null;
		}
		return (String) value2;
	}

	public static void keyArg(final String s) {
		if (s == null) {
			return;
		}
		final String[] split = s.split(";");
		for (int i = 0; i < split.length; ++i) {
			final String[] split2 = split[i].split("=");
			KeyMapping.aHashtable1067.put(split2[0], split2[1]);
		}
	}

	static {
		KeyMapping.deviceKeyToStr = new Hashtable();
		KeyMapping.strToPCKey = new Hashtable();
		KeyMapping.keysTable = new Hashtable();
		KeyMapping.aHashtable1067 = new Hashtable();
		KeyMapping.keyCacheStack = new Stack();
		KeyMapping.keysTable.put("48", "NUM_0");
		KeyMapping.keysTable.put("49", "NUM_1");
		KeyMapping.keysTable.put("50", "NUM_2");
		KeyMapping.keysTable.put("51", "NUM_3");
		KeyMapping.keysTable.put("52", "NUM_4");
		KeyMapping.keysTable.put("53", "NUM_5");
		KeyMapping.keysTable.put("54", "NUM_6");
		KeyMapping.keysTable.put("55", "NUM_7");
		KeyMapping.keysTable.put("56", "NUM_8");
		KeyMapping.keysTable.put("57", "NUM_9");
		KeyMapping.keysTable.put("47", "Key /");
		KeyMapping.keysTable.put("46", "Key .");
		KeyMapping.keysTable.put("42", "Key *");
		KeyMapping.keysTable.put("10", "F1");
		KeyMapping.keysTable.put("11", "F2");
		KeyMapping.keysTable.put("12", "F3");
		KeyMapping.keysTable.put("22", "F4");
		KeyMapping.keysTable.put("14", "F5");
		KeyMapping.keysTable.put("15", "F6");
		KeyMapping.keysTable.put("16", "F7");
		KeyMapping.keysTable.put("17", "F8");
		KeyMapping.keysTable.put("18", "F9");
		KeyMapping.keysTable.put("19", "F10");
		KeyMapping.keysTable.put("20", "F11");
		KeyMapping.keysTable.put("21", "F12");
		KeyMapping.keysTable.put("1", "UP");
		KeyMapping.keysTable.put("2", "DOWN");
		KeyMapping.keysTable.put("3", "LEFT");
		KeyMapping.keysTable.put("4", "RIGHT");
		KeyMapping.keysTable.put("5", "PageUp");
		KeyMapping.keysTable.put("6", "PageDown");
		KeyMapping.keysTable.put("7", "Home");
		KeyMapping.keysTable.put("8", "End");
		KeyMapping.keysTable.put("9", "Insert");
		KeyMapping.keysTable.put("13", "Enter");
		KeyMapping.keysTable.put("80", "Enter");
		KeyMapping.deviceKeycodes = new String[]{"48", "55", "56", "57", "52", "53", "54", "49", "50", "51", "42", "47", "1", "2", "3", "4", "13", "10", "11"};
	}
}
