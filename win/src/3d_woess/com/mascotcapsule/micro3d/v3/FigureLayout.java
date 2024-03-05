package com.mascotcapsule.micro3d.v3;

public class FigureLayout {
    private AffineTrans mAffine;
    private AffineTrans[] mAffineArray;
    private int mAngle;
    private int mCenterX;
    private int mCenterY;
    private int mFar;
    private int mNear;
    private int mParallelH;
    private int mParallelW;
    private int mPerspectiveH;
    private int mPerspectiveW;
    private int mProjectionType;
    private int mScaleX;
    private int mScaleY;

    public FigureLayout() {
        setAffineTrans((AffineTrans) null);
        this.mScaleX = 512;
        this.mScaleY = 512;
    }

    public FigureLayout(AffineTrans trans, int sx, int sy, int cx, int cy) {
        setAffineTrans(trans);
        this.mScaleX = sx;
        this.mScaleY = sy;
        this.mCenterX = cx;
        this.mCenterY = cy;
    }

    public final AffineTrans getAffineTrans() {
        return this.mAffine;
    }

    public final int getCenterX() {
        return this.mCenterX;
    }

    public final int getCenterY() {
        return this.mCenterY;
    }

    public final int getParallelHeight() {
        return this.mParallelH;
    }

    public final int getParallelWidth() {
        return this.mParallelW;
    }

    public final int getScaleX() {
        return this.mScaleX;
    }

    public final int getScaleY() {
        return this.mScaleY;
    }

    public final void selectAffineTrans(int idx) {
        if ((this.mAffineArray == null) || (idx < 0) || (idx >= this.mAffineArray.length)) {
            throw new IllegalArgumentException();
        }
        this.mAffine = this.mAffineArray[idx];
    }

    public final void setAffineTrans(AffineTrans trans) {
        if (trans == null) {
            trans = new AffineTrans();
            trans.setIdentity();
        }
        if (this.mAffineArray == null) {
            this.mAffineArray = new AffineTrans[1];
            this.mAffineArray[0] = trans;
        }
        this.mAffine = trans;
    }

    public final void setAffineTrans(AffineTrans[] trans) {
        if (trans == null) {
            throw new NullPointerException();
        }
        if (trans.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        for (int i = 0; i < trans.length; i++) {
            if (trans[i] == null) {
                throw new NullPointerException("Null element at index=" + i);
            }
        }
        this.mAffineArray = trans;
    }

    public final void setAffineTransArray(AffineTrans[] trans) {
        setAffineTrans(trans);
    }

    public final void setCenter(int cx, int cy) {
        this.mCenterX = cx;
        this.mCenterY = cy;
    }

    public final void setParallelSize(int w, int h) {
        if ((w < 0) || (h < 0)) {
            throw new IllegalArgumentException("with=" + w + ", height=" + h);
        }
        this.mParallelW = w;
        this.mParallelH = h;
        this.mProjectionType = 1;
    }

    public final void setPerspective(int zNear, int zFar, int angle) {
        if ((zNear >= zFar) || (zNear < 1) || (zNear > 32766) || (zFar > 32767) || (angle < 1) || (angle > 2047)) {
            throw new IllegalArgumentException("zNear=" + zNear + ", zFar=" + zFar + ", angle=" + angle);
        }
        this.mNear = zNear;
        this.mFar = zFar;
        this.mAngle = angle;
        this.mProjectionType = 2;
    }

    public final void setPerspective(int zNear, int zFar, int width, int height) {
        if ((zNear >= zFar) || (zNear < 1) || (zNear > 32766) || (zFar > 32767) || (width < 0) || (height < 0)) {
            throw new IllegalArgumentException("zNear=" + zNear + ", zFar=" + zFar + ", with=" + width + ", height="
                    + height);
        }
        this.mNear = zNear;
        this.mFar = zFar;
        this.mPerspectiveW = width;
        this.mPerspectiveH = height;
        this.mProjectionType = 3;
    }

    public final void setScale(int sx, int sy) {
        this.mScaleX = sx;
        this.mScaleY = sy;
        this.mProjectionType = 0;
    }

    static {
        System.loadLibrary("java_micro3d_v3_32");
        nInitClass();
    }

    private static native void nInitClass();
}
