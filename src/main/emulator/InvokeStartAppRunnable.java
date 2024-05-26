package emulator;

final class InvokeStartAppRunnable implements Runnable {
	InvokeStartAppRunnable(final EventQueue j) {
		super();
	}

	public final void run() {
		Emulator.getMIDlet().invokeStartApp();
	}
}
