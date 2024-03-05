package emulator.media;

import emulator.*;

public class AVIWriter {
    public AVIWriter() {
        super();
    }

    public final boolean method841(final String s, final int n, final int n2, final int n3) {
        try {
            return this.startAVI(s, n, n2, n3);
        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            Emulator.getEmulator().getLogStream().println("+++ emulator.dll not loaded +++");
        } catch (Exception ex) {
        }
        return false;
    }

    public final boolean method842() {
        try {
            this.finishAVI();
        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            Emulator.getEmulator().getLogStream().println("+++ emulator.dll not loaded +++");
        } catch (Exception ex) {
        }
        return true;
    }

    public final void method843(final int[] array) {
        try {
            this.addFrameAVI(array);
        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            Emulator.getEmulator().getLogStream().println("+++ emulator.dll not loaded +++");
        } catch (Exception ex) {
        }
    }

    public native boolean startAVI(final String p0, final int p1, final int p2, final int p3);

    public native void finishAVI();

    public native void addFrameAVI(final int[] p0);
}
