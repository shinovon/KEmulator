package javax.bluetooth;

import java.io.*;

public class BluetoothStateException extends IOException {
	private static final long serialVersionUID = 1L;

	public BluetoothStateException() {
		super();
	}

	public BluetoothStateException(final String s) {
		super(s);
	}
}
