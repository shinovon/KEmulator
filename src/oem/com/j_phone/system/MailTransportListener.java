package com.j_phone.system;

public abstract interface MailTransportListener {
	public static final int MAIL_SUCCEEDED = 0;
	public static final int MAIL_FAILED = -1;
	public static final int MAIL_STOP = -2;
	public static final int MAIL_PART_FAILED = -3;
	public static final int MAIL_UNKNOWN = -4;

	public abstract void mailSent(int paramInt);

	public abstract void messageReceived(int paramInt);
}
