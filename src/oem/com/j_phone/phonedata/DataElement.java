package com.j_phone.phonedata;

import java.io.IOException;
import java.util.Date;

public abstract interface DataElement {
	public static final int STRING = 1;
	public static final int INT = 2;
	public static final int DATE = 3;
	public static final int BOOLEAN = 4;

	public abstract String getType();

	public abstract int getElementCount(int paramInt)
			throws IOException;

	public abstract int getDataType(int paramInt);

	public abstract String getString(int paramInt1, int paramInt2)
			throws IOException;

	public abstract Integer getInt(int paramInt1, int paramInt2)
			throws IOException;

	public abstract Date getDate(int paramInt1, int paramInt2)
			throws IOException;

	public abstract Boolean getBoolean(int paramInt1, int paramInt2)
			throws IOException;

	public abstract void setString(int paramInt1, int paramInt2, String paramString)
			throws IOException;

	public abstract void setInt(int paramInt1, int paramInt2, Integer paramInteger)
			throws IOException;

	public abstract void setBoolean(int paramInt1, int paramInt2, Boolean paramBoolean)
			throws IOException;

	public abstract boolean isListElement();

	public abstract DataElement createClone()
			throws IOException;
}
