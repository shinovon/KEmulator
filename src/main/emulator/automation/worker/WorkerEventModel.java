package emulator.automation.worker;

import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;
import mjson.Json;

final class WorkerEventModel {
	private static final Object LOCK = new Object();
	private static final int MAX_EVENTS = 256;
	private static final ArrayDeque<Json> EVENTS = new ArrayDeque<Json>();
	private static long revision;
	private static long frameRevision;
	private static long eventCursor;

	private WorkerEventModel() {
	}

	static long revision() {
		synchronized (LOCK) {
			return revision;
		}
	}

	static long frameRevision() {
		synchronized (LOCK) {
			return frameRevision;
		}
	}

	static long cursor() {
		synchronized (LOCK) {
			return eventCursor;
		}
	}

	static long stateChanged(String event, Json details) {
		synchronized (LOCK) {
			revision++;
			appendEventLocked(event, revision, details);
			return revision;
		}
	}

	static long frameRendered() {
		synchronized (LOCK) {
			if (frameRevision >= revision) {
				return frameRevision;
			}
			frameRevision = revision;
			Json details = Json.object().set("frameRevision", frameRevision);
			appendEventLocked("frame-rendered", revision, details);
			return frameRevision;
		}
	}

	private static void appendEventLocked(String event, long eventRevision, Json details) {
		eventCursor++;
		Json item = Json.object()
			.set("schemaVersion", 2)
			.set("cursor", eventCursor)
			.set("revision", eventRevision)
			.set("event", event)
			.set("timestamp", System.currentTimeMillis());
		if (details != null && details.isObject()) {
			for (String key : details.asJsonMap().keySet()) {
				item.set(key, details.at(key));
			}
		}
		EVENTS.addLast(item);
		while (EVENTS.size() > MAX_EVENTS) {
			EVENTS.removeFirst();
		}
		LOCK.notifyAll();
	}

	static boolean awaitEventAfter(long afterCursor, long timeoutMs) throws InterruptedException {
		long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(Math.max(0L, timeoutMs));
		synchronized (LOCK) {
			while (eventCursor <= afterCursor) {
				long remaining = deadline - System.nanoTime();
				if (remaining <= 0L) {
					return false;
				}
				TimeUnit.NANOSECONDS.timedWait(LOCK, remaining);
			}
			return true;
		}
	}

	static boolean awaitFrameAfter(long afterRevision, long timeoutMs) throws InterruptedException {
		long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(Math.max(0L, timeoutMs));
		synchronized (LOCK) {
			while (frameRevision <= afterRevision) {
				long remaining = deadline - System.nanoTime();
				if (remaining <= 0L) {
					return false;
				}
				TimeUnit.NANOSECONDS.timedWait(LOCK, remaining);
			}
			return true;
		}
	}

	static Json eventsSince(long afterCursor) {
		Json result = Json.array();
		synchronized (LOCK) {
			for (Json event : EVENTS) {
				if (event.at("cursor", 0L).asLong() > afterCursor) {
					result.add(event.dup());
				}
			}
		}
		return result;
	}
}
