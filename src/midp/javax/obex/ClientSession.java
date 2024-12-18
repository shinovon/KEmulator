package javax.obex;

import javax.microedition.io.Connection;
import java.io.IOException;

public interface ClientSession extends Connection {
	void setAuthenticator(final Authenticator p0);

	HeaderSet createHeaderSet();

	void setConnectionID(final long p0);

	long getConnectionID();

	HeaderSet connect(final HeaderSet p0) throws IOException;

	HeaderSet disconnect(final HeaderSet p0) throws IOException;

	HeaderSet setPath(final HeaderSet p0, final boolean p1, final boolean p2) throws IOException;

	HeaderSet delete(final HeaderSet p0) throws IOException;

	Operation get(final HeaderSet p0) throws IOException;

	Operation put(final HeaderSet p0) throws IOException;
}
