package javax.bluetooth;

import java.io.*;

public class BluetoothConnectionException extends IOException {
    private static final long serialVersionUID = 1L;
    int anInt1392;
    public static final int UNKNOWN_PSM = 1;
    public static final int SECURITY_BLOCK = 2;
    public static final int NO_RESOURCES = 3;
    public static final int FAILED_NOINFO = 4;
    public static final int TIMEOUT = 5;
    public static final int UNACCEPTABLE_PARAMS = 6;

    public BluetoothConnectionException(final int n) {
        this(n, null);
    }

    public BluetoothConnectionException(final int n, final String s) {
        super(method830(n, s));
    }

    public int getStatus() {
        return this.anInt1392;
    }

    private static final String method830(final int n, final String s) {
        String s2 = null;
        switch (n) {
            case 1: {
                s2 = "UNKNOWN_PSM";
                break;
            }
            case 2: {
                s2 = "SECURITY_BLOCK";
                break;
            }
            case 3: {
                s2 = "NO_RESOURCES";
                break;
            }
            case 4: {
                s2 = "FAILED_NOINFO";
                break;
            }
            case 5: {
                s2 = "TIMEOUT";
                break;
            }
            case 6: {
                s2 = "UNACCEPTABLE_PARAMS";
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid error code");
            }
        }
        final String s3;
        return s3 = ((s != null) ? (s2 + ":" + s) : s2);
    }
}
