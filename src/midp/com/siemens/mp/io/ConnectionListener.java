package com.siemens.mp.io;

public abstract interface ConnectionListener {
    public abstract void receiveData(byte[] paramArrayOfByte);
}
