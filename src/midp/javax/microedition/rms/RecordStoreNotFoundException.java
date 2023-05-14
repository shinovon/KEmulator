package javax.microedition.rms;

public class RecordStoreNotFoundException extends RecordStoreException
{
    private static final long serialVersionUID = 1L;
    
    public RecordStoreNotFoundException() {
        super();
    }
    
    public RecordStoreNotFoundException(final String s) {
        super(s);
    }
}
