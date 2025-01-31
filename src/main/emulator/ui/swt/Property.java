package emulator.ui.swt;

import emulator.*;
import emulator.graphics2D.IFont;
import emulator.graphics2D.IGraphics2D;
import emulator.graphics2D.IImage;
import emulator.graphics2D.awt.FontAWT;
import emulator.graphics2D.swt.FontSWT;
import emulator.ui.IProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import javax.microedition.rms.RecordStore;
import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.charset.Charset;
import java.util.List;
import java.util.*;

public final class Property implements IProperty, SelectionListener {

	// список выключаемых апи
	public static final String[][] API_LIST = {
			// стандартные
			{"FileConnection (JSR 75)", "javax.microedition.io.file"},
			{"PIM (JSR 75) (Stub)", "javax.microedition.pim"},
			{"Bluetooth (JSR 82) (Stub)", "javax.bluetooth"},
			{"Messaging (JSR 120)", "javax.wireless.messaging"},
			{"Media (JSR 135)", "javax.microedition.media"},
			{"Crypto (JSR 177 SATSA)", "javax.crypto"}, // встроено в jre
			{"Location (JSR 179)", "javax.microedition.location"},
			{"M3G (JSR 184)", "javax.microedition.m3g"},
			{"ContentHandler (JSR 211) (Stub)", "javax.microedition.content"},
			{"Advanced Media (JSR 234)", "javax.microedition.amms"},
			{"OpenGL ES (JSR 239)", "javax.microedition.khronos"},
			{"Sensor (JSR 256)", "javax.microedition.sensor"},

			// вендоры
			{"Nokia", "com.nokia.mid"},
			{"Samsung", "com.samsung"},
			{"Siemens", "com.siemens"},
			{"Sprint", "com.sprintpcs"},
			{"Sony Ericsson", "com.sonyericsson"},
			{"Motorola", "com.motorola"},
			{"Vodafone", "com.vodafone"},
			{"LG MMPP", "mmpp"},

			// nokia по отдельности
			{"Nokia UI", "com.nokia.mid.ui"},
			{"Nokia Sound", "com.nokia.mid.sound"},
			{"Nokia M3D(O)", "com.nokia.mid.m3d"},
			{"Nokia Location", "com.nokia.mid.location"},
			{"Nokia In-App", "com.nokia.mid.payment"},
			{"Nokia IAPInfo (Stub)", "com.nokia.mid.iapinfo"},

			{"MascotCapsule", "com.mascotcapsule"},

			// японские
			{"MEXA OpenGL", "com.mexa"},
			{"com.j_phone", "com.j_phone"},
			{"com.jblend", "com.jblend"},

			// экзотические
			{"Immersion VibeTonz", "com.immersion"},
			{"PantechAudio", "com.pantech"},
			{"RIM", "net.rim"},

			// nnapi
			{"Pigler Notifications", "org.pigler"},
			{"KEmulator Notifications (Deprecated)", "ru.nnproject.kemulator.notificationapi"},
			{"KEmulator Rich Presence", "ru.nnproject.kemulator.rpc"},
			{"KEmulator Window API", "ru.nnproject.kemulator.windowapi"},
			{"KEmulator", "kemulator"}, // проприетарные пропы по типу kemulator.version

			// отдельное

			{"CustomItem (MIDP 2.0)", "javax.microedition.lcdui.CustomItem"},
			{"Spacer (MIDP 2.0)", "javax.microedition.lcdui.Spacer"},
			{"Game (MIDP 2.0)", "javax.microedition.lcdui.game"},

			{"Float (CLDC 1.1)", "java.lang.Float"},
			{"Double (CLDC 1.1)", "java.lang.Double"},
	};

	public static final String[][] PERMISSIONS = {
			{"Send SMS", "messageconnection.send"},
			{"Receive SMS", "messageconnection.receive"},
			{"File System Access", "connector.open.file"},
			{"HTTP Connection", "connector.open.http"},
			{"Socket Connection", "connector.open.socket"},
			{"Socket Server", "connector.open.serversocket"},
			{"Camera", "media.camera"},
			{"Location", "location"},
			{"Open browser", "platformrequest"},
	};

	public static final String[][] PERMISSIONS_VALUES = {
			{
				"Allowed",
				"Ask once",
				"Ask every time",
				"Never",
			},
			{
				"allowed",
				"ask_once",
				"ask_always_until_no",
				"never"
			}
	};

	private static Display aDisplay656;
	private Shell setsShell;
	private Combo aCombo657;
	private CLabel aCLabel658;
	private CLabel aCLabel673;
	private Combo aCombo675;
	private CLabel aCLabel683;
	private Group aGroup660;
	private Text aText662;
	private Button aButton666;
	private CLabel aCLabel694;
	private Text screenWidthText;
	private CLabel aCLabel701;
	private Text screenHeightText;
	private CLabel aCLabel707;
	private Text aText695;
	private CLabel aCLabel712;
	private Text aText702;
	private CLabel aCLabel717;
	private Text aText708;
	private CLabel aCLabel722;
	private Text aText713;
	private CLabel aCLabel726;
	private Text aText718;
	private CLabel aCLabel730;
	private Text aText723;
	private CLabel aCLabel734;
	private Text aText727;
	private Composite aComposite667;
	private Button aButton676;
	private Button aButton685;
	private Scale aScale669;
	private CLabel aCLabel738;
	private CTabFolder tabFolder;
	private Composite customComp;
	private Composite keyMapBindsComp;
	private CLabel aCLabel741;
	private Text aText731;
	private CLabel aCLabel744;
	private Text aText735;
	private CLabel aCLabel747;
	private Text aText739;
	private CLabel aCLabel750;
	private Text aText742;
	private CLabel aCLabel753;
	private Text aText745;
	private CLabel aCLabel756;
	private Text aText748;
	private CLabel aCLabel759;
	private Text aText751;
	private CLabel aCLabel762;
	private Text aText754;
	private CLabel aCLabel765;
	private Text aText757;
	private CLabel aCLabel767;
	private Text aText760;
	private CLabel aCLabel769;
	private Text aText763;
	private CLabel aCLabel771;
	private Text aText766;
	private CLabel aCLabel773;
	private Text aText768;
	private CLabel aCLabel626;
	private Text aText770;
	private CLabel aCLabel628;
	private Text aText772;
	private CLabel aCLabel630;
	private Text aText774;
	private CLabel aCLabel632;
	private Text aText627;
	private CLabel aCLabel634;
	private Text aText629;
	private CLabel aCLabel636;
	private Text aText631;
	private Button aButton696;
	private Button aButton703;
	private Button aButton709;
	private Button aButton714;
	private Button aButton719;
	private Composite systemComp;
	private Composite coreApiComp;
	private Group coreApiGroup;
	private Group sysChecksGroup;
	private Button aButton724;
	private Button aButton728;
	private Button aButton732;
	private Button noNetworkBtn;
	private Button aButton746;
	private Button aButton749;
	private Button aButton752;
	private Composite sysFontComp;
	private CLabel aCLabel638;
	private Combo aCombo689;
	private CLabel aCLabel640;
	private Spinner aSpinner670;
	private Canvas aCanvas663;
	private CLabel aCLabel642;
	private Spinner aSpinner679;
	private Canvas aCanvas680;
	private CLabel aCLabel644;
	private Spinner aSpinner690;
	private Canvas aCanvas691;
	private CLabel aCLabel645;
	private Text aText633;
	private String device;
	private String defaultFont;
	private String rmsFolder;
	private String screenWidth;
	private String screenHeight;
	private String lsoft;
	private String rsoft;
	private String fire;
	private String up;
	private String down;
	private String left;
	private String right;
	private int fontSmallSize;
	private int fontMediumSize;
	private int fontLargeSize;
	private CLabel aCLabel646;
	Combo controllerCombo;
	private Button keymapRefreshBtn;
	private static String[] aStringArray661;
	private IImage anIImage671;
	private IImage anIImage674;
	private IImage anIImage693;
	private Composite recordsComp;
	private Table aTable665;
	private CLabel aCLabel647;
	private Button clearRecordsBtn;
	private Button aButton761;
	private Composite networkComp;
	private Group networkProxyGroup;
	private CLabel aCLabel648;
	private Combo proxyTypeCombo;
	private CLabel aCLabel649;
	private Text aText635;
	private CLabel aCLabel650;
	private CLabel aCLabel651;
	private Text aText637;
	private CLabel aCLabel652;
	private Text aText639;
	private CLabel aCLabel653;
	private Text aText641;
	private CLabel aCLabel654;
	private Text aText643;
	private Button aButton764;
	//private Spinner inactiveTimerSpinner;
	private Button rpcBtn;
	private Button antiAliasBtn;
	private Composite mediaComp;
	private Group mediaGroup;
	private Text vlcDirText;
	private Font f;
	private CLabel labelLocale;
	private Text localeText;
	private Button keymapClearBtn;

	private Combo languageCombo;
	private Combo updateBranchCombo;

	private Composite keyMapControllerComp;
	private Composite keyMapTabComp;
	private Button softkeyMotFixCheck;
	private Button synchronizeKeyEventsCheck;

	private Composite m3gComp;
	private Button m3gIgnoreOverwriteCheck;
	private Button m3gForcePersCorrect;
	private Button m3gDisableLightClamp;
	private Combo m3gAACombo;
	private Combo m3gTexFilterCombo;
	private Combo m3gMipmapCombo;
	private Button vmsCheck;
	//    private Button reopenMidiCheck;
	private Button globalMidiCheck;
//	private Button ignoreRegionRepaintCheck;
	private Button fpsCounterCheck;
//	private Button serialCallsCheck;
	private Button keyPressOnRepeatCheck;
	private Button antiAliasTextBtn;
	private Composite disableApiComp;
	private Table disableApiTable;
	private Composite propsComp;
	private Text propsText;
	private Button mediaDumpCheck;
	private Button ottCheck;
	private ScrolledComposite securityComp;
	private Button mascotNo2DMixingCheck;
	private Composite mascotComp;
	private Button mascotIgnoreBgCheck;
	private Button mascotTextureFilterCheck;
	private Button mascotBackgroundFilterCheck;
	private Button securityCheck;
	private Composite securityContent;
	private Tree rmsTree;
//	private Button forceServicePaintCheck;
	private Composite langComposite;
	private Button pointerEventsCheck;
	private Button fpsLimitJlCheck;
	private Button autoUpdatesBtn;
	private Button m3gFlushImmediately;
	private Button keymapResetBtn;
	private Button asyncFlushCheck;
//    private Button pollOnRepaintBtn;

	public Property() {
		super();
		this.loadProperties();
		this.updateProxy();
		UILocale.initLocale();
	}

	public void method354(final Shell shell) {
		Property.aDisplay656 = shell.getDisplay();
		this.method372(shell);
		this.setsShell.pack();
		this.setsShell.setSize(480, this.setsShell.getSize().y);
		securityComp.setMinHeight(securityContent.computeSize(-1, -1).y);
		securityComp.setExpandVertical(true);

//		try {
//			IScreen scr = Emulator.getEmulator().getScreen();
//			screenWidthText.setText("" + scr.getWidth());
//			screenHeightText.setText("" + scr.getHeight());
//		} catch (Exception ignored) {}

		this.setsShell.setLocation(shell.getLocation().x + (shell.getSize().x - this.setsShell.getSize().x >> 1), shell.getLocation().y + (shell.getSize().y - this.setsShell.getSize().y >> 1));
		this.setsShell.open();
		while (!this.setsShell.isDisposed()) {
			if (!Property.aDisplay656.readAndDispatch()) {
				Property.aDisplay656.sleep();
			}
		}
		this.anIImage671 = null;
		this.anIImage674 = null;
		this.anIImage693 = null;
	}

	public void resetDeviceName() {
		this.device = Emulator.deviceName;
	}

	public String getDefaultFontName() {
		return this.defaultFont;
	}

	public int getFontSmallSize() {
		return this.fontSmallSize;
	}

	public int getFontMediumSize() {
		return this.fontMediumSize;
	}

	public int getFontLargeSize() {
		return this.fontLargeSize;
	}

	public void setDefaultFontName(final String aString682) {
		this.defaultFont = aString682;
	}

	public void setFontSmallSize(final int anInt664) {
		this.fontSmallSize = anInt664;
	}

	public void getFontMediumSize(final int anInt681) {
		this.fontMediumSize = anInt681;
	}

	public void getFontLargeSize(final int anInt687) {
		this.fontLargeSize = anInt687;
	}

	private String method355() {
		return this.device + "_" + this.screenWidth + "x" + this.screenHeight;
	}

	public String getRmsFolderPath() {
		String s = null;
		Label_0077:
		{
			StringBuffer sb;
			String substring;
			if ((s = this.rmsFolder).startsWith(".")) {
				sb = new StringBuffer().append(Emulator.getUserPath());
				substring = s.substring(1);
			} else {
				if (!s.startsWith("/") && !s.startsWith("\\")) {
					break Label_0077;
				}
				sb = new StringBuffer().append(Emulator.getUserPath());
				substring = s;
			}
			s = sb.append(substring).toString();
		}
		final File file = new File(s);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
        /*
        final String string = s + "/" + this.method355();
        final File file2;
        if (!(file2 = new File(string)).exists() || !file2.isDirectory()) {
            file2.mkdirs();
        }
        */
		try {
			return file.getCanonicalPath() + File.separator;
		} catch (IOException e) {
			return s + File.separator;
		}
	}

	public String getOldRmsPath() {
		return getRmsFolderPath() + method355() + File.separator;
	}

	public void setCustomProperties() {
		if (this.device.equalsIgnoreCase(Emulator.deviceName)) {
			Devices.setProperty("SCREEN_WIDTH", this.screenWidth);
			Devices.setProperty("SCREEN_HEIGHT", this.screenHeight);
			Devices.setProperty("KEY_S1", this.lsoft);
			Devices.setProperty("KEY_S2", this.rsoft);
			Devices.setProperty("KEY_FIRE", this.fire);
			Devices.setProperty("KEY_UP", this.up);
			Devices.setProperty("KEY_DOWN", this.down);
			Devices.setProperty("KEY_LEFT", this.left);
			Devices.setProperty("KEY_RIGHT", this.right);
			Devices.writeProperties();
		}
	}

	public void updateCustomProperties() {
		this.screenWidth = Devices.getProperty("SCREEN_WIDTH");
		this.screenHeight = Devices.getProperty("SCREEN_HEIGHT");
		this.lsoft = Devices.getProperty("KEY_S1");
		this.rsoft = Devices.getProperty("KEY_S2");
		this.fire = Devices.getProperty("KEY_FIRE");
		this.up = Devices.getProperty("KEY_UP");
		this.down = Devices.getProperty("KEY_DOWN");
		this.left = Devices.getProperty("KEY_LEFT");
		this.right = Devices.getProperty("KEY_RIGHT");
	}

