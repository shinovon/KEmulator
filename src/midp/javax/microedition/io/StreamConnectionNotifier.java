package javax.microedition.io;

import java.io.IOException;

public interface StreamConnectionNotifier extends Connection {
	StreamConnection acceptAndOpen() throws IOException;
}
