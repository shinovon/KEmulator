package com.mascotcapsule.micro3d.v3;

public class Util3D {
    static {
        System.loadLibrary("java_micro3d_v3_32");
    }

    public static native int sqrt(int paramInt);

    public static native int sin(int paramInt);

    public static native int cos(int paramInt);
}
