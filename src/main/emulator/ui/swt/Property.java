package emulator.ui.swt;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import javax.microedition.rms.RecordStore;

import java.io.*;
import java.lang.reflect.Field;
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
			{"Bluetooth (JSR 82)", "javax.bluetooth"},
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
			{"Send Datagram", "connector.open.datagram"},
			{"Receive Datagram", "connector.open.datagramreceive"},
			{"SSL Socket", "connector.open.ssl"},
			{"Bluetooth", "connector.open.bt"},
			{"Camera", "media.capture.video"},
			{"Microphone", "media.capture.audio"},
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
	private Shell settingsShell;
	private Text rmsFolderText;
	private CTabFolder tabFolder;
	private Text leftSoftKeyBinding;
	private Text rightSoftKeyBinding;
	private Text leftBinding;
	private Text rightBinding;
	private Text upBinding;
	private Text downBinding;
	private Text fireBinding;
	private Text num1Binding;
	private Text num2Binding;
	private Text num3Binding;
	private Text num4Binding;
	private Text num5Binding;
	private Text num6Binding;
	private Text num7Binding;
	private Text num8Binding;
	private Text num9Binding;
	private Text num0Binding;
	private Text starBinding;
	private Text poundBinding;
	private Button keyCacheCheck;
	private Button recordReleasedImagesCheck;
	private Button trackAllocationsCheck;
	private Button trackMethodCallsCheck;
	private Composite systemComp;
	private ScrolledComposite systemScrollComp;
	private Button vibrationCheck;
	private Button noNetworkBtn;
	private Button xrayOverlapCheck;
	private Button xrayClipCheck;
	private Button infoColorHexCheck;
	private Composite fontComp;
	private ScrolledComposite fontScrollComp;
	private Combo defaultFontCombo;
	private Combo monoFontCombo;
	private Spinner largeFontSpinner;
	private Canvas largeFontCanvas;
	private Spinner mediumFontSpinner;
	private Canvas mediumFontCanvas;
	private Spinner smallFontSpinner;
	private Canvas smallFontCanvas;
	private Text fontPreviewText;
	private String defaultFont;
	private String monospaceFont;
	private String rmsFolder;
	private int fontSmallSize;
	private int fontMediumSize;
	private int fontLargeSize;
	Combo controllerCombo;
	private static String[] keyboardBindings;
	private IImage largeFontPreview;
	private IImage mediumFontPreview;
	private IImage smallFontPreview;
	private Composite recordsComp;
	private Table legacyRecordsTable;
	private Composite networkComp;
	private Group networkProxyGroup;
	private Combo proxyTypeCombo;
	private Text proxyHostText;
	private Text proxyPortText;
	private Text proxyUserText;
	private Text proxyPasswordText;
	private Text proxyDomainText;
	private Button proxyConnectButton;
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
	private Button storeCreatedImagesBtn;

	private int lastChangedFont;
	private Button showAppSettingsBtn;
	private Button maMidiCheck;
	private Button passMinimizeBtn;
	private Button countImagesSize;
	private Button traceHttpConnectionCheck;

	private ScrolledComposite hotkeysComp;
	private Composite hotkeysContent;
	private Button hotkeysResetBtn;

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
		this.createSettingsShell(parent);
		this.settingsShell.pack();
		this.settingsShell.setSize(420, this.settingsShell.getSize().y);
		systemScrollComp.setMinHeight(systemComp.computeSize(-1, -1).y);
		
		systemScrollComp.setExpandVertical(true);
		fontScrollComp.setMinHeight(fontComp.computeSize(-1, -1).y);
		fontScrollComp.setExpandVertical(true);
		securityComp.setMinHeight(securityContent.computeSize(-1, -1).y);
		securityComp.setExpandVertical(true);
		hotkeysComp.setMinHeight(hotkeysContent.computeSize(-1, -1).y);
		hotkeysComp.setExpandVertical(true);

		this.settingsShell.setLocation(parent.getLocation().x + (parent.getSize().x - this.settingsShell.getSize().x >> 1), parent.getLocation().y + (parent.getSize().y - this.settingsShell.getSize().y >> 1));
		this.settingsShell.open();
		while (!this.settingsShell.isDisposed()) {
			if (!Property.display.readAndDispatch()) {
				Property.display.sleep();
			}
		}
		this.largeFontPreview = null;
		this.mediumFontPreview = null;
		this.smallFontPreview = null;
	}

	public String getDefaultFontName() {
		return this.defaultFont;
	}

	public String getMonospaceFontName() {
		return monospaceFont;
	}

	public void setDefaultFontName(final String fontName) {
		this.defaultFont = fontName;
	}

	public String getRmsFolderPath() {
		String s = resolveUserRelativePath(this.rmsFolder);
		if (s.startsWith("file://")) s = s.substring(7);
		final File file = new File(s);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
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
			this.rmsFolder = properties.getProperty("RMSFolder", "./rms");
			if (rmsFolder.equals("/rms")) rmsFolder = "./rms";
			this.fontSmallSize = Integer.parseInt(properties.getProperty("FontSmallSize", String.valueOf(12)));
			this.fontMediumSize = Integer.parseInt(properties.getProperty("FontMediumSize", String.valueOf(14)));
			this.fontLargeSize = Integer.parseInt(properties.getProperty("FontLargeSize", String.valueOf(16)));
//			Settings.g2d = (properties.getProperty("2D_Graphics_Engine", "AWT").equalsIgnoreCase("SWT") ? 0 : 1);
			Settings.g3d = (properties.getProperty("3D_Graphics_Engine", "LWJ").equalsIgnoreCase("SWERVE") ? 0 : 1);
			{
				String s = properties.getProperty("Micro3D_Engine", Emulator.isX64() ? "GL" : "DLL");
				Settings.micro3d = s.equalsIgnoreCase("DLL") ? 0 : s.equalsIgnoreCase("SW") ? 2 : 1;
			}

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
				} else if (((String) k).startsWith("Accelerators.")) {
					Accelerators.set(((String) k).substring("Accelerators.".length()), properties.getProperty((String) k));
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
			Settings.hideDisplayableOnMinimize = Boolean.parseBoolean(properties.getProperty("HideDisplayableOnMinimize", "false"));

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
			Settings.traceNew = Boolean.parseBoolean(properties.getProperty("EnableNewTrack", "false"));
			Settings.traceCalls = Boolean.parseBoolean(properties.getProperty("EnableMethodTrack", "false"));
			Settings.traceHttp = Boolean.parseBoolean(properties.getProperty("TraceHttp", "false"));
			Settings.enableTrace = Boolean.parseBoolean(properties.getProperty("ShowMethodTrack", "false"));
			Settings.updateBranch = properties.getProperty("UpdateBranch", Emulator.debugBuild ? "dev" : "stable");
			Settings.autoUpdate = Integer.parseInt(properties.getProperty("AutoUpdate", "0"));
			Settings.altLessSpeedShortcuts = Boolean.parseBoolean(properties.getProperty("AltLessSpeedShortcuts", "false"));
			Settings.showAppSettingsOnStart = Boolean.parseBoolean(properties.getProperty("ShowAppSettingsOnStart", "true"));
			Settings.globalSettings = Boolean.parseBoolean(properties.getProperty("GlobalSettings", "false"));
			Settings.storeCreatedImages = Boolean.parseBoolean(properties.getProperty("StoreCreatedImages", "false"));
			Settings.countImagesInObjectsSize = Boolean.parseBoolean(properties.getProperty("CountImagesInObjectsSize", "false"));

			Settings.bypassVserv = Boolean.parseBoolean(properties.getProperty("BypassVserv", "true"));
			Settings.wavCache = Boolean.parseBoolean(properties.getProperty("WavCache", "true"));

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
			Settings.maMidi = Boolean.parseBoolean(properties.getProperty("UseMaSmwForMIDI", "false"));

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
			this.rmsFolder = "./rms";
			EmulatorScreen.locX = Integer.MIN_VALUE;
			EmulatorScreen.locY = Integer.MIN_VALUE;
			Settings.autoGenJad = false;
			Settings.traceNew = false;
			Settings.traceCalls = false;
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
			} else if (list.contains("SF Mono")) {
				res = "SF Mono";
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
			properties.setProperty("2D_Graphics_Engine", /*(Settings.g2d == 0) ? "SWT" : */"AWT");
			properties.setProperty("3D_Graphics_Engine", (Settings.g3d == 0) ? "SWERVE" : "LWJ");
			properties.setProperty("Micro3D_Engine", Settings.micro3d == 0 ? "DLL" : Settings.micro3d == 2 ? "SW" : "GL");

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
			properties.setProperty("HideDisplayableOnMinimize", String.valueOf(Settings.hideDisplayableOnMinimize));

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
			properties.setProperty("EnableNewTrack", String.valueOf(Settings.traceNew));
			properties.setProperty("EnableMethodTrack", String.valueOf(Settings.traceCalls));
			properties.setProperty("TraceHttp", String.valueOf(Settings.traceHttp));
			properties.setProperty("ShowMethodTrack", String.valueOf(Settings.enableTrace));
			properties.setProperty("UpdateBranch", Settings.updateBranch);
			properties.setProperty("AutoUpdate", String.valueOf(Settings.autoUpdate));
			if (properties.getProperty("AltLessSpeedShortcuts") == null) //overwrite only if not in the file, can be changed only externaly
				properties.setProperty("AltLessSpeedShortcuts",String.valueOf(Settings.altLessSpeedShortcuts));
			properties.setProperty("ShowAppSettingsOnStart", String.valueOf(Settings.showAppSettingsOnStart));
			properties.setProperty("GlobalSettings", String.valueOf(Settings.globalSettings));
			properties.setProperty("StoreCreatedImages", String.valueOf(Settings.storeCreatedImages));
			properties.setProperty("CountImagesInObjectsSize", String.valueOf(Settings.countImagesInObjectsSize));

			properties.setProperty("BypassVserv", String.valueOf(Settings.bypassVserv));
			properties.setProperty("WavCache", String.valueOf(Settings.wavCache));

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
			properties.setProperty("UseMaSmwForMIDI", String.valueOf(Settings.maMidi));

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

			for (String k: Accelerators.accelerators.keySet()) {
				properties.setProperty("Accelerators." + k, String.valueOf(Accelerators.accelerators.get(k)));
			}

			properties.store(fileOutputStream, "KEmulator properties");
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void apply() {
		this.defaultFont = this.defaultFontCombo.getText().trim();
		this.monospaceFont = this.monoFontCombo.getText().trim();
		this.rmsFolder = this.rmsFolderText.getText().trim();
		this.fontSmallSize = this.smallFontSpinner.getSelection();
		this.fontMediumSize = this.mediumFontSpinner.getSelection();
		this.fontLargeSize = this.largeFontSpinner.getSelection();
		KeyMapping.mapDeviceKey(17, KeyMapping.method601(Property.keyboardBindings[17]));
		KeyMapping.mapDeviceKey(18, KeyMapping.method601(Property.keyboardBindings[18]));
		KeyMapping.mapDeviceKey(14, KeyMapping.method601(Property.keyboardBindings[14]));
		KeyMapping.mapDeviceKey(15, KeyMapping.method601(Property.keyboardBindings[15]));
		KeyMapping.mapDeviceKey(12, KeyMapping.method601(Property.keyboardBindings[12]));
		KeyMapping.mapDeviceKey(13, KeyMapping.method601(Property.keyboardBindings[13]));
		KeyMapping.mapDeviceKey(16, KeyMapping.method601(Property.keyboardBindings[16]));
		KeyMapping.mapDeviceKey(10, KeyMapping.method601(Property.keyboardBindings[10]));
		KeyMapping.mapDeviceKey(11, KeyMapping.method601(Property.keyboardBindings[11]));
		KeyMapping.mapDeviceKey(1, KeyMapping.method601(Property.keyboardBindings[1]));
		KeyMapping.mapDeviceKey(2, KeyMapping.method601(Property.keyboardBindings[2]));
		KeyMapping.mapDeviceKey(3, KeyMapping.method601(Property.keyboardBindings[3]));
		KeyMapping.mapDeviceKey(4, KeyMapping.method601(Property.keyboardBindings[4]));
		KeyMapping.mapDeviceKey(5, KeyMapping.method601(Property.keyboardBindings[5]));
		KeyMapping.mapDeviceKey(6, KeyMapping.method601(Property.keyboardBindings[6]));
		KeyMapping.mapDeviceKey(7, KeyMapping.method601(Property.keyboardBindings[7]));
		KeyMapping.mapDeviceKey(8, KeyMapping.method601(Property.keyboardBindings[8]));
		KeyMapping.mapDeviceKey(9, KeyMapping.method601(Property.keyboardBindings[9]));
		KeyMapping.mapDeviceKey(0, KeyMapping.method601(Property.keyboardBindings[0]));
		KeyMapping.init();
		if (Settings.enableKeyCache != this.keyCacheCheck.getSelection()) {
			KeyMapping.keyCacheStack.clear();
			Settings.enableKeyCache = this.keyCacheCheck.getSelection();
		}
		if (Settings.enableVibration != this.vibrationCheck.getSelection() && !(Settings.enableVibration = this.vibrationCheck.getSelection())) {
			Emulator.getEmulator().getScreen().stopVibra();
		}
		Settings.networkNotAvailable = this.noNetworkBtn.getSelection();
		Settings.xrayOverlapScreen = this.xrayOverlapCheck.getSelection();
		Settings.xrayShowClipBorder = this.xrayClipCheck.getSelection();
		Settings.infoColorHex = this.infoColorHexCheck.getSelection();
		Settings.recordReleasedImg = this.recordReleasedImagesCheck.getSelection();
		Settings.traceNew = this.trackAllocationsCheck.getSelection();
		Settings.traceCalls = this.trackMethodCallsCheck.getSelection();
		Settings.traceHttp = this.traceHttpConnectionCheck.getSelection();
		Settings.proxyType = this.proxyTypeCombo.getSelectionIndex();
		Settings.proxyHost = this.proxyHostText.getText().trim();
		Settings.proxyPort = this.proxyPortText.getText().trim();
		Settings.proxyUser = this.proxyUserText.getText().trim();
		Settings.proxyPass = this.proxyPasswordText.getText();
		Settings.proxyDomain = this.proxyDomainText.getText().trim();
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

		Settings.storeCreatedImages = storeCreatedImagesBtn.getSelection();
		Settings.showAppSettingsOnStart = showAppSettingsBtn.getSelection();
		Settings.countImagesInObjectsSize = countImagesSize.getSelection();

		if (maMidiCheck != null) Settings.maMidi = maMidiCheck.getSelection();

		this.updateProxy();
	}

	private void createSettingsShell(final Shell shell) {
		this.settingsShell = new Shell(shell, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		settingsShell.setText(UILocale.get("OPTION_FRAME_TITLE", "Options & Properties"));
		this.settingsShell.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/res/icon")));
		try {
			// windows dpi fix
			if (f == null) {
				FontData fd = shell.getFont().getFontData()[0];

				Field heightField = FontData.class.getField("height");
				heightField.setAccessible(true);
				int height = heightField.getInt(fd);

				Field dataField = FontData.class.getField("data");
				dataField.setAccessible(true);
				Object data = dataField.get(fd);
				Field lfHeightField = data.getClass().getField("lfHeight");
				lfHeightField.setAccessible(true);
				int lfHeight = lfHeightField.getInt(data);

				heightField.set(fd, (height / -lfHeight) * 12);

				f = new Font(shell.getDisplay(), fd);
			}
			shell.setFont(f);
		} catch (Throwable ignored) {}
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		this.settingsShell.setLayout(layout);
		this.createKeyMappingControls();
		this.createDialogButtons();
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
		
		languageCombo = new Combo(langComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
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

	private void createDialogButtons() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = GridData.FILL;
		layoutData.horizontalSpan = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = GridData.CENTER;
		Composite dialogButtons = new Composite(this.settingsShell, SWT.NONE);
		dialogButtons.setLayout(new GridLayout(2, true));
		(dialogButtons).setLayoutData(layoutData);
		Button okButton = new Button(dialogButtons, SWT.FLAT | SWT.PUSH);
		okButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		okButton.setSelection(false);
		okButton.setText(UILocale.get("DIALOG_OK", "OK"));
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				apply();
				saveProperties();
				settingsShell.dispose();
			}
		});
		Button cancelButton = new Button(dialogButtons, SWT.FLAT | SWT.PUSH);
		cancelButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cancelButton.setText(UILocale.get("DIALOG_CANCEL", "Cancel"));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				settingsShell.dispose();
			}
		});
	}

	private void createKeyMappingControls() {
		final GridData layoutData = new GridData();
		layoutData.grabExcessVerticalSpace = true;
		(layoutData).horizontalAlignment = GridData.FILL;
		layoutData.horizontalSpan = 2;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = GridData.CENTER;
		this.tabFolder = new CTabFolder(this.settingsShell, SWT.BORDER | SWT.FLAT);
//		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(22));
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
		setupHotkeysComp();

		final CTabItem keymapTab = new CTabItem(this.tabFolder, SWT.NONE);
		keymapTab.setText(UILocale.get("OPTION_TAB_KEYMAP", "KeyMap"));
		keymapTab.setControl(this.keyMapTabComp);

		final CTabItem sysFontTab = new CTabItem(this.tabFolder, SWT.NONE);
		sysFontTab.setText(UILocale.get("OPTION_TAB_FONT", "Font"));
		sysFontTab.setControl(this.fontScrollComp);

		final CTabItem systemTab = new CTabItem(this.tabFolder, SWT.NONE);
		systemTab.setText(UILocale.get("OPTION_TAB_SYSTEM", "System"));
		systemTab.setControl(this.systemScrollComp);

		final CTabItem disableApiTab = new CTabItem(this.tabFolder, SWT.NONE);
		disableApiTab.setText(UILocale.get("OPTION_TAB_DISABLE_API", "Disable APIs"));
		disableApiTab.setControl(this.disableApiComp);

		final CTabItem rmsTab = new CTabItem(this.tabFolder, SWT.NONE);
		rmsTab.setText(UILocale.get("OPTION_TAB_RECORDS", "Records"));
		rmsTab.setControl(this.recordsComp);

		final CTabItem networkTab = new CTabItem(this.tabFolder, SWT.NONE);
		networkTab.setText(UILocale.get("OPTION_TAB_NETWORK", "Network"));
		networkTab.setControl(this.networkComp);

		final CTabItem mediaTab = new CTabItem(this.tabFolder, SWT.NONE);
		mediaTab.setText(UILocale.get("OPTION_TAB_MEDIA", "Media"));
		mediaTab.setControl(this.mediaComp);

		final CTabItem securityTab = new CTabItem(this.tabFolder, SWT.NONE);
		securityTab.setText(UILocale.get("OPTION_TAB_SECURITY", "Security"));
		securityTab.setControl(this.securityComp);

		final CTabItem propsTab = new CTabItem(this.tabFolder, SWT.NONE);
		propsTab.setText(UILocale.get("OPTION_TAB_SYSTEM_PROPERTIES", "Properties"));
		propsTab.setControl(this.propsComp);

		final CTabItem hotkeysTab = new CTabItem(this.tabFolder, SWT.NONE);
		hotkeysTab.setText("Hotkeys");
		hotkeysTab.setControl(this.hotkeysComp);
	}

	private void setupKeyMapComp() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.CENTER;
		final GridData layoutData2 = new GridData();
		(layoutData2).grabExcessHorizontalSpace = true;
		layoutData2.verticalAlignment = GridData.CENTER;
		layoutData2.horizontalAlignment = GridData.FILL;
		final GridData layoutData3 = new GridData();
		(layoutData3).grabExcessHorizontalSpace = false;
		layoutData3.verticalAlignment = GridData.CENTER;
		layoutData3.horizontalAlignment = GridData.FILL;
		final GridData layoutData4 = new GridData();
		(layoutData4).grabExcessHorizontalSpace = false;
		layoutData4.verticalAlignment = GridData.CENTER;
		layoutData4.horizontalAlignment = GridData.FILL;
		final GridData layoutData5 = new GridData();
		(layoutData5).grabExcessHorizontalSpace = false;
		layoutData5.verticalAlignment = GridData.CENTER;
		layoutData5.horizontalAlignment = GridData.FILL;
		final GridData layoutData6 = new GridData();
		(layoutData6).grabExcessHorizontalSpace = false;
		layoutData6.verticalAlignment = GridData.CENTER;
		layoutData6.horizontalAlignment = GridData.FILL;
		final GridData layoutData7 = new GridData();
		(layoutData7).grabExcessHorizontalSpace = false;
		layoutData7.verticalAlignment = GridData.CENTER;
		layoutData7.horizontalAlignment = GridData.FILL;
		final GridData layoutData8 = new GridData();
		(layoutData8).grabExcessHorizontalSpace = false;
		layoutData8.verticalAlignment = GridData.CENTER;
		layoutData8.horizontalAlignment = GridData.FILL;
		final GridData layoutData9 = new GridData();
		(layoutData9).grabExcessHorizontalSpace = false;
		layoutData9.verticalAlignment = GridData.CENTER;
		layoutData9.horizontalAlignment = GridData.FILL;
		final GridData layoutData10 = new GridData();
		(layoutData10).grabExcessHorizontalSpace = false;
		layoutData10.verticalAlignment = GridData.CENTER;
		layoutData10.horizontalAlignment = GridData.FILL;
		final GridData layoutData11 = new GridData();
		(layoutData11).grabExcessHorizontalSpace = false;
		layoutData11.verticalAlignment = GridData.CENTER;
		layoutData11.horizontalAlignment = GridData.FILL;
		final GridData layoutData12 = new GridData();
		(layoutData12).grabExcessHorizontalSpace = false;
		layoutData12.verticalAlignment = GridData.CENTER;
		layoutData12.horizontalAlignment = GridData.FILL;
		final GridData layoutData13 = new GridData();
		(layoutData13).grabExcessHorizontalSpace = false;
		layoutData13.verticalAlignment = GridData.CENTER;
		layoutData13.horizontalAlignment = GridData.FILL;
		final GridData layoutData14 = new GridData();
		(layoutData14).grabExcessHorizontalSpace = false;
		layoutData14.verticalAlignment = GridData.CENTER;
		layoutData14.horizontalAlignment = GridData.FILL;
		final GridData layoutData15 = new GridData();
		(layoutData15).grabExcessHorizontalSpace = false;
		layoutData15.verticalAlignment = GridData.CENTER;
		layoutData15.horizontalAlignment = GridData.FILL;
		final GridData layoutData16 = new GridData();
		(layoutData16).grabExcessHorizontalSpace = false;
		layoutData16.verticalAlignment = GridData.CENTER;
		layoutData16.horizontalAlignment = GridData.FILL;
		final GridData layoutData17 = new GridData();
		(layoutData17).grabExcessHorizontalSpace = false;
		layoutData17.verticalAlignment = GridData.CENTER;
		layoutData17.horizontalAlignment = GridData.FILL;
		final GridData layoutData18 = new GridData();
		(layoutData18).grabExcessHorizontalSpace = false;
		layoutData18.verticalAlignment = GridData.CENTER;
		layoutData18.horizontalAlignment = GridData.FILL;
		final GridData layoutData19 = new GridData();
		(layoutData19).grabExcessHorizontalSpace = false;
		layoutData19.verticalAlignment = GridData.CENTER;
		layoutData19.horizontalAlignment = GridData.FILL;
		final GridData layoutData20 = new GridData();
		(layoutData20).grabExcessHorizontalSpace = true;
		layoutData20.verticalAlignment = GridData.CENTER;
		layoutData20.horizontalAlignment = GridData.FILL;
		final GridData layoutData21 = new GridData();
		(layoutData21).horizontalAlignment = GridData.FILL;
		layoutData21.verticalAlignment = GridData.CENTER;
		layoutData21.widthHint = 30;
		final GridData layoutData22 = new GridData();
		(layoutData22).horizontalAlignment = GridData.FILL;
		layoutData22.verticalAlignment = GridData.CENTER;
		final GridData layoutData23 = new GridData();
		(layoutData23).horizontalAlignment = GridData.FILL;
		layoutData23.verticalAlignment = GridData.CENTER;
		final GridData layoutData24 = new GridData();
		(layoutData24).horizontalAlignment = GridData.FILL;
		layoutData24.verticalAlignment = GridData.CENTER;
		final GridData layoutData25 = new GridData();
		(layoutData25).horizontalAlignment = GridData.FILL;
		layoutData25.verticalAlignment = GridData.CENTER;
		layoutData25.widthHint = 30;
		final GridData layoutData26 = new GridData();
		(layoutData26).horizontalAlignment = GridData.FILL;
		layoutData26.verticalAlignment = GridData.CENTER;
		final GridData layoutData27 = new GridData();
		(layoutData27).horizontalAlignment = GridData.FILL;
		layoutData27.verticalAlignment = GridData.CENTER;
		final GridData layoutData28 = new GridData();
		(layoutData28).horizontalSpan = 2;
		layoutData28.verticalAlignment = GridData.CENTER;
		layoutData28.horizontalAlignment = GridData.FILL;
		final GridData layoutData29 = new GridData();
		(layoutData29).horizontalAlignment = GridData.FILL;
		layoutData29.verticalAlignment = GridData.CENTER;
		final GridData layoutData30 = new GridData();
		(layoutData30).horizontalAlignment = GridData.FILL;
		layoutData30.verticalAlignment = GridData.CENTER;
		final GridData layoutData31 = new GridData();
		(layoutData31).horizontalAlignment = GridData.FILL;
		layoutData31.verticalAlignment = GridData.CENTER;
		final GridData layoutData32 = new GridData();
		(layoutData32).horizontalAlignment = GridData.FILL;
		layoutData32.verticalAlignment = GridData.CENTER;
		final GridData layoutData33 = new GridData();
		(layoutData33).horizontalAlignment = GridData.FILL;
		layoutData33.verticalAlignment = GridData.CENTER;
		final GridData layoutData34 = new GridData();
		(layoutData34).horizontalAlignment = GridData.FILL;
		layoutData34.verticalAlignment = GridData.CENTER;
		final GridData layoutData36 = new GridData();
		(layoutData36).horizontalAlignment = GridData.FILL;
		layoutData36.verticalAlignment = GridData.CENTER;
		final GridData layoutData37 = new GridData();
		layoutData37.grabExcessHorizontalSpace = true;
		(layoutData37).horizontalAlignment = GridData.FILL;
		layoutData37.verticalAlignment = GridData.CENTER;
		final GridData layoutData38 = new GridData();
		layoutData38.grabExcessHorizontalSpace = true;
		(layoutData38).horizontalAlignment = GridData.FILL;
		layoutData38.verticalAlignment = GridData.CENTER;
		final GridData layoutData39 = new GridData();
		(layoutData39).horizontalAlignment = GridData.FILL;
		layoutData39.verticalAlignment = GridData.CENTER;
		final GridData layoutData40 = new GridData();
		(layoutData40).horizontalAlignment = GridData.FILL;
		layoutData40.grabExcessHorizontalSpace = false;
		layoutData40.verticalAlignment = GridData.CENTER;

		keyMapTabComp = new Composite(this.tabFolder, SWT.NONE);
		keyMapTabComp.setFont(f);
		GridLayout l = new GridLayout();
		l.marginWidth = 0;
		l.marginHeight = 0;
		keyMapTabComp.setLayout(l);

		keyMapControllerComp = new Composite(keyMapTabComp, SWT.NONE);
		keyMapControllerComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l = new GridLayout();
		l.numColumns = 6;
		keyMapControllerComp.setLayout(l);

		Composite keyMapBindsComp = new Composite(keyMapTabComp, SWT.NONE);
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
		
		CLabel controllerLabel = new CLabel(this.keyMapControllerComp, SWT.NONE);
		controllerLabel.setText(UILocale.get("OPTION_KEYMAP_CONTROLLER", "Controller:"));
		controllerLabel.setLayoutData(layoutData);
		
		this.createControllerSelector();
		
		CLabel leftSoftKeyLabel = new CLabel(keyMapBindsComp, SWT.NONE);
		(leftSoftKeyLabel).setText(UILocale.get("OPTION_KEYMAP_LSK", "LeftSoftKey:"));
		leftSoftKeyLabel.setLayoutData(layoutData38);
		
		(this.leftSoftKeyBinding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.leftSoftKeyBinding.setLayoutData(layoutData20);
		this.leftSoftKeyBinding.setText(KeyMapping.get(17));
		this.leftSoftKeyBinding.addKeyListener(new KeyBindListener(leftSoftKeyBinding, 17));
		leftSoftKeyBinding.addVerifyListener(verify);
		
		CLabel rightSoftKeyLabel = new CLabel(keyMapBindsComp, SWT.NONE);
		(rightSoftKeyLabel).setText(UILocale.get("OPTION_KEYMAP_RSK", "RightSoftKey:"));
		rightSoftKeyLabel.setLayoutData(layoutData37);
		
		(this.rightSoftKeyBinding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.rightSoftKeyBinding.setLayoutData(layoutData2);
		this.rightSoftKeyBinding.setText(KeyMapping.get(18));
		this.rightSoftKeyBinding.addKeyListener(new KeyBindListener(rightSoftKeyBinding, 18));
		rightSoftKeyBinding.addVerifyListener(verify);
		
		CLabel num1Label = new CLabel(keyMapBindsComp, SWT.NONE);
		(num1Label).setText(UILocale.get("OPTION_KEYMAP_NUM_1", "Num_1:"));
		num1Label.setLayoutData(layoutData40);
		
		(this.num1Binding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.num1Binding.setLayoutData(layoutData16);
		this.num1Binding.setText(KeyMapping.get(1));
		this.num1Binding.addKeyListener(new KeyBindListener(num1Binding, 1));
		num1Binding.addVerifyListener(verify);
		
		CLabel num2Label = new CLabel(keyMapBindsComp, SWT.NONE);
		(num2Label).setText(UILocale.get("OPTION_KEYMAP_NUM_2", "Num_2:"));
		num2Label.setLayoutData(layoutData32);
		
		(this.num2Binding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.num2Binding.setLayoutData(layoutData5);
		this.num2Binding.setText(KeyMapping.get(2));
		this.num2Binding.addKeyListener(new KeyBindListener(num2Binding, 2));
		num2Binding.addVerifyListener(verify);
		
		CLabel num3Label = new CLabel(keyMapBindsComp, SWT.NONE);
		(num3Label).setText(UILocale.get("OPTION_KEYMAP_NUM_3", "Num_3:"));
		num3Label.setLayoutData(layoutData31);
		
		(this.num3Binding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.num3Binding.setLayoutData(layoutData15);
		this.num3Binding.setText(KeyMapping.get(3));
		this.num3Binding.addKeyListener(new KeyBindListener(num3Binding, 3));
		num3Binding.addVerifyListener(verify);
		
		CLabel num4Label = new CLabel(keyMapBindsComp, SWT.NONE);
		(num4Label).setText(UILocale.get("OPTION_KEYMAP_NUM_4", "Num_4:"));
		num4Label.setLayoutData(layoutData27);
		
		(this.num4Binding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.num4Binding.setLayoutData(layoutData6);
		this.num4Binding.setText(KeyMapping.get(4));
		this.num4Binding.addKeyListener(new KeyBindListener(num4Binding, 4));
		num4Binding.addVerifyListener(verify);
		
		CLabel num5Label = new CLabel(keyMapBindsComp, SWT.NONE);
		(num5Label).setText(UILocale.get("OPTION_KEYMAP_NUM_5", "Num_5:"));
		num5Label.setLayoutData(layoutData25);
		
		(this.num5Binding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.num5Binding.setLayoutData(layoutData14);
		this.num5Binding.setText(KeyMapping.get(5));
		this.num5Binding.addKeyListener(new KeyBindListener(num5Binding, 5));
		num5Binding.addVerifyListener(verify);
		
		CLabel num6Label = new CLabel(keyMapBindsComp, SWT.NONE);
		(num6Label).setText(UILocale.get("OPTION_KEYMAP_NUM_6", "Num_6:"));
		num6Label.setLayoutData(layoutData26);
		
		(this.num6Binding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.num6Binding.setLayoutData(layoutData7);
		this.num6Binding.setText(KeyMapping.get(6));
		this.num6Binding.addKeyListener(new KeyBindListener(num6Binding, 6));
		num6Binding.addVerifyListener(verify);
		
		CLabel num7Label = new CLabel(keyMapBindsComp, SWT.NONE);
		(num7Label).setText(UILocale.get("OPTION_KEYMAP_NUM_7", "Num_7:"));
		num7Label.setLayoutData(layoutData24);
		
		(this.num7Binding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.num7Binding.setLayoutData(layoutData13);
		this.num7Binding.setText(KeyMapping.get(7));
		this.num7Binding.addKeyListener(new KeyBindListener(num7Binding, 7));
		num7Binding.addVerifyListener(verify);
		
		CLabel num8Label = new CLabel(keyMapBindsComp, SWT.NONE);
		(num8Label).setText(UILocale.get("OPTION_KEYMAP_NUM_8", "Num_8:"));
		num8Label.setLayoutData(layoutData23);
		
		(this.num8Binding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.num8Binding.setLayoutData(layoutData8);
		this.num8Binding.setText(KeyMapping.get(8));
		this.num8Binding.addKeyListener(new KeyBindListener(num8Binding, 8));
		num8Binding.addVerifyListener(verify);
		
		CLabel num9Label = new CLabel(keyMapBindsComp, SWT.NONE);
		(num9Label).setText(UILocale.get("OPTION_KEYMAP_NUM_9", "Num_9:"));
		num9Label.setLayoutData(layoutData21);
		
		(this.num9Binding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.num9Binding.setLayoutData(layoutData12);
		this.num9Binding.setText(KeyMapping.get(9));
		this.num9Binding.addKeyListener(new KeyBindListener(num9Binding, 9));
		num9Binding.addVerifyListener(verify);
		
		CLabel num0Label = new CLabel(keyMapBindsComp, SWT.NONE);
		(num0Label).setText(UILocale.get("OPTION_KEYMAP_NUM_0", "Num_0:"));
		num0Label.setLayoutData(layoutData22);
		
		this.num0Binding = new Text(keyMapBindsComp, SWT.BORDER);
		num0Binding.setEditable(true);
		this.num0Binding.setLayoutData(layoutData9);
		this.num0Binding.setText(KeyMapping.get(0));
		this.num0Binding.addKeyListener(new KeyBindListener(num0Binding, 0));
		num0Binding.addVerifyListener(verify);
		
		CLabel starKeyLabel = new CLabel(keyMapBindsComp, SWT.NONE);
		(starKeyLabel).setText(UILocale.get("OPTION_KEYMAP_KEY_*", "Key *:"));
		starKeyLabel.setLayoutData(layoutData30);
		
		(this.starBinding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.starBinding.setLayoutData(layoutData11);
		this.starBinding.setText(KeyMapping.get(10));
		this.starBinding.addKeyListener(new KeyBindListener(starBinding, 10));
		starBinding.addVerifyListener(verify);
		
		CLabel poundKeyLabel = new CLabel(keyMapBindsComp, SWT.NONE);
		(poundKeyLabel).setText(UILocale.get("OPTION_KEYMAP_KEY_#", "Key #:"));
		poundKeyLabel.setLayoutData(layoutData29);
		
		(this.poundBinding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.poundBinding.setLayoutData(layoutData10);
		this.poundBinding.setText(KeyMapping.get(11));
		this.poundBinding.addKeyListener(new KeyBindListener(poundBinding, 11));
		poundBinding.addVerifyListener(verify);
		
		CLabel leftKeyLabel = new CLabel(keyMapBindsComp, SWT.NONE);
		(leftKeyLabel).setText(UILocale.get("OPTION_KEYMAP_PAD_LEFT", "Pad_Left:"));
		
		(this.leftBinding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.leftBinding.setLayoutData(layoutData19);
		this.leftBinding.setText(KeyMapping.get(14));
		this.leftBinding.addKeyListener(new KeyBindListener(leftBinding, 14));
		leftBinding.addVerifyListener(verify);
		
		CLabel rightKeyLabel = new CLabel(keyMapBindsComp, SWT.NONE);
		(rightKeyLabel).setText(UILocale.get("OPTION_KEYMAP_PAD_RIGHT", "Pad_Right:"));
		rightKeyLabel.setLayoutData(layoutData36);
		
		(this.rightBinding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.rightBinding.setLayoutData(layoutData3);
		this.rightBinding.setText(KeyMapping.get(15));
		this.rightBinding.addKeyListener(new KeyBindListener(rightBinding, 15));
		rightBinding.addVerifyListener(verify);
		
		CLabel upKeyLabel = new CLabel(keyMapBindsComp, SWT.NONE);
		(upKeyLabel).setText(UILocale.get("OPTION_KEYMAP_PAD_UP", "Pad_Up:"));
		upKeyLabel.setLayoutData(layoutData34);
		
		(this.upBinding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.upBinding.setLayoutData(layoutData18);
		this.upBinding.setText(KeyMapping.get(12));
		this.upBinding.addKeyListener(new KeyBindListener(upBinding, 12));
		upBinding.addVerifyListener(verify);
		
		CLabel downKeyLabel = new CLabel(keyMapBindsComp, SWT.NONE);
		(downKeyLabel).setText(UILocale.get("OPTION_KEYMAP_PAD_DOWN", "Pad_Down:"));
		downKeyLabel.setLayoutData(layoutData33);
		
		(this.downBinding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.downBinding.setLayoutData(layoutData4);
		this.downBinding.setText(KeyMapping.get(13));
		this.downBinding.addKeyListener(new KeyBindListener(downBinding, 13));
		downBinding.addVerifyListener(verify);
		
		CLabel fireKeyLabel = new CLabel(keyMapBindsComp, SWT.NONE);
		(fireKeyLabel).setText(UILocale.get("OPTION_KEYMAP_PAD_MIDDLE", "Pad_Middle:"));
		fireKeyLabel.setLayoutData(layoutData39);
		
		(this.fireBinding = new Text(keyMapBindsComp, SWT.BORDER)).setEditable(true);
		this.fireBinding.setLayoutData(layoutData17);
		this.fireBinding.setText(KeyMapping.get(16));
		this.fireBinding.addKeyListener(new KeyBindListener(fireBinding, 16));
		fireBinding.addVerifyListener(verify);
		
		this.keyCacheCheck = new Button(keyMapBindsComp, SWT.CHECK);
		keyCacheCheck .setText(UILocale.get("OPTION_KEYMAP_KEY_CACHE", "Enable Key Cache"));
		this.keyCacheCheck.setLayoutData(layoutData28);
		this.keyCacheCheck.setSelection(Settings.enableKeyCache);
		this.cacheKeyboardBindings();
	}

	private void createControllerSelector() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalSpan = 2;
		layoutData.verticalAlignment = GridData.CENTER;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		this.controllerCombo = new Combo(this.keyMapControllerComp, SWT.READ_ONLY);
		controllerCombo.setLayoutData(layoutData);
		this.controllerCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent modifyEvent) {
				refreshKeyBindingFields();
			}
		});
		final GridData layoutData2 = new GridData();
		(layoutData2).horizontalAlignment = GridData.CENTER;
		layoutData2.verticalAlignment = GridData.CENTER;
		Button keymapRefreshBtn;
		keymapRefreshBtn = new Button(this.keyMapControllerComp, SWT.FLAT | SWT.PUSH);
		keymapRefreshBtn.setText(UILocale.get("OPTION_KEYMAP_REFRESH", "Refresh"));
		keymapRefreshBtn.setLayoutData(layoutData2);
		keymapRefreshBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				Controllers.refresh(true);
				populateControllerSelector();
			}
		});
		Button keymapClearBtn;
		keymapClearBtn = new Button(this.keyMapControllerComp, SWT.FLAT | SWT.PUSH);
		keymapClearBtn.setText(UILocale.get("OPTION_KEYMAP_CLEAR", "Clear"));
		GridData layoutData3 = new GridData();
		layoutData3.horizontalAlignment = GridData.CENTER;
		layoutData3.verticalAlignment = GridData.CENTER;
		keymapClearBtn.setLayoutData(layoutData3);
		keymapClearBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				num0Binding.setText("");
				num1Binding.setText("");
				num2Binding.setText("");
				num3Binding.setText("");
				num4Binding.setText("");
				num5Binding.setText("");
				num6Binding.setText("");
				num7Binding.setText("");
				num8Binding.setText("");
				num9Binding.setText("");
				starBinding.setText("");
				poundBinding.setText("");
				leftBinding.setText("");
				rightBinding.setText("");
				upBinding.setText("");
				downBinding.setText("");
				fireBinding.setText("");
				leftSoftKeyBinding.setText("");
				rightSoftKeyBinding.setText("");
				if (controllerCombo.getText().equals("Keyboard")) {
					for (int i = 0; i < Property.keyboardBindings.length; i++) {
						Property.keyboardBindings[i] = "";
						KeyMapping.mapDeviceKey(i, "");
					}
				} else {
					for (int i = 0; i < 19; i++) {
						Controllers.bind(controllerCombo.getSelectionIndex() - 1, i, "");
					}
				}
			}
		});
		Button keymapResetBtn = new Button(this.keyMapControllerComp, SWT.FLAT | SWT.PUSH);
		keymapResetBtn.setText(UILocale.get("OPTION_KEYMAP_RESET", "Reset"));
		GridData layoutData4 = new GridData();
		layoutData4.horizontalAlignment = GridData.CENTER;
		layoutData4.verticalAlignment = GridData.CENTER;
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
					for (int i = 0; i < Property.keyboardBindings.length; i++) {
						Property.keyboardBindings[i] = KeyMapping.get(i);
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
				refreshKeyBindingFields();
			}
		});
		this.populateControllerSelector();
	}

	private void populateControllerSelector() {
		this.controllerCombo.removeAll();
		this.controllerCombo.add("Keyboard");
		for (int controllerCount = Controllers.getControllersCount(), i = 0; i < controllerCount; ++i) {
			this.controllerCombo.add(Controllers.getController(i).getName());
		}
		this.controllerCombo.setText("Keyboard");
	}

	private void cacheKeyboardBindings() {
		Property.keyboardBindings[0] = this.num0Binding.getText().trim();
		Property.keyboardBindings[1] = this.num1Binding.getText().trim();
		Property.keyboardBindings[2] = this.num2Binding.getText().trim();
		Property.keyboardBindings[3] = this.num3Binding.getText().trim();
		Property.keyboardBindings[4] = this.num4Binding.getText().trim();
		Property.keyboardBindings[5] = this.num5Binding.getText().trim();
		Property.keyboardBindings[6] = this.num6Binding.getText().trim();
		Property.keyboardBindings[7] = this.num7Binding.getText().trim();
		Property.keyboardBindings[8] = this.num8Binding.getText().trim();
		Property.keyboardBindings[9] = this.num9Binding.getText().trim();
		Property.keyboardBindings[10] = this.starBinding.getText().trim();
		Property.keyboardBindings[11] = this.poundBinding.getText().trim();
		Property.keyboardBindings[14] = this.leftBinding.getText().trim();
		Property.keyboardBindings[15] = this.rightBinding.getText().trim();
		Property.keyboardBindings[12] = this.upBinding.getText().trim();
		Property.keyboardBindings[13] = this.downBinding.getText().trim();
		Property.keyboardBindings[16] = this.fireBinding.getText().trim();
		Property.keyboardBindings[17] = this.leftSoftKeyBinding.getText().trim();
		Property.keyboardBindings[18] = this.rightSoftKeyBinding.getText().trim();
	}

	private void refreshKeyBindingFields() {
		final int selectionIndex;
		if ((selectionIndex = this.controllerCombo.getSelectionIndex()) < 0 || this.num0Binding == null || this.num0Binding.isDisposed()) {
			return;
		}
		Text text;
		String rightSoftKeyValue;
		if (selectionIndex == 0) {
			this.num0Binding.setText(Property.keyboardBindings[0]);
			this.num1Binding.setText(Property.keyboardBindings[1]);
			this.num2Binding.setText(Property.keyboardBindings[2]);
			this.num3Binding.setText(Property.keyboardBindings[3]);
			this.num4Binding.setText(Property.keyboardBindings[4]);
			this.num5Binding.setText(Property.keyboardBindings[5]);
			this.num6Binding.setText(Property.keyboardBindings[6]);
			this.num7Binding.setText(Property.keyboardBindings[7]);
			this.num8Binding.setText(Property.keyboardBindings[8]);
			this.num9Binding.setText(Property.keyboardBindings[9]);
			this.starBinding.setText(Property.keyboardBindings[10]);
			this.poundBinding.setText(Property.keyboardBindings[11]);
			this.leftBinding.setText(Property.keyboardBindings[14]);
			this.rightBinding.setText(Property.keyboardBindings[15]);
			this.upBinding.setText(Property.keyboardBindings[12]);
			this.downBinding.setText(Property.keyboardBindings[13]);
			this.fireBinding.setText(Property.keyboardBindings[16]);
			this.leftSoftKeyBinding.setText(Property.keyboardBindings[17]);
			text = this.rightSoftKeyBinding;
			rightSoftKeyValue = Property.keyboardBindings[18];
		} else {
			final int n = selectionIndex - 1;
			this.num0Binding.setText(Controllers.getBind(n, 0));
			this.num1Binding.setText(Controllers.getBind(n, 1));
			this.num2Binding.setText(Controllers.getBind(n, 2));
			this.num3Binding.setText(Controllers.getBind(n, 3));
			this.num4Binding.setText(Controllers.getBind(n, 4));
			this.num5Binding.setText(Controllers.getBind(n, 5));
			this.num6Binding.setText(Controllers.getBind(n, 6));
			this.num7Binding.setText(Controllers.getBind(n, 7));
			this.num8Binding.setText(Controllers.getBind(n, 8));
			this.num9Binding.setText(Controllers.getBind(n, 9));
			this.starBinding.setText(Controllers.getBind(n, 10));
			this.poundBinding.setText(Controllers.getBind(n, 11));
			this.leftBinding.setText(Controllers.getBind(n, 14));
			this.rightBinding.setText(Controllers.getBind(n, 15));
			this.upBinding.setText(Controllers.getBind(n, 12));
			this.downBinding.setText(Controllers.getBind(n, 13));
			this.fireBinding.setText(Controllers.getBind(n, 16));
			this.leftSoftKeyBinding.setText(Controllers.getBind(n, 17));
			text = this.rightSoftKeyBinding;
			rightSoftKeyValue = Controllers.getBind(n, 18);
		}
		text.setText(rightSoftKeyValue);
	}

	public boolean updateController() {
		if (this.controllerCombo == null || this.controllerCombo.isDisposed()) {
			return false;
		}
		Property aClass38_14971 = this;
		display.asyncExec(new Runnable() {
			private final Property aClass38_1497 = aClass38_14971;

			public final void run() {
				int selectionIndex;
				if ((selectionIndex = getControllerCombo(this.aClass38_1497).getSelectionIndex()) == 0) {
					return;
				}
				final String controllerInput;
				String string;
				if (!(string = (controllerInput = Controllers.method749())).equalsIgnoreCase("LEFT")
						&& !string.equalsIgnoreCase("RIGHT")
						&& !string.equalsIgnoreCase("UP")
						&& !string.equalsIgnoreCase("DOWN")
						&& !string.startsWith("Axis")) {
					string = "B_" + string;
				}
				--selectionIndex;
				Text text;
				if (getDigit0KeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 0, controllerInput);
					text = getDigit0KeyField(this.aClass38_1497);
				} else if (getDigit1KeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 1, controllerInput);
					text = getDigit1KeyField(this.aClass38_1497);
				} else if (getDigit2KeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 2, controllerInput);
					text = getDigit2KeyField(this.aClass38_1497);
				} else if (getDigit3KeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 3, controllerInput);
					text = getDigit3KeyField(this.aClass38_1497);
				} else if (getDigit4KeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 4, controllerInput);
					text = getDigit4KeyField(this.aClass38_1497);
				} else if (getDigit5KeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 5, controllerInput);
					text = getDigit5KeyField(this.aClass38_1497);
				} else if (getDigit6KeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 6, controllerInput);
					text = getDigit6KeyField(this.aClass38_1497);
				} else if (getDigit7KeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 7, controllerInput);
					text = getDigit7KeyField(this.aClass38_1497);
				} else if (getDigit8KeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 8, controllerInput);
					text = getDigit8KeyField(this.aClass38_1497);
				} else if (getDigit9KeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 9, controllerInput);
					text = getDigit9KeyField(this.aClass38_1497);
				} else if (getStarKeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 10, controllerInput);
					text = getStarKeyField(this.aClass38_1497);
				} else if (getPoundKeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 11, controllerInput);
					text = getPoundKeyField(this.aClass38_1497);
				} else if (getLeftDirectionKeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 14, controllerInput);
					text = getLeftDirectionKeyField(this.aClass38_1497);
				} else if (getRightDirectionKeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 15, controllerInput);
					text = getRightDirectionKeyField(this.aClass38_1497);
				} else if (getUpDirectionKeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 12, controllerInput);
					text = getUpDirectionKeyField(this.aClass38_1497);
				} else if (getDownDirectionKeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 13, controllerInput);
					text = getDownDirectionKeyField(this.aClass38_1497);
				} else if (getCenterKeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 16, controllerInput);
					text = getCenterKeyField(this.aClass38_1497);
				} else if (getLeftSoftKeyField(this.aClass38_1497).isFocusControl()) {
					Controllers.bind(selectionIndex, 17, controllerInput);
					text = getLeftSoftKeyField(this.aClass38_1497);
				} else {
					if (!getRightSoftKeyField(this.aClass38_1497).isFocusControl()) {
						return;
					}
					Controllers.bind(selectionIndex, 18, controllerInput);
					text = getRightSoftKeyField(this.aClass38_1497);
				}
				text.setText(string);
			}
		});
		return true;
	}

	private void setupSystemComp() {
		this.systemScrollComp = new ScrolledComposite(this.tabFolder, SWT.V_SCROLL);
		this.systemComp = new Composite(this.systemScrollComp, SWT.NONE);
		systemComp.setLayout(new GridLayout());
		this.initSystemComp();
		systemScrollComp.setContent(systemComp);
		systemScrollComp.setExpandHorizontal(true);
	}

	private void setupDisableApiComp() {
		disableApiComp = new Composite(tabFolder, SWT.NONE);
		disableApiComp.setLayout(new GridLayout());

		GridData d = new GridData();
		d.horizontalAlignment = GridData.FILL;
		d.grabExcessHorizontalSpace = true;
		d.grabExcessVerticalSpace = true;
		d.verticalAlignment = GridData.FILL;
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
		propsComp = new Composite(tabFolder, SWT.NONE);
		propsComp.setLayout(new GridLayout());

		GridData d = new GridData();
		d.horizontalAlignment = GridData.FILL;
		d.grabExcessHorizontalSpace = true;
		d.grabExcessVerticalSpace = true;
		d.verticalAlignment = GridData.FILL;

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
		this.mediaComp = new Composite(this.tabFolder, SWT.NONE);
		mediaComp.setLayout(new GridLayout());

		final GridData fill = new GridData();
		fill.horizontalAlignment = GridData.FILL;
		fill.grabExcessHorizontalSpace = true;
		fill.grabExcessVerticalSpace = true;
		fill.verticalAlignment = GridData.FILL;

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

		Group mediaGroup = new Group(this.mediaComp, SWT.NONE);
		mediaGroup.setText(UILocale.get("OPTION_TAB_MEDIA", "Media"));
		mediaGroup.setLayout(new GridLayout(2, false));
		mediaGroup.setLayoutData(fill);

		Label label = new Label(mediaGroup, SWT.SHADOW_NONE);
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
				FileDialog fileDialog = new FileDialog(settingsShell, SWT.OPEN);
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

		if (!Emulator.isX64()) {
			maMidiCheck = new Button(mediaGroup, SWT.CHECK);
			maMidiCheck.setText("Use MA-3/5 emulator for MIDI playback (experimental)");
			maMidiCheck.setLayoutData(fillHor5);
			maMidiCheck.setSelection(Settings.maMidi);
		}

		vlcCheck = new Button(mediaGroup, SWT.CHECK);
		vlcCheck.setText("Enable VLC Player");
		vlcCheck.setLayoutData(fillHor4);
		vlcCheck.setSelection(Settings.enableVlc);

		label = new Label(mediaGroup, SWT.SHADOW_NONE);
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
				FileDialog fileDialog = new FileDialog(settingsShell, SWT.OPEN);
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
					if (vlcDir.getFileName().toString().equalsIgnoreCase("lib")) {
						vlcDir = vlcDir.getParent();
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

		ottCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
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
		groupData.horizontalAlignment = GridData.FILL;
		groupData.grabExcessHorizontalSpace = true;
		groupData.grabExcessVerticalSpace = true;
		groupData.verticalAlignment = GridData.BEGINNING;

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

			CLabel label = new CLabel(permGroup, SWT.NONE);
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
	
	private void setupHotkeysComp() {
		hotkeysComp = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		hotkeysContent = new Composite(hotkeysComp, SWT.NONE);

		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 5;

		hotkeysContent.setLayout(new GridLayout());

		final GridData labelGridData = new GridData();
		labelGridData.horizontalAlignment = SWT.FILL;
		labelGridData.grabExcessHorizontalSpace = true;
		labelGridData.horizontalSpan = 2;

		final GridData groupData = new GridData();
		groupData.horizontalAlignment = GridData.FILL;
		groupData.grabExcessHorizontalSpace = true;
		groupData.grabExcessVerticalSpace = true;
		groupData.verticalAlignment = GridData.BEGINNING;

//		hotkeysResetBtn = new Button(hotkeysContent, SWT.PUSH);
//		hotkeysResetBtn.setText("Reset");
//		hotkeysResetBtn.setLayoutData(labelGridData);

		Group group = new Group(hotkeysContent, SWT.NONE);
		group.setText("Hotkeys");
		group.setLayout(layout);
		group.setLayoutData(groupData);

		VerifyListener verify = new VerifyListener() {
			public void verifyText(VerifyEvent verifyEvent) {
				verifyEvent.doit = verifyEvent.start == 0 && verifyEvent.text != null;
			}
		};

		for (String s: Accelerators.accelerators.keySet()) {
			final GridData data = new GridData();
			data.horizontalAlignment = SWT.FILL;

			CLabel label = new CLabel(group, SWT.NONE);
			label.setText(s);
			label.setLayoutData(data);

			final GridData textLayoutData = new GridData();
			textLayoutData.grabExcessHorizontalSpace = true;
			textLayoutData.horizontalAlignment = GridData.FILL;
			textLayoutData.verticalAlignment = GridData.CENTER;

			Text text = new Text(group, SWT.BORDER);
			text.setText(Accelerators.get(s));
			text.setEditable(true);
			text.setLayoutData(textLayoutData);
			text.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					e.doit = false;

					if (e.keyCode == SWT.BS) {
						Accelerators.set(s, "0");
						text.setText(Accelerators.get(s));
						return;
					}

					int key = 0;
					if (e.keyCode >= 'a' && e.keyCode <= 'z') {
						key = Character.toUpperCase(e.keyCode);
					} else if (e.keyCode >= ' ' && e.keyCode <= ']') {
						key = e.keyCode;
					} else {
						switch (e.keyCode) {
						case SWT.ARROW_UP:
						case SWT.ARROW_DOWN:
						case SWT.ARROW_LEFT:
						case SWT.ARROW_RIGHT:
						case SWT.PAGE_UP:
						case SWT.PAGE_DOWN:
						case SWT.HOME:
						case SWT.END:
						case SWT.INSERT:
						case SWT.F1:
						case SWT.F2:
						case SWT.F3:
						case SWT.F4:
						case SWT.F5:
						case SWT.F6:
						case SWT.F7:
						case SWT.F8:
						case SWT.F9:
						case SWT.F10:
						case SWT.F11:
						case SWT.F12:
							key = e.keyCode;
						}
					}

					if (key == 0) return;

					if ((e.stateMask & SWT.CONTROL) != 0) {
						key |= SWT.CONTROL;
					}
					if ((e.stateMask & SWT.ALT) != 0) {
						key |= SWT.ALT;
					}
					if ((e.stateMask & SWT.SHIFT) != 0) {
						key |= SWT.SHIFT;
					}
					if ((e.stateMask & SWT.COMMAND) != 0) {
						key |= SWT.COMMAND;
					}

					Accelerators.set(s, Integer.toString(key));
					text.setText(Accelerators.get(s));
				}
			});
			text.addVerifyListener(verify);
		}

		hotkeysComp.setContent(hotkeysContent);
		hotkeysComp.setExpandHorizontal(true);
	}

	private void initSystemComp() {

		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		
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

		updateBranchCombo = new Combo(updateComp, SWT.DROP_DOWN | SWT.READ_ONLY);
		updateBranchCombo.setLayoutData(comboLayoutData2);
		updateBranchCombo.setFont(f);
		updateBranchCombo.add("stable");
		updateBranchCombo.add("dev");

		updateBranchCombo.setText(Settings.updateBranch);

		autoUpdatesBtn = new Button(updateComp, SWT.CHECK);
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
		
		this.infoColorHexCheck = new Button(grpUi, SWT.CHECK);
		infoColorHexCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		infoColorHexCheck.setText(UILocale.get("OPTION_SYSTEM_INFO_COLOR", "Info View: Show color in (R,G,B)"));
		this.infoColorHexCheck.setSelection(Settings.infoColorHex);

		showAppSettingsBtn = new Button(grpUi, SWT.CHECK);
		showAppSettingsBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		showAppSettingsBtn.setText("Show application settings on first start");
		showAppSettingsBtn.setSelection(Settings.showAppSettingsOnStart);
		
		grpDebug = new Group(systemComp, SWT.NONE);
		grpDebug.setText("Debug");
		grpDebug.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpDebug.setLayout(new GridLayout(1, false));
		
		this.xrayOverlapCheck = new Button(grpDebug, SWT.CHECK);
		xrayOverlapCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		xrayOverlapCheck.setText(UILocale.get("OPTION_SYSTEM_XRAY_BG", "X-Ray View: OverLap images"));
		this.xrayOverlapCheck.setSelection(Settings.xrayOverlapScreen);
		
		this.xrayClipCheck = new Button(grpDebug, SWT.CHECK);
		xrayClipCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		xrayClipCheck.setText(UILocale.get("OPTION_SYSTEM_XRAY_CLIP", "X-Ray View: Show image clipping region"));
		this.xrayClipCheck.setSelection(Settings.xrayShowClipBorder);
		
		this.recordReleasedImagesCheck = new Button(grpDebug, SWT.CHECK);
		recordReleasedImagesCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		recordReleasedImagesCheck.setText(UILocale.get("OPTION_SYSTEM_RELEASED_IMG", "Memory View: Record released images"));
		this.recordReleasedImagesCheck.setSelection(Settings.recordReleasedImg);

		storeCreatedImagesBtn = new Button(grpDebug, SWT.CHECK);
		storeCreatedImagesBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		storeCreatedImagesBtn.setText("Memory View: Record created images");
		storeCreatedImagesBtn.setSelection(Settings.storeCreatedImages);

		countImagesSize = new Button(grpDebug, SWT.CHECK);
		countImagesSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		countImagesSize.setText("Memory View: Count images in objects total size");
		countImagesSize.setSelection(Settings.countImagesInObjectsSize);
		
		this.trackAllocationsCheck = new Button(grpDebug, SWT.CHECK);
		trackAllocationsCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		trackAllocationsCheck.setText(UILocale.get("OPTION_SYSTEM_TRACK_NEW", "Trace \"new/new[]...\" operations"));
		this.trackAllocationsCheck.setSelection(Settings.traceNew);
		
		this.trackMethodCallsCheck = new Button(grpDebug, SWT.CHECK);
		trackMethodCallsCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		trackMethodCallsCheck.setText(UILocale.get("OPTION_SYSTEM_TRACK_METHOD", "Trace method calls"));
		this.trackMethodCallsCheck.setSelection(Settings.traceCalls);

		traceHttpConnectionCheck = new Button(grpDebug, SWT.CHECK);
		traceHttpConnectionCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		traceHttpConnectionCheck.setText("Trace HTTP connections");
		traceHttpConnectionCheck.setSelection(Settings.traceHttp);

		grpMisc = new Group(systemComp, SWT.NONE);
		grpMisc.setText("Misc");
		grpMisc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpMisc.setLayout(new GridLayout(1, false));
		
		antiAliasBtn = new Button(grpMisc, SWT.CHECK);
		antiAliasBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		antiAliasBtn.setText(UILocale.get("OPTION_SYSTEM_AWT_ANTIALIASING", "AWT Smooth drawing"));
		antiAliasBtn.setSelection(Settings.awtAntiAliasing);

		passMinimizeBtn = new Button(grpMisc, SWT.CHECK);
		passMinimizeBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		passMinimizeBtn.setText("Pass window minimized state to MIDlet");
		passMinimizeBtn.setSelection(Settings.hideDisplayableOnMinimize);
	}

	private void setupSysFontComp() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalSpan = 2;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.CENTER;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.grabExcessHorizontalSpace = true;
		final GridData layoutData2 = new GridData();
		(layoutData2).horizontalAlignment = GridData.FILL;
		layoutData2.verticalAlignment = GridData.CENTER;
		final GridData layoutData5 = new GridData();
		(layoutData5).horizontalAlignment = GridData.FILL;
		layoutData5.verticalAlignment = GridData.CENTER;
		final GridData layoutData3 = new GridData();
		(layoutData3).horizontalAlignment = GridData.BEGINNING;
		layoutData3.verticalAlignment = GridData.CENTER;
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		this.fontScrollComp = new ScrolledComposite(this.tabFolder, SWT.V_SCROLL);
		this.fontComp = new Composite(fontScrollComp, SWT.NONE);
		fontComp.setLayout(layout);
		CLabel defaultFontLabel = new CLabel(this.fontComp, SWT.NONE);
		(defaultFontLabel).setText(UILocale.get("OPTION_FONT_DEFAULT_FONT", "Default Font:"));
		defaultFontLabel.setLayoutData(layoutData2);
		this.setupDefaultFontChoice();

		CLabel monospaceFontLabel = new CLabel(this.fontComp, SWT.NONE);
		(monospaceFontLabel).setText("Monospace Font:");
		monospaceFontLabel.setLayoutData(layoutData5);
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

		CLabel largeFontLabel = new CLabel(fontPreviewGroup, SWT.NONE);
		(largeFontLabel).setText(UILocale.get("OPTION_FONT_LARGE_SIZE", "Large Size:"));
		largeFontLabel.setLayoutData(layoutData3);
		this.largeFontSpinner = new Spinner(fontPreviewGroup, SWT.BORDER);
		largeFontSpinner.setMinimum(1);
		this.largeFontSpinner.setSelection(this.fontLargeSize);
		this.largeFontSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent modifyEvent) {
				refreshFontPreviews(1);
			}
		});
		this.createLargeFontPreview(fontPreviewGroup);
		CLabel mediumFontLabel = new CLabel(fontPreviewGroup, SWT.NONE);
		(mediumFontLabel).setText(UILocale.get("OPTION_FONT_MIDDLE_SIZE", "Medium Size:"));
		this.mediumFontSpinner = new Spinner(fontPreviewGroup, SWT.BORDER);
		mediumFontSpinner.setMinimum(1);
		this.mediumFontSpinner.setSelection(this.fontMediumSize);
		this.mediumFontSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent modifyEvent) {
				refreshFontPreviews(2);
			}
		});
		this.createMediumFontPreview(fontPreviewGroup);
		CLabel smallFontLabel = new CLabel(fontPreviewGroup, SWT.NONE);
		(smallFontLabel).setText(UILocale.get("OPTION_FONT_SMALL_SIZE", "Small Size:"));
		this.smallFontSpinner = new Spinner(fontPreviewGroup, SWT.BORDER);
		smallFontSpinner.setMinimum(1);
		this.smallFontSpinner.setSelection(this.fontSmallSize);
		this.smallFontSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent modifyEvent) {
				refreshFontPreviews(4);
			}
		});
		this.createSmallFontPreview(fontPreviewGroup);
		CLabel fontPreviewTextLabel = new CLabel(fontPreviewGroup, SWT.NONE);
		(fontPreviewTextLabel).setText(UILocale.get("OPTION_FONT_TEST_TEXT", "Test Text:"));
		this.fontPreviewText = new Text(fontPreviewGroup, SWT.BORDER);
		fontPreviewText.setText(UILocale.get("OPTION_FONT_TEST_TEXT_TXT", "This is an Example."));
		this.fontPreviewText.setLayoutData(layoutData);
		this.fontPreviewText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				refreshFontPreviews(7);
			}
		});


		final GridData layoutData4 = new GridData();
		(layoutData4).horizontalSpan = 3;
		antiAliasTextBtn = new Button(fontComp, SWT.CHECK);
		antiAliasTextBtn.setText(UILocale.get("OPTION_SYSTEM_TEXT_ANTIALIASING", "Text Antialiasing"));
		antiAliasTextBtn.setSelection(Settings.textAntiAliasing);
		antiAliasTextBtn.setLayoutData(layoutData4);

		fontScrollComp.setContent(fontComp);
		fontScrollComp.setExpandHorizontal(true);
	}

	private void setupDefaultFontChoice() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalSpan = 2;
		layoutData.verticalAlignment = GridData.CENTER;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		this.defaultFontCombo = new Combo(this.fontComp, SWT.READ_ONLY);
		defaultFontCombo.setLayoutData(layoutData);
		this.defaultFontCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				lastChangedFont = 0;
				refreshFontPreviews(7);
			}
		});
		final FontData[] fontList = Property.display.getFontList(null, true);
		final ArrayList<Comparable> list = new ArrayList<Comparable>();
