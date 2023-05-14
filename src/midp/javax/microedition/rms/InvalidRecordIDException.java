package javax.microedition.rms;

public class InvalidRecordIDException extends RecordStoreException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidRecordIDException() {
        super();
    }
    
    public InvalidRecordIDException(final String s) {
        super(s);
    }
}
