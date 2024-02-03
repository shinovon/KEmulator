package emulator;

import java.util.*;
import java.io.*;

public final class UILocale
{
    static PropertyResourceBundle bundle;
    
    public UILocale() {
        super();
    }
    
    public static void initLocale() {
        try {
            final FileInputStream fileInputStream = new FileInputStream(Emulator.getAbsolutePath() + "/lang/" + Settings.uiLanguage + ".txt");
            UILocale.bundle = new PropertyResourceBundle(new InputStreamReader(fileInputStream, "UTF-8"));
            fileInputStream.close();
        } catch (Exception e) {
            try {
                final FileInputStream fileInputStream = new FileInputStream(Emulator.getAbsolutePath() + "/lang/en.txt");
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
