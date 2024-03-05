package com.mascotcapsule.micro3d.v3;

public class Vector3D {
    public int x;
    public int y;
    public int z;

    public Vector3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vector3D v) {
        set(v);
    }

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.y;
    }

    public final int getZ() {
        return this.z;
    }

    public final int innerProduct(Vector3D v) {
        if (v == null) {
            throw new NullPointerException();
        }
        return nDot(v);
    }

    public static int innerProduct(Vector3D v1, Vector3D v2) {
        if ((v1 == null) || (v2 == null)) {
            throw new NullPointerException();
        }
        return nDot2(v1, v2);
    }

    public final void outerProduct(Vector3D v) {
        if (v == null) {
            throw new NullPointerException();
        }
        nCross(v);
    }

    public static Vector3D outerProduct(Vector3D v1, Vector3D v2) {
        if ((v1 == null) || (v2 == null)) {
            throw new NullPointerException();
        }
        Vector3D r = new Vector3D();
        nCross2(v1, v2, r);
        return r;
    }

    public final void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final void set(Vector3D v) {
        if (v == null) {
            throw new NullPointerException();
        }
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public final void setX(int x) {
        this.x = x;
    }

    public final void setY(int y) {
        this.y = y;
    }

    public final void setZ(int z) {
        this.z = z;
    }

    static {
        System.loadLibrary("java_micro3d_v3_32");
        nInitClass();
    }

    public Vector3D() {
    }

    public final native void unit();

    private native void nCross(Vector3D paramVector3D);

    private native int nDot(Vector3D paramVector3D);

    private static native void nCross2(Vector3D paramVector3D1, Vector3D paramVector3D2, Vector3D paramVector3D3);

    private static native int nDot2(Vector3D paramVector3D1, Vector3D paramVector3D2);

    private static native void nInitClass();
}
