package com.motorola.funlight;

public abstract interface Region {
    public abstract int getColor();

    public abstract int getControl();

    public abstract int getID();

    public abstract void releaseControl();

    public abstract int setColor(int paramInt);

    public abstract int setColor(byte paramByte1, byte paramByte2, byte paramByte3);

    public abstract String toString();
}
