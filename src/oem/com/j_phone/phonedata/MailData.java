package com.j_phone.phonedata;

import java.io.IOException;

public abstract interface MailData
		extends DataElement {
	public static final int FROM_ADDRESS_INFO = 1;
	public static final int TO_ADDRESS_INFO = 2;
	public static final int CC_ADDRESS_INFO = 3;
	public static final int BCC_ADDRESS_INFO = 4;
	public static final int REPLYTO_ADDRESS_INFO = 5;
	public static final int FROM_NAME_INFO = 6;
	public static final int TO_NAME_INFO = 7;
	public static final int CC_NAME_INFO = 8;
	public static final int BCC_NAME_INFO = 9;
	public static final int SUBJECT_INFO = 10;
	public static final int BODY_INFO = 11;
	public static final int DATE_INFO = 12;
	public static final int MAIL_TYPE_INFO = 13;
	public static final int MAIL_TYPE_SUPER = 0;
	public static final int MAIL_TYPE_SKY = 1;
	public static final int MAIL_TYPE_GREETING = 2;
	public static final int PRIORITY_NOMAL = 0;
	public static final int PRIORITY_URGENT = 1;
	public static final int PRIORITY_LOW = 2;
	public static final int CONFIRM_OFF = 0;
	public static final int CONFIRM_ON = 1;
	public static final int UNREAD = 0;
	public static final int ADREAD = 1;
	public static final int SEND_STATE_MIDST = 0;
	public static final int SEND_STATE_SUCCESS = 1;
	public static final int SEND_STATE_CANCEL = 2;
	public static final int SEND_STATE_FAIL = 3;
	public static final int SEND_STATE_NO_MESSAGE = 4;

	public abstract boolean isUnRead()
			throws IOException;

	public abstract boolean hasRemainder()
			throws IOException;

	public abstract int hasSendState()
			throws IOException;

	public abstract int getAttachedFileCount()
			throws IOException;

	public abstract String getAttachedFileName(int paramInt)
			throws IOException;

	public abstract byte[] getAttachedFileData(int paramInt)
			throws IOException;

	public abstract void setState(int paramInt)
			throws IOException;

	public abstract int setAttachedFile(String paramString)
			throws IOException;

	public abstract int setAttachedData(byte[] paramArrayOfByte, String paramString, int paramInt)
			throws IOException;

	public abstract void removeAttachedFile(int paramInt)
			throws IOException;

	public abstract void setConfirm(int paramInt)
			throws IOException;

	public abstract void setPriority(int paramInt)
			throws IOException;
}
