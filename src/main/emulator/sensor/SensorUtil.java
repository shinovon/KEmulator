package emulator.sensor;

import javax.microedition.sensor.*;

public final class SensorUtil
{
    private int position;
    private String aString463;
    private String aString464;
    private String aString465;
    private String aString466;
    private String aString467;
    
    private SensorUtil() {
        super();
    }
    
    public final String method246() {
        return this.aString464;
    }
    
    public final String method252() {
        return this.aString465;
    }
    
    public final String method254() {
        return this.aString466;
    }
    
    public final String method255() {
        return this.aString467;
    }
    
    private static boolean method247(final char c) {
        return Character.isDigit(c) || Character.isLowerCase(c) || Character.isUpperCase(c) || method253(c);
    }
    
    private static boolean method253(final char c) {
        final char[] array = { '-', '_', '.', '!', '~', '*', '\'', '(', ')' };
        for (int i = 0; i < array.length; ++i) {
            if (c == array[i]) {
                return true;
            }
        }
        return false;
    }
    
    private String method256() throws IllegalArgumentException {
        if (this.position < 0 || this.position > this.aString463.length() - 1) {
            throw new IllegalArgumentException("Wrong position");
        }
        if (!method247(this.aString463.charAt(this.position))) {
            throw new IllegalArgumentException("First symbol is not alphanum");
        }
        final StringBuffer sb = new StringBuffer();
        char char1;
        while (this.position < this.aString463.length() && method247(char1 = this.aString463.charAt(this.position))) {
            sb.append(char1);
            ++this.position;
        }
        return sb.toString();
    }
    
    private String method248(final String[] array) throws IllegalArgumentException {
        final String method256 = this.method256();
        boolean b = false;
        for (int i = 0; i < array.length; ++i) {
            if (method256.equals(array[i])) {
                b = true;
                break;
            }
        }
        if (!b) {
            throw new IllegalArgumentException("Current word isn't found in the input array");
        }
        return method256;
    }
    
    private void method249(final char c) throws IllegalArgumentException {
        if (this.position < 0 || this.position > this.aString463.length() - 1) {
            throw new IllegalArgumentException("Wrong position");
        }
        if (c != this.aString463.charAt(this.position++)) {
            throw new IllegalArgumentException("Current symbol is not separator url = \"" + this.aString463 + "\" pos = " + this.position);
        }
    }
    
    public static SensorUtil method250(final String aString463) {
        final SensorUtil d;
        (d = new SensorUtil()).aString463 = aString463;
        if (!d.aString463.startsWith("sensor:")) {
            throw new IllegalArgumentException("Wrong scheme");
        }
        d.position = "sensor:".length();
        final int length = d.aString463.length();
        d.aString465 = d.method256();
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        while (d.position < length) {
            d.method249(';');
            final String method248 = d.method248(new String[] { "contextType", "model", "location" });
            d.method249('=');
            if (method248.equals("contextType")) {
                if (n != 0) {
                    throw new IllegalArgumentException("contextType defined twice");
                }
                n = 1;
                d.aString466 = d.method248(new String[] { "ambient", "device", "user", "vehicle" });
            }
            else if (method248.equals("model")) {
                if (n2 != 0) {
                    throw new IllegalArgumentException("model defined twice");
                }
                n2 = 1;
                d.aString464 = d.method256();
            }
            else {
                if (!method248.equals("location")) {
                    continue;
                }
                if (n3 != 0) {
                    throw new IllegalArgumentException("location defined twice");
                }
                n3 = 1;
                d.aString467 = d.method256();
            }
        }
        return d;
    }
    
    public static String getUrl(final SensorInfo sensorInfo) {
        final StringBuffer sb;
        (sb = new StringBuffer("sensor:")).append(sensorInfo.getQuantity());
        if (sensorInfo.getContextType() != null) {
            sb.append(';');
            sb.append("contextType");
            sb.append('=');
            sb.append(sensorInfo.getContextType());
        }
        if (sensorInfo.getModel() != null) {
            sb.append(';');
            sb.append("model");
            sb.append('=');
            sb.append(sensorInfo.getModel());
        }
        String s = null;
        try {
            s = (String)sensorInfo.getProperty("location");
        }
        catch (IllegalArgumentException ex) {}
        if (s != null) {
            sb.append(';');
            sb.append("location");
            sb.append('=');
            sb.append(s);
        }
        return sb.toString();
    }
}
