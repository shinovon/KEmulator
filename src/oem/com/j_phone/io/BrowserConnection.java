package com.j_phone.io;

import java.io.IOException;
import javax.microedition.io.Connection;

public abstract interface BrowserConnection
		extends Connection {
	public abstract void connect()
			throws IOException;
}
