package emulator;

import java.util.*;
import net.java.games.input.*;

public final class Controllers
{
    private static ArrayList anArrayList1287;
    private static ArrayList anArrayList1293;
    private static int count;
    private static boolean loaded;
    private static String[][] stringMultiArr;
    private static boolean[][] aBooleanArrayArray1291;
    private static String aString1292;
    
    public Controllers() {
        super();
    }
    
    private static void init() throws Exception {
        if (Controllers.loaded) {
            return;
        }
        try {
            final Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
            final ArrayList list = new ArrayList<Controller>();
        	//System.out.println("Controllers");
            for (int i = 0; i < controllers.length; ++i) {
                final Controller controller;
                if (!(controller = controllers[i]).getType().equals(Controller.Type.KEYBOARD) && !controller.getType().equals(Controller.Type.MOUSE)) {
                	//System.out.println(controller.toString());
                	String s = controller.getName();
                	if(s.contains("tablet")) continue;
                	if(s.contains("STAR")) continue;
                    list.add(controller);
                }
            }
            for (int size = list.size(), j = 0; j < size; ++j) {
                method739((Controller) list.get(j));
            }
            Controllers.loaded = true;
        }
        catch (Throwable t) {t.printStackTrace();
            throw new Exception("Failed to initialise controllers", t);
        }
    }
    
    private static void method739(final Controller controller) {
        final Controller[] controllers;
        if ((controllers = controller.getControllers()).length == 0) {
            Controllers.anArrayList1287.add(controller);
            ++Controllers.count;
            //l.anArrayList1293.add(null);
            return;
        }
        for (int i = 0; i < controllers.length; ++i) {
        	//System.out.println(controller.toString() + ">PARENT>" + controllers[i]);
            method739(controllers[i]);
        }
    }
    
    public static int method740() {
        return Controllers.count;
    }
    
    public static Controller method741(final int n) {
        return (Controller) Controllers.anArrayList1287.get(n);
    }
    
    private static void method755() {
        Controllers.stringMultiArr = new String[Controllers.count][19];
        for (int i = 0; i < Controllers.count; ++i) {
            Controllers.stringMultiArr[i][0] = "0";
            Controllers.stringMultiArr[i][1] = "1";
            Controllers.stringMultiArr[i][2] = "2";
            Controllers.stringMultiArr[i][3] = "3";
            Controllers.stringMultiArr[i][4] = "4";
            Controllers.stringMultiArr[i][5] = "5";
            Controllers.stringMultiArr[i][6] = "6";
            Controllers.stringMultiArr[i][7] = "7";
            Controllers.stringMultiArr[i][8] = "8";
            Controllers.stringMultiArr[i][9] = "12";
            Controllers.stringMultiArr[i][10] = "13";
            Controllers.stringMultiArr[i][11] = "14";
            Controllers.stringMultiArr[i][12] = "UP";
            Controllers.stringMultiArr[i][13] = "DOWN";
            Controllers.stringMultiArr[i][14] = "LEFT";
            Controllers.stringMultiArr[i][15] = "RIGHT";
            Controllers.stringMultiArr[i][16] = "9";
            Controllers.stringMultiArr[i][17] = "10";
            Controllers.stringMultiArr[i][18] = "11";
        }
    }
    
    public static void method743(final int n, final int n2, final String s) {
        Controllers.stringMultiArr[n][n2] = s;
    }
    
    public static String method744(final int n, final int n2) {
        String string;
        if (!(string = Controllers.stringMultiArr[n][n2]).equalsIgnoreCase("LEFT") && !string.equalsIgnoreCase("RIGHT") && !string.equalsIgnoreCase("UP") && !string.equalsIgnoreCase("DOWN")) {
            string = "B_" + string;
        }
        return string;
    }
    
    private static int method746(final int n, final String s) {
        int n2;
        for (n2 = 0; n2 < 19 && !s.equalsIgnoreCase(Controllers.stringMultiArr[n][n2]); ++n2) {}
        if (n2 == 19) {
            return 10000;
        }
        return method747(Keyboard.deviceKeycodes[n2]);
    }
    
    private static int method747(final String s) {
        return Integer.parseInt(Keyboard.replaceKey(Integer.parseInt(s)));
    }
    
    private static boolean method748(final String aString1292) {
        Controllers.aString1292 = aString1292;
        return Emulator.getEmulator().getProperty().updateController();
    }
    
    public static String method749() {
        return Controllers.aString1292;
    }
    
    public static void method750(final boolean b) {
        if (b) {
            try {
                init();
                if (Controllers.count > 0) {
                    Controllers.aBooleanArrayArray1291 = new boolean[Controllers.count][4];
                    method755();
                }
                return;
            }
            catch (Exception ex) {ex.printStackTrace();
                Emulator.getEmulator().getLogStream().println("6 "+ex.toString());
            }
        }
        method756();
    }
    
