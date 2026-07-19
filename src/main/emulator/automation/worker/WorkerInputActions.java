package emulator.automation.worker;

import emulator.Emulator;
import emulator.EventQueue;
import emulator.KeyMapping;
import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationException;
import javax.microedition.lcdui.Canvas;
import mjson.Json;

final class WorkerInputActions {
	private WorkerInputActions() {
	}

	static int resolveKeyCode(String key, Json codeValue) {
		if (codeValue != null && !codeValue.isNull()) {
			try {
				return codeValue.asInteger();
			} catch (RuntimeException ignored) {
			}
		}

		if (key == null) {
			throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "press-key requires key or code");
		}

		String normalized = key.trim().toUpperCase().replace('-', '_');
		if (normalized.startsWith("NUM") && normalized.length() == 4) {
			return Character.forDigit(normalized.charAt(3) - '0', 10);
		}

		if (normalized.startsWith("NUM_") && normalized.length() == 5) {
			return Character.forDigit(normalized.charAt(4) - '0', 10);
		}

		if ("0".equals(normalized))
			return Canvas.KEY_NUM0;
		if ("1".equals(normalized))
			return Canvas.KEY_NUM1;
		if ("2".equals(normalized))
			return Canvas.KEY_NUM2;
		if ("3".equals(normalized))
			return Canvas.KEY_NUM3;
		if ("4".equals(normalized))
			return Canvas.KEY_NUM4;
		if ("5".equals(normalized))
			return Canvas.KEY_NUM5;
		if ("6".equals(normalized))
			return Canvas.KEY_NUM6;
		if ("7".equals(normalized))
			return Canvas.KEY_NUM7;
		if ("8".equals(normalized))
			return Canvas.KEY_NUM8;
		if ("9".equals(normalized))
			return Canvas.KEY_NUM9;
		if ("STAR".equals(normalized) || "*".equals(normalized))
			return Canvas.KEY_STAR;
		if ("POUND".equals(normalized) || "HASH".equals(normalized) || "#".equals(normalized))
			return Canvas.KEY_POUND;
		if ("UP".equals(normalized))
			return KeyMapping.getArrowKeyFromDevice(Canvas.UP);
		if ("DOWN".equals(normalized))
			return KeyMapping.getArrowKeyFromDevice(Canvas.DOWN);
		if ("LEFT".equals(normalized))
			return KeyMapping.getArrowKeyFromDevice(Canvas.LEFT);
		if ("RIGHT".equals(normalized))
			return KeyMapping.getArrowKeyFromDevice(Canvas.RIGHT);
		if ("FIRE".equals(normalized) || "MIDDLE".equals(normalized) || "OK".equals(normalized))
			return KeyMapping.getArrowKeyFromDevice(Canvas.FIRE);
		if ("LSK".equals(normalized) || "SOFT_LEFT".equals(normalized) || "S1".equals(normalized))
			return KeyMapping.soft1();
		if ("RSK".equals(normalized) || "SOFT_RIGHT".equals(normalized) || "S2".equals(normalized))
			return KeyMapping.soft2();
		throw new AutomationException(
			AutomationErrorCodes.UNKNOWN_KEY,
			"Unknown key: " + key,
			Json.object().set("key", key));
	}

	static void pressKey(final int code, int durationMs) {
		EventQueue queue = Emulator.getEventQueue();
		if (queue == null) {
			throw new AutomationException(
				AutomationErrorCodes.APP_INPUT_UNAVAILABLE, "Application input is not available.");
		}

		queue.keyPress(code);
		try {
			Thread.sleep(durationMs);
		} catch (InterruptedException ignored) {
		}

		queue.keyRelease(code);
	}

	static void tap(int x, int y) {
		EventQueue queue = Emulator.getEventQueue();
		if (queue == null) {
			throw new AutomationException(
				AutomationErrorCodes.APP_INPUT_UNAVAILABLE, "Application input is not available.");
		}

		queue.mouseDown(x, y, 0);
		try {
			Thread.sleep(30L);
		} catch (InterruptedException ignored) {
		}

		queue.mouseUp(x, y, 0);
	}

	static void drag(Json points, int delayMs) {
		if (!points.isArray() || points.asJsonList().isEmpty()) {
			throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "drag requires at least one point");
		}

		EventQueue queue = Emulator.getEventQueue();
		if (queue == null) {
			throw new AutomationException(
				AutomationErrorCodes.APP_INPUT_UNAVAILABLE, "Application input is not available.");
		}

		Json first = points.at(0);
		int x = first.at("x", -1).asInteger();
		int y = first.at("y", -1).asInteger();
		if (x < 0 || y < 0) {
			throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "drag point requires x and y");
		}

		queue.mouseDown(x, y, 0);
		for (int i = 1; i < points.asJsonList().size(); i++) {
			Json point = points.at(i);
			x = point.at("x", -1).asInteger();
			y = point.at("y", -1).asInteger();
			if (x < 0 || y < 0) {
				throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "drag point requires x and y");
			}

			queue.mouseDrag(x, y, 0);
			try {
				Thread.sleep(delayMs);
			} catch (InterruptedException ignored) {
			}
		}

		queue.mouseUp(x, y, 0);
	}
}
