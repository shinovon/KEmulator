package javax.microedition.rms;

public interface RecordListener
{
    void recordAdded(final RecordStore p0, final int p1);
    
    void recordChanged(final RecordStore p0, final int p1);
    
    void recordDeleted(final RecordStore p0, final int p1);
}
