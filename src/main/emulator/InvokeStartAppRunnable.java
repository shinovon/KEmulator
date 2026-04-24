package emulator;

import emulator.automation.worker.AutomationWorkerRuntime;
import emulator.custom.CustomMethod;

final class InvokeStartAppRunnable implements Runnable {
	private final boolean first;

	InvokeStartAppRunnable(boolean first) {
		this.first = first;
	}

	public final void run() {
		try {
			Emulator.getMIDlet().invokeStartApp();
			Emulator.getEmulator().getScreen().appStarted(first);
			AutomationWorkerRuntime.onMidletStarted(first);
		} catch (RuntimeException e) {
			if (e.getCause() == null || !first) {
				throw e;
			}
			Emulator.getEmulator().getScreen().showMessageThreadSafe(
					UILocale.get("FAIL_LAUNCH_MIDLET",
					"Fail to launch the MIDlet class:") + " " + Emulator.midletClassName,
					CustomMethod.getStackTrace(e.getCause())
			);
		}
	}
}