    private static void method756() {
        Controllers.loaded = false;
        Controllers.anArrayList1287.clear();
        Controllers.count = 0;
        Controllers.anArrayList1293.clear();
        Controllers.aBooleanArrayArray1291 = null;
        Controllers.stringMultiArr = null;
    }
    
    public static void method751() {
        if (!Controllers.loaded) {
            return;
        }
        if (Controllers.count == 0) {
            return;
        }
        for (int i = 0; i < Controllers.count; ++i) {
            final Controller method741;
            (method741 = method741(i)).poll();
            final Event event = new Event();
            if (method741.getEventQueue().getNextEvent(event)) {
                final Component.Identifier identifier;
                final String name = (identifier = event.getComponent().getIdentifier()).getName();
                float value = event.getValue();
                if (identifier instanceof Component.Identifier.Button) {
                    final int n = (event.getValue() == 1.0f) ? 67108864 : 134217728;
                    final int method742 = method746(i, identifier.getName());
                    boolean method743 = false;
                    if (n == 67108864) {
                        method743 = method748(identifier.getName());
                    }
                    if (!method743 && method742 != 10000) {
                        Emulator.getEventQueue().method717(n, method742);
                    }
                }
                else {
                    int n2;
                    float n3;
                    if (identifier.equals(Component.Identifier.Axis.POV)) {
                        final float method744 = method745(value);
                        final float method745 = method754(value);
                        method742(i, method744);
                        n2 = i;
                        n3 = method745;
                    }
                    else {
                        if (Math.abs(value) < 0.05f) {
                            value = 0.0f;
                        }
                        if (name.equalsIgnoreCase("x")) {
                            method742(i, value);
                            continue;
                        }
                        if (!name.equalsIgnoreCase("y")) {
                            continue;
                        }
                        n2 = i;
                        n3 = value;
                    }
                    method753(n2, n3);
                }
            }
        }
    }
    
    private static void method742(final int n, final float n2) {
        boolean[] array;
        int n3;
        boolean b;
        if (n2 == -1.0f) {
            if (method748("LEFT")) {
                return;
            }
            Emulator.getEventQueue().method717(67108864, method746(n, "LEFT"));
            array = Controllers.aBooleanArrayArray1291[n];
            n3 = 0;
            b = true;
        }
        else if (n2 == 1.0f) {
            if (method748("RIGHT")) {
                return;
            }
            Emulator.getEventQueue().method717(67108864, method746(n, "RIGHT"));
            array = Controllers.aBooleanArrayArray1291[n];
            n3 = 1;
            b = true;
        }
        else {
            if (Controllers.aBooleanArrayArray1291[n][0]) {
                Emulator.getEventQueue().method717(134217728, method746(n, "LEFT"));
                array = Controllers.aBooleanArrayArray1291[n];
                n3 = 0;
            }
            else {
                if (!Controllers.aBooleanArrayArray1291[n][1]) {
                    return;
                }
                Emulator.getEventQueue().method717(134217728, method746(n, "RIGHT"));
                array = Controllers.aBooleanArrayArray1291[n];
                n3 = 1;
            }
            b = false;
        }
        array[n3] = b;
    }
    
    private static void method753(final int n, final float n2) {
        boolean[] array;
        int n3;
        boolean b;
        if (n2 == -1.0f) {
            if (method748("UP")) {
                return;
            }
            Emulator.getEventQueue().method717(67108864, method746(n, "UP"));
            array = Controllers.aBooleanArrayArray1291[n];
            n3 = 2;
            b = true;
        }
        else if (n2 == 1.0f) {
            if (method748("DOWN")) {
                return;
            }
            Emulator.getEventQueue().method717(67108864, method746(n, "DOWN"));
            array = Controllers.aBooleanArrayArray1291[n];
            n3 = 3;
            b = true;
        }
        else {
            if (Controllers.aBooleanArrayArray1291[n][2]) {
                Emulator.getEventQueue().method717(134217728, method746(n, "UP"));
                array = Controllers.aBooleanArrayArray1291[n];
                n3 = 2;
            }
            else {
                if (!Controllers.aBooleanArrayArray1291[n][3]) {
                    return;
                }
                Emulator.getEventQueue().method717(134217728, method746(n, "DOWN"));
                array = Controllers.aBooleanArrayArray1291[n];
                n3 = 3;
            }
            b = false;
        }
        array[n3] = b;
    }
    
    private static float method745(final float n) {
        if (n == 0.875f || n == 0.125f || n == 1.0f) {
            return -1.0f;
        }
        if (n == 0.625f || n == 0.375f || n == 0.5f) {
            return 1.0f;
        }
        return 0.0f;
    }
    
    private static float method754(final float n) {
        if (n == 0.875f || n == 0.625f || n == 0.75f) {
            return 1.0f;
        }
        if (n == 0.125f || n == 0.375f || n == 0.25f) {
            return -1.0f;
        }
        return 0.0f;
    }
    
    static {
        Controllers.anArrayList1287 = new ArrayList();
        Controllers.anArrayList1293 = new ArrayList();
    }
}
