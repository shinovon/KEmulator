package emulator;

import java.util.*;

public final class Settings {
    /*
     * awt - 1
     * swt - 0
     */
    public static int g2d;
    public static boolean enableVibration;
    public static boolean enableKeyRepeat;
    public static boolean ignoreFullScreen;
    public static boolean alwaysOnTop;
    public static boolean enableKeyCache;
    public static boolean associateWithJar;
    public static boolean rightClickMenu;
    public static int canvasScale;
    public static int frameRate;
    public static int steps;
    public static long aLong1235;
    public static String fileEncoding;
    public static String[] recentJars;
    public static boolean infoColorHex;
    public static boolean recordReleasedImg;
    public static boolean xrayView;
    public static boolean xrayOverlapScreen;
    public static boolean xrayShowClipBorder;
    public static int speedModifier;
    public static boolean autoGenJad;
    public static boolean enableMethodTrack;
    public static boolean enableNewTrack;
    public static boolean threadMethodTrack;
    private static final Random rng;
    public static final long rngSeed;
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
    public static String locale = "en-US";
    public static boolean jdwpDebug;
    public static int debugPort = 29292;
    public static Map<String, String> controllerBinds = new HashMap<String, String>();
    public static boolean pollKeyboardOnRepaint = true;
    public static String uiLanguage = "en";
    public static boolean rpc;
    public static boolean uei;
    public static boolean fpsCounter = true;
    public static boolean synchronizeKeyEvents = true;
    public static boolean motorolaSoftKeyFix = true;
    public static int g3d; // 0 - swerve, 1 - lwjgl

    public Settings() {
        super();
    }

    static {
        Settings.g2d = 1;
        Settings.g3d = 1;
        Settings.canvasScale = 100;
        Settings.frameRate = 60;
        Settings.steps = -1;
        Settings.recentJars = new String[5];
        Settings.speedModifier = 1;
        rng = new Random();
        rngSeed = Settings.rng.nextLong();
        Settings.recordedRandomSeed = Settings.rngSeed;
        Settings.aBoolean1274 = false;
    }
}
