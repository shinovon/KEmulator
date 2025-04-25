package emulator;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.github.sarxos.webcam.Webcam;
import emulator.custom.CustomClassLoader;
import emulator.custom.CustomMethod;
import emulator.graphics3D.IGraphics3D;
import emulator.media.EmulatorMIDI;
import emulator.media.MMFPlayer;
import emulator.ui.IEmulator;
import emulator.ui.swt.EmulatorImpl;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Screen;
import javax.microedition.media.Manager;
import javax.microedition.midlet.MIDlet;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Emulator implements Runnable {
	public static boolean debugBuild = true;
	public static String version = "2.19.2";
	public static String revision = "";
	public static final int numericVersion = 32;

	static EmulatorImpl emulatorimpl;
	private static MIDlet midlet;
	private static Canvas currentCanvas;
	private static Screen currentScreen;
	private static EventQueue eventQueue;
	private static KeyRecords record;
	public static Vector jarLibrarys;
	public static Vector jarClasses;
	public static String midletJar;
	public static String midletClassName;
	public static String classPath;
	public static String jadPath;
	public static String deviceName;
	public static String deviceFile;
	public static String[] commandLineArguments;
	public static emulator.custom.CustomClassLoader customClassLoader;
	public static String iconPath;

	protected static Object rpc;
	private static Thread rpcCallbackThread;
	public static long rpcStartTimestamp;
	public static long rpcEndTimestamp;
	public static String rpcState;
	public static String rpcDetails;
	public static int rpcPartySize;
	public static int rpcPartyMax;

	public static String httpUserAgent;
	private final static Thread backgroundThread;
	private static IEmulatorPlatform platform;

	public static final String os = System.getProperty("os.name").toLowerCase();
	public static final boolean win = os.startsWith("win");
	public static final boolean linux = os.contains("linux") || os.contains("nix");
	public static final boolean macos = os.startsWith("darwin") || os.startsWith("mac os");
	public static final boolean isPortable = checkIsPortable();
	private static Class<?> midletClass;
	private static boolean forked;
	private static boolean updated;

	private static void initRichPresence() {
		if (!Settings.rpc)
			return;
		final DiscordRPC rpc = (DiscordRPC) (Emulator.rpc = DiscordRPC.INSTANCE);
		DiscordEventHandlers handlers = new DiscordEventHandlers();
//        handlers.ready = new DiscordEventHandlers.OnReady() {
//            public void accept(DiscordUser user) {}
//        };
		rpc.Discord_Initialize("823522436444192818", handlers, true, "");
		DiscordRichPresence presence = new DiscordRichPresence();
		presence.startTimestamp = rpcStartTimestamp = System.currentTimeMillis() / 1000;
		presence.state = "No MIDlet loaded";
		rpc.Discord_UpdatePresence(presence);
		rpcCallbackThread = new Thread("KEmulator RPC-Callback-Handler") {
			public void run() {
				while (true) {
					rpc.Discord_RunCallbacks();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		rpcCallbackThread.start();
	}

	public static void updatePresence() {
		if (rpc == null)
			return;
		DiscordRPC rpc = (DiscordRPC) Emulator.rpc;
		DiscordRichPresence presence = new DiscordRichPresence();
		presence.state = rpcState;
		presence.details = rpcDetails;
		presence.startTimestamp = rpcStartTimestamp;
		presence.endTimestamp = rpcEndTimestamp;
		presence.partySize = rpcPartySize;
		presence.partyMax = rpcPartyMax;
		rpc.Discord_UpdatePresence(presence);
	}

	private Emulator() {
		super();
	}

	public static IEmulator getEmulator() {
		return Emulator.emulatorimpl;
	}

	public static KeyRecords getRobot() {
		return Emulator.record;
	}

	public static Canvas getCanvas() {
		return Emulator.currentCanvas;
	}

	public static void setCanvas(final Canvas canvas) {
		Emulator.currentCanvas = canvas;
		Emulator.emulatorimpl.getScreen().setCurrent(canvas);
	}

	public static Screen getScreen() {
		return Emulator.currentScreen;
	}

	public static void setScreen(final Screen screen) {
		Emulator.currentScreen = screen;
		Emulator.emulatorimpl.getScreen().setCurrent(screen);
	}

	public static MIDlet getMIDlet() {
		return Emulator.midlet;
	}

	public static void setMIDlet(final MIDlet midlet) {
		Emulator.midlet = midlet;
	}

	public static Display getCurrentDisplay() {
		return Display.getDisplay(Emulator.midlet);
	}

	public static EventQueue getEventQueue() {
		return Emulator.eventQueue;
	}

	public static emulator.custom.CustomClassLoader getCustomClassLoader() {
		return Emulator.customClassLoader;
	}

	public static void notifyDestroyed() {
		if (rpcCallbackThread != null)
			rpcCallbackThread.interrupt();
		MMFPlayer.close();
		Emulator.emulatorimpl.getProperty().saveProperties();
		if (Settings.autoGenJad) {
			generateJad();
			return;
		}
		saveTargetDevice();
	}

	public static void openFileExternally(final String fileName) {
		try {
			if (win) {
				// I intentionally use this only for windows.
				Desktop.getDesktop().open(new File(fileName));
				return;
			}
			if (macos) {
				Runtime.getRuntime().exec("open \"" + fileName + "\"");
				return;
			}
			if (linux) {
				// I have no idea how to open files if there is no XDG.
				// see https://github.com/ppy/osu/discussions/24499#discussioncomment-6698365 for fun.
				if (Files.isExecutable(Paths.get("/usr/bin/xdg-open")))
					Runtime.getRuntime().exec("/usr/bin/xdg-open \"" + fileName + "\"");
				else if (Files.isExecutable(Paths.get("/usr/bin/gedit")))
					// let's try gedit? it seems like the most well-known one.
					Runtime.getRuntime().exec("/usr/bin/gedit \"" + fileName + "\"");
				else
					getEmulator().getScreen().showMessage("Non XDG compliant systems are not supported.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		getEmulator().getScreen().showMessage("Failed to open file.");
	}

	private static void generateJad() {
		if (Emulator.midletJar == null) {
			return;
		}
		try {
			final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(Emulator.midletJar.substring(0, Emulator.midletJar.length() - 3) + "jad"), "UTF-8");
			final Enumeration<Object> keys = (Emulator.emulatorimpl.midletProps).keys();
			while (keys.hasMoreElements()) {
				final String s;
				if (!(s = (String) keys.nextElement()).equalsIgnoreCase("KEmu-Platform")) {
					outputStreamWriter.write(s + ": " + Emulator.emulatorimpl.midletProps.getProperty(s) + "\r\n");
				}
			}
			if (Emulator.emulatorimpl.midletProps.getProperty("MIDlet-Jar-URL") == null) {
				outputStreamWriter.write("MIDlet-Jar-URL: " + new File(Emulator.midletJar).getName() + "\r\n");
			}
			if (Emulator.emulatorimpl.midletProps.getProperty("MIDlet-Jar-Size") == null) {
				outputStreamWriter.write("MIDlet-Jar-Size: " + new File(Emulator.midletJar).length() + "\r\n");
			}
			outputStreamWriter.write("KEmu-Platform: " + Emulator.deviceName + "\r\n");
			outputStreamWriter.flush();
			outputStreamWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Properties getMidletProperties() {
		return Emulator.emulatorimpl.midletProps;
	}

	private static void saveTargetDevice() {
		if (Emulator.midletJar == null) {
			return;
		}
		if (Settings.writeKemCfg) {
			String propsPath = new File(Emulator.midletJar).getParentFile().getAbsolutePath() + File.separatorChar + "kemulator.cfg";
			try {
				String key = new File(Emulator.midletJar).getName();
				key = key.substring(0, key.lastIndexOf("."));
				final Properties properties = new Properties();
				if (new File(propsPath).exists()) {
					FileInputStream in = new FileInputStream(propsPath);
					try {
						properties.load(in);
					} finally {
						in.close();
					}
				}
				properties.setProperty(key, Emulator.deviceName);
				FileOutputStream out = new FileOutputStream(propsPath);
				try {
					properties.store(out, "KEmulator platforms");
				} finally {
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		final String propsPath = getUserPath() + File.separatorChar + "midlets.txt";
		try {
			String key = new File(Emulator.midletJar).getCanonicalPath();
			final Properties properties = new Properties();
			if (new File(propsPath).exists()) {
				FileInputStream in = new FileInputStream(propsPath);
				try {
					properties.load(in);
				} finally {
					in.close();
				}
			}
			StringBuilder s = new StringBuilder("!");

			s.append(Emulator.deviceName).append(';')
					.append("SCREEN_WIDTH:").append(Devices.getProperty("SCREEN_WIDTH")).append(';')
					.append("SCREEN_HEIGHT:").append(Devices.getProperty("SCREEN_HEIGHT")).append(';')
					.append("KEY_S1:").append(Devices.getProperty("KEY_S1")).append(';')
					.append("KEY_S2:").append(Devices.getProperty("KEY_S2")).append(';')
					.append("KEY_FIRE:").append(Devices.getProperty("KEY_FIRE")).append(';')
					.append("KEY_UP:").append(Devices.getProperty("KEY_UP")).append(';')
					.append("KEY_DOWN:").append(Devices.getProperty("KEY_DOWN")).append(';')
					.append("KEY_LEFT:").append(Devices.getProperty("KEY_LEFT")).append(';')
					.append("KEY_RIGHT:").append(Devices.getProperty("KEY_RIGHT"))
			;

			properties.setProperty(key, s.toString());
			FileOutputStream out = new FileOutputStream(propsPath);
			try {
				properties.store(out, "KEmulator MIDlets database");
			} finally {
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadTargetDevice() {
		final String property;
		if ((property = Emulator.emulatorimpl.midletProps.getProperty("KEmu-Platform")) != null) {
			tryToSetDevice(property);
			return;
		}
		if (Emulator.midletJar == null) {
			return;
		}
		String propsPath = getUserPath() + File.separatorChar + "midlets.txt";
		if (new File(propsPath).exists()) {
			try {
				String key = new File(Emulator.midletJar).getCanonicalPath();
				final Properties p = new Properties();
				FileInputStream in = new FileInputStream(propsPath);
				try {
					p.load(in);
				} finally {
					in.close();
				}
				final String device = p.getProperty(key, null);
				if (device != null) {
					tryToSetDevice(device);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		propsPath = new File(Emulator.midletJar).getParentFile().getAbsolutePath() + File.separatorChar + "kemulator.cfg";

		String key = new File(Emulator.midletJar).getName();
		key = key.substring(0, key.lastIndexOf("."));

		if (new File(propsPath).exists()) {
			try {
				final Properties p = new Properties();
				FileInputStream in = new FileInputStream(propsPath);
				try {
					p.load(in);
				} finally {
					in.close();
				}
				final String device = p.getProperty(key, null);
				if (device != null) {
					tryToSetDevice(device);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getTitle(String s) {
		if (Settings.customTitle != null) return Settings.customTitle;
		StringBuilder sb = new StringBuilder();
		sb.append(platform.getTitleName()).append(' ').append(version);
		if (s != null) {
			sb.append(" - ").append(s);
		} else if (midletJar != null && Emulator.emulatorimpl.midletProps != null) {
			sb.append(" - ").append(Emulator.emulatorimpl.getAppProperty("MIDlet-Name"));
		}
		if (Settings.uei) sb.append(" (UEI)");
		return sb.toString();
	}

	public static String getCmdVersionString() {
		return platform.getName() + " " + version;
	}

	public static String getInfoString() {
		return platform.getInfoString((version.startsWith("2.") ? "v" : "") + version);
	}

	public static String getAboutString() {
		return "KEmulator nnmod\n" + version + "\n\n\t" + UILocale.get("ABOUT_INFO_EMULATOR", "Mobile Game Emulator");
	}

	public static void getLibraries() {
		final File file;
		if ((file = new File(getAbsolutePath() + File.separatorChar + "libs")).exists() && file.isDirectory()) {
			final File[] listFiles = file.listFiles();
			for (int i = 0; i < listFiles.length; ++i) {
				final String absolutePath;
				if ((absolutePath = listFiles[i].getAbsolutePath()).endsWith(".jar")) {
					Emulator.jarLibrarys.add(absolutePath);
				}
			}
		}
	}

	public static String getJadPath() {
		File file;
		if (Emulator.jadPath != null) {
			file = new File(Emulator.jadPath);
		} else if (Emulator.midletJar != null) {
			file = new File(Emulator.midletJar.substring(0, Emulator.midletJar.length() - 3) + "jad");
		} else {
			return null;
		}
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		return null;
	}

	public static boolean getJarClasses() throws Exception {
		try {
			if (Emulator.midletClassName == null) {
				Properties props = null;
				File file2;
				File file;
				if (Emulator.jadPath != null) {
					file = (file2 = new File(Emulator.jadPath));
				} else {
					final StringBuffer sb = new StringBuffer();
					file = (file2 = new File(sb.append(Emulator.midletJar, 0, Emulator.midletJar.length() - 3).append("jad").toString()));
				}
				final File file3 = file2;
				if (file.exists()) {
					(props = new Properties()).load(new InputStreamReader(new FileInputStream(file3), "UTF-8"));
					final Enumeration<Object> keys = props.keys();
					while (keys.hasMoreElements()) {
						final String s = (String) keys.nextElement();
						props.put(s, props.getProperty(s));
					}
				}
				Emulator.emulatorimpl.getLogStream().println("Get classes from " + Emulator.midletJar);
				final ZipFile zipFile;
				final Enumeration entries = (zipFile = new ZipFile(Emulator.midletJar)).getEntries();
				while (entries.hasMoreElements()) {
					final ZipEntry zipEntry;
					if ((zipEntry = (ZipEntry) entries.nextElement()).getName().endsWith(".class")) {
						final String replace = zipEntry.getName().substring(0, zipEntry.getName().length() - 6).replace('/', '.');
						Emulator.jarClasses.add(replace);
						Emulator.emulatorimpl.getLogStream().println("Get class " + replace);
					}
				}
				if (props == null || !props.containsKey("MIDlet-1")) {
					try {
						final Attributes mainAttributes = zipFile.getManifest().getMainAttributes();
						props = new Properties();
						for (final Map.Entry<Object, Object> entry : mainAttributes.entrySet()) {
							props.put(entry.getKey().toString(), entry.getValue());
						}
						if (!props.containsKey("MIDlet-1")) throw new Exception();
					} catch (Exception ex2) {
						final InputStream inputStream;
						(inputStream = zipFile.getInputStream(zipFile.getEntry("META-INF/MANIFEST.MF"))).skip(3L);
						(props = new Properties()).load(new InputStreamReader(inputStream, "UTF-8"));
						inputStream.close();
						final Enumeration<Object> keys2 = props.keys();
						while (keys2.hasMoreElements()) {
							final String s2 = (String) keys2.nextElement();
							props.put(s2, props.getProperty(s2));
						}
					}
				}
				Emulator.emulatorimpl.midletProps = props;
				if (props.containsKey("MIDlet-2") && props.containsKey("MIDlet-1")) {
					// find all midlets and show choice window
					Vector<String> midletKeys = new Vector<String>();
					final Enumeration<Object> keys = props.keys();
					while (keys.hasMoreElements()) {
						final String s = (String) keys.nextElement();
						if (s.startsWith("MIDlet-")) {
							String num = s.substring("MIDlet-".length());
							try {
								Integer.parseInt(num);
								midletKeys.add(s);
							} catch (Exception ignored) {
							}
						}
					}
					if (midletKeys.size() != 0) {
						// must have to load device before screen is initialized
						loadTargetDevice();

						int n = emulatorimpl.getScreen().showMidletChoice(midletKeys);
						if (n == -1) {
							CustomMethod.close();
							System.exit(0);
							return false;
						}
						String c = props.getProperty(midletKeys.get(n));
						Emulator.midletClassName = c.substring(c.lastIndexOf(",") + 1).trim();
						return true;
					}
				}
				Emulator.midletClassName = props.getProperty("MIDlet-1");
				if (Emulator.midletClassName != null) {
					Emulator.midletClassName = Emulator.midletClassName.substring(Emulator.midletClassName.lastIndexOf(",") + 1).trim();
				}
			} else {
				if (Emulator.classPath == null) {
					return false;
				}
				final String[] split = Emulator.classPath.split(";");
				for (int i = 0; i < split.length; ++i) {
					Emulator.emulatorimpl.getLogStream().println("Get classes from " + split[i]);
					loadClassesFrom(new File(split[i]), null);
				}
				Properties aProperties1369 = null;
				final File file4;
				if (Emulator.jadPath != null && (file4 = new File(Emulator.jadPath)).exists()) {
					(aProperties1369 = new Properties()).load(new InputStreamReader(new FileInputStream(file4), "UTF-8"));
					final Enumeration keys3 = (aProperties1369).keys();
					while (keys3.hasMoreElements()) {
						final String s3 = (String) keys3.nextElement();
						aProperties1369.put(s3, aProperties1369.getProperty(s3));
					}
				}
				if (aProperties1369 == null) {
					aProperties1369 = new Properties();
				}
				Emulator.emulatorimpl.midletProps = aProperties1369;
			}
		} catch (Exception ex) {
			if (ex.toString().equalsIgnoreCase("java.io.IOException: Negative seek offset")) {
				ex.printStackTrace();
				Emulator.emulatorimpl.getScreen().showMessage(UILocale.get("LOAD_ZIP_ERROR", "Input file is not JAR or ZIP archive."), CustomMethod.getStackTrace(ex));
				return false;
			}
			throw ex;
		}
		loadTargetDevice();
		return true;
	}

	private static void loadClassesFrom(final File file, String s) {
		s = ((s != null) ? (s + ".") : "");
		final File[] listFiles = file.listFiles();
		for (int i = 0; i < listFiles.length; ++i) {
			final String string = s + listFiles[i].getName();
			if (listFiles[i].isDirectory()) {
				loadClassesFrom(listFiles[i], string);
			} else if (string.endsWith(".class")) {
				Emulator.jarClasses.add(string.substring(0, string.length() - 6));
				Emulator.emulatorimpl.getLogStream().println("Get class " + string.substring(0, string.length() - 6));
			}
		}
	}

	public static File getFileFromClassPath(final String s) {
		if (Emulator.classPath == null) {
			return null;
		}
		final String[] split = Emulator.classPath.split(";");
		for (int i = 0; i < split.length; ++i) {
			final File file;
			if ((file = new File(split[i] + File.separatorChar + s)).exists()) {
				return file;
			}
		}
		return null;
	}

	public static void setupMRUList() {
		if (Emulator.midletJar == null && Settings.recentJars[0].trim().equalsIgnoreCase("")) {
			return;
		}
		if (Settings.recentJars[0].trim().equalsIgnoreCase("")) {
			Settings.recentJars[0] = Emulator.midletJar;
			return;
		}
		if (Emulator.midletJar != null) {
			for (int i = 4; i > 0; --i) {
				if (Settings.recentJars[i].equalsIgnoreCase(Settings.recentJars[0])) {
					Settings.recentJars[i] = Settings.recentJars[1];
					Settings.recentJars[1] = Settings.recentJars[0];
					if (!Settings.recentJars[0].equalsIgnoreCase(Emulator.midletJar)) {
						Settings.recentJars[0] = Emulator.midletJar;
					}
					return;
				}
			}
		}
		int j;
		for (j = 4; j > 0; --j) {
			if (Settings.recentJars[j].equalsIgnoreCase(Settings.recentJars[0])) {
				Settings.recentJars[j] = Settings.recentJars[1];
				Settings.recentJars[1] = Settings.recentJars[0];
				break;
			}
		}
		if (j == 0) {
			for (int k2 = 4; k2 > 0; --k2) {
				Settings.recentJars[k2] = Settings.recentJars[k2 - 1];
			}
		}
		String[] array;
		int n;
		String midletJar;
		if (Emulator.midletJar == null) {
			array = Settings.recentJars;
			n = 0;
			midletJar = "";
		} else {
			array = Settings.recentJars;
			n = 0;
			midletJar = Emulator.midletJar;
		}
		array[n] = midletJar;
	}

	private static void setProperties() {
		System.setProperty("microedition.configuration", "CLDC-1.1");
		System.setProperty("microedition.profiles", "MIDP-2.0");
		if (platform.supportsM3G())
			System.setProperty("microedition.m3g.version", "1.1");
		System.setProperty("microedition.encoding", Settings.fileEncoding);
		if (System.getProperty("microedition.locale") == null) {
			System.setProperty("microedition.locale", Settings.locale);
		}
		if (System.getProperty("microedition.platform") == null) {
			String plat = Emulator.deviceName;
			DevicePlatform c = Devices.getPlatform(Emulator.deviceName);
			if (c.exists("OVERRIDE_NAME")) {
				plat = c.getString("OVERRIDE_NAME");
			}
			if (c.exists("R")) {
				plat += "-" + c.getString("R");
			}

			Emulator.httpUserAgent = plat + " (Java/" + System.getProperty("java.version") + "; KEmulator/" + version + ")";

			if (!c.exists("PLATFORM_VERSION2") && c.exists("PLATFORM_VERSION")) {
				plat += "/" + c.getString("PLATFORM_VERSION");
			}
			if (c.exists("CUSTOM_UA")) {
				Emulator.httpUserAgent = c.getString("CUSTOM_UA");
			}
			if (c.exists("PLATFORM_VERSION2") && c.exists("PLATFORM_VERSION")) {
				plat += "/" + c.getString("PLATFORM_VERSION2");
			}
//                if (c.exists("SW_PLATFORM")) {
//                    plat += "sw_platform=" + c.getString("SW_PLATFORM");
//                }
//                if (c.exists("SW_PLATFORM_VERSION")) {
//                    plat += ";sw_platform_version=" + c.getString("SW_PLATFORM_VERSION");
//                }
			System.setProperty("microedition.platform", plat);
		}
		System.setProperty("microedition.media.version", "1.0");
		System.setProperty("supports.mixing", "true");
		System.setProperty("supports.audio.capture", "false");
		System.setProperty("supports.video.capture", "false");
		System.setProperty("supports.photo.capture", "false");
		System.setProperty("supports.recording", "false");
		System.setProperty("microedition.io.file.FileConnection.version", "1.0");
		System.setProperty("microedition.pim.version", "1.0");
		System.setProperty("bluetooth.api.version", "1.0");
		if (System.getProperty("wireless.messaging.sms.smsc") == null) {
			System.setProperty("wireless.messaging.sms.smsc", "+8613800010000");
		}
		if (System.getProperty("wireless.messaging.mms.mmsc") == null) {
			System.setProperty("wireless.messaging.mms.mmsc", "http://mms.kemulator.com");
		}
		System.setProperty("microedition.sensor.version", "1.0");
		System.setProperty("kemulator.notificationapi.version", "1.0");
		System.setProperty("fileconn.dir.photos", "file:///root/photos/");
		System.setProperty("fileconn.dir.music", "file:///root/music/");
		System.setProperty("fileconn.dir.private", "file://root/private/");
		System.setProperty("fileconn.dir.memorycard", "file:///root/e/");
		System.setProperty("microedition.hostname", "localhost");
		if (System.getProperty("wireless.messaging.version") == null) {
			System.setProperty("wireless.messaging.version", "1.0");
		}
		System.setProperty("kemulator.mod.version", version);
		System.setProperty("kemulator.mod.versionint", "" + numericVersion);
		System.setProperty("com.nokia.mid.ui.softnotification", "true");
		System.setProperty("com.nokia.mid.ui.version", "1.4");
		System.setProperty("com.nokia.mid.ui.customfontsize", "true");
		System.setProperty("com.nokia.pointer.number", "0");
		System.setProperty("kemulator.hwid", getHWID());
		System.setProperty("microedition.amms.version", "1.0");
		System.setProperty("org.pigler.api.version", "1.4-kemulator");
		if (platform.isX64()) System.setProperty("kemulator.x64", "true");
		System.setProperty("kemulator.rpc.version", "1.0");

		if (!platform.isX64() && System.getProperty("kemulator.disablecamera") == null && !Settings.disableCamera) {
			try {
				Webcam w = Webcam.getDefault();
				if (w != null) {
					System.setProperty("supports.video.capture", "true");
					System.setProperty("supports.photo.capture", "true");
					System.setProperty("supports.mediacapabilities", "camera");
					System.setProperty("camera.orientations", "devcam0:inwards");
					Dimension d = w.getViewSize();
					System.setProperty("camera.resolutions", "devcam0:" + d.width + "x" + d.height);
				}
			} catch (Throwable ignored) {
			}
		}

		try {
			Settings.softbankApi = Emulator.emulatorimpl.getAppProperty("MIDxlet-API") != null;
		} catch (Exception ignored) {
		}
	}

	private static String getHWID() {
		try {
			String s = System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(s.getBytes());
			StringBuffer sb = new StringBuffer();
			byte[] b = md.digest();
			for (byte aByteData : b) {
				String hex = Integer.toHexString(0xff & aByteData);
				if (hex.length() == 1) sb.append('0');
				sb.append(hex);
			}

			return sb.toString();
		} catch (Exception e) {
			return "null";
		}
	}

	public static void main(final String[] commandLineArguments) {
		try {
			platform = ((IEmulatorPlatform) Class.forName("emulator.EmulatorPlatform").newInstance());
		} catch (Exception e) {
			System.out.println("Platform class not found");
			return;
		}
		try {
			Manifest versionManifest = new Manifest(Emulator.class.getResourceAsStream("/META-INF/version.mf"));
			final Attributes mainAttributes = versionManifest.getMainAttributes();
			for (Map.Entry entry : mainAttributes.entrySet()) {
				if (entry.getKey().toString().equals("Git-Revision")) {
					String s = entry.getValue().toString();
					if (s.length() == 0) break;
					if (debugBuild) version = s;
					revision = s;
					break;
				}
			}
		} catch (Exception ignored) {
		}
		String arch = System.getProperty("os.arch");
		if (!platform.isX64() && (!arch.contains("86") || !win)) {
			JOptionPane.showMessageDialog(new JPanel(), "Can't run this version of KEmulator nnmod on this architecture (" + arch + "). Try multi-platform version instead.");
			System.exit(0);
			return;
		}
		try {
			try {
				platform.loadLibraries();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JPanel(), "Failed to load libraries: " + e.getMessage());
				System.exit(0);
				return;
			}
			EmulatorMIDI.initDevices();
			Emulator.commandLineArguments = commandLineArguments;
			UILocale.initLocale();
			parseLaunchArgs(commandLineArguments); //
			Emulator.emulatorimpl = new EmulatorImpl();
			parseLaunchArgs(commandLineArguments);
			// Force m3g engine to LWJGL in x64 build
			if (platform.isX64()) Settings.micro3d = Settings.g3d = 1;

			// Restart with additional arguments required for specific os or java version
			if (!(forked || Settings.uei) && (macos || isJava9())) {
				loadGame(null, false);
				return;
			}

			platform.load3D();
			Controllers.refresh(true);
			Emulator.emulatorimpl.getLogStream().stdout(getCmdVersionString() + " Running on "
					+ System.getProperty("os.name") + ' ' + System.getProperty("os.arch")
					+ " (" + System.getProperty("os.version") + "), Java: "
					+ System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
			Devices.load(Emulator.deviceFile);
			tryToSetDevice(Emulator.deviceName);
			setupMRUList();
			initRichPresence();

			if (Settings.autoUpdate == 0) {
				Settings.autoUpdate = updated ? 2 : Emulator.emulatorimpl.getScreen().showUpdateDialog(0);
			}
			backgroundThread.start();

			if (Emulator.midletClassName == null && Emulator.midletJar == null) {
				Emulator.emulatorimpl.getScreen().startEmpty();
				EmulatorImpl.dispose();
				System.exit(0);
				return;
			}
			Emulator.record = new KeyRecords();
			getLibraries();
			try {
				if (!getJarClasses()) {
					Emulator.emulatorimpl.getScreen().showMessage(UILocale.get("LOAD_CLASSES_ERROR", "Get Classes Failed!! Plz check the input jar or classpath."));
					System.exit(1);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Emulator.emulatorimpl.getScreen().showMessage(UILocale.get("LOAD_CLASSES_ERROR", "Get Classes Failed!! Plz check the input jar or classpath."), CustomMethod.getStackTrace(e));
				System.exit(1);
				return;
			}
			InputStream inputStream = null;
			final String appProperty;
			if ((appProperty = Emulator.emulatorimpl.getAppProperty("MIDlet-Icon")) != null) {
				iconPath = appProperty;
				inputStream = emulator.custom.CustomJarResources.getResourceAsStream(appProperty);
			} else {
				final String appProperty2;
				if ((appProperty2 = Emulator.emulatorimpl.getAppProperty("MIDlet-1")) != null) {
					try {
						String s = appProperty2.split(",")[1].trim();
						iconPath = s;
						inputStream = emulator.custom.CustomJarResources.getResourceAsStream(s);
					} catch (Exception ex3) {
						ex3.printStackTrace();
					}
				}
			}
			if (Emulator.emulatorimpl.getAppProperty("MIDlet-Name") != null) {
				Emulator.rpcState = (Settings.uei ? "Debugging " : "Running ") + Emulator.emulatorimpl.getAppProperty("MIDlet-Name");
				Emulator.rpcDetails = Settings.uei ? "UEI" : new File(midletJar).getName();
				updatePresence();
			}
			Emulator.emulatorimpl.getScreen().setWindowIcon(inputStream);
			setProperties();
			if (Emulator.midletClassName == null) {
				Emulator.emulatorimpl.getScreen().showMessage(UILocale.get("LOAD_MIDLET_ERROR", "Can not find MIDlet class. Plz check jad or use -midlet param."));
				System.exit(1);
				return;
			}
			getEmulator().getLogStream().stdout("Launch MIDlet class: " + Emulator.midletClassName);
			try {
				midletClass = Class.forName(Emulator.midletClassName = Emulator.midletClassName.replace('/', '.'), true, Emulator.customClassLoader);
			} catch (Throwable e) {
				e.printStackTrace();
				Emulator.emulatorimpl.getScreen().showMessage(UILocale.get("FAIL_LAUNCH_MIDLET", "Fail to launch the MIDlet class:") + " " + Emulator.midletClassName, CustomMethod.getStackTrace(e));
				System.exit(1);
				return;
			}
			Emulator.eventQueue = new EventQueue();
			new Thread(new Emulator()).start();
			Emulator.emulatorimpl.getScreen().startWithMidlet();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			EmulatorImpl.dispose();
		} catch (Throwable ignored) {
		}
		System.exit(0);
	}

	private static void tryToSetDevice(String deviceName) {
		String[][] c = null;
		if (deviceName.startsWith("!")) {
			deviceName = deviceName.substring(1);
			String[] a = deviceName.split(";");
			c = new String[a.length][2];
			int idx = 0;
			for (String s : a) {
				int i = s.indexOf(':');
				if (i == -1) {
					deviceName = s;
					continue;
				}
				c[idx][0] = s.substring(0, i);
				c[idx++][1] = s.substring(i + 1);
			}
		}
		Emulator.deviceName = deviceName;
		if (!Devices.setPlatform(Emulator.deviceName)) {
			Devices.setPlatform(Emulator.deviceName = "SonyEricssonK800");
		}
		Emulator.emulatorimpl.getProperty().setCustomProperties();
		if (c != null) {
			for (String[] p : c) {
				if (p == null || p[0] == null) continue;
				Devices.setProperty(p[0], p[1]);
			}
		}
		Devices.writeProperties();
		Emulator.emulatorimpl.getProperty().updateCustomProperties();
		Emulator.emulatorimpl.getProperty().resetDeviceName();
		KeyMapping.init();
	}

	static boolean parseLaunchArgs(final String[] array) {
		if (array.length < 1) {
			return false;
		}
		if (array.length == 1 && (array[0].endsWith(".jar") || array[0].endsWith(".jad"))) {
			String path = array[0];
			if (path.endsWith(".jar")) {
				try {
					Emulator.midletJar = new File(path).getCanonicalPath();
				} catch (Exception e) {
					Emulator.midletJar = path;
				}
			} else {
				Emulator.jadPath = path;
				Emulator.midletJar = getMidletJarUrl(path);
			}
			return true;
		}
		for (int i = 0; i < array.length; ++i) {
			String key = array[i].trim();
			if (key.startsWith("-")) {
				key = key.substring(1).toLowerCase();
			} else if (i == array.length - 1 && (array[0].endsWith(".jar") || array[0].endsWith(".jad"))) {
				String path = array[0];
				if (path.endsWith(".jar")) {
					try {
						Emulator.midletJar = new File(path).getCanonicalPath();
					} catch (Exception e) {
						Emulator.midletJar = path;
					}
				} else {
					Emulator.jadPath = path;
					Emulator.midletJar = getMidletJarUrl(path);
				}
			}
			String value = null;
			if (i < array.length - 1) {
				value = array[i + 1].trim();
				if (!value.startsWith("-")) i++;
				else value = null;
			}
			if (key.equals("awt")) {
				Settings.g2d = 1;
			} else if (key.equals("swt")) {
				Settings.g2d = 0;
			} else if (key.equals("lwj")) {
				Settings.g3d = 1;
			} else if (key.equals("swerve")) {
				Settings.g3d = 0;
			} else if (key.equals("mascotgl")) {
				Settings.micro3d = 1;
			} else if (key.equals("mascotdll")) {
				Settings.micro3d = 0;
			} else if (key.equalsIgnoreCase("log")) {
				Settings.showLogFrame = true;
			} else if (key.equalsIgnoreCase("uei")) {
				Settings.uei = true;
			} else if (key.equalsIgnoreCase("s")) {
				forked = true;
			} else if (key.equalsIgnoreCase("updated")) {
				updated = true;
			} else if (value != null) {
				if (key.equalsIgnoreCase("jar")) {
					try {
						Emulator.midletJar = new File(value).getCanonicalPath();
					} catch (Exception e) {
						Emulator.midletJar = value;
					}
				} else if (key.equalsIgnoreCase("midlet")) {
					Emulator.midletClassName = array[i];
				} else if (key.equalsIgnoreCase("cp")) {
					Emulator.classPath = value;
				} else if (key.equalsIgnoreCase("jad")) {
					Emulator.jadPath = value;
				} else if (key.equalsIgnoreCase("rec")) {
					Settings.recordedKeysFile = value;
					Settings.playingRecordedKeys = new File(value).exists();
				} else if (key.equalsIgnoreCase("device")) {
					Emulator.deviceName = value;
				} else if (key.equalsIgnoreCase("devicefile")) {
					Emulator.deviceFile = value;
				} else if (key.equalsIgnoreCase("fontname")) {
					getEmulator().getProperty().setDefaultFontName(value);
				} else if (key.equalsIgnoreCase("fontsmall")) {
					getEmulator().getProperty().setFontSmallSize(Integer.parseInt(value));
				} else if (key.equalsIgnoreCase("fontmedium")) {
					getEmulator().getProperty().getFontMediumSize(Integer.parseInt(value));
				} else if (key.equalsIgnoreCase("fontlarge")) {
					getEmulator().getProperty().getFontLargeSize(Integer.parseInt(value));
				} else if (key.equalsIgnoreCase("key")) {
					KeyMapping.keyArg(value);
				}
			}
		}
		return true;
	}

	public static String getAbsoluteFile() {
		String s = System.getProperty("user.dir");
		if (new File(s + File.separatorChar + "KEmulator.jar").exists() || new File(s + File.separatorChar + "sensorsimulator.jar").exists()) {
			return s + File.separatorChar + "KEmulator.jar";
		}
		s = Emulator.class.getProtectionDomain().getCodeSource().getLocation().getFile().substring(1);
		if (s.endsWith("bin/"))
			s = s.substring(0, s.length() - 4);
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (Exception ex) {
			return s;
		}
	}

	public static String getAbsolutePath() {
		String s = System.getProperty("user.dir");
		if (new File(s + File.separatorChar + "KEmulator.jar").exists() || new File(s + File.separatorChar + "sensorsimulator.jar").exists()) {
			return s;
		}
		File file = new File(getAbsoluteFile()).getParentFile();
		try {
			s = file.getCanonicalPath();
		} catch (IOException e) {
			s = file.getAbsolutePath();
		}
		return s;
	}

	private static boolean checkIsPortable() {
		String path = getAbsoluteFile();
		// if kemu jar is read-only, it's probably somewhere in system folders.
		if (Files.exists(Paths.get(path)) && !Files.isWritable(Paths.get(path)))
			return false;

		// we may live in system folders but are writable (container, misconfiguration, etc.)
		if (linux) {
			// we installed from package
			if (path.startsWith("/opt/") || path.startsWith("/usr/") || path.startsWith("/bin/"))
				return false;
			// we live in ~.local/bin
			if (path.contains("/home/") && path.contains("/.local/"))
				return false;
			//TODO flatpack/snap?

			return true;
		}
		if (win) {
			// installer will write to registry path to installed jar. Let's check it.
			try {
				Process regRequest = Runtime.getRuntime().exec("reg query \"HKEY_LOCAL_MACHINE\\Software\\nnproject\\KEmulator\" /v JarInstalledPath");
				StringBuilder sw = new StringBuilder();
				try (InputStream is = regRequest.getInputStream()) {
					int c;
					while ((c = is.read()) != -1)
						sw.append((char) c);
				}
				String output = sw.toString();
				int i = output.indexOf("REG_SZ");
				if (i != -1) {
					String pathFromReg = output.substring(i + 6).trim();
					if (Files.isSameFile(Paths.get(pathFromReg), Paths.get(path)))
						return false; // we are running "installed" instance
				}
			} catch (IOException e) {
				// ignore
			}

			// installed into appdata
			if (path.contains("\\Users\\") && path.contains("\\AppData\\Local\\"))
				return false;

			// all other cases will be handled by writeablility check above.
			return true;
		}

		// non-".app" program management on macos is messy so doing nothing
		return true;
	}

	public static String getUserPath() {
		installed:
		{
			if (!isPortable) {
				String s;
				if (linux) {
					s = System.getenv("HOME") + "/.local/share/KEmulator/";
				} else if (win) {
					s = System.getenv("APPDATA");
					if (s == null)
						break installed;
					s += "\\KEmulator";
				} else {
					break installed;
				}
				Path p = Paths.get(s);
				if (Files.exists(p) && Files.isDirectory(p))
					return s;

				try {
					Files.createDirectories(p);
				} catch (IOException e) {
					break installed;
				}

				return s;
			}
		}
		return getAbsolutePath();
	}

	public static void loadGame(String s, boolean b) {
		loadGame(s, Settings.g2d, Settings.g3d, Settings.micro3d, b);
	}

	public static void loadGame(final String s, final int engine2d, final int engine3d, int mascotEngine, final boolean b) {
		ArrayList<String> cmd = new ArrayList<String>();
		getEmulator().getLogStream().println(s == null ? "Restarting" : ("loadGame: " + s));
		String javahome = System.getProperty("java.home");
		cmd.add(javahome == null || javahome.length() < 1 ? "java" : (javahome + (!win ? "/bin/java" : "/bin/java.exe")));
		cmd.add("-cp");
		cmd.add(System.getProperty("java.class.path"));
		cmd.add("-Xmx" + Settings.xmx + "M");

		// start with debug server
		if (Settings.jdwpDebug) {
			cmd.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=" + Settings.debugPort);
		}

		// linux fixes
		cmd.add("-Djava.library.path=" + getAbsolutePath());
		if ("false".equals(System.getProperty("sun.java3d.d3d"))) {
			cmd.add("-Dsun.java3d.d3d=false");
		}

		// mac cocoa fix
		if (os.startsWith("darwin") || os.startsWith("mac os")) {
			cmd.add("-XstartOnFirstThread");
		}


		if (debugBuild) {
			File f = new File(getAbsolutePath() + "/../eclipse/KEmulator_base/agent.jar");
			if (f.exists()) {
				cmd.add("-javaagent:" + f);
			} else {
				cmd.add("-javaagent:" + getAbsoluteFile());
			}
		} else {
			cmd.add("-javaagent:" + getAbsoluteFile());
		}

		if (isJava9()) {
			cmd.add("--add-opens");
			cmd.add("java.base/java.lang=ALL-UNNAMED");
			cmd.add("--add-opens");
			cmd.add("java.base/java.lang.reflect=ALL-UNNAMED");
			cmd.add("--add-opens");
			cmd.add("java.base/java.lang.ref=ALL-UNNAMED");
			cmd.add("--add-opens");
			cmd.add("java.base/java.io=ALL-UNNAMED");
			cmd.add("--add-opens");
			cmd.add("java.base/java.util=ALL-UNNAMED");
			if (isJava17())
				cmd.add("--enable-native-access=ALL-UNNAMED");
		}

		cmd.add("emulator.Emulator");
		if (s == null) {
			for (String a : Emulator.commandLineArguments) {
				if (a.equals("-swt") || a.equals("-awt")
						|| a.equals("-swerve") || a.equals("-lwj"))
					continue;
				cmd.add(a);
			}
		} else if (s.endsWith(".jad")) {
			cmd.add("-jad");
			cmd.add(s);
			cmd.add("-jar");
			cmd.add(getMidletJarUrl(s));
		} else {
			cmd.add("-jar");
			cmd.add(s);
		}
		if (Settings.recordedKeysFile != null) {
			cmd.add("-rec");
			cmd.add(Settings.recordedKeysFile);
		}

		cmd.add(engine2d == 0 ? "-swt" : "-awt");
		cmd.add(engine3d == 0 ? "-swerve" : "-lwj");
		cmd.add(mascotEngine == 0 ? "-mascotdll" : "-mascotgl");

		cmd.add("-s");

		getEmulator().disposeSubWindows();
		notifyDestroyed();
		try {
			ProcessBuilder newKem = new ProcessBuilder().directory(new File(getAbsolutePath())).command(cmd).inheritIO();
			// something inside SWT libs setting this to X11 on dual-server systems.
			// GTK is clever enough to decide itself, so clearing it
			newKem.environment().remove("GDK_BACKEND");
			newKem.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		CustomMethod.close();
		System.exit(0);
	}

	static String getMidletJarUrl(String jadPath) {
		try {
			File file = new File(jadPath);
			if (file.exists()) {
				Properties properties = new Properties();
				properties.load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
				return file.getParent() + File.separator + properties.getProperty("MIDlet-Jar-URL");
			}
		} catch (Exception ignored) {
		}
		return null;
	}

	public static void AntiCrack(final Exception ex) {
	}

	static {
		Emulator.customClassLoader = new CustomClassLoader(ClassLoader.getSystemClassLoader());
		Emulator.jarLibrarys = new Vector();
		Emulator.jarClasses = new Vector();
		Emulator.deviceName = "SonyEricssonK800";
		Emulator.deviceFile = "/res/plat";
		backgroundThread = new Thread(new Runnable() {
			public void run() {
				Manager.checkLibVlcSupport();
				if (!updated && !Settings.uei && Settings.autoUpdate == 2 && Updater.checkUpdate() == Updater.STATE_UPDATE_AVAILABLE) {
					EmulatorImpl.asyncExec(new Runnable() {
						public void run() {
							Emulator.emulatorimpl.getScreen().showUpdateDialog(1);
						}
					});
				}
			}
		});
	}

	public static String getMidletName() {
		String x = emulatorimpl.getAppProperty("MIDlet-1").split(",")[0];
		if (x.startsWith(" "))
			x = x.substring(1);
		if (x.endsWith(" "))
			x = x.substring(0, x.length() - 1);
		return x;
	}

	public static boolean isX64() {
		return platform.isX64();
	}

	public static IEmulatorPlatform getPlatform() {
		return platform;
	}

	public static IGraphics3D getGraphics3D() {
		return platform.getGraphics3D();
	}

	public static int getJavaVersionMajor() {
		try {
			return Integer.parseInt(System.getProperty("java.version").split("\\.")[0]);
		} catch (Throwable e) {
			return 0;
		}
	}

	public static boolean isJava9() {
		try {
			return getJavaVersionMajor() >= 9;
		} catch (Throwable e) {
			return false;
		}
	}

	public static boolean isJava17() {
		try {
			return getJavaVersionMajor() >= 17;
		} catch (Throwable e) {
			return false;
		}
	}

	public void run() {
		try {
			midletClass.newInstance();
		} catch (Throwable e) {
			e.printStackTrace();
			Emulator.eventQueue.stop();
			EmulatorImpl.syncExec(new Runnable() {
				public void run() {
					Emulator.emulatorimpl.getScreen().showMessage(UILocale.get("FAIL_LAUNCH_MIDLET", "Fail to launch the MIDlet class:") + " " + Emulator.midletClassName, CustomMethod.getStackTrace(e));
				}
			});
			return;
		}
		Emulator.emulatorimpl.getClassWatcher().fillClassList();
		Emulator.emulatorimpl.getProfiler().fillClassList();
		Emulator.eventQueue.queue(EventQueue.EVENT_START);
	}
}