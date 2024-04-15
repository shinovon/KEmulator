package emulator.graphics3D;

import emulator.graphics3D.b;

import java.util.Arrays;

public class G3DUtils {
    private static final float FLOAT_EPSILON = 1e-6f;
    
    public static native boolean intelSSE_Invert4x4(float[] var0, float var1);
    public static boolean Invert4x4(float[] var0, float var1) {
        try {
            return intelSSE_Invert4x4(var0, var1);
        } catch (Error e) {
            // TODO
            mert4x4(var0, var1);
            return true;
        }
    }

    static void mert4x4(float m[], float epsilon) {
        // Adapted from The Mesa 3-D graphics library. 
        // Copyright (C) 1999-2007  Brian Paul   All Rights Reserved.
        // Published under the MIT license (see the header of this file)
        float m0 = m[0];
        float m1 = m[1];
        float m2 = m[2];
        float m3 = m[3];
        float m4 = m[4];
        float m5 = m[5];
        float m6 = m[6];
        float m7 = m[7];
        float m8 = m[8];
        float m9 = m[9];
        float mA = m[10];
        float mB = m[11];
        float mC = m[12];
        float mD = m[13];
        float mE = m[14];
        float mF = m[15];

        m[0] = m5 * mA * mF - m5 * mB * mE - m9 * m6 * mF + m9 * m7 * mE + mD * m6 * mB - mD * m7 * mA;
        m[4] = -m4 * mA * mF + m4 * mB * mE + m8 * m6 * mF - m8 * m7 * mE - mC * m6 * mB + mC * m7 * mA;
        m[8] = m4 * m9 * mF - m4 * mB * mD - m8 * m5 * mF + m8 * m7 * mD + mC * m5 * mB - mC * m7 * m9;
        m[12] = -m4 * m9 * mE + m4 * mA * mD + m8 * m5 * mE - m8 * m6 * mD - mC * m5 * mA + mC * m6 * m9;
        m[1] = -m1 * mA * mF + m1 * mB * mE + m9 * m2 * mF - m9 * m3 * mE - mD * m2 * mB + mD * m3 * mA;
        m[5] = m0 * mA * mF - m0 * mB * mE - m8 * m2 * mF + m8 * m3 * mE + mC * m2 * mB - mC * m3 * mA;
        m[9] = -m0 * m9 * mF + m0 * mB * mD + m8 * m1 * mF - m8 * m3 * mD - mC * m1 * mB + mC * m3 * m9;
        m[13] = m0 * m9 * mE - m0 * mA * mD - m8 * m1 * mE + m8 * m2 * mD + mC * m1 * mA - mC * m2 * m9;
        m[2] = m1 * m6 * mF - m1 * m7 * mE - m5 * m2 * mF + m5 * m3 * mE + mD * m2 * m7 - mD * m3 * m6;
        m[6] = -m0 * m6 * mF + m0 * m7 * mE + m4 * m2 * mF - m4 * m3 * mE - mC * m2 * m7 + mC * m3 * m6;
        m[10] = m0 * m5 * mF - m0 * m7 * mD - m4 * m1 * mF + m4 * m3 * mD + mC * m1 * m7 - mC * m3 * m5;
        m[14] = -m0 * m5 * mE + m0 * m6 * mD + m4 * m1 * mE - m4 * m2 * mD - mC * m1 * m6 + mC * m2 * m5;
        m[3] = -m1 * m6 * mB + m1 * m7 * mA + m5 * m2 * mB - m5 * m3 * mA - m9 * m2 * m7 + m9 * m3 * m6;
        m[7] = m0 * m6 * mB - m0 * m7 * mA - m4 * m2 * mB + m4 * m3 * mA + m8 * m2 * m7 - m8 * m3 * m6;
        m[11] = -m0 * m5 * mB + m0 * m7 * m9 + m4 * m1 * mB - m4 * m3 * m9 - m8 * m1 * m7 + m8 * m3 * m5;
        m[15] = m0 * m5 * mA - m0 * m6 * m9 - m4 * m1 * mA + m4 * m2 * m9 + m8 * m1 * m6 - m8 * m2 * m5;
        // (Ain't that pretty?)

        float det = m0 * m[0] + m1 * m[4] + m2 * m[8] + m3 * m[12];
        if (Math.abs(det) <= epsilon) {
            setIdentity4x4(m);
            return;
        }
        float mDet = 1.0f / det;
        for (int i = 0; i < 16; i++) {
            m[i] *= mDet;
        }
    }

