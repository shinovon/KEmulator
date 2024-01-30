package com.siemens.mp.media.protocol;

import com.siemens.mp.media.Controllable;

import java.io.IOException;

public abstract interface SourceStream
        extends Controllable {
    public static final int NOT_SEEKABLE = 0;
    public static final int SEEKABLE_TO_START = 1;
    public static final int RANDOM_ACCESSIBLE = 2;

    public abstract ContentDescriptor getContentDescriptor();

    public abstract long getContentLength();

    public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
            throws IOException;

    public abstract int getTransferSize();

    public abstract long seek(long paramLong)
            throws IOException;

    public abstract long tell();

    public abstract int getSeekType();
}
