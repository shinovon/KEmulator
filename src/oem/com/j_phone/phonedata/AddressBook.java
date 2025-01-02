package com.j_phone.phonedata;

import java.io.IOException;

public abstract interface AddressBook
		extends PhoneData {
	public static final int GROUP_SEARCH = 1;
	public static final int KANA_SEARCH = 2;
	public static final int NUMBER_SEARCH = 3;
	public static final int MAIL_ADDRESS_SEARCH = 4;

	public abstract int[] getGroupNoList()
			throws IOException;

	public abstract String getGroupName(int paramInt)
			throws IOException;

	public abstract int getPhoneNumberMaxCount()
			throws IOException;

	public abstract int getMailAddressMaxCount()
			throws IOException;

	public abstract DataEnumeration elements(int paramInt1, String paramString, int paramInt2, int paramInt3)
			throws IOException;
}
