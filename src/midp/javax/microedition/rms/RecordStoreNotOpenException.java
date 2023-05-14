package javax.microedition.rms;

public class RecordStoreNotOpenException extends RecordStoreException
{
    private static final long serialVersionUID = 1L;
    
    public RecordStoreNotOpenException() {
        super();
    }
    
    public RecordStoreNotOpenException(final String s) {
        super(s);
    }
}
