package com.nttdocomo.ui.ogl.math;

public abstract class Tuple4f {
    public float x;
    public float y;
    public float z;
    public float w;

    public Tuple4f() {
    }

    public Tuple4f(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
        this.x = paramFloat1;
        this.y = paramFloat2;
        this.z = paramFloat3;
        this.w = paramFloat4;
    }

    public void add(Tuple4f t) {
        x += t.x;
        y += t.y;
        z += t.z;
        w += t.w;
    }

    public void mul(Matrix4f t) {
        float nx = t.m[0] * x + t.m[4] * y + t.m[8]  * z + t.m[12] * w;
        float ny = t.m[1] * x + t.m[5] * y + t.m[9]  * z + t.m[13] * w;
        float nz = t.m[2] * x + t.m[6] * y + t.m[10] * z + t.m[14] * w;
        float nw = t.m[3] * x + t.m[7] * y + t.m[11] * z + t.m[15] * w;
        x = nx;
        y = ny;
        z = nz;
        w = nw;
    }
}
