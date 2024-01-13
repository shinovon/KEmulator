package emulator;

import java.util.*;
import java.io.*;

public final class UILocale
{
    static PropertyResourceBundle bundle;
    static String encoding;
    
    public UILocale() {
        super();
    }
    
    public static void initLocale() {
        try {
            final FileInputStream fileInputStream = new FileInputStream(Emulator.getAbsolutePath() + "/language/default.txt");
            UILocale.bundle = new PropertyResourceBundle(fileInputStream);
            fileInputStream.close();
            UILocale.encoding = UILocale.bundle.getString("LOCAL_ENCODEING");
        }
        catch (Exception ex) {
            UILocale.bundle = null;
        }
    }
    
    public static String get(final String s, final String s2) {
        if (UILocale.bundle != null) {
            try {
                return new String(UILocale.bundle.getString(s).getBytes("ISO-8859-1"), UILocale.encoding);
            }
            catch (Exception ex) {
                return s2;
            }
        }
        return s2;
    }
}
