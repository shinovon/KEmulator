package emulator;

import java.util.*;

/*
 * Keyboard
 */
public final class Keyboard
{
    private static Hashtable aHashtable1062;
    private static Hashtable aHashtable1065;
    private static Hashtable keysTable;
    private static Hashtable aHashtable1067;
    public static Stack aStack1063;
    public static String[] aStringArray1064;
    
    public Keyboard() {
        super();
    }
    
    public static String get(final int n) {
        return method594(Keyboard.aStringArray1064[n]);
    }
    
    public static String method594(final String s) {
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
    
    public static int method595(final int n) {
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
        final int method595 = method595(8);
        final int method596 = method595(1);
        final int method597 = method595(2);
        final int method598 = method595(6);
        final int method599 = method595(5);
        final int n = (Devices.method617("KEY_S1") != 0) ? Devices.method617("KEY_S1") : 21;
        final int n2 = (Devices.method617("KEY_S2") != 0) ? Devices.method617("KEY_S2") : 22;
        Keyboard.aHashtable1065.clear();
        Keyboard.aHashtable1065.put("NUM_0", "48");
        Keyboard.aHashtable1065.put("NUM_1", "49");
        Keyboard.aHashtable1065.put("NUM_2", "50");
        Keyboard.aHashtable1065.put("NUM_3", "51");
        Keyboard.aHashtable1065.put("NUM_4", "52");
        Keyboard.aHashtable1065.put("NUM_5", "53");
        Keyboard.aHashtable1065.put("NUM_6", "54");
        Keyboard.aHashtable1065.put("NUM_7", "55");
        Keyboard.aHashtable1065.put("NUM_8", "56");
        Keyboard.aHashtable1065.put("NUM_9", "57");
        Keyboard.aHashtable1065.put("*", "42");
        Keyboard.aHashtable1065.put("#", "35");
        Keyboard.aHashtable1065.put("UP", String.valueOf(method596));
        Keyboard.aHashtable1065.put("DOWN", String.valueOf(method598));
        Keyboard.aHashtable1065.put("LEFT", String.valueOf(method597));
        Keyboard.aHashtable1065.put("RIGHT", String.valueOf(method599));
        Keyboard.aHashtable1065.put("MIDDLE", String.valueOf(method595));
        Keyboard.aHashtable1065.put("S1", String.valueOf(n));
        Keyboard.aHashtable1065.put("S2", String.valueOf(n2));
    }
    
    private static void method606() {
        Keyboard.aHashtable1062.clear();
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[0], "NUM_0");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[1], "NUM_1");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[2], "NUM_2");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[3], "NUM_3");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[4], "NUM_4");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[5], "NUM_5");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[6], "NUM_6");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[7], "NUM_7");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[8], "NUM_8");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[9], "NUM_9");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[10], "*");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[11], "#");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[12], "UP");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[13], "DOWN");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[14], "LEFT");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[15], "RIGHT");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[16], "MIDDLE");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[17], "S1");
        Keyboard.aHashtable1062.put(Keyboard.aStringArray1064[18], "S2");
    }
    
    public static void method596(final int n, final String s) {
        if (s != null) {
            Keyboard.aStringArray1064[n] = s;
        }
    }
    
    public static boolean method597(final int n) {
        return Integer.parseInt(method605(Integer.parseInt(Keyboard.aStringArray1064[17]))) == n;
    }
    
    public static boolean method603(final int n) {
        return Integer.parseInt(method605(Integer.parseInt(Keyboard.aStringArray1064[18]))) == n;
    }
    
    public static int method598() {
        return Integer.parseInt((String) Keyboard.aHashtable1065.get("S1"));
    }
    
    public static int method604() {
        return Integer.parseInt((String) Keyboard.aHashtable1065.get("S2"));
    }
    
    public static void init() {
        method602();
        method606();
    }
    
    public static String method605(int n) {
        if (n == 80 && !Settings.canvasKeyboard) {
            n = 13;
        }
        final Object value;
        if ((value = Keyboard.aHashtable1062.get(String.valueOf(n))) != null) {
            return (String)Keyboard.aHashtable1065.get(value);
        }
        /*
        if(Settings.fpsMode) {
        	//up
        	if(n == 'w') return "" + method595(1);
        	//down
        	if(n == 's') return "" + method595(6);
        	
        	//num1
        	if(n == 'a') return "" + 49;
        	//num3
        	if(n == 'd') return "" + 51;
        	//num7
        	if(n == 'q') return "" + 55;
        	//num9
        	if(n == 'e') return "" + 57;
        	//num0
        	if(n == 'c') return "" + 48;
        	//star
        	if(n == 'x') return "" + 42;
        	//hash
        	if(n == 'z') return "" + 35;
        }
        */
        final String method594;
        if ((method594 = method594(String.valueOf(n))) == null) {
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
        Keyboard.aHashtable1062 = new Hashtable();
        Keyboard.aHashtable1065 = new Hashtable();
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
        Keyboard.aStringArray1064 = new String[] { "48", "55", "56", "57", "52", "53", "54", "49", "50", "51", "42", "47", "1", "2", "3", "4", "13", "10", "11" };
    }
}
