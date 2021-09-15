package com.mascotcapsule.micro3d.v3;

public class AffineTrans {
	public int m00;
	public int m01;
	public int m02;
	public int m03;
	public int m10;
	public int m11;
	public int m12;
	public int m13;
	public int m20;
	public int m21;
	public int m22;
	public int m23;

	public AffineTrans() {
	}

	public AffineTrans(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6,
			int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12) {
		set(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramInt9,
				paramInt10, paramInt11, paramInt12);
	}

	public AffineTrans(AffineTrans paramAffineTrans) {
		set(paramAffineTrans);
	}

	public AffineTrans(int[][] paramArrayOfInt) {
		set(paramArrayOfInt);
	}

	public AffineTrans(int[] paramArrayOfInt) {
		set(paramArrayOfInt);
	}

	public AffineTrans(int[] paramArrayOfInt, int paramInt) {
		set(paramArrayOfInt, paramInt);
	}

	public final void setIdentity() {
		m00 = 4096;
		m10 = 0;
		m20 = 0;
		m01 = 0;
		m11 = 4096;
		m21 = 0;
		m02 = 0;
		m12 = 0;
		m22 = 4096;
		m03 = 0;
		m13 = 0;
		m23 = 0;
	}

	public final void get(int[] paramArrayOfInt) {
		get(paramArrayOfInt, 0);
	}

	public final void get(int[] paramArrayOfInt, int paramInt) {
		if (paramArrayOfInt == null) {
			throw new NullPointerException();
		}
		if ((paramInt < 0) || (paramArrayOfInt.length - paramInt < 12)) {
			throw new IllegalArgumentException();
		}
		paramArrayOfInt[(paramInt++)] = m00;
		paramArrayOfInt[(paramInt++)] = m01;
		paramArrayOfInt[(paramInt++)] = m02;
		paramArrayOfInt[(paramInt++)] = m03;
		paramArrayOfInt[(paramInt++)] = m10;
		paramArrayOfInt[(paramInt++)] = m11;
		paramArrayOfInt[(paramInt++)] = m12;
		paramArrayOfInt[(paramInt++)] = m13;
		paramArrayOfInt[(paramInt++)] = m20;
		paramArrayOfInt[(paramInt++)] = m21;
		paramArrayOfInt[(paramInt++)] = m22;
		paramArrayOfInt[paramInt] = m23;
	}

	public final void set(int[] paramArrayOfInt, int paramInt) {
		if (paramArrayOfInt == null) {
			throw new NullPointerException();
		}
		if ((paramInt < 0) || (paramArrayOfInt.length - paramInt < 12)) {
			throw new IllegalArgumentException();
		}
		m00 = paramArrayOfInt[(paramInt++)];
		m01 = paramArrayOfInt[(paramInt++)];
		m02 = paramArrayOfInt[(paramInt++)];
		m03 = paramArrayOfInt[(paramInt++)];
		m10 = paramArrayOfInt[(paramInt++)];
		m11 = paramArrayOfInt[(paramInt++)];
		m12 = paramArrayOfInt[(paramInt++)];
		m13 = paramArrayOfInt[(paramInt++)];
		m20 = paramArrayOfInt[(paramInt++)];
		m21 = paramArrayOfInt[(paramInt++)];
		m22 = paramArrayOfInt[(paramInt++)];
		m23 = paramArrayOfInt[paramInt];
	}

