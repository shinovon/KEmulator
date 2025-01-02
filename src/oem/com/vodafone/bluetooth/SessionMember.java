package com.vodafone.bluetooth;

import java.io.IOException;

public class SessionMember
		extends SessionBase {
	public SessionMember(SessionListener paramSessionListener)
			throws NullPointerException {
		super(null);
	}

	public final int open(LocalService paramLocalService)
			throws SecurityException, NullPointerException, IllegalStateException, IOException {
		return 0;
	}

	public final int openSecured(LocalService paramLocalService, boolean paramBoolean1, boolean paramBoolean2)
			throws SecurityException, NullPointerException, IllegalStateException, IOException {
		return 0;
	}

	public String getBluetoothAddress(int paramInt) {
		return null;
	}
}
