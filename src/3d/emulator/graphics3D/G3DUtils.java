package emulator.graphics3D;

public class G3DUtils {

//    public static native boolean intelSSE_Invert4x4(float[] var0, float var1);

    public static boolean Invert4x4(float[] var0) {
        float[] var1 = new float[12];
        float[] var2 = new float[16];
        float[] var3 = new float[16];

        for(int var4 = 0; var4 < 4; ++var4) {
            var2[var4 + 0] = var0[var4 * 4 + 0];
            var2[var4 + 4] = var0[var4 * 4 + 1];
            var2[var4 + 8] = var0[var4 * 4 + 2];
            var2[var4 + 12] = var0[var4 * 4 + 3];
        }

        var1[0] = var2[10] * var2[15];
        var1[1] = var2[11] * var2[14];
        var1[2] = var2[9] * var2[15];
        var1[3] = var2[11] * var2[13];
        var1[4] = var2[9] * var2[14];
        var1[5] = var2[10] * var2[13];
        var1[6] = var2[8] * var2[15];
        var1[7] = var2[11] * var2[12];
        var1[8] = var2[8] * var2[14];
        var1[9] = var2[10] * var2[12];
        var1[10] = var2[8] * var2[13];
        var1[11] = var2[9] * var2[12];
        var3[0] = var1[0] * var2[5] + var1[3] * var2[6] + var1[4] * var2[7];
        var3[0] -= var1[1] * var2[5] + var1[2] * var2[6] + var1[5] * var2[7];
        var3[1] = var1[1] * var2[4] + var1[6] * var2[6] + var1[9] * var2[7];
        var3[1] -= var1[0] * var2[4] + var1[7] * var2[6] + var1[8] * var2[7];
        var3[2] = var1[2] * var2[4] + var1[7] * var2[5] + var1[10] * var2[7];
        var3[2] -= var1[3] * var2[4] + var1[6] * var2[5] + var1[11] * var2[7];
        var3[3] = var1[5] * var2[4] + var1[8] * var2[5] + var1[11] * var2[6];
        var3[3] -= var1[4] * var2[4] + var1[9] * var2[5] + var1[10] * var2[6];
        var3[4] = var1[1] * var2[1] + var1[2] * var2[2] + var1[5] * var2[3];
        var3[4] -= var1[0] * var2[1] + var1[3] * var2[2] + var1[4] * var2[3];
        var3[5] = var1[0] * var2[0] + var1[7] * var2[2] + var1[8] * var2[3];
        var3[5] -= var1[1] * var2[0] + var1[6] * var2[2] + var1[9] * var2[3];
        var3[6] = var1[3] * var2[0] + var1[6] * var2[1] + var1[11] * var2[3];
        var3[6] -= var1[2] * var2[0] + var1[7] * var2[1] + var1[10] * var2[3];
        var3[7] = var1[4] * var2[0] + var1[9] * var2[1] + var1[10] * var2[2];
        var3[7] -= var1[5] * var2[0] + var1[8] * var2[1] + var1[11] * var2[2];
        var1[0] = var2[2] * var2[7];
        var1[1] = var2[3] * var2[6];
        var1[2] = var2[1] * var2[7];
        var1[3] = var2[3] * var2[5];
        var1[4] = var2[1] * var2[6];
        var1[5] = var2[2] * var2[5];
        var1[6] = var2[0] * var2[7];
        var1[7] = var2[3] * var2[4];
        var1[8] = var2[0] * var2[6];
        var1[9] = var2[2] * var2[4];
        var1[10] = var2[0] * var2[5];
        var1[11] = var2[1] * var2[4];
        var3[8] = var1[0] * var2[13] + var1[3] * var2[14] + var1[4] * var2[15];
        var3[8] -= var1[1] * var2[13] + var1[2] * var2[14] + var1[5] * var2[15];
        var3[9] = var1[1] * var2[12] + var1[6] * var2[14] + var1[9] * var2[15];
        var3[9] -= var1[0] * var2[12] + var1[7] * var2[14] + var1[8] * var2[15];
        var3[10] = var1[2] * var2[12] + var1[7] * var2[13] + var1[10] * var2[15];
        var3[10] -= var1[3] * var2[12] + var1[6] * var2[13] + var1[11] * var2[15];
        var3[11] = var1[5] * var2[12] + var1[8] * var2[13] + var1[11] * var2[14];
        var3[11] -= var1[4] * var2[12] + var1[9] * var2[13] + var1[10] * var2[14];
        var3[12] = var1[2] * var2[10] + var1[5] * var2[11] + var1[1] * var2[9];
        var3[12] -= var1[4] * var2[11] + var1[0] * var2[9] + var1[3] * var2[10];
        var3[13] = var1[8] * var2[11] + var1[0] * var2[8] + var1[7] * var2[10];
        var3[13] -= var1[6] * var2[10] + var1[9] * var2[11] + var1[1] * var2[8];
        var3[14] = var1[6] * var2[9] + var1[11] * var2[11] + var1[3] * var2[8];
        var3[14] -= var1[10] * var2[11] + var1[2] * var2[8] + var1[7] * var2[9];
        var3[15] = var1[10] * var2[10] + var1[4] * var2[8] + var1[9] * var2[9];
        var3[15] -= var1[8] * var2[9] + var1[11] * var2[10] + var1[5] * var2[8];
        float var6 = var2[0] * var3[0] + var2[1] * var3[1] + var2[2] * var3[2] + var2[3] * var3[3];
        var6 = 1.0F / var6;

        for(int var5 = 0; var5 < 16; ++var5) {
            var0[var5] = var3[var5] * var6;
        }

        return true;
    }

