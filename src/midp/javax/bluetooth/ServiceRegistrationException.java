package javax.bluetooth;

import java.io.*;

public class ServiceRegistrationException extends IOException {
	private static final long serialVersionUID = 1L;

	public ServiceRegistrationException() {
		super();
	}

	public ServiceRegistrationException(final String s) {
		super(s);
	}
}
