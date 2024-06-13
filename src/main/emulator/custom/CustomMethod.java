package emulator.custom;

import java.util.*;

import javax.microedition.media.Manager;

import emulator.debug.*;
import emulator.*;
import emulator.custom.h.MethodInfo;
import emulator.graphics3D.lwjgl.Emulator3D;

import java.io.*;

public class CustomMethod {
	private static long aLong13;
	private static long aLong17;
	private static Hashtable aHashtable14;
	private static Thread aThread15;
	//static StringBuffer aStringBuffer16;
	static String trackStr;
	private static BufferedWriter trackWriter;
	private static FileWriter fw;

	public CustomMethod() {
		super();
	}

	public static void gc() {
		++Profiler.gcCallCount;
	}

	public static void yield() throws InterruptedException {
//        Thread.sleep(1L);
		Thread.yield();
	}

	public static void sleep(long t) throws InterruptedException {
		Thread.sleep(t);
	}


	public static String getProperty(final String prop) {
		String res = System.getProperty(prop);
		boolean b = true;
		if (Settings.systemProperties != null && Settings.systemProperties.containsKey(prop)) {
			res = Settings.systemProperties.get(prop);
		} else {
			if (prop.equalsIgnoreCase("fileconn.dir.private")) {
				res = "file://root/private_" + Emulator.midletClassName.replace("\\", "_").replace("/", "_").replace(".", "_") + "/";
			} else if (prop.equalsIgnoreCase("user.name")) {
				res = "KEmulator";
			} else if (prop.equalsIgnoreCase("console.encoding")) {
				res = System.getProperty("file.encoding");
			} else if (prop.equalsIgnoreCase("com.nokia.mid.networkavailability")) {
				res = Settings.networkNotAvailable ? "unavailable" : "available";
			} else if (prop.equalsIgnoreCase("com.nokia.mid.batterylevel")) {
				res = "80";
			} else if (prop.startsWith("com.nokia.memoryram")) {
				b = false;
				Runtime r = Runtime.getRuntime();
				if (prop.equals("com.nokia.memoryramfree")) {
					res = String.valueOf(r.freeMemory() + r.maxMemory() - r.totalMemory());
				} else if (prop.equals("com.nokia.memoryramtotal")) {
					res = String.valueOf(r.maxMemory());
				}
			} else if (prop.equalsIgnoreCase("kemulator.threadtrace")) {
				b = false;
				res = getStackTrace(new Exception("Trace")).replace("\t", "").replace("\r", "");
			} else if (((prop.equalsIgnoreCase("com.nokia.mid.imei") || prop.equalsIgnoreCase("com.nokia.imei"))
					&& !Settings.protectedPackages.contains("com.nokia.mid")) ||
					prop.equalsIgnoreCase("device.imei") ||
					prop.equalsIgnoreCase("phone.imei") ||
					(prop.equalsIgnoreCase("com.sonyericsson.imei") && !Settings.protectedPackages.contains("com.sonyericsson")) ||
					(prop.equalsIgnoreCase("com.samsung.imei") && !Settings.protectedPackages.contains("com.samsung")) ||
					(prop.equalsIgnoreCase("com.siemens.IMEI") && !Settings.protectedPackages.contains("com.siemens"))
			) {
				res = Permission.askIMEI();
			} else if (prop.equalsIgnoreCase("kemulator.libvlc.supported")) {
				res = String.valueOf(Manager.isLibVlcSupported());
			} else if (prop.equalsIgnoreCase("com.nokia.pointer.number")) {
				b = false;
			} else if (prop.equalsIgnoreCase("microedition.locale")) {
				res = Settings.locale;
			} else if (prop.equalsIgnoreCase("microedition.encoding")) {
				res = Settings.fileEncoding;
			} else if (prop.equalsIgnoreCase("Platform")) {
				res = "";
			}
			// Hide properties of disabled APIs
			if(!Settings.protectedPackages.isEmpty()) {
				if (prop.startsWith("com.nokia") && Settings.protectedPackages.contains("com.nokia.mid")) {
					res = null;
				} else if (prop.startsWith("com.nokia.mid.ui") && Settings.protectedPackages.contains("com.nokia.mid.ui")) {
					res = null;
				} else if (prop.startsWith("wireless.messaging") && Settings.protectedPackages.contains("javax.wireless.messaging")) {
					res = null;
				} else if (prop.startsWith("microedition.m3g") && Settings.protectedPackages.contains("javax.microedition.m3g")) {
					res = null;
				} else if (prop.startsWith("microedition.sensor") && Settings.protectedPackages.contains("javax.microedition.sensor")) {
					res = null;
				} else if (prop.startsWith("microedition.amms") && Settings.protectedPackages.contains("javax.microedition.amms")) {
					res = null;
				} else if (prop.startsWith("microedition.media") && Settings.protectedPackages.contains("javax.microedition.media")) {
					res = null;
				} else if (prop.startsWith("microedition.pim") && Settings.protectedPackages.contains("javax.microedition.pim")) {
					res = null;
				} else if (prop.startsWith("bluetooth") && Settings.protectedPackages.contains("javax.microedition.bluetooth")) {
					res = null;
				} else if ((prop.startsWith("fileconn") || prop.startsWith("microdition.io.file")) && Settings.protectedPackages.contains("javax.microedition.io.file")) {
					res = null;
				} else if (prop.startsWith("org.pigler") && Settings.protectedPackages.contains("org.pigler")) {
					res = null;
				} else if (prop.startsWith("kemulator.rpc") && Settings.protectedPackages.contains("ru.nnproject.kemulator.rpc")) {
					res = null;
				} else if (prop.startsWith("kemulator.notificationapi") && Settings.protectedPackages.contains("ru.nnproject.kemulator.notificationapi")) {
					res = null;
				} else if (prop.startsWith("kemulator") && Settings.protectedPackages.contains("kemulator")) {
					res = null;
				} else if (Settings.protectedPackages.contains(prop)) {
					res = null;
				}
			}
		}
		if (b)
			Emulator.getEmulator().getLogStream().println("System.getProperty#" + prop + "=" + res);
		return res;
	}

