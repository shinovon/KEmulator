package emulator;

final class InvokeStartAppRunnable implements Runnable {
	private final boolean first;

	InvokeStartAppRunnable(boolean first) {
		this.first = first;
	}

	public final void run() {
		Emulator.getMIDlet().invokeStartApp();
		Emulator.getEmulator().getScreen().appStarted(first);
	}
}
