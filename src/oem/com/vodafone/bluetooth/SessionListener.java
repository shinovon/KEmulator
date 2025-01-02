package com.vodafone.bluetooth;

public abstract interface SessionListener {
	public static final int SIGNAL_START = 0;
	public static final int SIGNAL_END = 1;
	public static final int SIGNAL_PAUSE = 2;
	public static final int SIGNAL_WAIT = 3;
	public static final int SIGNAL_REJECT = 4;
	public static final int CONN_OPENED = 5;
	public static final int CONN_CLOSED = 6;
	public static final int CONN_FAILED = 7;
	public static final int SUCCESS = 0;
	public static final int ERROR_NO_CONNECTION = 1;
	public static final int ERROR_GOT_NACK = 2;
	public static final int ERROR_ACK_TIMEOUT = 3;

	public abstract void gotConnectionStatus(int paramInt1, int paramInt2);

	public abstract void gotMemberList(int[] paramArrayOfInt);

	public abstract void gotMessage(int paramInt, String paramString);

	public abstract void gotMessage(int paramInt, byte[] paramArrayOfByte);

	public abstract void gotSignal(int paramInt1, int paramInt2);

	public abstract void gotResult(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2);
}
