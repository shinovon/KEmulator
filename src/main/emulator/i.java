package emulator;

public final class i {
    public i() {
        super();
    }

    /**
     * loadLibrary
     * x32
     */
    public static void a(final String s) {
        if (Emulator.isX64()) throw new UnsatisfiedLinkError("x64 version!");
        try {
            System.load(Emulator.getAbsolutePath() + "/" + s + ".dll");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            System.loadLibrary(s);
        }
    }
}
