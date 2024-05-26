package javax.microedition.io;

import java.io.*;

public interface StreamConnectionNotifier extends Connection {
	StreamConnection acceptAndOpen() throws IOException;
}
