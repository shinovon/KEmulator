package javax.microedition.io;

import java.io.*;

public interface SecureConnection extends SocketConnection
{
    SecurityInfo getSecurityInfo() throws IOException;
}