	public void loadProperties() {
		try {
			if (!new File(Emulator.getUserPath() + "/property.txt").exists()) {
				throw new FileNotFoundException();
			}
			final FileInputStream fileInputStream = new FileInputStream(Emulator.getUserPath() + "/property.txt");
			final Properties properties = new Properties();
			properties.load(fileInputStream);
			final String property = properties.getProperty("Device", "SonyEricssonK800");
			this.device = property;
			Emulator.deviceName = property;
			this.defaultFont = properties.getProperty("DefaultFont", "Tahoma");
			this.rmsFolder = properties.getProperty("RMSFolder", "/rms");
			this.fontSmallSize = Integer.parseInt(properties.getProperty("FontSmallSize", String.valueOf(12)));
			this.fontMediumSize = Integer.parseInt(properties.getProperty("FontMediumSize", String.valueOf(14)));
			this.fontLargeSize = Integer.parseInt(properties.getProperty("FontLargeSize", String.valueOf(16)));
			this.screenWidth = properties.getProperty("SCREEN_WIDTH", "240");
			this.screenHeight = properties.getProperty("SCREEN_HEIGHT", "320");
			this.lsoft = properties.getProperty("KEY_LSOFT", "-6");
			this.rsoft = properties.getProperty("KEY_RSOFT", "-7");
			this.fire = properties.getProperty("KEY_FIRE", "-5");
			this.up = properties.getProperty("KEY_UP", "-1");
			this.down = properties.getProperty("KEY_DOWN", "-2");
			this.left = properties.getProperty("KEY_LEFT", "-3");
			this.right = properties.getProperty("KEY_RIGHT", "-4");
			Settings.g2d = (properties.getProperty("2D_Graphics_Engine", "AWT").equalsIgnoreCase("SWT") ? 0 : 1);
			Settings.g3d = (properties.getProperty("3D_Graphics_Engine", "LWJ").equalsIgnoreCase("DLL") ? 0 : 1);
			Settings.micro3d = (properties.getProperty("Micro3D_Engine", Emulator.isX64() ? "GL" : "DLL").equalsIgnoreCase("DLL") ? 0 : 1);

			Settings.frameRate = Integer.parseInt(properties.getProperty("FrameRate", String.valueOf(30)));
			Settings.asyncFlush = Boolean.parseBoolean(properties.getProperty("AsyncFlush", "true"));

			// keyboard mappings
			KeyMapping.mapDeviceKey(0, KeyMapping.method601(properties.getProperty("MAP_KEY_NUM_0")));
			KeyMapping.mapDeviceKey(1, KeyMapping.method601(properties.getProperty("MAP_KEY_NUM_1")));
			KeyMapping.mapDeviceKey(2, KeyMapping.method601(properties.getProperty("MAP_KEY_NUM_2")));
			KeyMapping.mapDeviceKey(3, KeyMapping.method601(properties.getProperty("MAP_KEY_NUM_3")));
			KeyMapping.mapDeviceKey(4, KeyMapping.method601(properties.getProperty("MAP_KEY_NUM_4")));
			KeyMapping.mapDeviceKey(5, KeyMapping.method601(properties.getProperty("MAP_KEY_NUM_5")));
			KeyMapping.mapDeviceKey(6, KeyMapping.method601(properties.getProperty("MAP_KEY_NUM_6")));
			KeyMapping.mapDeviceKey(7, KeyMapping.method601(properties.getProperty("MAP_KEY_NUM_7")));
			KeyMapping.mapDeviceKey(8, KeyMapping.method601(properties.getProperty("MAP_KEY_NUM_8")));
			KeyMapping.mapDeviceKey(9, KeyMapping.method601(properties.getProperty("MAP_KEY_NUM_9")));
			KeyMapping.mapDeviceKey(10, KeyMapping.method601(properties.getProperty("MAP_KEY_STAR")));
			KeyMapping.mapDeviceKey(11, KeyMapping.method601(properties.getProperty("MAP_KEY_POUND")));
			KeyMapping.mapDeviceKey(12, KeyMapping.method601(properties.getProperty("MAP_KEY_UP")));
			KeyMapping.mapDeviceKey(13, KeyMapping.method601(properties.getProperty("MAP_KEY_DOWN")));
			KeyMapping.mapDeviceKey(14, KeyMapping.method601(properties.getProperty("MAP_KEY_LEFT")));
			KeyMapping.mapDeviceKey(15, KeyMapping.method601(properties.getProperty("MAP_KEY_RIGHT")));
			KeyMapping.mapDeviceKey(16, KeyMapping.method601(properties.getProperty("MAP_KEY_MIDDLE")));
			KeyMapping.mapDeviceKey(17, KeyMapping.method601(properties.getProperty("MAP_KEY_LSOFT")));
			KeyMapping.mapDeviceKey(18, KeyMapping.method601(properties.getProperty("MAP_KEY_RSOFT")));
			Settings.enableKeyCache = Boolean.parseBoolean(properties.getProperty("EnableKeyCache", "false"));
			Settings.canvasKeyboard = Boolean.parseBoolean(properties.getProperty("CanvasKeyboardMode", "true"));
			Settings.recordKeys = Boolean.parseBoolean(properties.getProperty("RecordKeys", "false"));

			// controller mappings
			for (Object k : properties.keySet()) {
				if (((String) k).startsWith("ControllerMap.")) {
					Settings.controllerBinds.put(((String) k).substring("ControllerMap.".length()), properties.getProperty((String) k));
					// permissions
				} else if (((String) k).startsWith("Security.")) {
					Permission.permissions.put(((String) k).substring("Security.".length()), Permission.fromString(properties.getProperty((String) k)));
				}
			}

			// api
			Settings.enableVibration = Boolean.parseBoolean(properties.getProperty("EnableVibration", "false"));
			Settings.enableKeyRepeat = Boolean.parseBoolean(properties.getProperty("EnableKeyRepeat", "false"));
			Settings.ignoreFullScreen = Boolean.parseBoolean(properties.getProperty("IgnoreFullScreenMode", "false"));
			Settings.networkNotAvailable = Boolean.parseBoolean(properties.getProperty("NetworkNotAvailable", "false"));

			// platform
			Settings.synchronizeKeyEvents = Boolean.parseBoolean(properties.getProperty("SynchronizeKeyEvents", "false"));
			Settings.motorolaSoftKeyFix = Boolean.parseBoolean(properties.getProperty("MotorolaSoftKeyFix", "false"));
			Settings.patchSynchronizedPaint = Boolean.parseBoolean(properties.getProperty("PatchSynchronizedPaint", "true"));
			Settings.pollKeyboardOnRepaint = Boolean.parseBoolean(properties.getProperty("PollKeyboardOnRepaint", "true"));
			Settings.ignoreRegionRepaint = Boolean.parseBoolean(properties.getProperty("IgnoreRegionRepaint", "false"));
			Settings.startAppOnResume = Boolean.parseBoolean(properties.getProperty("StartAppOnResume", "true"));
			Settings.keyPressOnRepeat = Boolean.parseBoolean(properties.getProperty("KeyPressOnRepeat", "false"));
			Settings.ignoreServiceRepaints = Boolean.parseBoolean(properties.getProperty("IgnoreServiceRepaints", "false"));
			Settings.dontRepaintOnSetCurrent = Boolean.parseBoolean(properties.getProperty("DontRepaintOnSetCurrent", "false"));
			Settings.hasPointerEvents = Boolean.parseBoolean(properties.getProperty("HasPointerEvents", "true"));
			Settings.j2lStyleFpsLimit = Boolean.parseBoolean(properties.getProperty("FPSLimitJLStyle", "false"));
			Settings.queueSleep = Boolean.parseBoolean(properties.getProperty("EventQueueSleep", "true"));

			String[] protectedPackages = properties.getProperty("ProtectedPackages", "").split(";");
			if (protectedPackages.length > 0) {
				Settings.protectedPackages.addAll(Arrays.asList(protectedPackages));
			}

			String[] sysProps = properties.getProperty("SystemProperties", "").split("\n");
            for (String s : sysProps) {
                if ((s = s.trim()).isEmpty()) continue;
                int i = s.indexOf(':');
                if (i == -1) continue;
                String k = s.substring(0, i).trim();
                String v = s.substring(i + 1).trim();
                Settings.systemProperties.put(k, v);
            }

            Settings.fileEncoding = properties.getProperty("FileEncoding", "ISO-8859-1");
			Settings.locale = properties.getProperty("MIDPLocale", "en-US");

			// emulator
			Settings.rightClickMenu = Boolean.parseBoolean(properties.getProperty("RightClickMenu", "false"));
			Settings.xrayOverlapScreen = Boolean.parseBoolean(properties.getProperty("XRayOverLapScreen", "false"));
			Settings.xrayShowClipBorder = Boolean.parseBoolean(properties.getProperty("XRayShowClipBorder", "false"));
			Settings.infoColorHex = Boolean.parseBoolean(properties.getProperty("InfoColorHex", "false"));
			Settings.recordReleasedImg = Boolean.parseBoolean(properties.getProperty("RecordReleasedImg", "false"));
			Settings.autoGenJad = Boolean.parseBoolean(properties.getProperty("AutoGenJad", "false"));
			Settings.enableNewTrack = Boolean.parseBoolean(properties.getProperty("EnableNewTrack", "false"));
			Settings.enableMethodTrack = Boolean.parseBoolean(properties.getProperty("EnableMethodTrack", "false"));
			Settings.threadMethodTrack = Boolean.parseBoolean(properties.getProperty("ShowMethodTrack", "false"));
			Settings.updateBranch = properties.getProperty("UpdateBranch", Emulator.debugBuild ? "dev" : "stable");
			Settings.autoUpdate = Integer.parseInt(properties.getProperty("AutoUpdate", "0"));

			Settings.bypassVserv = Boolean.parseBoolean(properties.getProperty("BypassVserv", "true"));
			Settings.wavCache = Boolean.parseBoolean(properties.getProperty("WavCache", "true"));

			Settings.rpc = Boolean.parseBoolean(properties.getProperty("DiscordRichPresence", "true"));
			Settings.uiLanguage = properties.getProperty("UILanguage", "en");
			Settings.writeKemCfg = Boolean.parseBoolean(properties.getProperty("WriteKemulatorCfg", "false"));

			for (int i = 0; i < 5; ++i) {
				Settings.recentJars[i] = properties.getProperty("MRUList" + i, "");
			}

			// proxy
			Settings.proxyType = Integer.parseInt(properties.getProperty("ProxyType", "0"));
			if (Settings.proxyType < 0 || Settings.proxyType > 2) {
				Settings.proxyType = 0;
			}
			Settings.proxyHost = properties.getProperty("ProxyHost", "");
			Settings.proxyPort = properties.getProperty("ProxyPort", "");
			Settings.proxyUser = properties.getProperty("ProxyUsername", "");
			Settings.proxyPass = properties.getProperty("ProxyPassword", "");
			Settings.proxyDomain = properties.getProperty("ProxyDomain", "");

			// view
			Settings.showLogFrame = Boolean.parseBoolean(properties.getProperty("ShowLogFrame", "false"));
			Settings.showInfoFrame = Boolean.parseBoolean(properties.getProperty("ShowInfoFrame", "false"));
			Settings.showMemViewFrame = Boolean.parseBoolean(properties.getProperty("ShowMemViewFrame", "false"));
			Settings.fpsCounter = Boolean.parseBoolean(properties.getProperty("FPSCounter", "true"));

			Settings.awtAntiAliasing = Boolean.parseBoolean(properties.getProperty("AWTAntiAliasing", "false"));
			Settings.textAntiAliasing = Boolean.parseBoolean(properties.getProperty("TextAntiAliasing", "true"));

			if (Emulator.getEmulator() != null && Emulator.getEmulator().getScreen() != null) {
				((EmulatorScreen) Emulator.getEmulator().getScreen()).toggleMenuAccelerators(!Settings.canvasKeyboard);
				((EmulatorScreen) Emulator.getEmulator().getScreen()).setFpsMode(Settings.fpsMode);
			}

			// display
			Settings.canvasScale = Integer.parseInt(properties.getProperty("CanvasScale", String.valueOf(100)));
			if (Settings.canvasScale < 100 || Settings.canvasScale % 50 != 0) {
				Settings.canvasScale = 100;
			}
			Settings.resizeMode = Integer.parseInt(properties.getProperty("ResizeMode", "2"));
			Settings.keepAspectRatio = Boolean.parseBoolean(properties.getProperty("KeepAspectRatio", "true"));

			// window
			EmulatorScreen.locX = Integer.parseInt(properties.getProperty("LocationX", "-1"));
			EmulatorScreen.locY = Integer.parseInt(properties.getProperty("LocationY", "-1"));
			EmulatorScreen.sizeW = Integer.parseInt(properties.getProperty("SizeW", "-1"));
			EmulatorScreen.sizeH = Integer.parseInt(properties.getProperty("SizeH", "-1"));
			EmulatorScreen.maximized = Boolean.parseBoolean(properties.getProperty("Maximized", "false"));
			EmulatorScreen.defaultSize = Boolean.parseBoolean(properties.getProperty("DefaultSize", "true"));

			Settings.alwaysOnTop = Boolean.parseBoolean(properties.getProperty("AlwaysOnTop", "false"));

			// m3g
			Settings.m3gIgnoreOverwrite = Boolean.parseBoolean(properties.getProperty("M3GIgnoreOverwrite", "false"));
			Settings.m3gForcePerspectiveCorrection = Boolean.parseBoolean(properties.getProperty("M3GForcePerspectiveCorrection", "false"));
			Settings.m3gDisableLightClamp = Boolean.parseBoolean(properties.getProperty("M3GDisableLightClamp", "false"));
			Settings.m3gFlushImmediately = Boolean.parseBoolean(properties.getProperty("M3GFlushImmediately", "false"));
			Settings.m3gThread = Boolean.parseBoolean(properties.getProperty("M3GThread", "true"));

			Settings.m3gAA = Integer.parseInt(properties.getProperty("M3GAA", "0"));
			Settings.m3gTexFilter = Integer.parseInt(properties.getProperty("M3GTexFilter", "0"));
			Settings.m3gMipmapping = Integer.parseInt(properties.getProperty("M3GMipmapping", "0"));
			Settings.m3gContextMode = Integer.parseInt(properties.getProperty("M3GContextMode", "0"));

			// mascot`
			Settings.mascotNo2DMixing = Boolean.parseBoolean(properties.getProperty("MascotNo2DMixing", "false"));
			Settings.mascotIgnoreBackground = Boolean.parseBoolean(properties.getProperty("MascotIgnoreBackground", "false"));
			Settings.mascotTextureFilter = Boolean.parseBoolean(properties.getProperty("MascotTextureFilter", "false"));
			Settings.mascotBackgroundFilter = Boolean.parseBoolean(properties.getProperty("MascotBackgroundFilter", "false"));

			// media
			Settings.vlcDir = properties.getProperty("VlcDir", "");
			Settings.searchVms = Boolean.parseBoolean(properties.getProperty("MIDISearchVMS", "true"));
			Settings.reopenMidiDevice = Boolean.parseBoolean(properties.getProperty("MIDIReopenDevice", "true"));
			Settings.oneMidiAtTime = Boolean.parseBoolean(properties.getProperty("MIDIGlobalSequencer", "false"));
			Settings.enableMediaDump = Boolean.parseBoolean(properties.getProperty("EnableMediaDump", "false"));
			Settings.enableOTT = Boolean.parseBoolean(properties.getProperty("EnableOTT", "true"));

			// jvm
			Settings.xmx = Integer.parseInt(properties.getProperty("JVMHeap", "512"));

			// security
			Settings.enableSecurity = Boolean.parseBoolean(properties.getProperty("SecurityEnabled", "true"));

			fileInputStream.close();
		} catch (Exception ex) {
			if (!(ex instanceof FileNotFoundException)) {
				System.out.println("properties load failed");
				ex.printStackTrace();
			}
			this.device = "SonyEricssonK800";
			this.defaultFont = "Tahoma";
			this.rmsFolder = "/rms";
			this.screenWidth = "240";
			this.screenHeight = "320";
			this.lsoft = "-6";
			this.rsoft = "-7";
			this.fire = "-5";
			this.up = "-1";
			this.down = "-2";
			this.left = "-3";
			this.right = "-4";
			EmulatorScreen.locX = -1;
			EmulatorScreen.locY = -1;
			Settings.fileEncoding = "ISO-8859-1";
			Settings.autoGenJad = false;
			Settings.enableNewTrack = false;
			Settings.enableMethodTrack = false;
			this.fontSmallSize = 12;
			this.fontMediumSize = 14;
			this.fontLargeSize = 16;
			for (int j = 0; j < 5; ++j) {
				Settings.recentJars[j] = "";
			}
			Settings.proxyType = 0;
			Settings.proxyHost = "";
			Settings.proxyPort = "";
			Settings.proxyUser = "";
			Settings.proxyPass = "";
			Settings.proxyDomain = "";
			Settings.showLogFrame = false;
			Settings.showInfoFrame = false;
			Settings.showMemViewFrame = false;
		}
	}

