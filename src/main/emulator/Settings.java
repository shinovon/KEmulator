package emulator;

import java.util.*;

public final class Settings
{
	/*
	 * awt - 1
	 * swt - 0
	 */
    public static int g2d;
    /*
     * 1 - lwj
     * 0 - wgl
     */
    public static int g3d;
    public static boolean enableVibration;
    public static boolean enableKeyRepeat;
    public static boolean ignoreFullScreen;
    public static boolean alwaysOnTop;
    public static boolean enableKeyCache;
    public static boolean associateWithJar;
    public static boolean rightClickMenu;
    public static int canvasScale;
    public static int frameRate;
    public static int e;
    public static long aLong1235;
    public static String fileEncoding;
    public static String[] aArray;
    public static boolean infoColorHex;
    public static boolean recordReleasedImg;
    public static boolean xrayView;
    public static boolean xrayOverlapScreen;
    public static boolean xrayShowClipBorder;
    public static int f;
    public static boolean autoGenJad;
    public static boolean enableMethodTrack;
    public static boolean enableNewTrack;
    public static boolean threadMethodTrack;
    private static final Random aRandom1238;
    public static final long aLong1241;
    public static long recordedRandomSeed;
    public static boolean playingRecordedKeys;
    public static boolean recordKeys;
    public static String recordedKeysFile;
    public static int proxyType;
    public static String proxyHost;
    public static String proxyPort;
    public static String proxyDomain;
    public static String proxyUser;
    public static String proxyPass;
    public static boolean networkNotAvailable;
    public static boolean showLogFrame;
    public static boolean showInfoFrame;
    public static boolean showMemViewFrame;
    public static boolean aBoolean1274;
	public static boolean canvasKeyboard;
	public static boolean awtAntiAliasing;
	public static boolean fpsMode;
	public static boolean textAntiAliasing = true;
	// 0 - OTHER, 1 - BOUNCETALES, 2 - MICRO CS, 3 - QUANTUM
	public static int fpsGame;
	public static boolean playerBufferAll = false;
	public static String vlcDir = "";
    
    public Settings() {
        super();
    }
    
    static {
        Settings.g2d = 1;
        Settings.g3d = 1;
        Settings.canvasScale = 100;
        Settings.frameRate = 30;
        Settings.e = -1;
        Settings.aArray = new String[5];
        Settings.f = 1;
        aRandom1238 = new Random();
        aLong1241 = Settings.aRandom1238.nextLong();
        Settings.recordedRandomSeed = Settings.aLong1241;
        Settings.aBoolean1274 = false;
    }
}
