package emulator.automation.worker;

import emulator.Emulator;
import emulator.EventQueue;
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
import mjson.Json;

final class WorkerCommands {
	private static final Object LOCK = new Object();
	private static Map<Integer, TargetedCommand> commandRegistry = new LinkedHashMap<Integer, TargetedCommand>();
	private static long nextInvocationId = 1L;

	private WorkerCommands() {
	}

	static void invalidate() {
		synchronized (LOCK) {
			commandRegistry = new LinkedHashMap<Integer, TargetedCommand>();
		}
	}

	private static long nextInvocationId() {
		synchronized (LOCK) {
			return nextInvocationId++;
		}
	}

	private static Json awaitCommandOutcome(
		long invocationId, long afterCursor, long timeoutMs) throws InterruptedException {
		long deadline = System.nanoTime()
			+ java.util.concurrent.TimeUnit.MILLISECONDS.toNanos(Math.max(0L, timeoutMs));
		long cursor = afterCursor;
		while (true) {
			Json events = WorkerEventModel.eventsSince(cursor);
			Json permissionEvent = null;
			for (Json event : events.asJsonList()) {
				cursor = Math.max(cursor, event.at("cursor", cursor).asLong());
				String eventName = event.at("event", "").asString();
				if (("command-finished".equals(eventName)
					|| "command-failed".equals(eventName))
					&& event.at("invocationId", -1L).asLong() == invocationId) {
					return event;
				}
				if ("permission-requested".equals(eventName)) {
					permissionEvent = event;
				}
			}
			if (permissionEvent != null && WorkerPermissions.snapshot() != null) {
				return permissionEvent;
			}

			long remainingNanos = deadline - System.nanoTime();
			if (remainingNanos <= 0L) {
				return null;
			}
			long remainingMs = Math.max(
				1L,
				java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(remainingNanos));
			if (!WorkerEventModel.awaitEventAfter(cursor, remainingMs)) {
				return null;
			}
		}
	}

