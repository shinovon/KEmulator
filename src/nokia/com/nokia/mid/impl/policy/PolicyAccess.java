package com.nokia.mid.impl.policy;

public final class PolicyAccess {
	public static final int mailToID = 0;
	public static final int telID = 1;
	public static final int browserID = 2;

	public static boolean checkPermission(String api, String msg) {
		if ((api == null) || (msg == null)) {
			throw new IllegalArgumentException("api and message strings cannot be null.");
		}
		if (msg.length() == 0) {
			msg = " ";
		}
		return true;
	}

	public static boolean checkPermission(String api, int msgID) {
		return true;
	}

	public static boolean isManufacturerSigned0() {
		return true;
	}
}
