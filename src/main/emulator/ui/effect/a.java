package emulator.ui.effect;

import java.util.*;

public final class a {
    int anInt316;
    int anInt320;
    boolean aBoolean317;
    int anInt322;
    public int anInt324;
    public int anInt325;
    int[] anIntArray318;
    int[] anIntArray321;
    int[] anIntArray323;
    Random aRandom319;

    public a() {
        super();
        this.anIntArray318 = null;
        this.anIntArray321 = null;
        this.anInt316 = 0;
        this.anInt320 = 0;
        this.aBoolean317 = true;
        this.anInt322 = 1;
        this.anInt324 = 0;
        this.anInt325 = 5;
        this.aRandom319 = new Random();
    }

    public final void method135(final int anInt316, final int anInt317) {
        if (this.anIntArray318 != null) {
            this.anIntArray318 = null;
        }
        if (this.anIntArray321 != null) {
            this.anIntArray321 = null;
        }
        if (this.anIntArray323 != null) {
            this.anIntArray323 = null;
        }
        this.anIntArray318 = new int[anInt316 * anInt317];
        this.anIntArray321 = new int[anInt316 * anInt317];
        this.anIntArray323 = new int[anInt316 * anInt317];
        this.anInt316 = anInt316;
        this.anInt320 = anInt317;
        this.anInt324 = 0;
    }

    public final void method136(final int[] array, final int[] array2) {
        if (!this.aBoolean317) {
            this.method140(array, array2);
        } else {
            this.method141(array, array2);
        }
        this.method139(this.anInt324, this.anInt325);
        this.anInt324 ^= 1;
    }
    /*
    private void method139(final int n, final int n2) {
        int i = this.anInt316 + 1;
        int[] array;
        int[] array2;
        if (n == 0) {
            array = this.anIntArray318;
            array2 = this.anIntArray321;
        }
        else {
            array = this.anIntArray321;
            array2 = this.anIntArray318;
        }
        final int[] array3 = array2;
        while (i < (this.anInt320 - 1) * this.anInt316) {
            while (i < i + this.anInt316 - 2) {
                final int n3 = (array3[i + this.anInt316] + array3[i - this.anInt316] + array3[i + 1] + array3[i - 1] + array3[i - this.anInt316 - 1] + array3[i - this.anInt316 + 1] + array3[i + this.anInt316 - 1] + array3[i + this.anInt316 + 1] >> 2) - array[i];
                array[i] = n3 - (n3 >> n2);
                ++i;
            }
            i += 2;
        }
    }*/

    private void method139(int var1, int var2) {
        int var3 = this.anInt316 + 1;
        int[] var10000;
        int[] var4;
        if (var1 == 0) {
            var4 = this.anIntArray318;
            var10000 = this.anIntArray321;
        } else {
            var4 = this.anIntArray321;
            var10000 = this.anIntArray318;
        }

        int[] var5 = var10000;

        for (int var6 = (this.anInt320 - 1) * this.anInt316; var3 < var6; var3 += 2) {
            for (int var7 = var3 + this.anInt316 - 2; var3 < var7; ++var3) {
                int var8 = (var5[var3 + this.anInt316] + var5[var3 - this.anInt316] + var5[var3 + 1] + var5[var3 - 1] + var5[var3 - this.anInt316 - 1] + var5[var3 - this.anInt316 + 1] + var5[var3 + this.anInt316 - 1] + var5[var3 + this.anInt316 + 1] >> 2) - var4[var3];
                var4[var3] = var8 - (var8 >> var2);
            }
        }

    }


    public final void method137(int n, int n2, final int n3, final int n4, final int n5) {
        final int[] array = (n5 == 0) ? this.anIntArray318 : this.anIntArray321;
        final int n6 = n3 * n3;
        if (n < 0) {
            n = 1 + n3 + this.aRandom319.nextInt() % (this.anInt316 - 2 * n3 - 1);
        }
        if (n2 < 0) {
            n2 = 1 + n3 + this.aRandom319.nextInt() % (this.anInt320 - 2 * n3 - 1);
        }
        int n7 = -n3;
        int n8 = n3;
        int n9 = -n3;
        int n10 = n3;
        if (n - n3 < 1) {
            n7 -= n - n3 - 1;
        }
        if (n2 - n3 < 1) {
            n9 -= n2 - n3 - 1;
        }
        if (n + n3 > this.anInt316 - 1) {
            n8 -= n + n3 - this.anInt316 + 1;
        }
        if (n2 + n3 > this.anInt320 - 1) {
            n10 -= n2 + n3 - this.anInt320 + 1;
        }
        for (int i = n9; i < n10; ++i) {
            final int n11 = i * i;
            for (int j = n7; j < n8; ++j) {
                if (j * j + n11 < n6) {
                    final int n12 = this.anInt316 * (i + n2) + (j + n);
                    array[n12] += n4;
                }
            }
        }
    }

