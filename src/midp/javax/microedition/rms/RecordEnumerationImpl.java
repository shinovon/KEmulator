package javax.microedition.rms;

import java.util.Vector;

public class RecordEnumerationImpl implements RecordEnumeration, RecordListener {
    private RecordStore recordStore;
    private Object iSync;
    private RecordFilter iRecordFilter;
    private RecordComparator iRecordComparator;
    private boolean iKeepUpdated;
    private int[] iRecordIDs;
    private int iCurrentPos;

    public RecordEnumerationImpl(RecordStore aRms, Object aSync, RecordFilter aFilter, RecordComparator aComparator, boolean aKeepUpdated) {
        recordStore = aRms;
        iSync = aSync;
        iRecordFilter = aFilter;
        iRecordComparator = aComparator;
        keepUpdated(aKeepUpdated);
        reset();
        rebuild();
    }

    public int numRecords() {
        synchronized (iSync) {
            ensureNotDestroyed();
            return iRecordIDs.length;
        }
    }

    int getRecordId(int aIndex) throws IllegalArgumentException {
        synchronized (iSync) {
            try {
                return iRecordIDs[aIndex];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException();
            }
        }
    }

    public byte[] nextRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException {
        synchronized (iSync) {
            ensureNotDestroyed();

            int recID = nextRecordId();
            return getRecord(recID);
        }
    }

    public int nextRecordId() throws InvalidRecordIDException {
        synchronized (iSync) {
            ensureNotDestroyed();
            iCurrentPos += 1;
            if (iCurrentPos >= iRecordIDs.length) {
                throw new InvalidRecordIDException("Next/previous record not found");
            }
            return iRecordIDs[iCurrentPos];
        }
    }

    public byte[] previousRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException {
        synchronized (iSync) {
            ensureNotDestroyed();
            int recID = previousRecordId();
            return getRecord(recID);
        }
    }

    public int previousRecordId() throws InvalidRecordIDException {
        synchronized (iSync) {
            ensureNotDestroyed();
            if ((iCurrentPos == 0) || (iRecordIDs.length == 0)) {
                throw new InvalidRecordIDException("Next/previous record not found");
            }
            if (iCurrentPos == -1) {
                iCurrentPos = (iRecordIDs.length - 1);
            } else {
                iCurrentPos -= 1;
            }
            return iRecordIDs[iCurrentPos];
        }
    }

    public boolean hasNextElement() {
        synchronized (iSync) {
            ensureNotDestroyed();
            return iCurrentPos < iRecordIDs.length - 1;
        }
    }

    public boolean hasPreviousElement() {
        synchronized (iSync) {
            ensureNotDestroyed();
            return (iRecordIDs.length != 0) && (iCurrentPos != 0);
        }
    }

    public void reset() {
        synchronized (iSync) {
            ensureNotDestroyed();
            iCurrentPos = -1;
        }
    }

    public void rebuild() {
        synchronized (iSync) {
            ensureNotDestroyed();
            try {
                iRecordIDs = recordStore.getRecordIds();
            } catch (RecordStoreException e) {
                iRecordIDs = new int[0];
            }
            if ((iRecordFilter != null)) {
                doTagFilter();
            }
            if (iRecordComparator != null) {
                doSort();
            }
            reset();
        }
    }

    public void keepUpdated(boolean aKeepUpdated) {
        synchronized (iSync) {
            ensureNotDestroyed();
            iKeepUpdated = aKeepUpdated;
            if (iKeepUpdated) {
                rebuild();
                recordStore.addRecordListener(this);
            } else {
                recordStore.removeRecordListener(this);
            }
        }
    }

    public boolean isKeptUpdated() {
        synchronized (iSync) {
            ensureNotDestroyed();
            return iKeepUpdated;
        }
    }

    public void destroy() {
        synchronized (iSync) {
            ensureNotDestroyed();

            keepUpdated(false);
            iRecordFilter = null;
            iRecordComparator = null;
            iRecordIDs = null;
            recordStore = null;
        }
    }

