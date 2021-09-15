package com.mascotcapsule.micro3d.v3;

public class Util3D {
	public static final int FIGURE_SCALE_SHIFT = 9;
	public static final int FIGURE_SCALE_ONE = 512;
	public static final int COS_SIN_SHIFT = 10;
	public static final int COS_SIN_ONE = 1024;
	public static final int PRECISION_SHIFT = 12;
	public static final int PRECISION_ONE = 4096;
	public static final int ANGLE_SHIFT = 12;
	public static final int ANGLE_2PI = 4096;
	public static final int ANGLE_PI = 2048;
	public static final int ANGLE_MASK = 4095;
	public static final int ANGLE_360 = 4096;
	public static final int ANGLE_180 = 2048;
	public static final int ANGLE_90 = 1024;
	public static final int ANGLE_45 = 512;

	public static final int sqrt(int paramInt) {
		return (int) Math.sqrt(paramInt);
	}

	public static final int sin(int paramInt) {
		double d1;
		double d2;
		return (int) ((d2 = Math.sin(d1 = paramInt / 2048.0D * 3.141592653589793D)) * 1024.0D);
	}

	public static final int cos(int paramInt) {
		double d1;
		double d2;
		return (int) ((d2 = Math.cos(d1 = paramInt / 2048.0D * 3.141592653589793D)) * 1024.0D);
	}

	static final int a(int paramInt) {
		return sin(paramInt) >> 10 << 12;
	}

	static final int b(int paramInt) {
		return cos(paramInt) >> 10 << 12;
	}
}