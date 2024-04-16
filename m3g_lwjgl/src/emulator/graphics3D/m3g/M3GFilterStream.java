package emulator.graphics3D.m3g;

import java.io.IOException;
import java.io.InputStream;

public class M3GFilterStream extends InputStream {
    private InputStream in;
    private int pos;

    public int read() throws IOException {
        ++this.pos;
        return this.in.read();
    }

    public final void resetPos() {
        this.pos = 0;
    }

    public final int getPos() {
        return this.pos;
    }

    public int available() throws IOException {
        return this.in.available();
    }

    public void close() throws IOException {
        this.in.close();
    }

    public M3GFilterStream(InputStream var1) {
        this.in = var1;
        this.resetPos();
    }
}
