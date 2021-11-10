package emulator;

import java.util.*;

/*
 * Keyboard
 */
public final class Keyboard
{
    private static Hashtable deviceKeyToStr;
    private static Hashtable strToPCKey;
    private static Hashtable keysTable;
    private static Hashtable aHashtable1067;
    public static Stack aStack1063;
    public static String[] deviceKeycodes;
    
    public Keyboard() {
        super();
    }
    
    public static String get(final int n) {
        return keyToString(Keyboard.deviceKeycodes[n]);
    }
    
    public static String keyToString(final String s) {
        final String s2;
        if ((s2 = (String) Keyboard.keysTable.get(s)) != null) {
            return s2;
        }
        final int int1;
        if ((int1 = Integer.parseInt(s)) < 32 || int1 > 122) {
            return s2;
        }
        return new String(new char[] { (char)int1 }).toUpperCase();
    }
    
    public static String method601(final String s) {
        final Enumeration<String> keys = Keyboard.keysTable.keys();
        String value = null;
        while (keys.hasMoreElements()) {
            final String s2 = keys.nextElement();
            if (((String)Keyboard.keysTable.get(s2)).equalsIgnoreCase(s)) {
                value = s2;
                break;
            }
        }
        if (value == "80") {
            value = "13";
        }
        if (value == null && s.length() == 1) {
            value = String.valueOf((int)s.toLowerCase().charAt(0));
        }
        return value;
    }
    
    public static int getArrowKeyFromDevice(final int n) {
        int n2 = 0;
        switch (n) {
            case 1: {
                n2 = ((Devices.method617("KEY_UP") != 0) ? Devices.method617("KEY_UP") : 1);
                break;
            }
            case 6: {
                n2 = ((Devices.method617("KEY_DOWN") != 0) ? Devices.method617("KEY_DOWN") : 6);
                break;
            }
            case 2: {
                n2 = ((Devices.method617("KEY_LEFT") != 0) ? Devices.method617("KEY_LEFT") : 2);
                break;
            }
            case 5: {
                n2 = ((Devices.method617("KEY_RIGHT") != 0) ? Devices.method617("KEY_RIGHT") : 5);
                break;
            }
            case 8: {
                n2 = ((Devices.method617("KEY_FIRE") != 0) ? Devices.method617("KEY_FIRE") : 8);
                break;
            }
        }
        return n2;
    }
    
    private static void method602() {
        final int mid = getArrowKeyFromDevice(8);
        final int up = getArrowKeyFromDevice(1);
        final int left = getArrowKeyFromDevice(2);
        final int down = getArrowKeyFromDevice(6);
        final int right = getArrowKeyFromDevice(5);
        final int s1 = (Devices.method617("KEY_S1") != 0) ? Devices.method617("KEY_S1") : 21;
        final int s2 = (Devices.method617("KEY_S2") != 0) ? Devices.method617("KEY_S2") : 22;
        Keyboard.strToPCKey.clear();
        Keyboard.strToPCKey.put("NUM_0", "48");
        Keyboard.strToPCKey.put("NUM_1", "49");
        Keyboard.strToPCKey.put("NUM_2", "50");
        Keyboard.strToPCKey.put("NUM_3", "51");
        Keyboard.strToPCKey.put("NUM_4", "52");
        Keyboard.strToPCKey.put("NUM_5", "53");
        Keyboard.strToPCKey.put("NUM_6", "54");
        Keyboard.strToPCKey.put("NUM_7", "55");
        Keyboard.strToPCKey.put("NUM_8", "56");
        Keyboard.strToPCKey.put("NUM_9", "57");
        Keyboard.strToPCKey.put("*", "42");
        Keyboard.strToPCKey.put("#", "35");
        Keyboard.strToPCKey.put("UP", String.valueOf(up));
        Keyboard.strToPCKey.put("DOWN", String.valueOf(down));
        Keyboard.strToPCKey.put("LEFT", String.valueOf(left));
        Keyboard.strToPCKey.put("RIGHT", String.valueOf(right));
        Keyboard.strToPCKey.put("MIDDLE", String.valueOf(mid));
        Keyboard.strToPCKey.put("S1", String.valueOf(s1));
        Keyboard.strToPCKey.put("S2", String.valueOf(s2));
    }
    
