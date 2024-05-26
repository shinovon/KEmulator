package javax.microedition.io;

import java.io.*;

public interface DatagramConnection extends Connection {
	int getMaximumLength() throws IOException;

	int getNominalLength() throws IOException;

	void send(final Datagram p0) throws IOException;

	void receive(final Datagram p0) throws IOException;

	Datagram newDatagram(final int p0) throws IOException;

	Datagram newDatagram(final int p0, final String p1) throws IOException;

	Datagram newDatagram(final byte[] p0, final int p1) throws IOException;

	Datagram newDatagram(final byte[] p0, final int p1, final String p2) throws IOException;
}
