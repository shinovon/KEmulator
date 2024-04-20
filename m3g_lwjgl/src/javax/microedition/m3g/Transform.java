package javax.microedition.m3g;

import emulator.graphics3D.Transform3D;

public class Transform {
    private Transform3D impl;

    public Transform() {
        this.impl = new Transform3D();
    }

    public Transform(Transform var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else {
            this.impl = new Transform3D();
            this.impl.set(var1.impl);
        }
    }

    Transform3D getImpl_() {
        return this.impl;
    }

    public Object getImpl() {
        return this.impl;
    }

    public void setIdentity() {
        this.impl.setIdentity();
    }

    public void set(Transform var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else {
            this.impl.set(var1.impl);
        }
    }

    public void set(float[] var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else if (var1.length < 16) {
            throw new IllegalArgumentException();
        } else {
            this.impl.set(var1);
        }
    }

    public void get(float[] var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else if (var1.length < 16) {
            throw new IllegalArgumentException();
        } else {
            this.impl.get(var1);
        }
    }

    public void invert() {
        this.impl.invert();
    }

    public void transpose() {
        this.impl.transpose();
    }

    public void postMultiply(Transform var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else {
            this.impl.postMultiply(var1.impl, false);
        }
    }

    public void preMultiply(Transform var1) {
        this.impl.postMultiply(var1.impl, true);
    }

    public void postScale(float var1, float var2, float var3) {
        this.impl.postScale(var1, var2, var3);
    }

    public void postRotate(float var1, float var2, float var3, float var4) {
        if (var2 == 0.0F && var3 == 0.0F && var4 == 0.0F && var1 != 0.0F) {
            throw new IllegalArgumentException();
        } else {
            this.impl.postRotate(var1, var2, var3, var4);
        }
    }

    public void postRotateQuat(float var1, float var2, float var3, float var4) {
        if (var1 == 0.0F && var2 == 0.0F && var3 == 0.0F && var4 == 0.0F) {
            throw new IllegalArgumentException();
        } else {
            this.impl.postRotateQuat(var1, var2, var3, var4);
        }
    }

    public void postTranslate(float var1, float var2, float var3) {
        this.impl.postTranslate(var1, var2, var3);
    }

    public void transform(VertexArray var1, float[] var2, boolean var3) {
        if (var1 != null && var2 != null) {
            if (var1.getComponentCount() != 4 && var2.length >= 4 * var1.getVertexCount()) {
                int var4 = var3 ? 1 : 0;
                int var5 = var1.getVertexCount();
                int var6 = var1.getComponentCount();
                int var7 = var5 * 4;
                int var8 = 0;
                int var9 = 0;
                if (var1.getComponentType() == 1) {
                    byte[] var10 = new byte[var5 * var6];
                    var1.get(0, var5, var10);

                    while (var8 < var7) {
                        if (var8 % 4 < var6) {
                            var2[var8++] = (float) var10[var9++];
                        } else {
                            var2[var8++] = (float) var4;
                        }
                    }
                } else {
                    short[] var11 = new short[var5 * var6];
                    var1.get(0, var5, var11);

                    while (var8 < var7) {
                        if (var8 % 4 < var6) {
                            var2[var8++] = (float) var11[var9++];
                        } else {
                            var2[var8++] = (float) var4;
                        }
                    }
                }

                this.impl.transform(var2);
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new NullPointerException();
        }
    }

    public void transform(float[] var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else if (var1.length % 4 != 0) {
            throw new IllegalArgumentException();
        } else {
            this.impl.transform(var1);
        }
    }
}
