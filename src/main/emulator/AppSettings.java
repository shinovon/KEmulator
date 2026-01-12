package emulator;

import emulator.ui.IEmulatorFrontend;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

public class AppSettings {

	public static String iniSection;
	public static String jarHash;

	private static final Map<String, String> appProperties = new HashMap<>();

	// runtime only
	public static boolean softbankApi;
	public static boolean blackberryApi = false;
	public static String customTitle;
	public static boolean uei;
	public static boolean asmSkipDebug;

	public static int speedModifier = 1;
	public static int steps = -1;
	public static boolean xrayView;
	public static boolean xrayBuffer;

	// device
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
	public static boolean m3gThread;

	public static int m3gAA;
	public static int m3gTexFilter;
	public static int m3gMipmapping;

	public static final int APP_CONTROLLED = 0;

	public static final int AA_OFF = 1, AA_ON = 2;

	public static final int TEX_FILTER_NEAREST = 1, TEX_FILTER_LINEAR = 2;

	public static final int MIP_OFF = 1, MIP_LINEAR = 2, MIP_TRILINEAR = 3,
			MIP_ANISO_2 = 4, MIP_ANISO_4 = 5, MIP_ANISO_8 = 6, MIP_ANISO_16 = 7;

	// mascot
	public static boolean mascotNo2DMixing;
	public static boolean mascotIgnoreBackground;
	public static boolean mascotTextureFilter;
	public static boolean mascotBackgroundFilter;

	public static final Map<String, String> systemProperties = new HashMap<>();

	public static void init() {
		if (!applyPreset(devicePreset = Devices.getDefaultPreset(), true)) {
			// fallback in case user deletes presets
			screenWidth = 240;
			screenHeight = 320;

			leftSoftKey = -6;
			rightSoftKey = -7;
			fireKey = -5;
			upKey = -1;
			downKey = -2;
			leftKey = -3;
			rightKey = -4;
		}

		fontSmallSize = 12;
		fontMediumSize = 14;
		fontLargeSize = 16;

		locale = "en-US";
		fileEncoding = "ISO-8859-1";
		microeditionPlatform = "Nokia6700c-1/13.10";

		frameRate = 30;

		enableKeyRepeat = true;
		hasPointerEvents = true;

		ignoreFullScreen = true;
		asyncFlush = true;
		startAppOnResume = true;

		j2lStyleFpsLimit = false;
		motorolaSoftKeyFix = false;
		keyPressOnRepeat = false;
		synchronizeKeyEvents = false;

		patchYield = false;
		ignoreGc = true;
		patchSleep = false;
		ignoreSleep = false;
		applySpeedToSleep = false;

		m3gIgnoreOverwrite = false;
		m3gForcePerspectiveCorrection = false;
		m3gDisableLightClamp = false;
		m3gFlushImmediately = false;
		m3gThread = false;

		m3gAA = APP_CONTROLLED;
		m3gTexFilter = APP_CONTROLLED;
		m3gMipmapping = APP_CONTROLLED;

		mascotNo2DMixing = false;
		mascotIgnoreBackground = false;
		mascotTextureFilter = false;
		mascotBackgroundFilter = false;
	}

	public static void clear() {
		appProperties.clear();
	}

	public static int load(boolean reset) {
		Map<String, String> properties = appProperties;

		IEmulatorFrontend emulator = Emulator.getEmulator();
		boolean screenDetected = true;
		String jar = Emulator.midletJar;
		if (jar != null) {
			jar = new File(jar).getName();
		}
		detectScreen: {
			if (Emulator.startWidth != 0) {
				screenWidth = Emulator.startWidth;
				screenHeight = Emulator.startHeight;
				break detectScreen;
			}
			try {
				String s = emulator.getAppProperty("MIDxlet-ScreenSize");
				if (s == null) {
					s = emulator.getAppProperty("SEMC-Screen-Size");
				}
				if (s == null) {
					s = emulator.getAppProperty("Nokia-MIDlet-Original-Display-Size");
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
							if ("Y".equals(emulator.getAppProperty("MIDxlet-WideScreen"))) {
								screenWidth = h;
								screenHeight = w;
							} else {
								screenWidth = w;
								screenHeight = h;
							}
							break detectScreen;
						}
					}
				}
			} catch (Exception ignored) {}

