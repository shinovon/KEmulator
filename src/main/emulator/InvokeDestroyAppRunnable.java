package emulator;

public final class InvokeDestroyAppRunnable implements Runnable {
    InvokeDestroyAppRunnable(final EventQueue j) {
        super();
    }

    public final void run() {
        Emulator.getMIDlet().invokeDestroyApp(true);
    }
}
