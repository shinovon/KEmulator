package javax.microedition.rms;

final class RecordEnumerationImpl implements RecordEnumeration, RecordListener
{
    int anInt622;
    private RecordStore aRecordStore623;
    private boolean aBoolean624;
    
    public RecordEnumerationImpl(final RecordStore aRecordStore623, final boolean b) {
        super();
        this.aRecordStore623 = aRecordStore623;
        this.keepUpdated(b);
        this.reset();
    }
    
    public final int numRecords() {
        return this.aRecordStore623.records.size();
    }
    
    public final byte[] nextRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException {
        if (this.anInt622 >= this.numRecords()) {
            throw new InvalidRecordIDException("" + this.anInt622);
        }
        final byte[] record = this.aRecordStore623.getRecord(this.method349());
        this.nextRecordId();
        return record;
    }
    
    public final int nextRecordId() throws InvalidRecordIDException {
        final int method349 = this.method349();
        ++this.anInt622;
        return method349;
    }
    
    public final byte[] previousRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException {
        if (this.anInt622 < 0) {
            throw new InvalidRecordIDException("" + this.anInt622);
        }
        final byte[] record = this.aRecordStore623.getRecord(this.method349());
        this.previousRecordId();
        return record;
    }
    
    private int method349() throws InvalidRecordIDException {
        if (this.anInt622 < 0 || this.anInt622 >= this.aRecordStore623.records.size()) {
            throw new InvalidRecordIDException("" + this.anInt622);
        }
        return (int) this.aRecordStore623.records.get(this.anInt622);
    }
    
    public final int previousRecordId() throws InvalidRecordIDException {
        final int method349 = this.method349();
        --this.anInt622;
        return method349;
    }
    
    public final boolean hasNextElement() {
        return this.anInt622 < this.numRecords();
    }
    
    public final boolean hasPreviousElement() {
        return this.anInt622 > 0;
    }
    
    public final void reset() {
        this.anInt622 = 0;
    }
    
    public final void rebuild() {
    }
    
    public final void keepUpdated(final boolean aBoolean624) {
        if (aBoolean624 != this.aBoolean624) {
            this.aBoolean624 = aBoolean624;
            if (aBoolean624) {
                this.aRecordStore623.addRecordListener(this);
                this.rebuild();
                return;
            }
            this.aRecordStore623.removeRecordListener(this);
        }
    }
    
    public final boolean isKeptUpdated() {
        return this.aBoolean624;
    }
    
    public final void destroy() {
        if (this.aBoolean624) {
            this.aRecordStore623.removeRecordListener(this);
        }
        this.aRecordStore623 = null;
    }
    
    public final synchronized void recordAdded(final RecordStore recordStore, final int n) {
    }
    
    public final synchronized void recordChanged(final RecordStore recordStore, final int n) {
    }
    
    public final synchronized void recordDeleted(final RecordStore recordStore, final int n) {
    }
}
