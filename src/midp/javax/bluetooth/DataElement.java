package javax.bluetooth;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Full implementation of DataElement for SDP.
 * Supports all data types defined in JSR-82.
 */
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

    private final int dataType;
    private Object value; // Long, Boolean, String, UUID, byte[], Vector<DataElement>
    private Vector<DataElement> elements; // for DATSEQ/DATALT

    /**
     * Creates NULL, DATSEQ, DATALT.
     */
    public DataElement(final int valueType) {
        if (valueType != NULL && valueType != DATSEQ && valueType != DATALT) {
            throw new IllegalArgumentException("Use other constructor for type " + valueType);
        }
        this.dataType = valueType;
        if (valueType == DATSEQ || valueType == DATALT) {
            this.elements = new Vector<>();
            this.value = elements;
        } else {
            this.value = null;
        }
    }

    public DataElement(final boolean bool) {
        this.dataType = BOOL;
        this.value = bool ? Boolean.TRUE : Boolean.FALSE;
    }

    public DataElement(final int valueType, final long value) {
        this.dataType = valueType;
        switch (valueType) {
            case U_INT_1:
                if (value < 0 || value > 0xFFL) throw new IllegalArgumentException("U_INT_1 out of range");
                break;
            case U_INT_2:
                if (value < 0 || value > 0xFFFFL) throw new IllegalArgumentException("U_INT_2 out of range");
                break;
            case U_INT_4:
                if (value < 0 || value > 0xFFFFFFFFL) throw new IllegalArgumentException("U_INT_4 out of range");
                break;
            case INT_1:
                if (value < -128 || value > 127) throw new IllegalArgumentException("INT_1 out of range");
                break;
            case INT_2:
                if (value < -32768 || value > 32767) throw new IllegalArgumentException("INT_2 out of range");
                break;
            case INT_4:
                if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) throw new IllegalArgumentException("INT_4 out of range");
                break;
            case INT_8:
                // any long is valid
                break;
            default:
                throw new IllegalArgumentException("Invalid type for long constructor: " + valueType);
        }
        this.value = Long.valueOf(value);
    }

    public DataElement(final int valueType, final Object value) {
        if (value == null) throw new NullPointerException();
        this.dataType = valueType;
        switch (valueType) {
            case U_INT_8:
            case U_INT_16:
            case INT_16:
                if (!(value instanceof byte[])) throw new IllegalArgumentException("Expected byte[] for type " + valueType);
                break;
            case URL:
            case STRING:
                if (!(value instanceof String)) throw new IllegalArgumentException("Expected String for type " + valueType);
                break;
            case UUID:
                if (!(value instanceof UUID)) throw new IllegalArgumentException("Expected UUID for type " + valueType);
                break;
            case BOOL:
                if (!(value instanceof Boolean)) throw new IllegalArgumentException("Expected Boolean");
                break;
            case DATSEQ:
            case DATALT:
                throw new IllegalArgumentException("Use DataElement(int) for DATSEQ/DATALT");
            case NULL:
                throw new IllegalArgumentException("NULL has no value");
            default:
                throw new IllegalArgumentException("Invalid type: " + valueType);
        }
        this.value = value;
        if (value instanceof byte[]) {
            // Defensive copy
            byte[] src = (byte[]) value;
            byte[] copy = new byte[src.length];
            System.arraycopy(src, 0, copy, 0, src.length);
            this.value = copy;
        }
    }

    public void addElement(final DataElement elem) {
        if (dataType != DATSEQ && dataType != DATALT) {
            throw new ClassCastException("Not DATSEQ/DATALT");
        }
        if (elem == null) throw new NullPointerException();
        elements.addElement(elem);
    }

    public void insertElementAt(final DataElement elem, final int index) {
        if (dataType != DATSEQ && dataType != DATALT) {
            throw new ClassCastException("Not DATSEQ/DATALT");
        }
        if (elem == null) throw new NullPointerException();
        elements.insertElementAt(elem, index);
    }

    public int getSize() {
        if (dataType != DATSEQ && dataType != DATALT) {
            throw new ClassCastException("Not DATSEQ/DATALT");
        }
        return elements.size();
    }

    public boolean removeElement(final DataElement elem) {
        if (dataType != DATSEQ && dataType != DATALT) {
            throw new ClassCastException("Not DATSEQ/DATALT");
        }
        return elements.removeElement(elem);
    }

    public int getDataType() {
        return dataType;
    }

    public long getLong() {
        if (value instanceof Long) {
            return ((Long) value).longValue();
        }
        throw new ClassCastException("Not a long type: " + dataType);
    }

    public boolean getBoolean() {
        if (dataType == BOOL && value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        throw new ClassCastException("Not BOOL");
    }

    public Object getValue() {
        if (dataType == DATSEQ || dataType == DATALT) {
            // Return enumeration of elements
            return elements.elements();
        }
        if (dataType == NULL) {
            throw new ClassCastException("NULL has no value");
        }
        if (value instanceof byte[]) {
            byte[] src = (byte[]) value;
            byte[] copy = new byte[src.length];
            System.arraycopy(src, 0, copy, 0, src.length);
            return copy;
        }
        return value;
    }

    @Override
    public String toString() {
        return "DataElement[type=" + dataType + ", value=" + value + "]";
    }
}