			if (jar != null) {
				if (jar.contains("128x128")) {
					screenWidth = 128;
					screenHeight = 128;
					break detectScreen;
				}
				if (jar.contains("128x160")) {
					screenWidth = 128;
					screenHeight = 160;
					break detectScreen;
				}
				if (jar.contains("130x130")) {
					applyPreset("Siemens", false);
					screenWidth = 130;
					screenHeight = 130;
					break detectScreen;
				}
				if (jar.contains("132x176")) {
					applyPreset("Siemens", false);
					screenWidth = 132;
					screenHeight = 176;
					break detectScreen;
				}
				if (jar.contains("176x208")) {
					screenWidth = 176;
					screenHeight = 208;
					break detectScreen;
				}
				if (jar.contains("176x220")) {
					screenWidth = 176;
					screenHeight = 220;
					break detectScreen;
				}
				if (jar.contains("240x320")) {
					screenWidth = 240;
					screenHeight = 320;
					break detectScreen;
				}
				if (jar.contains("320x240")) {
					screenWidth = 320;
					screenHeight = 240;
					break detectScreen;
				}
				if (jar.contains("240x400")) {
					screenWidth = 240;
					screenHeight = 400;
					break detectScreen;
				}
				if (jar.contains("360x640")) {
					screenWidth = 360;
					screenHeight = 640;
					break detectScreen;
				}
				if (jar.contains("640x360")) {
					screenWidth = 640;
					screenHeight = 360;
					break detectScreen;
				}
			}
			screenDetected = false;
		}

		if (Emulator.doja) {
			if (screenDetected) {
				applyPreset("Sharp", false);
			} else {
				applyPreset(devicePreset = "120x160 (Sharp/DoCoMo - full screen)", true);
			}
			fileEncoding = "Shift_JIS";
			m3gThread = true;
		} else if (AppSettings.softbankApi) {
			// TODO
			applyPreset("Motorola", false);
			m3gThread = true;
		}

		String midletName = emulator.getAppProperty("MIDlet-Name");
		if (midletName != null) {
			if (midletName.contains("Angry Birds")) {
				m3gFlushImmediately = true;
			} else if (midletName.contains("勇闯恶魔城-Konami正版")) {
				j2lStyleFpsLimit = true;
			}
		}