	public final void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6,
			int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12) {
		m00 = paramInt1;
		m01 = paramInt2;
		m02 = paramInt3;
		m03 = paramInt4;
		m10 = paramInt5;
		m11 = paramInt6;
		m12 = paramInt7;
		m13 = paramInt8;
		m20 = paramInt9;
		m21 = paramInt10;
		m22 = paramInt11;
		m23 = paramInt12;
	}

	public final void set(AffineTrans paramAffineTrans) {
		if (paramAffineTrans == null) {
			throw new NullPointerException();
		}
		m00 = m00;
		m01 = m01;
		m02 = m02;
		m03 = m03;
		m10 = m10;
		m11 = m11;
		m12 = m12;
		m13 = m13;
		m20 = m20;
		m21 = m21;
		m22 = m22;
		m23 = m23;
	}

	public final void set(int[][] paramArrayOfInt) {
		if (paramArrayOfInt == null) {
			throw new NullPointerException();
		}
		if (paramArrayOfInt.length < 3) {
			throw new IllegalArgumentException();
		}
		if ((paramArrayOfInt[0].length < 4) || (paramArrayOfInt[1].length < 4) || (paramArrayOfInt[2].length < 4)) {
			throw new IllegalArgumentException();
		}
		m00 = paramArrayOfInt[0][0];
		m01 = paramArrayOfInt[0][1];
		m02 = paramArrayOfInt[0][2];
		m03 = paramArrayOfInt[0][3];
		m10 = paramArrayOfInt[1][0];
		m11 = paramArrayOfInt[1][1];
		m12 = paramArrayOfInt[1][2];
		m13 = paramArrayOfInt[1][3];
		m20 = paramArrayOfInt[2][0];
		m21 = paramArrayOfInt[2][1];
		m22 = paramArrayOfInt[2][2];
		m23 = paramArrayOfInt[2][3];
	}

	public final void set(int[] paramArrayOfInt) {
		set(paramArrayOfInt, 0);
	}

	public final Vector3D transPoint(Vector3D paramVector3D) {
		return transform(paramVector3D);
	}

	public final Vector3D transform(Vector3D var1) {
		if (var1== null) {
			throw new NullPointerException();
		}
		Vector3D var2;
		(var2 = new Vector3D()).x = (var1.x * this.m00 + var1.y * this.m01 + var1.z * this.m02 >> 12) + this.m03;
		var2.y = (var1.x * this.m10 + var1.y * this.m11 + var1.z * this.m12 >> 12) + this.m13;
		var2.z = (var1.x * this.m20 + var1.y * this.m21 + var1.z * this.m22 >> 12) + this.m23;
		return var2;
	}

	public final void setRotationX(int paramInt) {
		int i = Util3D.a(paramInt);
		int j = Util3D.b(paramInt);
		m00 = 4096;
		m10 = 0;
		m20 = 0;
		m01 = 0;
		m11 = j;
		m21 = i;
		m02 = 0;
		m12 = (-i);
		m22 = j;
		m03 = 0;
		m13 = 0;
		m23 = 0;
	}

	public final void setRotationY(int paramInt) {
		int i = Util3D.a(paramInt);
		int j = Util3D.b(paramInt);
		m00 = j;
		m10 = 0;
		m20 = (-i);
		m01 = 0;
		m11 = 4096;
		m21 = 0;
		m02 = i;
		m12 = 0;
		m22 = j;
		m03 = 0;
		m13 = 0;
		m23 = 0;
	}

	public final void setRotationZ(int paramInt) {
		int i = Util3D.a(paramInt);
		int j = Util3D.b(paramInt);
		m00 = j;
		m10 = i;
		m20 = 0;
		m01 = (-i);
		m11 = j;
		m21 = 0;
		m02 = 0;
		m12 = 0;
		m22 = 4096;
		m03 = 0;
		m13 = 0;
		m23 = 0;
	}

	public final void rotationX(int paramInt) {
		setRotationX(paramInt);
	}

	public final void rotationY(int paramInt) {
		setRotationY(paramInt);
	}

	public final void rotationZ(int paramInt) {
		setRotationZ(paramInt);
	}

	public final void multiply(AffineTrans paramAffineTrans) {
		mul(paramAffineTrans);
	}

	public final void mul(AffineTrans paramAffineTrans) {
		if (paramAffineTrans == null) {
			throw new NullPointerException();
		}
		int i = m00;
		int j = m10;
		int k = m20;
		int m = m01;
		int n = m11;
		int i1 = m21;
		int i2 = m02;
		int i3 = m12;
		int i4 = m22;
		int i5 = m03;
		int i6 = m13;
		int i7 = m23;
		int i8 = m00;
		int i9 = m01;
		int i10 = m02;
		m00 = (i8 * i + i9 * j + i10 * k >> 12);
		m01 = (i8 * m + i9 * n + i10 * i1 >> 12);
		m02 = (i8 * i2 + i9 * i3 + i10 * i4 >> 12);
		m03 = ((i8 * i5 + i9 * i6 + i10 * i7 >> 12) + m03);
		i8 = m10;
		i9 = m11;
		i10 = m12;
		m10 = (i8 * i + i9 * j + i10 * k >> 12);
		m11 = (i8 * m + i9 * n + i10 * i1 >> 12);
		m12 = (i8 * i2 + i9 * i3 + i10 * i4 >> 12);
		m13 = ((i8 * i5 + i9 * i6 + i10 * i7 >> 12) + m13);
		i8 = m20;
		i9 = m21;
		i10 = m22;
		m20 = (i8 * i + i9 * j + i10 * k >> 12);
		m21 = (i8 * m + i9 * n + i10 * i1 >> 12);
		m22 = (i8 * i2 + i9 * i3 + i10 * i4 >> 12);
		m23 = ((i8 * i5 + i9 * i6 + i10 * i7 >> 12) + m23);
	}

	public final void multiply(AffineTrans paramAffineTrans1, AffineTrans paramAffineTrans2) {
		mul(paramAffineTrans1, paramAffineTrans2);
	}

	public final void mul(AffineTrans paramAffineTrans1, AffineTrans paramAffineTrans2) {
		if ((paramAffineTrans1 == null) || (paramAffineTrans2 == null)) {
			throw new NullPointerException();
		}
		int i = m00;
		int j = m10;
		int k = m20;
		int m = m01;
		int n = m11;
		int i1 = m21;
		int i2 = m02;
		int i3 = m12;
		int i4 = m22;
		int i5 = m03;
		int i6 = m13;
		int i7 = m23;
		int i8 = m00;
		int i9 = m01;
		int i10 = m02;
		m00 = (i8 * i + i9 * j + i10 * k >> 12);
		m01 = (i8 * m + i9 * n + i10 * i1 >> 12);
		m02 = (i8 * i2 + i9 * i3 + i10 * i4 >> 12);
		m03 = ((i8 * i5 + i9 * i6 + i10 * i7 >> 12) + m03);
		i8 = m10;
		i9 = m11;
		i10 = m12;
		m10 = (i8 * i + i9 * j + i10 * k >> 12);
		m11 = (i8 * m + i9 * n + i10 * i1 >> 12);
		m12 = (i8 * i2 + i9 * i3 + i10 * i4 >> 12);
		m13 = ((i8 * i5 + i9 * i6 + i10 * i7 >> 12) + m13);
		i8 = m20;
		i9 = m21;
		i10 = m22;
		m20 = (i8 * i + i9 * j + i10 * k >> 12);
		m21 = (i8 * m + i9 * n + i10 * i1 >> 12);
		m22 = (i8 * i2 + i9 * i3 + i10 * i4 >> 12);
		m23 = ((i8 * i5 + i9 * i6 + i10 * i7 >> 12) + m23);
	}

	public final void rotationV(Vector3D paramVector3D, int paramInt) {
		setRotation(paramVector3D, paramInt);
	}

	public final void setRotation(Vector3D var1, int var2) {
		if (var1 == null) {
			throw new NullPointerException();
		}
		int var3 = Util3D.a(var2);
		int var4 = Util3D.b(var2);
		int var5 = 4096 - var4;
		int var6 = var1.x;
		int var7 = var1.y;
		int var8 = var1.z;
		int var9 = var5 * var6 >> 12;
		int var10 = var5 * var7 >> 12;
		int var11 = var5 * var8 >> 12;
		this.m00 = var9 * var6 + (var4 << 12);
		this.m10 = var9 * var7 + var3 * var8;
		this.m20 = var9 * var8 - var3 * var7;
		this.m01 = var9 * var7 - var3 * var8;
		this.m11 = var10 * var7 + (var4 << 12);
		this.m21 = var10 * var8 + var3 * var6;
		this.m02 = var9 * var7 + var3 * var7;
		this.m12 = var10 * var8 - var3 * var6;
		this.m22 = var11 * var8 + (var4 << 12);
		this.m03 = 0;
		this.m13 = 0;
		this.m23 = 0;
		this.m00 >>= 12;
		this.m10 >>= 12;
		this.m20 >>= 12;
		this.m01 >>= 12;
		this.m11 >>= 12;
		this.m21 >>= 12;
		this.m02 >>= 12;
		this.m12 >>= 12;
		this.m22 >>= 12;
	}

	public final void setViewTrans(Vector3D paramVector3D1, Vector3D paramVector3D2, Vector3D paramVector3D3) {
		lookAt(paramVector3D1, paramVector3D2, paramVector3D3);
	}

	public final void lookAt(Vector3D var1, Vector3D var2, Vector3D var3) {
		if (var1 != null && var2 != null && var3 != null) {
			Vector3D var4;
			(var4 = Vector3D.outerProduct(var2, var3)).unit();
			var3 = Vector3D.outerProduct(var4, var2);
			this.m00 = var4.x;
			this.m01 = var3.x;
			this.m02 = -var2.x;
			this.m03 = var1.x;
			this.m10 = var4.y;
			this.m11 = var3.y;
			this.m12 = -var2.y;
			this.m13 = var1.y;
			this.m20 = var4.z;
			this.m21 = var3.z;
			this.m22 = -var2.z;
			this.m23 = var1.z;
		} else {
			throw new NullPointerException();
		}
	}
}