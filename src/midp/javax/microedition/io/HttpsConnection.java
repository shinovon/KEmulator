package javax.microedition.io;

import java.io.IOException;

public interface HttpsConnection extends HttpConnection {
	int getPort();

	SecurityInfo getSecurityInfo() throws IOException;
}
