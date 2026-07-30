package emulator.automation.worker;

import emulator.Emulator;
import emulator.EventQueue;
import emulator.KeyMapping;
import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationException;
import java.util.concurrent.Callable;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
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

	private static String classifyKey(final int code) {
		return WorkerFrontendThread.call(new Callable<String>() {
			public String call() {
				Display display = Emulator.getCurrentDisplay();
				Displayable current = display == null ? null : display.getCurrent();
				if (KeyMapping.isLeftSoft(code) || KeyMapping.isRightSoft(code)) {
					return "nokia-softkey";
				}
				if (current instanceof Canvas) {
					return "raw-canvas-key";
				}
				if (current instanceof javax.microedition.lcdui.List
					&& (code == KeyMapping.getArrowKeyFromDevice(Canvas.UP)
						|| code == KeyMapping.getArrowKeyFromDevice(Canvas.DOWN)
						|| code == KeyMapping.getArrowKeyFromDevice(Canvas.LEFT)
						|| code == KeyMapping.getArrowKeyFromDevice(Canvas.RIGHT))) {
					return "list-navigation";
				}
				return "native-lcdui-key";
			}
		});
	}

	private static void awaitDispatch(EventQueue queue, int sequence, long timeoutMs, String phase) {
		try {
			if (!queue.waitForInputDispatch(sequence, timeoutMs)) {
				throw new AutomationException(
					AutomationErrorCodes.TIMEOUT,
					"Timed out waiting for input " + phase + " dispatch",
					Json.object()
						.set("phase", phase)
						.set("sequence", sequence)
						.set("timeoutMs", timeoutMs)
						.set("lastRevision", WorkerEventModel.revision()));
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AutomationException(
				AutomationErrorCodes.WORKER_FAILURE,
				"Interrupted while waiting for input dispatch",
				null,
				e);
		}
	}

	static Json pressKey(final int code, int durationMs, boolean waitDispatched, boolean waitRelease) {
		EventQueue queue = Emulator.getEventQueue();
		if (queue == null) {
			throw new AutomationException(
				AutomationErrorCodes.APP_INPUT_UNAVAILABLE, "Application input is not available.");
		}

		int pressSequence = queue.keyPressTracked(code);
		if (waitDispatched || waitRelease) {
			awaitDispatch(queue, pressSequence, 5000L, "press");
		}
		try {
			Thread.sleep(durationMs);
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}

		int releaseSequence = queue.keyReleaseTracked(code);
		if (waitDispatched || waitRelease) {
			awaitDispatch(queue, releaseSequence, 5000L, "release");
		}
		return Json.object()
			.set("kind", classifyKey(code))
			.set("pressSequence", pressSequence)
			.set("releaseSequence", releaseSequence)
			.set("pressDispatched", waitDispatched || waitRelease)
			.set("releaseDispatched", waitDispatched || waitRelease)
			.set("revision", WorkerEventModel.revision());
	}

	static Json tap(int x, int y, boolean waitDispatched) {
		EventQueue queue = Emulator.getEventQueue();
		if (queue == null) {
			throw new AutomationException(
				AutomationErrorCodes.APP_INPUT_UNAVAILABLE, "Application input is not available.");
		}

		int pressSequence = queue.mouseDownTracked(x, y, 0);
		if (waitDispatched) {
			awaitDispatch(queue, pressSequence, 5000L, "pointer-press");
		}
		try {
			Thread.sleep(30L);
		} catch (InterruptedException ignored) {
		}

		int releaseSequence = queue.mouseUpTracked(x, y, 0);
		if (waitDispatched) {
			awaitDispatch(queue, releaseSequence, 5000L, "pointer-release");
		}
		return Json.object()
			.set("kind", "pointer-event")
			.set("pressSequence", pressSequence)
			.set("releaseSequence", releaseSequence)
			.set("dispatched", waitDispatched)
			.set("revision", WorkerEventModel.revision());
	}

	static Json drag(Json points, int delayMs, boolean waitDispatched) {
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

		int lastSequence = queue.mouseDownTracked(x, y, 0);
		for (int i = 1; i < points.asJsonList().size(); i++) {
			Json point = points.at(i);
			x = point.at("x", -1).asInteger();
			y = point.at("y", -1).asInteger();
			if (x < 0 || y < 0) {
				throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "drag point requires x and y");
			}

			lastSequence = queue.mouseDragTracked(x, y, 0);
			try {
				Thread.sleep(delayMs);
			} catch (InterruptedException ignored) {
			}
		}

		lastSequence = queue.mouseUpTracked(x, y, 0);
		if (waitDispatched) {
			awaitDispatch(queue, lastSequence, 5000L, "pointer-release");
		}
		return Json.object()
			.set("kind", "pointer-event")
			.set("lastSequence", lastSequence)
			.set("dispatched", waitDispatched)
			.set("revision", WorkerEventModel.revision());
	}
}
