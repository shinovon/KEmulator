package javax.microedition.io;

import java.io.*;

public interface Datagram extends DataInput, DataOutput
{
    String getAddress();
    
    byte[] getData();
    
    int getLength();
    
    int getOffset();
    
    void setAddress(final String p0) throws IOException;
    
    void setAddress(final Datagram p0);
    
    void setLength(final int p0);
    
    void setData(final byte[] p0, final int p1, final int p2);
    
    void reset();
}
