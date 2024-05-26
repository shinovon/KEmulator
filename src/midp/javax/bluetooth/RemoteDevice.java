package javax.bluetooth;

import java.io.*;
import javax.microedition.io.*;

public class RemoteDevice {
	protected RemoteDevice(final String s) {
		super();
	}

	public boolean isTrustedDevice() {
		return false;
	}

	public String getFriendlyName(final boolean b) throws IOException {
		return null;
	}

	public final String getBluetoothAddress() {
		return "";
	}

	public boolean equals(final Object o) {
		return false;
	}

	public int hashCode() {
		return 0;
	}

	public static RemoteDevice getRemoteDevice(final Connection connection) throws IOException {
		return null;
	}

	public boolean authenticate() throws IOException {
		return false;
	}

	public boolean authorize(final Connection connection) throws IOException {
		return false;
	}

	public boolean encrypt(final Connection connection, final boolean b) throws IOException {
		return false;
	}

	public boolean isAuthenticated() {
		return false;
	}

	public boolean isAuthorized(final Connection connection) throws IOException {
		return false;
	}

	public boolean isEncrypted() {
		return false;
	}
}
