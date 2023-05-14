package javax.microedition.rms;

public class RecordStoreFullException extends RecordStoreException
{
    private static final long serialVersionUID = 1L;
    
    public RecordStoreFullException() {
        super();
    }
    
    public RecordStoreFullException(final String s) {
        super(s);
    }
}
