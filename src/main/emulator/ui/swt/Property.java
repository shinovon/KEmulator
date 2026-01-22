package emulator.ui.swt;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import emulator.*;
import emulator.custom.CustomMethod;
import emulator.graphics2D.IFont;
import emulator.graphics2D.IGraphics2D;
import emulator.graphics2D.IImage;
import emulator.graphics2D.awt.FontAWT;
import emulator.graphics2D.awt.ImageAWT;
import emulator.graphics2D.swt.FontSWT;
import emulator.graphics2D.swt.ImageSWT;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	private static Display display;
	private Shell setsShell;
	private Text aText662;
	private CTabFolder tabFolder;
	private Text aText731;
	private Text aText735;
	private Text aText739;
	private Text aText742;
	private Text aText745;
	private Text aText748;
	private Text aText751;
	private Text aText754;
	private Text aText757;
	private Text aText760;
	private Text aText763;
	private Text aText766;
	private Text aText768;
	private Text aText770;
	private Text aText772;
	private Text aText774;
	private Text aText627;
	private Text aText629;
	private Text aText631;
	private Button aButton696;
	private Button aButton703;
	private Button aButton714;
	private Button aButton719;
	private Composite systemComp;
	private ScrolledComposite systemScrollComp;
	private Button vibrationCheck;
	private Button noNetworkBtn;
	private Button aButton746;
	private Button aButton749;
	private Button aButton752;
	private Composite fontComp;
	private ScrolledComposite fontScrollComp;
	private Combo defaultFontCombo;
	private Combo monoFontCombo;
	private Spinner aSpinner670;
	private Canvas aCanvas663;
	private Spinner aSpinner679;
	private Canvas aCanvas680;
	private Spinner aSpinner690;
	private Canvas aCanvas691;
	private Text aText633;
	private String defaultFont;
	private String monospaceFont;
	private String rmsFolder;
	private int fontSmallSize;
	private int fontMediumSize;
	private int fontLargeSize;
	Combo controllerCombo;
	private static String[] aStringArray661;
	private IImage anIImage671;
	private IImage anIImage674;
	private IImage anIImage693;
	private Composite recordsComp;
	private Table aTable665;
	private Composite networkComp;
	private Group networkProxyGroup;
	private Combo proxyTypeCombo;
	private Text aText635;
	private Text aText637;
	private Text aText639;
	private Text aText641;
	private Text aText643;
	private Button aButton764;
	private Button rpcBtn;
	private Button antiAliasBtn;
	private Composite mediaComp;
	private Text vlcDirText;
	private Font f;

	private Combo languageCombo;
	private Combo updateBranchCombo;

	private Composite keyMapControllerComp;
	private Composite keyMapTabComp;

	private Button vmsCheck;
	private Button globalMidiCheck;
	private Button fpsCounterCheck;
	private Button antiAliasTextBtn;
	private Composite disableApiComp;
	private Table disableApiTable;
	private Composite propsComp;
	private Text propsText;
	private Button mediaDumpCheck;
	private ScrolledComposite securityComp;
	private Button securityCheck;
	private Composite securityContent;
	private Tree rmsTree;
	private Button autoUpdatesBtn;
	private Combo ottCombo;
	private Text soundfontPathText;
	private Button vlcCheck;
	private Group grpUi;
	private Group grpDebug;
	private Group grpMisc;

	private int lastChangedFont;

	public Property() {
		super();
		this.loadProperties();
		this.updateProxy();
		UILocale.initLocale();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open(final Shell parent) {
		display = parent.getDisplay();
		this.method372(parent);
		this.setsShell.pack();
		this.setsShell.setSize(420, this.setsShell.getSize().y);
		systemScrollComp.setMinHeight(systemComp.computeSize(-1, -1).y);
		
		systemScrollComp.setExpandVertical(true);
		fontScrollComp.setMinHeight(fontComp.computeSize(-1, -1).y);
		fontScrollComp.setExpandVertical(true);
		securityComp.setMinHeight(securityContent.computeSize(-1, -1).y);
		securityComp.setExpandVertical(true);

		this.setsShell.setLocation(parent.getLocation().x + (parent.getSize().x - this.setsShell.getSize().x >> 1), parent.getLocation().y + (parent.getSize().y - this.setsShell.getSize().y >> 1));
		this.setsShell.open();
		while (!this.setsShell.isDisposed()) {
			if (!Property.display.readAndDispatch()) {
				Property.display.sleep();
			}
		}
		this.anIImage671 = null;
		this.anIImage674 = null;
		this.anIImage693 = null;
	}

	public String getDefaultFontName() {
		return this.defaultFont;
	}

	public String getMonospaceFontName() {
		return monospaceFont;
	}

	public void setDefaultFontName(final String aString682) {
		this.defaultFont = aString682;
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
		return getRmsFolderPath() + "legacy" + File.separator;
	}

	public void loadProperties() {
		try {
			final Properties properties = new Properties();
			File file = new File(Emulator.getUserPath(), "property.txt");
			if (file.exists()) {
				final FileInputStream fileInputStream = new FileInputStream(file);
				properties.load(fileInputStream);
				fileInputStream.close();
			}
			this.defaultFont = properties.getProperty("DefaultFont", null);
			this.monospaceFont = properties.getProperty("MonospacedFont", null);
			if (defaultFont == null) {
				String systemName = pickFont(false);
				Font systemFont = Display.getDefault().getSystemFont();
				if (systemFont != null && systemFont.getFontData().length > 0)
					systemName = systemFont.getFontData()[0].getName();
				defaultFont = systemName;
			}
			if (monospaceFont == null) {
				monospaceFont = pickFont(true);
			}
			this.rmsFolder = properties.getProperty("RMSFolder", "/rms");
			this.fontSmallSize = Integer.parseInt(properties.getProperty("FontSmallSize", String.valueOf(12)));
			this.fontMediumSize = Integer.parseInt(properties.getProperty("FontMediumSize", String.valueOf(14)));
			this.fontLargeSize = Integer.parseInt(properties.getProperty("FontLargeSize", String.valueOf(16)));
			Settings.g2d = (properties.getProperty("2D_Graphics_Engine", "AWT").equalsIgnoreCase("SWT") ? 0 : 1);
			Settings.g3d = (properties.getProperty("3D_Graphics_Engine", "LWJ").equalsIgnoreCase("SWERVE") ? 0 : 1);
			Settings.micro3d = (properties.getProperty("Micro3D_Engine", Emulator.isX64() ? "GL" : "DLL").equalsIgnoreCase("DLL") ? 0 : 1);

			// keyboard mappings
			if (properties.containsKey("MAP_KEY_NUM_0")) {
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
			}
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
			Settings.networkNotAvailable = Boolean.parseBoolean(properties.getProperty("NetworkNotAvailable", "false"));

			// platform
			Settings.pollKeyboardOnRepaint = Boolean.parseBoolean(properties.getProperty("PollKeyboardOnRepaint", "true"));
			Settings.ignoreRegionRepaint = Boolean.parseBoolean(properties.getProperty("IgnoreRegionRepaint", "false"));
			Settings.ignoreServiceRepaints = Boolean.parseBoolean(properties.getProperty("IgnoreServiceRepaints", "false"));
			Settings.dontRepaintOnSetCurrent = Boolean.parseBoolean(properties.getProperty("DontRepaintOnSetCurrent", "false"));
			Settings.queueSleep = Boolean.parseBoolean(properties.getProperty("EventQueueSleep", "true"));

			String[] protectedPackages = properties.getProperty("ProtectedPackages", "").split(";");
			if (protectedPackages.length > 0) {
				Settings.protectedPackages.addAll(Arrays.asList(protectedPackages));
			}

			String[] sysProps = properties.getProperty("SystemProperties", "").split("\n");
			for (String s : sysProps) {
				if ((s = s.trim()).isEmpty())
					continue;
				int i = s.indexOf(':');
				if (i == -1)
					continue;
				String k = s.substring(0, i).trim();
				String v = s.substring(i + 1).trim();
				Settings.systemProperties.put(k, v);
			}

			// emulator
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
			Settings.altLessSpeedShortcuts = Boolean.parseBoolean(properties.getProperty("AltLessSpeedShortcuts", "false"));
			Settings.showAppSettingsOnStart = Boolean.parseBoolean(properties.getProperty("ShowAppSettingsOnStart", "true"));
			Settings.globalSettings = Boolean.parseBoolean(properties.getProperty("GlobalSettings", "false"));

			Settings.bypassVserv = Boolean.parseBoolean(properties.getProperty("BypassVserv", "true"));
			Settings.wavCache = Boolean.parseBoolean(properties.getProperty("WavCache", "true"));

			Settings.rpc = Boolean.parseBoolean(properties.getProperty("DiscordRichPresence", "false"));
			Settings.uiLanguage = properties.getProperty("UILanguage", "en");

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

			// display
			Settings.canvasScale = Integer.parseInt(properties.getProperty("CanvasScale", String.valueOf(1)))/100f;
			if (Settings.canvasScale < 1f || Settings.canvasScale % 0.5f != 0) {
				Settings.canvasScale = 1f;
			}
			Settings.resizeMode = ResizeMethod.fromInt(Integer.parseInt(properties.getProperty("ResizeMode", "2")));
			Settings.interpolation = Integer.parseInt(properties.getProperty("Interpolation", "0"));

			// window
			EmulatorScreen.locX = Integer.parseInt(properties.getProperty("LocationX", String.valueOf(Integer.MIN_VALUE)));
			EmulatorScreen.locY = Integer.parseInt(properties.getProperty("LocationY", String.valueOf(Integer.MIN_VALUE)));
			EmulatorScreen.sizeW = Integer.parseInt(properties.getProperty("SizeW", "-1"));
			EmulatorScreen.sizeH = Integer.parseInt(properties.getProperty("SizeH", "-1"));
			EmulatorScreen.maximized = Boolean.parseBoolean(properties.getProperty("Maximized", "false"));
			EmulatorScreen.fullscreen = Boolean.parseBoolean(properties.getProperty("FullscreenWindow", "false"));

			Settings.alwaysOnTop = Boolean.parseBoolean(properties.getProperty("AlwaysOnTop", "false"));

			// m3g
			Settings.m3gContextMode = Integer.parseInt(properties.getProperty("M3GContextMode", "0"));

			// media
			Settings.vlcDir = properties.getProperty("VlcDir", "");
			Settings.searchVms = Boolean.parseBoolean(properties.getProperty("MIDISearchVMS", "true"));
			Settings.reopenMidiDevice = Boolean.parseBoolean(properties.getProperty("MIDIReopenDevice", "true"));
			Settings.oneMidiAtTime = Boolean.parseBoolean(properties.getProperty("MIDIGlobalSequencer", "false"));
			Settings.enableMediaDump = Boolean.parseBoolean(properties.getProperty("EnableMediaDump", "false"));
			Settings.ottDecoder = Integer.parseInt(properties.getProperty("OTTConverter", "2"));
			Settings.enableVlc = Boolean.parseBoolean(properties.getProperty("EnableVlc", "true"));
			Settings.soundfontPath = properties.getProperty("SoundfontPath", "");

			// jvm
			Settings.xmx = Integer.parseInt(properties.getProperty("JVMHeap", "512"));

			// security
			Settings.enableSecurity = Boolean.parseBoolean(properties.getProperty("SecurityEnabled", "true"));

			// devutils
			Settings.ideaPath = properties.getProperty("IdeaPath", null);
			Settings.proguardPath = properties.getProperty("ProguardPath", null);
			Settings.ideaJdkTablePatched = Boolean.parseBoolean(properties.getProperty("IdeaJdkTablePatched", "false"));
			Settings.lastIdeaRepoPath = properties.getProperty("LastIdeaProjectsRepo", "");

			Settings.deviceFile = properties.getProperty("PresetsPath", Settings.deviceFile);
		} catch (Exception ex) {
			if (!(ex instanceof FileNotFoundException)) {
				System.out.println("properties load failed");
				ex.printStackTrace();
			}
			this.defaultFont = "Tahoma";
			this.monospaceFont = "Consolas";
			this.rmsFolder = "/rms";
			EmulatorScreen.locX = Integer.MIN_VALUE;
			EmulatorScreen.locY = Integer.MIN_VALUE;
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
			Settings.canvasScale = 1f;
		}
	}

	private String pickFont(boolean monospace) {
		String res = "Arial";
		final ArrayList<String> list = new ArrayList<>();

		final FontData[] fontList = Display.getCurrent().getFontList(null, true);
		for (FontData fontData : fontList) {
			String name = fontData.getName();
			if (!list.contains(name) && !name.startsWith("@")) {
				list.add(name);
				if (monospace && name.endsWith(" Mono")) {
					res = name;
				}
			}
		}
		final FontData[] fontList2 = Display.getCurrent().getFontList(null, false);
		for (FontData fontData : fontList2) {
			String name = fontData.getName();
			if (!list.contains(name) && !name.startsWith("@")) {
				list.add(name);
				if (monospace && name.endsWith(" Mono")) {
					res = name;
				}
			}
		}

		if (monospace) {
			if (list.contains("Cascadia Mono")) {
				res = "Cascadia Mono";
			} else if (list.contains("DejaVu Sans Mono")) {
				res = "DejaVu Sans Mono";
			} else if (list.contains("Droid Sans Mono")) {
				res = "Droid Sans Mono";
			} else if (list.contains("Consolas")) {
				res = "Consolas";
			} else if (list.contains("Courier New")) {
				res = "Courier New";
			}
		} else {
			if (list.contains("Segoe UI")) {
				res = "Segoe UI";
			} else if (list.contains("Noto Sans")) {
				res = "Noto Sans";
			} else if (list.contains("DejaVu Sans")) {
				res = "DejaVu Sans";
			} else if (list.contains("Liberation Sans")) {
				res = "Liberation Sans";
			} else if (list.contains("Tahoma")) {
				res = "Tahoma";
			} else if (list.contains("Roboto")) {
				res = "Roboto";
			} else if (list.contains("Arial")) {
				res = "Arial";
			}
		}

		return res;
	}

	public void saveProperties() {
		try {
			final FileOutputStream fileOutputStream = new FileOutputStream(Emulator.getUserPath() + "/property.txt");
			final Properties properties = new SortProperties();

			properties.setProperty("DefaultFont", this.defaultFont);
			properties.setProperty("MonospacedFont", this.monospaceFont);
			properties.setProperty("RMSFolder", this.rmsFolder);
			properties.setProperty("FontSmallSize", String.valueOf(this.fontSmallSize));
			properties.setProperty("FontMediumSize", String.valueOf(this.fontMediumSize));
			properties.setProperty("FontLargeSize", String.valueOf(this.fontLargeSize));
			properties.setProperty("2D_Graphics_Engine", (Settings.g2d == 0) ? "SWT" : "AWT");
			properties.setProperty("3D_Graphics_Engine", (Settings.g3d == 0) ? "SWERVE" : "LWJ");
			properties.setProperty("Micro3D_Engine", (Settings.micro3d == 0) ? "DLL" : "GL");

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
			properties.setProperty("NetworkNotAvailable", String.valueOf(Settings.networkNotAvailable));

			// platform
			properties.setProperty("PollKeyboardOnRepaint", String.valueOf(Settings.pollKeyboardOnRepaint));
			properties.setProperty("IgnoreRegionRepaint", String.valueOf(Settings.ignoreRegionRepaint));
			properties.setProperty("IgnoreServiceRepaints", String.valueOf(Settings.ignoreServiceRepaints));
			properties.setProperty("DontRepaintOnSetCurrent", String.valueOf(Settings.dontRepaintOnSetCurrent));
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

			// emulator
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
			if (properties.getProperty("AltLessSpeedShortcuts") == null) //overwrite only if not in the file, can be changed only externaly
				properties.setProperty("AltLessSpeedShortcuts",String.valueOf(Settings.altLessSpeedShortcuts));
			properties.setProperty("ShowAppSettingsOnStart", String.valueOf(Settings.showAppSettingsOnStart));
			properties.setProperty("GlobalSettings", String.valueOf(Settings.globalSettings));

			properties.setProperty("BypassVserv", String.valueOf(Settings.bypassVserv));
			properties.setProperty("WavCache", String.valueOf(Settings.wavCache));

			properties.setProperty("DiscordRichPresence", String.valueOf(Settings.rpc));
			properties.setProperty("UILanguage", Settings.uiLanguage);

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
			properties.setProperty("CanvasScale", String.valueOf(Math.round(Settings.canvasScale*100)));
			properties.setProperty("ResizeMode", Settings.resizeMode.toString());
			properties.setProperty("Interpolation", String.valueOf(Settings.interpolation));

			// window
			properties.setProperty("LocationX", String.valueOf(EmulatorScreen.locX));
			properties.setProperty("LocationY", String.valueOf(EmulatorScreen.locY));
			properties.setProperty("SizeW", String.valueOf(EmulatorScreen.sizeW));
			properties.setProperty("SizeH", String.valueOf(EmulatorScreen.sizeH));
			properties.setProperty("Maximized", String.valueOf(EmulatorScreen.maximized));
			properties.setProperty("FullscreenWindow", String.valueOf(EmulatorScreen.fullscreen));

			properties.setProperty("AlwaysOnTop", String.valueOf(Settings.alwaysOnTop));

			// m3g
			properties.setProperty("M3GContextMode", String.valueOf(Settings.m3gContextMode));

			// media
			properties.setProperty("VlcDir", Settings.vlcDir);
			properties.setProperty("MIDISearchVMS", String.valueOf(Settings.searchVms));
			properties.setProperty("MIDIReopenDevice", String.valueOf(Settings.reopenMidiDevice));
			properties.setProperty("MIDIGlobalSequencer", String.valueOf(Settings.oneMidiAtTime));
			properties.setProperty("EnableMediaDump", String.valueOf(Settings.enableMediaDump));
			properties.setProperty("OTTConverter", String.valueOf(Settings.ottDecoder));
			properties.setProperty("EnableVlc", String.valueOf(Settings.enableVlc));
			properties.setProperty("SoundfontPath", Settings.soundfontPath);

			// jvm
			properties.setProperty("JVMHeap", String.valueOf(Settings.xmx));

			// security
			properties.setProperty("SecurityEnabled", String.valueOf(Settings.enableSecurity));
			for (String k: Permission.permissions.keySet()) {
				properties.setProperty("Security." + k, Permission.getPermissionLevelString(k));
			}

			// devutils
			if (Settings.ideaPath != null) properties.setProperty("IdeaPath", Settings.ideaPath);
			if (Settings.proguardPath != null) properties.setProperty("ProguardPath", Settings.proguardPath);
			properties.setProperty("IdeaJdkTablePatched", String.valueOf(Settings.ideaJdkTablePatched));
			properties.setProperty("LastIdeaProjectsRepo", Settings.lastIdeaRepoPath);

			properties.setProperty("PresetsPath", Settings.deviceFile);

			properties.store(fileOutputStream, "KEmulator properties");
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void apply() {
		this.defaultFont = this.defaultFontCombo.getText().trim();
		this.monospaceFont = this.monoFontCombo.getText().trim();
		this.rmsFolder = this.aText662.getText().trim();
		this.fontSmallSize = this.aSpinner690.getSelection();
		this.fontMediumSize = this.aSpinner679.getSelection();
		this.fontLargeSize = this.aSpinner670.getSelection();
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
		KeyMapping.init();
		if (Settings.enableKeyCache != this.aButton696.getSelection()) {
			KeyMapping.keyCacheStack.clear();
			Settings.enableKeyCache = this.aButton696.getSelection();
		}
		if (Settings.enableVibration != this.vibrationCheck.getSelection() && !(Settings.enableVibration = this.vibrationCheck.getSelection())) {
			Emulator.getEmulator().getScreen().stopVibra();
		}
		Settings.networkNotAvailable = this.noNetworkBtn.getSelection();
		Settings.xrayOverlapScreen = this.aButton746.getSelection();
		Settings.xrayShowClipBorder = this.aButton749.getSelection();
		Settings.infoColorHex = this.aButton752.getSelection();
		Settings.recordReleasedImg = this.aButton703.getSelection();
		Settings.enableNewTrack = this.aButton714.getSelection();
		Settings.enableMethodTrack = this.aButton719.getSelection();
		Settings.proxyType = this.proxyTypeCombo.getSelectionIndex();
		Settings.proxyHost = this.aText635.getText().trim();
		Settings.proxyPort = this.aText637.getText().trim();
		Settings.proxyUser = this.aText639.getText().trim();
		Settings.proxyPass = this.aText641.getText();
		Settings.proxyDomain = this.aText643.getText().trim();
		Settings.rpc = this.rpcBtn.getSelection();
		Settings.awtAntiAliasing = antiAliasBtn.getSelection();
		Settings.textAntiAliasing = antiAliasTextBtn.getSelection();
		Settings.vlcDir = vlcDirText.getText().trim();
		Settings.soundfontPath = soundfontPathText.getText().trim();

		//set UILanguage
		Settings.uiLanguage = languageCombo.getText().trim();
		if (!languageCombo.getText().trim().equals(Settings.uiLanguage)) {
			UILocale.initLocale();
			Emulator.getEmulator().updateLanguage();
		}

		String updateBranch = updateBranchCombo.getText().trim();
		if (!updateBranch.equals(Settings.updateBranch)) {
			Settings.updateBranch = updateBranch;
			Updater.state = Updater.STATE_INITIAL;
		}

		Settings.searchVms = vmsCheck.getSelection();
		Settings.oneMidiAtTime = globalMidiCheck.getSelection();

		Settings.fpsCounter = fpsCounterCheck.getSelection();

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
		Settings.ottDecoder = ottCombo.getSelectionIndex();
		Settings.enableVlc = vlcCheck.getSelection();
		Settings.enableSecurity = securityCheck.getSelection();

		Settings.autoUpdate = autoUpdatesBtn.getSelection() ? 2 : 1;

		this.updateProxy();
	}

	private void method372(final Shell shell) {
		this.setsShell = new Shell(shell, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		setsShell.setText(UILocale.get("OPTION_FRAME_TITLE", "Options & Properties"));
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
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		this.setsShell.setLayout(layout);
		this.method393();
		this.method390();
	}

	private void genLanguageList() {
		final GridLayout langLayout = new GridLayout();
		langLayout.numColumns = 2;
		langLayout.marginWidth = 0;
		langLayout.marginHeight = 0;
		
		Composite langComposite = new Composite(grpUi, SWT.NONE);
		langComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		langComposite.setLayout(langLayout);
		
		final GridData gd_label_1 = new GridData();
		gd_label_1.horizontalAlignment = GridData.FILL;
		gd_label_1.verticalAlignment = GridData.CENTER;
		
		CLabel label_1 = new CLabel(langComposite, SWT.NONE);
		label_1.setText("Language:");
		label_1.setLayoutData(gd_label_1);
		
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
	}

	private void method390() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = 4;
		layoutData.horizontalSpan = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		Composite aComposite667 = new Composite(this.setsShell, 0);
		aComposite667.setLayout(new GridLayout(2, true));
		(aComposite667).setLayoutData(layoutData);
		Button aButton676 = new Button(aComposite667, 8388616);
		aButton676.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		aButton676.setSelection(false);
		aButton676.setText(UILocale.get("DIALOG_OK", "OK"));
		aButton676.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				apply();
				saveProperties();
				setsShell.dispose();
			}
		});
		Button aButton685 = new Button(aComposite667, 8388616);
		aButton685.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		aButton685.setText(UILocale.get("DIALOG_CANCEL", "Cancel"));
		aButton685.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				setsShell.dispose();
			}
		});
	}

	private void method393() {
		final GridData layoutData = new GridData();
		layoutData.grabExcessVerticalSpace = true;
		(layoutData).horizontalAlignment = 4;
		layoutData.horizontalSpan = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		this.tabFolder = new CTabFolder(this.setsShell, SWT.BORDER | SWT.FLAT);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(22));
		this.tabFolder.setSimple(true);
		tabFolder.setFont(f);
		this.tabFolder.setMRUVisible(false);
		this.tabFolder.setUnselectedCloseVisible(false);
		this.tabFolder.setUnselectedImageVisible(false);
		this.tabFolder.setLayoutData(layoutData);
		this.setupKeyMapComp();
		this.setupSystemComp();
		setupDisableApiComp();
		setupPropsComp();
		this.setupSysFontComp();
		this.setupRecordsComp();
		this.setupNetworkComp();
		this.setupMediaComp();
		setupSecurityComp();
		final CTabItem keymapTab = new CTabItem(this.tabFolder, 0);
		keymapTab.setText(UILocale.get("OPTION_TAB_KEYMAP", "KeyMap"));
		keymapTab.setControl(this.keyMapTabComp);
		final CTabItem sysFontTab = new CTabItem(this.tabFolder, 0);
		sysFontTab.setText(UILocale.get("OPTION_TAB_FONT", "Font"));
		sysFontTab.setControl(this.fontScrollComp);
		final CTabItem systemTab = new CTabItem(this.tabFolder, 0);
		systemTab.setText(UILocale.get("OPTION_TAB_SYSTEM", "System"));
		systemTab.setControl(this.systemScrollComp);
		final CTabItem disableApiTab = new CTabItem(this.tabFolder, 0);
		disableApiTab.setText(UILocale.get("OPTION_TAB_DISABLE_API", "Disable APIs"));
		disableApiTab.setControl(this.disableApiComp);
		final CTabItem rmsTab = new CTabItem(this.tabFolder, 0);
		rmsTab.setText(UILocale.get("OPTION_TAB_RECORDS", "Records"));
		rmsTab.setControl(this.recordsComp);
		final CTabItem networkTab = new CTabItem(this.tabFolder, 0);
		networkTab.setText(UILocale.get("OPTION_TAB_NETWORK", "Network"));
		networkTab.setControl(this.networkComp);
		final CTabItem mediaTab = new CTabItem(this.tabFolder, 0);
		mediaTab.setText(UILocale.get("OPTION_TAB_MEDIA", "Media"));
		mediaTab.setControl(this.mediaComp);
		final CTabItem securityTab = new CTabItem(this.tabFolder, 0);
		securityTab.setText(UILocale.get("OPTION_TAB_SECURITY", "Security"));
		securityTab.setControl(this.securityComp);
		final CTabItem propsTab = new CTabItem(this.tabFolder, 0);
		propsTab.setText(UILocale.get("OPTION_TAB_SYSTEM_PROPERTIES", "Properties"));
		propsTab.setControl(this.propsComp);
	}

	private void setupKeyMapComp() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = 4;
		layoutData.verticalAlignment = 2;
		final GridData layoutData2 = new GridData();
		(layoutData2).grabExcessHorizontalSpace = true;
		layoutData2.verticalAlignment = 2;
		layoutData2.horizontalAlignment = 4;
		final GridData layoutData3 = new GridData();
		(layoutData3).grabExcessHorizontalSpace = false;
		layoutData3.verticalAlignment = 2;
		layoutData3.horizontalAlignment = 4;
		final GridData layoutData4 = new GridData();
		(layoutData4).grabExcessHorizontalSpace = false;
		layoutData4.verticalAlignment = 2;
		layoutData4.horizontalAlignment = 4;
		final GridData layoutData5 = new GridData();
		(layoutData5).grabExcessHorizontalSpace = false;
		layoutData5.verticalAlignment = 2;
		layoutData5.horizontalAlignment = 4;
		final GridData layoutData6 = new GridData();
		(layoutData6).grabExcessHorizontalSpace = false;
		layoutData6.verticalAlignment = 2;
		layoutData6.horizontalAlignment = 4;
		final GridData layoutData7 = new GridData();
		(layoutData7).grabExcessHorizontalSpace = false;
		layoutData7.verticalAlignment = 2;
		layoutData7.horizontalAlignment = 4;
		final GridData layoutData8 = new GridData();
		(layoutData8).grabExcessHorizontalSpace = false;
		layoutData8.verticalAlignment = 2;
		layoutData8.horizontalAlignment = 4;
		final GridData layoutData9 = new GridData();
		(layoutData9).grabExcessHorizontalSpace = false;
		layoutData9.verticalAlignment = 2;
		layoutData9.horizontalAlignment = 4;
		final GridData layoutData10 = new GridData();
		(layoutData10).grabExcessHorizontalSpace = false;
		layoutData10.verticalAlignment = 2;
		layoutData10.horizontalAlignment = 4;
		final GridData layoutData11 = new GridData();
		(layoutData11).grabExcessHorizontalSpace = false;
		layoutData11.verticalAlignment = 2;
		layoutData11.horizontalAlignment = 4;
		final GridData layoutData12 = new GridData();
		(layoutData12).grabExcessHorizontalSpace = false;
		layoutData12.verticalAlignment = 2;
		layoutData12.horizontalAlignment = 4;
		final GridData layoutData13 = new GridData();
		(layoutData13).grabExcessHorizontalSpace = false;
		layoutData13.verticalAlignment = 2;
		layoutData13.horizontalAlignment = 4;
		final GridData layoutData14 = new GridData();
		(layoutData14).grabExcessHorizontalSpace = false;
		layoutData14.verticalAlignment = 2;
		layoutData14.horizontalAlignment = 4;
		final GridData layoutData15 = new GridData();
		(layoutData15).grabExcessHorizontalSpace = false;
		layoutData15.verticalAlignment = 2;
		layoutData15.horizontalAlignment = 4;
		final GridData layoutData16 = new GridData();
		(layoutData16).grabExcessHorizontalSpace = false;
		layoutData16.verticalAlignment = 2;
		layoutData16.horizontalAlignment = 4;
		final GridData layoutData17 = new GridData();
		(layoutData17).grabExcessHorizontalSpace = false;
		layoutData17.verticalAlignment = 2;
		layoutData17.horizontalAlignment = 4;
		final GridData layoutData18 = new GridData();
		(layoutData18).grabExcessHorizontalSpace = false;
		layoutData18.verticalAlignment = 2;
		layoutData18.horizontalAlignment = 4;
		final GridData layoutData19 = new GridData();
		(layoutData19).grabExcessHorizontalSpace = false;
		layoutData19.verticalAlignment = 2;
		layoutData19.horizontalAlignment = 4;
		final GridData layoutData20 = new GridData();
		(layoutData20).grabExcessHorizontalSpace = true;
		layoutData20.verticalAlignment = 2;
		layoutData20.horizontalAlignment = 4;
		final GridData layoutData21 = new GridData();
		(layoutData21).horizontalAlignment = 4;
		layoutData21.verticalAlignment = 2;
		layoutData21.widthHint = 30;
		final GridData layoutData22 = new GridData();
		(layoutData22).horizontalAlignment = 4;
		layoutData22.verticalAlignment = 2;
		final GridData layoutData23 = new GridData();
		(layoutData23).horizontalAlignment = 4;
		layoutData23.verticalAlignment = 2;
		final GridData layoutData24 = new GridData();
		(layoutData24).horizontalAlignment = 4;
		layoutData24.verticalAlignment = 2;
		final GridData layoutData25 = new GridData();
		(layoutData25).horizontalAlignment = 4;
		layoutData25.verticalAlignment = 2;
		layoutData25.widthHint = 30;
		final GridData layoutData26 = new GridData();
		(layoutData26).horizontalAlignment = 4;
		layoutData26.verticalAlignment = 2;
		final GridData layoutData27 = new GridData();
		(layoutData27).horizontalAlignment = 4;
		layoutData27.verticalAlignment = 2;
		final GridData layoutData28 = new GridData();
		(layoutData28).horizontalSpan = 2;
		layoutData28.verticalAlignment = 2;
		layoutData28.horizontalAlignment = 4;
		final GridData layoutData29 = new GridData();
		(layoutData29).horizontalAlignment = 4;
		layoutData29.verticalAlignment = 2;
		final GridData layoutData30 = new GridData();
		(layoutData30).horizontalAlignment = 4;
		layoutData30.verticalAlignment = 2;
		final GridData layoutData31 = new GridData();
		(layoutData31).horizontalAlignment = 4;
		layoutData31.verticalAlignment = 2;
		final GridData layoutData32 = new GridData();
		(layoutData32).horizontalAlignment = 4;
		layoutData32.verticalAlignment = 2;
		final GridData layoutData33 = new GridData();
		(layoutData33).horizontalAlignment = 4;
		layoutData33.verticalAlignment = 2;
		final GridData layoutData34 = new GridData();
		(layoutData34).horizontalAlignment = 4;
		layoutData34.verticalAlignment = 2;
		final GridData layoutData36 = new GridData();
		(layoutData36).horizontalAlignment = 4;
		layoutData36.verticalAlignment = 2;
		final GridData layoutData37 = new GridData();
		layoutData37.grabExcessHorizontalSpace = true;
		(layoutData37).horizontalAlignment = 4;
		layoutData37.verticalAlignment = 2;
		final GridData layoutData38 = new GridData();
		layoutData38.grabExcessHorizontalSpace = true;
		(layoutData38).horizontalAlignment = 4;
		layoutData38.verticalAlignment = 2;
		final GridData layoutData39 = new GridData();
		(layoutData39).horizontalAlignment = 4;
		layoutData39.verticalAlignment = 2;
		final GridData layoutData40 = new GridData();
		(layoutData40).horizontalAlignment = 4;
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

		Composite keyMapBindsComp = new Composite(keyMapTabComp, 0);
		final GridLayout layout = new GridLayout();
		keyMapBindsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		VerifyListener verify = new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent verifyEvent) {
				verifyEvent.doit = verifyEvent.start == 0 && verifyEvent.text != null;
			}
		};

		layout.numColumns = 4;
		keyMapBindsComp.setLayout(layout);
		
		CLabel aCLabel646 = new CLabel(this.keyMapControllerComp, 0);
		aCLabel646.setText(UILocale.get("OPTION_KEYMAP_CONTROLLER", "Controller:"));
		aCLabel646.setLayoutData(layoutData);
		
		this.method400();
		
		CLabel aCLabel741 = new CLabel(keyMapBindsComp, 0);
		(aCLabel741).setText(UILocale.get("OPTION_KEYMAP_LSK", "LeftSoftKey:"));
		aCLabel741.setLayoutData(layoutData38);
		
		(this.aText731 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText731.setLayoutData(layoutData20);
		this.aText731.setText(KeyMapping.get(17));
		this.aText731.addKeyListener(new Class135(aText731, 17));
		aText731.addVerifyListener(verify);
		
		CLabel aCLabel744 = new CLabel(keyMapBindsComp, 0);
		(aCLabel744).setText(UILocale.get("OPTION_KEYMAP_RSK", "RightSoftKey:"));
		aCLabel744.setLayoutData(layoutData37);
		
		(this.aText735 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText735.setLayoutData(layoutData2);
		this.aText735.setText(KeyMapping.get(18));
		this.aText735.addKeyListener(new Class135(aText735, 18));
		aText735.addVerifyListener(verify);
		
		CLabel aCLabel762 = new CLabel(keyMapBindsComp, 0);
		(aCLabel762).setText(UILocale.get("OPTION_KEYMAP_NUM_1", "Num_1:"));
		aCLabel762.setLayoutData(layoutData40);
		
		(this.aText754 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText754.setLayoutData(layoutData16);
		this.aText754.setText(KeyMapping.get(1));
		this.aText754.addKeyListener(new Class135(aText754, 1));
		aText754.addVerifyListener(verify);
		
		CLabel aCLabel765 = new CLabel(keyMapBindsComp, 0);
		(aCLabel765).setText(UILocale.get("OPTION_KEYMAP_NUM_2", "Num_2:"));
		aCLabel765.setLayoutData(layoutData32);
		
		(this.aText757 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText757.setLayoutData(layoutData5);
		this.aText757.setText(KeyMapping.get(2));
		this.aText757.addKeyListener(new Class135(aText757, 2));
		aText757.addVerifyListener(verify);
		
		CLabel aCLabel767 = new CLabel(keyMapBindsComp, 0);
		(aCLabel767).setText(UILocale.get("OPTION_KEYMAP_NUM_3", "Num_3:"));
		aCLabel767.setLayoutData(layoutData31);
		
		(this.aText760 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText760.setLayoutData(layoutData15);
		this.aText760.setText(KeyMapping.get(3));
		this.aText760.addKeyListener(new Class135(aText760, 3));
		aText760.addVerifyListener(verify);
		
		CLabel aCLabel769 = new CLabel(keyMapBindsComp, 0);
		(aCLabel769).setText(UILocale.get("OPTION_KEYMAP_NUM_4", "Num_4:"));
		aCLabel769.setLayoutData(layoutData27);
		
		(this.aText763 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText763.setLayoutData(layoutData6);
		this.aText763.setText(KeyMapping.get(4));
		this.aText763.addKeyListener(new Class135(aText763, 4));
		aText763.addVerifyListener(verify);
		
		CLabel aCLabel771 = new CLabel(keyMapBindsComp, 0);
		(aCLabel771).setText(UILocale.get("OPTION_KEYMAP_NUM_5", "Num_5:"));
		aCLabel771.setLayoutData(layoutData25);
		
		(this.aText766 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText766.setLayoutData(layoutData14);
		this.aText766.setText(KeyMapping.get(5));
		this.aText766.addKeyListener(new Class135(aText766, 5));
		aText766.addVerifyListener(verify);
		
		CLabel aCLabel773 = new CLabel(keyMapBindsComp, 0);
		(aCLabel773).setText(UILocale.get("OPTION_KEYMAP_NUM_6", "Num_6:"));
		aCLabel773.setLayoutData(layoutData26);
		
		(this.aText768 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText768.setLayoutData(layoutData7);
		this.aText768.setText(KeyMapping.get(6));
		this.aText768.addKeyListener(new Class135(aText768, 6));
		aText768.addVerifyListener(verify);
		
		CLabel aCLabel626 = new CLabel(keyMapBindsComp, 0);
		(aCLabel626).setText(UILocale.get("OPTION_KEYMAP_NUM_7", "Num_7:"));
		aCLabel626.setLayoutData(layoutData24);
		
		(this.aText770 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText770.setLayoutData(layoutData13);
		this.aText770.setText(KeyMapping.get(7));
		this.aText770.addKeyListener(new Class135(aText770, 7));
		aText770.addVerifyListener(verify);
		
		CLabel aCLabel628 = new CLabel(keyMapBindsComp, 0);
		(aCLabel628).setText(UILocale.get("OPTION_KEYMAP_NUM_8", "Num_8:"));
		aCLabel628.setLayoutData(layoutData23);
		
		(this.aText772 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText772.setLayoutData(layoutData8);
		this.aText772.setText(KeyMapping.get(8));
		this.aText772.addKeyListener(new Class135(aText772, 8));
		aText772.addVerifyListener(verify);
		
		CLabel aCLabel630 = new CLabel(keyMapBindsComp, 0);
		(aCLabel630).setText(UILocale.get("OPTION_KEYMAP_NUM_9", "Num_9:"));
		aCLabel630.setLayoutData(layoutData21);
		
		(this.aText774 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText774.setLayoutData(layoutData12);
		this.aText774.setText(KeyMapping.get(9));
		this.aText774.addKeyListener(new Class135(aText774, 9));
		aText774.addVerifyListener(verify);
		
		CLabel aCLabel632 = new CLabel(keyMapBindsComp, 0);
		(aCLabel632).setText(UILocale.get("OPTION_KEYMAP_NUM_0", "Num_0:"));
		aCLabel632.setLayoutData(layoutData22);
		
		this.aText627 = new Text(keyMapBindsComp, 2048);
		aText627.setEditable(true);
		this.aText627.setLayoutData(layoutData9);
		this.aText627.setText(KeyMapping.get(0));
		this.aText627.addKeyListener(new Class135(aText627, 0));
		aText627.addVerifyListener(verify);
		
		CLabel aCLabel634 = new CLabel(keyMapBindsComp, 0);
		(aCLabel634).setText(UILocale.get("OPTION_KEYMAP_KEY_*", "Key *:"));
		aCLabel634.setLayoutData(layoutData30);
		
		(this.aText629 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText629.setLayoutData(layoutData11);
		this.aText629.setText(KeyMapping.get(10));
		this.aText629.addKeyListener(new Class135(aText629, 10));
		aText629.addVerifyListener(verify);
		
		CLabel aCLabel636 = new CLabel(keyMapBindsComp, 0);
		(aCLabel636).setText(UILocale.get("OPTION_KEYMAP_KEY_#", "Key #:"));
		aCLabel636.setLayoutData(layoutData29);
		
		(this.aText631 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText631.setLayoutData(layoutData10);
		this.aText631.setText(KeyMapping.get(11));
		this.aText631.addKeyListener(new Class135(aText631, 11));
		aText631.addVerifyListener(verify);
		
		CLabel aCLabel747 = new CLabel(keyMapBindsComp, 0);
		(aCLabel747).setText(UILocale.get("OPTION_KEYMAP_PAD_LEFT", "Pad_Left:"));
		
		(this.aText739 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText739.setLayoutData(layoutData19);
		this.aText739.setText(KeyMapping.get(14));
		this.aText739.addKeyListener(new Class135(aText739, 14));
		aText739.addVerifyListener(verify);
		
		CLabel aCLabel750 = new CLabel(keyMapBindsComp, 0);
		(aCLabel750).setText(UILocale.get("OPTION_KEYMAP_PAD_RIGHT", "Pad_Right:"));
		aCLabel750.setLayoutData(layoutData36);
		
		(this.aText742 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText742.setLayoutData(layoutData3);
		this.aText742.setText(KeyMapping.get(15));
		this.aText742.addKeyListener(new Class135(aText742, 15));
		aText742.addVerifyListener(verify);
		
		CLabel aCLabel753 = new CLabel(keyMapBindsComp, 0);
		(aCLabel753).setText(UILocale.get("OPTION_KEYMAP_PAD_UP", "Pad_Up:"));
		aCLabel753.setLayoutData(layoutData34);
		
		(this.aText745 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText745.setLayoutData(layoutData18);
		this.aText745.setText(KeyMapping.get(12));
		this.aText745.addKeyListener(new Class135(aText745, 12));
		aText745.addVerifyListener(verify);
		
		CLabel aCLabel756 = new CLabel(keyMapBindsComp, 0);
		(aCLabel756).setText(UILocale.get("OPTION_KEYMAP_PAD_DOWN", "Pad_Down:"));
		aCLabel756.setLayoutData(layoutData33);
		
		(this.aText748 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText748.setLayoutData(layoutData4);
		this.aText748.setText(KeyMapping.get(13));
		this.aText748.addKeyListener(new Class135(aText748, 13));
		aText748.addVerifyListener(verify);
		
		CLabel aCLabel759 = new CLabel(keyMapBindsComp, 0);
		(aCLabel759).setText(UILocale.get("OPTION_KEYMAP_PAD_MIDDLE", "Pad_Middle:"));
		aCLabel759.setLayoutData(layoutData39);
		
		(this.aText751 = new Text(keyMapBindsComp, 2048)).setEditable(true);
		this.aText751.setLayoutData(layoutData17);
		this.aText751.setText(KeyMapping.get(16));
		this.aText751.addKeyListener(new Class135(aText751, 16));
		aText751.addVerifyListener(verify);
		
		this.aButton696 = new Button(keyMapBindsComp, 32);
		aButton696 .setText(UILocale.get("OPTION_KEYMAP_KEY_CACHE", "Enable Key Cache"));
		this.aButton696.setLayoutData(layoutData28);
		this.aButton696.setSelection(Settings.enableKeyCache);
		this.method404();
	}

	private void method400() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalSpan = 2;
		layoutData.verticalAlignment = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = 4;
		this.controllerCombo = new Combo(this.keyMapControllerComp, 8);
		controllerCombo.setLayoutData(layoutData);
		this.controllerCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent modifyEvent) {
				method406();
			}
		});
		final GridData layoutData2 = new GridData();
		(layoutData2).horizontalAlignment = 2;
		layoutData2.verticalAlignment = 2;
		Button keymapRefreshBtn;
		keymapRefreshBtn = new Button(this.keyMapControllerComp, 8388616);
		keymapRefreshBtn.setText(UILocale.get("OPTION_KEYMAP_REFRESH", "Refresh"));
		keymapRefreshBtn.setLayoutData(layoutData2);
		keymapRefreshBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				Controllers.refresh(true);
				method402();
			}
		});
		Button keymapClearBtn;
		keymapClearBtn = new Button(this.keyMapControllerComp, 8388616);
		keymapClearBtn.setText(UILocale.get("OPTION_KEYMAP_CLEAR", "Clear"));
		GridData layoutData3 = new GridData();
		layoutData3.horizontalAlignment = 2;
		layoutData3.verticalAlignment = 2;
		keymapClearBtn.setLayoutData(layoutData3);
		keymapClearBtn.addSelectionListener(new SelectionAdapter() {
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
		Button keymapResetBtn = new Button(this.keyMapControllerComp, 8388616);
		keymapResetBtn.setText(UILocale.get("OPTION_KEYMAP_RESET", "Reset"));
		GridData layoutData4 = new GridData();
		layoutData4.horizontalAlignment = 2;
		layoutData4.verticalAlignment = 2;
		keymapResetBtn.setLayoutData(layoutData4);
		keymapResetBtn.addSelectionListener(new SelectionAdapter() {
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
		display.asyncExec(new Class193(this));
		return true;
	}

	private void setupSystemComp() {
		this.systemScrollComp = new ScrolledComposite(this.tabFolder, SWT.V_SCROLL);
		this.systemComp = new Composite(this.systemScrollComp, 0);
		systemComp.setLayout(new GridLayout());
		this.initSystemComp();
		systemScrollComp.setExpandHorizontal(true);
		systemScrollComp.setContent(systemComp);
	}

	private void setupDisableApiComp() {
		disableApiComp = new Composite(tabFolder, 0);
		disableApiComp.setLayout(new GridLayout());

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
		propsComp = new Composite(tabFolder, 0);
		propsComp.setLayout(new GridLayout());

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
		this.mediaComp = new Composite(this.tabFolder, 0);
		mediaComp.setLayout(new GridLayout());

		final GridData fill = new GridData();
		fill.horizontalAlignment = 4;
		fill.grabExcessHorizontalSpace = true;
		fill.grabExcessVerticalSpace = true;
		fill.verticalAlignment = 4;

		final GridData fillHor = new GridData();
		fillHor.horizontalAlignment = GridData.FILL;
		fillHor.grabExcessHorizontalSpace = true;
		fillHor.horizontalSpan = 2;

		final GridData fillHor2 = new GridData();
		fillHor2.horizontalAlignment = GridData.FILL;
		fillHor2.grabExcessHorizontalSpace = true;

		final GridData fillHor3 = new GridData();
		fillHor3.horizontalAlignment = GridData.FILL;
		fillHor3.grabExcessHorizontalSpace = true;

		final GridData fillHor4 = new GridData();
		fillHor4.horizontalAlignment = GridData.FILL;
		fillHor4.grabExcessHorizontalSpace = true;
		fillHor4.horizontalSpan = 2;

		final GridData fillHor5 = new GridData();
		fillHor5.horizontalAlignment = GridData.FILL;
		fillHor5.grabExcessHorizontalSpace = true;
		fillHor5.horizontalSpan = 2;

		final GridData fillHor6 = new GridData();
		fillHor6.horizontalAlignment = GridData.FILL;
		fillHor6.grabExcessHorizontalSpace = true;
		fillHor6.horizontalSpan = 2;

		final GridData fillHor7 = new GridData();
		fillHor7.horizontalAlignment = GridData.FILL;
		fillHor7.grabExcessHorizontalSpace = true;
		fillHor7.horizontalSpan = 2;

		Group mediaGroup = new Group(this.mediaComp, 0);
		mediaGroup.setText(UILocale.get("OPTION_TAB_MEDIA", "Media"));
		mediaGroup.setLayout(new GridLayout(2, false));
		mediaGroup.setLayoutData(fill);

		Label label = new Label(mediaGroup, 32);
		label.setText("MIDI Soundfont Path:");
		label.setLayoutData(fillHor7);

		soundfontPathText = new Text(mediaGroup, SWT.BORDER);
		soundfontPathText.setEditable(true);
		soundfontPathText.setEnabled(true);
		soundfontPathText.setLayoutData(fillHor3);
		soundfontPathText.setText(Settings.soundfontPath);

		Button sfPathBtn = new Button(mediaGroup, SWT.PUSH);
		sfPathBtn.setText("...");
		sfPathBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(setsShell, SWT.OPEN);
				fileDialog.setFilterExtensions(new String[] { "*.sf2;*.dls", "*.*" });
				String selection = fileDialog.open();
				if (selection != null) {
					soundfontPathText.setText(selection);
				}
			}
		});

		vmsCheck = new Button(mediaGroup, SWT.CHECK);
		vmsCheck.setText(UILocale.get("OPTION_MEDIA_VMS", "Search for VirtualMIDISynth as MIDI device"));
		vmsCheck.setLayoutData(fillHor);
		vmsCheck.setSelection(Settings.searchVms);

		globalMidiCheck = new Button(mediaGroup, SWT.CHECK);
		globalMidiCheck.setText(UILocale.get("OPTION_MEDIA_GLOBAL_MIDI", "Allow only one MIDI playback at time (reduces lag)"));
		globalMidiCheck.setLayoutData(fillHor5);
		globalMidiCheck.setSelection(Settings.oneMidiAtTime);

		vlcCheck = new Button(mediaGroup, SWT.CHECK);
		vlcCheck.setText("Enable VLC Player");
		vlcCheck.setLayoutData(fillHor4);
		vlcCheck.setSelection(Settings.enableVlc);

		label = new Label(mediaGroup, 32);
		label.setText(String.format(
			"%s (%s):",
			UILocale.get("OPTION_MEDIA_VLC_DIR", "VLC Path"),
			System.getProperty("os.arch").contains("64") ? "64-bit" : "32-bit"
		));
		label.setLayoutData(fillHor7);

		vlcDirText = new Text(mediaGroup, SWT.BORDER);
		vlcDirText.setEditable(true);
		vlcDirText.setEnabled(true);
		vlcDirText.setLayoutData(fillHor2);
		vlcDirText.setText(Settings.vlcDir);

		Button vlcDirBtn = new Button(mediaGroup, SWT.PUSH);
		vlcDirBtn.setText("...");
		vlcDirBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(setsShell, SWT.OPEN);
				fileDialog.setFilterExtensions(new String[] { "vlc;vlc.exe;libvlc.dll;libvlc.so;libvlc.dylib"});
				String selection = fileDialog.open();
				if (selection != null) {
					Path vlcExe = Paths.get(selection);
					Path vlcDir = vlcExe.getParent();
					Path libvlc = vlcDir.resolve("libvlc.dll");
					if (!Files.exists(libvlc)) {
						libvlc = vlcDir.resolve("libvlc.so");
					}
					if (!Files.exists(libvlc)) {
						libvlc = vlcDir.resolve("libvlc.dylib");
					}
					if (!Files.exists(libvlc)) {
						Emulator.getEmulator().getScreen().showMessage("libvlc not found in selected path!");
						return;
					}
					try {
						NativeLibrary lib = NativeLibrary.getInstance(libvlc.toString());
						lib.dispose();
					} catch (Throwable ex) {
						Emulator.getEmulator().getScreen().showMessage(
								"Failed to load libvlc, check if it has same architecture as JVM!",
								CustomMethod.getStackTrace(ex));
						return;
					}

					vlcDirText.setText(vlcDir.toString());
				}
			}
		});

		mediaDumpCheck = new Button(mediaGroup, SWT.CHECK);
		mediaDumpCheck.setText(UILocale.get("OPTION_MEDIA_DUMP", "Enable media exporting (higher memory usage)"));
		mediaDumpCheck.setLayoutData(fillHor6);
		mediaDumpCheck.setSelection(Settings.enableMediaDump);

		setupOttChoice(mediaGroup);

//        reopenMidiCheck = new Button(mediaGroup, SWT.CHECK);
//        reopenMidiCheck.setText(UILocale.get("OPTION_MEDIA_REOPEN_MIDI", "Reopen MIDI device every time"));
//        reopenMidiCheck.setLayoutData(fillHor);
//        reopenMidiCheck.setSelection(Settings.reopenMidiDevice);
	}

	private void setupOttChoice(Group mediaGroup) {
		final GridData compLayoutData = new GridData();
		compLayoutData.horizontalAlignment = GridData.FILL;
		compLayoutData.verticalAlignment = GridData.FILL;
		compLayoutData.grabExcessHorizontalSpace = true;

		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		Composite comp = new Composite(mediaGroup, SWT.NONE);
		comp.setLayout(layout);
		comp.setLayoutData(compLayoutData);

		final GridData labelLayoutData = new GridData();
		labelLayoutData.horizontalAlignment = GridData.FILL;
		labelLayoutData.verticalAlignment = GridData.CENTER;

		CLabel label = new CLabel(comp, SWT.NONE);
		label.setText("Nokia Tone Decoder:");
		label.setLayoutData(labelLayoutData);

		final GridData comboLayoutData = new GridData();
		comboLayoutData.horizontalAlignment = GridData.FILL;
		comboLayoutData.grabExcessHorizontalSpace = true;

		ottCombo = new Combo(comp, 12);
		ottCombo.setLayoutData(comboLayoutData);
		ottCombo.setFont(f);
		String[] items = new String[] {
				"Disabled",
				"KEmulator",
				"FreeJ2ME-Plus",
		};
		ottCombo.setItems(items);
		ottCombo.setText(items[Settings.ottDecoder]);
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

		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = 4;
		
		Group updateComp = new Group(systemComp, SWT.NONE);
		updateComp.setText("Updates");
		updateComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		updateComp.setLayout(new GridLayout(2, false));

		final GridData labelLayoutData2 = new GridData();
		labelLayoutData2.horizontalAlignment = GridData.FILL;
		labelLayoutData2.verticalAlignment = GridData.CENTER;

		CLabel label = new CLabel(updateComp, SWT.NONE);
		label.setText(UILocale.get("OPTION_SYSTEM_UPDATE_BRANCH", "Update branch:"));
		label.setLayoutData(labelLayoutData2);

		final GridData comboLayoutData2 = new GridData();
		comboLayoutData2.horizontalAlignment = GridData.FILL;
		comboLayoutData2.grabExcessHorizontalSpace = true;

		updateBranchCombo = new Combo(updateComp, 12);
		updateBranchCombo.setLayoutData(comboLayoutData2);
		updateBranchCombo.setFont(f);
		updateBranchCombo.add("stable");
		updateBranchCombo.add("dev");

		updateBranchCombo.setText(Settings.updateBranch);

		autoUpdatesBtn = new Button(updateComp, 32);
		autoUpdatesBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		autoUpdatesBtn.setText(UILocale.get("OPTION_SYSTEM_AUTO_UPDATES", "Automatically check for updates"));
		autoUpdatesBtn.setSelection(Settings.autoUpdate == 2);
		
		grpUi = new Group(systemComp, SWT.NONE);
		grpUi.setText("UI");
		grpUi.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpUi.setLayout(new GridLayout(1, false));
		
		genLanguageList();
		
		fpsCounterCheck = new Button(grpUi, SWT.CHECK);
		fpsCounterCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		fpsCounterCheck.setText(UILocale.get("OPTION_SYSTEM_FPS_COUNT", "FPS Counter"));
		fpsCounterCheck.setSelection(Settings.fpsCounter);

		this.vibrationCheck = new Button(grpUi, SWT.CHECK);
		vibrationCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		vibrationCheck.setText("Enable Vibration");
		this.vibrationCheck.setSelection(Settings.enableVibration);
		vibrationCheck.setToolTipText("Shakes the window during vibration");
		
		this.aButton752 = new Button(grpUi, 32);
		aButton752.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		aButton752.setText(UILocale.get("OPTION_SYSTEM_INFO_COLOR", "Info View: Show color in (R,G,B)"));
		this.aButton752.setSelection(Settings.infoColorHex);
		
		grpDebug = new Group(systemComp, SWT.NONE);
		grpDebug.setText("Debug");
		grpDebug.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpDebug.setLayout(new GridLayout(1, false));
		
		this.aButton746 = new Button(grpDebug, 32);
		aButton746.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		aButton746.setText(UILocale.get("OPTION_SYSTEM_XRAY_BG", "X-Ray View: OverLap images"));
		this.aButton746.setSelection(Settings.xrayOverlapScreen);
		
		this.aButton749 = new Button(grpDebug, 32);
		aButton749.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		aButton749.setText(UILocale.get("OPTION_SYSTEM_XRAY_CLIP", "X-Ray View: Show image clipping region"));
		this.aButton749.setSelection(Settings.xrayShowClipBorder);
		
		this.aButton703 = new Button(grpDebug, 32);
		aButton703.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		aButton703.setText(UILocale.get("OPTION_SYSTEM_RELEASED_IMG", "Memory View: Record released images"));
		this.aButton703.setSelection(Settings.recordReleasedImg);
		
		this.aButton714 = new Button(grpDebug, 32);
		aButton714.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		aButton714.setText(UILocale.get("OPTION_SYSTEM_TRACK_NEW", "Track \"new/new[]...\" operations"));
		this.aButton714.setSelection(Settings.enableNewTrack);
		
		this.aButton719 = new Button(grpDebug, 32);
		aButton719.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		aButton719.setText(UILocale.get("OPTION_SYSTEM_TRACK_METHOD", "Track method calls"));
		this.aButton719.setSelection(Settings.enableMethodTrack);

		grpMisc = new Group(systemComp, SWT.NONE);
		grpMisc.setText("Misc");
		grpMisc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpMisc.setLayout(new GridLayout(1, false));
		
		this.antiAliasBtn = new Button(grpMisc, 32);
		antiAliasBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		antiAliasBtn.setText(UILocale.get("OPTION_SYSTEM_AWT_ANTIALIASING", "AWT Smooth drawing"));
		this.antiAliasBtn.setSelection(Settings.awtAntiAliasing);
		
		this.rpcBtn = new Button(grpMisc, 32);
		rpcBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		rpcBtn.setText(UILocale.get("OPTION_SYSTEM_DISCORD_RICHPRESENCE", "Discord Rich Presence"));
		this.rpcBtn.setSelection(Settings.rpc);
	}

	private void setupSysFontComp() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalSpan = 2;
		layoutData.horizontalAlignment = 4;
		layoutData.verticalAlignment = 2;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.grabExcessHorizontalSpace = true;
		final GridData layoutData2 = new GridData();
		(layoutData2).horizontalAlignment = 4;
		layoutData2.verticalAlignment = 2;
		final GridData layoutData5 = new GridData();
		(layoutData5).horizontalAlignment = 4;
		layoutData5.verticalAlignment = 2;
		final GridData layoutData3 = new GridData();
		(layoutData3).horizontalAlignment = 1;
		layoutData3.verticalAlignment = 2;
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		this.fontScrollComp = new ScrolledComposite(this.tabFolder, SWT.V_SCROLL);
		this.fontComp = new Composite(fontScrollComp, SWT.NONE);
		fontComp.setLayout(layout);
		CLabel aCLabel638 = new CLabel(this.fontComp, 0);
		(aCLabel638).setText(UILocale.get("OPTION_FONT_DEFAULT_FONT", "Default Font:"));
		aCLabel638.setLayoutData(layoutData2);
		this.setupDefaultFontChoice();

		CLabel aCLabel = new CLabel(this.fontComp, 0);
		(aCLabel).setText("Monospace Font:");
		aCLabel.setLayoutData(layoutData5);
		this.setupMonoFontChoice();
		
		lastChangedFont = 0;

		Group fontPreviewGroup = new Group(this.fontComp, SWT.NONE);
		fontPreviewGroup.setLayout(new GridLayout(3, false));
		fontPreviewGroup.setText("Preview");
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		fontPreviewGroup.setLayoutData(gd);

		CLabel aCLabel640 = new CLabel(fontPreviewGroup, 0);
		(aCLabel640).setText(UILocale.get("OPTION_FONT_LARGE_SIZE", "Large Size:"));
		aCLabel640.setLayoutData(layoutData3);
		this.aSpinner670 = new Spinner(fontPreviewGroup, 2048);
		aSpinner670.setMinimum(1);
		this.aSpinner670.setSelection(this.fontLargeSize);
		this.aSpinner670.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent modifyEvent) {
				method360(1);
			}
		});
		this.method420(fontPreviewGroup);
		CLabel aCLabel642 = new CLabel(fontPreviewGroup, 0);
		(aCLabel642).setText(UILocale.get("OPTION_FONT_MIDDLE_SIZE", "Medium Size:"));
		this.aSpinner679 = new Spinner(fontPreviewGroup, 2048);
		aSpinner679.setMinimum(1);
		this.aSpinner679.setSelection(this.fontMediumSize);
		this.aSpinner679.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent modifyEvent) {
				method360(2);
			}
		});
		this.method422(fontPreviewGroup);
		CLabel aCLabel644 = new CLabel(fontPreviewGroup, 0);
		(aCLabel644).setText(UILocale.get("OPTION_FONT_SMALL_SIZE", "Small Size:"));
		this.aSpinner690 = new Spinner(fontPreviewGroup, 2048);
		aSpinner690.setMinimum(1);
		this.aSpinner690.setSelection(this.fontSmallSize);
		this.aSpinner690.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent modifyEvent) {
				method360(4);
			}
		});
		this.method424(fontPreviewGroup);
		CLabel aCLabel645 = new CLabel(fontPreviewGroup, 0);
		(aCLabel645).setText(UILocale.get("OPTION_FONT_TEST_TEXT", "Test Text:"));
		this.aText633 = new Text(fontPreviewGroup, 2048);
		aText633.setText(UILocale.get("OPTION_FONT_TEST_TEXT_TXT", "This is an Example."));
		this.aText633.setLayoutData(layoutData);
		this.aText633.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				method360(7);
			}
		});


		final GridData layoutData4 = new GridData();
		(layoutData4).horizontalSpan = 3;
		antiAliasTextBtn = new Button(fontComp, 32);
		antiAliasTextBtn.setText(UILocale.get("OPTION_SYSTEM_TEXT_ANTIALIASING", "Text Antialiasing"));
		antiAliasTextBtn.setSelection(Settings.textAntiAliasing);
		antiAliasTextBtn.setLayoutData(layoutData4);

		fontScrollComp.setExpandHorizontal(true);
		fontScrollComp.setContent(fontComp);
	}

	private void setupDefaultFontChoice() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalSpan = 2;
		layoutData.verticalAlignment = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = 4;
		this.defaultFontCombo = new Combo(this.fontComp, 8);
		defaultFontCombo.setLayoutData(layoutData);
		this.defaultFontCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				lastChangedFont = 0;
				method360(7);
			}
		});
		final FontData[] fontList = Property.display.getFontList(null, true);
		final ArrayList<Comparable> list = new ArrayList<Comparable>();
		list.add("Nokia");
		list.add("Series 60");
		for (FontData fontData : fontList) {
			if (!list.contains(fontData.getName()) && !fontData.getName().startsWith("@")) {
				list.add(fontData.getName());
			}
		}
		final FontData[] fontList2 = Property.display.getFontList(null, false);
		for (FontData fontData : fontList2) {
			if (!list.contains(fontData.getName()) && !fontData.getName().startsWith("@")) {
				list.add(fontData.getName());
			}
		}
		Collections.sort(list);
		String aString682 = (String) list.get(0);
		for (Comparable comparable : list) {
			this.defaultFontCombo.add((String) comparable);
			if (this.defaultFont.equalsIgnoreCase((String) comparable)) {
				aString682 = (String) comparable;
			}
		}
		this.defaultFont = aString682;
		this.defaultFontCombo.setText(this.defaultFont);
	}

	private void setupMonoFontChoice() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalSpan = 2;
		layoutData.verticalAlignment = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = 4;
		this.monoFontCombo = new Combo(this.fontComp, 8);
		monoFontCombo.setLayoutData(layoutData);
		this.monoFontCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				lastChangedFont = 1;
				method360(7);
			}
		});
		final FontData[] fontList = Property.display.getFontList(null, true);
		final ArrayList<Comparable> list = new ArrayList<Comparable>();
		for (FontData fontData : fontList) {
			if (!list.contains(fontData.getName()) && !fontData.getName().startsWith("@")) {
				list.add(fontData.getName());
			}
		}
		final FontData[] fontList2 = Property.display.getFontList(null, false);
		for (FontData fontData : fontList2) {
			if (!list.contains(fontData.getName()) && !fontData.getName().startsWith("@")) {
				list.add(fontData.getName());
			}
		}
		Collections.sort(list);
		String aString682 = (String) list.get(0);
		for (Comparable comparable : list) {
			this.monoFontCombo.add((String) comparable);
			if (this.monospaceFont.equalsIgnoreCase((String) comparable)) {
				aString682 = (String) comparable;
			}
		}
		this.monospaceFont = aString682;
		this.monoFontCombo.setText(this.monospaceFont);
	}

	private void method420(Composite parent) {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		this.aCanvas663 = new Canvas(parent, 264192);
		aCanvas663.setLayoutData(layoutData);
		this.aCanvas663.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent paintEvent) {
				if (anIImage671 == null) {
					method360(1);
				}
				if (Settings.g2d == 0) {
					((ImageSWT) anIImage671).method12(paintEvent.gc, 0, 0);
					return;
				}
				if (Settings.g2d == 1) {
					((ImageAWT) anIImage671).copyToScreen(paintEvent.gc);
				}
			}
		});
	}

	private void method422(Composite parent) {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		this.aCanvas680 = new Canvas(parent, 264192);
		aCanvas680.setLayoutData(layoutData);
		this.aCanvas680.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent paintEvent) {
				if (anIImage674 == null) {
					method360(2);
				}
				anIImage674.copyToScreen(paintEvent.gc);
			}
		});
	}

	private void method424(Composite parent) {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = 2;
		this.aCanvas691 = new Canvas(parent, 264192);
		aCanvas691.setLayoutData(layoutData);
		this.aCanvas691.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent paintEvent) {
				if (anIImage693 == null) {
					method360(4);
				}
				if (Settings.g2d == 0) {
					((ImageSWT) anIImage693).copyToScreen(paintEvent.gc);
					return;
				}
				if (Settings.g2d == 1) {
					((ImageAWT) anIImage693).copyToScreen(paintEvent.gc);
				}
			}
		});
	}

	private void method360(final int n) {
		if (!this.setsShell.isVisible()) {
			return;
		}
		String fontName = lastChangedFont == 1 ? monoFontCombo.getText() : defaultFontCombo.getText();
		IFont font = null;
		if ((n & 0x1) != 0x0) {
			Label_0080:
			{
				IFont font2;
				if (Settings.g2d == 0) {
					font2 = new FontSWT(fontName, this.aSpinner670.getSelection(), 0);
				} else {
					if (Settings.g2d != 1) {
						break Label_0080;
					}
					font2 = new FontAWT(fontName, this.aSpinner670.getSelection(), 0, false);
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
					font3 = new FontSWT(fontName, this.aSpinner679.getSelection(), 0);
				} else {
					if (Settings.g2d != 1) {
						break Label_0242;
					}
					font3 = new FontAWT(fontName, this.aSpinner679.getSelection(), 0, false);
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
					font4 = new FontSWT(fontName, this.aSpinner690.getSelection(), 0);
				} else {
					if (Settings.g2d != 1) {
						break Label_0404;
					}
					font4 = new FontAWT(fontName, this.aSpinner690.getSelection(), 0, false);
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
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = 2;
		layoutData.verticalAlignment = 2;
		final GridData layoutData2 = new GridData();
		(layoutData2).horizontalSpan = 3;
		layoutData2.verticalAlignment = 2;
		layoutData2.horizontalAlignment = 4;
		final GridData layoutData3 = new GridData();
		(layoutData3).horizontalSpan = 3;
		layoutData3.verticalAlignment = 4;
		layoutData3.grabExcessHorizontalSpace = true;
		layoutData3.grabExcessVerticalSpace = true;
		layoutData3.heightHint = 160;
		layoutData3.horizontalAlignment = 4;
		final GridData layoutData4 = new GridData();
		(layoutData4).horizontalAlignment = 4;
		layoutData4.grabExcessHorizontalSpace = true;
		layoutData4.verticalAlignment = 2;
		final GridData layoutData5 = new GridData();
		(layoutData5).horizontalAlignment = 4;
		layoutData5.verticalAlignment = 2;
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		this.recordsComp = new Composite(this.tabFolder, 0);
		recordsComp.setLayout(layout);
		CLabel aCLabel683 = new CLabel(this.recordsComp, 0);
		(aCLabel683).setText(UILocale.get("OPTION_RECORDS_RMS_FOLDER", "RMS Folder:"));
		aCLabel683.setLayoutData(layoutData5);
		this.aText662 = new Text(this.recordsComp, 2048);
		aText662.setEditable(true);
		this.aText662.setEnabled(true);
		this.aText662.setLayoutData(layoutData4);
		this.aText662.setText(this.rmsFolder);
		Button aButton666;
		aButton666 = new Button(this.recordsComp, 8388616);
		aButton666.setText("...");
		aButton666.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				Emulator.getEmulator().getScreen();
				EmulatorScreen.pauseStep();
				final DirectoryDialog directoryDialog;
				(directoryDialog = new DirectoryDialog(setsShell)).setText(UILocale.get("OPTION_RECORDS_OPEN_DIRECTORY", "Select a directory for rms"));
				directoryDialog.setMessage(UILocale.get("OPTION_RECORDS_CHOOSE_DIR", "Choose a directory"));
				directoryDialog.setFilterPath(System.getProperty("user.dir"));
				final String open;
				if ((open = directoryDialog.open()) != null) {
					aText662.setText(open);
					method428();
				}
				((EmulatorScreen) Emulator.getEmulator().getScreen()).resumeStep();
			}
		});

		Button clearRecordsBtn;
		if (Emulator.midletClassName != null) {
			CLabel aCLabel647 = new CLabel(this.recordsComp, 0);
			(aCLabel647).setText(UILocale.get("OPTION_RECORDS_RMS_TEXT", "All Records in current midlet:"));
			aCLabel647.setLayoutData(layoutData2);
			this.aTable665 = new Table(this.recordsComp, 2080);
			aTable665.setHeaderVisible(false);
			this.aTable665.setLayoutData(layoutData3);
			this.aTable665.setLinesVisible(true);
			Button aButton761 = new Button(this.recordsComp, 8388608);
			(aButton761).setText(UILocale.get("OPTION_RECORDS_SELECT_ALL", "Select All"));
			aButton761.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent selectionEvent) {
					for (int i = 0; i < aTable665.getItemCount(); ++i) {
						aTable665.getItem(i).setChecked(true);
					}
				}
			});
			clearRecordsBtn = new Button(this.recordsComp, 8388608);
			clearRecordsBtn.setText(UILocale.get("OPTION_RECORDS_CLEAR_RECORD", "Clear Selected Records"));
			clearRecordsBtn.setLayoutData(layoutData);
			clearRecordsBtn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent selectionEvent) {
					// TODO: rms delete
					final String string = method374() + ".";
					for (int i = 0; i < aTable665.getItemCount(); ++i) {
						final File file;
						if (aTable665.getItem(i).getChecked()) {
							try {
								RecordStore.deleteRecordStore(aTable665.getItem(i).getText().trim());
							} catch (Exception ignored) {}
						}
					}
					method428();
				}
			});
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


			clearRecordsBtn = new Button(this.recordsComp, 8388608);
			clearRecordsBtn.setText(UILocale.get("OPTION_RECORDS_CLEAR_RECORD", "Clear Selected Records"));
			clearRecordsBtn.setLayoutData(layoutData);
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
	}

	private void setupNetworkComp() {
		this.networkComp = new Composite(this.tabFolder, 0);
		networkComp.setLayout(new GridLayout());
		this.setupNetworkTabContent();
	}

	private void setupNetworkTabContent() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalSpan = 2;
		layoutData.verticalAlignment = 2;
		layoutData.horizontalAlignment = 2;
		final GridData layoutData2 = new GridData();
		(layoutData2).horizontalAlignment = 4;
		layoutData2.grabExcessHorizontalSpace = true;
		layoutData2.verticalAlignment = 2;
		final GridData layoutData3 = new GridData();
		(layoutData3).horizontalAlignment = 3;
		layoutData3.verticalAlignment = 2;
		final GridData layoutData4 = new GridData();
		(layoutData4).horizontalAlignment = 4;
		layoutData4.grabExcessHorizontalSpace = true;
		layoutData4.verticalAlignment = 2;
		final GridData layoutData5 = new GridData();
		(layoutData5).horizontalAlignment = 3;
		layoutData5.verticalAlignment = 2;
		final GridData layoutData6 = new GridData();
		(layoutData6).horizontalAlignment = 3;
		layoutData6.verticalAlignment = 2;
		final GridData gridData = new GridData();
		(gridData).horizontalAlignment = 4;
		gridData.widthHint = 40;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
		final GridData gridData3 = new GridData();
		(gridData3).horizontalAlignment = 4;
		gridData3.widthHint = 40;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.verticalAlignment = 2;
		final GridData layoutData7 = new GridData();
		(layoutData7).horizontalAlignment = 4;
		layoutData7.widthHint = 50;
		layoutData7.grabExcessHorizontalSpace = true;
		layoutData7.verticalAlignment = 2;
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		final GridData layoutData8 = new GridData();
		(layoutData8).horizontalAlignment = 4;
		layoutData8.grabExcessHorizontalSpace = true;
