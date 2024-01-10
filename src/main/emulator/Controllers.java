package emulator;

import java.util.*;
import net.java.games.input.*;

public final class Controllers
{
    private static ArrayList controllers;
    private static ArrayList anArrayList1293;
    private static int count;
    private static boolean loaded;
    private static String[][] binds;
    private static boolean[][] dpad;
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
                    if(s.contains("Gaming Keyboard")) continue;
                    if(s.contains("Gaming Mouse")) continue;
                    list.add(controller);
                }
            }
            System.out.println(list);
            for (int size = list.size(), j = 0; j < size; ++j) {
                addController((Controller) list.get(j));
            }
            Controllers.loaded = true;
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw new Exception("Failed to initialise controllers", t);
        }
    }
    
    private static void addController(final Controller controller) {
        final Controller[] controllers = controller.getControllers();
        if (controllers.length == 0) {
            Controllers.controllers.add(controller);
            ++Controllers.count;
            //l.anArrayList1293.add(null);
            return;
        }
        for (int i = 0; i < controllers.length; ++i) {
        	//System.out.println(controller.toString() + ">PARENT>" + controllers[i]);
            addController(controllers[i]);
        }
    }
    
    public static int getControllersCount() {
        return Controllers.count;
    }
    
    public static Controller getController(final int n) {
        return (Controller) Controllers.controllers.get(n);
    }
    
    private static void initBinds() {
        Controllers.binds = new String[Controllers.count][19];
        for (int i = 0; i < Controllers.count; ++i) {
            Controllers.binds[i][0] = "0";
            Controllers.binds[i][1] = "1";
            Controllers.binds[i][2] = "2";
            Controllers.binds[i][3] = "3";
            Controllers.binds[i][4] = "4";
            Controllers.binds[i][5] = "5";
            Controllers.binds[i][6] = "6";
            Controllers.binds[i][7] = "7";
            Controllers.binds[i][8] = "8";
            Controllers.binds[i][9] = "12";
            Controllers.binds[i][10] = "13";
            Controllers.binds[i][11] = "14";
            Controllers.binds[i][12] = "UP";
            Controllers.binds[i][13] = "DOWN";
            Controllers.binds[i][14] = "LEFT";
            Controllers.binds[i][15] = "RIGHT";
            Controllers.binds[i][16] = "9";
            Controllers.binds[i][17] = "10";
            Controllers.binds[i][18] = "11";
        }
    }
    
    public static void bind(final int controllerId, final int n2, final String s) {
        Controllers.binds[controllerId][n2] = s;
    }
    
    public static String getBind(final int controllerId, final int n2) {
        String string;
        if (!(string = Controllers.binds[controllerId][n2]).equalsIgnoreCase("LEFT") && !string.equalsIgnoreCase("RIGHT") && !string.equalsIgnoreCase("UP") && !string.equalsIgnoreCase("DOWN")) {
            string = "B_" + string;
        }
        return string;
    }
    
    private static int method746(final int n, final String s) {
        int n2;
        for (n2 = 0; n2 < 19 && !s.equalsIgnoreCase(Controllers.binds[n][n2]); ++n2) {}
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
    
    public static void refresh(final boolean b) {
        System.out.println("refresh " + b);
        if (b) {
            try {
                init();
                if (Controllers.count > 0) {
                    Controllers.dpad = new boolean[Controllers.count][4];
                    initBinds();
                }
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        reset();
    }
    
    private static void reset() {
        Controllers.loaded = false;
        Controllers.controllers.clear();
        Controllers.count = 0;
        Controllers.anArrayList1293.clear();
        Controllers.dpad = null;
        Controllers.binds = null;
    }
    
    public static void poll() {
        if (!Controllers.loaded) {
            return;
        }
        if (Controllers.count == 0) {
            return;
        }
        for (int i = 0; i < Controllers.count; ++i) {
            final Controller controller = getController(i);
            try {
                if(!controller.poll()) {

                } else {
                    System.out.println(i + " " + controller);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            final Event event = new Event();
            if (controller.getEventQueue().getNextEvent(event)) {
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
                    System.out.println("button " + method742 + " " + method743);
                    if (!method743 && method742 != 10000) {
                        Emulator.getEventQueue().controllerEvent(n, method742);
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
            Emulator.getEventQueue().controllerEvent(67108864, method746(n, "LEFT"));
            array = Controllers.dpad[n];
            n3 = 0;
            b = true;
        }
        else if (n2 == 1.0f) {
            if (method748("RIGHT")) {
                return;
            }
            Emulator.getEventQueue().controllerEvent(67108864, method746(n, "RIGHT"));
            array = Controllers.dpad[n];
            n3 = 1;
            b = true;
        }
        else {
            if (Controllers.dpad[n][0]) {
                Emulator.getEventQueue().controllerEvent(134217728, method746(n, "LEFT"));
                array = Controllers.dpad[n];
                n3 = 0;
            }
            else {
                if (!Controllers.dpad[n][1]) {
                    return;
                }
                Emulator.getEventQueue().controllerEvent(134217728, method746(n, "RIGHT"));
                array = Controllers.dpad[n];
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
            Emulator.getEventQueue().controllerEvent(67108864, method746(n, "UP"));
            array = Controllers.dpad[n];
            n3 = 2;
            b = true;
        }
        else if (n2 == 1.0f) {
            if (method748("DOWN")) {
                return;
            }
            Emulator.getEventQueue().controllerEvent(67108864, method746(n, "DOWN"));
            array = Controllers.dpad[n];
            n3 = 3;
            b = true;
        }
        else {
            if (Controllers.dpad[n][2]) {
                Emulator.getEventQueue().controllerEvent(134217728, method746(n, "UP"));
                array = Controllers.dpad[n];
                n3 = 2;
            }
            else {
                if (!Controllers.dpad[n][3]) {
                    return;
                }
                Emulator.getEventQueue().controllerEvent(134217728, method746(n, "DOWN"));
                array = Controllers.dpad[n];
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
        Controllers.controllers = new ArrayList();
        Controllers.anArrayList1293 = new ArrayList();
    }
}