//		if (Emulator.jadPath != null) {
//			set("JadPath", Emulator.jadPath);
//		}
//
//		if (Emulator.midletJar != null) {
//			set("JarPath", Emulator.midletJar);
//		}
		
		String s;
		if (AppSettings.uei) {
			s = "UEI";
		} else if (Settings.globalSettings) {
			s = "All";
		} else if (Emulator.midletJar == null) {
			s = "Classpath";
		} else {
			jarHash = getJarHash(Emulator.midletJar);
			s = jarHash + ':' + Emulator.midletJar;
		}
		iniSection = s;

		if (reset) return 0;

		if (!loadIni(false)) {
			return uei || !Settings.showAppSettingsOnStart ? 1 : 0;
		}

		if (properties.containsKey("DevicePreset")) {
			devicePreset = properties.get("DevicePreset");
		}

		if (Emulator.startWidth == 0) {
			if (properties.containsKey("ScreenWidth")) {
				screenWidth = Integer.parseInt(properties.get("ScreenWidth"));
			}
			if (properties.containsKey("ScreenHeight")) {
				screenHeight = Integer.parseInt(properties.get("ScreenHeight"));
			}
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

		if (properties.containsKey("FontSmallSize")) {
			fontSmallSize = Integer.parseInt(properties.get("FontSmallSize"));
		}

		if (properties.containsKey("FontMediumSize")) {
			fontMediumSize = Integer.parseInt(properties.get("FontMediumSize"));
		}

		if (properties.containsKey("FontLargeSize")) {
			fontLargeSize = Integer.parseInt(properties.get("FontLargeSize"));
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

		if (properties.containsKey("M3GIgnoreOverwrite")) {
			m3gIgnoreOverwrite = Boolean.parseBoolean(properties.get("M3GIgnoreOverwrite"));
		}

		if (properties.containsKey("M3GForcePerspectiveCorrection")) {
			m3gForcePerspectiveCorrection = Boolean.parseBoolean(properties.get("M3GForcePerspectiveCorrection"));
		}

		if (properties.containsKey("M3GDisableLightClamp")) {
			m3gDisableLightClamp = Boolean.parseBoolean(properties.get("M3GDisableLightClamp"));
		}

		if (properties.containsKey("M3GFlushImmediately")) {
			m3gFlushImmediately = Boolean.parseBoolean(properties.get("M3GFlushImmediately"));
		}

		if (properties.containsKey("M3GThread")) {
			m3gThread = Boolean.parseBoolean(properties.get("M3GThread"));
		}

		if (properties.containsKey("M3GAA")) {
			m3gAA = Integer.parseInt(properties.get("M3GAA"));
		}

		if (properties.containsKey("M3GTexFilter")) {
			m3gTexFilter = Integer.parseInt(properties.get("M3GTexFilter"));
		}

		if (properties.containsKey("M3GMipmapping")) {
			m3gMipmapping = Integer.parseInt(properties.get("M3GMipmapping"));
		}

		if (properties.containsKey("MascotNo2DMixing")) {
			mascotNo2DMixing = Boolean.parseBoolean(properties.get("MascotNo2DMixing"));
		}

		if (properties.containsKey("MascotIgnoreBackground")) {
			mascotIgnoreBackground = Boolean.parseBoolean(properties.get("MascotIgnoreBackground"));
		}

		if (properties.containsKey("MascotTextureFilter")) {
			mascotTextureFilter = Boolean.parseBoolean(properties.get("MascotTextureFilter"));
		}

		if (properties.containsKey("MascotBackgroundFilter")) {
			mascotBackgroundFilter = Boolean.parseBoolean(properties.get("MascotBackgroundFilter"));
		}

		if (properties.containsKey("SystemProperties")) {
			String[] sysProps = properties.get("SystemProperties").split("\n");
			for (String l : sysProps) {
				if ((l = l.trim()).isEmpty()) continue;
				int i = l.indexOf(':');
				if (i == -1) continue;
				String k = l.substring(0, i).trim();
				String v = l.substring(i + 1).trim();
				AppSettings.systemProperties.put(k, v);
			}
		}

		return 1;
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
		if (jarHash == null) return;

		AppSettings.set("FileEncoding", fileEncoding);
		AppSettings.set("DevicePreset", devicePreset);
		AppSettings.set("MIDPPlatform", microeditionPlatform);
		AppSettings.set("MIDPLocale", locale);

		AppSettings.set("ScreenWidth", screenWidth);
		AppSettings.set("ScreenHeight", screenHeight);

		AppSettings.set("KeyLeftSoft", leftSoftKey);
		AppSettings.set("KeyRightSoft", rightSoftKey);
		AppSettings.set("KeyUp", upKey);
		AppSettings.set("KeyDown", downKey);
		AppSettings.set("KeyLeft", leftKey);
		AppSettings.set("KeyRight", rightKey);

		AppSettings.set("FontLargeSize", fontLargeSize);
		AppSettings.set("FontMediumSize", fontMediumSize);
		AppSettings.set("FontSmallSize", fontSmallSize);

		AppSettings.set("EnableKeyRepeat", enableKeyRepeat);
		AppSettings.set("HasPointerEvents", hasPointerEvents);

		AppSettings.set("IgnoreFullScreenMode", ignoreFullScreen);
		AppSettings.set("FPSLimitJLStyle", j2lStyleFpsLimit);
		AppSettings.set("MotorolaSoftKeyFix", motorolaSoftKeyFix);
		AppSettings.set("KeyPressOnRepeat", keyPressOnRepeat);
		AppSettings.set("SynchronizeKeyEvents", synchronizeKeyEvents);
		AppSettings.set("AsyncFlush", asyncFlush);
		AppSettings.set("StartAppOnResume", startAppOnResume);

		AppSettings.set("M3GIgnoreOverwrite", m3gIgnoreOverwrite);
		AppSettings.set("M3GForcePerspectiveCorrection", m3gForcePerspectiveCorrection);
		AppSettings.set("M3GDisableLightClamp", m3gDisableLightClamp);
		AppSettings.set("M3GFlushImmediately", m3gFlushImmediately);
		AppSettings.set("M3GThread", m3gThread);

		AppSettings.set("M3GAA", AppSettings.m3gAA);
		AppSettings.set("M3GTexFilter", AppSettings.m3gTexFilter);
		AppSettings.set("M3GMipmapping", AppSettings.m3gMipmapping);

		AppSettings.set("MascotNo2DMixing", mascotNo2DMixing);
		AppSettings.set("MascotIgnoreBackground", mascotIgnoreBackground);
		AppSettings.set("MascotTextureFilter", mascotTextureFilter);
		AppSettings.set("MascotBackgroundFilter", mascotBackgroundFilter);

		StringBuilder sb = new StringBuilder();
		if (!AppSettings.systemProperties.isEmpty()) {
			for (String k : AppSettings.systemProperties.keySet()) {
				sb.append(k).append(':').append(AppSettings.systemProperties.get(k)).append('\n');
			}
			sb.setLength(sb.length() - 1);
			AppSettings.set("SystemProperties", sb.toString());
		}

		loadIni(true);
	}

	public static boolean applyPreset(String s, boolean screen) {
		if (s == null) {
			return false;
		}
		DevicePlatform p = Devices.getPlatform(s);
		if (p == null) {
			return false;
		}
		if (screen && p.exists("SCREEN_WIDTH")) {
			screenWidth = Integer.parseInt(p.getString("SCREEN_WIDTH"));
			screenHeight = Integer.parseInt(p.getString("SCREEN_HEIGHT"));
		}
		if (p.exists("PLATFORM")) {
			microeditionPlatform = p.getString("PLATFORM");
		}
		if (p.exists("ENCODING")) {
			fileEncoding = p.getString("ENCODING");
		}
		if (p.exists("FONT_SMALL_SIZE")) {
			fontSmallSize = Integer.parseInt(p.getString("FONT_SMALL_SIZE"));
		}
		if (p.exists("FONT_MEDIUM_SIZE")) {
			fontMediumSize = Integer.parseInt(p.getString("FONT_MEDIUM_SIZE"));
		}
		if (p.exists("FONT_LARGE_SIZE")) {
			fontLargeSize = Integer.parseInt(p.getString("FONT_LARGE_SIZE"));
		}
		if (p.exists("FORCE_FULLSCREEN")) {
			ignoreFullScreen = Boolean.parseBoolean(p.getString("FORCE_FULLSCREEN"));
		}
		leftSoftKey = Integer.parseInt(p.getString("KEY_S1"));
		rightSoftKey = Integer.parseInt(p.getString("KEY_S2"));
		fireKey = Integer.parseInt(p.getString("KEY_FIRE"));
		upKey = Integer.parseInt(p.getString("KEY_UP"));
		downKey = Integer.parseInt(p.getString("KEY_DOWN"));
		leftKey = Integer.parseInt(p.getString("KEY_LEFT"));
		rightKey = Integer.parseInt(p.getString("KEY_RIGHT"));
		return true;
	}

	private static boolean loadIni(boolean save) {
		Path midletsPath = getMidletsPath();
		String exactSection = '[' + iniSection + ']';
		boolean found = false;
		boolean notEmpty = false;

		if (!save && Files.notExists(midletsPath)) return false;

		try {
			BufferedWriter writer = null;
			Path tempPath = null;
			if (save) {
				tempPath = midletsPath.getParent().resolve("midlets.ini.tmp");
				writer = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
			}
			try (BufferedReader reader = Files.newBufferedReader(midletsPath, StandardCharsets.UTF_8)) {
				String line;
				String hashPrefix = "[" + jarHash + ":";
				String pathSuffix = ":" + Emulator.midletJar + "]";
				for (;;) {
					line = reader.readLine();
					if (line == null) break;
					notEmpty = true;

					if (!line.isEmpty() && line.charAt(0) == '[' && !found) {
						match: {
							if (line.equals(exactSection)) {
								found = true;
								break match;
							}
							if (uei || Emulator.midletJar == null) {
								break match;
							}
							if (line.startsWith(hashPrefix) || line.endsWith(pathSuffix)) {
								found = true;
								break match;
							}
						}
						if (found) {
							if (save) {
//								writer.write(exactSection);
								// TODO replace section?
								writer.write(line);
								writer.newLine();
								List<String> list = new ArrayList<>(appProperties.keySet());
								Collections.sort(list);
								for (String key : list) {
									writer.write(key);
									writer.write('=');
									writer.write(escape(appProperties.get(key)));
									writer.newLine();
								}
							}
							continue;
						}
					}

					if (save && !found) {
						writer.write(line);
						writer.newLine();
					}

					if (!found) continue;

					if (line.startsWith("[")) {
						if (save) {
							writer.newLine();
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

					if (save) continue;

					int sepIdx = line.indexOf('=');
					if (sepIdx == -1) {
						continue;
					}

					appProperties.put(line.substring(0, sepIdx), unescape(line.substring(sepIdx + 1)));
				}
			} finally {
				if (writer != null) {
					writer.close();
					Files.move(tempPath, midletsPath, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!found && save) {
			try {
				try (BufferedWriter writer = Files.newBufferedWriter(midletsPath, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
					if (notEmpty) writer.newLine();
					writer.write(exactSection);
					writer.newLine();
					List<String> list = new ArrayList<>(appProperties.keySet());
					Collections.sort(list);
					for (String key : list) {
						writer.write(key);
						writer.write('=');
						writer.write(escape(appProperties.get(key)));
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

	private static String escape(String s) {
		if (s == null) return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\\':
				sb.append("\\\\");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	private static String unescape(String s) {
		if (s == null) return "";
		return s.replace("\\n", "\n")
				.replace("\\r", "\r")
				.replace("\\t", "\t")
				.replace("\\\\", "\\");
	}

	private static Path getMidletsPath() {
		return Paths.get(Emulator.getUserPath(), "midlets.ini");
	}

	private static String getJarHash(String file) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

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
}
