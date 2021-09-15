package javax.microedition.io;

import java.io.*;

public interface ServerSocketConnection extends StreamConnectionNotifier
{
    String getLocalAddress() throws IOException;
    
    int getLocalPort() throws IOException;
}