	static Json observe(
		Displayable current, WorkerPermissions.PendingPermission permission, Vector<TargetedCommand> commands) {
		LinkedHashMap<Integer, TargetedCommand> nextRegistry = new LinkedHashMap<Integer, TargetedCommand>();
		Json items = Json.array();
		int id = 1;
		for (TargetedCommand command : commands) {
			if (command == null) {
				continue;
			}

			nextRegistry.put(Integer.valueOf(id), command);
			id++;
		}

		synchronized (LOCK) {
			commandRegistry = nextRegistry;
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

	private static void refreshFromCurrentDisplay() {
		WorkerFrontendThread.call(new Callable<Object>() {
			public Object call() {
				Display display = Emulator.getCurrentDisplay();
				Displayable current = display == null ? null : display.getCurrent();
				observe(current, WorkerPermissions.snapshot(), AutomationStateExtractor.buildCommands(current));

				return null;
			}
		});
	}

	private static TargetedCommand findByLabel(String label) {
		TargetedCommand match = null;
		for (TargetedCommand candidate : commandRegistry.values()) {
			String candidateLabel = candidate.command == null
				? candidate.text
				: candidate.command.getLabel();
			if (!label.equals(candidateLabel) && !label.equals(candidate.text)) {
				continue;
			}
			if (match != null) {
				throw new AutomationException(
					AutomationErrorCodes.INVALID_REQUEST,
					"Command label is ambiguous: " + label,
					Json.object().set("label", label));
			}
			match = candidate;
		}
		return match;
	}

	static Json select(Json request) {
		int id = request.at("id", -1).asInteger();
		String label = request.has("label") && !request.at("label").isNull()
			? request.at("label").asString()
			: null;
		if (id < 0 && (label == null || label.length() == 0)) {
			throw new AutomationException(
				AutomationErrorCodes.INVALID_REQUEST,
				"select-command requires id or label");
		}

		refreshFromCurrentDisplay();
		long oldRevision = WorkerEventModel.revision();
		if (!request.has("expectRevision") || request.at("expectRevision").isNull()) {
			throw new AutomationException(
				AutomationErrorCodes.INVALID_REQUEST,
				"select-command requires expectRevision");
		}
		long expectedRevision = request.at("expectRevision").asLong();
		if (expectedRevision != oldRevision) {
			throw new AutomationException(
				AutomationErrorCodes.STALE_REVISION,
				"Stale revision: " + expectedRevision + ", current: " + oldRevision,
				Json.object()
					.set("expectedRevision", expectedRevision)
					.set("currentRevision", oldRevision));
		}
		final TargetedCommand command;
		synchronized (LOCK) {
			command = id >= 0
				? commandRegistry.get(Integer.valueOf(id))
				: findByLabel(label);
		}

		if (command == null) {
			throw new AutomationException(
				AutomationErrorCodes.UNKNOWN_COMMAND_ID,
				id >= 0 ? "Unknown command id: " + id : "Unknown command label: " + label,
				Json.object().set("id", id).set("label", label));
		}

		Json oldState = WorkerSessionSnapshot.build(false);
		int oldDisplayIdentity = WorkerLcduiActions.currentDisplayIdentity();
		String oldDisplaySignature = oldState.at("displayable").toString();
		long timeoutMs = request.at("timeoutMs", 5000L).asLong();
		long startedAt = System.nanoTime();
		final long invocationId = nextInvocationId();
		long eventCursor = WorkerEventModel.cursor();
		final Long requiredRevision = request.has("expectRevision")
			&& !request.at("expectRevision").isNull()
				? Long.valueOf(request.at("expectRevision").asLong())
				: null;
		try {
			if (!command.enqueueAndWait(timeoutMs, new Runnable() {
				public void run() {
					long currentRevision = WorkerEventModel.revision();
					if (requiredRevision != null
						&& requiredRevision.longValue() != currentRevision) {
						throw new AutomationException(
							AutomationErrorCodes.STALE_REVISION,
							"Stale revision: " + requiredRevision + ", current: " + currentRevision,
							Json.object()
								.set("expectedRevision", requiredRevision.longValue())
								.set("currentRevision", currentRevision));
					}
					if (!command.isCurrentTarget()) {
						throw new AutomationException(
							AutomationErrorCodes.STALE_REVISION,
							"LCDUI command target is no longer current",
							Json.object().set("currentRevision", currentRevision));
					}
				}
			}, new EventQueue.CommandDispatchListener() {
				public void commandFinished(Throwable failure) {
					Json details = Json.object()
						.set("invocationId", invocationId)
						.set("commandId", id)
						.set(
							"label",
							command.command == null
								? command.text
								: command.command.getLabel());
					if (failure == null) {
						WorkerEventModel.stateChanged("command-finished", details);
						return;
					}
					details
						.set("errorType", failure.getClass().getName())
						.set("message", failure.getMessage());
					WorkerEventModel.stateChanged("command-failed", details);
				}
			})) {
				throw new AutomationException(
					AutomationErrorCodes.TIMEOUT,
					"Timed out waiting to enqueue LCDUI command",
					Json.object()
						.set("timeoutMs", timeoutMs)
						.set("oldRevision", oldRevision)
						.set("lastState", WorkerSessionSnapshot.build(false)));
			}
			long elapsedMs = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(
				System.nanoTime() - startedAt);
			long remainingMs = timeoutMs - elapsedMs;
			Json outcome = remainingMs <= 0L
				? null
				: awaitCommandOutcome(invocationId, eventCursor, remainingMs);
			if (outcome == null) {
				throw new AutomationException(
					AutomationErrorCodes.TIMEOUT,
					"Timed out waiting for LCDUI command completion",
					Json.object()
						.set("timeoutMs", timeoutMs)
						.set("elapsedMs", elapsedMs)
						.set("oldRevision", oldRevision)
						.set("lastState", WorkerSessionSnapshot.build(false)));
			}
			if ("command-failed".equals(outcome.at("event", "").asString())) {
				throw new AutomationException(
					AutomationErrorCodes.WORKER_FAILURE,
					"LCDUI command handler failed",
					Json.object()
						.set("invocationId", invocationId)
						.set("errorType", outcome.at("errorType", "java.lang.Throwable").asString())
						.set("message", outcome.at("message", "").asString())
						.set("lastState", WorkerSessionSnapshot.build(false)));
			}
			if ("permission-requested".equals(outcome.at("event", "").asString())) {
				invalidate();
				Json state = WorkerSessionSnapshot.build(false);
				WorkerPermissions.PendingPermission permission = WorkerPermissions.snapshot();
				long pendingElapsedMs = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(
					System.nanoTime() - startedAt);
				return Json.object()
					.set("ok", true)
					.set("pending", true)
					.set("status", "permission-pending")
					.set("id", id)
					.set("label", command.command == null ? null : command.command.getLabel())
					.set("text", command.text)
					.set("oldRevision", oldRevision)
					.set("newRevision", state.at("revision", WorkerEventModel.revision()).asLong())
					.set("elapsedMs", pendingElapsedMs)
					.set("waitNextDisplayRequested", request.at("waitNextDisplay", false).asBoolean())
					.set("permissionRequest", permission == null ? null : permission.toJson())
					.set("state", state);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AutomationException(
				AutomationErrorCodes.WORKER_FAILURE,
				"Interrupted while waiting for LCDUI command dispatch",
				null,
				e);
		}
		invalidate();

		Json transition = null;
		if (request.at("waitNextDisplay", false).asBoolean()) {
			long elapsedMs = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(
				System.nanoTime() - startedAt);
			long remainingMs = timeoutMs - elapsedMs;
			if (remainingMs <= 0L) {
				throw new AutomationException(
					AutomationErrorCodes.TIMEOUT,
					"Timed out waiting for the next LCDUI display",
					Json.object()
						.set("timeoutMs", timeoutMs)
						.set("elapsedMs", elapsedMs)
						.set("oldRevision", oldRevision)
						.set("lastState", WorkerSessionSnapshot.build(false)));
			}
			transition = WorkerWaits.waitForNextDisplay(
				oldDisplayIdentity,
				oldDisplaySignature,
				remainingMs);
		}
		Json state = WorkerSessionSnapshot.build(false);
		long elapsedMs = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(
			System.nanoTime() - startedAt);

		Json result = Json.object()
			.set("ok", true)
			.set("id", id)
			.set("label", command.command == null ? null : command.command.getLabel())
			.set("text", command.text)
			.set("oldRevision", oldRevision)
			.set("newRevision", state.at("revision", WorkerEventModel.revision()).asLong())
			.set("elapsedMs", elapsedMs)
			.set("state", state);
		if (transition != null) {
			result.set("transition", transition);
		}
		return result;
	}
}
