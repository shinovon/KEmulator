package javax.microedition.io;

import java.io.*;

public interface InputConnection extends Connection
{
    InputStream openInputStream() throws IOException;
    
    DataInputStream openDataInputStream() throws IOException;
}
