package emulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.Attributes;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Screen;
import javax.microedition.midlet.MIDlet;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import emulator.custom.CustomClassLoader;
import emulator.media.MMFPlayer;
import emulator.ui.IEmulator;
import emulator.ui.swt.EmulatorImpl;

public class Emulator
{
    static EmulatorImpl emulatorimpl;
    private static MIDlet midlet;
    private static Canvas currentCanvas;
    private static Screen currentScreen;
    private static network.b netMonitor;
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
	private static Info[] midiDeviceInfo;
	public static String midipath;
	public static String iconPath;
	public static int screenBrightness = 100;
	public static int inactivityTimer = 0;

	protected static DiscordRPC rpc;
	private static long presenceStartTimestamp;
	private static Thread rpcCallbackThread;
	public static boolean rpcEnabled;
	private static Vector allowPerms = new Vector();
	private static Vector notAllowPerms = new Vector();
	public static String customUA;
	private static String imei;
	public static boolean askPermissions = false;
	public static boolean askImei = false;
	private static String midletName;
	
	public static final String titleVersion = "v2.10.2";
	public static final String aboutVersion = "v2.10";
	public static final int numericVersion = 10;
	public static final String propVersion = "v2.10";

	private static void loadRichPresence() {
		if(!rpcEnabled)
			return;
		rpc = DiscordRPC.INSTANCE;
		DiscordEventHandlers handlers = new DiscordEventHandlers();
		handlers.ready = (user) -> {};
		rpc.Discord_Initialize("823522436444192818", handlers, true, "");
		DiscordRichPresence presence = new DiscordRichPresence();
		presence.startTimestamp = presenceStartTimestamp = System.currentTimeMillis() / 1000;
		presence.state = "No Jar";
		rpc.Discord_UpdatePresence(presence);
		rpcCallbackThread = new Thread("RPC-Callback-Handler") {
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
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

	public static void updatePresence(String state, String details) {
		if(rpc == null)
			return;
		DiscordRichPresence presence = new DiscordRichPresence();
		presence.state = state;
		presence.startTimestamp = presenceStartTimestamp;
		presence.details = details;
		if (details != null && details != "")
			presence.details = details;
		rpc.Discord_UpdatePresence(presence);
	}
	
	private static int getAppPermissionLevel(String x) {
		if(!askPermissions) return 2;
		x = x.toLowerCase();
		switch(x) {
		case "connector.open.http":
		case "connector.open.https":
		case "connector.open.file":
		case "connector.open.socket":
		case "connector.open.serversocket":
		case "connector.open.sms":
			return 5;
		default:
			return 4;
		}
	}
	
	public static String askIMEI() {
		if(notAllowPerms.contains("imei"))
			return null;
		if(!askImei) return "0";
		if(imei != null) return imei;
		JFrame parent = new JFrame();
		parent.setAlwaysOnTop(true);
		String s = JOptionPane.showInputDialog(parent, "Application asks for IMEI", "", JOptionPane.WARNING_MESSAGE);
		if(s == null) {
			notAllowPerms.add("imei");
			return null;
		}
		allowPerms.add("imei");
		return imei = s;
	}
	
	public static void checkPermission(String x) {
		int p = getAppPermissionLevel(x);
		JFrame parent = new JFrame();
		parent.setAlwaysOnTop(true);
		//0: always ask
		//1: ask once if yes, ask next time if no is pressed
		//2: always allowed
		//3: never
		//4: always ask until no is pressed
		//5: ask once
		if(p == 0) {
			// always ask
			int i = JOptionPane.showConfirmDialog(parent, localizePerm(x), "", JOptionPane.YES_NO_OPTION);
			if(i == JOptionPane.NO_OPTION)
				throw new SecurityException(x);
			allowPerms.add(x);
		} else if(p == 1) {
			// ask once
			if(allowPerms.contains(x))
				return;
			int i = JOptionPane.showConfirmDialog(parent, localizePerm(x), "", JOptionPane.YES_NO_OPTION);
			if(i == JOptionPane.NO_OPTION) {
				throw new SecurityException(x);
			}
			allowPerms.add(x);
		} else if(p == 2) {
			// allowed
		} else if(p == 3) {
			// never
			throw new SecurityException(x);
		} else if(p == 4) {
			// always ask until "no" is pressed
			if(notAllowPerms.contains(x))
				return;
			int i = JOptionPane.showConfirmDialog(parent, localizePerm(x), "", JOptionPane.YES_NO_OPTION);
			if(i == JOptionPane.NO_OPTION) {
				notAllowPerms.add(x);
				throw new SecurityException(x);
			}
			allowPerms.add(x);
		} else if(p == 5) {
			// ask once
			if(notAllowPerms.contains(x))
				throw new SecurityException(x);
			if(allowPerms.contains(x))
				return;
			int i = JOptionPane.showConfirmDialog(parent, localizePerm(x), "", JOptionPane.YES_NO_OPTION);
			if(i == JOptionPane.NO_OPTION) {
				notAllowPerms.add(x);
				throw new SecurityException(x);
			}
			allowPerms.add(x);
		} else {
			
		}
	}
    
    private static String localizePerm(String x) {
		switch(x) {
		case "connector.open.http":
			return "Allow the application to open HTTP connections?";
		case "connector.open.https":
			return "Allow the application to open HTTPS connections?";
		case "connector.open.file":
			return "Allow the application to access the file system?";
		case "connector.open.socket":
			return "Allow the application to open socket connections?";
		case "connector.open.serversocket":
			return "Allow the application to open server socket connections?";
		default:
			return "Allow the application to use \'" + x + "\'?";
		}
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
    
    public static void setCanvas(final Canvas aCanvas500) {
        Emulator.currentCanvas = aCanvas500;
    }
    
    public static Screen getScreen() {
        return Emulator.currentScreen;
    }
    
    public static void setScreen(final Screen aScreen501) {
        Emulator.currentScreen = aScreen501;
    }
    
    public static MIDlet getMIDlet() {
        return Emulator.midlet;
    }
    
    public static void setMIDlet(final MIDlet amiDlet499) {
        Emulator.midlet = amiDlet499;
    }
    
    public static Display getCurrentDisplay() {
        return Display.getDisplay(Emulator.midlet);
    }
    
    public static network.b getNetMonitor() {
        return Emulator.netMonitor;
    }
    
    public static EventQueue getEventQueue() {
        return Emulator.eventQueue;
    }
    
    public static emulator.custom.CustomClassLoader getCustomClassLoader() {
        return Emulator.customClassLoader;
    }
    
    public static void notifyDestroyed() {
		if(rpcCallbackThread != null)
			rpcCallbackThread.interrupt();
        MMFPlayer.method200();
        Emulator.emulatorimpl.getProperty().saveProperties();
        if (Settings.autoGenJad) {
            method280();
            return;
        }
        method286();
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
        }
        catch (Exception ex) {ex.printStackTrace();}
    }
    
    public static Properties getMidletProperties() {
    	return Emulator.emulatorimpl.midletProps;
    }
    
    private static void method286() {
        if (Emulator.midletJar == null) {
            return;
        }
        try {
            String s;
            String s2;
            if (Emulator.midletJar.lastIndexOf("\\") != -1) {
                s = Emulator.midletJar;
                s2 = "\\";
            }
            else {
                s = Emulator.midletJar;
                s2 = "/";
            }
            final int lastIndex = s.lastIndexOf(s2);
            final String string = Emulator.midletJar.substring(0, lastIndex) + "/kemulator.cfg";
            final String substring = Emulator.midletJar.substring(lastIndex + 1, Emulator.midletJar.lastIndexOf("."));
            final Properties properties = new Properties();
            if (new File(string).exists()) {
                final FileInputStream fileInputStream = new FileInputStream(string);
                properties.load(fileInputStream);
                fileInputStream.close();
            }
            properties.setProperty(substring, Emulator.deviceName);
            final FileOutputStream fileOutputStream = new FileOutputStream(string);
            properties.store(fileOutputStream, "KEmulator platforms");
            fileOutputStream.close();
        }
        catch (Exception ex) { ex.printStackTrace();}
    }
    
    private static void method287() {
        final String property;
        if ((property = Emulator.emulatorimpl.midletProps.getProperty("KEmu-Platform")) != null) {
            tryToSetDevice(property);
            return;
        }
        if (Emulator.midletJar == null) {
            return;
        }
        String s;
        String s2;
        if (Emulator.midletJar.lastIndexOf("\\") != -1) {
            s = Emulator.midletJar;
            s2 = "\\";
        }
        else {
            s = Emulator.midletJar;
            s2 = "/";
        }
        final int lastIndex = s.lastIndexOf(s2);
        final String string = Emulator.midletJar.substring(0, lastIndex) + "/kemulator.cfg";
        final String substring = Emulator.midletJar.substring(lastIndex + 1, Emulator.midletJar.lastIndexOf("."));
        try {
            final FileInputStream fileInputStream = new FileInputStream(string);
            final Properties properties;
            (properties = new Properties()).load(fileInputStream);
            fileInputStream.close();
            final String property2;
            if ((property2 = properties.getProperty(substring, null)) != null) {
                tryToSetDevice(property2);
            }
        }
        catch (Exception ex) {ex.printStackTrace();}
    }
    
    public static String getVersionString() {
        return "KEmulator nnmod " + titleVersion;
    }
    
    public static String getInfoString() {
        return "KEmulator v1.0.3" + "\n\tmod nnproject "+aboutVersion+"\n\n\t" + UILocale.uiText("ABOUT_INFO_EMULATOR", "Mobile Game Emulator") + "\n\n" + UILocale.uiText("ABOUT_INFO_APIS", "Support APIs") + ":\n\n\t" + "MIDP 2.0(JSR118)\n\tNokiaUI 1.4\n\tSamsung 1.0\n\tSprint 1.0\n\tWMA 1.0(JSR120)\n\tSensor(JSR256)\n\tM3G 1.1(JSR184)\n\tOpenGL ES(JSR239)";
    }
    
    public static String getAboutString() {
        return "KEmulator v1.0.3" + "\n\tmod nnproject "+aboutVersion+"\n\n\t" + UILocale.uiText("ABOUT_INFO_EMULATOR", "Mobile Game Emulator");
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
        Label_0068: {
            File file2;
            if (Emulator.jadPath != null) {
                file2 = new File(Emulator.jadPath);
            }
            else {
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
                }
                else {
                    final StringBuffer sb = new StringBuffer();
                    file = (file2 = new File(sb.append(Emulator.midletJar.substring(0, Emulator.midletJar.length() - 3)).append("jad").toString()));
                   
                }
                final File file3 = file2;
                if (file.exists()) {
                    ((Properties)(props = new Properties())).load(new FileInputStream(file3));
                    final Enumeration<Object> keys = props.keys();
                    while (keys.hasMoreElements()) {
                        final String s = (String) keys.nextElement();
                        props.put(s, new String(props.getProperty(s).getBytes("ISO-8859-1"), "UTF-8"));
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
                            props.put(entry.getKey().toString(), (String) entry.getValue());
                        }
                    }
                    catch (Exception ex2) {
                        final InputStream inputStream;
                        (inputStream = zipFile.getInputStream(zipFile.getEntry("META-INF/MANIFEST.MF"))).skip(3L);
                        ((Properties)(props = new Properties())).load(inputStream);
                        inputStream.close();
                        final Enumeration<Object> keys2 = props.keys();
                        while (keys2.hasMoreElements()) {
                            final String s2 = (String) keys2.nextElement();
                            props.put(s2, new String(props.getProperty(s2).getBytes("ISO-8859-1"), "UTF-8"));
                        }
                    }
                }
                Emulator.emulatorimpl.midletProps = (Properties)props;
                if(props.containsKey("MIDlet-2") && props.containsKey("MIDlet-1")) {
                	// find all midlets and show choice window
                	Vector<String> midletKeys = new Vector<String>();
                	final Enumeration<Object> keys = props.keys();
                    while (keys.hasMoreElements()) {
                        final String s = (String) keys.nextElement();
                        if(s.startsWith("MIDlet-")) {
                        	String num = s.substring("MIDlet-".length());
                        	try {
                        		int n = Integer.parseInt(num);
                        		String v = props.getProperty(s);
                        		v = v.substring(0, v.indexOf(","));
                        		midletKeys.add(n + " (" + v + ")");
                        	} catch (Exception e) {
                        	}
                        }
                    }
                    if(midletKeys.size() > 0) {
                        String[] arr = midletKeys.toArray(new String[0]);
                        String c = (String) JOptionPane.showInputDialog(null, "Choose MIDlet to run", "KEmulator", JOptionPane.QUESTION_MESSAGE, null, arr, arr[0]);
                        if(c == null) {
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
            }
            else {
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
                    (aProperties1369 = new Properties()).load(new FileInputStream(file4));
                    final Enumeration keys3 = (aProperties1369).keys();
                    while (keys3.hasMoreElements()) {
                        final String s3 = (String) keys3.nextElement();
                        aProperties1369.put(s3, new String(aProperties1369.getProperty(s3).getBytes("ISO-8859-1"), "UTF-8"));
                    }
                }
                if (aProperties1369 == null) {
                    aProperties1369 = new Properties();
                }
                Emulator.emulatorimpl.midletProps = aProperties1369;
            }
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            Emulator.emulatorimpl.getLogStream().println("3 "+ex.toString());
        	if(ex.toString().equalsIgnoreCase("java.io.IOException: Negative seek offset")) {
        		Emulator.emulatorimpl.getEmulatorScreen().method552(UILocale.uiText("LOAD_DRM_ERROR", "Input file isn't ZIP. Trying to load DRM Content?"));
        	}
            return false;
        }
        method287();
        return true;
    }
    
    private static void method281(final File file, String s) {
        s = ((s != null) ? (s + ".") : "");
        final File[] listFiles = file.listFiles();
        for (int i = 0; i < listFiles.length; ++i) {
            final String string = s + listFiles[i].getName();
            if (listFiles[i].isDirectory()) {
                method281(listFiles[i], string);
            }
            else if (string.endsWith(".class")) {
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
        if (Emulator.midletJar == null && Settings.aArray[0].trim().equalsIgnoreCase("")) {
            return;
        }
        if (Settings.aArray[0].trim().equalsIgnoreCase("")) {
            Settings.aArray[0] = Emulator.midletJar;
            return;
        }
        if (Emulator.midletJar != null) {
            for (int i = 4; i > 0; --i) {
                if (Settings.aArray[i].equalsIgnoreCase(Settings.aArray[0])) {
                    Settings.aArray[i] = Settings.aArray[1];
                    Settings.aArray[1] = Settings.aArray[0];
                    if (!Settings.aArray[0].equalsIgnoreCase(Emulator.midletJar)) {
                        Settings.aArray[0] = Emulator.midletJar;
                    }
                    return;
                }
            }
        }
        int j;
        for (j = 4; j > 0; --j) {
            if (Settings.aArray[j].equalsIgnoreCase(Settings.aArray[0])) {
                Settings.aArray[j] = Settings.aArray[1];
                Settings.aArray[1] = Settings.aArray[0];
                break;
            }
        }
        if (j == 0) {
            for (int k2 = 4; k2 > 0; --k2) {
                Settings.aArray[k2] = Settings.aArray[k2 - 1];
            }
        }
        String[] array;
        int n;
        String midletJar;
        if (Emulator.midletJar == null) {
            array = Settings.aArray;
            n = 0;
            midletJar = "";
        }
        else {
            array = Settings.aArray;
            n = 0;
            midletJar = Emulator.midletJar;
        }
        array[n] = midletJar;
    }
    
    private static void method288() {
        System.setProperty("microedition.configuration", "CDLC-1.1");
        System.setProperty("microedition.profiles", "MIDP-2.0");
        System.setProperty("microedition.m3g.version", "1.1");
        System.setProperty("microedition.encoding", Settings.fileEncoding);
        if (System.getProperty("microedition.locale") == null) {
            System.setProperty("microedition.locale", "ru-RU");
        }
        if (System.getProperty("microedition.platform") == null) {
        	String plat = Emulator.deviceName;
        	DevicePlatform c = Devices.getPlatform(Emulator.deviceName);
        	if(true) {
            	if(c.exists("OVERRIDE_NAME")) {
            		plat = c.getString("OVERRIDE_NAME");
            	}
        		boolean e = c.exists("MIDP20_CLDC11") || c.exists("MIDP20_CLDC10") || c.exists("MIDP21_CLDC11") || c.exists("MIDP10_CLDC10");
        		if(c.exists("R")) {
        			plat += "-" + c.getString("R");
        		}

        		String s2 = plat;
        		if(!c.exists("PLATFORM_VERSION2") && c.exists("PLATFORM_VERSION")) {
        			plat += "/" + c.getString("PLATFORM_VERSION");
        		}
        		if(e) {
        			Emulator.customUA = s2 + "/" + c.getString("PLATFORM_VERSION");
        			if(c.exists("PLATFORM_VERSION2")) {
        				Emulator.customUA += " (" + c.getString("PLATFORM_VERSION2") + ")";
        			}
        			s2 = Emulator.customUA;
        			if(c.exists("SYMBIANOS_VERSION")) {
        				Emulator.customUA += " SymbianOS/" + c.getString("SYMBIANOS_VERSION");
        			}
        			if(c.exists("S60_VERSION")) {
        				Emulator.customUA += " Series60/" + c.getString("S60_VERSION");
        			}
        		}
        		boolean x = c.exists("SW_PLATFORM") || c.exists("SW_PLATFORM_VERSION");
        		if(c.exists("SYMBIANOS_VERSION") && x)
        			if(c.getString("SYMBIANOS_VERSION").equals("9.3") || c.getString("SYMBIANOS_VERSION").equals("3"))
        				Emulator.customUA = s2 + " (Java/1.8.0_60; KEmulator/1.0.3) UNTRUSTED/1.0";
        		else if(c.exists("CUSTOM_UA")) {
        			Emulator.customUA = c.getString("CUSTOM_UA");
        		} else if(c.exists("MIDP20_CLDC11")) {
        			Emulator.customUA = Emulator.customUA + " Profile/MIDP-2.0 Configuration/CLDC-1.1";
        		} else if(c.exists("MIDP20_CLDC10")) {
        			Emulator.customUA = Emulator.customUA + " Profile/MIDP-2.0 Configuration/CLDC-1.0";
        		} else if(c.exists("MIDP21_CLDC11")) {
        			Emulator.customUA = Emulator.customUA + " Profile/MIDP-2.1 Configuration/CLDC-1.1";
        		} else if(c.exists("MIDP10_CLDC10")) {
        			Emulator.customUA = Emulator.customUA + " Profile/MIDP-1.0 Configuration/CLDC-1.0";
        		} else if(c.parent != null && c.parent.name.equalsIgnoreCase("Nokia_SERIES40")) {
        			Emulator.customUA = Emulator.customUA + " Profile/MIDP-2.0 Configuration/CLDC-1.1";
        		}
        		if(c.exists("PLATFORM_VERSION2") && c.exists("PLATFORM_VERSION")) {
        			plat += "/" + c.getString("PLATFORM_VERSION2");
        		}
        		if(x) {
        			plat += "/";
        		}
        		if(c.exists("SW_PLATFORM")) {
        			plat += "sw_platform=" + c.getString("SW_PLATFORM");
        		}
        		if(c.exists("SW_PLATFORM_VERSION")) {
        			plat += ";sw_platform_version=" + c.getString("SW_PLATFORM_VERSION");
        		}
        	}
        	String midlet = Emulator.emulatorimpl.getAppProperty("MIDlet-Name");
        	Emulator.midletName = midlet;
        	if(midlet != null) {
        		if(midlet.equalsIgnoreCase("vika touch")) {
	        		// TODO: насрать
	        	} else if(midlet.equalsIgnoreCase("bounce tales")) {
	        		Settings.fpsGame = 1;
	        	} else if(midlet.equalsIgnoreCase("micro counter strike")) {
	        		Settings.fpsGame = 2;
	        	} else Settings.fpsGame = 0;
        	}
        	
        	if(midlet != null && (midlet.equalsIgnoreCase("Shiza") || midlet.toLowerCase().startsWith("touch")/* || midlet.toLowerCase().startsWith("vika")*/))
        	plat += "/KEmulatorMod";
            System.setProperty("microedition.platform", plat);
            //System.out.println("paltform set: " + plat);
            //System.out.println("ua: " + Emulator.customUA);
        }
        System.setProperty("microedition.media.version", "1.0");
        System.setProperty("supports.mixing", "true");
        System.setProperty("supports.audio.capture", "false");
        System.setProperty("supports.video.capture", "true");
        System.setProperty("supports.photo.capture", "true");
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
        System.setProperty("kemulator.filemanagerapi.version", "1.0");
        System.setProperty("fileconn.dir.photos", "file:///root/photos/");
        System.setProperty("fileconn.dir.music", "file:///root/music/");
        System.setProperty("fileconn.dir.private", "file://root/private/");
        System.setProperty("fileconn.dir.memorycard", "file:///root/e/");
        System.setProperty("microedition.hostname", "localhost");
        if (System.getProperty("wireless.messaging.version") == null) {
        	System.setProperty("wireless.messaging.version", "1.0");
        }
        System.setProperty("kemulator.mod.version", propVersion);
        System.setProperty("kemulator.mod.versionint", "" + numericVersion);
        System.setProperty("com.nokia.mid.ui.softnotification", "true");
        System.setProperty("com.nokia.mid.ui.version", "1.4");
        System.setProperty("com.nokia.mid.ui.customfontsize", "true");
        System.setProperty("com.nokia.pointer.number", "1");
        System.setProperty("kemulator.hwid", getHWID());
        
    }

	private static String getHWID() {
        try {
            String s = System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            StringBuffer sb = new StringBuffer();
            byte b[] = md.digest();
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
    	midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        Emulator.commandLineArguments = commandLineArguments;
        UILocale.method709();
        Emulator.emulatorimpl = new EmulatorImpl();
        i.a("emulator");
        Controllers.method750(true);
        Emulator.emulatorimpl.getLogStream().stdout(getVersionString() + " Running...");
        method283(commandLineArguments);
        Devices.load(Emulator.deviceFile);
        tryToSetDevice(Emulator.deviceName);
        setupMRUList();
    	loadRichPresence();
        if (Emulator.midletClassName == null && Emulator.midletJar == null) {
            Emulator.emulatorimpl.getEmulatorScreen().method553(false);
            EmulatorImpl.dispose();
            return;
        }
        Emulator.record = new KeyRecords();
        getLibraries();
        if (!getJarClasses()) {
            Emulator.emulatorimpl.getEmulatorScreen().method552(UILocale.uiText("LOAD_CLASSES_ERROR", "Get Classes Failed!! Plz check the input jar or classpath."));
            return;
        }
        InputStream inputStream = null;
        final String appProperty;
        if ((appProperty = Emulator.emulatorimpl.getAppProperty("MIDlet-Icon")) != null) {
        	iconPath = appProperty;
            inputStream = emulator.custom.CustomJarResources.getResourceStream(appProperty);
        }
        else {
            final String appProperty2;
            if ((appProperty2 = Emulator.emulatorimpl.getAppProperty("MIDlet-1")) != null) {
                try {
                	String s = appProperty2.split(",")[1].trim();
                	iconPath = s;
                    inputStream = emulator.custom.CustomJarResources.getResourceStream(s);
                }
                catch (Exception ex3) {ex3.printStackTrace();}
            }
        }
        String jar = midletJar;
        if(jar.indexOf("/") > -1)
        	jar = jar.substring(jar.lastIndexOf("/")+1);
        if(jar.indexOf("\\") > -1)
        	jar = jar.substring(jar.lastIndexOf("\\")+1);
        if(Emulator.emulatorimpl.getAppProperty("MIDlet-Name") != null)
        	Emulator.updatePresence("Running " + Emulator.emulatorimpl.getAppProperty("MIDlet-Name"), "" + jar);
        Emulator.emulatorimpl.getEmulatorScreen().method551(inputStream);
        method288();
        if (Emulator.midletClassName == null) {
            Emulator.emulatorimpl.getEmulatorScreen().method552(UILocale.uiText("LOAD_MIDLET_ERROR", "Can not find MIDlet class. Plz check jad or use -midlet param."));
            return;
        }
        getEmulator().getLogStream().stdout("Launch MIDlet class: " + Emulator.midletClassName);
        Class<?> forName;
        try {
            forName = Class.forName(Emulator.midletClassName, true, (ClassLoader)Emulator.customClassLoader);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Emulator.emulatorimpl.getEmulatorScreen().method552(UILocale.uiText("FAIL_LAUNCH_MIDLET", "Fail to launch the MIDlet class:") + " " + Emulator.midletClassName);
            return;
        }
        catch (Error error) {
            error.printStackTrace();
            Emulator.emulatorimpl.getEmulatorScreen().method552(UILocale.uiText("FAIL_LAUNCH_MIDLET", "Fail to launch the MIDlet class:") + " " + Emulator.midletClassName);
            return;
        }
        Emulator.eventQueue = new EventQueue();
        Emulator.netMonitor = new network.b();
        try {
            forName.newInstance();
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
            Emulator.eventQueue.stop();
            Emulator.emulatorimpl.getEmulatorScreen().method552(UILocale.uiText("FAIL_LAUNCH_MIDLET", "Fail to launch the MIDlet class:") + " " + Emulator.midletClassName);
            return;
        }
        catch (Error error2) {
            error2.printStackTrace();
            Emulator.eventQueue.stop();
            Emulator.emulatorimpl.getEmulatorScreen().method552(UILocale.uiText("FAIL_LAUNCH_MIDLET", "Fail to launch the MIDlet class:") + " " + Emulator.midletClassName);
            return;
        }
        Emulator.emulatorimpl.method822().method302();
        Emulator.emulatorimpl.method829().method302();
        Emulator.eventQueue.queue(10);
        Emulator.emulatorimpl.getEmulatorScreen().method553(true);
        EmulatorImpl.dispose();
    }
    
    private static void tryToSetDevice(final String deviceName) {
        Emulator.deviceName = deviceName;
        if (!Devices.setPlatform(Emulator.deviceName)) {
            Devices.setPlatform(Emulator.deviceName = "SonyEricssonK800");
        }
        Emulator.emulatorimpl.getProperty().setCustomProperties();
        Emulator.emulatorimpl.getProperty().updateCustomProperties();
        Emulator.emulatorimpl.getProperty().resetDeviceName();
        Keyboard.init();
    }
    
    static boolean method283(final String[] array) {
        String lowerCase = "";
        if (array.length < 1) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            final String trim;
            if ((trim = array[i].trim()).startsWith("-")) {
                if ((lowerCase = trim.substring(1).toLowerCase()).equals("awt")) {
                    Settings.g2d = 1;
                    Emulator.commandLineArguments[i] = "";
                }
                else if (lowerCase.equals("swt")) {
                    Settings.g2d = 0;
                    Emulator.commandLineArguments[i] = "";
                }
                else if (lowerCase.equals("wgl")) {
                    Settings.g3d = 0;
                }
                else if (lowerCase.equals("lwj")) {
                    Settings.g3d = 1;
                }
                else if (lowerCase.equalsIgnoreCase("log")) {
                    Settings.showLogFrame = true;
                }
            }
            else if (lowerCase.equalsIgnoreCase("jar")) {
                Emulator.midletJar = trim;
            }
            else if (lowerCase.equalsIgnoreCase("midlet")) {
                Emulator.midletClassName = array[i];
            }
            else if (lowerCase.equalsIgnoreCase("cp")) {
                Emulator.classPath = trim;
            }
            else if (lowerCase.equalsIgnoreCase("jad")) {
                Emulator.jadPath = trim;
            }
            else if (lowerCase.equalsIgnoreCase("rec")) {
            	File localFile;
                Settings.bString = trim;
                Settings.bString = null;
                Settings.q = (localFile = new File(trim)).exists();
            }
            else if (lowerCase.equalsIgnoreCase("device")) {
                Emulator.deviceName = trim;
            }
            else if (lowerCase.equalsIgnoreCase("devicefile")) {
                Emulator.deviceFile = trim;
            }
            else if (lowerCase.equalsIgnoreCase("fontname")) {
                getEmulator().getProperty().setDefaultFontName(trim);
            }
            else if (lowerCase.equalsIgnoreCase("fontsmall")) {
                getEmulator().getProperty().setFontSmallSize(Integer.parseInt(trim));
            }
            else if (lowerCase.equalsIgnoreCase("fontmedium")) {
                getEmulator().getProperty().getFontMediumSize(Integer.parseInt(trim));
            }
            else if (lowerCase.equalsIgnoreCase("fontlarge")) {
                getEmulator().getProperty().getFontLargeSize(Integer.parseInt(trim));
            }
            else if (lowerCase.equalsIgnoreCase("key")) {
                Keyboard.keyArg(trim);
            }
        }
        return true;
    }
    
    public static String getAbsoluteFile() {
    	return System.getProperty("user.dir");
        /*
    	String substring = new Emulator().getClass().getProtectionDomain().getCodeSource().getLocation().getFile().substring(1);
        if(substring.endsWith("bin/"))
        	substring = substring.substring(0, substring.length() - 4);
        try {
            return URLDecoder.decode(substring, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            return substring;
        }
        */
    }
    
    public static String getAbsolutePath() {
    	return getAbsoluteFile();
       // final String absoluteFile = getAbsoluteFile();
        //String s;
       // if ((s = absoluteFile.substring(0, absoluteFile.lastIndexOf(47))).charAt(0) == '/') {
        //    s = "/" + s;
        //}
       // return s;
    }
    
    public static void loadGame(final String s, final int n, final int n2, final boolean b) {
        final String property;
        String s2;
        if ((property = System.getProperty("java.home")) == null || property.length() < 1) {
            s2 = "java";
        }
        else {
            final String string = property + "\\bin\\java.exe";
            final File file;
            s2 = (((file = new File(string)).exists() && file.isFile()) ? ("\"" + string + "\"") : "java");
        }
        final String s3 = s2;
        final String string2 = " -cp \"" + System.getProperty("java.class.path") + "\"";
        final String s4 = " emulator.Emulator";
        final StringBuffer sb = new StringBuffer(100);
        if (s == null) {
            for (int i = 0; i < Emulator.commandLineArguments.length; ++i) {
                sb.append(" " + Emulator.commandLineArguments[i]);
            }
            method284(sb, "-jar");
            method284(sb, "-cp");
            method284(sb, "-jad");
        }
        else if (s.endsWith(".jad")) {
            sb.append(" -jad \"" + s + "\"");
            sb.append(" -jar \"" + getMidletJarUrl(s) + "\"");
        }
        else {
            sb.append(" -jar \"" + s + "\"");
        }
        if (Settings.bString != null) {
            sb.append(" -rec \"" + Settings.bString + "\"");
        }
        if (n == 1) {
            sb.append(" -awt");
        }
        else if (n == 0) {
            sb.append(" -swt");
        }
        if (n2 == 1) {
            sb.append(" -lwj");
        }
        else if (n2 == 0) {
            sb.append(" -wgl");
        }
        final String[] array;
        (array = new String[2])[0] = "cmd.exe";
        array[1] = s3 + string2 + s4 + (Object)sb;
        String[] array2;
        int n3;
        StringBuffer sb2;
        String s5;
        if (b) {
            array2 = array;
            n3 = 1;
            sb2 = new StringBuffer().append("/c start cmd.exe /c \"").append(array[1]);
            s5 = " && pause \"";
        }
        else {
            array2 = array;
            n3 = 1;
            sb2 = new StringBuffer().append("/c \"").append(array[1]);
            s5 = "\"";
        }
        array2[n3] = sb2.append(s5).toString();
        getEmulator().getLogStream().print(array[0] + " " + array[1]);
        getEmulator().disposeSubWindows();
        notifyDestroyed();
        try {
            Runtime.getRuntime().exec(array);
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            AntiCrack(ex);
        }
        System.exit(0);
    }
    
    private static void method284(final StringBuffer sb, final String s) {
        final int index;
        if ((index = sb.indexOf(s)) != -1) {
            final int n = index + (s.length() + 1);
            sb.insert(n, "\"");
            final int index2;
            if ((index2 = sb.indexOf("-", n)) != -1) {
                sb.insert(index2 - 1, "\"");
                return;
            }
            sb.append("\"");
        }
    }
    
    private static String getMidletJarUrl(final String s) {
        try {
            final File file;
            if ((file = new File(s)).exists()) {
                final Properties properties;
                (properties = new Properties()).load(new FileInputStream(file));
                final String s2 = new String(properties.getProperty("MIDlet-Jar-URL").getBytes("ISO-8859-1"), "UTF-8");
                final String absolutePath = file.getAbsolutePath();
                return absolutePath.substring(0, absolutePath.lastIndexOf(92)) + "\\" + s2;
            }
            return null;
        }
        catch (Exception ex) {ex.printStackTrace();
            return null;
        }
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
    }

	public static Info getMidiDeviceInfo() throws MidiUnavailableException {
		for (int i = 0; i < midiDeviceInfo.length; ++i) {
			if(midiDeviceInfo[i].getName().toLowerCase().contains("virtual")) {
				return midiDeviceInfo[i];
			}
		}
		return MidiSystem.getSynthesizer().getDeviceInfo();
	}

	public static String getMidletName() {
		String x = emulatorimpl.getAppProperty("MIDlet-1").split(",")[2];
		if(x.startsWith(" "))
			x = x.substring(1);
		if(x.endsWith(" "))
			x = x.substring(0, x.length() - 1);
		return x;
	}
}
