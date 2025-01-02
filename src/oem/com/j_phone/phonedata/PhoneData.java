package com.j_phone.phonedata;

import java.io.IOException;

public abstract interface PhoneData {
	public static final int SORT_ASCENDING = 0;
	public static final int SORT_DESCENDING = 1;

	public abstract void close();

	public abstract String getListType();

	public abstract DataEnumeration elements(int paramInt1, int paramInt2, int paramInt3)
			throws IOException;

	public abstract void createElement(DataElement paramDataElement)
			throws IOException;

	public abstract void delete(DataElement paramDataElement)
			throws IOException;

	public abstract void importElementRawData(byte[] paramArrayOfByte)
			throws IOException;

	public abstract byte[] exportElementRawData(DataElement paramDataElement)
			throws IOException;

	public abstract int getListMaxCount()
			throws IOException;
}
