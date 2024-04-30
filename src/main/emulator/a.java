package emulator;

final class a implements Runnable {
    private boolean aBoolean1048;
    private Runnable aRunnable1049;
    private final EventQueue aj1050;

    private a(final EventQueue aj1050) {
        super();
        this.aj1050 = aj1050;
    }

    public final void method590(final Runnable aRunnable1049) {
        if (aRunnable1049 != null) {
            this.aRunnable1049 = aRunnable1049;
            this.aBoolean1048 = true;
        }
    }

    public final void run() {
        while (EventQueue.method723(this.aj1050)) {
            if (this.aBoolean1048) {
                this.aBoolean1048 = false;
                this.aRunnable1049.run();
            }
            try {
                Thread.sleep(1L);
            } catch (Exception ignored) {}
        }
    }

    a(final EventQueue j, final InvokeStartAppRunnable c) {
        this(j);
    }
}
