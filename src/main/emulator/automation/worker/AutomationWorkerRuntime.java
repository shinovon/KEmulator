package emulator.automation.worker;

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

	public static boolean isEnabled() {
		return WorkerRuntimeState.isEnabled();
	}

	public static void onMidletStarted(boolean first) {
		WorkerRuntimeState.setMidletStarted(true);
	}

	public static boolean requestPermission(String message) {
		return WorkerPermissions.request(message);
	}

	public static void startIfEnabled() {
		WorkerRuntimeLifecycle.startIfEnabled(WorkerSocketServer.serverLoop());
	}
}