    static void setIdentity4x4(float m[]) {
        Arrays.fill(m, 0.0f);
        m[0] = 1.0f;
        m[5] = 1.0f;
        m[10] = 1.0f;
        m[15] = 1.0f;
    }

    public static float method601(int var0, int var1) {
        return (float)(var0 >> var1 & 255) / 255.0F;
    }

    public static void method602(float[] var0, int var1) {
        var0[0] = (float)(var1 >> 16 & 255) / 255.0F;
        var0[1] = (float)(var1 >> 8 & 255) / 255.0F;
        var0[2] = (float)(var1 & 255) / 255.0F;
        var0[3] = (float)(var1 >> 24 & 255) / 255.0F;
    }

    public static int method603(float[] var0) {
        float[] var10000;
        int var1;
        byte var10001;
        int var2;
        int var3;
        int var5;
        if(var0.length == 1) {
            var1 = 255;
            var2 = 255;
            var3 = 255;
            var10000 = var0;
            var10001 = 0;
        } else {
            var3 = (int)(method610(var0[0]) * 255.0F + 0.5F);
            var2 = (int)(method610(var0[1]) * 255.0F + 0.5F);
            var1 = (int)(method610(var0[2]) * 255.0F + 0.5F);
            if(var0.length != 4) {
                var5 = 255;
                return (var5 << 24) + (var3 << 16) + (var2 << 8) + var1;
            }

            var10000 = var0;
            var10001 = 3;
        }

        var5 = (int)(method610(var10000[var10001]) * 255.0F + 0.5F);
        return (var5 << 24) + (var3 << 16) + (var2 << 8) + var1;
    }

    public static float method604(float var0) {
        return var0 >= 0.0F?var0:0.0F;
    }

    public static float method610(float var0) {
        return var0 >= 0.0F?(var0 <= 1.0F?var0:1.0F):0.0F;
    }

    public static float method605(float var0, float var1, float var2) {
        return var0 >= var1?(var0 <= var2?var0:var2):var1;
    }

    public static int method606(int var0, int var1, int var2) {
        return var0 >= var1?(var0 <= var2?var0:var2):var1;
    }

    public static int method607(float var0) {
        return var0 >= 0.0F?(int)(var0 + 0.5F):(int)(var0 - 0.5F);
    }

    public static final boolean method608(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int[] var8) {
        int var9 = var0 < var4?var4:var0;
        int var10000;
        int var10001;
        if(var0 + var2 > var4 + var6) {
            var10000 = var4;
            var10001 = var6;
        } else {
            var10000 = var0;
            var10001 = var2;
        }

        int var10;
        if((var10 = var10000 + var10001) - var9 < 0) {
            return false;
        } else {
            var8[0] = var9;
            var8[2] = var10 - var9;
            var9 = var1 < var5?var5:var1;
            if(var1 + var3 > var5 + var7) {
                var10000 = var5;
                var10001 = var7;
            } else {
                var10000 = var1;
                var10001 = var3;
            }

            if((var10 = var10000 + var10001) - var9 < 0) {
                return false;
            } else {
                var8[1] = var9;
                var8[3] = var10 - var9;
                return true;
            }
        }
    }

    public static final boolean method609(b var0, b var1, b var2, b var3, b var4, b var5, int var6) {
        b var7 = new b();
        b var8 = new b();
        b var9 = new b();
        b var10 = new b();
        b var11 = new b();
        var7.method426(var3, var2);
        var8.method426(var4, var2);
        var10.method430(var1, var8);
        float var12 = var7.method427(var10);
        if(var6 == 0 && var12 <= 0.0F) {
            return false;
        } else if(var6 == 1 && var12 >= 0.0F) {
            return false;
        } else if(var12 > -1.0E-5F && var12 < 1.0E-5F) {
            return false;
        } else {
            float var13 = 1.0F / var12;
            var9.method426(var0, var2);
            var5.aFloat610 = var9.method427(var10) * var13;
            if(var5.aFloat610 >= 0.0F && var5.aFloat610 <= 1.0F) {
                var11.method430(var9, var7);
                var5.aFloat612 = var1.method427(var11) * var13;
                if(var5.aFloat612 >= 0.0F && var5.aFloat610 + var5.aFloat612 <= 1.0F) {
                    var5.aFloat608 = var8.method427(var11) * var13;
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
