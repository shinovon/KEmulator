package emulator.automation.worker;

import emulator.Emulator;
import emulator.EventQueue;
import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationException;
import java.util.concurrent.TimeUnit;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;
import mjson.Json;

final class WorkerLcduiActions {
	private interface Action {
		Json run();
	}

	private WorkerLcduiActions() {
	}

	private static Displayable currentDisplayable() {
		Display display = Emulator.getCurrentDisplay();
		return display == null ? null : display.getCurrent();
	}

	static int currentDisplayIdentity() {
		return WorkerFrontendThread.call(new java.util.concurrent.Callable<Integer>() {
			public Integer call() {
				Displayable current = currentDisplayable();
				return Integer.valueOf(current == null ? 0 : System.identityHashCode(current));
			}
		}).intValue();
	}

	private static void checkRevision(Json request) {
		if (!request.has("expectRevision") || request.at("expectRevision").isNull()) {
			return;
		}
		long expected = request.at("expectRevision").asLong();
		long current = WorkerEventModel.revision();
		if (expected != current) {
			throw new AutomationException(
				AutomationErrorCodes.STALE_REVISION,
				"Stale revision: " + expected + ", current: " + current,
				Json.object().set("expectedRevision", expected).set("currentRevision", current));
		}
	}

	private static Json onEventThread(final Json request, final Action action) {
		final Json[] result = new Json[1];
		EventQueue queue = Emulator.getEventQueue();
		if (queue == null) {
			throw new AutomationException(
				AutomationErrorCodes.APP_INPUT_UNAVAILABLE,
				"LCDUI event queue is not available");
		}
		try {
			boolean completed = queue.callAndWait(new Runnable() {
				public void run() {
					checkRevision(request);
					result[0] = action.run();
				}
			}, request.at("timeoutMs", 5000L).asLong());
			if (!completed) {
				throw new AutomationException(
					AutomationErrorCodes.TIMEOUT,
					"Timed out waiting for the LCDUI event thread",
					Json.object()
						.set("timeoutMs", request.at("timeoutMs", 5000L).asLong())
						.set("lastRevision", WorkerEventModel.revision()));
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AutomationException(
				AutomationErrorCodes.WORKER_FAILURE,
				"Interrupted while waiting for the LCDUI event thread",
				null,
				e);
		}
		return result[0] == null ? Json.object() : result[0];
	}

	private static Form requireForm() {
		Displayable current = currentDisplayable();
		if (!(current instanceof Form)) {
			throw new AutomationException(
				AutomationErrorCodes.LCDUI_CONTROL_UNAVAILABLE,
				"Current displayable is not a Form",
				Json.object().set(
					"currentKind",
					current == null ? "none" : current.getClass().getName()));
		}
		return (Form) current;
	}

	private static Item findItem(Form form, int itemIndex, Class expected) {
		if (itemIndex >= 0) {
			if (itemIndex >= form.size()) {
				throw new AutomationException(
					AutomationErrorCodes.LCDUI_CONTROL_UNAVAILABLE,
					"Form item index is out of range: " + itemIndex,
					Json.object().set("itemIndex", itemIndex).set("itemCount", form.size()));
			}
			Item item = form.get(itemIndex);
			if (!expected.isInstance(item)) {
				throw new AutomationException(
					AutomationErrorCodes.LCDUI_CONTROL_UNAVAILABLE,
					"Form item " + itemIndex + " is not a " + expected.getSimpleName());
			}
			return item;
		}
		for (int i = 0; i < form.size(); i++) {
			Item item = form.get(i);
			if (expected.isInstance(item)) {
				return item;
			}
		}
		throw new AutomationException(
			AutomationErrorCodes.LCDUI_CONTROL_UNAVAILABLE,
			"Current Form has no " + expected.getSimpleName());
	}

	static Json listSelect(final Json request) {
		return onEventThread(request, new Action() {
			public Json run() {
				Displayable current = currentDisplayable();
				if (!(current instanceof javax.microedition.lcdui.List)) {
					throw new AutomationException(
						AutomationErrorCodes.LCDUI_CONTROL_UNAVAILABLE,
						"Current displayable is not a List");
				}
				javax.microedition.lcdui.List list = (javax.microedition.lcdui.List) current;
				int index = request.at("index", -1).asInteger();
				if (index < 0 || index >= list.size()) {
					throw new AutomationException(
						AutomationErrorCodes.INVALID_REQUEST,
						"List index is out of range: " + index,
						Json.object().set("index", index).set("size", list.size()));
				}
				long oldRevision = WorkerEventModel.revision();
				list.setSelectedIndex(index, true);
				return Json.object()
					.set("oldRevision", oldRevision)
					.set("newRevision", WorkerEventModel.revision())
					.set("selectedIndex", list.getSelectedIndex());
			}
		});
	}

	static Json listMove(final Json request) {
		return onEventThread(request, new Action() {
			public Json run() {
				Displayable current = currentDisplayable();
				if (!(current instanceof javax.microedition.lcdui.List)) {
					throw new AutomationException(
						AutomationErrorCodes.LCDUI_CONTROL_UNAVAILABLE,
						"Current displayable is not a List");
				}
				javax.microedition.lcdui.List list = (javax.microedition.lcdui.List) current;
				String direction = request.at("direction", "").asString();
				int count = request.at("count", 1).asInteger();
				if (count < 1) {
					throw new AutomationException(
						AutomationErrorCodes.INVALID_REQUEST,
						"List move count must be positive");
				}
				int delta;
				if ("up".equals(direction)) {
					delta = -count;
				} else if ("down".equals(direction)) {
					delta = count;
				} else {
					throw new AutomationException(
						AutomationErrorCodes.INVALID_REQUEST,
						"List move direction must be up or down");
				}
				int oldIndex = list.getSelectedIndex();
				if (oldIndex < 0 && list.size() > 0) {
					oldIndex = 0;
				}
				int next = Math.max(0, Math.min(list.size() - 1, oldIndex + delta));
				long oldRevision = WorkerEventModel.revision();
				list.setSelectedIndex(next, true);
				return Json.object()
					.set("oldRevision", oldRevision)
					.set("newRevision", WorkerEventModel.revision())
					.set("previousIndex", oldIndex)
					.set("selectedIndex", list.getSelectedIndex());
			}
		});
	}

	static Json choiceSet(final Json request) {
		return onEventThread(request, new Action() {
			public Json run() {
				Form form = requireForm();
				int itemIndex = request.at("itemIndex", -1).asInteger();
				ChoiceGroup choice = (ChoiceGroup) findItem(form, itemIndex, ChoiceGroup.class);
				int index = request.at("index", -1).asInteger();
				if (index < 0 || index >= choice.size()) {
					throw new AutomationException(
						AutomationErrorCodes.INVALID_REQUEST,
						"Choice index is out of range: " + index);
				}
				long oldRevision = WorkerEventModel.revision();
				choice.setSelectedIndex(index, request.at("selected", true).asBoolean());
				form._itemStateChanged(choice);
				return Json.object()
					.set("oldRevision", oldRevision)
					.set("newRevision", WorkerEventModel.revision())
					.set("selectedIndex", choice.getSelectedIndex());
			}
		});
	}

	static Json gaugeSet(final Json request) {
		return onEventThread(request, new Action() {
			public Json run() {
				Form form = requireForm();
				Gauge gauge = (Gauge) findItem(
					form,
					request.at("itemIndex", -1).asInteger(),
					Gauge.class);
				long oldRevision = WorkerEventModel.revision();
				gauge.setValue(request.at("value").asInteger());
				form._itemStateChanged(gauge);
				return Json.object()
					.set("oldRevision", oldRevision)
					.set("newRevision", WorkerEventModel.revision())
					.set("value", gauge.getValue())
					.set("maxValue", gauge.getMaxValue());
			}
		});
	}

	static Json textFieldSet(final Json request) {
		return onEventThread(request, new Action() {
			public Json run() {
				Form form = requireForm();
				TextField textField = (TextField) findItem(
					form,
					request.at("itemIndex", -1).asInteger(),
					TextField.class);
				long oldRevision = WorkerEventModel.revision();
				String value = request.at("value", "").asString();
				textField.setString(value);
				form._itemStateChanged(textField);
				return Json.object()
					.set("oldRevision", oldRevision)
					.set("newRevision", WorkerEventModel.revision())
					.set("value", textField.getString());
			}
		});
	}

	static Json waitIdle(Json request) {
		long start = System.nanoTime();
		long timeoutMs = request.at("timeoutMs", 5000L).asLong();
		EventQueue queue = Emulator.getEventQueue();
		if (queue == null) {
			throw new AutomationException(
				AutomationErrorCodes.APP_INPUT_UNAVAILABLE,
				"LCDUI event queue is not available");
		}
		try {
			if (!queue.waitUntilIdle(timeoutMs)) {
				throw new AutomationException(
					AutomationErrorCodes.TIMEOUT,
					"Timed out waiting for LCDUI idle",
					Json.object()
						.set("timeoutMs", timeoutMs)
						.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))
						.set("lastRevision", WorkerEventModel.revision()));
			}
			WorkerFrontendThread.call(new java.util.concurrent.Callable<Object>() {
				public Object call() {
					return null;
				}
			});
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AutomationException(
				AutomationErrorCodes.WORKER_FAILURE,
				"Interrupted while waiting for LCDUI idle",
				null,
				e);
		}
		return Json.object()
			.set("idle", true)
			.set("revision", WorkerEventModel.revision())
			.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
	}
}
