package javax.microedition.io;

import java.io.IOException;

public interface ServerSocketConnection extends StreamConnectionNotifier {
	String getLocalAddress() throws IOException;

	int getLocalPort() throws IOException;
}
