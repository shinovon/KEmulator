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
            try {
                //String arch = System.getProperty("os.arch");
                //if(arch.equals("x86")) arch = "";
                //else if(arch.equals("amd64")) arch = "64";
                // System.load(Emulator.getAbsolutePath() + "/" + s + arch + ".dll");
                System.load(Emulator.getAbsolutePath() + "/" + s + ".dll");
            } catch (UnsatisfiedLinkError unsatisfiedLinkError2) {
                unsatisfiedLinkError2.printStackTrace();
                System.loadLibrary(s);
            }
        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            throw unsatisfiedLinkError;
        }
    }
}
