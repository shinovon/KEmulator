package javax.microedition.io.file;

public class IllegalModeException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public IllegalModeException() {
        super();
    }
    
    public IllegalModeException(final String s) {
        super(s);
    }
}
