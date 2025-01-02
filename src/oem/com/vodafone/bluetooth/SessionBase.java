package com.vodafone.bluetooth;

public abstract class SessionBase {
	public static final int REPORT_NONE = 0;
	public static final int REPORT_ERROR = 1;
	public static final int REPORT_RESULT = 2;

	SessionBase(SessionListener paramSessionListener)
			throws NullPointerException {
	}

	public boolean close(int paramInt) {
		return false;
	}

	public int send(int[] paramArrayOfInt, String paramString, int paramInt)
			throws NullPointerException, IllegalArgumentException {
		return 0;
	}

	public int send(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt)
			throws NullPointerException, IllegalArgumentException {
		return 0;
	}

	public int sendSignal(int[] paramArrayOfInt, int paramInt1, int paramInt2)
			throws NullPointerException, IllegalArgumentException {
		return 0;
	}

	public void cleanAllMessage() {
	}
}
