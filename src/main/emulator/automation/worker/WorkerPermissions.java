package emulator.automation.worker;

import emulator.automation.shared.AutomationErrorCodes;
import emulator.automation.shared.AutomationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import mjson.Json;

final class WorkerPermissions {
	private static final Object LOCK = new Object();
	private static int permissionCounter = 1;
	private static final Map<Integer, PendingPermission> pendingPermissions = new HashMap<Integer, PendingPermission>();
	private static final Queue<Integer> pendingPermissionOrder = new LinkedList<Integer>();

	private WorkerPermissions() {
	}

	static final class PendingPermission {
		final int id;
		final String message;
		private final Object lock = new Object();
		private int response = -1;

		private PendingPermission(int id, String message) {
			this.id = id;
			this.message = message;
		}

		boolean await() {
			synchronized (lock) {
				while (response == -1) {
					try {
						lock.wait();
					} catch (InterruptedException ignored) {
						Thread.currentThread().interrupt();

						return false;
					}
				}

				return response == 1;
			}
		}

		void resolve(boolean allow) {
			synchronized (lock) {
				response = allow ? 1 : 0;
				lock.notifyAll();
			}
		}

		void cancel() {
			synchronized (lock) {
				if (response == -1) {
					response = 0;
				}

				lock.notifyAll();
			}
		}

		Json toJson() {
			return Json.object().set("id", id).set("message", message);
		}
	}

	private static int nextPermissionId() {
		synchronized (LOCK) {
			return permissionCounter++;
		}
	}

	static void cancelAll() {
		synchronized (LOCK) {
			for (PendingPermission permission : pendingPermissions.values()) {
				if (permission != null) {
					permission.cancel();
				}
			}

			pendingPermissions.clear();
			pendingPermissionOrder.clear();
			LOCK.notifyAll();
		}
	}

	static PendingPermission snapshot() {
		synchronized (LOCK) {
			while (!pendingPermissionOrder.isEmpty()) {
				Integer id = pendingPermissionOrder.peek();
				PendingPermission permission = pendingPermissions.get(id);
				if (permission != null) {
					return permission;
				}

				pendingPermissionOrder.remove();
			}

			return null;
		}
	}

	static void resolve(int id, boolean allow) {
		PendingPermission permission;
		synchronized (LOCK) {
			Integer head = pendingPermissionOrder.peek();
			if (head == null) {
				throw new AutomationException(
					AutomationErrorCodes.UNKNOWN_PERMISSION_ID,
					"Unknown permission id: " + id,
					Json.object().set("id", id));
			}

			if (head.intValue() != id) {
				throw new AutomationException(
					AutomationErrorCodes.PERMISSION_ORDER_VIOLATION,
					"Only the head pending permission may be answered. Expected id " + head + ", got " + id,
					Json.object().set("expectedId", head.intValue()).set("requestedId", id));
			}

			permission = pendingPermissions.remove(head);
			pendingPermissionOrder.remove(head);
		}

		permission.resolve(allow);
	}

	static boolean request(String message) {
		if (WorkerRuntimeState.isShutdownRequested()) {
			return false;
		}

		PendingPermission request = new PendingPermission(nextPermissionId(), message);
		synchronized (LOCK) {
			if (WorkerRuntimeState.isShutdownRequested()) {
				return false;
			}

			pendingPermissions.put(Integer.valueOf(request.id), request);
			pendingPermissionOrder.add(Integer.valueOf(request.id));
			LOCK.notifyAll();
		}

		return request.await();
	}
}
