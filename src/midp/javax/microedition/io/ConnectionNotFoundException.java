package javax.microedition.io;

import java.io.*;

public class ConnectionNotFoundException extends IOException {
	private static final long serialVersionUID = 1L;

	public ConnectionNotFoundException() {
		super();
	}

	public ConnectionNotFoundException(Exception e) {
	}

	public ConnectionNotFoundException(String s) {
		super(s);
	}
}
