package emulator;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;

import javax.microedition.lcdui.Canvas;
import java.util.ArrayList;

public class Controllers {
	private static final ArrayList controllers = new ArrayList();
	private static int count;
	private static boolean loaded;
	private static String[][] binds;
	private static boolean[][] arrowKeysState;
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
			for (Controller controller : controllers) {
				if (!controller.getType().equals(Controller.Type.KEYBOARD) && !controller.getType().equals(Controller.Type.MOUSE)) {
					String s = controller.getName();
					if (s.contains("tablet")) continue;
					if (s.contains("STAR")) continue;
					if (s.contains("Gaming Keyboard")) continue;
					if (s.contains("Gaming Mouse")) continue;
					if (s.contains("Microphone")) continue;
					list.add(controller);
				}
			}
			for (int size = list.size(), j = 0; j < size; ++j) {
				addController((Controller) list.get(j));
			}
			Controllers.loaded = true;
		} catch (Throwable t) {
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
		for (Controller value : controllers) {
			addController(value);
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
			if (Settings.controllerBinds.containsKey(name + ".0")) {
				for (int j = 0; j < 19; j++) {
					String s = Settings.controllerBinds.get(name + "." + j);
					if (s != null) Controllers.binds[i][j] = s;
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
		int n2 = 0;
		while (n2 < 19 && !s.equalsIgnoreCase(Controllers.binds[n][n2]))
			++n2;
		if (n2 == 19) {
			return 10000;
		}
		return method747(KeyMapping.deviceKeycodes[n2]);
	}

	private static int method747(String s) {
		if (s == null || s.isEmpty() || (s = KeyMapping.replaceKey(Integer.parseInt(s))) == null)
			return 10000;
		return Integer.parseInt(s);
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
					Controllers.arrowKeysState = new boolean[Controllers.count][8];
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
		Controllers.arrowKeysState = null;
		Controllers.binds = null;
	}

	public static void poll() {
		if (!Controllers.loaded || Controllers.count == 0) {
			return;
		}
		for (int i = 0; i < Controllers.count; ++i) {
			Controller controller = getController(i);
			if (!controller.poll()) {
				continue;
			}
			Event event = new Event();
			while (controller.getEventQueue().getNextEvent(event)) {
				Component.Identifier identifier = event.getComponent().getIdentifier();
				String name = identifier.getName();
				float value = event.getValue();
				if (identifier instanceof Component.Identifier.Button) {
					boolean b = method748(name);
					int key = map(i, name);
					if (key == 10000) return;
					if (value == 1.0f) {
						if (!b) Emulator.getEventQueue().keyPress(key);
					} else {
						Emulator.getEventQueue().keyRelease(key);
					}
				} else if (identifier.equals(Component.Identifier.Axis.POV)) {
					handleX(i, povX(value), true);
					handleY(i, povY(value), true);
				} else {
					if (Math.abs(value) < 0.05f) {
						value = 0.0f;
					}
					if (name.equalsIgnoreCase("x")) {
						filterX(i, value, true);
						continue;
					}
					if (name.equalsIgnoreCase("y")) {
						filterY(i, value, true);
						continue;
					}
					if (name.equalsIgnoreCase("z")) {
						filterX(i, value, false);
						continue;
					}
					if (name.equalsIgnoreCase("rz")) {
						filterY(i, value, false);
						continue;
					}
				}
			}
		}
	}

	private static void filterX(int n, float f, boolean pov) {
		if (Math.abs(f) <= 0.05f && Math.abs(lastX) <= 0.05f) return;
		handleX(n, f, pov);
		lastX = f;
	}

	private static void filterY(int n, float f, boolean pov) {
		if (Math.abs(f) <= 0.05f && Math.abs(lastY) <= 0.05f) return;
		handleY(n, f, pov);
		lastY = f;
	}

	private static void handleX(int n, float f, boolean pov) {
		if (pov) {
			if (f < -0.7f) {
				if (method748("LEFT")) return;
				if (Controllers.arrowKeysState[n][0])
					return;
				Emulator.getEventQueue().keyPress(map(n, "LEFT"));
				Controllers.arrowKeysState[n][0] = true;
			} else if (f > 0.7f) {
				if (method748("RIGHT")) return;
				if (Controllers.arrowKeysState[n][1])
					return;
				Emulator.getEventQueue().keyPress(map(n, "RIGHT"));
				Controllers.arrowKeysState[n][1] = true;
			} else if (Controllers.arrowKeysState[n][0]) {
				Emulator.getEventQueue().keyRelease(map(n, "LEFT"));
				Controllers.arrowKeysState[n][0] = false;
			} else if (Controllers.arrowKeysState[n][1]) {
				Emulator.getEventQueue().keyRelease(map(n, "RIGHT"));
				Controllers.arrowKeysState[n][1] = false;
			}
			return;
		}
		if (f < -0.7f) {
			if (Controllers.arrowKeysState[n][4])
				return;
			Emulator.getEventQueue().keyPress(KeyMapping.getArrowKeyFromDevice(Canvas.LEFT));
			Controllers.arrowKeysState[n][4] = true;
		} else if (f > 0.7f) {
			if (Controllers.arrowKeysState[n][5])
				return;
			Emulator.getEventQueue().keyPress(KeyMapping.getArrowKeyFromDevice(Canvas.RIGHT));
			Controllers.arrowKeysState[n][5] = true;
		} else if (Controllers.arrowKeysState[n][4]) {
			Emulator.getEventQueue().keyRelease(KeyMapping.getArrowKeyFromDevice(Canvas.LEFT));
			Controllers.arrowKeysState[n][4] = false;
		} else if (Controllers.arrowKeysState[n][5]) {
			Emulator.getEventQueue().keyRelease(KeyMapping.getArrowKeyFromDevice(Canvas.RIGHT));
			Controllers.arrowKeysState[n][5] = false;
		}
	}

	private static void handleY(int n, float f, boolean pov) {
		if (pov) {
			if (f < -0.7f) {
				if (method748("UP")) return;
				if (Controllers.arrowKeysState[n][2])
					return;
				Emulator.getEventQueue().keyPress(map(n, "UP"));
				Controllers.arrowKeysState[n][2] = true;
			} else if (f > 0.7f) {
				if (method748("DOWN")) return;
				if (Controllers.arrowKeysState[n][3])
					return;
				Emulator.getEventQueue().keyPress(map(n, "DOWN"));
				Controllers.arrowKeysState[n][3] = true;
			} else if (Controllers.arrowKeysState[n][2]) {
				Emulator.getEventQueue().keyRelease(map(n, "UP"));
				Controllers.arrowKeysState[n][2] = false;
			} else if (Controllers.arrowKeysState[n][3]) {
				Emulator.getEventQueue().keyRelease(map(n, "DOWN"));
				Controllers.arrowKeysState[n][3] = false;
			}
			return;
		}
		if (f < -0.7f) {
			if (method748("UP")) return;
			if (Controllers.arrowKeysState[n][6])
				return;
			Emulator.getEventQueue().keyPress(KeyMapping.getArrowKeyFromDevice(Canvas.UP));
			Controllers.arrowKeysState[n][6] = true;
		} else if (f > 0.7f) {
			if (method748("DOWN")) return;
			if (Controllers.arrowKeysState[n][7])
				return;
			Emulator.getEventQueue().keyPress(KeyMapping.getArrowKeyFromDevice(Canvas.DOWN));
			Controllers.arrowKeysState[n][7] = true;
		} else if (Controllers.arrowKeysState[n][6]) {
			Emulator.getEventQueue().keyRelease(KeyMapping.getArrowKeyFromDevice(Canvas.UP));
			Controllers.arrowKeysState[n][6] = false;
		} else if (Controllers.arrowKeysState[n][7]) {
			Emulator.getEventQueue().keyRelease(KeyMapping.getArrowKeyFromDevice(Canvas.DOWN));
			Controllers.arrowKeysState[n][7] = false;
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
