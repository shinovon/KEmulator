package com.vodafone.bluetooth;

import java.io.IOException;

public class SessionManager
		extends SessionBase {
	public SessionManager(SessionListener paramSessionListener)
			throws NullPointerException {
		super(null);
	}

	public final int open(RemoteService paramRemoteService)
			throws SecurityException, NullPointerException, IOException {
		return 0;
	}

	public final int openSecured(RemoteService paramRemoteService, boolean paramBoolean)
			throws SecurityException, NullPointerException, IOException {
		return 0;
	}
}
