package emulator.automation.worker;

import emulator.Emulator;
import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationException;
import emulator.ui.TargetedCommand;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import javax.microedition.lcdui.AutomationStateExtractor;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import mjson.Json;

final class WorkerCommandSnapshots {
	private static final Object LOCK = new Object();
	private static Map<Integer, TargetedCommand> commandRegistry = new LinkedHashMap<Integer, TargetedCommand>();
	private static int commandSnapshotCounter = 1;
	private static int commandSnapshotId = 0;
	private static String commandSnapshotSignature = "";

	private WorkerCommandSnapshots() {
	}

	private static void appendDisplayStateSignature(
		StringBuilder signature, Displayable current, WorkerPermissions.PendingPermission permission) {
		signature
			.append("displayableKind=")
			.append(AutomationStateExtractor.getDisplayableKind(current))
			.append('\n');
		signature
			.append("title=")
			.append(current == null || current.getTitle() == null ? "" : current.getTitle())
			.append('\n');
		if (current instanceof javax.microedition.lcdui.List) {
			javax.microedition.lcdui.List list = (javax.microedition.lcdui.List) current;
			signature
				.append("list.selectedIndex=")
				.append(list.getSelectedIndex())
				.append('\n');
			for (int i = 0; i < list.size(); i++) {
				signature
					.append("list.item[")
					.append(i)
					.append("]=")
					.append(list.getString(i))
					.append('\n');
			}
		}

		if (current instanceof TextBox) {
			TextBox textBox = (TextBox) current;
			signature.append("textbox.text=").append(textBox.getString()).append('\n');
			signature
				.append("textbox.caret=")
				.append(textBox.getCaretPosition())
				.append('\n');
			signature
				.append("textbox.constraints=")
				.append(textBox.getConstraints())
				.append('\n');
		}

		if (permission != null) {
			signature.append("permission.id=").append(permission.id).append('\n');
			signature
				.append("permission.message=")
				.append(permission.message == null ? "" : permission.message)
				.append('\n');
		}
	}

	private static void appendCommandSignature(StringBuilder signature, TargetedCommand command, int id) {
		signature.append(id).append('|');
		signature.append(command.targetSignature()).append('|');
		signature.append(command.text == null ? "" : command.text).append('|');
		signature.append(command.isChoice()).append('|');
		signature.append(command.wasSelected).append('|');
		if (command.command != null) {
			signature
				.append(command.command.getLabel() == null ? "" : command.command.getLabel())
				.append('|');
			signature.append(command.command.getCommandType()).append('|');
			signature.append(command.command.getPriority());
		}

		signature.append('\n');
	}

	static int currentId() {
		synchronized (LOCK) {
			return commandSnapshotId;
		}
	}

	static void invalidate() {
		synchronized (LOCK) {
			commandSnapshotId = commandSnapshotCounter++;
			commandSnapshotSignature = null;
			commandRegistry = new LinkedHashMap<Integer, TargetedCommand>();
		}
	}

	static Json snapshot(
		Displayable current, WorkerPermissions.PendingPermission permission, Vector<TargetedCommand> commands) {
		LinkedHashMap<Integer, TargetedCommand> nextRegistry = new LinkedHashMap<Integer, TargetedCommand>();
		Json items = Json.array();
		StringBuilder signature = new StringBuilder();
		int nextSnapshotId;
		boolean changed;
		int id = 1;
		appendDisplayStateSignature(signature, current, permission);
		for (TargetedCommand command : commands) {
			if (command == null) {
				continue;
			}

			appendCommandSignature(signature, command, id);
			nextRegistry.put(Integer.valueOf(id), command);
			id++;
		}

		synchronized (LOCK) {
			changed = !signature.toString().equals(commandSnapshotSignature);
			nextSnapshotId = changed ? commandSnapshotCounter++ : commandSnapshotId;
			if (changed) {
				commandSnapshotSignature = signature.toString();
				commandSnapshotId = nextSnapshotId;
				commandRegistry = nextRegistry;
			} else {
				nextRegistry = new LinkedHashMap<Integer, TargetedCommand>(commandRegistry);
			}
		}

		id = 1;
		for (TargetedCommand command : commands) {
			if (command == null) {
				continue;
			}

			Json item = Json.object();
			item.set("id", id);
			item.set("text", command.text);
			item.set("choice", command.isChoice());
			item.set("selected", command.wasSelected);
			if (command.command != null) {
				item.set("label", command.command.getLabel());
				item.set("type", command.command.getCommandType());
				item.set("priority", command.command.getPriority());
			}

			items.add(item);
			id++;
		}

		return items;
	}

	private static int refreshFromCurrentDisplay() {
		return WorkerFrontendThread.call(new Callable<Integer>() {
			public Integer call() {
				Display display = Emulator.getCurrentDisplay();
				Displayable current = display == null ? null : display.getCurrent();
				snapshot(current, WorkerPermissions.snapshot(), AutomationStateExtractor.buildCommands(current));

				return Integer.valueOf(currentId());
			}
		})
			.intValue();
	}

	static Json select(Json request) {
		int id = request.at("id", -1).asInteger();
		int snapshotId = request.at("snapshotId", -1).asInteger();
		if (id < 0) {
			throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "select-command requires id");
		}

		if (snapshotId < 0) {
			throw new AutomationException(AutomationErrorCodes.INVALID_REQUEST, "select-command requires snapshotId");
		}

		int currentSnapshotId = refreshFromCurrentDisplay();
		final TargetedCommand command;
		synchronized (LOCK) {
			if (snapshotId != currentSnapshotId) {
				throw new AutomationException(
					AutomationErrorCodes.STALE_SNAPSHOT,
					"Stale command snapshot: " + snapshotId + ", current: " + currentSnapshotId,
					Json.object().set("snapshotId", snapshotId).set("currentSnapshotId", currentSnapshotId));
			}

			command = commandRegistry.get(Integer.valueOf(id));
		}

		if (command == null) {
			throw new AutomationException(
				AutomationErrorCodes.UNKNOWN_COMMAND_ID,
				"Unknown command id: " + id,
				Json.object().set("id", id).set("snapshotId", snapshotId));
		}

		WorkerFrontendThread.call(new Callable<Object>() {
			public Object call() {
				command.invoke();
				invalidate();

				return null;
			}
		});

		return Json.object()
			.set("ok", true)
			.set("snapshotId", snapshotId)
			.set("id", id)
			.set("text", command.text);
	}
}
