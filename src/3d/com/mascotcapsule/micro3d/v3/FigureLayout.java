package com.mascotcapsule.micro3d.v3;

public class FigureLayout {
	private AffineTrans[] jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3AffineTrans;
	private AffineTrans affineTrans;
	private int jdField_a_of_type_Int;
	private int b;
	private int c;
	private int d;
	private int e;
	private int f;
	public int mPersNear;
	public int mPersFar;
	public int mPersAngle;
	public int mPersWidth;
	public int mPersHeight;
	public int mLayoutType;

	public FigureLayout() {
		setAffineTrans((AffineTrans) null);
		jdField_a_of_type_Int = 512;
		b = 512;
	}

	public FigureLayout(AffineTrans paramAffineTrans, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		setAffineTrans(paramAffineTrans);
		jdField_a_of_type_Int = paramInt1;
		b = paramInt2;
		c = paramInt3;
		d = paramInt4;
	}

	public final AffineTrans getAffineTrans() {
		return affineTrans;
	}

	public final void setAffineTrans(AffineTrans paramAffineTrans) {
		if (paramAffineTrans == null) {
			(paramAffineTrans = new AffineTrans()).setIdentity();
		}
		if (jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3AffineTrans == null) {
			jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3AffineTrans = new AffineTrans[1];
			jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3AffineTrans[0] = paramAffineTrans;
		}
		affineTrans = paramAffineTrans;
	}

	public final void setAffineTransArray(AffineTrans[] paramArrayOfAffineTrans) {
		setAffineTrans(paramArrayOfAffineTrans);
	}

	public final void setAffineTrans(AffineTrans[] paramArrayOfAffineTrans) {
		if ((paramArrayOfAffineTrans == null) || (paramArrayOfAffineTrans.length == 0)) {
			throw new NullPointerException();
		}
		for (int i = 0; i < paramArrayOfAffineTrans.length; i++) {
			if (paramArrayOfAffineTrans[i] == null) {
				throw new NullPointerException();
			}
		}
		jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3AffineTrans = paramArrayOfAffineTrans;
	}

	public final void selectAffineTrans(int paramInt) {
		if ((jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3AffineTrans == null) || (paramInt < 0)
				|| (paramInt >= jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3AffineTrans.length)) {
			throw new ArrayIndexOutOfBoundsException();
		}
		affineTrans = jdField_a_of_type_ArrayOfComMascotcapsuleMicro3dV3AffineTrans[paramInt];
	}

	public final int getScaleX() {
		return jdField_a_of_type_Int;
	}

	public final int getScaleY() {
		return b;
	}

	public final void setScale(int paramInt1, int paramInt2) {
		jdField_a_of_type_Int = paramInt1;
		b = paramInt2;
		mLayoutType = 0;
	}

	public final int getParallelWidth() {
		return e;
	}

	public final int getParallelHeight() {
		return f;
	}

	public final void setParallelSize(int paramInt1, int paramInt2) {
		if ((paramInt1 < 0) || (paramInt2 < 0)) {
			throw new IllegalArgumentException();
		}
		e = paramInt1;
		f = paramInt2;
		mLayoutType = 1;
	}

	public final int getCenterX() {
		return c;
	}

	public final int getCenterY() {
		return d;
	}

	public final void setCenter(int paramInt1, int paramInt2) {
		c = paramInt1;
		d = paramInt2;
	}

	public final void setPerspective(int paramInt1, int paramInt2, int paramInt3) {
		if ((paramInt1 >= paramInt2) || (paramInt1 < 1) || (paramInt1 > 32766) || (paramInt2 < 2) || (paramInt2 > 32767)
				|| (paramInt3 <= 0) || (paramInt3 >= 2048)) {
			throw new IllegalArgumentException();
		}
		mPersNear = paramInt1;
		mPersFar = paramInt2;
		mPersAngle = paramInt3;
		mLayoutType = 2;
	}

	public final void setPerspective(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		if ((paramInt1 >= paramInt2) || (paramInt1 < 1) || (paramInt1 > 32766) || (paramInt2 < 2) || (paramInt2 > 32767)
				|| (paramInt3 < 0) || (paramInt4 < 0)) {
			throw new IllegalArgumentException();
		}
		mPersNear = paramInt1;
		mPersFar = paramInt2;
		mPersWidth = paramInt3;
		mPersHeight = paramInt4;
		mLayoutType = 3;
	}
}