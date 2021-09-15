package javax.microedition.io;

import java.io.*;

public interface UDPDatagramConnection extends DatagramConnection
{
    String getLocalAddress() throws IOException;
    
    int getLocalPort() throws IOException;
}
