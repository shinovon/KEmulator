package emulator;

import emulator.ui.IProperty;
import emulator.ui.swt.Property;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class AppSettings {

	public static String jarSha1;

	public static boolean softbankApi;
	public static boolean blackberryApi = false;
	public static String customTitle;
	public static boolean uei;
	public static boolean asmSkipDebug;

	public static int speedModifier = 1;
	public static int steps = -1;
	public static boolean xrayView;
	public static boolean xrayBuffer;

	public static String devicePreset;
	
	public static int screenWidth;
	public static int screenHeight;
	
	public static int leftSoftKey;
	public static int rightSoftKey;
	public static int fireKey;
	public static int upKey;
	public static int downKey;
	public static int leftKey;
	public static int rightKey;

	// font
	public static int fontSmallSize;
	public static int fontMediumSize;
	public static int fontLargeSize;

	public static String locale;
	public static String fileEncoding;
	public static String microeditionPlatform;

	public static int frameRate;

	public static boolean enableKeyRepeat;
	public static boolean hasPointerEvents;

	public static boolean ignoreFullScreen;
	public static boolean asyncFlush;
	public static boolean startAppOnResume;

	// tweaks
	public static boolean j2lStyleFpsLimit;
	public static boolean motorolaSoftKeyFix;
	public static boolean keyPressOnRepeat;
	public static boolean synchronizeKeyEvents;

	// patches
	public static boolean patchYield;
	public static boolean ignoreGc;
	public static boolean patchSleep;
	public static boolean ignoreSleep;
	public static boolean applySpeedToSleep;

	// m3g
	public static boolean m3gIgnoreOverwrite;
	public static boolean m3gForcePerspectiveCorrection;
	public static boolean m3gDisableLightClamp;
	public static boolean m3gFlushImmediately;
	public static int m3gAA;
	public static int m3gTexFilter;
	public static int m3gMipmapping;

	// mascot
	public static boolean mascotNo2DMixing;
	public static boolean mascotIgnoreBackground;
	public static boolean mascotTextureFilter;
	public static boolean mascotBackgroundFilter;

	public static final Map<String, String> systemProperties = new HashMap<String, String>();

	private static Map<String, String> appProperties;

	public static void init() {
		// copy values from Settings

		Property p = (Property) Emulator.getEmulator().getProperty();

		devicePreset = Emulator.deviceName;
		
		screenWidth = Integer.parseInt(p.getScreenWidth());
		screenHeight = Integer.parseInt(p.getScreenHeight());
		
		leftSoftKey = Integer.parseInt(p.getLsoft());
		rightSoftKey = Integer.parseInt(p.getRsoft());
		fireKey = Integer.parseInt(p.getFire());
		upKey = Integer.parseInt(p.getUp());
		downKey = Integer.parseInt(p.getDown());
		leftKey = Integer.parseInt(p.getLeft());
		rightKey = Integer.parseInt(p.getRight());

		fontSmallSize = p.getFontSmallSize();
		fontMediumSize = p.getFontMediumSize();
		fontLargeSize = p.getFontLargeSize();

		locale = Settings.locale;
		fileEncoding = Settings.fileEncoding;
		microeditionPlatform = Settings.microeditionPlatform;

		frameRate = Settings.frameRate;

		enableKeyRepeat = Settings.enableKeyRepeat;
		hasPointerEvents = Settings.hasPointerEvents;

		ignoreFullScreen = Settings.ignoreFullScreen;
		asyncFlush = Settings.asyncFlush;
		startAppOnResume = Settings.startAppOnResume;

		j2lStyleFpsLimit = Settings.j2lStyleFpsLimit;
		motorolaSoftKeyFix = Settings.motorolaSoftKeyFix;
		keyPressOnRepeat = Settings.keyPressOnRepeat;
		synchronizeKeyEvents = Settings.synchronizeKeyEvents;

		patchYield = Settings.patchYield;
		ignoreGc = Settings.ignoreGc;
		patchSleep = Settings.patchSleep;
		ignoreSleep = Settings.ignoreSleep;
		applySpeedToSleep = Settings.applySpeedToSleep;

		m3gIgnoreOverwrite = Settings.m3gIgnoreOverwrite;
		m3gForcePerspectiveCorrection = Settings.m3gForcePerspectiveCorrection;
		m3gDisableLightClamp = Settings.m3gDisableLightClamp;
		m3gFlushImmediately = Settings.m3gFlushImmediately;
		m3gAA = Settings.m3gAA;
		m3gTexFilter = Settings.m3gTexFilter;
		m3gMipmapping = Settings.m3gMipmapping;

		mascotNo2DMixing = Settings.mascotNo2DMixing;
		mascotIgnoreBackground = Settings.mascotIgnoreBackground;
		mascotTextureFilter = Settings.mascotTextureFilter;
		mascotBackgroundFilter = Settings.mascotBackgroundFilter;
	}

	public static void clear() {
		appProperties.clear();
	}

	public static int load() {
		Map<String, String> properties = appProperties = new HashMap<>();

		try {
			String s = Emulator.getEmulator().getAppProperty("MIDxlet-ScreenSize");
			if (s == null) {
				s = Emulator.getEmulator().getAppProperty("SEMC-Screen-Size");
			}
			if (s == null) {
				s = Emulator.getEmulator().getAppProperty("Nokia-MIDlet-Original-Display-Size");
			}
			if (s != null) {
				s = s.trim();
				char sep = ',';
				if (s.indexOf(sep) == -1) {
					sep = 'x';
				}
				if (s.indexOf(sep) != -1) {
					int w = Integer.parseInt(s.substring(0, s.indexOf(sep)).trim());
					int h = Integer.parseInt(s.substring(s.indexOf(sep) + 1).trim());

					if (w > 0 && h > 0) {
						if ("Y".equals(Emulator.getEmulator().getAppProperty("MIDxlet-WideScreen"))) {
							screenWidth = h;
							screenHeight = w;
						} else {
							screenWidth = w;
							screenHeight = h;
						}
					}
				}
			}
			System.out.println(screenWidth + "x" + screenHeight);
		} catch (Exception ignored) {}

		if (Emulator.doja) {
			fileEncoding = "Shift_JIS";
		} else if (AppSettings.softbankApi) {
			devicePreset = "400x240 (SoftBank - full screen)";
		}
		
		String s;
		if (AppSettings.uei) {
			s = "UEI";
		} else if (Emulator.midletJar == null) {
			return -1;
		} else {
			s = getJarSHA1(Emulator.midletJar);
		}
		if (s == null) {
			return -1;
		}
		jarSha1 = s;

		if (!load(false)) {
			return 0;
		}

		if (properties.containsKey("DevicePreset")) {
			devicePreset = properties.get("DevicePreset");
		}
		
		if (properties.containsKey("ScreenWidth")) {
			screenWidth = Integer.parseInt(properties.get("ScreenWidth"));
		}
		if (properties.containsKey("ScreenHeight")) {
			screenHeight = Integer.parseInt(properties.get("ScreenHeight"));
		}
		
		if (properties.containsKey("KeyLeftSoft")) {
			leftSoftKey = Integer.parseInt(properties.get("KeyLeftSoft"));
		}
		if (properties.containsKey("KeyRightSoft")) {
			rightSoftKey = Integer.parseInt(properties.get("KeyRightSoft"));
		}
		if (properties.containsKey("KeyFire")) {
			fireKey = Integer.parseInt(properties.get("KeyFire"));
		}
		if (properties.containsKey("KeyUp")) {
			upKey = Integer.parseInt(properties.get("KeyUp"));
		}
		if (properties.containsKey("KeyDown")) {
			downKey = Integer.parseInt(properties.get("KeyDown"));
		}
		if (properties.containsKey("KeyLeft")) {
			leftKey = Integer.parseInt(properties.get("KeyLeft"));
		}
		if (properties.containsKey("KeyFire")) {
			rightKey = Integer.parseInt(properties.get("KeyRight"));
		}

		if (properties.containsKey("MIDPLocale")) {
			locale = properties.get("MIDPLocale");
		}
		if (properties.containsKey("FileEncoding")) {
			fileEncoding = properties.get("FileEncoding");
		}
		if (properties.containsKey("MIDPPlatform")) {
			microeditionPlatform = properties.get("MIDPPlatform");
		}

		if (properties.containsKey("FrameRate")) {
			frameRate = Integer.parseInt(properties.get("FrameRate"));
		}

		if (properties.containsKey("EnableKeyRepeat")) {
			enableKeyRepeat = Boolean.parseBoolean(properties.get("EnableKeyRepeat"));
		}
		if (properties.containsKey("HasPointerEvents")) {
			hasPointerEvents = Boolean.parseBoolean(properties.get("HasPointerEvents"));
		}

		if (properties.containsKey("IgnoreFullScreenMode")) {
			ignoreFullScreen = Boolean.parseBoolean(properties.get("IgnoreFullScreenMode"));
		}
		if (properties.containsKey("AsyncFlush")) {
			asyncFlush = Boolean.parseBoolean(properties.get("AsyncFlush"));
		}
		if (properties.containsKey("StartAppOnResume")) {
			startAppOnResume = Boolean.parseBoolean(properties.get("StartAppOnResume"));
		}

		if (properties.containsKey("FPSLimitJLStyle")) {
			j2lStyleFpsLimit = Boolean.parseBoolean(properties.get("FPSLimitJLStyle"));
		}
		if (properties.containsKey("MotorolaSoftKeyFix")) {
			motorolaSoftKeyFix = Boolean.parseBoolean(properties.get("MotorolaSoftKeyFix"));
		}
		if (properties.containsKey("KeyPressOnRepeat")) {
			keyPressOnRepeat = Boolean.parseBoolean(properties.get("KeyPressOnRepeat"));
		}
		if (properties.containsKey("SynchronizeKeyEvents")) {
			synchronizeKeyEvents = Boolean.parseBoolean(properties.get("SynchronizeKeyEvents"));
		}

		if (properties.containsKey("PatchYield")) {
			patchYield = Boolean.parseBoolean(properties.get("PatchYield"));
		}
		if (properties.containsKey("IgnoreGC")) {
			ignoreGc = Boolean.parseBoolean(properties.get("IgnoreGC"));
		}
		if (properties.containsKey("PatchSleep")) {
			patchSleep = Boolean.parseBoolean(properties.get("PatchSleep"));
		}
		if (properties.containsKey("IgnoreSleep")) {
			ignoreSleep = Boolean.parseBoolean(properties.get("IgnoreSleep"));
		}
		if (properties.containsKey("ApplySpeedToSleep")) {
			applySpeedToSleep = Boolean.parseBoolean(properties.get("ApplySpeedToSleep"));
		}

		return 0;
//		return 1;
	}
	
	public static void set(String key, String value) {
		appProperties.put(key, value);
	}
	
	public static void set(String key, boolean value) {
		appProperties.put(key, Boolean.toString(value));
	}
	
	public static void set(String key, int value) {
		appProperties.put(key, Integer.toString(value));
	}

	public static void save() {
		if (jarSha1 == null) return;
		load(true);
	}

	private static boolean load(boolean save) {
		Path midletsPath = getMidletsPath();

		String jarSection = "[" + jarSha1 + "]";

		boolean found = false;
		try {
			BufferedWriter writer = null;
			Path tempPath = null;
			if (save) {
				tempPath = midletsPath.getParent().resolve("midlets.ini.tmp");
				writer = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
			}
			try (BufferedReader reader = Files.newBufferedReader(midletsPath, StandardCharsets.UTF_8)) {
				String line;
				for (;;) {
					line = reader.readLine();
					if (line == null) break;

					if (save && !found) {
						writer.write(line);
						writer.newLine();
					}

					if (line.equals(jarSection)) {
						found = true;
						if (save) {
							for (String key : appProperties.keySet()) {
								writer.write(key);
								writer.write('=');
								String value = appProperties.get(key);
								writer.write(value);
								writer.newLine();
							}
						}
						continue;
					}

					if (!found) continue;

					if (line.startsWith("[")) {
						if (save) {
							writer.write(line);
							writer.newLine();
							for (;;) {
								line = reader.readLine();
								if (line == null) break;

								writer.write(line);
								writer.newLine();
							}
						}
						break;
					}

					int sepIdx = line.indexOf('=');
					if (sepIdx == -1) {
						continue;
					}

					String key = line.substring(0, sepIdx);
					String value = line.substring(sepIdx + 1);

					appProperties.put(key, value);
				}
			} finally {
				if (writer != null) {
					writer.close();
					Files.deleteIfExists(midletsPath);
					Files.move(tempPath, midletsPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!found && save) {
			try {
				try (BufferedWriter writer = Files.newBufferedWriter(midletsPath, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
					writer.write(jarSection);
					writer.newLine();
					for (String key : appProperties.keySet()) {
						writer.write(key);
						writer.write('=');
						String value = appProperties.get(key);
						writer.write(value);
						writer.newLine();
					}
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		return found;
	}

	private static String getJarSHA1(String file) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");

			try (InputStream input = new FileInputStream(file)) {
				byte[] buffer = new byte[8192];
				int len;
				while ((len = input.read(buffer)) != -1) {
					md.update(buffer, 0, len);
				}
			}

			return Utils.getDigestString(md);
		} catch (Exception e) {
			return null;
		}
	}

	private static Path getMidletsPath() {
		return Paths.get(Emulator.getUserPath(), "midlets.ini");
	}
}
