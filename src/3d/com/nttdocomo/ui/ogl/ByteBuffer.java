package com.nttdocomo.ui.ogl;

public abstract interface ByteBuffer
        extends DirectBuffer {
    public abstract byte[] get(int paramInt, byte[] paramArrayOfByte);

    public abstract byte[] get(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);

    public abstract void put(int paramInt, byte[] paramArrayOfByte);

    public abstract void put(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
}
