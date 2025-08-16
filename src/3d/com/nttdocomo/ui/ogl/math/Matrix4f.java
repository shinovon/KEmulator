package com.nttdocomo.ui.ogl.math;

import emulator.graphics3D.G3DUtils;

public final class Matrix4f {
    public float[] m = new float[16];
    private static final float[] defaultMatrix = new float[]{1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};

    public Matrix4f() {
        setIdentity();
    }

    public Matrix4f(Matrix4f paramMatrix4f) {
        System.arraycopy(paramMatrix4f.m, 0, this.m, 0, 16);
    }

    public void mul(Matrix4f t) {
        float[] result = new float[16];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result[col * 4 + row] =
                        this.m[0 * 4 + row] * t.m[col * 4 + 0] +
                        this.m[1 * 4 + row] * t.m[col * 4 + 1] +
                        this.m[2 * 4 + row] * t.m[col * 4 + 2] +
                        this.m[3 * 4 + row] * t.m[col * 4 + 3];
            }
        }
        this.m = result;
    }

    public void mul(Matrix4f m1, Matrix4f m2) {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                this.m[col * 4 + row] =
                        m1.m[0 * 4 + row] * m2.m[col * 4 + 0] +
                        m1.m[1 * 4 + row] * m2.m[col * 4 + 1] +
                        m1.m[2 * 4 + row] * m2.m[col * 4 + 2] +
                        m1.m[3 * 4 + row] * m2.m[col * 4 + 3];
            }
        }
    }


    public void transform(Tuple4f v) {
        float x = v.x, y = v.y, z = v.z, w = v.w;
        v.x = m[0] * x + m[4] * y + m[8] * z + m[12] * w;
        v.y = m[1] * x + m[5] * y + m[9] * z + m[13] * w;
        v.z = m[2] * x + m[6] * y + m[10] * z + m[14] * w;
        v.w = m[3] * x + m[7] * y + m[11] * z + m[15] * w;
    }

    public void invert() {
        if (!G3DUtils.Invert4x4(this.m)) {
            throw new ArithmeticException();
        }
    }

    public void setIdentity() {
        System.arraycopy(defaultMatrix, 0, this.m, 0, 16);
    }

    public void translate(float x, float y, float z) {
        Matrix4f t = new Matrix4f();
        t.m[12] = x;
        t.m[13] = y;
        t.m[14] = z;
        this.mul(t);
    }

    public void rotate(float angle, float x, float y, float z) {
        float len = (float) Math.sqrt(x * x + y * y + z * z);
        if (len != 0f) {
            x /= len;
            y /= len;
            z /= len;
        }
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        float oneMinusC = 1.0f - c;

        Matrix4f r = new Matrix4f();
        r.m[0] = c + x * x * oneMinusC;
        r.m[4] = x * y * oneMinusC - z * s;
        r.m[8] = x * z * oneMinusC + y * s;

        r.m[1] = y * x * oneMinusC + z * s;
        r.m[5] = c + y * y * oneMinusC;
        r.m[9] = y * z * oneMinusC - x * s;

        r.m[2] = z * x * oneMinusC - y * s;
        r.m[6] = z * y * oneMinusC + x * s;
        r.m[10] = c + z * z * oneMinusC;

        this.mul(r);
    }

    public void scale(float x, float y, float z) {
        Matrix4f s = new Matrix4f();
        s.m[0] = x;
        s.m[5] = y;
        s.m[10] = z;
        this.mul(s);
    }

    public void lookAt(Point3f eye, Point3f look, Vector3f up) {
        float fx = look.x - eye.x;
        float fy = look.y - eye.y;
        float fz = look.z - eye.z;
        float rlf = 1.0f / (float) Math.sqrt(fx * fx + fy * fy + fz * fz);
        fx *= rlf;
        fy *= rlf;
        fz *= rlf;

        float rlu = 1.0f / (float) Math.sqrt(up.x * up.x + up.y * up.y + up.z * up.z);
        float upx = up.x * rlu;
        float upy = up.y * rlu;
        float upz = up.z * rlu;

        float sx = fy * upz - fz * upy;
        float sy = fz * upx - fx * upz;
        float sz = fx * upy - fy * upx;
        float rls = 1.0f / (float) Math.sqrt(sx * sx + sy * sy + sz * sz);
        sx *= rls;
        sy *= rls;
        sz *= rls;

        float ux = sy * fz - sz * fy;
        float uy = sz * fx - sx * fz;
        float uz = sx * fy - sy * fx;

        m[0] = sx;
        m[1] = ux;
        m[2] = -fx;
        m[3] = 0.0f;

        m[4] = sy;
        m[5] = uy;
        m[6] = -fy;
        m[7] = 0.0f;

        m[8] = sz;
        m[9] = uz;
        m[10] = -fz;
        m[11] = 0.0f;

        m[12] = -(sx * eye.x + sy * eye.y + sz * eye.z);
        m[13] = -(ux * eye.x + uy * eye.y + uz * eye.z);
        m[14] = (fx * eye.x + fy * eye.y + fz * eye.z);
        m[15] = 1.0f;
    }
}