    private void method140(final int[] array, final int[] array2) {
        int i = this.anInt316 + 1;
        final int[] anIntArray318 = this.anIntArray318;
        while (i < (this.anInt320 - 1) * this.anInt316) {
            while (i < i + this.anInt316 - 2) {
                array2[i] = array[i + this.anInt316 * (anIntArray318[i] - anIntArray318[i + this.anInt316] >> 3) + (anIntArray318[i] - anIntArray318[i + 1] >> 3)];
                ++i;
                array2[i] = array[i + this.anInt316 * (anIntArray318[i] - anIntArray318[i + this.anInt316] >> 3) + (anIntArray318[i] - anIntArray318[i + 1] >> 3)];
                ++i;
            }
            i += 2;
        }
    }

    /*
    private void method141(final int[] array, final int[] array2) {
        int i = this.anInt316 + 1;
        final int n = this.anInt316 * this.anInt320;
        final int[] anIntArray318 = this.anIntArray318;
        while (i < (this.anInt320 - 1) * this.anInt316) {
            while (i < i + this.anInt316 - 2) {
                final int n2 = anIntArray318[i] - anIntArray318[i + 1];
                final int n3;
                if ((n3 = i + this.anInt316 * (anIntArray318[i] - anIntArray318[i + this.anInt316] >> 3) + (n2 >> 3)) < n && n3 > 0) {
                    array2[i] = method138(array[n3], n2);
                }
                ++i;
                final int n4 = anIntArray318[i] - anIntArray318[i + 1];
                final int n5;
                if ((n5 = i + this.anInt316 * (anIntArray318[i] - anIntArray318[i + this.anInt316] >> 3) + (n4 >> 3)) < n && n5 > 0) {
                    array2[i] = method138(array[n5], n4);
                }
                ++i;
            }
            i += 2;
        }
    }

    */
    private void method141(int[] var1, int[] var2) {
        int var3 = this.anInt316 + 1;
        int var4 = this.anInt316 * this.anInt320;
        int[] var5 = this.anIntArray318;

        for (int var6 = (this.anInt320 - 1) * this.anInt316; var3 < var6; var3 += 2) {
            for (int var7 = var3 + this.anInt316 - 2; var3 < var7; ++var3) {
                int var8 = var5[var3] - var5[var3 + 1];
                int var9 = var5[var3] - var5[var3 + this.anInt316];
                int var10;
                int var11;
                if ((var10 = var3 + this.anInt316 * (var9 >> 3) + (var8 >> 3)) < var4 && var10 > 0) {
                    var11 = method138(var1[var10], var8);
                    var2[var3] = var11;
                }

                ++var3;
                var8 = var5[var3] - var5[var3 + 1];
                var9 = var5[var3] - var5[var3 + this.anInt316];
                if ((var10 = var3 + this.anInt316 * (var9 >> 3) + (var8 >> 3)) < var4 && var10 > 0) {
                    var11 = method138(var1[var10], var8);
                    var2[var3] = var11;
                }
            }
        }
    }


    private static int method138(final int n, final int n2) {
        final int n3 = (n >> 16 & 0xFF) - n2;
        final int n4 = (n >> 8 & 0xFF) - n2;
        final int n5 = (n & 0xFF) - n2;
        return -16777216 + ((((n3 < 0) ? 0 : ((n3 > 255) ? 255 : n3)) & 0xFF) << 16) + ((((n4 < 0) ? 0 : ((n4 > 255) ? 255 : n4)) & 0xFF) << 8) + (((n5 < 0) ? 0 : ((n5 > 255) ? 255 : n5)) & 0xFF);
    }
}