	public static long currentTimeMillis() {
		++Profiler.currentTimeMillisCallCount;
		final long currentTimeMillis = System.currentTimeMillis();
		final long n2;
		final long n = ((n2 = Settings.speedModifier) < 0L) ? ((100L + n2 << 10) / 100L) : (n2 << 10);
		if (Settings.aLong1235 > 0L) {
			CustomMethod.aLong13 += n * (currentTimeMillis - CustomMethod.aLong17 - Settings.aLong1235) >> 10;
			CustomMethod.aLong17 = currentTimeMillis;
			Settings.aLong1235 = 0L;
		} else {
			CustomMethod.aLong13 += n * (currentTimeMillis - CustomMethod.aLong17) >> 10;
			CustomMethod.aLong17 = currentTimeMillis;
		}
		return CustomMethod.aLong13;
	}

	public static InputStream getResourceAsStream(final Object o, final String s) {
		return CustomJarResources.getResourceAsStream(o, s);
	}

	public static void showTrackInfo(final String s) {
		if (Settings.threadMethodTrack) {
			System.out.print(s);
			if (trackWriter != null) {
				try {
					trackWriter.append(s);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static int method16() {
		final Thread currentThread = Thread.currentThread();
		int n;
		if (CustomMethod.aThread15 != null && CustomMethod.aThread15 != currentThread) {
			n = 0;
			trackStr = "=====" + currentThread.toString() + "=====\n";
			showTrackInfo(trackStr);
		} else {
			final Integer value;
			n = (((value = (Integer) CustomMethod.aHashtable14.get(currentThread)) == null) ? 0 : value);
		}
		CustomMethod.aHashtable14.put(currentThread, n + 1);
		CustomMethod.aThread15 = currentThread;
		return n;
	}

	private static void method17() {
		final Thread currentThread = Thread.currentThread();
		final Object value;
		if ((value = CustomMethod.aHashtable14.get(currentThread)) != null) {
			CustomMethod.aHashtable14.put(currentThread, Math.max((Integer) value - 1, 0));
		}
	}

	public static void beginMethod(final String s) {
		if (h.aHashtable1061 == null) {
			h.aHashtable1061 = new Hashtable();
			h.method591();
		}
		final h.MethodInfo methodInfo;
		if ((methodInfo = (MethodInfo) h.aHashtable1061.get(s)) != null) {
			final int method16 = method16();
			++methodInfo.anInt1182;
			trackStr = "";
			for (int i = 0; i < method16; ++i) {
				trackStr += ("  ");
			}
			trackStr += s + "\n";
			showTrackInfo(trackStr);
			methodInfo.aLong1174 = System.currentTimeMillis();
		}
	}

	public static void endMethod(final String s) {
		final h.MethodInfo methodInfo;
		if ((methodInfo = (MethodInfo) h.aHashtable1061.get(s)) != null) {
			if (methodInfo.anInt1182 > 0) {
				methodInfo.aLong1179 += System.currentTimeMillis() - methodInfo.aLong1174;
				methodInfo.aFloat1175 = (float) methodInfo.aLong1179 / methodInfo.anInt1182;
			}
			method17();
		}
	}

	public static void exit(int i) {
		close();
		System.exit(i);
	}

	public static void close() {
		try {
			Emulator.getEventQueue().stop();
			Emulator3D.exit();
		} catch (Throwable ignored) {}
		if (trackWriter != null) {
			try {
				trackWriter.close();
				trackWriter = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getStackTrace(Throwable t) {
		String res = null;
		if (t != null) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				t.printStackTrace(new PrintStream(baos));
				res = baos.toString();
			} catch (Throwable t2) {
				t2.printStackTrace();
			}
		}
		return res;
	}

	static {
		CustomMethod.aHashtable14 = new Hashtable();
		try {
			fw = new FileWriter(Emulator.getUserPath() + "/track.txt", false);
			trackWriter = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		trackStr = "";
	}
}