//		list.add("Nokia");
//		list.add("Series 60");
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
		String fontName = (String) list.get(0);
		for (Comparable comparable : list) {
			this.defaultFontCombo.add((String) comparable);
			if (this.defaultFont.equalsIgnoreCase((String) comparable)) {
				fontName = (String) comparable;
			}
		}
		this.defaultFont = fontName;
		this.defaultFontCombo.setText(this.defaultFont);
	}

	private void setupMonoFontChoice() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalSpan = 2;
		layoutData.verticalAlignment = GridData.CENTER;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		this.monoFontCombo = new Combo(this.fontComp, SWT.READ_ONLY);
		monoFontCombo.setLayoutData(layoutData);
		this.monoFontCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				lastChangedFont = 1;
				refreshFontPreviews(7);
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
		String fontName = (String) list.get(0);
		for (Comparable comparable : list) {
			this.monoFontCombo.add((String) comparable);
			if (this.monospaceFont.equalsIgnoreCase((String) comparable)) {
				fontName = (String) comparable;
			}
		}
		this.monospaceFont = fontName;
		this.monoFontCombo.setText(this.monospaceFont);
	}

	private void createLargeFontPreview(Composite parent) {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = GridData.CENTER;
		this.largeFontCanvas = new Canvas(parent, SWT.NO_BACKGROUND | SWT.BORDER);
		largeFontCanvas.setLayoutData(layoutData);
		this.largeFontCanvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent paintEvent) {
				if (largeFontPreview == null) {
					refreshFontPreviews(1);
				}
				if (Settings.g2d == 0) {
					((ImageSWT) largeFontPreview).method12(paintEvent.gc, 0, 0);
					return;
				}
				if (Settings.g2d == 1) {
					((ImageAWT) largeFontPreview).copyToScreen(paintEvent.gc);
				}
			}
		});
	}

	private void createMediumFontPreview(Composite parent) {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = GridData.CENTER;
		this.mediumFontCanvas = new Canvas(parent, SWT.NO_BACKGROUND | SWT.BORDER);
		mediumFontCanvas.setLayoutData(layoutData);
		this.mediumFontCanvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent paintEvent) {
				if (mediumFontPreview == null) {
					refreshFontPreviews(2);
				}
				mediumFontPreview.copyToScreen(paintEvent.gc);
			}
		});
	}

	private void createSmallFontPreview(Composite parent) {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = GridData.CENTER;
		this.smallFontCanvas = new Canvas(parent, SWT.NO_BACKGROUND | SWT.BORDER);
		smallFontCanvas.setLayoutData(layoutData);
		this.smallFontCanvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent paintEvent) {
				if (smallFontPreview == null) {
					refreshFontPreviews(4);
				}
				if (Settings.g2d == 0) {
					((ImageSWT) smallFontPreview).copyToScreen(paintEvent.gc);
					return;
				}
				if (Settings.g2d == 1) {
					((ImageAWT) smallFontPreview).copyToScreen(paintEvent.gc);
				}
			}
		});
	}

	private void refreshFontPreviews(final int n) {
		if (!this.settingsShell.isVisible()) {
			return;
		}
		String fontName = lastChangedFont == 1 ? monoFontCombo.getText() : defaultFontCombo.getText();
		IFont font = null;
		if ((n & 0x1) != 0x0) {
			if (Settings.g2d == 0) {
				font = new FontSWT(fontName, this.largeFontSpinner.getSelection(), 0);
			} else if (Settings.g2d == 1) {
				font = new FontAWT(fontName, this.largeFontSpinner.getSelection(), 0, false);
			}
			final int x = this.largeFontCanvas.getSize().x;
			final int y = this.largeFontCanvas.getSize().y;
			this.largeFontPreview = Emulator.getEmulator().newImage(x, y, false, 0);
			final IGraphics2D graphics;
			(graphics = this.largeFontPreview.createGraphics()).setColor(65280, false);
			graphics.setFont(font);
			graphics.drawString(this.fontPreviewText.getText(), 1, y >> 1);
			this.largeFontCanvas.redraw();
		}
		if ((n & 0x2) != 0x0) {
			if (Settings.g2d == 0) {
				font = new FontSWT(fontName, this.mediumFontSpinner.getSelection(), 0);
			} else if (Settings.g2d == 1) {
				font = new FontAWT(fontName, this.mediumFontSpinner.getSelection(), 0, false);
			}
			final int x2 = this.mediumFontCanvas.getSize().x;
			final int y2 = this.mediumFontCanvas.getSize().y;
			this.mediumFontPreview = Emulator.getEmulator().newImage(x2, y2, false, 0);
			final IGraphics2D graphics2;
			(graphics2 = this.mediumFontPreview.createGraphics()).setColor(65280, false);
			graphics2.setFont(font);
			graphics2.drawString(this.fontPreviewText.getText(), 1, y2 >> 1);
			this.mediumFontCanvas.redraw();
		}
		if ((n & 0x4) != 0x0) {
			if (Settings.g2d == 0) {
				font = new FontSWT(fontName, this.smallFontSpinner.getSelection(), 0);
			} else if (Settings.g2d == 1) {
				font = new FontAWT(fontName, this.smallFontSpinner.getSelection(), 0, false);
			}
			final int x3 = this.smallFontCanvas.getSize().x;
			final int y3 = this.smallFontCanvas.getSize().y;
			this.smallFontPreview = Emulator.getEmulator().newImage(x3, y3, false, 0);
			final IGraphics2D graphics3;
			(graphics3 = this.smallFontPreview.createGraphics()).setColor(65280, false);
			graphics3.setFont(font);
			graphics3.drawString(this.fontPreviewText.getText(), 1, y3 >> 1);
			this.smallFontCanvas.redraw();
		}
	}

	private void setupRecordsComp() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = GridData.CENTER;
		layoutData.verticalAlignment = GridData.CENTER;
		final GridData layoutData2 = new GridData();
		(layoutData2).horizontalSpan = 3;
		layoutData2.verticalAlignment = GridData.CENTER;
		layoutData2.horizontalAlignment = GridData.FILL;
		final GridData layoutData3 = new GridData();
		(layoutData3).horizontalSpan = 3;
		layoutData3.verticalAlignment = GridData.FILL;
		layoutData3.grabExcessHorizontalSpace = true;
		layoutData3.grabExcessVerticalSpace = true;
		layoutData3.heightHint = 160;
		layoutData3.horizontalAlignment = GridData.FILL;
		final GridData layoutData4 = new GridData();
		(layoutData4).horizontalAlignment = GridData.FILL;
		layoutData4.grabExcessHorizontalSpace = true;
		layoutData4.verticalAlignment = GridData.CENTER;
		final GridData layoutData5 = new GridData();
		(layoutData5).horizontalAlignment = GridData.FILL;
		layoutData5.verticalAlignment = GridData.CENTER;
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		this.recordsComp = new Composite(this.tabFolder, SWT.NONE);
		recordsComp.setLayout(layout);
		CLabel rmsFolderLabel = new CLabel(this.recordsComp, SWT.NONE);
		(rmsFolderLabel).setText(UILocale.get("OPTION_RECORDS_RMS_FOLDER", "RMS Folder:"));
		rmsFolderLabel.setLayoutData(layoutData5);
		this.rmsFolderText = new Text(this.recordsComp, SWT.BORDER);
		rmsFolderText.setEditable(true);
		this.rmsFolderText.setEnabled(true);
		this.rmsFolderText.setLayoutData(layoutData4);
		this.rmsFolderText.setText(this.rmsFolder);
		Button browseRmsButton;
		browseRmsButton = new Button(this.recordsComp, SWT.FLAT | SWT.PUSH);
		browseRmsButton.setText("...");
		browseRmsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				Emulator.getEmulator().getScreen();
				EmulatorScreen.pauseStep();
				final DirectoryDialog directoryDialog;
				(directoryDialog = new DirectoryDialog(settingsShell)).setText(UILocale.get("OPTION_RECORDS_OPEN_DIRECTORY", "Select a directory for rms"));
				directoryDialog.setMessage(UILocale.get("OPTION_RECORDS_CHOOSE_DIR", "Choose a directory"));
				directoryDialog.setFilterPath(System.getProperty("user.dir"));
				final String open;
				if ((open = directoryDialog.open()) != null) {
					rmsFolderText.setText(open);
					refreshRmsTree();
				}
				((EmulatorScreen) Emulator.getEmulator().getScreen()).resumeStep();
			}
		});

		Button clearRecordsBtn;
		if (Emulator.midletClassName != null) {
			CLabel recordsLabel = new CLabel(this.recordsComp, SWT.NONE);
			(recordsLabel).setText(UILocale.get("OPTION_RECORDS_RMS_TEXT", "All Records in current midlet:"));
			recordsLabel.setLayoutData(layoutData2);
			this.legacyRecordsTable = new Table(this.recordsComp, SWT.CHECK | SWT.BORDER);
			legacyRecordsTable.setHeaderVisible(false);
			this.legacyRecordsTable.setLayoutData(layoutData3);
			this.legacyRecordsTable.setLinesVisible(true);
			Button selectAllRecordsButton = new Button(this.recordsComp, SWT.FLAT);
			(selectAllRecordsButton).setText(UILocale.get("OPTION_RECORDS_SELECT_ALL", "Select All"));
			selectAllRecordsButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent selectionEvent) {
					for (int i = 0; i < legacyRecordsTable.getItemCount(); ++i) {
						legacyRecordsTable.getItem(i).setChecked(true);
					}
				}
			});
			clearRecordsBtn = new Button(this.recordsComp, SWT.FLAT);
			clearRecordsBtn.setText(UILocale.get("OPTION_RECORDS_CLEAR_RECORD", "Clear Selected Records"));
			clearRecordsBtn.setLayoutData(layoutData);
			clearRecordsBtn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent selectionEvent) {
					// TODO: rms delete
					final String string = resolveRmsDirectory() + ".";
					for (int i = 0; i < legacyRecordsTable.getItemCount(); ++i) {
						final File file;
						if (legacyRecordsTable.getItem(i).getChecked()) {
							try {
								RecordStore.deleteRecordStore(legacyRecordsTable.getItem(i).getText().trim());
							} catch (Exception ignored) {}
						}
					}
					refreshRmsTree();
				}
			});
			new TableColumn(this.legacyRecordsTable, SWT.NONE).setWidth(200);
			this.refreshRmsTree();
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


			clearRecordsBtn = new Button(this.recordsComp, SWT.FLAT);
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

	private String resolveRmsDirectory() {
		String s = resolveUserRelativePath(this.rmsFolderText.getText().trim());
		final File file;
		if (!(file = new File(s)).exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		return s + "/";
	}

	private static String resolveUserRelativePath(String path) {
		if (path.startsWith(".")) {
			return Emulator.getUserPath() + path.substring(1);
		}
		if (path.startsWith("/") || path.startsWith("\\")) {
			return Emulator.getUserPath() + path;
		}
		return path;
	}

	private void refreshRmsTree() {
		if (rmsTree != null) {
			rmsTree.removeAll();
			String rootPath = resolveRmsDirectory();
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
		legacyRecordsTable.removeAll();
		String[] rms = RecordStore.listRecordStores();
		if (rms == null) return;
		for (String s : rms) {
			TableItem ti = new TableItem(this.legacyRecordsTable, SWT.NONE);
			ti.setText(0, s);
			ti.setChecked(false);
		}
	}

	private void setupNetworkComp() {
		this.networkComp = new Composite(this.tabFolder, SWT.NONE);
		networkComp.setLayout(new GridLayout());
		this.setupNetworkTabContent();
	}

	private void setupNetworkTabContent() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalSpan = 2;
		layoutData.verticalAlignment = GridData.CENTER;
		layoutData.horizontalAlignment = GridData.CENTER;
		final GridData layoutData2 = new GridData();
		(layoutData2).horizontalAlignment = GridData.FILL;
		layoutData2.grabExcessHorizontalSpace = true;
		layoutData2.verticalAlignment = GridData.CENTER;
		final GridData layoutData3 = new GridData();
		(layoutData3).horizontalAlignment = GridData.END;
		layoutData3.verticalAlignment = GridData.CENTER;
		final GridData layoutData4 = new GridData();
		(layoutData4).horizontalAlignment = GridData.FILL;
		layoutData4.grabExcessHorizontalSpace = true;
		layoutData4.verticalAlignment = GridData.CENTER;
		final GridData layoutData5 = new GridData();
		(layoutData5).horizontalAlignment = GridData.END;
		layoutData5.verticalAlignment = GridData.CENTER;
		final GridData layoutData6 = new GridData();
		(layoutData6).horizontalAlignment = GridData.END;
		layoutData6.verticalAlignment = GridData.CENTER;
		final GridData gridData = new GridData();
		(gridData).horizontalAlignment = GridData.FILL;
		gridData.widthHint = 40;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		final GridData gridData3 = new GridData();
		(gridData3).horizontalAlignment = GridData.FILL;
		gridData3.widthHint = 40;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.verticalAlignment = GridData.CENTER;
		final GridData layoutData7 = new GridData();
		(layoutData7).horizontalAlignment = GridData.FILL;
		layoutData7.widthHint = 50;
		layoutData7.grabExcessHorizontalSpace = true;
		layoutData7.verticalAlignment = GridData.CENTER;
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		final GridData layoutData8 = new GridData();
		(layoutData8).horizontalAlignment = GridData.FILL;
		layoutData8.grabExcessHorizontalSpace = true;
//		layoutData8.grabExcessVerticalSpace = true;
		layoutData8.verticalAlignment = GridData.BEGINNING;
		this.networkProxyGroup = new Group(this.networkComp, SWT.NONE);
		networkProxyGroup.setText(UILocale.get("OPTION_NETWORK_PROXY", "Proxy"));
		this.networkProxyGroup.setLayout(layout);
		this.networkProxyGroup.setLayoutData(layoutData8);
		CLabel proxyTypeLabel;
		proxyTypeLabel = new CLabel(this.networkProxyGroup, SWT.NONE);
		proxyTypeLabel.setText(UILocale.get("OPTION_NETWORK_PROXY_TYPE", "ProxyType:"));
		this.setupProxyCombo();
		CLabel proxySpacerLabel;
		proxySpacerLabel = new CLabel(this.networkProxyGroup, SWT.NONE);
		proxySpacerLabel.setText("");
		CLabel proxyHostLabel;
		proxyHostLabel = new CLabel(this.networkProxyGroup, SWT.NONE);
		proxyHostLabel.setText(UILocale.get("OPTION_NETWORK_HOST", "Host:"));
		proxyHostLabel.setLayoutData(layoutData6);
		this.proxyHostText = new Text(this.networkProxyGroup, SWT.BORDER);
		proxyHostText.setLayoutData(layoutData7);
		this.proxyHostText.setText(Settings.proxyHost);
		CLabel proxyPortLabel;
		proxyPortLabel = new CLabel(this.networkProxyGroup, SWT.NONE);
		proxyPortLabel.setText(UILocale.get("OPTION_NETWORK_PORT", "Port:"));
		this.proxyPortText = new Text(this.networkProxyGroup, SWT.BORDER);
		proxyPortText.setText(Settings.proxyPort);
		this.proxyPortText.setLayoutData(gridData);
		CLabel proxyUserLabel;
		proxyUserLabel = new CLabel(this.networkProxyGroup, SWT.NONE);
		proxyUserLabel.setText(UILocale.get("OPTION_NETWORK_USERNAME", "Username:"));
		proxyUserLabel.setLayoutData(layoutData5);
		this.proxyUserText = new Text(this.networkProxyGroup, SWT.BORDER);
		proxyUserText.setLayoutData(layoutData4);
		this.proxyUserText.setText(Settings.proxyUser);
		CLabel proxyPasswordLabel;
		proxyPasswordLabel = new CLabel(this.networkProxyGroup, SWT.NONE);
		proxyPasswordLabel.setText(UILocale.get("OPTION_NETWORK_PASSWORD", "Password:"));
		this.proxyPasswordText = new Text(this.networkProxyGroup, SWT.PASSWORD | SWT.BORDER);
		proxyPasswordText.setText(Settings.proxyPass);
		this.proxyPasswordText.setLayoutData(gridData3);
		CLabel proxyDomainLabel;
		proxyDomainLabel = new CLabel(this.networkProxyGroup, SWT.NONE);
		proxyDomainLabel.setText(UILocale.get("OPTION_NETWORK_DOMAIN", "Domain:"));
		proxyDomainLabel.setLayoutData(layoutData3);
		this.proxyDomainText = new Text(this.networkProxyGroup, SWT.BORDER);
		proxyDomainText.setLayoutData(layoutData2);
		this.proxyDomainText.setText(Settings.proxyDomain);
		this.proxyConnectButton = new Button(this.networkProxyGroup, SWT.FLAT);
		proxyConnectButton.setText(UILocale.get("OPTION_NETWORK_CONNECT", "Connect"));
		this.proxyConnectButton.setLayoutData(layoutData);
		this.proxyConnectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				testProxyConnection();
			}
		});
		Property aClass38_8311 = this;
		this.proxyTypeCombo.addModifyListener(new ModifyListener() {
			private final Property aClass38_831 = aClass38_8311;

			public final void modifyText(final ModifyEvent modifyEvent) {
				final boolean b = getProxyTypeCombo(this.aClass38_831).getSelectionIndex() != 0;
				getProxyHostField(this.aClass38_831).setEnabled(b);
				getProxyPortField(this.aClass38_831).setEnabled(b);
				getProxyUsernameField(this.aClass38_831).setEnabled(b);
				getProxyPasswordField(this.aClass38_831).setEnabled(b);
				getProxyDomainField(this.aClass38_831).setEnabled(b);
				getProxyConnectButton(this.aClass38_831).setEnabled(b);
				clearAppliedProxyProperties(this.aClass38_831);
			}
		});
		this.proxyTypeCombo.select(Settings.proxyType);
		new Label(networkProxyGroup, SWT.NONE);
		new Label(networkProxyGroup, SWT.NONE);
		new Label(networkProxyGroup, SWT.NONE);
		new Label(networkProxyGroup, SWT.NONE);

		final GridData gridData2 = new GridData();
		(gridData2).horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.BEGINNING;
		
		this.noNetworkBtn = new Button(this.networkComp, SWT.CHECK);
		noNetworkBtn.setText(UILocale.get("OPTION_COREAPI_NO_NETWORK", "Restrict network connections"));
		this.noNetworkBtn.setLayoutData(gridData2);
		this.noNetworkBtn.setSelection(Settings.networkNotAvailable);
	}

	private void setupProxyCombo() {
		final GridData layoutData = new GridData();
		(layoutData).horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalSpan = 2;
		layoutData.verticalAlignment = GridData.CENTER;
		this.proxyTypeCombo = new Combo(this.networkProxyGroup, SWT.READ_ONLY);
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

	private void testProxyConnection() {
		Emulator.getEmulator().getScreen();
		EmulatorScreen.pauseStep();
		if (this.proxyHostText.getText().trim().length() < 1) {
			final MessageBox messageBox;
			(messageBox = new MessageBox(this.settingsShell)).setText(UILocale.get("OPTION_NETWORK_PROXY_TEST", "Proxy Test"));
			messageBox.setMessage(UILocale.get("OPTION_NETWORK_PROXY_EMPTY", "Empty proxy host!"));
			messageBox.open();
		} else {
			final MessageBox messageBox2;
			(messageBox2 = new MessageBox(this.settingsShell)).setText(UILocale.get("OPTION_NETWORK_PROXY_TEST", "Proxy Test"));
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
		return settingsShell;
	}

	static Combo getControllerCombo(final Property property) {
		return property.controllerCombo;
	}

	static Text getLeftSoftKeyField(final Property property) {
		return property.leftSoftKeyBinding;
	}

	static String[] getKeyboardBindings() {
		return Property.keyboardBindings;
	}

	static Text getRightSoftKeyField(final Property property) {
		return property.rightSoftKeyBinding;
	}

	static Text getDigit1KeyField(final Property property) {
		return property.num1Binding;
	}

	static Text getDigit2KeyField(final Property property) {
		return property.num2Binding;
	}

	static Text getDigit3KeyField(final Property property) {
		return property.num3Binding;
	}

	static Text getDigit4KeyField(final Property property) {
		return property.num4Binding;
	}

	static Text getDigit5KeyField(final Property property) {
		return property.num5Binding;
	}

	static Text getDigit6KeyField(final Property property) {
		return property.num6Binding;
	}

	static Text getDigit7KeyField(final Property property) {
		return property.num7Binding;
	}

	static Text getDigit8KeyField(final Property property) {
		return property.num8Binding;
	}

	static Text getDigit9KeyField(final Property property) {
		return property.num9Binding;
	}

	static Text getDigit0KeyField(final Property property) {
		return property.num0Binding;
	}

	static Text getStarKeyField(final Property property) {
		return property.starBinding;
	}

	static Text getPoundKeyField(final Property property) {
		return property.poundBinding;
	}

	static Text getLeftDirectionKeyField(final Property property) {
		return property.leftBinding;
	}

	static Text getRightDirectionKeyField(final Property property) {
		return property.rightBinding;
	}

	static Text getUpDirectionKeyField(final Property property) {
		return property.upBinding;
	}

	static Text getDownDirectionKeyField(final Property property) {
		return property.downBinding;
	}

	static Text getCenterKeyField(final Property property) {
		return property.fireBinding;
	}

	static Combo getProxyTypeCombo(final Property property) {
		return property.proxyTypeCombo;
	}

	static Text getProxyHostField(final Property property) {
		return property.proxyHostText;
	}

	static Text getProxyPortField(final Property property) {
		return property.proxyPortText;
	}

	static Text getProxyUsernameField(final Property property) {
		return property.proxyUserText;
	}

	static Text getProxyPasswordField(final Property property) {
		return property.proxyPasswordText;
	}

	static Text getProxyDomainField(final Property property) {
		return property.proxyDomainText;
	}

	static Button getProxyConnectButton(final Property property) {
		return property.proxyConnectButton;
	}

	static void clearAppliedProxyProperties(final Property property) {
		removeProxyProperties();
	}

	static {
		Property.keyboardBindings = new String[19];
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
		private final String username;
		private final String password;

		MyAuthenticator(final Property property, final String username, final String password) {
			super();
			this.username = username;
			this.password = password;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(this.username, this.password.toCharArray());
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
