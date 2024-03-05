package javax.microedition.media.protocol;

import javax.microedition.media.*;
import java.io.*;

public interface SourceStream extends Controllable {
    public static final int NOT_SEEKABLE = 0;
    public static final int SEEKABLE_TO_START = 1;
    public static final int RANDOM_ACCESSIBLE = 2;

    ContentDescriptor getContentDescriptor();

    long getContentLength();

    int read(final byte[] p0, final int p1, final int p2) throws IOException;

    int getTransferSize();

    long seek(final long p0) throws IOException;

    long tell();

    int getSeekType();
}
