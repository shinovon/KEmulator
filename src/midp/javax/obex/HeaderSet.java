package javax.obex;

import java.io.*;

public interface HeaderSet {
    public static final int COUNT = 192;
    public static final int NAME = 1;
    public static final int TYPE = 66;
    public static final int LENGTH = 195;
    public static final int TIME_ISO_8601 = 68;
    public static final int TIME_4_BYTE = 196;
    public static final int DESCRIPTION = 5;
    public static final int TARGET = 70;
    public static final int HTTP = 71;
    public static final int WHO = 74;
    public static final int OBJECT_CLASS = 79;
    public static final int APPLICATION_PARAMETER = 76;

    void setHeader(final int p0, final Object p1);

    Object getHeader(final int p0) throws IOException;

    int[] getHeaderList() throws IOException;

    void createAuthenticationChallenge(final String p0, final boolean p1, final boolean p2);

    int getResponseCode() throws IOException;
}
