package emulator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public final class Devices {
	public static DevicePlatform curPlatform;
	private static Hashtable aHashtable1074;
	private static ArrayList anArrayList1075;
	private static Properties properties;
	private static final String[] aStringArray1077 = new String[] { "layout", "preset" };
	private static String defaultPreset;

	public Devices() {
		super();
	}

	public static void load(final String s) {
		try {
			final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream stream = null;
			try {
				stream = Emulator.class.getResourceAsStream(s);
			} catch (Exception ignored) {}
			if (stream == null) {
				stream = new FileInputStream(s);
			}
			if (stream == null) {
				throw new Exception("device file could not be found: " + s);
			}
			try {
				final Document parse = documentBuilder.parse(stream);
				defaultPreset = ((Element) parse.getElementsByTagName("default").item(0)).getAttribute("value");
				for (int i = 0; i < Devices.aStringArray1077.length; ++i) {
					final NodeList elementsByTagName = parse.getElementsByTagName(Devices.aStringArray1077[i]);
					for (int j = 0; j < elementsByTagName.getLength(); ++j) {
						final DevicePlatform e = new DevicePlatform((Element) elementsByTagName.item(j));
						Devices.aHashtable1074.put(e.name.toLowerCase(), e);
						if (i > 0) {
							Devices.anArrayList1075.add(e.name);
						}
					}
				}
			} finally {
				stream.close();
			}
//			Collections.sort((List<Comparable>) Devices.anArrayList1075);
		} catch (Exception ex2) {
			ex2.printStackTrace();
			Emulator.getEmulator().getLogStream().println("Failed to load XML file.");
		}
	}

	public static boolean setPreset(final String s) {
		final DevicePlatform ane1073;
		if ((ane1073 = (DevicePlatform) Devices.aHashtable1074.get(s.toLowerCase())) != null) {
			Devices.curPlatform = ane1073;
			return true;
		}
		Emulator.getEmulator().getLogStream().println("Not found preset: " + s);
		return false;
	}

	public static DevicePlatform getPlatform(final String s) {
		return (DevicePlatform) Devices.aHashtable1074.get(s.toLowerCase());
	}

	public static String getProperty(final String s) {
		return Devices.curPlatform.getString(s);
	}

	public static int getPropertyInt(final String s) {
		if (Devices.curPlatform == null) {
			return 0;
		}
		return Devices.curPlatform.getInt(s);
	}

	public static void setProperty(final String s, final String s2) {
		Devices.properties.setProperty(s.toUpperCase(), s2);
	}

	public static void writeProperties() {
		final Enumeration<?> propertyNames = Devices.properties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			final String s = (String) propertyNames.nextElement();
			Devices.curPlatform.properties.setProperty(s, (String) Devices.properties.get(s));
		}
	}

	public static String getDefaultPreset() {
		return defaultPreset;
	}

	public static Enumeration method620() {
		return Collections.enumeration((Collection<Object>) Devices.anArrayList1075);
	}

	static {
		Devices.aHashtable1074 = new Hashtable();
		Devices.anArrayList1075 = new ArrayList();
		Devices.properties = new Properties();
	}
}
