package com.j_phone.io;

import java.io.IOException;

public class ConnectionException extends IOException {

	public ConnectionException() {
		super();
	}

	public ConnectionException(int stat) {
		super();
	}

	public ConnectionException(int status, String message) {
		super(message);
	}

	public int getStatus() {
		return 0;
	}
}