    private static void method606() {
        Keyboard.deviceKeyToStr.clear();
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[0], "NUM_0");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[1], "NUM_1");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[2], "NUM_2");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[3], "NUM_3");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[4], "NUM_4");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[5], "NUM_5");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[6], "NUM_6");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[7], "NUM_7");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[8], "NUM_8");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[9], "NUM_9");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[10], "*");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[11], "#");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[12], "UP");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[13], "DOWN");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[14], "LEFT");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[15], "RIGHT");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[16], "MIDDLE");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[17], "S1");
        Keyboard.deviceKeyToStr.put(Keyboard.deviceKeycodes[18], "S2");
    }
    
    public static void mapDeviceKey(final int n, final String s) {
        if (s != null) {
            Keyboard.deviceKeycodes[n] = s;
        }
    }
    
    public static boolean isLeftSoft(final int n) {
        return Integer.parseInt(method605(Integer.parseInt(Keyboard.deviceKeycodes[17]))) == n;
    }
    
    public static boolean isRightSoft(final int n) {
        return Integer.parseInt(method605(Integer.parseInt(Keyboard.deviceKeycodes[18]))) == n;
    }
    
    public static int soft1() {
        return Integer.parseInt((String) Keyboard.strToPCKey.get("S1"));
    }
    
    public static int soft2() {
        return Integer.parseInt((String) Keyboard.strToPCKey.get("S2"));
    }
    
    public static void init() {
        method602();
        method606();
    }
    
    public static int fpsKey(int n) {
        if(Settings.fpsGame == 2) {
        	//up
        	if(n == 'w') return getArrowKeyFromDevice(1);
        	//down
        	if(n == 's') return getArrowKeyFromDevice(6);
        	
        	//num1
        	if(n == 'a') return 49;
        	//num3
        	if(n == 'd') return 51;
        	//num7
        	if(n == 'q') return 55;
        	//num9
        	if(n == 'e') return 57;
        	//num0
        	if(n == 'c') return 48;
        	//star
        	if(n == 'x') return 42;
        	//hash
        	if(n == 'z') return 35;
        } else if(Settings.fpsGame == 3) {
        	//up
        	if(n == 'w') return getArrowKeyFromDevice(1);
        	//down
        	if(n == 's') return getArrowKeyFromDevice(6);
        	
        	//num1
        	//if(n == 'a') return 49;
        	//num3
        	//if(n == 'd') return 51;
        	//num7
        	if(n == 'a') return 55;
        	//num9
        	if(n == 'd') return 57;
        	//num0
        	if(n == 'c') return 48;
        	//star
        	if(n == 'x') return 42;
        	//hash
        	if(n == 'z') return 35;
        }
        return 0;
    }
    
    public static String method605(int n) {
        if (n == 80) {
            n = 13;
        }

    	if(Settings.canvasKeyboard && (n >= '0' && n <= '9')) {
        	return "" + n;
    	}
        final Object value;
        if ((value = Keyboard.deviceKeyToStr.get(String.valueOf(n))) != null) {
            return (String)Keyboard.strToPCKey.get(value);
        }
        if(Settings.fpsMode) {
        	int g = fpsKey(n);
        	if(g != 0) return "" + g;
        }
        final String method594;
        if ((method594 = keyToString(String.valueOf(n))) == null) {
            return null;
        }
        final Object value2;
        if ((value2 = Keyboard.aHashtable1067.get(method594)) == null) {
        	if(Settings.canvasKeyboard) {
        		if((n >= 32 && n <= 126) || n == 8) {
                	return "" + n;
        		}
        	}
            return null;
        }
        return (String)value2;
    }
    
    public static void keyArg(final String s) {
        if (s == null) {
            return;
        }
        final String[] split = s.split(";");
        for (int i = 0; i < split.length; ++i) {
            final String[] split2 = split[i].split("=");
            Keyboard.aHashtable1067.put(split2[0], split2[1]);
        }
    }
    
    static {
        Keyboard.deviceKeyToStr = new Hashtable();
        Keyboard.strToPCKey = new Hashtable();
        Keyboard.keysTable = new Hashtable();
        Keyboard.aHashtable1067 = new Hashtable();
        Keyboard.aStack1063 = new Stack();
        Keyboard.keysTable.put("48", "NUM_0");
        Keyboard.keysTable.put("49", "NUM_1");
        Keyboard.keysTable.put("50", "NUM_2");
        Keyboard.keysTable.put("51", "NUM_3");
        Keyboard.keysTable.put("52", "NUM_4");
        Keyboard.keysTable.put("53", "NUM_5");
        Keyboard.keysTable.put("54", "NUM_6");
        Keyboard.keysTable.put("55", "NUM_7");
        Keyboard.keysTable.put("56", "NUM_8");
        Keyboard.keysTable.put("57", "NUM_9");
        Keyboard.keysTable.put("47", "Key /");
        Keyboard.keysTable.put("46", "Key .");
        Keyboard.keysTable.put("42", "Key *");
        Keyboard.keysTable.put("10", "F1");
        Keyboard.keysTable.put("11", "F2");
        Keyboard.keysTable.put("12", "F3");
        Keyboard.keysTable.put("22", "F4");
        Keyboard.keysTable.put("14", "F5");
        Keyboard.keysTable.put("15", "F6");
        Keyboard.keysTable.put("16", "F7");
        Keyboard.keysTable.put("17", "F8");
        Keyboard.keysTable.put("18", "F9");
        Keyboard.keysTable.put("19", "F10");
        Keyboard.keysTable.put("20", "F11");
        Keyboard.keysTable.put("21", "F12");
        Keyboard.keysTable.put("1", "UP");
        Keyboard.keysTable.put("2", "DOWN");
        Keyboard.keysTable.put("3", "LEFT");
        Keyboard.keysTable.put("4", "RIGHT");
        Keyboard.keysTable.put("5", "PageUp");
        Keyboard.keysTable.put("6", "PageDown");
        Keyboard.keysTable.put("7", "Home");
        Keyboard.keysTable.put("8", "End");
        Keyboard.keysTable.put("9", "Insert");
        Keyboard.keysTable.put("13", "Enter");
        Keyboard.keysTable.put("80", "Enter");
        Keyboard.deviceKeycodes = new String[] { "48", "55", "56", "57", "52", "53", "54", "49", "50", "51", "42", "47", "1", "2", "3", "4", "13", "10", "11" };
    }
}
