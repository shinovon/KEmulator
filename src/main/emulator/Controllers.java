package emulator;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;

import javax.microedition.lcdui.Canvas;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Set;

public class Controllers {
	private static final ArrayList controllers = new ArrayList();
	private static int count;
	private static boolean loaded;
	private static String[][] binds;
	private static boolean[][] arrowKeysState;
	private static String bind;
	private static float[][] axisState;

	private static final int AXIS_POV = -1;
	private static final int AXIS_X = 0;
	private static final int AXIS_Y = 1;
	private static final int AXIS_Z = 2;
	private static final int AXIS_RZ = 3;
	private static final int AXIS_RX = 4;
	private static final int AXIS_RY = 5;
	private static final int AXIS_POV_X = 6;
	private static final int AXIS_POV_Y = 7;

	public Controllers() {
		super();
	}

	private static void init() throws Exception {
		if (Controllers.loaded) {
			return;
		}
		try {
			Controller[] controllers = getDefaultEnvironment().getControllers();
			ArrayList list = new ArrayList<Controller>();
			for (Controller controller : controllers) {
				if (!controller.getType().equals(Controller.Type.KEYBOARD)
						&& !controller.getType().equals(Controller.Type.MOUSE)) {
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

			// TODO remove
			if (name.toLowerCase().contains("xbox")) {
				Settings.controllerZMap = 5;
				Settings.controllerRZMap = 5;
			}

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
		if (s.startsWith("Axis")) {
			return s;
		}
		if (!s.isEmpty() && !s.equalsIgnoreCase("LEFT")
				&& !s.equalsIgnoreCase("RIGHT")
				&& !s.equalsIgnoreCase("UP")
				&& !s.equalsIgnoreCase("DOWN")) {
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

	private static boolean bindChange(String aString1292) {
		Controllers.bind = aString1292;
		return Emulator.getEmulator().getProperty().updateController();
	}

	public static String method749() {
		return Controllers.bind;
	}

	public static void refresh(boolean b) {
		reset();
		if (b) {
			try {
				init();
				if (Controllers.count > 0) {
					Controllers.arrowKeysState = new boolean[Controllers.count][8];
					Controllers.axisState = new float[Controllers.count][8];
					initBinds();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				reset();
			}
		}
	}

	private static ControllerEnvironment getDefaultEnvironment() throws Exception {
		if (Utils.isJava9()) {
			return ControllerEnvironment.getDefaultEnvironment();
		}
		Constructor<ControllerEnvironment> constructor = (Constructor<ControllerEnvironment>)
				Class.forName("net.java.games.input.DefaultControllerEnvironment").getDeclaredConstructors()[0];
		constructor.setAccessible(true);
		return constructor.newInstance();
	}

	private static void reset() {
		Controllers.loaded = false;
		Controllers.controllers.clear();
		Controllers.count = 0;
		Controllers.arrowKeysState = null;
		Controllers.binds = null;

		try {
			Set<Thread> threads = Thread.getAllStackTraces().keySet();
			for (Thread thread : threads) {
				if (thread.getClass().getName().equals("net.java.games.input.RawInputEventQueue$QueueThread")) {
					thread.interrupt();
					try {
						thread.join();
					} catch (InterruptedException e) {
						thread.interrupt();
					}
				}
			}
		} catch (Exception ignored) {}
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
					int key = map(i, name);
					if (value == 1.0f) {
						if (bindChange(name) || key == 10000)
							continue;
						dispatchKeyEvent(key, true);
					} else if (key != 10000) {
						dispatchKeyEvent(key, false);
					}
				} else if (identifier.equals(Component.Identifier.Axis.POV)) {
					filter(i, value, AXIS_POV);
				} else {
					if (Math.abs(value) < 0.05f) {
						value = 0.0f;
					}
					if (name.equalsIgnoreCase("x")) {
						filter(i, value, AXIS_X);
						continue;
					}
					if (name.equalsIgnoreCase("y")) {
						filter(i, value, AXIS_Y);
						continue;
					}
					if (name.equalsIgnoreCase("z")) {
						filter(i, value, AXIS_Z);
						continue;
					}
					if (name.equalsIgnoreCase("rz")) {
						filter(i, value, AXIS_RZ);
						continue;
					}
					if (name.equalsIgnoreCase("rx")) {
						filter(i, value, AXIS_RX);
						continue;
					}
					if (name.equalsIgnoreCase("ry")) {
						filter(i, value, AXIS_RY);
						continue;
					}
				}
			}
		}
	}

	private static void filter(int n, float f, int axis) {
		int mode = 0;
		switch (axis) {
			case AXIS_POV:
				filter(n, povX(f), AXIS_POV_X);
				filter(n, povY(f), AXIS_POV_Y);
				return;
			case AXIS_X:
				mode = Settings.controllerXMap;
				break;
			case AXIS_Y:
				mode = Settings.controllerYMap;
				break;
			case AXIS_Z:
				mode = Settings.controllerZMap;
				break;
			case AXIS_RX:
				mode = Settings.controllerRXMap;
				break;
			case AXIS_RY:
				mode = Settings.controllerRYMap;
				break;
			case AXIS_RZ:
				mode = Settings.controllerRZMap;
				break;
			case AXIS_POV_X:
				mode = Settings.controllerPovXMap;
				break;
			case AXIS_POV_Y:
				mode = Settings.controllerPovYMap;
				break;
		}
		if (Math.abs(f) <= Settings.axisFilter && Math.abs(axisState[n][axis]) <= Settings.axisFilter)
			return;
		switch (mode) {
			case 0: // ignore
				break;
			case 1: // map horizontal
				horizontal(n, f, true);
				break;
			case 2: // map vertical
				vertical(n, f, true);
				break;
			case 3: // direct horizontal
				horizontal(n, f, false);
				break;
			case 4: // direct vertical
				vertical(n, f, false);
				break;
			case 5: // map to button
				String name = null;
				switch (axis) {
					case AXIS_X:
						name = "Axis_X";
						break;
					case AXIS_Y:
						name = "Axis_Y";
						break;
					case AXIS_Z:
						name = "Axis_Z";
						break;
					case AXIS_RX:
						name = "Axis_RX";
						break;
					case AXIS_RY:
						name = "Axis_RY";
						break;
					case AXIS_RZ:
						name = "Axis_RZ";
						break;
					case AXIS_POV_X:
						name = "Axis_POV_X";
						break;
					case AXIS_POV_Y:
						name = "Axis_POV_Y";
						break;
					default:
						break;
				}
				int key = map(n, name);

				if (f >= Settings.axisThreshold) {
					if (bindChange(name)
							|| key == 10000
							|| axisState[n][axis] >= Settings.axisThreshold)
						break;

					dispatchKeyEvent(key, true);
				} else {
					if (axisState[n][axis] < Settings.axisThreshold
							|| key == 10000)
						break;

					dispatchKeyEvent(key, false);
				}
				break;
		}
		axisState[n][axis] = f;
	}

	private static void horizontal(int n, float f, boolean map) {
		if (Settings.controllerInverseHor) f = -f;
		if (map) {
			if (f <= -Settings.axisThreshold) {
				if (bindChange("LEFT")) return;
				if (Controllers.arrowKeysState[n][0])
					return;
				dispatchKeyEvent(map(n, "LEFT"), true);
				Controllers.arrowKeysState[n][0] = true;
			} else if (f >= Settings.axisThreshold) {
				if (bindChange("RIGHT")) return;
				if (Controllers.arrowKeysState[n][1])
					return;
				dispatchKeyEvent(map(n, "RIGHT"), true);
				Controllers.arrowKeysState[n][1] = true;
			} else if (Controllers.arrowKeysState[n][0]) {
				dispatchKeyEvent(map(n, "LEFT"), false);
				Controllers.arrowKeysState[n][0] = false;
			} else if (Controllers.arrowKeysState[n][1]) {
				dispatchKeyEvent(map(n, "RIGHT"), false);
				Controllers.arrowKeysState[n][1] = false;
			}
			return;
		}
		if (f <= -Settings.axisThreshold) {
			if (Controllers.arrowKeysState[n][4])
				return;
			dispatchKeyEvent(KeyMapping.getArrowKeyFromDevice(Canvas.LEFT), true);
			Controllers.arrowKeysState[n][4] = true;
		} else if (f >= Settings.axisThreshold) {
			if (Controllers.arrowKeysState[n][5])
				return;
			dispatchKeyEvent(KeyMapping.getArrowKeyFromDevice(Canvas.RIGHT), true);
			Controllers.arrowKeysState[n][5] = true;
		} else if (Controllers.arrowKeysState[n][4]) {
			dispatchKeyEvent(KeyMapping.getArrowKeyFromDevice(Canvas.LEFT), false);
			Controllers.arrowKeysState[n][4] = false;
		} else if (Controllers.arrowKeysState[n][5]) {
			dispatchKeyEvent(KeyMapping.getArrowKeyFromDevice(Canvas.RIGHT), false);
			Controllers.arrowKeysState[n][5] = false;
		}
	}

	private static void vertical(int n, float f, boolean map) {
		if (Settings.controllerInverseVer) f = -f;
		if (map) {
			if (f <= -Settings.axisThreshold) {
				if (bindChange("UP")) return;
				if (Controllers.arrowKeysState[n][2])
					return;
				dispatchKeyEvent(map(n, "UP"), true);
				Controllers.arrowKeysState[n][2] = true;
			} else if (f >= Settings.axisThreshold) {
				if (bindChange("DOWN")) return;
				if (Controllers.arrowKeysState[n][3])
					return;
				dispatchKeyEvent(map(n, "DOWN"), true);
				Controllers.arrowKeysState[n][3] = true;
			} else if (Controllers.arrowKeysState[n][2]) {
				dispatchKeyEvent(map(n, "UP"), false);
				Controllers.arrowKeysState[n][2] = false;
			} else if (Controllers.arrowKeysState[n][3]) {
				dispatchKeyEvent(map(n, "DOWN"), false);
				Controllers.arrowKeysState[n][3] = false;
			}
			return;
		}
		if (f <= -Settings.axisThreshold) {
			if (Controllers.arrowKeysState[n][6])
				return;
			dispatchKeyEvent(KeyMapping.getArrowKeyFromDevice(Canvas.UP), true);
			Controllers.arrowKeysState[n][6] = true;
		} else if (f >= Settings.axisThreshold) {
			if (Controllers.arrowKeysState[n][7])
				return;
			dispatchKeyEvent(KeyMapping.getArrowKeyFromDevice(Canvas.DOWN), true);
			Controllers.arrowKeysState[n][7] = true;
		} else if (Controllers.arrowKeysState[n][6]) {
			dispatchKeyEvent(KeyMapping.getArrowKeyFromDevice(Canvas.UP), false);
			Controllers.arrowKeysState[n][6] = false;
		} else if (Controllers.arrowKeysState[n][7]) {
			dispatchKeyEvent(KeyMapping.getArrowKeyFromDevice(Canvas.DOWN), false);
			Controllers.arrowKeysState[n][7] = false;
		}
	}
	
	private static void dispatchKeyEvent(int key, boolean state) {
		EventQueue queue = Emulator.getEventQueue();
		if (queue == null || key == 10000) return;
		
		if (state) {
			queue.keyPress(key);
		} else {
			queue.keyRelease(key);
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
