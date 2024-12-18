package javax.microedition.io;

import java.io.IOException;

public interface SecureConnection extends SocketConnection {
	SecurityInfo getSecurityInfo() throws IOException;
}
