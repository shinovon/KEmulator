package emulator.automation.worker;

import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationException;
import java.util.concurrent.TimeUnit;
import mjson.Json;

final class WorkerWaits {
	private WorkerWaits() {
	}

	private static long timeout(Json request) {
		long timeoutMs = request.at("timeoutMs", 5000L).asLong();
		if (timeoutMs < 0L || timeoutMs > emulator.automation.shared.AutomationLimits.MAX_WAIT_MS) {
			throw new AutomationException(
				AutomationErrorCodes.INVALID_REQUEST,
				"timeoutMs must be between 0 and "
					+ emulator.automation.shared.AutomationLimits.MAX_WAIT_MS);
		}
		return timeoutMs;
	}

	private static boolean displayMatches(Json state, Json request) {
		Json displayable = state.at("displayable");
		if (displayable == null || displayable.isNull() || !displayable.isObject()) {
			return false;
		}
		if (request.has("kind")
			&& !request.at("kind").isNull()
			&& !request.at("kind").asString().equals(displayable.at("kind", "").asString())) {
			return false;
		}
		if (request.has("title")
			&& !request.at("title").isNull()
			&& !request.at("title").asString().equals(displayable.at("title", "").asString())) {
			return false;
		}
		if (request.has("selectedIndex")
			&& !request.at("selectedIndex").isNull()
			&& request.at("selectedIndex").asInteger()
				!= displayable.at("selectedIndex", -1).asInteger()) {
			return false;
		}
		if (request.has("afterRevision")
			&& !request.at("afterRevision").isNull()
			&& state.at("revision", 0L).asLong() <= request.at("afterRevision").asLong()) {
			return false;
		}
		return true;
	}

	private static boolean permissionMatches(Json state, Json request) {
		Json permission = state.at("permissionRequest");
		if (permission == null || permission.isNull() || !permission.isObject()) {
			return false;
		}
		if (!request.has("name") || request.at("name").isNull()) {
			return true;
		}
		String name = request.at("name").asString();
		String permissionName = permission.at("name", "").asString();
		String message = permission.at("message", "").asString();
		return name.equals(permissionName) || message.indexOf(name) >= 0;
	}

	private static boolean matches(String type, Json state, Json request) {
		if ("display".equals(type)) {
			return displayMatches(state, request);
		}
		if ("worker-ready".equals(type)) {
			return state.at("ready", false).asBoolean();
		}
		if ("permission".equals(type)) {
			return permissionMatches(state, request);
		}
		throw new AutomationException(
			AutomationErrorCodes.INVALID_REQUEST,
			"Unknown worker wait type: " + type);
	}

	static Json waitFor(Json request) {
		String type = request.at("type", "").asString();
		if ("idle".equals(type)) {
			return WorkerLcduiActions.waitIdle(request);
		}
		if ("frame".equals(type)) {
			return waitForFrame(request);
		}
		long timeoutMs = timeout(request);
		long start = System.nanoTime();
		Json last = Json.object();
		while (true) {
			long cursor = WorkerEventModel.cursor();
			last = WorkerSessionSnapshot.build(false);
			if (matches(type, last, request)) {
				return Json.object()
					.set("condition", type)
					.set("matched", true)
					.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))
					.set("state", last);
			}
			long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
			long remaining = timeoutMs - elapsed;
			if (remaining <= 0L) {
				throw timeoutFailure(type, timeoutMs, start, last);
			}
			if (WorkerEventModel.cursor() != cursor) {
				continue;
			}
			try {
				if (!WorkerEventModel.awaitEventAfter(cursor, remaining)) {
					throw timeoutFailure(type, timeoutMs, start, last);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new AutomationException(
					AutomationErrorCodes.WORKER_FAILURE,
					"Interrupted while waiting for " + type,
					null,
					e);
			}
		}
	}

	private static AutomationException timeoutFailure(
		String type,
		long timeoutMs,
		long start,
		Json last) {
		return new AutomationException(
			AutomationErrorCodes.TIMEOUT,
			"Timed out waiting for " + type,
			Json.object()
				.set("condition", type)
				.set("timeoutMs", timeoutMs)
				.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))
				.set("lastState", last));
	}

	private static Json waitForFrame(Json request) {
		long timeoutMs = timeout(request);
		long afterRevision = request.at("afterRevision", WorkerEventModel.frameRevision()).asLong();
		long start = System.nanoTime();
		try {
			if (!WorkerEventModel.awaitFrameAfter(afterRevision, timeoutMs)) {
				throw new AutomationException(
					AutomationErrorCodes.TIMEOUT,
					"Timed out waiting for frame after revision " + afterRevision,
					Json.object()
						.set("condition", "frame")
						.set("afterRevision", afterRevision)
						.set("timeoutMs", timeoutMs)
						.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))
						.set("lastFrameRevision", WorkerEventModel.frameRevision())
						.set("lastState", WorkerSessionSnapshot.build(false)));
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AutomationException(
				AutomationErrorCodes.WORKER_FAILURE,
				"Interrupted while waiting for frame",
				null,
				e);
		}
		return Json.object()
			.set("condition", "frame")
			.set("matched", true)
			.set("afterRevision", afterRevision)
			.set("frameRevision", WorkerEventModel.frameRevision())
			.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
	}

	static Json waitForNextDisplay(
		int oldIdentity,
		String oldDisplaySignature,
		long timeoutMs) {
		long start = System.nanoTime();
		while (true) {
			long cursor = WorkerEventModel.cursor();
			int currentIdentity = WorkerLcduiActions.currentDisplayIdentity();
			Json state = WorkerSessionSnapshot.build(false);
			String currentDisplaySignature = state.at("displayable").toString();
			if (currentIdentity != oldIdentity
				|| !currentDisplaySignature.equals(oldDisplaySignature)) {
				return Json.object()
					.set("changed", true)
					.set("displayIdentity", currentIdentity)
					.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))
					.set("state", state);
			}
			long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
			long remaining = timeoutMs - elapsed;
			if (remaining <= 0L) {
				throw new AutomationException(
					AutomationErrorCodes.TIMEOUT,
					"Timed out waiting for next display",
					Json.object()
						.set("condition", "next-display")
						.set("elapsedMs", elapsed)
						.set("lastState", WorkerSessionSnapshot.build(false)));
			}
			if (WorkerEventModel.cursor() != cursor) {
				continue;
			}
			try {
				if (!WorkerEventModel.awaitEventAfter(cursor, remaining)) {
					throw new AutomationException(
						AutomationErrorCodes.TIMEOUT,
						"Timed out waiting for next display",
						Json.object()
							.set("condition", "next-display")
							.set("elapsedMs", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start))
							.set("lastState", WorkerSessionSnapshot.build(false)));
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new AutomationException(
					AutomationErrorCodes.WORKER_FAILURE,
					"Interrupted while waiting for next display",
					null,
					e);
			}
		}
	}

	static Json readEvents(Json request) {
		long since = request.at("since", 0L).asLong();
		return Json.object()
			.set("schemaVersion", 2)
			.set("since", since)
			.set("cursor", WorkerEventModel.cursor())
			.set("events", WorkerEventModel.eventsSince(since));
	}
}
