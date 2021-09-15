package emulator.media.amr;

public final class c
{
    private int anInt832;
    private byte[] aByteArray833;
    
    public c(final byte[] aByteArray833) {
        super();
        this.anInt832 = 0;
        this.aByteArray833 = aByteArray833;
    }
    
    public final int a(final int n) {
        final int method466 = this.a(this.anInt832, n);
        this.anInt832 += n;
        return method466;
    }
    
    private int a(final int n, final int n2) {
        final int n3 = n / 8;
        final int n4 = (n + n2) / 8;
        int n5 = 0;
        int n6 = n3;
        do {
            final byte b;
            n5 |= (((b = this.aByteArray833[n6]) >= 0) ? b : (b + 256)) << (3 - (n6 - n3)) * 8;
        } while (++n6 <= n4 && n6 < this.aByteArray833.length);
        return n5 >>> 32 - (n % 8 + n2) & (int)Math.pow(2.0, n2) - 1;
    }
}
