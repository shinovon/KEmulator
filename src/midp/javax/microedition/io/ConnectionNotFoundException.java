package javax.microedition.io;

import java.io.IOException;

public class ConnectionNotFoundException extends IOException {
	private static final long serialVersionUID = 1L;

	public ConnectionNotFoundException() {
		super();
	}

	public ConnectionNotFoundException(Exception e) {
		super(e);
	}

	public ConnectionNotFoundException(String message, Exception cause) {
		super(message, cause);
	}

	public ConnectionNotFoundException(String s) {
		super(s);
	}
}
