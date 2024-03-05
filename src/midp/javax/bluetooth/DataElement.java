package javax.bluetooth;

public class DataElement {
    public static final int NULL = 0;
    public static final int U_INT_1 = 8;
    public static final int U_INT_2 = 9;
    public static final int U_INT_4 = 10;
    public static final int U_INT_8 = 11;
    public static final int U_INT_16 = 12;
    public static final int INT_1 = 16;
    public static final int INT_2 = 17;
    public static final int INT_4 = 18;
    public static final int INT_8 = 19;
    public static final int INT_16 = 20;
    public static final int URL = 64;
    public static final int UUID = 24;
    public static final int BOOL = 40;
    public static final int STRING = 32;
    public static final int DATSEQ = 48;
    public static final int DATALT = 56;

    public DataElement(final int n) {
        super();
    }

    public DataElement(final boolean b) {
        super();
    }

    public DataElement(final int n, final long n2) {
        super();
    }

    public DataElement(final int n, final Object o) {
        super();
    }

    public void addElement(final DataElement dataElement) {
    }

    public void insertElementAt(final DataElement dataElement, final int n) {
    }

    public int getSize() {
        return 0;
    }

    public boolean removeElement(final DataElement dataElement) {
        return false;
    }

    public int getDataType() {
        return 0;
    }

    public long getLong() {
        return -1L;
    }

    public boolean getBoolean() {
        return false;
    }

    public Object getValue() {
        return null;
    }
}
