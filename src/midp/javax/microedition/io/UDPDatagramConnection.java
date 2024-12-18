package javax.microedition.io;

import java.io.IOException;

public interface UDPDatagramConnection extends DatagramConnection {
	String getLocalAddress() throws IOException;

	int getLocalPort() throws IOException;
}
