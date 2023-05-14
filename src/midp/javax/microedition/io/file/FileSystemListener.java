package javax.microedition.io.file;

public interface FileSystemListener
{
    public static final int ROOT_ADDED = 0;
    public static final int ROOT_REMOVED = 1;
    
    void rootChanged(final int p0, final String p1);
}
