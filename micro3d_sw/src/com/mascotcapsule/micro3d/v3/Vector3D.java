/*
 * MIT License
 * Copyright (c) 2026 Yury Kharchenko
 */

package com.mascotcapsule.micro3d.v3;

public class Vector3D {
	public int x;
	public int y;
	public int z;

	public Vector3D() {}

	public Vector3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3D(Vector3D v) {
		if (v == null) throw new NullPointerException();
		
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	public final int getZ() {
		return z;
	}

	public final int innerProduct(Vector3D v) {
		if (v == null) throw new NullPointerException();
		
		return x * v.x + y * v.y + z * v.z;
	}

	public static int innerProduct(Vector3D v1, Vector3D v2) {
		if (v1 == null) throw new NullPointerException();
		
		return v1.innerProduct(v2);
	}

	public final void outerProduct(Vector3D v) {
		if (v == null) throw new NullPointerException();
		
		int x = this.x;
		int y = this.y;
		int z = this.z;
		this.x = y * v.z - z * v.y;
		this.y = z * v.x - x * v.z;
		this.z = x * v.y - y * v.x;
	}

	public static Vector3D outerProduct(Vector3D v1, Vector3D v2) {
		if (v1 == null || v2 == null) {
			throw new NullPointerException();
		}
		
		int x = v1.y * v2.z - v1.z * v2.y;
		int y = v1.z * v2.x - v1.x * v2.z;
		int z = v1.x * v2.y - v1.y * v2.x;
		return new Vector3D(x, y, z);
	}

	static void outerProduct(Vector3D v1, Vector3D v2, Vector3D result) {
		if (v1 == null || v2 == null) {
			throw new NullPointerException();
		}
		
		int x = v1.y * v2.z - v1.z * v2.y;
		int y = v1.z * v2.x - v1.x * v2.z;
		int z = v1.x * v2.y - v1.y * v2.x;
		
		result.x = x;
		result.y = y;
		result.z = z;
	}

	public final void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public final void set(Vector3D v) {
		if (v == null) throw new NullPointerException();
		
		x = v.x;
		y = v.y;
		z = v.z;
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

	public final void unit() {
		int x = this.x;
		int y = this.y;
		int z = this.z;
		int shift = numberOfLeadingZeros(Math.abs(x) | Math.abs(y) | Math.abs(z)) - 17;
		
		if (shift > 0) {
			x <<= shift;
			y <<= shift;
			z <<= shift;
		} else if (shift < 0) {
			shift = -shift;
			x >>= shift;
			y >>= shift;
			z >>= shift;
		}
		
		int i = Util3D.sqrt(x * x + y * y + z * z);
		if (i != 0) {
			this.x = (x << 12) / i;
			this.y = (y << 12) / i;
			this.z = (z << 12) / i;
		} else {
			this.x = 0;
			this.y = 0;
			this.z = 4096;
		}
	}
	
	private static final int numberOfLeadingZeros(int i) {
		if (i == 0) return 32;
		int n = 1;
		if (i >>> 16 == 0) { n += 16; i <<= 16; }
		if (i >>> 24 == 0) { n +=  8; i <<=  8; }
		if (i >>> 28 == 0) { n +=  4; i <<=  4; }
		if (i >>> 30 == 0) { n +=  2; i <<=  2; }
		n -= i >>> 31;
		return n;
	}
}