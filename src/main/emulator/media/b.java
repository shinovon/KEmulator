package emulator.media;

import emulator.*;

public final class b {
    a ana1225;
    int anInt1226;
    byte[] aByteArray1227;
    static int anInt1229;
    static int anInt1230;
    static final int[] anIntArray1228;

    public b(final byte[] array) {
        super();
        this.ana1225 = new a();
        this.anInt1226 = array.length;
        System.arraycopy(array, 0, this.aByteArray1227 = new byte[this.anInt1226 + 10], 0, this.anInt1226);
        this.method729(this.aByteArray1227);
    }

    public final byte[] method726() {
        return this.ana1225.method730();
    }

    private static int method727(final byte[] array, final int n) {
        final int n2 = ((array[b.anInt1229] & 0xFF) << 8) + (array[b.anInt1229 + 1] & 0xFF) >> 16 - (n + b.anInt1230) & (1 << n) - 1;
        b.anInt1230 += n;
        if (b.anInt1230 > 7) {
            b.anInt1230 -= 8;
            ++b.anInt1229;
        }
        return n2;
    }

    private static int method728(final int n) {
        return b.anIntArray1228[n];
    }

    private void method729(final byte[] array) {
        b.anInt1229 = 0;
        b.anInt1230 = 0;
        int n = 0;
        method727(array, 8);
        method727(array, 8);
        method727(array, 7);
        final int method727;
        if ((method727 = method727(array, 3)) != 1 && method727 != 2) {
            Emulator.getEmulator().getLogStream().println("Unsupported ringtone type");
            return;
        }
        for (int method728 = method727(array, 4), i = 0; i < method728; ++i) {
            method727(array, 8);
        }
        int method729 = method727(array, 8);
        while (b.anInt1229 < this.anInt1226) {
            if (method729 == 0) {
                break;
            }
            method727(array, 3);
            method727(array, 2);
            method727(array, 4);
            for (int method730 = method727(array, 8), n2 = 0; n2 < method730 && b.anInt1229 < this.anInt1226; ++n2) {
                switch (method727(array, 3)) {
                    case 0: {
                        method727(array, 2);
                        break;
                    }
                    case 1: {
                        if (n == 0) {
                            this.ana1225.method733();
                            n = 1;
                        }
                        this.ana1225.anInt1275 = method727(array, 4);
                        this.ana1225.anInt1277 = method727(array, 3);
                        this.ana1225.anInt1278 = method727(array, 2);
                        this.ana1225.method736();
                        break;
                    }
                    case 2: {
                        this.ana1225.anInt1279 = method727(array, 2);
                        if (this.ana1225.anInt1279 > 0) {
                            final a ana1225 = this.ana1225;
                            --ana1225.anInt1279;
                            break;
                        }
                        break;
                    }
                    case 3: {
                        method727(array, 2);
                        break;
                    }
                    case 4: {
                        this.ana1225.anInt1280 = method728(method727(array, 5));
                        if (n == 1) {
                            this.ana1225.method737();
                            break;
                        }
                        break;
                    }
                    case 5: {
                        this.ana1225.anInt1281 = method727(array, 4);
                        break;
                    }
                }
            }
            if (b.anInt1229 >= this.anInt1226) {
                break;
            }
            --method729;
        }
        this.ana1225.method738();
    }

    static {
        anIntArray1228 = new int[]{25, 28, 31, 35, 40, 45, 50, 56, 63, 70, 80, 90, 100, 112, 125, 140, 160, 180, 200, 225, 250, 285, 320, 355, 400, 450, 500, 565, 635, 715, 800, 900};
    }
}
