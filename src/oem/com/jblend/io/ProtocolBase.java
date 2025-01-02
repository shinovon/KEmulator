package com.jblend.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;

public abstract class ProtocolBase
		implements ConnectionImplFactory, StreamConnection {
	public Connection createConnectionImpl(String paramString, int paramInt, boolean paramBoolean)
			throws IOException {
		return null;
	}

	public abstract void open(String paramString, int paramInt, boolean paramBoolean)
			throws IOException;

	public int read()
			throws IOException {
		return 0;
	}

	public void write(int paramInt)
			throws IOException {
	}

	public void close()
			throws IOException {
	}

	public InputStream openInputStream()
			throws IOException {
		return null;
	}

	public OutputStream openOutputStream()
			throws IOException {
		return null;
	}

	public DataInputStream openDataInputStream()
			throws IOException {
		return null;
	}

	public DataOutputStream openDataOutputStream()
			throws IOException {
		return null;
	}
}
