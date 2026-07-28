package emulator;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;

public final class UILocale {
	static PropertyResourceBundle bundle;
	static PropertyResourceBundle enBundle;

	public static void initLocale() {
		try {
			try (FileInputStream fileInputStream = new FileInputStream(String.format("%s/lang/en.txt", Emulator.getAbsolutePath()))) {
				enBundle = new PropertyResourceBundle(new InputStreamReader(fileInputStream, "UTF-8"));
			}
		} catch (Exception ignored) {}
		try {
			try (FileInputStream fileInputStream = new FileInputStream(String.format("%s/lang/%s.txt", Emulator.getAbsolutePath(), Settings.uiLanguage))) {
				bundle = new PropertyResourceBundle(new InputStreamReader(fileInputStream, "UTF-8"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			bundle = enBundle;
		}
	}

	public static String get(final String s, final String def) {
		String r = def;
		try {
			r = UILocale.bundle.getString(s);
		} catch (Exception ignored) {}

		if (r == null) {
			try {
				r = UILocale.enBundle.getString(s);
			} catch (Exception ignored) {}
		}

		if (r == null) {
			r = def;
		}

		if (r == null) {
			r = s;
		}

		return r;
	}

	public static String get(final String s) {
		return get(s, null);
	}
}