    public void recordAdded(RecordStore aRecordStore, int aRecordId) {
        try {
            byte[] newRecord = getRecord(aRecordId);
            if ((iRecordFilter != null) && (!iRecordFilter.matches(newRecord))) {
                return;
            }
            int[] temp = new int[iRecordIDs.length + 1];
            temp[0] = aRecordId;
            System.arraycopy(iRecordIDs, 0, temp, 1, iRecordIDs.length);
            iRecordIDs = temp;
            if (iRecordComparator != null) {
                int i = 0;
                for (int j = 1; j < iRecordIDs.length; j++) {
                    if (iRecordComparator.compare(newRecord, getRecord(iRecordIDs[j])) != 1) {
                        break;
                    }
                    iRecordIDs[i] = iRecordIDs[j];
                    iRecordIDs[j] = aRecordId;
                    i++;
                }
                if ((iCurrentPos != -1) && (i <= iCurrentPos)) {
                    iCurrentPos += 1;
                }
            }
        } catch (RecordStoreException ignored) {
        }
    }

    public void recordChanged(RecordStore aRecordStore, int aRecordId) {
        recordDeleted(aRecordStore, aRecordId);
        recordAdded(aRecordStore, aRecordId);
    }

    public void recordDeleted(RecordStore aRecordStore, int aRecordId) {
        for (int i = 0; i < iRecordIDs.length; i++) {
            if (aRecordId == iRecordIDs[i]) {
                int[] temp = new int[iRecordIDs.length - 1];
                System.arraycopy(iRecordIDs, 0, temp, 0, i);
                System.arraycopy(iRecordIDs, i + 1, temp, i, iRecordIDs.length - i - 1);
                if ((iCurrentPos == temp.length) || (i <= iCurrentPos)) {
                    iCurrentPos -= 1;
                }
                iRecordIDs = temp;
            }
        }
    }

    private void ensureNotDestroyed() {
        if (recordStore == null) {
            throw new IllegalStateException("RecordEnumeration is not usable after destroy() has been called");
        }
    }

    private void doTagFilter() {
        try {
            Vector filtered = new Vector();
            for (int i = 0; i < iRecordIDs.length; i++) {
                byte[] record = getRecord(iRecordIDs[i]);
                if (filterMatches(record)) {
                    filtered.addElement(new Integer(iRecordIDs[i]));
                }
            }
            iRecordIDs = vectorToIntArray(filtered);
        } catch (RecordStoreException ignored) {
        }
    }

    private boolean filterMatches(byte[] aRecord) {
        return (iRecordFilter == null) || (iRecordFilter.matches(aRecord));
    }

    private int[] vectorToIntArray(Vector aVector) {
        int[] array = new int[aVector.size()];
        for (int i = 0; i < array.length; i++) {
            Integer id = (Integer) aVector.elementAt(i);
            array[i] = id.intValue();
        }
        return array;
    }

    private void doSort() {
        try {
            quickSort(iRecordIDs, 0, iRecordIDs.length - 1);
        } catch (RecordStoreException ignored) {
        }
    }

    private byte[] getRecord(int aRecordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        return recordStore.getRecord(aRecordId);
    }

    private void quickSort(int[] aArray, int aLeft, int aRight) throws RecordStoreException {
        if (aLeft >= aRight) {
            return;
        }
        int index = partition(aArray, aLeft, aRight);
        if (aLeft < index - 1) {
            quickSort(aArray, aLeft, index - 1);
        }
        if (index < aRight) {
            quickSort(aArray, index, aRight);
        }
    }

    private int partition(int[] aArray, int aLeft, int aRight) throws RecordStoreException {
        int i = aLeft;
        int j = aRight;

        int pivotIndex = aLeft + aRight >>> 1;
        byte[] pivot = getRecord(iRecordIDs[pivotIndex]);
        while (i <= j) {
            while (iRecordComparator.compare(getRecord(aArray[i]), pivot) == -1) {
                i++;
            }
            while (iRecordComparator.compare(getRecord(aArray[j]), pivot) == 1) {
                j--;
            }
            if (i <= j) {
                int tmp = aArray[i];
                aArray[i] = aArray[j];
                aArray[j] = tmp;
                i++;
                j--;
            }
        }
        return i;
    }
}
