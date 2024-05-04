package emulator;

import java.util.*;

public final class Settings {
    public static int g2d = 1; // swt - 0, awt - 1
    public static boolean enableVibration;
    public static boolean enableKeyRepeat;
    public static boolean ignoreFullScreen;
    public static boolean alwaysOnTop;
    public static boolean enableKeyCache;
    public static boolean associateWithJar;
    public static boolean rightClickMenu;
    public static int canvasScale = 100;
    public static int frameRate = 60;
    public static int steps = -1;
    public static long aLong1235;
    public static String fileEncoding;
    public static String[] recentJars = new String[5];
    public static boolean infoColorHex;
    public static boolean recordReleasedImg;
    public static boolean xrayView;
    public static boolean xrayOverlapScreen;
    public static boolean xrayShowClipBorder;
    public static int speedModifier = 1;
    public static boolean autoGenJad;
    public static boolean enableMethodTrack;
    public static boolean enableNewTrack;
    public static boolean threadMethodTrack;
    public static final long rngSeed = new Random().nextLong();
    public static long recordedRandomSeed = rngSeed;
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
    public static boolean aBoolean1274 = false;
    public static boolean canvasKeyboard;
    public static boolean awtAntiAliasing;
    public static boolean fpsMode;
    public static boolean textAntiAliasing = true;
    // 0 - OTHER, 1 - BOUNCETALES, 2 - MICRO CS, 3 - QUANTUM FIXME
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
    public static boolean motorolaSoftKeyFix = false;
    public static int g3d = 1; // 0 - swerve, 1 - lwjgl
    public static int resizeMode = 2; // 0 - center, 1 - sync, 2 - fill
    public static boolean keepAspectRatio = true;
    public static boolean integerResize = false;
    public static boolean patchSynchronizedPaint = true;

    //m3g
    public static boolean m3gIgnoreOverwrite;
    public static boolean m3gForcePerspectiveCorrection;
    public static boolean m3gDisableLightClamp;

    public static final int APP_CONTROLLED = 0;

    public static final int AA_OFF = 1, AA_ON = 2;
    public static int m3gAA;

    public static final int TEX_FILTER_NEAREST = 1, TEX_FILTER_LINEAR = 2;
    public static int m3gTexFilter;

    public static final int MIP_OFF = 1, MIP_LINEAR = 2, MIP_TRILINEAR = 3,
            MIP_ANISO_2 = 4, MIP_ANISO_4 = 5, MIP_ANISO_8 = 6, MIP_ANISO_16 = 7;
    public static int m3gMipmapping;

    public static boolean writeKemCfg = false;

    public static boolean reopenMidiDevice = false;
    public static boolean searchVms;

    public static int xmx = 512;
}
