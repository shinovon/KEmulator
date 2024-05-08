package com.nokia.mid.payment;

import java.io.IOException;
import java.io.InputStream;

public class DrmResourceInputStream extends InputStream {
    public int read() throws IOException {
        return 0;
    }

    public synchronized void close() throws IOException {
    }

    public int available() throws IOException {
        return 0;
    }

    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
        return 0;
    }

    public synchronized void mark(int paramInt) {
    }

    public boolean markSupported() {
        return false;
    }

    public synchronized void reset() throws IOException {
    }

    public long skip(long paramLong) throws IOException {
        return 0;
    }
}
