package com.mascotcapsule.micro3d.v3;

public class FigureLayout {
	private AffineTrans[] affineArray;
	private AffineTrans affineNow;
	private int mScaleX;
	private int mScaleY;
	private int mCenterX;
	private int mCenterY;
	private int paraWidth;
	private int paraHeight;
	public int mPersNear;
	public int mPersFar;
	public int mPersAngle;
	public int mPersWidth;
	public int mPersHeight;
	public int mLayoutType;

	public FigureLayout() {
		setAffineTrans((AffineTrans) null);
		mScaleX = 512;
		mScaleY = 512;
	}

	public FigureLayout(AffineTrans paramAffineTrans, int scx, int scy, int cx, int cy) {
		setAffineTrans(paramAffineTrans);
		mScaleX = scx;
		mScaleY = scy;
		mCenterX = cx;
		mCenterY = cy;
	}

	public final AffineTrans getAffineTrans() {
		return affineNow;
	}

	public final void setAffineTrans(AffineTrans paramAffineTrans) {
		if (paramAffineTrans == null) {
			(paramAffineTrans = new AffineTrans()).setIdentity();
		}
		if (affineArray == null) {
			affineArray = new AffineTrans[1];
			affineArray[0] = paramAffineTrans;
		}
		affineNow = paramAffineTrans;
	}

	public final void setAffineTransArray(AffineTrans[] affine) {
		setAffineTrans(affine);
	}

	public final void setAffineTrans(AffineTrans[] affine) {
		if ((affine == null) || (affine.length == 0)) {
			throw new NullPointerException();
		}
		for (int i = 0; i < affine.length; i++) {
			if (affine[i] == null) {
				throw new NullPointerException();
			}
		}
		affineArray = affine;
	}

	public final void selectAffineTrans(int i) {
		if ((affineArray == null) || (i < 0)
				|| (i >= affineArray.length)) {
			throw new ArrayIndexOutOfBoundsException();
		}
		affineNow = affineArray[i];
	}

	public final int getScaleX() {
		return mScaleX;
	}

	public final int getScaleY() {
		return mScaleY;
	}

	public final void setScale(int x, int y) {
		mScaleX = x;
		mScaleY = y;
		mLayoutType = 0;
	}

	public final int getParallelWidth() {
		return paraWidth;
	}

	public final int getParallelHeight() {
		return paraHeight;
	}

	public final void setParallelSize(int paramInt1, int paramInt2) {
		if ((paramInt1 < 0) || (paramInt2 < 0)) {
			throw new IllegalArgumentException();
		}
		paraWidth = paramInt1;
		paraHeight = paramInt2;
		mLayoutType = 1;
	}

	public final int getCenterX() {
		return mCenterX;
	}

	public final int getCenterY() {
		return mCenterY;
	}

	public final void setCenter(int x, int y) {
		mCenterX = x;
		mCenterY = y;
	}

	public final void setPerspective(int near, int far, int angle) {
		if ((near >= far) || (near < 1) || (near > 32766) || (far < 2) || (far > 32767)
				|| (angle <= 0) || (angle >= 2048)) {
			throw new IllegalArgumentException();
		}
		mPersNear = near;
		mPersFar = far;
		mPersAngle = angle;
		mLayoutType = 2;
	}

	public final void setPerspective(int near, int far, int w, int h) {
		if ((near >= far) || (near < 1) || (near > 32766) || (far < 2) || (far > 32767)
				|| (w < 0) || (h < 0)) {
			throw new IllegalArgumentException();
		}
		mPersNear = near;
		mPersFar = far;
		mPersWidth = w;
		mPersHeight = h;
		mLayoutType = 3;
	}
}