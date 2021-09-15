package emulator.media;

import emulator.*;

public class MMFPlayer
{
    static boolean aBoolean406;
    
    public MMFPlayer() {
        super();
    }
    
    public static boolean a() {
        if (MMFPlayer.aBoolean406) {
            return true;
        }
        try {
            i.a("mmfplayer");
            initMMFLibrary(Emulator.getAbsolutePath() + "/ma3smwemu.dll");
            MMFPlayer.aBoolean406 = true;
            return MMFPlayer.aBoolean406;
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {}
        catch (Exception ex) {}
        MMFPlayer.aBoolean406 = false;
        return MMFPlayer.aBoolean406;
    }
    
    public static void method200() {
        if (MMFPlayer.aBoolean406) {
            stop();
            destroy();
            MMFPlayer.aBoolean406 = false;
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
