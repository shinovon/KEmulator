package com.nttdocomo.ui.ogl;

public abstract interface ShortBuffer
        extends DirectBuffer {
    public abstract short[] get(int paramInt, short[] paramArrayOfShort);

    public abstract short[] get(int paramInt1, short[] paramArrayOfShort, int paramInt2, int paramInt3);

    public abstract void put(int paramInt, short[] paramArrayOfShort);

    public abstract void put(int paramInt1, short[] paramArrayOfShort, int paramInt2, int paramInt3);
}
