package emulator;

import emulator.ui.swt.ResizeMethod;

import java.util.*;

public final class Settings {

	// network
	public static int proxyType;
	public static String proxyHost;
	public static String proxyPort;
	public static String proxyDomain;
	public static String proxyUser;
	public static String proxyPass;
	public static boolean networkNotAvailable;

	// 2d and 3d engines
	public static int g2d = 1; // swt - 0, awt - 1
	public static int g3d = 1; // 0 - swerve, 1 - lwjgl
	public static int micro3d = 1; // 0 - dll, 1 - gl
	public static int m3gContextMode; // 0 - auto, 1 - glcanvas, 2 - lwjglx canvas, 3 - glfw window

	// security and system properties
	public static boolean enableSecurity = true;
	public static final Set<String> protectedPackages = new HashSet<String>();
	public static final Map<String, String> systemProperties = new HashMap<String, String>();

	// ota
	public static String updateBranch = "stable";
	public static int autoUpdate = 0; // 0 - not asked, 1 - manual, 2 - automatic

	// ui
	public static String[] recentJars = new String[5];
	public static boolean alwaysOnTop;
	public static float canvasScale;
	public static boolean showLogFrame;
	public static boolean showInfoFrame;
	public static boolean showMemViewFrame;
	public static String uiLanguage = "en";
	public static int interpolation;
	public static boolean altLessSpeedShortcuts;
	public static boolean showAppSettingsOnStart = true;
	public static boolean globalSettings = false;
	public static boolean fpsCounter = true;
	public static ResizeMethod resizeMode = ResizeMethod.Fit;
	public static boolean enableVibration;
	public static boolean infoColorHex;
	public static boolean autoGenJad;

	public static boolean rpc;

	// debug
	public static boolean recordReleasedImg;
	public static boolean xrayOverlapScreen;
	public static boolean xrayShowClipBorder;
	public static boolean enableMethodTrack;
	public static boolean enableNewTrack;
	public static boolean threadMethodTrack;

	public static boolean jdwpDebug;
	public static int debugPort = 29292;

	public static int xmx = 512;

	// input
	public static boolean enableKeyCache;
	public static boolean canvasKeyboard;

	public static long aLong1235;
	public static final long rngSeed = new Random().nextLong();
	public static long recordedRandomSeed = rngSeed;
	public static boolean playingRecordedKeys;
	public static boolean recordKeys;
	public static String recordedKeysFile;

	// controller
	public static Map<String, String> controllerBinds = new HashMap<String, String>();

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

	// legacy tweaks
	public static boolean hideEmulation;
	public static boolean hideDisplayableOnMinimize;
	public static boolean ignoreRegionRepaint;
	public static boolean ignoreServiceRepaints;
	public static boolean dontRepaintOnSetCurrent;
	public static boolean bypassVserv = true;
	public static boolean pollKeyboardOnRepaint = true;
	public static boolean queueSleep = true;

	public static boolean awtAntiAliasing;
	public static boolean textAntiAliasing = true;

	// media
	public static boolean enableVlc = true;
	public static String soundfontPath = "";
	public static boolean reopenMidiDevice = true;
	public static boolean searchVms;
	public static int ottDecoder = 2; // 0 - disabled, 1 - kemulator, 2 - freej2me
	public static boolean enableMediaDump;
	public static boolean wavCache = true;
	public static boolean disableCamera;
	public static boolean oneMidiAtTime;
	public static String vlcDir = "";
	public static boolean playerBufferAll = false;

	// devutils
	public static String ideaPath; // absolute path to idea binary
	public static String proguardPath; // absolute path to proguard.jar
	public static boolean ideaJdkTablePatched = false;
	public static String lastIdeaRepoPath = ""; // where last project was created

	public static String deviceFile = "/res/presets.xml";

}
