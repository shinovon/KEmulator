package javax.microedition.io;

import java.io.*;

public interface OutputConnection extends Connection {
	OutputStream openOutputStream() throws IOException;

	DataOutputStream openDataOutputStream() throws IOException;
}
