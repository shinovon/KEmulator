package com.mascotcapsule.micro3d.v3;

public class Vector3D {
    public int x;
    public int y;
    public int z;

    public Vector3D() {
    }

    public Vector3D(Vector3D paramVector3D) {
        set(paramVector3D);
    }

    public Vector3D(int paramInt1, int paramInt2, int paramInt3) {
        this.x = paramInt1;
        this.y = paramInt2;
        this.z = paramInt3;
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

    public final void setX(int paramInt) {
        this.x = paramInt;
    }

    public final void setY(int paramInt) {
        this.y = paramInt;
    }

    public final void setZ(int paramInt) {
        this.z = paramInt;
    }

    public final void set(Vector3D paramVector3D) {
    }

    public final void set(int paramInt1, int paramInt2, int paramInt3) {
    }

    public final void unit() {
    }

    public final int innerProduct(Vector3D paramVector3D) {
        return 0;
    }

    public final void outerProduct(Vector3D paramVector3D) {
    }

    public static final int innerProduct(Vector3D paramVector3D1, Vector3D paramVector3D2) {
      return 0;
    }

    public static final Vector3D outerProduct(Vector3D paramVector3D1, Vector3D paramVector3D2) {
      return null;
    }
}
