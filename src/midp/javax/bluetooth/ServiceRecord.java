package javax.bluetooth;

import java.io.IOException;

public interface ServiceRecord {
	public static final int NOAUTHENTICATE_NOENCRYPT = 0;
	public static final int AUTHENTICATE_NOENCRYPT = 1;
	public static final int AUTHENTICATE_ENCRYPT = 2;

	DataElement getAttributeValue(final int p0);

	RemoteDevice getHostDevice();

	int[] getAttributeIDs();

	boolean populateRecord(final int[] p0) throws IOException;

	String getConnectionURL(final int p0, final boolean p1);

	void setDeviceServiceClasses(final int p0);

	boolean setAttributeValue(final int p0, final DataElement p1);
}
