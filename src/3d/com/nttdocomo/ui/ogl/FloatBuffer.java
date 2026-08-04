package com.nttdocomo.ui.ogl;

public abstract interface FloatBuffer
        extends DirectBuffer {
    public abstract float[] get(int paramInt, float[] paramArrayOfFloat);

    public abstract float[] get(int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3);

    public abstract void put(int paramInt, float[] paramArrayOfFloat);

    public abstract void put(int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3);
}
