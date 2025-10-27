package emulator.sensor;

import javax.microedition.sensor.SensorInfo;
//https://github.com/hbao/phonemefeaturedevices/blob/78b194c67b3b21a9ec6a847972e6bf5dbafdde04/VirtualMachine/phoneme_feature/jsr256/src/share/classes/com/sun/javame/sensor/SensorUrl.java
public final class SensorUrl {
    private int currPos;
    private String parseUrl;
    private String model;
    private String quantity;
    private String contextType;
    private String location;

    private SensorUrl() {
        super();
    }

    public final String getModel() {
        return this.model;
    }

    public final String getQuantity() {
        return this.quantity;
    }

    public final String getContextType() {
        return this.contextType;
    }

    public final String getLocation() {
        return this.location;
    }

    private static boolean isAlphaNum(final char c) {
        return Character.isDigit(c) || Character.isLowerCase(c) || Character.isUpperCase(c) || isMark(c);
    }

    private static boolean isMark(final char c) {
        final char[] array = {'-', '_', '.', '!', '~', '*', '\'', '(', ')'};
        for (int i = 0; i < array.length; ++i) {
            if (c == array[i]) {
                return true;
            }
        }
        return false;
    }

    private String getId() throws IllegalArgumentException {
        if (this.currPos < 0 || this.currPos > this.parseUrl.length() - 1) {
            throw new IllegalArgumentException("Wrong position");
        }
        if (!isAlphaNum(this.parseUrl.charAt(this.currPos))) {
            throw new IllegalArgumentException("First symbol is not alphanum");
        }
        final StringBuffer sb = new StringBuffer();
        char char1;
        while (this.currPos < this.parseUrl.length() && isAlphaNum(char1 = this.parseUrl.charAt(this.currPos))) {
            sb.append(char1);
            ++this.currPos;
        }
        return sb.toString();
    }

    private String idCompareArr(final String[] array) throws IllegalArgumentException {
        final String method256 = this.getId();
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

    private void checkSeparator(final char c) throws IllegalArgumentException {
        if (this.currPos < 0 || this.currPos > this.parseUrl.length() - 1) {
            throw new IllegalArgumentException("Wrong position");
        }
        if (c != this.parseUrl.charAt(this.currPos++)) {
            throw new IllegalArgumentException("Current symbol is not separator url = \"" + this.parseUrl + "\" pos = " + this.currPos);
        }
    }

    public static SensorUrl parseUrl(final String aString463) {
        final SensorUrl d;
        (d = new SensorUrl()).parseUrl = aString463;
        if (!d.parseUrl.startsWith("sensor:")) {
            throw new IllegalArgumentException("Wrong scheme");
        }
        d.currPos = "sensor:".length();
        final int length = d.parseUrl.length();
        d.quantity = d.getId();
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        while (d.currPos < length) {
            d.checkSeparator(';');
            final String method248 = d.idCompareArr(new String[]{"contextType", "model", "location"});
            d.checkSeparator('=');
            if (method248.equals("contextType")) {
                if (n != 0) {
                    throw new IllegalArgumentException("contextType defined twice");
                }
                n = 1;
                d.contextType = d.idCompareArr(new String[]{"ambient", "device", "user", "vehicle"});
            } else if (method248.equals("model")) {
                if (n2 != 0) {
                    throw new IllegalArgumentException("model defined twice");
                }
                n2 = 1;
                d.model = d.getId();
            } else {
                if (!method248.equals("location")) {
                    continue;
                }
                if (n3 != 0) {
                    throw new IllegalArgumentException("location defined twice");
                }
                n3 = 1;
                d.location = d.getId();
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
            s = (String) sensorInfo.getProperty("location");
        } catch (IllegalArgumentException ignored) {
        }
        if (s != null) {
            sb.append(';');
            sb.append("location");
            sb.append('=');
            sb.append(s);
        }
        return sb.toString();
    }
}
