package emulator.automation.worker;

import emulator.Emulator;
import emulator.ui.IEmulatorFrontend;
import emulator.ui.IScreen;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.Callable;
import javax.microedition.lcdui.AutomationStateExtractor;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import mjson.Json;

final class WorkerSessionSnapshot {
	private WorkerSessionSnapshot() {
	}

	static Json build(final boolean includeImage) {
		return WorkerFrontendThread.call(new Callable<Json>() {
			public Json call() throws Exception {
				Json result = Json.object();
				IEmulatorFrontend frontend = Emulator.getEmulator();
				IScreen screen = frontend == null ? null : frontend.getScreen();
				Display display = Emulator.getCurrentDisplay();
				Displayable current = display == null ? null : display.getCurrent();

				result.set("ready", WorkerRuntimeState.isMidletStarted() && current != null);
				result.set("midletStarted", WorkerRuntimeState.isMidletStarted());
				result.set(
					"jarName",
					Emulator.midletJarPath == null ? null : new java.io.File(Emulator.midletJarPath).getName());
				result.set("width", screen == null ? 0 : screen.getWidth());
				result.set("height", screen == null ? 0 : screen.getHeight());
				result.set("displayableKind", AutomationStateExtractor.getDisplayableKind(current));
				result.set("title", current == null ? null : current.getTitle());
				WorkerPermissions.PendingPermission permission = WorkerPermissions.snapshot();
				result.set("permissionRequest", permission == null ? null : permission.toJson());

				Json softkeys = Json.object();
				softkeys.set("left", AutomationStateExtractor.getLeftSoftLabel(current));
				softkeys.set("right", AutomationStateExtractor.getRightSoftLabel(current));
				result.set("softkeys", softkeys);

				Json commands = WorkerCommandSnapshots.snapshot(
					current, permission, AutomationStateExtractor.buildCommands(current));
				result.set("commandSnapshotId", WorkerCommandSnapshots.currentId());
				result.set("commands", commands);

				if (current instanceof TextBox) {
					TextBox textBox = (TextBox) current;
					Json textState = Json.object();
					textState.set("text", textBox.getString());
					textState.set("caret", textBox.getCaretPosition());
					textState.set("constraints", textBox.getConstraints());
					textState.set("maxSize", textBox.getMaxSize());
					result.set("textBox", textState);
				}

				if (current instanceof javax.microedition.lcdui.List) {
					javax.microedition.lcdui.List list = (javax.microedition.lcdui.List) current;
					Json listState = Json.object();
					listState.set("selectedIndex", list.getSelectedIndex());
					Json items = Json.array();
					for (int i = 0; i < list.size(); i++) {
						items.add(list.getString(i));
					}

					listState.set("items", items);
					result.set("list", listState);
				}

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