//		layoutData8.grabExcessVerticalSpace = true;
		layoutData8.verticalAlignment = 1;
		this.networkProxyGroup = new Group(this.networkComp, 0);
		networkProxyGroup.setText(UILocale.get("OPTION_NETWORK_PROXY", "Proxy"));
		this.networkProxyGroup.setLayout(layout);
		this.networkProxyGroup.setLayoutData(layoutData8);
		CLabel aCLabel648;
		aCLabel648 = new CLabel(this.networkProxyGroup, 0);
		aCLabel648.setText(UILocale.get("OPTION_NETWORK_PROXY_TYPE", "ProxyType:"));
		this.setupProxyCombo();
		CLabel aCLabel651;
		aCLabel651 = new CLabel(this.networkProxyGroup, 0);
		aCLabel651.setText("");
		CLabel aCLabel649;
		aCLabel649 = new CLabel(this.networkProxyGroup, 0);
		aCLabel649.setText(UILocale.get("OPTION_NETWORK_HOST", "Host:"));
		aCLabel649.setLayoutData(layoutData6);
		this.aText635 = new Text(this.networkProxyGroup, 2048);
		aText635.setLayoutData(layoutData7);
		this.aText635.setText(Settings.proxyHost);
		CLabel aCLabel650;
		aCLabel650 = new CLabel(this.networkProxyGroup, 0);
		aCLabel650.setText(UILocale.get("OPTION_NETWORK_PORT", "Port:"));
		this.aText637 = new Text(this.networkProxyGroup, 2048);
		aText637.setText(Settings.proxyPort);
		this.aText637.setLayoutData(gridData);
		CLabel aCLabel652;
		aCLabel652 = new CLabel(this.networkProxyGroup, 0);
		aCLabel652.setText(UILocale.get("OPTION_NETWORK_USERNAME", "Username:"));
		aCLabel652.setLayoutData(layoutData5);
		this.aText639 = new Text(this.networkProxyGroup, 2048);
		aText639.setLayoutData(layoutData4);
		this.aText639.setText(Settings.proxyUser);
		CLabel aCLabel653;
		aCLabel653 = new CLabel(this.networkProxyGroup, 0);
		aCLabel653.setText(UILocale.get("OPTION_NETWORK_PASSWORD", "Password:"));
		this.aText641 = new Text(this.networkProxyGroup, 4196352);
		aText641.setText(Settings.proxyPass);
		this.aText641.setLayoutData(gridData3);
		CLabel aCLabel654;
		aCLabel654 = new CLabel(this.networkProxyGroup, 0);
		aCLabel654.setText(UILocale.get("OPTION_NETWORK_DOMAIN", "Domain:"));
		aCLabel654.setLayoutData(layoutData3);
		this.aText643 = new Text(this.networkProxyGroup, 2048);
		aText643.setLayoutData(layoutData2);
		this.aText643.setText(Settings.proxyDomain);
		this.aButton764 = new Button(this.networkProxyGroup, 8388608);
		aButton764.setText(UILocale.get("OPTION_NETWORK_CONNECT", "Connect"));
		this.aButton764.setLayoutData(layoutData);
		this.aButton764.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				method352();
			}
		});
		this.proxyTypeCombo.addModifyListener(new Class65(this));
		this.proxyTypeCombo.select(Settings.proxyType);
		new Label(networkProxyGroup, SWT.NONE);
		new Label(networkProxyGroup, SWT.NONE);
		new Label(networkProxyGroup, SWT.NONE);
		new Label(networkProxyGroup, SWT.NONE);

		final GridData gridData2 = new GridData();
		(gridData2).horizontalAlignment = 4;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.BEGINNING;
		
		this.noNetworkBtn = new Button(this.networkComp, SWT.CHECK);
		noNetworkBtn.setText(UILocale.get("OPTION_COREAPI_NO_NETWORK", "Restrict network connections"));
		this.noNetworkBtn.setLayoutData(gridData2);
		this.noNetworkBtn.setSelection(Settings.networkNotAvailable);
	}

	private void setupProxyCombo() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = 4;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalSpan = 2;
		layoutData.verticalAlignment = 2;
		this.proxyTypeCombo = new Combo(this.networkProxyGroup, 8);
		proxyTypeCombo.setLayoutData(layoutData);
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

	Shell getShell() {
		return setsShell;
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
