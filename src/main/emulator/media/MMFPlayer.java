package emulator.media;

import emulator.*;

public class MMFPlayer {
    static boolean initialized;

    public MMFPlayer() {
        super();
    }

    public static boolean a() {
        if (MMFPlayer.initialized) {
            return true;
        }
        try {
            i.a("mmfplayer");
            initMMFLibrary(Emulator.getAbsolutePath() + "/ma3smwemu.dll");
            return MMFPlayer.initialized = true;
        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
        } catch (Exception ex) {
        }
        return MMFPlayer.initialized = false;
    }

    public static void close() {
        if (MMFPlayer.initialized) {
            stop();
            destroy();
            MMFPlayer.initialized = false;
        }
    }

    public static native int initMMFLibrary(final String p0);

    public static native void initPlayer(final byte[] p0);

    public static native void play(final int p0, final int p1);

    public static native void destroy();

    public static native boolean isPlaying();

    public static native void stop();

    public static native void pause();

    public static native void resume();
}
