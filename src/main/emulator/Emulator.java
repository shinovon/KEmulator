package emulator;

import java.awt.Dimension;
import java.io.*;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Screen;
import javax.microedition.media.Manager;
import javax.microedition.midlet.MIDlet;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import emulator.graphics3D.IGraphics3D;
import emulator.media.EmulatorMIDI;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.github.sarxos.webcam.Webcam;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import emulator.custom.CustomClassLoader;
import emulator.custom.CustomMethod;
import emulator.media.MMFPlayer;
import emulator.ui.IEmulator;
import emulator.ui.swt.EmulatorImpl;

public class Emulator {
    public static boolean debugBuild = true;
    public static String version = "2.16";
    public static final int numericVersion = 16;

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
    private final static Thread vlcCheckerThread;
    private static IEmulatorPlatform platform;
    public static boolean installed;

    public static final String os = System.getProperty("os.name").toLowerCase();
    public static final boolean win = os.startsWith("win");
    public static final boolean linux = os.contains("linux") || os.contains("nix");

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
        DiscordRPC rpc = (DiscordRPC) Emulator.rpc;
        if (rpc == null)
            return;
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.state = rpcState;
        presence.details = rpcDetails;
        presence.startTimestamp = rpcStartTimestamp;
        presence.endTimestamp = rpcEndTimestamp;
        presence.partySize = rpcPartySize;
        presence.partyMax = rpcPartyMax;
        rpc.Discord_UpdatePresence(presence);
    }

    public Emulator() {
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
    }

    public static Screen getScreen() {
        return Emulator.currentScreen;
    }

    public static void setScreen(final Screen screen) {
        Emulator.currentScreen = screen;
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
            method280();
            return;
        }
        saveTargetDevice();
    }

    private static void method280() {
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
        if(Settings.writeKemCfg) {
            String propsPath = new File(Emulator.midletJar).getParentFile().getAbsolutePath() + "/kemulator.cfg";
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
        final String propsPath = getUserPath() + "/midlets.txt";
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
        String propsPath = getUserPath() + "/midlets.txt";
        if(new File(propsPath).exists()) {
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        propsPath = new File(Emulator.midletJar).getParentFile().getAbsolutePath() + "/kemulator.cfg";

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

    public static String getTitleVersionString() {
        return platform.getTitleName() + " " + version;
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
        if ((file = new File(getAbsolutePath() + "/libs")).exists() && file.isDirectory()) {
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
        File file = null;
        Label_0068:
        {
            File file2;
            if (Emulator.jadPath != null) {
                file2 = new File(Emulator.jadPath);
            } else {
                if (Emulator.midletJar == null) {
                    break Label_0068;
                }
                file2 = new File(Emulator.midletJar.substring(0, Emulator.midletJar.length() - 3) + "jad");
            }
            file = file2;
        }
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }

    public static boolean getJarClasses() {
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
                if (props == null) {
                    try {
                        final Attributes mainAttributes = zipFile.getManifest().getMainAttributes();
                        props = new Properties();
                        for (final Map.Entry<Object, Object> entry : mainAttributes.entrySet()) {
                            props.put(entry.getKey().toString(), entry.getValue());
                        }
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
                                int n = Integer.parseInt(num);
                                String v = props.getProperty(s);
                                v = v.substring(0, v.indexOf(","));
                                midletKeys.add(n + " (" + v + ")");
                            } catch (Exception ignored) {
                            }
                        }
                    }
                    if (midletKeys.size() > 0) {
                        String[] arr = midletKeys.toArray(new String[0]);
                        String c = (String) JOptionPane.showInputDialog(null, "Choose MIDlet to run", "KEmulator", JOptionPane.QUESTION_MESSAGE, null, arr, arr[0]);
                        if (c == null) {
                            CustomMethod.close();
                            System.exit(0);
                            return false;
                        }
                        c = "MIDlet-" + c.substring(0, c.indexOf(' '));
                        c = props.getProperty(c);
                        Emulator.midletClassName =
                                c.substring(c.lastIndexOf(",") + 1)
                                        .trim();
                    }
                } else {
                    Emulator.midletClassName = props.getProperty("MIDlet-1");
                    if (Emulator.midletClassName != null) {
                        Emulator.midletClassName = Emulator.midletClassName.substring(Emulator.midletClassName.lastIndexOf(",") + 1).trim();
                    }
                }
            } else {
                if (Emulator.classPath == null) {
                    return false;
                }
                final String[] split = Emulator.classPath.split(";");
                for (int i = 0; i < split.length; ++i) {
                    Emulator.emulatorimpl.getLogStream().println("Get classes from " + split[i]);
                    method281(new File(split[i]), null);
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
            ex.printStackTrace();
            if (ex.toString().equalsIgnoreCase("java.io.IOException: Negative seek offset")) {
                Emulator.emulatorimpl.getEmulatorScreen().showMessage(UILocale.get("LOAD_ZIP_ERROR", "Input file is not JAR or ZIP archive."));
            }
            return false;
        }
        loadTargetDevice();
        return true;
    }

    private static void method281(final File file, String s) {
        s = ((s != null) ? (s + ".") : "");
        final File[] listFiles = file.listFiles();
        for (int i = 0; i < listFiles.length; ++i) {
            final String string = s + listFiles[i].getName();
            if (listFiles[i].isDirectory()) {
                method281(listFiles[i], string);
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
            if ((file = new File(split[i] + "/" + s)).exists()) {
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
        System.setProperty("org.pigler.api.version", "1.2-kemulator");
        if (platform.isX64()) System.setProperty("kemulator.x64", "true");
        System.setProperty("kemulator.rpc.version", "1.0");

        if(!platform.isX64())
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
        } catch (Throwable ignored) {}

        try {
            String midlet = Emulator.emulatorimpl.getAppProperty("MIDlet-Name");
            String midletVendor = Emulator.emulatorimpl.getAppProperty("MIDlet-Vendor");
            if (midlet != null) {
                // TODO this is stupid
                if (midlet.equalsIgnoreCase("bounce tales")) {
                    Settings.fpsGame = 1;
                } else if (midlet.equalsIgnoreCase("micro counter strike")) {
                    Settings.fpsGame = 2;
                } else if (midlet.equalsIgnoreCase("quantum") || (midletVendor != null && midletVendor.toLowerCase().contains("ae-mods"))) {
                    Settings.fpsGame = 3;
                }
            }
        } catch (Exception ignored) {}
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
            if (debugBuild) {
                Manifest versionManifest = new Manifest(Emulator.class.getResourceAsStream("/META-INF/version.mf"));
                final Attributes mainAttributes = versionManifest.getMainAttributes();
                for (Map.Entry entry : mainAttributes.entrySet()) {
                    if (entry.getKey().toString().equals("Git-Revision")) {
                        String s = entry.getValue().toString();
                        if (s.length() > 0) version = s;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String arch = System.getProperty("os.arch");
        if (!platform.isX64() && !arch.equals("x86")) {
            JOptionPane.showMessageDialog(new JPanel(), "Can't run this version of KEmulator nnmod on this architecture (" + arch + "). Try x64 version instead.");
            System.exit(0);
            return;
        }
        platform.loadLibraries();
        EmulatorMIDI.initDevices();
        Emulator.commandLineArguments = commandLineArguments;
        UILocale.initLocale();
        parseLaunchArgs(commandLineArguments); //
        Emulator.emulatorimpl = new EmulatorImpl();
        parseLaunchArgs(commandLineArguments);
        // Force m3g engine to LWJGL in x64 build
        if(platform.isX64()) Settings.micro3d = Settings.g3d = 1;
        platform.load3D();
        vlcCheckerThread.start();
        Controllers.refresh(true);
        Emulator.emulatorimpl.getLogStream().stdout(getCmdVersionString() + " Running on " +
                System.getProperty("os.name") + " (" + System.getProperty("os.version") + "), Java: " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
        Devices.load(Emulator.deviceFile);
        tryToSetDevice(Emulator.deviceName);
        setupMRUList();
        initRichPresence();
        if (Emulator.midletClassName == null && Emulator.midletJar == null) {
            Emulator.emulatorimpl.getEmulatorScreen().start(false);
            EmulatorImpl.dispose();
            System.exit(0);
            return;
        }
        Emulator.record = new KeyRecords();
        getLibraries();
        if (!getJarClasses()) {
            Emulator.emulatorimpl.getEmulatorScreen().showMessage(UILocale.get("LOAD_CLASSES_ERROR", "Get Classes Failed!! Plz check the input jar or classpath."));
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
        String jar = midletJar;
        if (jar.contains("/"))
            jar = jar.substring(jar.lastIndexOf("/") + 1);
        if (jar.contains("\\"))
            jar = jar.substring(jar.lastIndexOf("\\") + 1);
        if (Emulator.emulatorimpl.getAppProperty("MIDlet-Name") != null) {
            Emulator.rpcState = (Settings.uei ? "Debugging " : "Running ") + Emulator.emulatorimpl.getAppProperty("MIDlet-Name");
            Emulator.rpcDetails = Settings.uei ? "UEI" : jar;
            updatePresence();
        }
        Emulator.emulatorimpl.getEmulatorScreen().setWindowIcon(inputStream);
        setProperties();
        if (Emulator.midletClassName == null) {
            Emulator.emulatorimpl.getEmulatorScreen().showMessage(UILocale.get("LOAD_MIDLET_ERROR", "Can not find MIDlet class. Plz check jad or use -midlet param."));
            System.exit(1);
            return;
        }
        getEmulator().getLogStream().stdout("Launch MIDlet class: " + Emulator.midletClassName);
        Class<?> midletClass;
        try {
            midletClass = Class.forName(Emulator.midletClassName, true, Emulator.customClassLoader);
        } catch (Throwable e) {
            e.printStackTrace();
            Emulator.emulatorimpl.getEmulatorScreen().showMessage(UILocale.get("FAIL_LAUNCH_MIDLET", "Fail to launch the MIDlet class:") + " " + Emulator.midletClassName);
            System.exit(1);
            return;
        }
        Emulator.eventQueue = new EventQueue();
        try {
            midletClass.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            Emulator.eventQueue.stop();
            Emulator.emulatorimpl.getEmulatorScreen().showMessage(UILocale.get("FAIL_LAUNCH_MIDLET", "Fail to launch the MIDlet class:") + " " + Emulator.midletClassName);
            System.exit(1);
            return;
        }
        Emulator.emulatorimpl.method822().method302();
        Emulator.emulatorimpl.method829().method302();
        Emulator.eventQueue.queue(10);
        Emulator.emulatorimpl.getEmulatorScreen().start(true);
        EmulatorImpl.dispose();
        System.exit(0);
    }

    private static void tryToSetDevice(final String deviceName) {
        Emulator.deviceName = deviceName;
        if (!Devices.setPlatform(Emulator.deviceName)) {
            Devices.setPlatform(Emulator.deviceName = "SonyEricssonK800");
        }
        Emulator.emulatorimpl.getProperty().setCustomProperties();
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
            } else if (key.equalsIgnoreCase("installed")) {
                installed = true;
            } else if (key.equalsIgnoreCase("uei")) {
                Settings.uei = true;
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
        if (new File(s + "/KEmulator.jar").exists() || new File(s + "/sensorsimulator.jar").exists()) {
            return s + "/KEmulator.jar";
        }
        s = new Emulator().getClass().getProtectionDomain().getCodeSource().getLocation().getFile().substring(1);
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
        if (new File(s + "/KEmulator.jar").exists() || new File(s + "/sensorsimulator.jar").exists()) {
            return s;
        }
        s = getAbsoluteFile();
        File file = new File(s).getParentFile();
        try {
            s = file.getCanonicalPath() + File.separator;
        } catch (IOException e) {
            s = file.getAbsolutePath() + File.separator;
        }
        return s;
    }

    public static String getUserPath() {
        installed:
        {
            if (installed) {
                String s;
                if (linux) {
                    s = "~/local/share/KEmulator";
                } else {
                    s = System.getenv("APPDATA");
                    if (s == null)
                        break installed;
                    s += "\\KEmulator";
                }
                File f = new File(s);
                if ((f.exists() && f.isDirectory()) || f.mkdir())
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
        if (os.startsWith("darwin")) {
            cmd.add("-XstartOnFirstThread");
        }

        cmd.add("-javaagent:" + getAbsoluteFile());

        cmd.add("emulator.Emulator");
        if (s == null) {
            for (String a: Emulator.commandLineArguments) {
                if(a.equals("-swt") || a.equals("-awt")
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

        if(installed) cmd.add("-installed");

        getEmulator().disposeSubWindows();
        notifyDestroyed();
        try {
            new ProcessBuilder().directory(new File(getAbsolutePath())).command(cmd).inheritIO().start();
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
                properties.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                String absolutePath = file.getAbsolutePath().replace('\\', '/');
                return absolutePath.substring(0, absolutePath.lastIndexOf('/')) + "/" + properties.getProperty("MIDlet-Jar-URL");
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void AntiCrack(final Exception ex) {
        ex.toString();
    }

    public static native void regAssociateJar(final String p0, final String p1, final String p2);

    public static native void unregAssociateJar(final String p0);

    public static native void regRightMenu(final String p0);

    public static native void unregRightMenu();

    static {
        Emulator.customClassLoader = new CustomClassLoader(ClassLoader.getSystemClassLoader());
        Emulator.jarLibrarys = new Vector();
        Emulator.jarClasses = new Vector();
        Emulator.deviceName = "SonyEricssonK800";
        Emulator.deviceFile = "/res/plat";
        vlcCheckerThread = new Thread(() -> Manager.checkLibVlcSupport());
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

    public static boolean isJava9() {
        try {
            return Integer.parseInt(System.getProperty("java.version").split("\\.")[0]) >= 9;
        } catch (Throwable e) {
            return false;
        }
    }
}
