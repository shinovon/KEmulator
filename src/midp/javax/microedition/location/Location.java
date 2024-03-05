package javax.microedition.location;

public class Location {
    public static final int MTE_SATELLITE = 1;
    public static final int MTE_TIMEDIFFERENCE = 2;
    public static final int MTE_TIMEOFARRIVAL = 4;
    public static final int MTE_CELLID = 8;
    public static final int MTE_SHORTRANGE = 16;
    public static final int MTE_ANGLEOFARRIVAL = 32;
    public static final int MTY_TERMINALBASED = 65536;
    public static final int MTY_NETWORKBASED = 131072;
    public static final int MTA_ASSISTED = 262144;
    public static final int MTA_UNASSISTED = 524288;

    public boolean isValid() {
        return false;
    }

    public long getTimestamp() {
        return 0L;
    }

    public QualifiedCoordinates getQualifiedCoordinates() {
        return null;
    }

    public float getSpeed() {
        return Float.NaN;
    }

    public float getCourse() {
        return Float.NaN;
    }

    public int getLocationMethod() {
        return 0;
    }

    public AddressInfo getAddressInfo() {
        return null;
    }

    public String getExtraInfo(String mimetype) {
        return null;
    }
}
