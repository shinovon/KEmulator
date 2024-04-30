package emulator.graphics3D.m3g;

import java.io.IOException;
import java.io.InputStream;

public class CountedInputStream extends InputStream {
    private InputStream in;
    private int counter;

    public int read() throws IOException {
        ++this.counter;
        return this.in.read();
    }

    public final void resetCounter() {
        this.counter = 0;
    }

    public final int getCounter() {
        return this.counter;
    }

    public int available() throws IOException {
        return this.in.available();
    }

    public void close() throws IOException {
        this.in.close();
    }

    public CountedInputStream(InputStream var1) {
        this.in = var1;
        this.resetCounter();
    }
}
