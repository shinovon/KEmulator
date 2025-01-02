package com.vodafone.bluetooth;

public abstract interface SeekListener {
	public static final int COMPLETED = 0;
	public static final int ERROR = 1;
	public static final int CANCELLED = 2;
	public static final int SERVICE_NOT_FOUND = 3;
	public static final int PUSH_SERVER_FOUND = 4;
	public static final int MASK_SERVICE_CLASSES = 16719872;
	public static final int MASK_MAJOR_DEVICE_CLASS = 7936;
	public static final int MASK_MINOR_DEVICE_CLASS = 252;

	public abstract void foundDevice(Device paramDevice, int paramInt);

	public abstract void terminatedDeviceSeek(int paramInt);

	public abstract void foundService(RemoteService[] paramArrayOfRemoteService);

	public abstract void terminatedServiceSeek(Device paramDevice, int paramInt);
}