	public void saveProperties() {
		try {
			final FileOutputStream fileOutputStream = new FileOutputStream(Emulator.getUserPath() + "/property.txt");
			final Properties properties = new SortProperties();

			properties.setProperty("Device", this.device);
			properties.setProperty("DefaultFont", this.defaultFont);
			properties.setProperty("RMSFolder", this.rmsFolder);
			properties.setProperty("FontSmallSize", String.valueOf(this.fontSmallSize));
			properties.setProperty("FontMediumSize", String.valueOf(this.fontMediumSize));
			properties.setProperty("FontLargeSize", String.valueOf(this.fontLargeSize));
			properties.setProperty("SCREEN_WIDTH", this.screenWidth);
			properties.setProperty("SCREEN_HEIGHT", this.screenHeight);
			properties.setProperty("KEY_LSOFT", this.lsoft);
			properties.setProperty("KEY_RSOFT", this.rsoft);
			properties.setProperty("KEY_FIRE", this.fire);
			properties.setProperty("KEY_UP", this.up);
			properties.setProperty("KEY_DOWN", this.down);
			properties.setProperty("KEY_LEFT", this.left);
			properties.setProperty("KEY_RIGHT", this.right);
			properties.setProperty("2D_Graphics_Engine", (Settings.g2d == 0) ? "SWT" : "AWT");
			properties.setProperty("3D_Graphics_Engine", (Settings.g3d == 0) ? "SWERVE" : "LWJ");
			properties.setProperty("Micro3D_Engine", (Settings.micro3d == 0) ? "DLL" : "GL");

			properties.setProperty("FrameRate", String.valueOf(Settings.frameRate));
			properties.setProperty("AsyncFlush", String.valueOf(Settings.asyncFlush));

			// keyboard mappings
			properties.setProperty("MAP_KEY_NUM_0", KeyMapping.get(0));
			properties.setProperty("MAP_KEY_NUM_1", KeyMapping.get(1));
			properties.setProperty("MAP_KEY_NUM_2", KeyMapping.get(2));
			properties.setProperty("MAP_KEY_NUM_3", KeyMapping.get(3));
			properties.setProperty("MAP_KEY_NUM_4", KeyMapping.get(4));
			properties.setProperty("MAP_KEY_NUM_5", KeyMapping.get(5));
			properties.setProperty("MAP_KEY_NUM_6", KeyMapping.get(6));
			properties.setProperty("MAP_KEY_NUM_7", KeyMapping.get(7));
			properties.setProperty("MAP_KEY_NUM_8", KeyMapping.get(8));
			properties.setProperty("MAP_KEY_NUM_9", KeyMapping.get(9));
			properties.setProperty("MAP_KEY_STAR", KeyMapping.get(10));
			properties.setProperty("MAP_KEY_POUND", KeyMapping.get(11));
			properties.setProperty("MAP_KEY_UP", KeyMapping.get(12));
			properties.setProperty("MAP_KEY_DOWN", KeyMapping.get(13));
			properties.setProperty("MAP_KEY_LEFT", KeyMapping.get(14));
			properties.setProperty("MAP_KEY_RIGHT", KeyMapping.get(15));
			properties.setProperty("MAP_KEY_MIDDLE", KeyMapping.get(16));
			properties.setProperty("MAP_KEY_LSOFT", KeyMapping.get(17));
			properties.setProperty("MAP_KEY_RSOFT", KeyMapping.get(18));
			properties.setProperty("EnableKeyCache", String.valueOf(Settings.enableKeyCache));
			properties.setProperty("CanvasKeyboardMode", String.valueOf(Settings.canvasKeyboard));
			properties.setProperty("RecordKeys", String.valueOf(Settings.recordKeys));

			// controller mappings
			for (Map.Entry<String, String> e : Settings.controllerBinds.entrySet()) {
				properties.setProperty("ControllerMap." + e.getKey(), e.getValue());
			}

			// api
			properties.setProperty("EnableVibration", String.valueOf(Settings.enableVibration));
			properties.setProperty("EnableKeyRepeat", String.valueOf(Settings.enableKeyRepeat));
			properties.setProperty("IgnoreFullScreenMode", String.valueOf(Settings.ignoreFullScreen));
			properties.setProperty("NetworkNotAvailable", String.valueOf(Settings.networkNotAvailable));

			// platform
			properties.setProperty("SynchronizeKeyEvents", String.valueOf(Settings.synchronizeKeyEvents));
			properties.setProperty("MotorolaSoftKeyFix", String.valueOf(Settings.motorolaSoftKeyFix));
			properties.setProperty("PatchSynchronizedPaint", String.valueOf(Settings.patchSynchronizedPaint));
			properties.setProperty("PollKeyboardOnRepaint", String.valueOf(Settings.pollKeyboardOnRepaint));
			properties.setProperty("IgnoreRegionRepaint", String.valueOf(Settings.ignoreRegionRepaint));
			properties.setProperty("StartAppOnResume", String.valueOf(Settings.startAppOnResume));
			properties.setProperty("KeyPressOnRepeat", String.valueOf(Settings.keyPressOnRepeat));
			properties.setProperty("IgnoreServiceRepaints", String.valueOf(Settings.ignoreServiceRepaints));
			properties.setProperty("DontRepaintOnSetCurrent", String.valueOf(Settings.dontRepaintOnSetCurrent));
			properties.setProperty("HasPointerEvents", String.valueOf(Settings.hasPointerEvents));
			properties.setProperty("FPSLimitJLStyle", String.valueOf(Settings.j2lStyleFpsLimit));
			properties.setProperty("EventQueueSleep", String.valueOf(Settings.queueSleep));

			StringBuilder builder = new StringBuilder();
			if (!Settings.protectedPackages.isEmpty()) {
				for (String s: Settings.protectedPackages) {
					builder.append(s).append(';');
				}
				builder.setLength(builder.length() - 1);
			}
			properties.setProperty("ProtectedPackages", builder.toString());

			builder.setLength(0);
			if (!Settings.systemProperties.isEmpty()) {
				for (String k : Settings.systemProperties.keySet()) {
					builder.append(k).append(':').append(Settings.systemProperties.get(k)).append('\n');
				}
				builder.setLength(builder.length() - 1);
			}
			properties.setProperty("SystemProperties", builder.toString());

			properties.setProperty("FileEncoding", Settings.fileEncoding);
			properties.setProperty("MIDPLocale", Settings.locale);

			// emulator
			properties.setProperty("RightClickMenu", String.valueOf(Settings.rightClickMenu));
			properties.setProperty("XRayOverLapScreen", String.valueOf(Settings.xrayOverlapScreen));
			properties.setProperty("XRayShowClipBorder", String.valueOf(Settings.xrayShowClipBorder));
			properties.setProperty("InfoColorHex", String.valueOf(Settings.infoColorHex));
			properties.setProperty("RecordReleasedImg", String.valueOf(Settings.recordReleasedImg));
			properties.setProperty("AutoGenJad", String.valueOf(Settings.autoGenJad));
			properties.setProperty("EnableNewTrack", String.valueOf(Settings.enableNewTrack));
			properties.setProperty("EnableMethodTrack", String.valueOf(Settings.enableMethodTrack));
			properties.setProperty("ShowMethodTrack", String.valueOf(Settings.threadMethodTrack));
			properties.setProperty("UpdateBranch", Settings.updateBranch);
			properties.setProperty("AutoUpdate", String.valueOf(Settings.autoUpdate));

			properties.setProperty("BypassVserv", String.valueOf(Settings.bypassVserv));
			properties.setProperty("WavCache", String.valueOf(Settings.wavCache));

			properties.setProperty("DiscordRichPresence", String.valueOf(Settings.rpc));
			properties.setProperty("UILanguage", Settings.uiLanguage);
			properties.setProperty("WriteKemulatorCfg", String.valueOf(Settings.writeKemCfg));

			for (int i = 0; i < 5; ++i) {
				properties.setProperty("MRUList" + i, Settings.recentJars[i]);
			}

			// proxy
			properties.setProperty("ProxyType", String.valueOf(Settings.proxyType));
			properties.setProperty("ProxyHost", Settings.proxyHost);
			properties.setProperty("ProxyPort", Settings.proxyPort);
			properties.setProperty("ProxyUsername", Settings.proxyUser);
			properties.setProperty("ProxyPassword", Settings.proxyPass);
			properties.setProperty("ProxyDomain", Settings.proxyDomain);

			// view
			properties.setProperty("ShowLogFrame", String.valueOf(Settings.showLogFrame));
			properties.setProperty("ShowInfoFrame", String.valueOf(Settings.showInfoFrame));
			properties.setProperty("ShowMemViewFrame", String.valueOf(Settings.showMemViewFrame));
			properties.setProperty("FPSCounter", String.valueOf(Settings.fpsCounter));

			properties.setProperty("AWTAntiAliasing", String.valueOf(Settings.awtAntiAliasing));
			properties.setProperty("TextAntiAliasing", String.valueOf(Settings.textAntiAliasing));

			// display
			properties.setProperty("CanvasScale", String.valueOf(Settings.canvasScale));
			properties.setProperty("ResizeMode", String.valueOf(Settings.resizeMode));
			properties.setProperty("KeepAspectRatio", String.valueOf(Settings.keepAspectRatio));

			// window
			properties.setProperty("LocationX", String.valueOf(EmulatorScreen.locX));
			properties.setProperty("LocationY", String.valueOf(EmulatorScreen.locY));
			properties.setProperty("SizeW", String.valueOf(EmulatorScreen.sizeW));
			properties.setProperty("SizeH", String.valueOf(EmulatorScreen.sizeH));
			properties.setProperty("Maximized", String.valueOf(EmulatorScreen.maximized));
			properties.setProperty("DefaultSize", String.valueOf(EmulatorScreen.defaultSize));

			properties.setProperty("AlwaysOnTop", String.valueOf(Settings.alwaysOnTop));

			// m3g
			properties.setProperty("M3GIgnoreOverwrite", String.valueOf(Settings.m3gIgnoreOverwrite));
			properties.setProperty("M3GForcePerspectiveCorrection", String.valueOf(Settings.m3gForcePerspectiveCorrection));
			properties.setProperty("M3GDisableLightClamp", String.valueOf(Settings.m3gDisableLightClamp));
			properties.setProperty("M3GFlushImmediately", String.valueOf(Settings.m3gFlushImmediately));
			properties.setProperty("M3GThread", String.valueOf(Settings.m3gThread));

			properties.setProperty("M3GAA", String.valueOf(Settings.m3gAA));
			properties.setProperty("M3GTexFilter", String.valueOf(Settings.m3gTexFilter));
			properties.setProperty("M3GMipmapping", String.valueOf(Settings.m3gMipmapping));
			properties.setProperty("M3GContextMode", String.valueOf(Settings.m3gContextMode));

			// mascot
			properties.setProperty("MascotNo2DMixing", String.valueOf(Settings.mascotNo2DMixing));
			properties.setProperty("MascotIgnoreBackground", String.valueOf(Settings.mascotIgnoreBackground));
			properties.setProperty("MascotTextureFilter", String.valueOf(Settings.mascotTextureFilter));
			properties.setProperty("MascotBackgroundFilter", String.valueOf(Settings.mascotBackgroundFilter));

			// media
			properties.setProperty("VlcDir", Settings.vlcDir);
			properties.setProperty("MIDISearchVMS", String.valueOf(Settings.searchVms));
			properties.setProperty("MIDIReopenDevice", String.valueOf(Settings.reopenMidiDevice));
			properties.setProperty("MIDIGlobalSequencer", String.valueOf(Settings.oneMidiAtTime));
			properties.setProperty("EnableMediaDump", String.valueOf(Settings.enableMediaDump));
			properties.setProperty("EnableOTT", String.valueOf(Settings.enableOTT));

			// jvm
			properties.setProperty("JVMHeap", String.valueOf(Settings.xmx));

			// security
			properties.setProperty("SecurityEnabled", String.valueOf(Settings.enableSecurity));
			for (String k: Permission.permissions.keySet()) {
				properties.setProperty("Security." + k, Permission.getPermissionLevelString(k));
			}

			properties.store(fileOutputStream, "KEmulator properties");
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void apply() {
		final String trim = this.aCombo657.getText().trim();
		this.device = trim;
		Emulator.deviceName = trim;
		Devices.curPlatform = Devices.getPlatform(Emulator.deviceName);
		this.defaultFont = this.aCombo689.getText().trim();
		this.rmsFolder = this.aText662.getText().trim();
		Settings.fileEncoding = this.aCombo675.getText().trim();
		this.fontSmallSize = this.aSpinner690.getSelection();
		this.fontMediumSize = this.aSpinner679.getSelection();
		this.fontLargeSize = this.aSpinner670.getSelection();
		this.screenWidth = this.screenWidthText.getText().trim();
		this.screenHeight = this.screenHeightText.getText().trim();
		this.lsoft = this.aText695.getText().trim();
		this.rsoft = this.aText702.getText().trim();
		this.fire = this.aText708.getText().trim();
		this.up = this.aText713.getText().trim();
		this.down = this.aText718.getText().trim();
		this.left = this.aText723.getText().trim();
		this.right = this.aText727.getText().trim();
		KeyMapping.mapDeviceKey(17, KeyMapping.method601(Property.aStringArray661[17]));
		KeyMapping.mapDeviceKey(18, KeyMapping.method601(Property.aStringArray661[18]));
		KeyMapping.mapDeviceKey(14, KeyMapping.method601(Property.aStringArray661[14]));
		KeyMapping.mapDeviceKey(15, KeyMapping.method601(Property.aStringArray661[15]));
		KeyMapping.mapDeviceKey(12, KeyMapping.method601(Property.aStringArray661[12]));
		KeyMapping.mapDeviceKey(13, KeyMapping.method601(Property.aStringArray661[13]));
		KeyMapping.mapDeviceKey(16, KeyMapping.method601(Property.aStringArray661[16]));
		KeyMapping.mapDeviceKey(10, KeyMapping.method601(Property.aStringArray661[10]));
		KeyMapping.mapDeviceKey(11, KeyMapping.method601(Property.aStringArray661[11]));
		KeyMapping.mapDeviceKey(1, KeyMapping.method601(Property.aStringArray661[1]));
		KeyMapping.mapDeviceKey(2, KeyMapping.method601(Property.aStringArray661[2]));
		KeyMapping.mapDeviceKey(3, KeyMapping.method601(Property.aStringArray661[3]));
		KeyMapping.mapDeviceKey(4, KeyMapping.method601(Property.aStringArray661[4]));
		KeyMapping.mapDeviceKey(5, KeyMapping.method601(Property.aStringArray661[5]));
		KeyMapping.mapDeviceKey(6, KeyMapping.method601(Property.aStringArray661[6]));
		KeyMapping.mapDeviceKey(7, KeyMapping.method601(Property.aStringArray661[7]));
		KeyMapping.mapDeviceKey(8, KeyMapping.method601(Property.aStringArray661[8]));
		KeyMapping.mapDeviceKey(9, KeyMapping.method601(Property.aStringArray661[9]));
		KeyMapping.mapDeviceKey(0, KeyMapping.method601(Property.aStringArray661[0]));
		Devices.setProperty("SCREEN_WIDTH", this.screenWidth);
		Devices.setProperty("SCREEN_HEIGHT", this.screenHeight);
		Devices.setProperty("KEY_S1", this.lsoft);
		Devices.setProperty("KEY_S2", this.rsoft);
		Devices.setProperty("KEY_FIRE", this.fire);
		Devices.setProperty("KEY_UP", this.up);
		Devices.setProperty("KEY_DOWN", this.down);
		Devices.setProperty("KEY_LEFT", this.left);
		Devices.setProperty("KEY_RIGHT", this.right);
		Devices.writeProperties();
		KeyMapping.init();
		if (Settings.enableKeyCache != this.aButton696.getSelection()) {
			KeyMapping.keyCacheStack.clear();
			Settings.enableKeyCache = this.aButton696.getSelection();
		}
		if (Settings.enableVibration != this.aButton724.getSelection() && !(Settings.enableVibration = this.aButton724.getSelection())) {
			Emulator.getEmulator().getScreen().stopVibra();
		}
		Settings.enableKeyRepeat = this.aButton728.getSelection();
		Settings.ignoreFullScreen = this.aButton732.getSelection();
		Settings.networkNotAvailable = this.noNetworkBtn.getSelection();
		Settings.synchronizeKeyEvents = synchronizeKeyEventsCheck.getSelection();
		Settings.motorolaSoftKeyFix = softkeyMotFixCheck.getSelection();
		Settings.xrayOverlapScreen = this.aButton746.getSelection();
		Settings.xrayShowClipBorder = this.aButton749.getSelection();
		Settings.infoColorHex = this.aButton752.getSelection();
		Settings.recordReleasedImg = this.aButton703.getSelection();
		Settings.autoGenJad = this.aButton709.getSelection();
		Settings.enableNewTrack = this.aButton714.getSelection();
		Settings.enableMethodTrack = this.aButton719.getSelection();
		Settings.proxyType = this.proxyTypeCombo.getSelectionIndex();
		Settings.proxyHost = this.aText635.getText().trim();
		Settings.proxyPort = this.aText637.getText().trim();
		Settings.proxyUser = this.aText639.getText().trim();
		Settings.proxyPass = this.aText641.getText();
		Settings.proxyDomain = this.aText643.getText().trim();
		//Emulator.inactivityTimer = this.inactiveTimerSpinner.getSelection();
		Settings.rpc = this.rpcBtn.getSelection();
		Settings.awtAntiAliasing = antiAliasBtn.getSelection();
		Settings.textAntiAliasing = antiAliasTextBtn.getSelection();
//        Settings.pollKeyboardOnRepaint = this.pollOnRepaintBtn.getSelection();
		Settings.vlcDir = vlcDirText.getText().trim();
		Settings.locale = localeText.getText().trim();

		//set UILanguage
		Settings.uiLanguage = languageCombo.getText().trim();
		if (!languageCombo.getText().trim().equals(Settings.uiLanguage)) {
			UILocale.initLocale();
			Emulator.getEmulator().updateLanguage();
		}

		Settings.updateBranch = updateBranchCombo.getText().trim();

		Settings.m3gIgnoreOverwrite = m3gIgnoreOverwriteCheck.getSelection();
		Settings.m3gForcePerspectiveCorrection = m3gForcePersCorrect.getSelection();
		Settings.m3gDisableLightClamp = m3gDisableLightClamp.getSelection();
		Settings.m3gFlushImmediately = m3gFlushImmediately.getSelection();

		Settings.m3gAA = m3gAACombo.getSelectionIndex();
		Settings.m3gTexFilter = m3gTexFilterCombo.getSelectionIndex();
		Settings.m3gMipmapping = m3gMipmapCombo.getSelectionIndex();

		Settings.mascotIgnoreBackground = mascotBackgroundFilterCheck.getSelection();
		Settings.mascotTextureFilter = mascotTextureFilterCheck.getSelection();
		Settings.mascotBackgroundFilter = mascotBackgroundFilterCheck.getSelection();
		Settings.mascotNo2DMixing = mascotNo2DMixingCheck.getSelection();

		Settings.searchVms = vmsCheck.getSelection();
//        Settings.reopenMidiDevice = reopenMidiCheck.getSelection();
		Settings.oneMidiAtTime = globalMidiCheck.getSelection();
//		Settings.ignoreRegionRepaint = ignoreRegionRepaintCheck.getSelection();
//		Settings.processSerialCallsOutOfQueue = serialCallsCheck.getSelection();

		Settings.fpsCounter = fpsCounterCheck.getSelection();
		Settings.keyPressOnRepeat = keyPressOnRepeatCheck.getSelection();
//		Settings.forcePaintOnServiceRepaints = forceServicePaintCheck.getSelection();
		Settings.hasPointerEvents = pointerEventsCheck.getSelection();
		Settings.j2lStyleFpsLimit = fpsLimitJlCheck.getSelection();
		Settings.asyncFlush = asyncFlushCheck.getSelection();

		String sysProps = propsText.getText();
		Settings.systemProperties.clear();
		if (!sysProps.isEmpty()) {
			String[] a = sysProps.split("\n");
			for (String s: a) {
				if ((s = s.trim()).isEmpty()) continue;
				int i = s.indexOf(':');
				if (i == -1) continue;
				String k = s.substring(0, i).trim();
				String v = s.substring(i + 1).trim();
				Settings.systemProperties.put(k, v);
			}
		}

		Settings.enableMediaDump = mediaDumpCheck.getSelection();
		Settings.enableOTT = ottCheck.getSelection();
		Settings.enableSecurity = securityCheck.getSelection();

		Settings.autoUpdate = autoUpdatesBtn.getSelection() ? 2 : 1;

//		try {
//			Emulator.getEmulator().getScreen().setSize(Integer.parseInt(screenWidthText.getText()), Integer.parseInt(screenHeightText.getText()));
//		} catch (Exception ignored) {}

		this.updateProxy();
	}

	private void method372(final Shell shell) {
		(this.setsShell = new Shell(shell, 67680)).setText(UILocale.get("OPTION_FRAME_TITLE", "Options & Properties"));
		this.setsShell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		// 1|Segoe UI|9.0|0|WINDOWS|1|-15|0|0|0|400|0|0|0|1|0|0|0|0|Segoe UI
		//setsShell.setFont(setsShell.getFont().getFontData()[0]);
		try {
			if (f == null) {
				FontData fd = setsShell.getFont().getFontData()[0];
				fd.height = (fd.height / -fd.data.lfHeight) * 12;
				f = new Font(shell.getDisplay(), fd);
			}

			setsShell.setFont(f);
		} catch (Error ignored) {

		}
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 2;
		this.setsShell.setLayout(layout);
		this.method393();
		this.method390();
	}

	private void method373() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.horizontalSpan = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalSpan = 1;
		layoutData.verticalAlignment = 2;
		(this.aCombo657 = new Combo(this.customComp, 12)).setLayoutData(layoutData);
		final Enumeration method620 = Devices.method620();
		while (method620.hasMoreElements()) {
			final String text = (String) method620.nextElement();
			this.aCombo657.add(text);
			if (this.device.equalsIgnoreCase(text)) {
				this.aCombo657.setText(text);
			}
		}
		this.aCombo657.addModifyListener(new Class117(this));
	}

	private void genLanguageList() {
		//language setting
		final GridData compLayoutData = new GridData();
		compLayoutData.horizontalAlignment = GridData.FILL;
		compLayoutData.verticalAlignment = GridData.FILL;
		compLayoutData.grabExcessHorizontalSpace = true;

		final GridLayout langLayout = new GridLayout();
		langLayout.numColumns = 2;
		langLayout.marginWidth = 0;
		langLayout.marginHeight = 0;

		langComposite = new Composite(sysChecksGroup, SWT.NONE);
		langComposite.setLayout(langLayout);
		langComposite.setLayoutData(compLayoutData);

		final GridData labelLayoutData = new GridData();
		labelLayoutData.horizontalAlignment = GridData.FILL;
		labelLayoutData.verticalAlignment = GridData.CENTER;

		CLabel label = new CLabel(langComposite, SWT.NONE);
		label.setText(UILocale.get("OPTION_SYSTEM_UI_LANGUAGE", "UI Language:"));
		label.setLayoutData(labelLayoutData);

		final GridData comboLayoutData = new GridData();
		comboLayoutData.horizontalAlignment = GridData.FILL;
		comboLayoutData.grabExcessHorizontalSpace = true;

		languageCombo = new Combo(langComposite, 12);
		languageCombo.setLayoutData(comboLayoutData);
		languageCombo.setFont(f);
		File folder = new File(Emulator.getAbsolutePath() + "/lang/");
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isFile() && file.getName().endsWith(".txt")) {
						String fileNameWithoutExtension = file.getName().substring(0, file.getName().length() - 4);
						languageCombo.add(fileNameWithoutExtension);
					}
				}
			}
		}
		languageCombo.setText(Settings.uiLanguage);

		final GridData labelLayoutData2 = new GridData();
		labelLayoutData2.horizontalAlignment = GridData.FILL;
		labelLayoutData2.verticalAlignment = GridData.CENTER;

		label = new CLabel(langComposite, SWT.NONE);
		label.setText(UILocale.get("OPTION_SYSTEM_UPDATE_BRANCH", "Update branch:"));
		label.setLayoutData(labelLayoutData2);

		final GridData comboLayoutData2 = new GridData();
		comboLayoutData2.horizontalAlignment = GridData.FILL;
		comboLayoutData2.grabExcessHorizontalSpace = true;

		updateBranchCombo = new Combo(langComposite, 12);
		updateBranchCombo.setLayoutData(comboLayoutData2);
		updateBranchCombo.setFont(f);
		updateBranchCombo.add("stable");
		updateBranchCombo.add("dev");

		updateBranchCombo.setText(Settings.updateBranch);
	}

	private void method379() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.horizontalSpan = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		(this.aCombo675 = new Combo(this.customComp, 8)).setLayoutData(layoutData);
		final SortedMap<String, Charset> availableCharsets = Charset.availableCharsets();
		final ArrayList<Comparable> list = new ArrayList(availableCharsets.keySet());
		Collections.sort(list);
		String s = (String) list.get(0);
		for (int i = 0; i < list.size(); ++i) {
			this.aCombo675.add((String) list.get(i));
			if (Settings.fileEncoding.equalsIgnoreCase((String) list.get(i))) {
				s = (String) list.get(i);
			}
		}
		Settings.fileEncoding = s;
		this.aCombo675.setText(s);
	}

	private void method384() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		final GridData layoutData2;
		(layoutData2 = new GridData()).horizontalAlignment = 4;
		layoutData2.grabExcessHorizontalSpace = true;
		layoutData2.verticalAlignment = 2;
		final GridData layoutData3;
		(layoutData3 = new GridData()).horizontalAlignment = 4;
		layoutData3.grabExcessHorizontalSpace = true;
		layoutData3.verticalAlignment = 2;
		final GridData layoutData4;
		(layoutData4 = new GridData()).horizontalAlignment = 4;
		layoutData4.grabExcessHorizontalSpace = true;
		layoutData4.verticalAlignment = 2;
		final GridData layoutData5;
		(layoutData5 = new GridData()).horizontalAlignment = 4;
		layoutData5.grabExcessHorizontalSpace = true;
		layoutData5.verticalAlignment = 2;
		final GridData layoutData6;
		(layoutData6 = new GridData()).horizontalAlignment = 4;
		layoutData6.grabExcessHorizontalSpace = true;
		layoutData6.verticalAlignment = 2;
		final GridData layoutData7;
		(layoutData7 = new GridData()).horizontalAlignment = 4;
		layoutData7.grabExcessHorizontalSpace = true;
		layoutData7.verticalAlignment = 2;
		final GridData layoutData8;
		(layoutData8 = new GridData()).horizontalAlignment = 1;
		layoutData8.verticalAlignment = 2;
		final GridData layoutData9;
		(layoutData9 = new GridData()).horizontalAlignment = 1;
		layoutData9.grabExcessHorizontalSpace = false;
		layoutData9.verticalAlignment = 2;
		final GridData layoutData10;
		(layoutData10 = new GridData()).horizontalAlignment = 4;
		layoutData10.grabExcessHorizontalSpace = true;
		layoutData10.verticalAlignment = 2;
		final GridData layoutData11;
		(layoutData11 = new GridData()).horizontalAlignment = 4;
		layoutData11.grabExcessHorizontalSpace = true;
		layoutData11.verticalAlignment = 2;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 4;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 10;
		layout.horizontalSpacing = 5;
		final GridData layoutData12;
		(layoutData12 = new GridData()).horizontalAlignment = 4;
		layoutData12.horizontalSpan = 3;
		layoutData12.grabExcessHorizontalSpace = true;
		layoutData12.grabExcessVerticalSpace = true;
		//layoutData12.heightHint = 140;
		layoutData12.verticalAlignment = 4;
		(this.aGroup660 = new Group(this.customComp, 0)).setText(UILocale.get("OPTION_CUSTOM_PROPERTIES", "Custom Properties"));
		this.aGroup660.setLayout(layout);
		this.aGroup660.setLayoutData(layoutData12);
		(this.aCLabel694 = new CLabel(this.aGroup660, 0)).setText(UILocale.get("OPTION_CUSTOM_SCREEN_WIDTH", "Screen Width:"));
		this.aCLabel694.setLayoutData(layoutData9);
		(this.screenWidthText = new Text(this.aGroup660, 2048)).setLayoutData(layoutData11);
		(this.aCLabel701 = new CLabel(this.aGroup660, 0)).setText(UILocale.get("OPTION_CUSTOM_SCREEN_HEIGHT", "Screen Height:"));
		this.aCLabel701.setLayoutData(layoutData8);
		(this.screenHeightText = new Text(this.aGroup660, 2048)).setLayoutData(layoutData10);
		(this.aCLabel707 = new CLabel(this.aGroup660, 0)).setText(UILocale.get("OPTION_CUSTOM_KEY_LSK", "Left Soft Key:"));
		(this.aText695 = new Text(this.aGroup660, 2048)).setLayoutData(layoutData7);
		(this.aCLabel712 = new CLabel(this.aGroup660, 0)).setText(UILocale.get("OPTION_CUSTOM_KEY_RSK", "Right Soft Key:"));
		(this.aText702 = new Text(this.aGroup660, 2048)).setLayoutData(layoutData6);
		(this.aCLabel717 = new CLabel(this.aGroup660, 0)).setText(UILocale.get("OPTION_CUSTOM_KEY_MIDDLE", "Fire/Middle:"));
		(this.aText708 = new Text(this.aGroup660, 2048)).setLayoutData(layoutData5);
		this.aCLabel722 = new CLabel(this.aGroup660, 0);
		this.aCLabel722 = new CLabel(this.aGroup660, 0);
		(this.aCLabel722 = new CLabel(this.aGroup660, 0)).setText(UILocale.get("OPTION_CUSTOM_KEY_UP", "Up:"));
		(this.aText713 = new Text(this.aGroup660, 2048)).setLayoutData(layoutData4);
		(this.aCLabel726 = new CLabel(this.aGroup660, 0)).setText(UILocale.get("OPTION_CUSTOM_KEY_DOWN", "Down:"));
		(this.aText718 = new Text(this.aGroup660, 2048)).setLayoutData(layoutData3);
		(this.aCLabel730 = new CLabel(this.aGroup660, 0)).setText(UILocale.get("OPTION_CUSTOM_KEY_LEFT", "Left:"));
		(this.aText723 = new Text(this.aGroup660, 2048)).setLayoutData(layoutData2);
		(this.aCLabel734 = new CLabel(this.aGroup660, 0)).setText(UILocale.get("OPTION_CUSTOM_KEY_RIGHT", "Right:"));
		(this.aText727 = new Text(this.aGroup660, 2048)).setLayoutData(layoutData);
		this.method387();
	}

	private void method387() {
		this.screenWidthText.setText(this.screenWidth);
		this.screenHeightText.setText(this.screenHeight);
		this.aText695.setText(this.lsoft);
		this.aText702.setText(this.rsoft);
		this.aText708.setText(this.fire);
		this.aText713.setText(this.up);
		this.aText718.setText(this.down);
		this.aText723.setText(this.left);
		this.aText727.setText(this.right);
	}

	private void method390() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.horizontalSpan = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		int x = (480 - 320) / 2;
		(this.aComposite667 = new Composite(this.setsShell, 0)).setLayoutData(layoutData);
		(this.aButton676 = new Button(this.aComposite667, 8388616)).setBounds(new Rectangle(62 + x, 1, 68, 19));
		this.aButton676.setSelection(false);
		this.aButton676.setText(UILocale.get("DIALOG_OK", "OK"));
		this.aButton676.addSelectionListener(new Class113(this));
		(this.aButton685 = new Button(this.aComposite667, 8388616)).setBounds(new Rectangle(197 + x, 0, 66, 21));
		this.aButton685.setText(UILocale.get("DIALOG_CANCEL", "Cancel"));
		this.aButton685.addSelectionListener(new Class111(this));
	}

	private void method393() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.horizontalSpan = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		(this.tabFolder = new CTabFolder(this.setsShell, 8390656)).setSelectionBackground(Display.getCurrent().getSystemColor(22));
		this.tabFolder.setSimple(true);
		tabFolder.setFont(f);
		this.tabFolder.setMRUVisible(false);
		this.tabFolder.setUnselectedCloseVisible(false);
		this.tabFolder.setUnselectedImageVisible(false);
		this.tabFolder.setLayoutData(layoutData);
		this.setupCustomComp();
		this.setupKeyMapComp();
		this.setupSystemComp();
		this.setupCoreApiComp();
		setupDisableApiComp();
		setupPropsComp();
		this.setupSysFontComp();
		this.setupRecordsComp();
		this.setupNetworkComp();
		this.setupMediaComp();
		this.setupM3GComp();
		setupMascotComp();
		setupSecurityComp();
		final CTabItem deviceTab;
		(deviceTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_CUSTOM", "General"));
		deviceTab.setControl(this.customComp);
		final CTabItem keymapTab;
		(keymapTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_KEYMAP", "KeyMap"));
		keymapTab.setControl(this.keyMapTabComp);
		final CTabItem sysFontTab;
		(sysFontTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_FONT", "Font"));
		sysFontTab.setControl(this.sysFontComp);
		final CTabItem systemTab;
		(systemTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_SYSTEM", "System"));
		systemTab.setControl(this.systemComp);
		final CTabItem coreApiTab;
		(coreApiTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_COREAPI", "CoreAPI"));
		coreApiTab.setControl(this.coreApiComp);
		final CTabItem disableApiTab;
		(disableApiTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_DISABLE_API", "Disable APIs"));
		disableApiTab.setControl(this.disableApiComp);
		final CTabItem rmsTab;
		(rmsTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_RECORDS", "Records"));
		rmsTab.setControl(this.recordsComp);
		final CTabItem networkTab;
		(networkTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_NETWORK", "Network"));
		networkTab.setControl(this.networkComp);
		final CTabItem mediaTab;
		(mediaTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_MEDIA", "Media"));
		mediaTab.setControl(this.mediaComp);
		final CTabItem m3gTab;
		(m3gTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_M3G", "M3G"));
		m3gTab.setControl(this.m3gComp);
		final CTabItem mascotTab;
		(mascotTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_MASCOT", "MascotCapsule"));
		mascotTab.setControl(this.mascotComp);
		final CTabItem securityTab;
		(securityTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_SECURITY", "Security"));
		securityTab.setControl(this.securityComp);
		final CTabItem propsTab;
		(propsTab = new CTabItem(this.tabFolder, 0)).setText(UILocale.get("OPTION_TAB_SYSTEM_PROPERTIES", "Properties"));
		propsTab.setControl(this.propsComp);
	}

	private void setupCustomComp() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.verticalAlignment = 4;
		this.customComp = new Composite(this.tabFolder, 0);
		final GridData layoutData2;
		(layoutData2 = new GridData()).horizontalAlignment = 4;
		layoutData2.horizontalSpan = 2;
		layoutData2.grabExcessHorizontalSpace = true;
		layoutData2.verticalAlignment = 2;
		final GridData layoutData3;
		(layoutData3 = new GridData()).horizontalAlignment = 4;
		layoutData3.verticalAlignment = 2;
		final GridData layoutData4;
		(layoutData4 = new GridData()).horizontalAlignment = 4;
		layoutData4.verticalAlignment = 2;
		final GridData layoutData5;
		(layoutData5 = new GridData()).horizontalAlignment = 4;
		layoutData5.verticalAlignment = 2;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 3;
		layout.marginWidth = 5;
		this.customComp.setLayout(layout);
		customComp.setFont(f);
		(this.aCLabel658 = new CLabel(this.customComp, 0)).setText(UILocale.get("OPTION_CUSTOM_DEVICE", "Device Select:"));
		this.aCLabel658.setLayoutData(layoutData3);
		this.method373();
		(this.aCLabel673 = new CLabel(this.customComp, 0)).setText(UILocale.get("OPTION_CUSTOM_ENCODING", "Default Encoding:"));
		this.aCLabel673.setLayoutData(layoutData4);
		this.method379();
		(this.labelLocale = new CLabel(this.customComp, 0)).setText(UILocale.get("OPTION_CUSTOM_LOCALE", "MIDP Locale:"));
		this.labelLocale.setLayoutData(layoutData5);

		final GridData layoutData333;
		(layoutData333 = new GridData()).horizontalAlignment = 4;
		layoutData333.horizontalSpan = 2;
		layoutData333.grabExcessHorizontalSpace = true;
		layoutData333.verticalAlignment = 2;
		localeText = new Text(this.customComp, 2048);
		localeText.setLayoutData(layoutData333);
		localeText.setText(Settings.locale);

		this.method384();
		(this.aCLabel738 = new CLabel(this.customComp, 0)).setText(UILocale.get("OPTION_CUSTOM_MAX_FPS", "Max FPS:") + " " + ((Settings.frameRate > 120) ? "\u221e" : String.valueOf(Settings.frameRate)));
		this.aCLabel738.setLayoutData(layoutData);
		(this.aScale669 = new Scale(this.customComp, 256)).setIncrement(1);
		this.aScale669.setMaximum(121);
		this.aScale669.setPageIncrement(5);
		this.aScale669.setSelection(Settings.frameRate);
		this.aScale669.setMinimum(1);
		this.aScale669.setLayoutData(layoutData2);
		this.aScale669.addSelectionListener(new Class109(this));
	}

	private void setupKeyMapComp() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.verticalAlignment = 2;
		final GridData layoutData2;
		(layoutData2 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData2.verticalAlignment = 2;
		layoutData2.horizontalAlignment = 4;
		final GridData layoutData3;
		(layoutData3 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData3.verticalAlignment = 2;
		layoutData3.horizontalAlignment = 4;
		final GridData layoutData4;
		(layoutData4 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData4.verticalAlignment = 2;
		layoutData4.horizontalAlignment = 4;
		final GridData layoutData5;
		(layoutData5 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData5.verticalAlignment = 2;
		layoutData5.horizontalAlignment = 4;
		final GridData layoutData6;
		(layoutData6 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData6.verticalAlignment = 2;
		layoutData6.horizontalAlignment = 4;
		final GridData layoutData7;
		(layoutData7 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData7.verticalAlignment = 2;
		layoutData7.horizontalAlignment = 4;
		final GridData layoutData8;
		(layoutData8 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData8.verticalAlignment = 2;
		layoutData8.horizontalAlignment = 4;
		final GridData layoutData9;
		(layoutData9 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData9.verticalAlignment = 2;
		layoutData9.horizontalAlignment = 4;
		final GridData layoutData10;
		(layoutData10 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData10.verticalAlignment = 2;
		layoutData10.horizontalAlignment = 4;
		final GridData layoutData11;
		(layoutData11 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData11.verticalAlignment = 2;
		layoutData11.horizontalAlignment = 4;
		final GridData layoutData12;
		(layoutData12 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData12.verticalAlignment = 2;
		layoutData12.horizontalAlignment = 4;
		final GridData layoutData13;
		(layoutData13 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData13.verticalAlignment = 2;
		layoutData13.horizontalAlignment = 4;
		final GridData layoutData14;
		(layoutData14 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData14.verticalAlignment = 2;
		layoutData14.horizontalAlignment = 4;
		final GridData layoutData15;
		(layoutData15 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData15.verticalAlignment = 2;
		layoutData15.horizontalAlignment = 4;
		final GridData layoutData16;
		(layoutData16 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData16.verticalAlignment = 2;
		layoutData16.horizontalAlignment = 4;
		final GridData layoutData17;
		(layoutData17 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData17.verticalAlignment = 2;
		layoutData17.horizontalAlignment = 4;
		final GridData layoutData18;
		(layoutData18 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData18.verticalAlignment = 2;
		layoutData18.horizontalAlignment = 4;
		final GridData layoutData19;
		(layoutData19 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData19.verticalAlignment = 2;
		layoutData19.horizontalAlignment = 4;
		final GridData layoutData20;
		(layoutData20 = new GridData()).grabExcessHorizontalSpace = false;
		layoutData20.verticalAlignment = 2;
		layoutData20.horizontalAlignment = 4;
		final GridData layoutData21;
		(layoutData21 = new GridData()).horizontalAlignment = 4;
		layoutData21.verticalAlignment = 2;
		layoutData21.widthHint = 30;
		final GridData layoutData22;
		(layoutData22 = new GridData()).horizontalAlignment = 4;
		layoutData22.verticalAlignment = 2;
		final GridData layoutData23;
		(layoutData23 = new GridData()).horizontalAlignment = 4;
		layoutData23.verticalAlignment = 2;
		final GridData layoutData24;
		(layoutData24 = new GridData()).horizontalAlignment = 4;
		layoutData24.verticalAlignment = 2;
		final GridData layoutData25;
		(layoutData25 = new GridData()).horizontalAlignment = 4;
		layoutData25.verticalAlignment = 2;
		layoutData25.widthHint = 30;
		final GridData layoutData26;
		(layoutData26 = new GridData()).horizontalAlignment = 4;
		layoutData26.verticalAlignment = 2;
		final GridData layoutData27;
		(layoutData27 = new GridData()).horizontalAlignment = 4;
		layoutData27.verticalAlignment = 2;
		final GridData layoutData28;
		(layoutData28 = new GridData()).horizontalSpan = 2;
		layoutData28.verticalAlignment = 2;
		layoutData28.horizontalAlignment = 4;
		final GridData layoutData29;
		(layoutData29 = new GridData()).horizontalAlignment = 4;
		layoutData29.verticalAlignment = 2;
		final GridData layoutData30;
		(layoutData30 = new GridData()).horizontalAlignment = 4;
		layoutData30.verticalAlignment = 2;
		final GridData layoutData31;
		(layoutData31 = new GridData()).horizontalAlignment = 4;
		layoutData31.verticalAlignment = 2;
		final GridData layoutData32;
		(layoutData32 = new GridData()).horizontalAlignment = 4;
		layoutData32.verticalAlignment = 2;
		final GridData layoutData33;
		(layoutData33 = new GridData()).horizontalAlignment = 4;
		layoutData33.verticalAlignment = 2;
		final GridData layoutData34;
		(layoutData34 = new GridData()).horizontalAlignment = 4;
		layoutData34.verticalAlignment = 2;
		final GridData layoutData35;
		(layoutData35 = new GridData()).horizontalAlignment = 4;
		final GridData layoutData36;
		(layoutData36 = new GridData()).horizontalAlignment = 4;
		layoutData36.verticalAlignment = 2;
		final GridData layoutData37;
		(layoutData37 = new GridData()).horizontalAlignment = 4;
		layoutData37.verticalAlignment = 2;
		final GridData layoutData38;
		(layoutData38 = new GridData()).horizontalAlignment = 4;
		layoutData38.verticalAlignment = 2;
		final GridData layoutData39;
		(layoutData39 = new GridData()).horizontalAlignment = 4;
		layoutData39.verticalAlignment = 2;
		final GridData layoutData40;
		(layoutData40 = new GridData()).horizontalAlignment = 4;
		layoutData40.grabExcessHorizontalSpace = false;
		layoutData40.verticalAlignment = 2;

		keyMapTabComp = new Composite(this.tabFolder, 0);
		keyMapTabComp.setFont(f);
		GridLayout l = new GridLayout();
		l.marginWidth = 0;
		l.marginHeight = 0;
		keyMapTabComp.setLayout(l);

		keyMapControllerComp = new Composite(keyMapTabComp, 0);
		keyMapControllerComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new GridLayout();
		l.numColumns = 6;
		keyMapControllerComp.setLayout(l);

		keyMapBindsComp = new Composite(keyMapTabComp, 0);
		final GridLayout layout;
		keyMapBindsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		VerifyListener verify = new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent verifyEvent) {
				verifyEvent.doit = verifyEvent.start == 0 && verifyEvent.text != null;
			}
		};

		(layout = new GridLayout()).numColumns = 4;
		layout.horizontalSpacing = 45;
		this.keyMapBindsComp.setLayout(layout);
		(this.aCLabel646 = new CLabel(this.keyMapControllerComp, 0)).setText(UILocale.get("OPTION_KEYMAP_CONTROLLER", "Controller:"));
		this.aCLabel646.setLayoutData(layoutData);
		this.method400();
		(this.aCLabel741 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_LSK", "LeftSoftKey:"));
		this.aCLabel741.setLayoutData(layoutData38);
		(this.aText731 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText731.setLayoutData(layoutData20);
		this.aText731.setText(KeyMapping.get(17));
		this.aText731.addKeyListener(new Class135(aText731, 17));
		aText731.addVerifyListener(verify);
		(this.aCLabel744 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_RSK", "RightSoftKey:"));
		this.aCLabel744.setLayoutData(layoutData37);
		(this.aText735 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText735.setLayoutData(layoutData2);
		this.aText735.setText(KeyMapping.get(18));
		this.aText735.addKeyListener(new Class135(aText735, 18));
		aText735.addVerifyListener(verify);
		(this.aCLabel762 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_NUM_1", "Num_1:"));
		this.aCLabel762.setLayoutData(layoutData40);
		(this.aText754 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText754.setLayoutData(layoutData16);
		this.aText754.setText(KeyMapping.get(1));
		this.aText754.addKeyListener(new Class135(aText754, 1));
		aText754.addVerifyListener(verify);
		(this.aCLabel765 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_NUM_2", "Num_2:"));
		this.aCLabel765.setLayoutData(layoutData32);
		(this.aText757 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText757.setLayoutData(layoutData5);
		this.aText757.setText(KeyMapping.get(2));
		this.aText757.addKeyListener(new Class135(aText757, 2));
		aText757.addVerifyListener(verify);
		(this.aCLabel767 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_NUM_3", "Num_3:"));
		this.aCLabel767.setLayoutData(layoutData31);
		(this.aText760 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText760.setLayoutData(layoutData15);
		this.aText760.setText(KeyMapping.get(3));
		this.aText760.addKeyListener(new Class135(aText760, 3));
		aText760.addVerifyListener(verify);
		(this.aCLabel769 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_NUM_4", "Num_4:"));
		this.aCLabel769.setLayoutData(layoutData27);
		(this.aText763 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText763.setLayoutData(layoutData6);
		this.aText763.setText(KeyMapping.get(4));
		this.aText763.addKeyListener(new Class135(aText763, 4));
		aText763.addVerifyListener(verify);
		(this.aCLabel771 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_NUM_5", "Num_5:"));
		this.aCLabel771.setLayoutData(layoutData25);
		(this.aText766 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText766.setLayoutData(layoutData14);
		this.aText766.setText(KeyMapping.get(5));
		this.aText766.addKeyListener(new Class135(aText766, 5));
		aText766.addVerifyListener(verify);
		(this.aCLabel773 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_NUM_6", "Num_6:"));
		this.aCLabel773.setLayoutData(layoutData26);
		(this.aText768 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText768.setLayoutData(layoutData7);
		this.aText768.setText(KeyMapping.get(6));
		this.aText768.addKeyListener(new Class135(aText768, 6));
		aText768.addVerifyListener(verify);
		(this.aCLabel626 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_NUM_7", "Num_7:"));
		this.aCLabel626.setLayoutData(layoutData24);
		(this.aText770 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText770.setLayoutData(layoutData13);
		this.aText770.setText(KeyMapping.get(7));
		this.aText770.addKeyListener(new Class135(aText770, 7));
		aText770.addVerifyListener(verify);
		(this.aCLabel628 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_NUM_8", "Num_8:"));
		this.aCLabel628.setLayoutData(layoutData23);
		(this.aText772 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText772.setLayoutData(layoutData8);
		this.aText772.setText(KeyMapping.get(8));
		this.aText772.addKeyListener(new Class135(aText772, 8));
		aText772.addVerifyListener(verify);
		(this.aCLabel630 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_NUM_9", "Num_9:"));
		this.aCLabel630.setLayoutData(layoutData21);
		(this.aText774 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText774.setLayoutData(layoutData12);
		this.aText774.setText(KeyMapping.get(9));
		this.aText774.addKeyListener(new Class135(aText774, 9));
		aText774.addVerifyListener(verify);
		(this.aCLabel632 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_NUM_0", "Num_0:"));
		this.aCLabel632.setLayoutData(layoutData22);
		(this.aText627 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText627.setLayoutData(layoutData9);
		this.aText627.setText(KeyMapping.get(0));
		this.aText627.addKeyListener(new Class135(aText627, 0));
		aText627.addVerifyListener(verify);
		(this.aCLabel634 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_KEY_*", "Key *:"));
		this.aCLabel634.setLayoutData(layoutData30);
		(this.aText629 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText629.setLayoutData(layoutData11);
		this.aText629.setText(KeyMapping.get(10));
		this.aText629.addKeyListener(new Class135(aText629, 10));
		aText629.addVerifyListener(verify);
		(this.aCLabel636 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_KEY_#", "Key #:"));
		this.aCLabel636.setLayoutData(layoutData29);
		(this.aText631 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText631.setLayoutData(layoutData10);
		this.aText631.setText(KeyMapping.get(11));
		this.aText631.addKeyListener(new Class135(aText631, 11));
		aText631.addVerifyListener(verify);
		(this.aCLabel747 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_PAD_LEFT", "Pad_Left:"));
		this.aCLabel747.setLayoutData(layoutData35);
		(this.aText739 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText739.setLayoutData(layoutData19);
		this.aText739.setText(KeyMapping.get(14));
		this.aText739.addKeyListener(new Class135(aText739, 14));
		aText739.addVerifyListener(verify);
		(this.aCLabel750 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_PAD_RIGHT", "Pad_Right:"));
		this.aCLabel750.setLayoutData(layoutData36);
		(this.aText742 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText742.setLayoutData(layoutData3);
		this.aText742.setText(KeyMapping.get(15));
		this.aText742.addKeyListener(new Class135(aText742, 15));
		aText742.addVerifyListener(verify);
		(this.aCLabel753 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_PAD_UP", "Pad_Up:"));
		this.aCLabel753.setLayoutData(layoutData34);
		(this.aText745 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText745.setLayoutData(layoutData18);
		this.aText745.setText(KeyMapping.get(12));
		this.aText745.addKeyListener(new Class135(aText745, 12));
		aText745.addVerifyListener(verify);
		(this.aCLabel756 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_PAD_DOWN", "Pad_Down:"));
		this.aCLabel756.setLayoutData(layoutData33);
		(this.aText748 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText748.setLayoutData(layoutData4);
		this.aText748.setText(KeyMapping.get(13));
		this.aText748.addKeyListener(new Class135(aText748, 13));
		aText748.addVerifyListener(verify);
		(this.aCLabel759 = new CLabel(this.keyMapBindsComp, 0)).setText(UILocale.get("OPTION_KEYMAP_PAD_MIDDLE", "Pad_Middle:"));
		this.aCLabel759.setLayoutData(layoutData39);
		(this.aText751 = new Text(this.keyMapBindsComp, 2048)).setEditable(true);
		this.aText751.setLayoutData(layoutData17);
		this.aText751.setText(KeyMapping.get(16));
		this.aText751.addKeyListener(new Class135(aText751, 16));
		aText751.addVerifyListener(verify);
		(this.aButton696 = new Button(this.keyMapBindsComp, 32)).setText(UILocale.get("OPTION_KEYMAP_KEY_CACHE", "Enable Key Cache"));
		this.aButton696.setLayoutData(layoutData28);
		this.aButton696.setSelection(Settings.enableKeyCache);
		this.method404();
	}

	private void method400() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalSpan = 2;
		layoutData.verticalAlignment = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = 4;
		(this.controllerCombo = new Combo(this.keyMapControllerComp, 8)).setLayoutData(layoutData);
		this.controllerCombo.addModifyListener(new Class185(this));
		final GridData layoutData2;
		(layoutData2 = new GridData()).horizontalAlignment = 2;
		layoutData2.verticalAlignment = 2;
		(this.keymapRefreshBtn = new Button(this.keyMapControllerComp, 8388616)).setText(UILocale.get("OPTION_KEYMAP_REFRESH", "Refresh"));
		this.keymapRefreshBtn.setLayoutData(layoutData2);
		this.keymapRefreshBtn.addSelectionListener(new Class184(this));
		(this.keymapClearBtn = new Button(this.keyMapControllerComp, 8388616)).setText(UILocale.get("OPTION_KEYMAP_CLEAR", "Clear"));
		GridData layoutData3 = new GridData();
		layoutData3.horizontalAlignment = 2;
		layoutData3.verticalAlignment = 2;
		this.keymapClearBtn.setLayoutData(layoutData3);
		this.keymapClearBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				aText627.setText("");
				aText754.setText("");
				aText757.setText("");
				aText760.setText("");
				aText763.setText("");
				aText766.setText("");
				aText768.setText("");
				aText770.setText("");
				aText772.setText("");
				aText774.setText("");
				aText629.setText("");
				aText631.setText("");
				aText739.setText("");
				aText742.setText("");
				aText745.setText("");
				aText748.setText("");
				aText751.setText("");
				aText731.setText("");
				aText735.setText("");
				if (controllerCombo.getText().equals("Keyboard")) {
					for (int i = 0; i < Property.aStringArray661.length; i++) {
						Property.aStringArray661[i] = "";
						KeyMapping.mapDeviceKey(i, "");
					}
				} else {
					for (int i = 0; i < 19; i++) {
						Controllers.bind(controllerCombo.getSelectionIndex() - 1, i, "");
					}
				}
			}
		});
		keymapResetBtn = new Button(this.keyMapControllerComp, 8388616);
		keymapResetBtn.setText(UILocale.get("OPTION_KEYMAP_RESET", "Reset"));
		GridData layoutData4 = new GridData();
		layoutData4.horizontalAlignment = 2;
		layoutData4.verticalAlignment = 2;
		this.keymapResetBtn.setLayoutData(layoutData4);
		this.keymapResetBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (controllerCombo.getText().equals("Keyboard")) {
					KeyMapping.mapDeviceKey(0, "48");
					KeyMapping.mapDeviceKey(1, "55");
					KeyMapping.mapDeviceKey(2, "56");
					KeyMapping.mapDeviceKey(3, "57");
					KeyMapping.mapDeviceKey(4, "52");
					KeyMapping.mapDeviceKey(5, "53");
					KeyMapping.mapDeviceKey(6, "54");
					KeyMapping.mapDeviceKey(7, "49");
					KeyMapping.mapDeviceKey(8, "50");
					KeyMapping.mapDeviceKey(9, "51");
					KeyMapping.mapDeviceKey(10, "42");
					KeyMapping.mapDeviceKey(11, "47");
					KeyMapping.mapDeviceKey(12, "1");
					KeyMapping.mapDeviceKey(13, "2");
					KeyMapping.mapDeviceKey(14, "3");
					KeyMapping.mapDeviceKey(15, "4");
					KeyMapping.mapDeviceKey(16, "13");
					KeyMapping.mapDeviceKey(17, "10");
					KeyMapping.mapDeviceKey(18, "11");
					for (int i = 0; i < Property.aStringArray661.length; i++) {
						Property.aStringArray661[i] = KeyMapping.get(i);
					}
				} else {
					int i = controllerCombo.getSelectionIndex() - 1;
					Controllers.bind(i, 0, "0");
					Controllers.bind(i, 1, "1");
					Controllers.bind(i, 2, "2");
					Controllers.bind(i, 3, "3");
					Controllers.bind(i, 4, "4");
					Controllers.bind(i, 5, "5");
					Controllers.bind(i, 6, "6");
					Controllers.bind(i, 7, "7");
					Controllers.bind(i, 8, "8");
					Controllers.bind(i, 9, "12");
					Controllers.bind(i, 10, "13");
					Controllers.bind(i, 11, "14");
					Controllers.bind(i, 12, "UP");
					Controllers.bind(i, 13, "DOWN");
					Controllers.bind(i, 14, "LEFT");
					Controllers.bind(i, 15, "RIGHT");
					Controllers.bind(i, 16, "9");
					Controllers.bind(i, 17, "10");
					Controllers.bind(i, 18, "11");
				}
				method406();
			}
		});
		this.method402();
	}

	private void method402() {
		this.controllerCombo.removeAll();
		this.controllerCombo.add("Keyboard");
		for (int method740 = Controllers.getControllersCount(), i = 0; i < method740; ++i) {
			this.controllerCombo.add(Controllers.getController(i).getName());
		}
		this.controllerCombo.setText("Keyboard");
	}

	private void method404() {
		Property.aStringArray661[0] = this.aText627.getText().trim();
		Property.aStringArray661[1] = this.aText754.getText().trim();
		Property.aStringArray661[2] = this.aText757.getText().trim();
		Property.aStringArray661[3] = this.aText760.getText().trim();
		Property.aStringArray661[4] = this.aText763.getText().trim();
		Property.aStringArray661[5] = this.aText766.getText().trim();
		Property.aStringArray661[6] = this.aText768.getText().trim();
		Property.aStringArray661[7] = this.aText770.getText().trim();
		Property.aStringArray661[8] = this.aText772.getText().trim();
		Property.aStringArray661[9] = this.aText774.getText().trim();
		Property.aStringArray661[10] = this.aText629.getText().trim();
		Property.aStringArray661[11] = this.aText631.getText().trim();
		Property.aStringArray661[14] = this.aText739.getText().trim();
		Property.aStringArray661[15] = this.aText742.getText().trim();
		Property.aStringArray661[12] = this.aText745.getText().trim();
		Property.aStringArray661[13] = this.aText748.getText().trim();
		Property.aStringArray661[16] = this.aText751.getText().trim();
		Property.aStringArray661[17] = this.aText731.getText().trim();
		Property.aStringArray661[18] = this.aText735.getText().trim();
	}

	private void method406() {
		final int selectionIndex;
		if ((selectionIndex = this.controllerCombo.getSelectionIndex()) < 0 || this.aText627 == null || this.aText627.isDisposed()) {
			return;
		}
		Text text;
		String method744;
		if (selectionIndex == 0) {
			this.aText627.setText(Property.aStringArray661[0]);
			this.aText754.setText(Property.aStringArray661[1]);
			this.aText757.setText(Property.aStringArray661[2]);
			this.aText760.setText(Property.aStringArray661[3]);
			this.aText763.setText(Property.aStringArray661[4]);
			this.aText766.setText(Property.aStringArray661[5]);
			this.aText768.setText(Property.aStringArray661[6]);
			this.aText770.setText(Property.aStringArray661[7]);
			this.aText772.setText(Property.aStringArray661[8]);
			this.aText774.setText(Property.aStringArray661[9]);
			this.aText629.setText(Property.aStringArray661[10]);
			this.aText631.setText(Property.aStringArray661[11]);
			this.aText739.setText(Property.aStringArray661[14]);
			this.aText742.setText(Property.aStringArray661[15]);
			this.aText745.setText(Property.aStringArray661[12]);
			this.aText748.setText(Property.aStringArray661[13]);
			this.aText751.setText(Property.aStringArray661[16]);
			this.aText731.setText(Property.aStringArray661[17]);
			text = this.aText735;
			method744 = Property.aStringArray661[18];
		} else {
			final int n = selectionIndex - 1;
			this.aText627.setText(Controllers.getBind(n, 0));
			this.aText754.setText(Controllers.getBind(n, 1));
			this.aText757.setText(Controllers.getBind(n, 2));
			this.aText760.setText(Controllers.getBind(n, 3));
			this.aText763.setText(Controllers.getBind(n, 4));
			this.aText766.setText(Controllers.getBind(n, 5));
			this.aText768.setText(Controllers.getBind(n, 6));
			this.aText770.setText(Controllers.getBind(n, 7));
			this.aText772.setText(Controllers.getBind(n, 8));
			this.aText774.setText(Controllers.getBind(n, 9));
			this.aText629.setText(Controllers.getBind(n, 10));
			this.aText631.setText(Controllers.getBind(n, 11));
			this.aText739.setText(Controllers.getBind(n, 14));
			this.aText742.setText(Controllers.getBind(n, 15));
			this.aText745.setText(Controllers.getBind(n, 12));
			this.aText748.setText(Controllers.getBind(n, 13));
			this.aText751.setText(Controllers.getBind(n, 16));
			this.aText731.setText(Controllers.getBind(n, 17));
			text = this.aText735;
			method744 = Controllers.getBind(n, 18);
		}
		text.setText(method744);
	}

	public boolean updateController() {
		if (this.controllerCombo == null || this.controllerCombo.isDisposed()) {
			return false;
		}
		EmulatorImpl.asyncExec(new Class193(this));
		return true;
	}

	private void setupSystemComp() {
		(this.systemComp = new Composite(this.tabFolder, 0)).setLayout(new GridLayout());
		this.initSystemComp();
	}

	private void setupCoreApiComp() {
		(this.coreApiComp = new Composite(this.tabFolder, 0)).setLayout(new GridLayout());

		final GridData gridData;
		(gridData = new GridData()).grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
		gridData.horizontalAlignment = 4;
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = 4;
		(this.coreApiGroup = new Group(this.coreApiComp, 0)).setLayout(new GridLayout());
		this.coreApiGroup.setLayoutData(layoutData);
		(this.aButton724 = new Button(this.coreApiGroup, SWT.CHECK)).setText(UILocale.get("OPTION_COREAPI_VIBRATION", "Enable Vibration APIs."));
		this.aButton724.setLayoutData(gridData);
		this.aButton724.setSelection(Settings.enableVibration);
		// автор подсказок - twospaces TODO: localize
		aButton724.setToolTipText("Shakes the window during vibration");

		(this.aButton728 = new Button(this.coreApiGroup, SWT.CHECK)).setText(UILocale.get("OPTION_COREAPI_KEY_REPEAT", "Enable Canvas.keyRepeated(int)."));
		this.aButton728.setLayoutData(gridData);
		this.aButton728.setSelection(Settings.enableKeyRepeat);
		aButton728.setToolTipText("Enables key repeats");

		(this.aButton732 = new Button(this.coreApiGroup, SWT.CHECK)).setText(UILocale.get("OPTION_COREAPI_FULLSCREEN", "Ignore Canvas.setFullScreenMode(boolean)."));
		this.aButton732.setLayoutData(gridData);
		this.aButton732.setSelection(Settings.ignoreFullScreen);
		aButton732.setToolTipText("Forces full screen mode");

		(this.synchronizeKeyEventsCheck = new Button(this.coreApiGroup, SWT.CHECK)).setText(UILocale.get("OPTION_COREAPI_SYNC_KEYEVENTS", "Synchronize key events"));
		this.synchronizeKeyEventsCheck.setLayoutData(gridData);
		this.synchronizeKeyEventsCheck.setSelection(Settings.synchronizeKeyEvents);
		synchronizeKeyEventsCheck.setToolTipText("Compatibility option");

		(this.softkeyMotFixCheck = new Button(this.coreApiGroup, SWT.CHECK)).setText(UILocale.get("OPTION_COREAPI_SOFTKEY_FIX", "Send keyPressed with commandAction"));
		this.softkeyMotFixCheck.setLayoutData(gridData);
		this.softkeyMotFixCheck.setSelection(Settings.motorolaSoftKeyFix);
		softkeyMotFixCheck.setToolTipText("Compatibility tweak for certain Motorola Triplets games");

//		ignoreRegionRepaintCheck = new Button(coreApiGroup, SWT.CHECK);
//		ignoreRegionRepaintCheck.setText(UILocale.get("OPTION_COREAPI_IGNORE_REGION_REPAINT", "Always repaint screen fully"));
//		ignoreRegionRepaintCheck.setLayoutData(gridData);
//		ignoreRegionRepaintCheck.setSelection(Settings.ignoreRegionRepaint);
//		ignoreRegionRepaintCheck.setToolTipText("Compatibility tweak for Opera Mini (breaks Fantasy Zone: Part 1)");

//		serialCallsCheck = new Button(coreApiGroup, SWT.CHECK);
//		serialCallsCheck.setText(UILocale.get("OPTION_COREAPI_SERIAL_CALLS", "Process serial calls out of queue"));
//		serialCallsCheck.setLayoutData(gridData);
//		serialCallsCheck.setSelection(Settings.processSerialCallsOutOfQueue);

		keyPressOnRepeatCheck = new Button(coreApiGroup, SWT.CHECK);
		keyPressOnRepeatCheck.setText(UILocale.get("OPTION_COREAPI_KEYPRESS_ON_REPEAT", "Send keyPressed on repeats"));
		keyPressOnRepeatCheck.setLayoutData(gridData);
		keyPressOnRepeatCheck.setSelection(Settings.keyPressOnRepeat);
		keyPressOnRepeatCheck.setToolTipText("Compatibility tweak for The Elder Scrolls: Oblivion");

//		forceServicePaintCheck = new Button(coreApiGroup, SWT.CHECK);
//		forceServicePaintCheck.setText(UILocale.get("OPTION_COREAPI_FORCE_SERVICE_REPAINTS", "Force paint on serviceRepaints()"));
//		forceServicePaintCheck.setLayoutData(gridData);
//		forceServicePaintCheck.setSelection(Settings.forcePaintOnServiceRepaints);
//		forceServicePaintCheck.setToolTipText("Compatibility tweak for games by SEGA China (breaks other games)");

		pointerEventsCheck = new Button(coreApiGroup, SWT.CHECK);
		pointerEventsCheck.setText(UILocale.get("OPTION_COREAPI_POINTER_EVENTS", "Canvas.hasPointerEvents() return value"));
		pointerEventsCheck.setLayoutData(gridData);
		pointerEventsCheck.setSelection(Settings.hasPointerEvents);

		fpsLimitJlCheck = new Button(coreApiGroup, SWT.CHECK);
		fpsLimitJlCheck.setText(UILocale.get("OPTION_COREAPI_FPS_LIMIT_JL", "J2ME-Loader style FPS limit"));
		fpsLimitJlCheck.setLayoutData(gridData);
		fpsLimitJlCheck.setSelection(Settings.j2lStyleFpsLimit);
		fpsLimitJlCheck.setToolTipText("Compatibility tweak for chinese version of Castlevania");

		asyncFlushCheck = new Button(coreApiGroup, SWT.CHECK);
		asyncFlushCheck.setText(UILocale.get("OPTION_COREAPI_ASYNC_FLUSH", "Async flush"));
		asyncFlushCheck.setLayoutData(gridData);
		asyncFlushCheck.setSelection(Settings.asyncFlush);
	}

	private void setupDisableApiComp() {
		(disableApiComp = new Composite(tabFolder, 0)).setLayout(new GridLayout());

		GridData d = new GridData();
		d.horizontalAlignment = 4;
		d.grabExcessHorizontalSpace = true;
		d.grabExcessVerticalSpace = true;
		d.verticalAlignment = 4;
		d.heightHint = 240;

		disableApiTable = new Table(disableApiComp, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL);
		disableApiTable.setLayoutData(d);
		for (String[] a: API_LIST) {
			TableItem i = new TableItem(disableApiTable, SWT.NONE);
			i.setText(0, a[0]);
			i.setChecked(Settings.protectedPackages.contains(a[1]));
		}
		disableApiTable.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				TableItem item = (TableItem) event.item;
				int idx = disableApiTable.indexOf(item);
				if (item.getChecked()) {
					Settings.protectedPackages.add(API_LIST[idx][1]);
				} else {
					Settings.protectedPackages.remove(API_LIST[idx][1]);
				}
			}
		});
	}

	private void setupPropsComp() {
		(propsComp = new Composite(tabFolder, 0)).setLayout(new GridLayout());

		GridData d = new GridData();
		d.horizontalAlignment = 4;
		d.grabExcessHorizontalSpace = true;
		d.grabExcessVerticalSpace = true;
		d.verticalAlignment = 4;

		propsText = new Text(propsComp, SWT.MULTI | SWT.BORDER);
		propsText.setLayoutData(d);
		propsText.setToolTipText("foo.bar: Example");

		StringBuilder s = new StringBuilder();
		for (String k: Settings.systemProperties.keySet()) {
			s.append(k).append(": ").append(Settings.systemProperties.get(k)).append('\n');
		}
		propsText.setText(s.toString());
	}

	private void setupMediaComp() {
		(this.mediaComp = new Composite(this.tabFolder, 0)).setLayout(new GridLayout());

		final GridData fill = new GridData();
		fill.horizontalAlignment = 4;
		fill.grabExcessHorizontalSpace = true;
		fill.grabExcessVerticalSpace = true;
		fill.verticalAlignment = 4;

		final GridData fillHor = new GridData();
		fillHor.horizontalAlignment = GridData.FILL;
		fillHor.grabExcessHorizontalSpace = true;

		final GridData fillHor2 = new GridData();
		fillHor2.horizontalAlignment = GridData.FILL;
		fillHor2.grabExcessHorizontalSpace = true;

		mediaGroup = new Group(this.mediaComp, 0);
		mediaGroup.setText(UILocale.get("OPTION_TAB_MEDIA", "Media"));
		mediaGroup.setLayout(new GridLayout());
		mediaGroup.setLayoutData(fill);

		new Label(this.mediaGroup, 32).setText(UILocale.get("OPTION_MEDIA_VLC_DIR", "VLC Path") +
				(System.getProperty("os.arch").contains("64") ? " (64-bit only)" : " (32-bit only)") + ":");
		vlcDirText = new Text(mediaGroup, SWT.BORDER);
		vlcDirText.setEditable(true);
		vlcDirText.setEnabled(true);
		vlcDirText.setLayoutData(fillHor2);
		vlcDirText.setText(Settings.vlcDir);

		vmsCheck = new Button(mediaGroup, SWT.CHECK);
		vmsCheck.setText(UILocale.get("OPTION_MEDIA_VMS", "Search for VirtualMIDISynth as MIDI device"));
		vmsCheck.setLayoutData(fillHor);
		vmsCheck.setSelection(Settings.searchVms);

		globalMidiCheck = new Button(mediaGroup, SWT.CHECK);
		globalMidiCheck.setText(UILocale.get("OPTION_MEDIA_GLOBAL_MIDI", "Allow only one MIDI playback at time (reduces lag)"));
		globalMidiCheck.setLayoutData(fillHor);
		globalMidiCheck.setSelection(Settings.oneMidiAtTime);

		mediaDumpCheck = new Button(mediaGroup, SWT.CHECK);
		mediaDumpCheck.setText(UILocale.get("OPTION_MEDIA_DUMP", "Enable media exporting (higher memory usage)"));
		mediaDumpCheck.setLayoutData(fillHor);
		mediaDumpCheck.setSelection(Settings.enableMediaDump);

		ottCheck = new Button(mediaGroup, SWT.CHECK);
		ottCheck.setText(UILocale.get("OPTION_MEDIA_OTT", "Nokia Tone playback (unstable)"));
		ottCheck.setLayoutData(fillHor);
		ottCheck.setSelection(Settings.enableOTT);

//        reopenMidiCheck = new Button(mediaGroup, SWT.CHECK);
//        reopenMidiCheck.setText(UILocale.get("OPTION_MEDIA_REOPEN_MIDI", "Reopen MIDI device every time"));
//        reopenMidiCheck.setLayoutData(fillHor);
//        reopenMidiCheck.setSelection(Settings.reopenMidiDevice);
	}

	private void setupM3GComp() {
		this.m3gComp = new Composite(this.tabFolder, 0);
		m3gComp.setLayout(new GridLayout());
		this.initM3GComp();
	}

	private void initM3GComp() {
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 5;
		/*m3gComp.setLayout(layout);*/

		final GridData groupData = new GridData();
		groupData.horizontalAlignment = 4;
		groupData.grabExcessHorizontalSpace = true;
		groupData.grabExcessVerticalSpace = true;
		groupData.verticalAlignment = 1;

		Group lwjglGroup = new Group(m3gComp, 0);
		lwjglGroup.setText(UILocale.get("OPTION_M3G_LWJGL_SETTINGS", "LWJGL Settings"));
		lwjglGroup.setLayout(layout);
		lwjglGroup.setLayoutData(groupData);

		final GridData labelGridData = new GridData();
		labelGridData.horizontalAlignment = SWT.FILL;
		labelGridData.grabExcessHorizontalSpace = true;
		labelGridData.horizontalSpan = 2;

		m3gIgnoreOverwriteCheck = new Button(lwjglGroup, SWT.CHECK);
		m3gIgnoreOverwriteCheck.setText(UILocale.get("OPTION_M3G_IGNORE_OVERWRITE", "Ignore M3G overwrite hint"));
		m3gIgnoreOverwriteCheck.setLayoutData(labelGridData);
		m3gIgnoreOverwriteCheck.setSelection(Settings.m3gIgnoreOverwrite);

		m3gForcePersCorrect = new Button(lwjglGroup, SWT.CHECK);
		m3gForcePersCorrect.setText(UILocale.get("OPTION_M3G_FORCE_PERSPECTIVE_CORRECTION", "Force perspective correction"));
		m3gForcePersCorrect.setLayoutData(labelGridData);
		m3gForcePersCorrect.setSelection(Settings.m3gForcePerspectiveCorrection);

		m3gDisableLightClamp = new Button(lwjglGroup, SWT.CHECK);
		m3gDisableLightClamp.setText(UILocale.get("OPTION_M3G_DISABLE_LIGHT_CLAMPING", "Disable light clamping"));
		m3gDisableLightClamp.setLayoutData(labelGridData);
		m3gDisableLightClamp.setSelection(Settings.m3gDisableLightClamp);

		m3gFlushImmediately = new Button(lwjglGroup, SWT.CHECK);
		m3gFlushImmediately.setText(UILocale.get("OPTION_M3G_FLUSH_IMMEDIATELY", "Flush contents immediately (slow!)"));
		m3gFlushImmediately.setLayoutData(labelGridData);
		m3gFlushImmediately.setSelection(Settings.m3gFlushImmediately);
		m3gFlushImmediately.setToolTipText("Fixes background in Angry Birds Seasons");

		final GridData dataFillLabel = new GridData();
		dataFillLabel.horizontalAlignment = SWT.FILL;

		final GridData dataFillLabel2 = new GridData();
		dataFillLabel2.horizontalAlignment = SWT.FILL;

		final GridData dataFillLabel3 = new GridData();
		dataFillLabel3.horizontalAlignment = SWT.FILL;

		final GridData listGridData = new GridData();
		listGridData.horizontalAlignment = SWT.FILL;
		listGridData.grabExcessHorizontalSpace = true;

		CLabel tmpLabel = new CLabel(lwjglGroup, 0);
		tmpLabel.setText(UILocale.get("OPTION_M3G_AA", "Anti-aliasing:"));
		tmpLabel.setLayoutData(dataFillLabel);

		m3gAACombo = new Combo(lwjglGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		m3gAACombo.setLayoutData(listGridData);
		m3gAACombo.add(UILocale.get("OPTION_M3G_APP_CONTROLLED", "Application-controlled"));
		m3gAACombo.add(UILocale.get("OPTION_M3G_FORCE_OFF", "Force off"));
		m3gAACombo.add(UILocale.get("OPTION_M3G_FORCE_ON", "Force on"));
		m3gAACombo.setText(m3gAACombo.getItem(Settings.m3gAA));

		tmpLabel = new CLabel(lwjglGroup, 0);
		tmpLabel.setText(UILocale.get("OPTION_M3G_TEXTURE_FILTER", "Texture filter:"));
		tmpLabel.setLayoutData(dataFillLabel2);

		m3gTexFilterCombo = new Combo(lwjglGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		m3gTexFilterCombo.setLayoutData(listGridData);
		m3gTexFilterCombo.add(UILocale.get("OPTION_M3G_APP_CONTROLLED", "Application-controlled"));
		m3gTexFilterCombo.add(UILocale.get("OPTION_M3G_FORCE_NEAREST", "Force nearest"));
		m3gTexFilterCombo.add(UILocale.get("OPTION_M3G_FORCE_LINEAR", "Force linear"));
		m3gTexFilterCombo.setText(m3gTexFilterCombo.getItem(Settings.m3gTexFilter));

		tmpLabel = new CLabel(lwjglGroup, 0);
		tmpLabel.setText(UILocale.get("OPTION_M3G_MIPMAPPING", "Mipmapping:"));
		tmpLabel.setLayoutData(dataFillLabel3);

		m3gMipmapCombo = new Combo(lwjglGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		m3gMipmapCombo.setLayoutData(listGridData);
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_APP_CONTROLLED", "Application-controlled"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_OFF", "Force off"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_BILINEAR", "Force bilinear"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_TRILINEAR", "Force trilinear"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_ANISO_2X", "Force anisotropic 2x"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_ANISO_4X", "Force anisotropic 4x"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_ANISO_8X", "Force anisotropic 8x"));
		m3gMipmapCombo.add(UILocale.get("OPTION_M3G_FORCE_ANISO_16X", "Force anisotropic 16x"));
		m3gMipmapCombo.setText(m3gMipmapCombo.getItem(Settings.m3gMipmapping));
	}

	private void setupMascotComp() {
		mascotComp = new Composite(this.tabFolder, 0);
		mascotComp.setLayout(new GridLayout());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 5;

		final GridData groupData = new GridData();
		groupData.horizontalAlignment = 4;
		groupData.grabExcessHorizontalSpace = true;
		groupData.grabExcessVerticalSpace = true;
		groupData.verticalAlignment = 1;

		Group lwjglGroup = new Group(mascotComp, 0);
		lwjglGroup.setText(UILocale.get("OPTION_M3G_LWJGL_SETTINGS", "LWJGL Settings"));
		lwjglGroup.setLayout(layout);
		lwjglGroup.setLayoutData(groupData);

		final GridData labelGridData = new GridData();
		labelGridData.horizontalAlignment = SWT.FILL;
		labelGridData.grabExcessHorizontalSpace = true;
		labelGridData.horizontalSpan = 2;

		mascotNo2DMixingCheck = new Button(lwjglGroup, SWT.CHECK);
		mascotNo2DMixingCheck.setText(UILocale.get("OPTION_MASCOT_NO_2D_MIXING", "No 2D mixing"));
		mascotNo2DMixingCheck.setLayoutData(labelGridData);
		mascotNo2DMixingCheck.setSelection(Settings.mascotNo2DMixing);
		
		mascotIgnoreBgCheck = new Button(lwjglGroup, SWT.CHECK);
		mascotIgnoreBgCheck.setText(UILocale.get("OPTION_MASCOT_IGNORE_BACKGROUND", "Ignore background"));
		mascotIgnoreBgCheck.setLayoutData(labelGridData);
		mascotIgnoreBgCheck.setSelection(Settings.mascotIgnoreBackground);

		mascotTextureFilterCheck = new Button(lwjglGroup, SWT.CHECK);
		mascotTextureFilterCheck.setText(UILocale.get("OPTION_MASCOT_TEXTURE_FILTER", "Texture filter"));
		mascotTextureFilterCheck.setLayoutData(labelGridData);
		mascotTextureFilterCheck.setSelection(Settings.mascotTextureFilter);

		mascotBackgroundFilterCheck = new Button(lwjglGroup, SWT.CHECK);
		mascotBackgroundFilterCheck.setText(UILocale.get("OPTION_MASCOT_BACKGROUND_FILTER", "Background filter"));
		mascotBackgroundFilterCheck.setLayoutData(labelGridData);
		mascotBackgroundFilterCheck.setSelection(Settings.mascotBackgroundFilter);
	}
	
	private void setupSecurityComp() {
		securityComp = new ScrolledComposite(tabFolder, SWT.V_SCROLL);

		securityContent = new Composite(securityComp, SWT.NONE);

		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 5;

		securityContent.setLayout(new GridLayout());

		final GridData labelGridData = new GridData();
		labelGridData.horizontalAlignment = SWT.FILL;
		labelGridData.grabExcessHorizontalSpace = true;
		labelGridData.horizontalSpan = 2;

		final GridData comboData = new GridData();
		comboData.horizontalAlignment = SWT.FILL;
		comboData.grabExcessHorizontalSpace = true;

		final GridData groupData = new GridData();
		groupData.horizontalAlignment = 4;
		groupData.grabExcessHorizontalSpace = true;
		groupData.grabExcessVerticalSpace = true;
		groupData.verticalAlignment = 1;

		securityCheck = new Button(securityContent, SWT.CHECK);
		securityCheck.setText(UILocale.get("OPTION_SECURITY_ENABLE", "Enable security"));
		securityCheck.setLayoutData(labelGridData);
		securityCheck.setSelection(Settings.enableSecurity);

		Group permGroup = new Group(securityContent, SWT.NONE);
		permGroup.setText(UILocale.get("OPTION_SECURITY_PERMISSIONS", "Permissions"));
		permGroup.setLayout(layout);
		permGroup.setLayoutData(groupData);


		for (String[] s: PERMISSIONS) {
			final GridData data = new GridData();
			data.horizontalAlignment = SWT.FILL;

			CLabel label = new CLabel(permGroup, 0);
			label.setText(s[0]);
			label.setLayoutData(data);

			Combo combo = new Combo(permGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
			combo.addSelectionListener(this);
			combo.setData(s[1]);
			combo.setItems(PERMISSIONS_VALUES[0]);
			String p = Permission.getPermissionLevelString(s[1]);
			for (int i = 0; i < PERMISSIONS_VALUES[1].length; i++) {
				if (PERMISSIONS_VALUES[1][i].equals(p)) {
					combo.setText(PERMISSIONS_VALUES[0][i]);
					break;
				}
			}
			combo.setLayoutData(comboData);
		}

		securityComp.setContent(securityContent);
		securityComp.setExpandHorizontal(true);
	}

	private void initSystemComp() {

		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = 4;
		(this.sysChecksGroup = new Group(this.systemComp, 0)).setLayout(new GridLayout());
		this.sysChecksGroup.setLayoutData(layoutData);
		genLanguageList();
		autoUpdatesBtn = new Button(this.sysChecksGroup, 32);
		autoUpdatesBtn.setText(UILocale.get("OPTION_SYSTEM_AUTO_UPDATES", "Automatically check for updates"));
		autoUpdatesBtn.setSelection(Settings.autoUpdate == 2);
		(this.aButton746 = new Button(this.sysChecksGroup, 32)).setText(UILocale.get("OPTION_SYSTEM_XRAY_BG", "X-Ray View: OverLap images."));
		this.aButton746.setSelection(Settings.xrayOverlapScreen);
		(this.aButton749 = new Button(this.sysChecksGroup, 32)).setText(UILocale.get("OPTION_SYSTEM_XRAY_CLIP", "X-Ray View: Show image clipping region."));
		this.aButton749.setSelection(Settings.xrayShowClipBorder);
		(this.aButton752 = new Button(this.sysChecksGroup, 32)).setText(UILocale.get("OPTION_SYSTEM_INFO_COLOR", "Info View: Show color in (R,G,B)."));
		this.aButton752.setSelection(Settings.infoColorHex);
		(this.aButton703 = new Button(this.sysChecksGroup, 32)).setText(UILocale.get("OPTION_SYSTEM_RELEASED_IMG", "Memory View: Record released images."));
		this.aButton703.setSelection(Settings.recordReleasedImg);
		(this.aButton709 = new Button(this.sysChecksGroup, 32)).setText(UILocale.get("OPTION_SYSTEM_AUTOGEN_JAD", "Auto Generate Jad file with \"KEmu-Platform\"."));
		this.aButton709.setSelection(Settings.autoGenJad);
		(this.aButton714 = new Button(this.sysChecksGroup, 32)).setText(UILocale.get("OPTION_SYSTEM_TRACK_NEW", "Track \"new/new[]...\" operations."));
		this.aButton714.setSelection(Settings.enableNewTrack);
		(this.aButton719 = new Button(this.sysChecksGroup, 32)).setText(UILocale.get("OPTION_SYSTEM_TRACK_METHOD", "Track method calls."));
		this.aButton719.setSelection(Settings.enableMethodTrack);
		//new Label(this.aGroup688, 32).setText(UILocale.uiText("OPTION_SYSTEM_INACTIVITY_TIMER", "Inactivity timer (Set 0 to disable)"));
		//this.inactiveTimerSpinner = new Spinner(this.aGroup688, 32);
		//inactiveTimerSpinner.setValues(0, 0, Integer.MAX_VALUE, 0, 1, 10);
		//inactiveTimerSpinner.setSelection(Emulator.inactivityTimer);
		(this.rpcBtn = new Button(this.sysChecksGroup, 32)).setText(UILocale.get("OPTION_SYSTEM_DISCORD_RICHPRESENCE", "Discord Rich Presence"));
		this.rpcBtn.setSelection(Settings.rpc);
		(this.antiAliasBtn = new Button(this.sysChecksGroup, 32)).setText(UILocale.get("OPTION_SYSTEM_AWT_ANTIALIASING", "AWT Smooth drawing"));
		this.antiAliasBtn.setSelection(Settings.awtAntiAliasing);
//        (this.pollOnRepaintBtn = new Button((Composite)this.sysChecksGroup, 32)).setText(UILocale.get("OPTION_SYSTEM_POLL_ON_REPAINT", "Poll keyboard on repaint"));
//        this.pollOnRepaintBtn.setSelection(Settings.pollKeyboardOnRepaint);
		fpsCounterCheck = new Button(sysChecksGroup, SWT.CHECK);
		fpsCounterCheck.setText(UILocale.get("OPTION_SYSTEM_FPS_COUNT", "FPS Counter"));
		fpsCounterCheck.setSelection(Settings.fpsCounter);
	}

	private void setupSysFontComp() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalSpan = 2;
		layoutData.horizontalAlignment = 4;
		layoutData.verticalAlignment = 2;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.grabExcessHorizontalSpace = true;
		final GridData layoutData2;
		(layoutData2 = new GridData()).horizontalAlignment = 4;
		layoutData2.verticalAlignment = 2;
		final GridData layoutData3;
		(layoutData3 = new GridData()).horizontalAlignment = 1;
		layoutData3.verticalAlignment = 2;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 3;
		(this.sysFontComp = new Composite(this.tabFolder, 0)).setLayout(layout);
		(this.aCLabel638 = new CLabel(this.sysFontComp, 0)).setText(UILocale.get("OPTION_FONT_DEFAULT_FONT", "Default Font:"));
		this.aCLabel638.setLayoutData(layoutData2);
		this.method418();
		(this.aCLabel640 = new CLabel(this.sysFontComp, 0)).setText(UILocale.get("OPTION_FONT_LARGE_SIZE", "Large Size:"));
		this.aCLabel640.setLayoutData(layoutData3);
		(this.aSpinner670 = new Spinner(this.sysFontComp, 2048)).setMinimum(1);
		this.aSpinner670.setSelection(this.fontLargeSize);
		this.aSpinner670.addModifyListener(new Class186(this));
		this.method420();
		(this.aCLabel642 = new CLabel(this.sysFontComp, 0)).setText(UILocale.get("OPTION_FONT_MIDDLE_SIZE", "Medium Size:"));
		(this.aSpinner679 = new Spinner(this.sysFontComp, 2048)).setMinimum(1);
		this.aSpinner679.setSelection(this.fontMediumSize);
		this.aSpinner679.addModifyListener(new Class187(this));
		this.method422();
		(this.aCLabel644 = new CLabel(this.sysFontComp, 0)).setText(UILocale.get("OPTION_FONT_SMALL_SIZE", "Small Size:"));
		(this.aSpinner690 = new Spinner(this.sysFontComp, 2048)).setMinimum(1);
		this.aSpinner690.setSelection(this.fontSmallSize);
		this.aSpinner690.addModifyListener(new Class188(this));
		this.method424();
		(this.aCLabel645 = new CLabel(this.sysFontComp, 0)).setText(UILocale.get("OPTION_FONT_TEST_TEXT", "Test Text:"));
		(this.aText633 = new Text(this.sysFontComp, 2048)).setText(UILocale.get("OPTION_FONT_TEST_TEXT_TXT", "This is an Example."));
		this.aText633.setLayoutData(layoutData);
		this.aText633.addModifyListener(new Class192(this));


		final GridData layoutData4;
		(layoutData4 = new GridData()).horizontalSpan = 3;
		(antiAliasTextBtn = new Button(sysFontComp, 32)).setText(UILocale.get("OPTION_SYSTEM_TEXT_ANTIALIASING", "Text Antialiasing"));
		antiAliasTextBtn.setSelection(Settings.textAntiAliasing);
		antiAliasTextBtn.setLayoutData(layoutData4);
	}

	private void method418() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalSpan = 2;
		layoutData.verticalAlignment = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = 4;
		(this.aCombo689 = new Combo(this.sysFontComp, 8)).setLayoutData(layoutData);
		this.aCombo689.addModifyListener(new Class191(this));
		final FontData[] fontList = Property.aDisplay656.getFontList(null, true);
		final ArrayList<Comparable> list = new ArrayList<Comparable>();
		list.add("Nokia");
		list.add("Series 60");
		for (FontData fontData : fontList) {
			if (!list.contains(fontData.getName()) && !fontData.getName().startsWith("@")) {
				list.add(fontData.getName());
			}
		}
		final FontData[] fontList2 = Property.aDisplay656.getFontList(null, false);
		for (FontData fontData : fontList2) {
			if (!list.contains(fontData.getName()) && !fontData.getName().startsWith("@")) {
				list.add(fontData.getName());
			}
		}
		Collections.sort(list);
		String aString682 = (String) list.get(0);
		for (Comparable comparable : list) {
			this.aCombo689.add((String) comparable);
			if (this.defaultFont.equalsIgnoreCase((String) comparable)) {
				aString682 = (String) comparable;
			}
		}
		this.defaultFont = aString682;
		this.aCombo689.setText(this.defaultFont);
	}

	private void method420() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		(this.aCanvas663 = new Canvas(this.sysFontComp, 264192)).setLayoutData(layoutData);
		this.aCanvas663.addPaintListener(new Class190(this));
	}

	private void method422() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		(this.aCanvas680 = new Canvas(this.sysFontComp, 264192)).setLayoutData(layoutData);
		this.aCanvas680.addPaintListener(new Class196(this));
	}

	private void method424() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		(this.aCanvas691 = new Canvas(this.sysFontComp, 264192)).setLayoutData(layoutData);
		this.aCanvas691.addPaintListener(new Class195(this));
	}

	private void method360(final int n) {
		if (!this.setsShell.isVisible()) {
			return;
		}
		IFont font = null;
		if ((n & 0x1) != 0x0) {
			Label_0080:
			{
				IFont font2;
				if (Settings.g2d == 0) {
					font2 = new FontSWT(this.aCombo689.getText(), this.aSpinner670.getSelection(), 0);
				} else {
					if (Settings.g2d != 1) {
						break Label_0080;
					}
					font2 = new FontAWT(this.aCombo689.getText(), this.aSpinner670.getSelection(), 0, false);
				}
				font = font2;
			}
			final int x = this.aCanvas663.getSize().x;
			final int y = this.aCanvas663.getSize().y;
			this.anIImage671 = Emulator.getEmulator().newImage(x, y, false, 0);
			final IGraphics2D graphics;
			(graphics = this.anIImage671.createGraphics()).setColor(65280, false);
			graphics.setFont(font);
			graphics.drawString(this.aText633.getText(), 1, y >> 1);
			this.aCanvas663.redraw();
		}
		if ((n & 0x2) != 0x0) {
			Label_0242:
			{
				IFont font3;
				if (Settings.g2d == 0) {
					font3 = new FontSWT(this.aCombo689.getText(), this.aSpinner679.getSelection(), 0);
				} else {
					if (Settings.g2d != 1) {
						break Label_0242;
					}
					font3 = new FontAWT(this.aCombo689.getText(), this.aSpinner679.getSelection(), 0, false);
				}
				font = font3;
			}
			final int x2 = this.aCanvas680.getSize().x;
			final int y2 = this.aCanvas680.getSize().y;
			this.anIImage674 = Emulator.getEmulator().newImage(x2, y2, false, 0);
			final IGraphics2D graphics2;
			(graphics2 = this.anIImage674.createGraphics()).setColor(65280, false);
			graphics2.setFont(font);
			graphics2.drawString(this.aText633.getText(), 1, y2 >> 1);
			this.aCanvas680.redraw();
		}
		if ((n & 0x4) != 0x0) {
			Label_0404:
			{
				IFont font4;
				if (Settings.g2d == 0) {
					font4 = new FontSWT(this.aCombo689.getText(), this.aSpinner690.getSelection(), 0);
				} else {
					if (Settings.g2d != 1) {
						break Label_0404;
					}
					font4 = new FontAWT(this.aCombo689.getText(), this.aSpinner690.getSelection(), 0, false);
				}
				font = font4;
			}
			final int x3 = this.aCanvas691.getSize().x;
			final int y3 = this.aCanvas691.getSize().y;
			this.anIImage693 = Emulator.getEmulator().newImage(x3, y3, false, 0);
			final IGraphics2D graphics3;
			(graphics3 = this.anIImage693.createGraphics()).setColor(65280, false);
			graphics3.setFont(font);
			graphics3.drawString(this.aText633.getText(), 1, y3 >> 1);
			this.aCanvas691.redraw();
		}
	}

	private void setupRecordsComp() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 2;
		layoutData.verticalAlignment = 2;
		final GridData layoutData2;
		(layoutData2 = new GridData()).horizontalSpan = 3;
		layoutData2.verticalAlignment = 2;
		layoutData2.horizontalAlignment = 4;
		final GridData layoutData3;
		(layoutData3 = new GridData()).horizontalSpan = 3;
		layoutData3.verticalAlignment = 4;
		layoutData3.grabExcessHorizontalSpace = true;
		layoutData3.grabExcessVerticalSpace = true;
		layoutData3.heightHint = 160;
		layoutData3.horizontalAlignment = 4;
		final GridData layoutData4;
		(layoutData4 = new GridData()).horizontalAlignment = 4;
		layoutData4.grabExcessHorizontalSpace = true;
		layoutData4.verticalAlignment = 2;
		final GridData layoutData5;
		(layoutData5 = new GridData()).horizontalAlignment = 4;
		layoutData5.verticalAlignment = 2;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 3;
		(this.recordsComp = new Composite(this.tabFolder, 0)).setLayout(layout);
		(this.aCLabel683 = new CLabel(this.recordsComp, 0)).setText(UILocale.get("OPTION_RECORDS_RMS_FOLDER", "RMS Folder:"));
		this.aCLabel683.setLayoutData(layoutData5);
		(this.aText662 = new Text(this.recordsComp, 2048)).setEditable(true);
		this.aText662.setEnabled(true);
		this.aText662.setLayoutData(layoutData4);
		this.aText662.setText(this.rmsFolder);
		(this.aButton666 = new Button(this.recordsComp, 8388616)).setText("...");
		this.aButton666.addSelectionListener(new Class101(this));

		if (Emulator.midletClassName != null) {
			(this.aCLabel647 = new CLabel(this.recordsComp, 0)).setText(UILocale.get("OPTION_RECORDS_RMS_TEXT", "All Records in current midlet:"));
			this.aCLabel647.setLayoutData(layoutData2);
			(this.aTable665 = new Table(this.recordsComp, 2080)).setHeaderVisible(false);
			this.aTable665.setLayoutData(layoutData3);
			this.aTable665.setLinesVisible(true);
			(this.aButton761 = new Button(this.recordsComp, 8388608)).setText(UILocale.get("OPTION_RECORDS_SELECT_ALL", "Select All"));
			this.aButton761.addSelectionListener(new Class194(this));
			(this.clearRecordsBtn = new Button(this.recordsComp, 8388608)).setText(UILocale.get("OPTION_RECORDS_CLEAR_RECORD", "Clear Selected Records"));
			this.clearRecordsBtn.setLayoutData(layoutData);
			this.clearRecordsBtn.addSelectionListener(new Class103(this));
			new TableColumn(this.aTable665, 0).setWidth(200);
			this.method428();
		} else {
			rmsTree = new Tree(recordsComp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			rmsTree.setLayoutData(layoutData3);
			String rootPath = getRmsFolderPath();
			rmsTree.addTreeListener(new TreeListener() {
				public void treeCollapsed(TreeEvent treeEvent) {
				}

				public void treeExpanded(TreeEvent e) {
					try {
						TreeItem root = (TreeItem) e.item;
						TreeItem[] items = root.getItems();
						for (TreeItem item: items) {
							if (item.getData() != null) return;
							item.dispose();
						}
						String d = (String) root.getData();
						if (d == null || d.startsWith("r")) {
							return;
						}
						d = d.substring(1);
						String[] list = new File(rootPath + d).list();
						if (list != null) {
							for (String s : list) {
								String l = decodeBase64(s);
								if (l == null) continue;
								TreeItem t = new TreeItem(root, SWT.NONE);
								t.setData("r" + d + "/" + s);
								t.setChecked(root.getChecked());
								t.setText(l);
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});

			String[] list = new File(rootPath).list();
			if (list != null) {
				for (String s : list) {
					String l = decodeBase64(s);
					if (l == null) continue;
					TreeItem t = new TreeItem(rmsTree, SWT.NONE);
					t.setData("m" + s);
					t.setText(l);

					new TreeItem(t, SWT.NONE);
				}
			}


			(this.clearRecordsBtn = new Button(this.recordsComp, 8388608)).setText(UILocale.get("OPTION_RECORDS_CLEAR_RECORD", "Clear Selected Records"));
			this.clearRecordsBtn.setLayoutData(layoutData);
			clearRecordsBtn.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent selectionEvent) {
					TreeItem[] items = rmsTree.getSelection();
					if (items == null)
						return;

					for (TreeItem item: items) {
						String d = (String) item.getData();
						if (d == null || d.startsWith("m")) continue;
						item.dispose();
						try {
							File file = new File(rootPath + d.substring(1));
							for (File value : file.listFiles()) {
								value.delete();
							}
							file.delete();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				public void widgetDefaultSelected(SelectionEvent selectionEvent) {
				}
			});
		}
	}

	private String decodeBase64(String name) {
		try {
			return new String(Base64.getDecoder().decode(name.replace('-', '/').getBytes("UTF-8")), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	private String method374() {
		String s = null;
		Label_0083:
		{
			StringBuffer sb;
			String substring;
			if ((s = this.aText662.getText().trim()).startsWith(".")) {
				sb = new StringBuffer().append(Emulator.getUserPath());
				substring = s.substring(1);
			} else {
				if (!s.startsWith("/") && !s.startsWith("\\")) {
					break Label_0083;
				}
				sb = new StringBuffer().append(Emulator.getUserPath());
				substring = s;
			}
			s = sb.append(substring).toString();
		}
		final File file;
		if (!(file = new File(s)).exists() || !file.isDirectory()) {
			file.mkdirs();
		}
//		final String string = s + "/" + this.method355();
//		final File file2;
//		if (!(file2 = new File(string)).exists() || !file2.isDirectory()) {
//			file2.mkdirs();
//		}
		return s + "/";
	}

	private void method428() {
		if (rmsTree != null) {
			rmsTree.removeAll();
			String rootPath = method374();
			String[] list = new File(rootPath).list();
			if (list != null) {
				for (String s : list) {
					String l = decodeBase64(s);
					if (l == null) continue;
					TreeItem t = new TreeItem(rmsTree, SWT.NONE);
					t.setData("m" + s);
					t.setText(l);

					new TreeItem(t, SWT.NONE);
				}
			}
			return;
		}
		aTable665.removeAll();
		String[] rms = RecordStore.listRecordStores();
		if (rms == null) return;
		for (String s : rms) {
			TableItem ti = new TableItem(this.aTable665, 0);
			ti.setText(0, s);
			ti.setChecked(false);
		}
        /*
        final File file;
        if (!(file = new File(this.method374())).exists() || !file.isDirectory()) {
            return;
        }
        final File[] listFiles;
        if ((listFiles = file.listFiles(new Class105(this))).length > this.aTable665.getItemCount()) {
            for (int i = listFiles.length - this.aTable665.getItemCount(); i > 0; --i) {
                new TableItem(this.aTable665, 0);
            }
        }
        else {
            while (this.aTable665.getItemCount() > listFiles.length) {
                this.aTable665.remove(listFiles.length);
            }
        }
        for (int j = 0; j < listFiles.length; ++j) {
            final TableItem item;
            (item = this.aTable665.getItem(j)).setText(0, listFiles[j].getName().substring(1));
            item.setChecked(false);
        }
         */
	}

	private void setupNetworkComp() {
		(this.networkComp = new Composite(this.tabFolder, 0)).setLayout(new GridLayout());
		this.setupNetworkTabContent();
	}

	private void setupNetworkTabContent() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalSpan = 2;
		layoutData.verticalAlignment = 2;
		layoutData.horizontalAlignment = 2;
		final GridData layoutData2;
		(layoutData2 = new GridData()).horizontalAlignment = 4;
		layoutData2.grabExcessHorizontalSpace = true;
		layoutData2.verticalAlignment = 2;
		final GridData layoutData3;
		(layoutData3 = new GridData()).horizontalAlignment = 3;
		layoutData3.verticalAlignment = 2;
		final GridData layoutData4;
		(layoutData4 = new GridData()).horizontalAlignment = 4;
		layoutData4.grabExcessHorizontalSpace = true;
		layoutData4.verticalAlignment = 2;
		final GridData layoutData5;
		(layoutData5 = new GridData()).horizontalAlignment = 3;
		layoutData5.verticalAlignment = 2;
		final GridData layoutData6;
		(layoutData6 = new GridData()).horizontalAlignment = 3;
		layoutData6.verticalAlignment = 2;
		final GridData gridData;
		(gridData = new GridData()).horizontalAlignment = 4;
		gridData.widthHint = 40;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
		final GridData layoutData7;
		(layoutData7 = new GridData()).horizontalAlignment = 4;
		layoutData7.widthHint = 50;
		layoutData7.grabExcessHorizontalSpace = true;
		layoutData7.verticalAlignment = 2;
		final GridLayout layout;
		(layout = new GridLayout()).numColumns = 4;
		final GridData layoutData8;
		(layoutData8 = new GridData()).horizontalAlignment = 4;
		layoutData8.grabExcessHorizontalSpace = true;
//		layoutData8.grabExcessVerticalSpace = true;
		layoutData8.verticalAlignment = 1;
		(this.networkProxyGroup = new Group(this.networkComp, 0)).setText(UILocale.get("OPTION_NETWORK_PROXY", "Proxy"));
		this.networkProxyGroup.setLayout(layout);
		this.networkProxyGroup.setLayoutData(layoutData8);
		(this.aCLabel648 = new CLabel(this.networkProxyGroup, 0)).setText(UILocale.get("OPTION_NETWORK_PROXY_TYPE", "ProxyType:"));
		this.setupProxyCombo();
		(this.aCLabel651 = new CLabel(this.networkProxyGroup, 0)).setText("");
		(this.aCLabel649 = new CLabel(this.networkProxyGroup, 0)).setText(UILocale.get("OPTION_NETWORK_HOST", "Host:"));
		this.aCLabel649.setLayoutData(layoutData6);
		(this.aText635 = new Text(this.networkProxyGroup, 2048)).setLayoutData(layoutData7);
		this.aText635.setText(Settings.proxyHost);
		(this.aCLabel650 = new CLabel(this.networkProxyGroup, 0)).setText(UILocale.get("OPTION_NETWORK_PORT", "Port:"));
		(this.aText637 = new Text(this.networkProxyGroup, 2048)).setText(Settings.proxyPort);
		this.aText637.setLayoutData(gridData);
		(this.aCLabel652 = new CLabel(this.networkProxyGroup, 0)).setText(UILocale.get("OPTION_NETWORK_USERNAME", "Username:"));
		this.aCLabel652.setLayoutData(layoutData5);
		(this.aText639 = new Text(this.networkProxyGroup, 2048)).setLayoutData(layoutData4);
		this.aText639.setText(Settings.proxyUser);
		(this.aCLabel653 = new CLabel(this.networkProxyGroup, 0)).setText(UILocale.get("OPTION_NETWORK_PASSWORD", "Password:"));
		(this.aText641 = new Text(this.networkProxyGroup, 4196352)).setText(Settings.proxyPass);
		this.aText641.setLayoutData(gridData);
		(this.aCLabel654 = new CLabel(this.networkProxyGroup, 0)).setText(UILocale.get("OPTION_NETWORK_DOMAIN", "Domain:"));
		this.aCLabel654.setLayoutData(layoutData3);
		(this.aText643 = new Text(this.networkProxyGroup, 2048)).setLayoutData(layoutData2);
		this.aText643.setText(Settings.proxyDomain);
		(this.aButton764 = new Button(this.networkProxyGroup, 8388608)).setText(UILocale.get("OPTION_NETWORK_CONNECT", "Connect"));
		this.aButton764.setLayoutData(layoutData);
		this.aButton764.addSelectionListener(new Class97(this));
		this.proxyTypeCombo.addModifyListener(new Class65(this));
		this.proxyTypeCombo.select(Settings.proxyType);

		final GridData gridData2;
		(gridData2 = new GridData()).horizontalAlignment = 4;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.BEGINNING;
		
		(this.noNetworkBtn = new Button(this.networkComp, SWT.CHECK)).setText(UILocale.get("OPTION_COREAPI_NO_NETWORK", "Restrict network connections"));
		this.noNetworkBtn.setLayoutData(gridData2);
		this.noNetworkBtn.setSelection(Settings.networkNotAvailable);
	}

	private void setupProxyCombo() {
		final GridData layoutData;
		(layoutData = new GridData()).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalSpan = 2;
		layoutData.verticalAlignment = 2;
		(this.proxyTypeCombo = new Combo(this.networkProxyGroup, 8)).setLayoutData(layoutData);
		this.proxyTypeCombo.add("None Proxy");
		this.proxyTypeCombo.add("HTTP Proxy");
		this.proxyTypeCombo.add("Socks5 Proxy");
	}

	private static void removeProxyProperties() {
		System.getProperties().remove("http.proxyHost");
		System.getProperties().remove("http.proxyPort");
		System.getProperties().remove("http.auth.ntlm.domain");
		System.getProperties().remove("socksProxyHost");
		System.getProperties().remove("socksProxyPort");
	}

	private void method352() {
		Emulator.getEmulator().getScreen();
		EmulatorScreen.pauseStep();
		if (this.aText635.getText().trim().length() < 1) {
			final MessageBox messageBox;
			(messageBox = new MessageBox(this.setsShell)).setText(UILocale.get("OPTION_NETWORK_PROXY_TEST", "Proxy Test"));
			messageBox.setMessage(UILocale.get("OPTION_NETWORK_PROXY_EMPTY", "Empty proxy host!"));
			messageBox.open();
		} else {
			final MessageBox messageBox2;
			(messageBox2 = new MessageBox(this.setsShell)).setText(UILocale.get("OPTION_NETWORK_PROXY_TEST", "Proxy Test"));
			messageBox2.setMessage(UILocale.get("OPTION_NETWORK_PROXY_UNIMP", "Proxy test is underimplemented!"));
			messageBox2.open();
		}
		((EmulatorScreen) Emulator.getEmulator().getScreen()).resumeStep();
	}

	private void updateProxy() {
		removeProxyProperties();
		final int anInt1257;
		if ((anInt1257 = Settings.proxyType) == 0) {
			return;
		}
		if (anInt1257 == 1) {
			System.setProperty("http.proxyHost", Settings.proxyHost);
			System.setProperty("http.proxyPort", Settings.proxyPort);
			if (Settings.proxyDomain.length() > 0) {
				System.setProperty("http.auth.ntlm.domain", Settings.proxyDomain);
			}
		} else if (anInt1257 == 2) {
			System.setProperty("socksProxyHost", Settings.proxyHost);
			System.setProperty("socksProxyPort", Settings.proxyPort);
		}
		Authenticator.setDefault(new MyAuthenticator(this, Settings.proxyUser, Settings.proxyPass));
	}

	static Combo method361(final Property class38) {
		return class38.aCombo657;
	}

	static void method362(final Property class38) {
		class38.method387();
	}

	static void method375(final Property class38) {
		class38.apply();
	}

	static Shell method364(final Property class38) {
		return class38.setsShell;
	}

	static Scale method370(final Property class38) {
		return class38.aScale669;
	}

	static CLabel method363(final Property class38) {
		return class38.aCLabel738;
	}

	static Combo method376(final Property class38) {
		return class38.controllerCombo;
	}

	static Text method366(final Property class38) {
		return class38.aText731;
	}

	static String[] method365() {
		return Property.aStringArray661;
	}

	static Text method377(final Property class38) {
		return class38.aText735;
	}

	static Text method380(final Property class38) {
		return class38.aText754;
	}

	static Text method385(final Property class38) {
		return class38.aText757;
	}

	static Text method388(final Property class38) {
		return class38.aText760;
	}

	static Text method391(final Property class38) {
		return class38.aText763;
	}

	static Text method394(final Property class38) {
		return class38.aText766;
	}

	static Text method397(final Property class38) {
		return class38.aText768;
	}

	static Text method399(final Property class38) {
		return class38.aText770;
	}

	static Text method401(final Property class38) {
		return class38.aText772;
	}

	static Text method403(final Property class38) {
		return class38.aText774;
	}

	static Text method405(final Property class38) {
		return class38.aText627;
	}

	static Text method407(final Property class38) {
		return class38.aText629;
	}

	static Text method409(final Property class38) {
		return class38.aText631;
	}

	static Text method411(final Property class38) {
		return class38.aText739;
	}

	static Text method413(final Property class38) {
		return class38.aText742;
	}

	static Text method415(final Property class38) {
		return class38.aText745;
	}

	static Text method417(final Property class38) {
		return class38.aText748;
	}

	static Text method419(final Property class38) {
		return class38.aText751;
	}

	static void method381(final Property class38) {
		class38.method406();
	}

	static void method386(final Property class38) {
		class38.method402();
	}

	static void method369(final Property class38, final int n) {
		class38.method360(n);
	}

	static IImage method356(final Property class38) {
		return class38.anIImage671;
	}

	static IImage method378(final Property class38) {
		return class38.anIImage674;
	}

	static IImage method382(final Property class38) {
		return class38.anIImage693;
	}

	static Table method367(final Property class38) {
		return class38.aTable665;
	}

	static String method368(final Property class38) {
		return class38.method374();
	}

	static void method389(final Property class38) {
		class38.method428();
	}

	static Text method421(final Property class38) {
		return class38.aText662;
	}

	static void method392(final Property class38) {
		class38.method352();
	}

	static Combo method383(final Property class38) {
		return class38.proxyTypeCombo;
	}

	static Text method423(final Property class38) {
		return class38.aText635;
	}

	static Text method425(final Property class38) {
		return class38.aText637;
	}

	static Text method427(final Property class38) {
		return class38.aText639;
	}

	static Text method429(final Property class38) {
		return class38.aText641;
	}

	static Text method431(final Property class38) {
		return class38.aText643;
	}

	static Button method357(final Property class38) {
		return class38.aButton764;
	}

	static void method395(final Property class38) {
		removeProxyProperties();
	}

	static {
		Property.aStringArray661 = new String[19];
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.widget instanceof Combo) {
			// permission combo
			Permission.permissions.put((String) e.widget.getData(), Permission.fromString(PERMISSIONS_VALUES[1][((Combo) e.widget).getSelectionIndex()]));
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	private final class MyAuthenticator extends Authenticator {
		private final String aString780;
		private final String aString781;

		MyAuthenticator(final Property class38, final String aString780, final String aString781) {
			super();
			this.aString780 = aString780;
			this.aString781 = aString781;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(this.aString780, this.aString781.toCharArray());
		}
	}

	private final class SortProperties extends Properties {
		private static final long serialVersionUID = 1L;

		public Enumeration keys() {
			final List list;
			Collections.sort((List<Comparable>) (list = Collections.list(super.keys())));
			return Collections.enumeration(list);
		}
	}
}
