package emulator.automation.worker;

import emulator.Emulator;
import emulator.ui.IEmulatorFrontend;
import emulator.ui.IScreen;
import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;
import java.util.Base64;
import java.util.concurrent.Callable;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AutomationStateExtractor;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import mjson.Json;

final class WorkerSessionSnapshot {
	private WorkerSessionSnapshot() {
	}

	private static String commandTypeName(int type) {
		switch (type) {
			case javax.microedition.lcdui.Command.SCREEN:
				return "screen";
			case javax.microedition.lcdui.Command.BACK:
				return "back";
			case javax.microedition.lcdui.Command.CANCEL:
				return "cancel";
			case javax.microedition.lcdui.Command.OK:
				return "ok";
			case javax.microedition.lcdui.Command.HELP:
				return "help";
			case javax.microedition.lcdui.Command.STOP:
				return "stop";
			case javax.microedition.lcdui.Command.EXIT:
				return "exit";
			case javax.microedition.lcdui.Command.ITEM:
				return "item";
			default:
				return "unknown";
		}
	}

	private static String itemKind(Item item) {
		if (item instanceof StringItem) {
			return "string-item";
		}
		if (item instanceof ChoiceGroup) {
			return "choice-group";
		}
		if (item instanceof Gauge) {
			return "gauge";
		}
		if (item instanceof TextField) {
			return "text-field";
		}
		return item == null ? "none" : item.getClass().getName();
	}

	private static Json buildFormItem(Item item, int index, boolean focused) {
		Json result = Json.object()
			.set("index", index)
			.set("kind", itemKind(item))
			.set("label", item == null ? null : item.getLabel())
			.set("focused", focused);
		if (item instanceof StringItem) {
			StringItem stringItem = (StringItem) item;
			result.set("text", stringItem.getText());
			result.set("appearanceMode", stringItem.getAppearanceMode());
		} else if (item instanceof ChoiceGroup) {
			ChoiceGroup choice = (ChoiceGroup) item;
			result.set("choiceType", AutomationStateExtractor.getChoiceType(choice));
			result.set("selectedIndex", choice.getSelectedIndex());
			Json choices = Json.array();
			for (int i = 0; i < choice.size(); i++) {
				choices.add(Json.object()
					.set("index", i)
					.set("text", choice.getString(i))
					.set("selected", choice.isSelected(i)));
			}
			result.set("choices", choices);
		} else if (item instanceof Gauge) {
			Gauge gauge = (Gauge) item;
			result.set("value", gauge.getValue());
			result.set("maxValue", gauge.getMaxValue());
			result.set("interactive", gauge.isInteractive());
		} else if (item instanceof TextField) {
			TextField textField = (TextField) item;
			result.set("text", textField.getString());
			result.set("constraints", textField.getConstraints());
			result.set("maxSize", textField.getMaxSize());
			result.set("caret", textField.getCaretPosition());
		}
		return result;
	}

	private static Json buildDisplayable(Displayable current) {
		if (current == null) {
			return Json.nil();
		}
		Json result = Json.object()
			.set("kind", AutomationStateExtractor.getStructuredDisplayableKind(current))
			.set("title", current.getTitle())
			.set("width", current.getWidth())
			.set("height", current.getHeight());
		Ticker ticker = current.getTicker();
		if (ticker != null) {
			result.set("ticker", ticker.getString());
		}
		if (current instanceof javax.microedition.lcdui.List) {
			javax.microedition.lcdui.List list = (javax.microedition.lcdui.List) current;
			result.set("selectedIndex", list.getSelectedIndex());
			Json items = Json.array();
			for (int i = 0; i < list.size(); i++) {
				items.add(Json.object()
					.set("index", i)
					.set("text", list.getString(i))
					.set("selected", list.isSelected(i)));
			}
			result.set("items", items);
		}
		if (current instanceof Form) {
			Form form = (Form) current;
			int focused = AutomationStateExtractor.getFocusedItemIndex(form);
			Json items = Json.array();
			for (int i = 0; i < form.size(); i++) {
				items.add(buildFormItem(form.get(i), i, i == focused));
			}
			result.set("focusedItemIndex", focused);
			result.set("items", items);
		}
		if (current instanceof Alert) {
			Alert alert = (Alert) current;
			result.set("text", alert.getString());
			result.set("timeout", alert.getTimeout());
			if (alert.getIndicator() != null) {
				result.set(
					"indicator",
					Json.object()
						.set("value", alert.getIndicator().getValue())
						.set("maxValue", alert.getIndicator().getMaxValue()));
			}
		}
		if (current instanceof Canvas) {
			result.set("pointerEvents", ((Canvas) current).hasPointerEvents());
		}
		return result;
	}

	static Json build(final boolean includeImage) {
		return WorkerFrontendThread.call(new Callable<Json>() {
			public Json call() throws Exception {
				Json result = Json.object();
				IEmulatorFrontend frontend = Emulator.getEmulator();
				IScreen screen = frontend == null ? null : frontend.getScreen();
				Display display = Emulator.getCurrentDisplay();
				Displayable current = display == null ? null : display.getCurrent();

				result.set("schemaVersion", 3);
				result.set("revision", WorkerEventModel.revision());
				result.set("frameRevision", WorkerEventModel.frameRevision());
				result.set("eventCursor", WorkerEventModel.cursor());
				result.set("ready", WorkerRuntimeState.isMidletStarted() && current != null);
				result.set("midletStarted", WorkerRuntimeState.isMidletStarted());
				result.set(
					"jarName",
					Emulator.midletJarPath == null ? null : new java.io.File(Emulator.midletJarPath).getName());
				result.set("width", screen == null ? 0 : screen.getWidth());
				result.set("height", screen == null ? 0 : screen.getHeight());
				Json displayable = buildDisplayable(current);
				WorkerPermissions.PendingPermission permission = WorkerPermissions.snapshot();
				result.set("permissionRequest", permission == null ? null : permission.toJson());

				Json softkeys = Json.object();
				softkeys.set("left", AutomationStateExtractor.getLeftSoftLabel(current));
				softkeys.set("right", AutomationStateExtractor.getRightSoftLabel(current));
				if (!displayable.isNull()) {
					displayable.set("softkeys", softkeys);
				}

				Json commands = WorkerCommands.observe(
					current, permission, AutomationStateExtractor.buildCommands(current));
				for (Json command : commands.asJsonList()) {
					if (command.has("type") && !command.at("type").isNull()) {
						command.set("typeName", commandTypeName(command.at("type").asInteger()));
					}
				}
				if (!displayable.isNull()) {
					displayable.set("commands", commands);
				}

				if (current instanceof TextBox) {
					TextBox textBox = (TextBox) current;
					displayable.set("text", textBox.getString());
					displayable.set("caret", textBox.getCaretPosition());
					displayable.set("constraints", textBox.getConstraints());
					displayable.set("maxSize", textBox.getMaxSize());
				}
				result.set("displayable", displayable);

				result.set(
					"jvmOptions",
					Json.make(ManagementFactory.getRuntimeMXBean().getInputArguments()));
				result.set("dataDir", System.getProperty("kemu.data.dir"));
				result.set("rmsDir", System.getProperty("kemu.rms.dir"));
				result.set("fileRoot", System.getProperty("kemu.file.root"));
				result.set("emulatedHeap", Json.nil());

				if (includeImage && screen != null && screen.getScreenImg() != null) {
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					screen.getScreenImg().write(output, "png");
					result.set("imageBase64", Base64.getEncoder().encodeToString(output.toByteArray()));
				}

				return result;
			}
		});
	}
}
