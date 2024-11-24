package emulator;

import emulator.ui.swt.EmulatorScreen;

final class InvokeStartAppRunnable implements Runnable {
	private final boolean first;

	InvokeStartAppRunnable(boolean first) {
		this.first = first;
	}

	public final void run() {
		((EmulatorScreen) Emulator.getEmulator().getScreen()).appStarting(first);
		Emulator.getMIDlet().invokeStartApp();
		((EmulatorScreen) Emulator.getEmulator().getScreen()).appStarted(first);
	}
}
