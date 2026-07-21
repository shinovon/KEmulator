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
	public static boolean resolutionRestartMidlet = false;
	public static boolean disableTouchDoubleClick = false;
	public static String favoritesPath = null;
	public static String favoritesViewMode = "detailed";
	public static boolean favoritesShowDetails = true;
	// Icons mode settings
	public static int favoritesUISize_icons = 12;
	public static int favoritesTextSize_icons = 12;
	public static int favoritesPadding_icons = 5;
	public static int favoritesMask_icons = 31;
	public static boolean favoritesNames_icons = true;
	public static int favoritesIconSize_icons = 64;
	// Compact mode settings
	public static int favoritesUISize_compact = 12;
	public static int favoritesTextSize_compact = 12;
	public static int favoritesPadding_compact = 5;
	public static int favoritesMask_compact = 15;
	// Detailed mode settings
	public static int favoritesUISize_detailed = 12;
	public static int favoritesTextSize_detailed = 12;
	public static int favoritesPadding_detailed = 5;
	// Runtime-only (not persisted directly, swapped via per-mode fields)
	public static int favoritesCompactMask = 15;
	public static int favoritesIconsMask = 31;
	public static boolean favoritesIconsShowNames = true;
	public static String pendingFavoriteMoves = null;
	public static String lastJarDir = null;
	public static int lastJarIndex = -1;
	public static boolean favoritesDarkMode = false;
	public static boolean fpsCounter = true;
	public static ResizeMethod resizeMode = ResizeMethod.Fit;

	public static final int[][] resolutionPresets = {
		{96, 64},
		{96, 65},
		{101, 64},
		{101, 80},
		{120, 160},
		{128, 96},
		{128, 110},
		{128, 112},
		{128, 116},
		{128, 128},
		{128, 131},
		{128, 148},
		{128, 149},
		{128, 160},
		{130, 130},
		{132, 176},
		{176, 176},
		{176, 182},
		{176, 204},
		{176, 205},
		{176, 206},
		{176, 208},
		{176, 220},
		{180, 320},
		{208, 173},
		{208, 208},
		{220, 176},
		{240, 240},
		{240, 266},
		{240, 297},
		{240, 298},
		{240, 299},
		{240, 304},
		{240, 320},
		{240, 400},
		{240, 432},
		{240, 480},
		{320, 180},
		{320, 224},
		{320, 240},
		{345, 800},
		{352, 416},
		{360, 640},
		{400, 240},
		{480, 800},
		{640, 360},
		{640, 480},
		{800, 345},
		{800, 480}
	};
	public static final String[] resolutionPresetNames = {
		"96x64",
		"96x65",
		"101x64",
		"101x80",
		"120x160",
		"128x96",
		"128x110",
		"128x112",
		"128x116",
		"128x128",
		"128x131",
		"128x148",
		"128x149",
		"128x160",
		"130x130",
		"132x176",
		"176x176",
		"176x182",
		"176x204",
		"176x205",
		"176x206",
		"176x208",
		"176x220",
		"180x320",
		"208x173",
		"208x208",
		"220x176",
		"240x240",
		"240x266",
		"240x297",
		"240x298",
		"240x299",
		"240x304",
		"240x320 (COMMON - full screen)",
		"240x400",
		"240x432",
		"240x480",
		"320x180",
		"320x224",
		"320x240",
		"345x800",
		"352x416",
		"360x640",
		"400x240",
		"480x800",
		"640x360",
		"640x480",
		"800x345",
		"800x480"
	};
	public static int currentPresetIdx = 33;

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
	public static boolean storeCreatedImages = true;

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

	public static boolean awtAntiAliasing;
	public static boolean textAntiAliasing = true;
	public static boolean lcduiSystemColors;

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
	public static boolean maMidi;

	// gamepad
	public static boolean j2meGamepadEnabled = false;
	public static boolean j2meGamepadAutoLaunch = false;
	public static String j2meGamepadPath;

	// devutils
	public static String ideaPath; // absolute path to idea binary
	public static String proguardPath; // absolute path to proguard.jar
	public static boolean ideaJdkTablePatched = false;
	public static String lastIdeaRepoPath = ""; // where last project was created

	public static String deviceFile = "/res/presets.xml";

	// lucky folder
	public static String[] luckyFolderPaths = new String[0];
	public static boolean[] luckyFolderFavBrowserMode = new boolean[0]; // true = show in fav browser, false = random
	public static int luckyFolderBrowserIndex; // 0 = favorites, 1+ = lucky folder index
}
