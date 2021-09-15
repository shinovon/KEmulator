package emulator;

import java.util.*;
import java.io.*;

public final class UILocale
{
    static PropertyResourceBundle aPropertyResourceBundle1191;
    static String encoding;
    
    public UILocale() {
        super();
    }
    
    public static void method709() {
        try {
            final FileInputStream fileInputStream = new FileInputStream(Emulator.getAbsolutePath() + "/language/default.txt");
            UILocale.aPropertyResourceBundle1191 = new PropertyResourceBundle(fileInputStream);
            fileInputStream.close();
            UILocale.encoding = UILocale.aPropertyResourceBundle1191.getString("LOCAL_ENCODEING");
        }
        catch (Exception ex) {
            UILocale.aPropertyResourceBundle1191 = null;
        }
    }
    
    public static String uiText(final String s, final String s2) {
        if (UILocale.aPropertyResourceBundle1191 != null) {
            try {
                return new String(UILocale.aPropertyResourceBundle1191.getString(s).getBytes("ISO-8859-1"), UILocale.encoding);
            }
            catch (Exception ex) {
                return s2;
            }
        }
        return s2;
    }
}
