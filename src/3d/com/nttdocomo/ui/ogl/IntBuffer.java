package com.nttdocomo.ui.ogl;

public abstract interface IntBuffer
        extends DirectBuffer {
    public abstract int[] get(int paramInt, int[] paramArrayOfInt);

    public abstract int[] get(int paramInt1, int[] paramArrayOfInt, int paramInt2, int paramInt3);

    public abstract void put(int paramInt, int[] paramArrayOfInt);

    public abstract void put(int paramInt1, int[] paramArrayOfInt, int paramInt2, int paramInt3);
}
