package com.jblend.io;

import java.io.IOException;
import javax.microedition.io.Connection;

public abstract interface ConnectionImplFactory {
	public abstract Connection createConnectionImpl(String paramString, int paramInt, boolean paramBoolean)
			throws IOException;
}
