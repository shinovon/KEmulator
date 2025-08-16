package com.nttdocomo.ui.ogl.math;

public final class Vector3f
        extends Tuple3f {
    public Vector3f() {
        super();
    }

    public Vector3f(float paramFloat1, float paramFloat2, float paramFloat3) {
        super(paramFloat1, paramFloat2, paramFloat3);
    }

    public void normalize() {
        float lenSq = x * x + y * y + z * z;
        if (lenSq > 0f) {
            float invLen = 1.0f / (float)Math.sqrt(lenSq);
            x *= invLen;
            y *= invLen;
            z *= invLen;
        }
    }

    public float dot(Vector3f v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public void cross(Vector3f v1, Vector3f v2) {
        x = v1.y * v2.z - v1.z * v2.y;
        y = v1.z * v2.x - v1.x * v2.z;
        z = v1.x * v2.y - v1.y * v2.x;
    }
}
