package javax.microedition.io;

import java.io.*;

public interface HttpsConnection extends HttpConnection
{
    int getPort();
    
    SecurityInfo getSecurityInfo() throws IOException;
}
