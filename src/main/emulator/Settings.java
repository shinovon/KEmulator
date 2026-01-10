package emulator;

import emulator.ui.swt.ResizeMethod;

import java.util.*;

public final class Settings {
	// original settings
	public static final int G2D_SWT = 0, G2D_AWT = 1;
	public static int g2d = G2D_AWT; // swt - 0, awt - 1
	public static boolean enableVibration;
	public static boolean alwaysOnTop;
	public static boolean enableKeyCache;
	public static boolean rightClickMenu;
	public static float canvasScale;
	public static long aLong1235;
	public static String[] recentJars = new String[5];
	public static boolean infoColorHex;
	public static boolean recordReleasedImg;
	public static boolean xrayOverlapScreen;
	public static boolean xrayShowClipBorder;
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

	// nnmod

	public static boolean canvasKeyboard;
	public static boolean awtAntiAliasing;
	public static boolean textAntiAliasing = true;
	public static boolean playerBufferAll = false;
	public static String vlcDir = "";


	public static boolean jdwpDebug;
	public static int debugPort = 29292;
	public static Map<String, String> controllerBinds = new HashMap<String, String>();
	public static boolean pollKeyboardOnRepaint = true;
	public static String uiLanguage = "en";
	public static boolean rpc;
	public static boolean fpsCounter = true;
	public static int g3d = 1; // 0 - swerve, 1 - lwjgl
	public static ResizeMethod resizeMode = ResizeMethod.Fit;

	//m3g
	public static boolean m3gThread = true;
	public static int m3gContextMode; // 0 - auto, 1 - glcanvas, 2 - lwjglx canvas, 3 - glfw window

	public static boolean reopenMidiDevice = true;
	public static boolean searchVms;

	public static int xmx = 512;
	public static boolean oneMidiAtTime;
	public static boolean ignoreRegionRepaint;
	public static int micro3d = 1; // 0 - dll, 1 - gl

	public static int ottDecoder = 2; // 0 - disabled, 1 - kemulator, 2 - freej2me


	public static boolean bypassVserv = true;
	public static boolean enableMediaDump;

	public static final Set<String> protectedPackages = new HashSet<String>();
	public static final Map<String, String> systemProperties = new HashMap<String, String>();

	public static boolean enableSecurity = true;
	public static boolean ignoreServiceRepaints;
	public static boolean dontRepaintOnSetCurrent;
	public static boolean wavCache = true;
	public static boolean queueSleep = true;
	public static String updateBranch = "stable";
	public static int autoUpdate = 0; // 0 - not asked, 1 - manual, 2 - automatic

	public static boolean disableCamera;

	public static int interpolation;
	// 0: ignored
	// 1: map horizontal, 2 - map vertical
	// 3: direct dpad horizontal, 4: direct dpad vertical
	// 5: map to own button
	public static int controllerPovXMap = 1,
			controllerPovYMap = 2,
			controllerXMap = 1,
			controllerYMap = 2,
			controllerZMap = 3, // should be ignored on xbox
			controllerRXMap = 5,
			controllerRYMap = 5,
			controllerRZMap = 4; // should be ignored on xbox

	// controller axis threshold
	public static float axisFilter = 0.05f, axisThreshold = 0.7f;
	public static boolean controllerInverseHor;
	public static boolean controllerInverseVer;

	// devutils
	public static String ideaPath; // absolute path to idea binary
	public static String proguardPath; // absolute path to proguard.jar
	public static boolean ideaJdkTablePatched = false;
	public static String lastIdeaRepoPath = ""; // where last project was created

  // hacks
	public static boolean patchYield;
	public static boolean ignoreGc = true;
	public static boolean patchSleep;
	public static boolean ignoreSleep;
	public static boolean applySpeedToSleep;
	public static boolean altLessSpeedShortcuts;
	public static boolean hideEmulation;
	public static boolean hideDisplayableOnMinimize;

	public static boolean enableVlc = true;
	public static String soundfontPath = "";
	public static boolean showAppSettingsOnStart = true;
}
