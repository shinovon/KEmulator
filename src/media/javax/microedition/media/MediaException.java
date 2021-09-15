package javax.microedition.media;

import javazoom.jl.decoder.JavaLayerException;

public class MediaException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public MediaException() {
        super();
    }
    
    public MediaException(final String s) {
        super(s);
    }

	public MediaException(Exception e) {
        super(e);
	}
}
