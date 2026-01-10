package emulator;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;

public final class UILocale {
	static PropertyResourceBundle bundle;

	public UILocale() {
		super();
	}

	public static void initLocale() {
		try {
			final FileInputStream fileInputStream = new FileInputStream(String.format("%s/lang/%s.txt", Emulator.getAbsolutePath(), Settings.uiLanguage));
			UILocale.bundle = new PropertyResourceBundle(new InputStreamReader(fileInputStream, "UTF-8"));
			fileInputStream.close();
		} catch (Exception e) {
			try {
				final FileInputStream fileInputStream = new FileInputStream(String.format("%s/lang/en.txt", Emulator.getAbsolutePath()));
				UILocale.bundle = new PropertyResourceBundle(new InputStreamReader(fileInputStream, "UTF-8"));
				fileInputStream.close();
			} catch (Exception e2) {
				UILocale.bundle = null;
			}
		}
	}

	public static String get(final String s, final String s2) {
		if (UILocale.bundle == null)
			return s2;
		try {
			return UILocale.bundle.getString(s);
		} catch (Exception e) {
			return s2;
		}
	}
}
