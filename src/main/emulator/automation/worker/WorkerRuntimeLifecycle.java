package emulator.automation.worker;

import emulator.Emulator;
import emulator.EventQueue;
import emulator.automation.shared.ProcessIdentity;
import emulator.automation.shared.TextValues;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

final class WorkerRuntimeLifecycle {
	interface ServerLoop {
		void run(ServerSocket serverSocket);
	}

	private static ServerSocket serverSocket;
	private static Thread serverThread;
	private static Thread watchdogThread;

	private WorkerRuntimeLifecycle() {
	}

	static void requestShutdown(String reason) {
		if (!WorkerRuntimeState.requestShutdown()) {
			return;
		}

		WorkerPermissions.cancelAll();
		new Thread(
			new Runnable() {
				public void run() {
					try {
						Thread.sleep(100L);
					} catch (InterruptedException ignored) {
						Thread.currentThread().interrupt();
					}

					try {
						if (serverSocket != null) {
							serverSocket.close();
						}
					} catch (IOException ignored) {
					}

					if (Emulator.getEventQueue() != null && Emulator.getMIDlet() != null) {
						Emulator.getEventQueue().queue(EventQueue.EVENT_EXIT);
					} else {
						System.exit(0);
					}
				}
			},
			"KEmulator-Automation-Worker-Shutdown-" + reason)
			.start();
	}

	private static void startControllerWatchdog() {
		if (watchdogThread != null
			|| TextValues.isBlank(WorkerRuntimeState.controllerPid())) {
			return;
		}

		watchdogThread = new Thread(
			new Runnable() {
				public void run() {
					while (!WorkerRuntimeState.isShutdownRequested()) {
						try {
							Thread.sleep(2000L);
						} catch (InterruptedException ignored) {
							Thread.currentThread().interrupt();

							return;
						}

						if (WorkerRuntimeState.isShutdownRequested()) {
							return;
						}

						Boolean controllerMatches = ProcessIdentity.matches(
							WorkerRuntimeState.controllerPid(),
							WorkerRuntimeState.controllerStartTimeMillis(),
							WorkerRuntimeState.controllerStartTicks());
						if (Boolean.FALSE.equals(controllerMatches)) {
							requestShutdown("controller-exited");

							return;
						}
					}
				}
			},
			"KEmulator-Automation-Worker-Watchdog");
		watchdogThread.setDaemon(true);
		watchdogThread.start();
	}

	static void startIfEnabled(final ServerLoop serverLoop) {
		if (!WorkerRuntimeState.isEnabled() || serverSocket != null || WorkerRuntimeState.port() <= 0) {
			return;
		}

		WorkerRuntimeState.resetShutdownRequested();
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(WorkerRuntimeState.bindHost(), WorkerRuntimeState.port()));
			serverThread = new Thread(
				new Runnable() {
					public void run() {
						serverLoop.run(serverSocket);
					}
				},
				"KEmulator-Automation-Worker-Server");
			serverThread.setDaemon(true);
			serverThread.start();
			startControllerWatchdog();
		} catch (IOException e) {
			throw new RuntimeException("Failed to start automation worker runtime", e);
		}
	}
}
