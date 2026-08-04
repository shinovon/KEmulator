package com.nttdocomo.device;

public class DeviceException
        extends RuntimeException {
    public static final int UNDEFINED = 0;
    public static final int ILLEGAL_STATE = 1;
    public static final int NO_RESOURCES = 2;
    public static final int BUSY_RESOURCE = 3;
    public static final int RACE_CONDITION = 4;
    public static final int INTERRUPTED = 5;

    public DeviceException() {
        this(0);
    }

    public DeviceException(int paramInt) {
        this(paramInt, null);
    }

    public DeviceException(int paramInt, String paramString) {
        super(paramString);
    }

    public int getStatus() {
        return 0;
    }
}
