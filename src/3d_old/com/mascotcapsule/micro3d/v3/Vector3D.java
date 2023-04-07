package com.mascotcapsule.micro3d.v3;

public class Vector3D {
	public int x;
	public int y;
	public int z;

	public Vector3D() {
	}

	public Vector3D(Vector3D var1) {
		this.set(var1);
	}

	public Vector3D(int var1, int var2, int var3) {
		this.x = var1;
		this.y = var2;
		this.z = var3;
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

	public final void setX(int var1) {
		this.x = var1;
	}

	public final void setY(int var1) {
		this.y = var1;
	}

	public final void setZ(int var1) {
		this.z = var1;
	}

	public final void set(Vector3D var1) {
		if (var1 == null) {
			throw new NullPointerException();
		} else {
			this.x = var1.x;
			this.y = var1.y;
			this.z = var1.z;
		}
	}

	public final void set(int var1, int var2, int var3) {
		this.x = var1;
		this.y = var2;
		this.z = var3;
	}

	public final void unit() {
		int var2;
		if ((var2 = Util3D.sqrt(this.x * this.x + this.y * this.y + this.z * this.z)) == 0) {
			var2 = 1;
		}

		this.x = (this.x << 12) / var2;
		this.y = (this.y << 12) / var2;
		this.z = (this.z << 12) / var2;
	}

	public final int innerProduct(Vector3D var1) {
		if (var1 == null) {
			throw new NullPointerException();
		} else {
			return this.x * var1.x + this.y * var1.y + this.z * var1.z;
		}
	}

	public final void outerProduct(Vector3D var1) {
		if (var1 == null) {
			throw new NullPointerException();
		} else {
			Vector3D var2;
			(var2 = new Vector3D()).x = this.y * var1.z - this.z * var1.y >> 12;
			var2.y = this.z * var1.x - this.x * var1.z >> 12;
			var2.z = this.x * var1.y - this.y * var1.x >> 12;
			var1.set(var2);
		}
	}

	public static final int innerProduct(Vector3D var0, Vector3D var1) {
		if (var0 != null && var1 != null) {
			return var0.x * var1.x + var0.y * var1.y + var0.z * var1.z;
		} else {
			throw new NullPointerException();
		}
	}

	public static final Vector3D outerProduct(Vector3D var0, Vector3D var1) {
		if (var0 != null && var1 != null) {
			Vector3D var2;
			(var2 = new Vector3D()).x = var0.y * var1.z - var0.z * var1.y >> 12;
			var2.y = var0.z * var1.x - var0.x * var1.z >> 12;
			var2.z = var0.x * var1.y - var0.y * var1.x >> 12;
			return var2;
		} else {
			throw new NullPointerException();
		}
	}
}