package emulator.automation.worker;

import mjson.Json;

public final class AutomationWorkerRuntime {
	private AutomationWorkerRuntime() {
	}

	public static void setEnabled(boolean enabled) {
		WorkerRuntimeState.setEnabled(enabled);
	}

	public static void setBindHost(String bindHost) {
		WorkerRuntimeState.setBindHost(bindHost);
	}

	public static void setPort(int port) {
		WorkerRuntimeState.setPort(port);
	}

	public static void setControllerPid(String controllerPid) {
		WorkerRuntimeState.setControllerPid(controllerPid);
	}

	public static void setControllerStartTimeMillis(String controllerStartTimeMillis) {
		WorkerRuntimeState.setControllerStartTimeMillis(controllerStartTimeMillis);
	}

	public static void setControllerStartTicks(String controllerStartTicks) {
		WorkerRuntimeState.setControllerStartTicks(controllerStartTicks);
	}

	public static void setReadyFile(String readyFile) {
		WorkerRuntimeState.setReadyFile(readyFile);
	}

	public static boolean isEnabled() {
		return WorkerRuntimeState.isEnabled();
	}

	public static void onMidletStarted(boolean first) {
		WorkerRuntimeState.setMidletStarted(true);
		WorkerEventModel.stateChanged(
			"worker-ready",
			Json.object().set("firstStart", first));
		WorkerRuntimeLifecycle.writeReadyMarker();
	}

	public static boolean requestPermission(String message) {
		return WorkerPermissions.request(null, message);
	}

	public static boolean requestPermission(String name, String message) {
		return WorkerPermissions.request(name, message);
	}

	public static void startIfEnabled() {
		WorkerRuntimeLifecycle.startIfEnabled(WorkerSocketServer.serverLoop());
	}

	public static void onDisplayChanged(String kind, String title) {
		if (!isEnabled()) {
			return;
		}
		WorkerEventModel.stateChanged(
			"display-changed",
			Json.object().set("kind", kind).set("title", title));
	}

	public static void onDisplayStateChanged(String event) {
		if (isEnabled()) {
			WorkerEventModel.stateChanged(event, null);
		}
	}

	public static void onSelectionChanged(int selectedIndex) {
		if (isEnabled()) {
			WorkerEventModel.stateChanged(
				"selection-changed",
				Json.object().set("selectedIndex", selectedIndex));
		}
	}

	public static void onInputDispatched() {
		if (isEnabled()) {
			WorkerEventModel.stateChanged("input-dispatched", null);
		}
	}

	public static void onFrameRendered() {
		if (isEnabled()) {
			WorkerEventModel.frameRendered();
		}
	}
}
