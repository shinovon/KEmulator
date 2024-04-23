package emulator.graphics3D;

public final class Transform3D {
    public float[] m_matrix = new float[16];
    private static final float[] defaultMatrix = new float[]{1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    private float[] aFloatArray617 = new float[4];

    public Transform3D() {
        this.setIdentity();
    }

    public final void setIdentity() {
        System.arraycopy(defaultMatrix, 0, this.m_matrix, 0, 16);
    }

    public final void get(float[] var1) {
        System.arraycopy(this.m_matrix, 0, var1, 0, 16);
    }

    public final void set(float[] var1) {
        System.arraycopy(var1, 0, this.m_matrix, 0, 16);
    }

    public final void set(Transform3D var1) {
        System.arraycopy(var1.m_matrix, 0, this.m_matrix, 0, 16);
    }

    public final void invert() {
//        if (!G3DUtils.Invert4x4(this.m_matrix, 1.0E-10F)) {
        if (!G3DUtils.Invert4x4(this.m_matrix)) {
            throw new ArithmeticException();
        }
    }

    public final void transpose() {
        this.swapAt(1, 4);
        this.swapAt(2, 8);
        this.swapAt(3, 12);
        this.swapAt(7, 13);
        this.swapAt(11, 14);
        this.swapAt(6, 9);
    }

    private void swapAt(int a, int b) {
        float tmp = this.m_matrix[a];
        this.m_matrix[a] = this.m_matrix[b];
        this.m_matrix[b] = tmp;
    }

    public final void postMultiply(Transform3D var1, boolean var2) {
        float[] var3 = new float[16];
        float[] var4 = (var2 ? this : var1).m_matrix;
        float[] var5 = (var2 ? var1 : this).m_matrix;

        for (int var6 = 0; var6 < 4; ++var6) {
            for (int var7 = 0; var7 < 4; ++var7) {
                int var8 = var7 << 2;
                var3[var8 + var6] += var4[0 + var6] * var5[var8 + 0];
                var3[var8 + var6] += var4[4 + var6] * var5[var8 + 1];
                var3[var8 + var6] += var4[8 + var6] * var5[var8 + 2];
                var3[var8 + var6] += var4[12 + var6] * var5[var8 + 3];
            }
        }

        System.arraycopy(var3, 0, this.m_matrix, 0, 16);
    }

    public final void postRotate(float var1, float var2, float var3, float var4) {
        Quaternion var5;
        (var5 = new Quaternion()).setAngleAxis(var1, var2, var3, var4);
        this.postRotateQuat(var5.x, var5.y, var5.z, var5.w);
    }

    public final void postRotateQuat(float var1, float var2, float var3, float var4) {
        Quaternion var5;
        (var5 = new Quaternion(var1, var2, var3, var4)).normalize();
        Transform3D var6;
        float[] var7 = (var6 = new Transform3D()).m_matrix;
        float var8 = var5.x * var5.x;
        float var9 = var5.x * var5.y;
        float var10 = var5.x * var5.z;
        float var11 = var5.x * var5.w;
        float var12 = var5.y * var5.y;
        float var13 = var5.y * var5.z;
        float var14 = var5.y * var5.w;
        float var15 = var5.z * var5.z;
        float var16 = var5.z * var5.w;
        var7[0] = 1.0F - 2.0F * (var12 + var15);
        var7[1] = 2.0F * (var9 - var16);
        var7[2] = 2.0F * (var10 + var14);
        var7[4] = 2.0F * (var9 + var16);
        var7[5] = 1.0F - 2.0F * (var8 + var15);
        var7[6] = 2.0F * (var13 - var11);
        var7[8] = 2.0F * (var10 - var14);
        var7[9] = 2.0F * (var13 + var11);
        var7[10] = 1.0F - 2.0F * (var8 + var12);
        this.postMultiply(var6, false);
    }

    public final void postScale(float var1, float var2, float var3) {
        this.m_matrix[0] *= var1;
        this.m_matrix[1] *= var2;
        this.m_matrix[2] *= var3;
        this.m_matrix[4] *= var1;
        this.m_matrix[5] *= var2;
        this.m_matrix[6] *= var3;
        this.m_matrix[8] *= var1;
        this.m_matrix[9] *= var2;
        this.m_matrix[10] *= var3;
        this.m_matrix[12] *= var1;
        this.m_matrix[13] *= var2;
        this.m_matrix[14] *= var3;
    }

    public final void postTranslate(float var1, float var2, float var3) {
        this.m_matrix[3] += this.m_matrix[0] * var1 + this.m_matrix[1] * var2 + this.m_matrix[2] * var3;
        this.m_matrix[7] += this.m_matrix[4] * var1 + this.m_matrix[5] * var2 + this.m_matrix[6] * var3;
        this.m_matrix[11] += this.m_matrix[8] * var1 + this.m_matrix[9] * var2 + this.m_matrix[10] * var3;
        this.m_matrix[15] += this.m_matrix[12] * var1 + this.m_matrix[13] * var2 + this.m_matrix[14] * var3;
    }

    private void method439(float[] var1, int var2) {
        int var3 = 0;

        for (int var4 = 0; var4 < 4; ++var4) {
            float var5 = (var5 = 0.0F + this.m_matrix[var3 + 0] * var1[var2 + 0]) + this.m_matrix[var3 + 1] * var1[var2 + 1] + this.m_matrix[var3 + 2] * var1[var2 + 2] + this.m_matrix[var3 + 3] * var1[var2 + 3];
            this.aFloatArray617[var4] = var5;
            var3 += 4;
        }

        System.arraycopy(this.aFloatArray617, 0, var1, var2, 4);
    }

    public final void transform(float[] var1) {
        for (int var2 = 0; var2 + 4 <= var1.length; var2 += 4) {
            this.method439(var1, var2);
        }

    }

    public final void transform(Vector4f var1) {
        float var2 = this.m_matrix[0] * var1.x + this.m_matrix[1] * var1.y + this.m_matrix[2] * var1.z + this.m_matrix[3] * var1.w;
        float var3 = this.m_matrix[4] * var1.x + this.m_matrix[5] * var1.y + this.m_matrix[6] * var1.z + this.m_matrix[7] * var1.w;
        float var4 = this.m_matrix[8] * var1.x + this.m_matrix[9] * var1.y + this.m_matrix[10] * var1.z + this.m_matrix[11] * var1.w;
        float var5 = this.m_matrix[12] * var1.x + this.m_matrix[13] * var1.y + this.m_matrix[14] * var1.z + this.m_matrix[15] * var1.w;
        var1.x = var2;
        var1.y = var3;
        var1.z = var4;
        var1.w = var5;
    }
}
