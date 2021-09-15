package javax.microedition.io;

public interface ContentConnection extends StreamConnection
{
    String getType();
    
    String getEncoding();
    
    long getLength();
}