    public static float getFloatColor(int var0, int var1) {
        return (float) (var0 >> var1 & 255) / 255.0F;
    }

    public static void fillFloatColor(float[] var0, int var1) {
        var0[0] = (float) (var1 >> 16 & 255) / 255.0F;
        var0[1] = (float) (var1 >> 8 & 255) / 255.0F;
        var0[2] = (float) (var1 & 255) / 255.0F;
        var0[3] = (float) (var1 >> 24 & 255) / 255.0F;
    }

    public static int getIntColor(float[] var0) {
        float[] var10000;
        int var1;
        byte var10001;
        int var2;
        int var3;
        int var5;
        if (var0.length == 1) {
            var1 = 255;
            var2 = 255;
            var3 = 255;
            var10000 = var0;
            var10001 = 0;
        } else {
            var3 = (int) (limit(var0[0]) * 255.0F + 0.5F);
            var2 = (int) (limit(var0[1]) * 255.0F + 0.5F);
            var1 = (int) (limit(var0[2]) * 255.0F + 0.5F);
            if (var0.length != 4) {
                var5 = 255;
                return (var5 << 24) + (var3 << 16) + (var2 << 8) + var1;
            }

            var10000 = var0;
            var10001 = 3;
        }

        var5 = (int) (limit(var10000[var10001]) * 255.0F + 0.5F);
        return (var5 << 24) + (var3 << 16) + (var2 << 8) + var1;
    }

    public static float limitPositive(float var0) {
        return var0 >= 0.0F ? var0 : 0.0F;
    }

    public static float limit(float var0) {
        return var0 >= 0.0F ? (var0 <= 1.0F ? var0 : 1.0F) : 0.0F;
    }

    public static float limit(float var0, float var1, float var2) {
        return var0 >= var1 ? (var0 <= var2 ? var0 : var2) : var1;
    }

    public static int limit(int var0, int var1, int var2) {
        return var0 >= var1 ? (var0 <= var2 ? var0 : var2) : var1;
    }

    public static int round(float var0) {
        return var0 >= 0.0F ? (int) (var0 + 0.5F) : (int) (var0 - 0.5F);
    }

    public static final boolean intersectRectangle(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int[] var8) {
        int var9 = var0 < var4 ? var4 : var0;
        int var10000;
        int var10001;
        if (var0 + var2 > var4 + var6) {
            var10000 = var4;
            var10001 = var6;
        } else {
            var10000 = var0;
            var10001 = var2;
        }

        int var10;
        if ((var10 = var10000 + var10001) - var9 < 0) {
            return false;
        } else {
            var8[0] = var9;
            var8[2] = var10 - var9;
            var9 = var1 < var5 ? var5 : var1;
            if (var1 + var3 > var5 + var7) {
                var10000 = var5;
                var10001 = var7;
            } else {
                var10000 = var1;
                var10001 = var3;
            }

            if ((var10 = var10000 + var10001) - var9 < 0) {
                return false;
            } else {
                var8[1] = var9;
                var8[3] = var10 - var9;
                return true;
            }
        }
    }

    public static final boolean intersectTriangle(Vector4f var0, Vector4f var1, Vector4f var2, Vector4f var3, Vector4f var4, Vector4f var5, int var6) {
        Vector4f var7 = new Vector4f();
        Vector4f var8 = new Vector4f();
        Vector4f var9 = new Vector4f();
        Vector4f var10 = new Vector4f();
        Vector4f var11 = new Vector4f();
        var7.sub(var3, var2);
        var8.sub(var4, var2);
        var10.cross(var1, var8);
        float var12 = var7.dot(var10);
        if (var6 == 0 && var12 <= 0.0F) {
            return false;
        } else if (var6 == 1 && var12 >= 0.0F) {
            return false;
        } else if (var12 > -1.0E-5F && var12 < 1.0E-5F) {
            return false;
        } else {
            float var13 = 1.0F / var12;
            var9.sub(var0, var2);
            var5.y = var9.dot(var10) * var13;
            if (var5.y >= 0.0F && var5.y <= 1.0F) {
                var11.cross(var9, var7);
                var5.z = var1.dot(var11) * var13;
                if (var5.z >= 0.0F && var5.y + var5.z <= 1.0F) {
                    var5.x = var8.dot(var11) * var13;
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
