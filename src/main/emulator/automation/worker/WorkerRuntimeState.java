package emulator.automation.worker;

import emulator.automation.shared.TextValues;

final class WorkerRuntimeState {
	private static boolean enabled;
	private static String bindHost = "127.0.0.1";
	private static int port;
	private static String controllerPid;
	private static Long controllerStartTimeMillis;
	private static String controllerStartTicks;
	private static volatile boolean midletStarted;
	private static volatile boolean shutdownRequested;

	private WorkerRuntimeState() {
	}

	static void setEnabled(boolean enabled) {
		WorkerRuntimeState.enabled = enabled;
	}

	static boolean isEnabled() {
		return enabled;
	}

	static void setBindHost(String bindHost) {
		String normalized = TextValues.trimToNull(bindHost);
		if (normalized != null) {
			WorkerRuntimeState.bindHost = normalized;
		}
	}

	static String bindHost() {
		return bindHost;
	}

	static void setPort(int port) {
		if (port > 0) {
			WorkerRuntimeState.port = port;
		}
	}

	static int port() {
		return port;
	}

	static void setControllerPid(String controllerPid) {
		String normalized = TextValues.trimToNull(controllerPid);
		if (normalized != null) {
			WorkerRuntimeState.controllerPid = normalized;
		}
	}

	static String controllerPid() {
		return controllerPid;
	}

	static void setControllerStartTimeMillis(String controllerStartTimeMillis) {
		String normalized = TextValues.trimToNull(controllerStartTimeMillis);
		if (normalized == null) {
			return;
		}

		try {
			WorkerRuntimeState.controllerStartTimeMillis = Long.valueOf(Long.parseLong(normalized));
		} catch (NumberFormatException ignored) {
		}
	}

	static Long controllerStartTimeMillis() {
		return controllerStartTimeMillis;
	}

	static void setControllerStartTicks(String controllerStartTicks) {
		String normalized = TextValues.trimToNull(controllerStartTicks);
		if (normalized != null) {
			WorkerRuntimeState.controllerStartTicks = normalized;
		}
	}

	static String controllerStartTicks() {
		return controllerStartTicks;
	}

	static void setMidletStarted(boolean midletStarted) {
		WorkerRuntimeState.midletStarted = midletStarted;
	}

	static boolean isMidletStarted() {
		return midletStarted;
	}

	static boolean isShutdownRequested() {
		return shutdownRequested;
	}

	static void resetShutdownRequested() {
		shutdownRequested = false;
	}

	static boolean requestShutdown() {
		if (shutdownRequested) {
			return false;
		}

		shutdownRequested = true;

		return true;
	}
}
