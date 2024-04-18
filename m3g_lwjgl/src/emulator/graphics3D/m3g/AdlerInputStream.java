package emulator.graphics3D.m3g;

import java.io.IOException;
import java.io.InputStream;

public final class AdlerInputStream extends CountedInputStream {
    private int anInt1093 = 1;
    private int anInt1094 = 0;

    public final int read() throws IOException {
        int var1;
        int var2;
        if ((var2 = var1 = super.read()) < 0) {
            var2 += 256;
        }

        this.anInt1093 = (this.anInt1093 + var2) % '\ufff1';
        this.anInt1094 = (this.anInt1094 + this.anInt1093) % '\ufff1';
        return var1;
    }

    public final long getChecksum() {
        return ((long) this.anInt1094 << 16) + (long) this.anInt1093;
    }

    public AdlerInputStream(InputStream var1) {
        super(var1);
    }
}
