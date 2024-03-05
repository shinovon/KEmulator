package javax.obex;

import javax.microedition.io.*;
import java.io.*;

public interface SessionNotifier extends Connection {
    Connection acceptAndOpen(final ServerRequestHandler p0) throws IOException;

    Connection acceptAndOpen(final ServerRequestHandler p0, final Authenticator p1) throws IOException;
}
