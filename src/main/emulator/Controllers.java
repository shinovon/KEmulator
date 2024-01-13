package emulator;

import java.util.*;

import emulator.ui.swt.Property;
import net.java.games.input.*;

public class Controllers
{
    private static ArrayList controllers = new ArrayList();
    private static int count;
    private static boolean loaded;
    private static String[][] binds;
    private static boolean[][] dpad;
    private static String aString1292;
    private static float lastX;
    private static float lastY;

    public Controllers() {
        super();
    }
    
    private static void init() throws Exception {
        if (Controllers.loaded) {
            return;
        }
        try {
            Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
            ArrayList list = new ArrayList<Controller>();
            for (int i = 0; i < controllers.length; ++i) {
                Controller controller;
                if (!(controller = controllers[i]).getType().equals(Controller.Type.KEYBOARD) && !controller.getType().equals(Controller.Type.MOUSE)) {
                	String s = controller.getName();
                	if(s.contains("tablet")) continue;
                	if(s.contains("STAR")) continue;
                    if(s.contains("Gaming Keyboard")) continue;
                    if(s.contains("Gaming Mouse")) continue;
                    list.add(controller);
                }
            }
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
    
    private static void addController(Controller controller) {
        Controller[] controllers = controller.getControllers();
        if (controllers.length == 0) {
            Emulator.getEmulator().getLogStream().println("Found controller: " + controller);
            Controllers.controllers.add(controller);
            ++Controllers.count;
            return;
        }
        for (int i = 0; i < controllers.length; ++i) {
            addController(controllers[i]);
        }
    }
    
    public static int getControllersCount() {
        return Controllers.count;
    }
    
    public static Controller getController(int n) {
        return (Controller) Controllers.controllers.get(n);
    }
    
    private static void initBinds() {
        Controllers.binds = new String[Controllers.count][19];
        for (int i = 0; i < Controllers.count; ++i) {
            String name = ((Controller) controllers.get(i)).getName();
            Controllers.binds[i][0] = "";
            Controllers.binds[i][1] = "";
            Controllers.binds[i][2] = "";
            Controllers.binds[i][3] = "";
            Controllers.binds[i][4] = "";
            Controllers.binds[i][5] = "";
            Controllers.binds[i][6] = "";
            Controllers.binds[i][7] = "";
            Controllers.binds[i][8] = "";
            Controllers.binds[i][9] = "";
            Controllers.binds[i][10] = "";
            Controllers.binds[i][11] = "";
            Controllers.binds[i][12] = "UP";
            Controllers.binds[i][13] = "DOWN";
            Controllers.binds[i][14] = "LEFT";
            Controllers.binds[i][15] = "RIGHT";
            Controllers.binds[i][16] = "9";
            Controllers.binds[i][17] = "10";
            Controllers.binds[i][18] = "11";
            if(Settings.controllerBinds.containsKey(name + ".0")) {
                for(int j = 0; j < 19; j++) {
                    String s = Settings.controllerBinds.get(name + "." + j);
                    if(s != null) Controllers.binds[i][j] = s;
                }
            }
        }
    }
    
    public static void bind(int controllerId, int n2, String s) {
        Controllers.binds[controllerId][n2] = s;
        try {
            Settings.controllerBinds.put(((Controller) controllers.get(controllerId)).getName() + "." + n2, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getBind(int controllerId, int n2) {
        String s = Controllers.binds[controllerId][n2];
        if (!s.isEmpty() && !s.equalsIgnoreCase("LEFT") && !s.equalsIgnoreCase("RIGHT") && !s.equalsIgnoreCase("UP") && !s.equalsIgnoreCase("DOWN")) {
            s = "B_" + s;
        }
        return s;
    }
    
    private static int map(int n, String s) {
        int n2;
        for (n2 = 0; n2 < 19 && !s.equalsIgnoreCase(Controllers.binds[n][n2]); ++n2);
        if (n2 == 19) {
            return 10000;
        }
        return method747(Keyboard.deviceKeycodes[n2]);
    }
    
    private static int method747(String s) {
        return Integer.parseInt(Keyboard.replaceKey(Integer.parseInt(s)));
    }
    
    private static boolean method748(String aString1292) {
        Controllers.aString1292 = aString1292;
        return Emulator.getEmulator().getProperty().updateController();
    }
    
    public static String method749() {
        return Controllers.aString1292;
    }
    
    public static void refresh(boolean b) {
        reset();
        if (b) {
            try {
                init();
                if (Controllers.count > 0) {
                    Controllers.dpad = new boolean[Controllers.count][4];
                    initBinds();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                reset();
            }
        }
    }
    
    private static void reset() {
        Controllers.loaded = false;
        Controllers.controllers.clear();
        Controllers.count = 0;
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
            Controller controller = getController(i);
            if(!controller.poll()) {
                continue;
            }
            Event event = new Event();
            if (controller.getEventQueue().getNextEvent(event)) {
                Component.Identifier identifier = event.getComponent().getIdentifier();
                String name = identifier.getName();
                float value = event.getValue();
                if (identifier instanceof Component.Identifier.Button) {
                    boolean b = method748(name);
                    int key = map(i, name);
                    if (key == 10000) return;
                    if (value == 1.0f) {
                        if(!b) Emulator.getEventQueue().keyPress(key);
                    } else {
                        Emulator.getEventQueue().keyRelease(key);
                    }
                } else if (identifier.equals(Component.Identifier.Axis.POV)) {
                    handleX(i, povX(value));
                    handleY(i, povY(value));
                } else {
                    if (Math.abs(value) < 0.05f) {
                        value = 0.0f;
                    }
                    if (name.equalsIgnoreCase("x")) {
                        filterX(i, value);
                        continue;
                    }
                    if (name.equalsIgnoreCase("y")) {
                        filterY(i, value);
                        continue;
                    }
                }
            }
        }
    }
    
    private static void filterX(int n, float f) {
        if(Math.abs(f) <= 0.05f && Math.abs(lastX) <= 0.05f) return;
        handleX(n, f);
        lastX = f;
    }

    private static void handleX(int n, float f) {
        if (f == -1.0f) {
            if (method748("LEFT")) return;
            Emulator.getEventQueue().keyPress(map(n, "LEFT"));
            Controllers.dpad[n][0] = true;
        } else if (f == 1.0f) {
            if (method748("RIGHT")) return;
            Emulator.getEventQueue().keyPress(map(n, "RIGHT"));
            Controllers.dpad[n][1] = true;
        } else if (Controllers.dpad[n][0]) {
            Emulator.getEventQueue().keyRelease(map(n, "LEFT"));
            Controllers.dpad[n][0] = false;
        } else if (Controllers.dpad[n][1]) {
            Emulator.getEventQueue().keyRelease(map(n, "RIGHT"));
            Controllers.dpad[n][1] = false;
        }
    }
    
    private static void filterY(int n, float f) {
        if(Math.abs(f) <= 0.05f && Math.abs(lastY) <= 0.05f) return;
        handleY(n, f);
        lastY = f;
    }

    private static void handleY(int n, float f) {
        if (f == -1.0f) {
            if (method748("UP")) return;
            Emulator.getEventQueue().keyPress(map(n, "UP"));
            Controllers.dpad[n][2] = true;
        } else if (f == 1.0f) {
            if (method748("DOWN")) return;
            Emulator.getEventQueue().keyPress(map(n, "DOWN"));
            Controllers.dpad[n][3] = true;
        } else if (Controllers.dpad[n][2]) {
            Emulator.getEventQueue().keyRelease(map(n, "UP"));
            Controllers.dpad[n][2] = false;
        } else if (Controllers.dpad[n][3]) {
            Emulator.getEventQueue().keyRelease(map(n, "DOWN"));
            Controllers.dpad[n][3] = false;
        }
    }
    
    private static float povX(float n) {
        if (n == 0.875f || n == 0.125f || n == 1.0f) {
            return -1.0f;
        }
        if (n == 0.625f || n == 0.375f || n == 0.5f) {
            return 1.0f;
        }
        return 0.0f;
    }
    
    private static float povY(float n) {
        if (n == 0.875f || n == 0.625f || n == 0.75f) {
            return 1.0f;
        }
        if (n == 0.125f || n == 0.375f || n == 0.25f) {
            return -1.0f;
        }
        return 0.0f;
    }
}
