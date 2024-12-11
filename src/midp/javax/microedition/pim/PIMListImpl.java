package javax.microedition.pim;

import java.util.Enumeration;

public class PIMListImpl implements PIMList {
    public String getName() {
        return null;
    }

    public void close() throws PIMException {

    }

    public Enumeration items() throws PIMException {
        return new Enumeration() {
            public boolean hasMoreElements() {
                return false;
            }
            public Object nextElement() {
                return null;
            }
        };
    }

    public Enumeration items(PIMItem paramPIMItem) throws PIMException {
        return items();
    }

    public Enumeration items(String paramString) throws PIMException {
        return items();
    }

    public Enumeration itemsByCategory(String paramString) throws PIMException {
        return items();
    }

    public String[] getCategories() throws PIMException {
        return new String[0];
    }

    public boolean isCategory(String paramString) throws PIMException {
        return false;
    }

    public void addCategory(String paramString) throws PIMException {

    }

    public void deleteCategory(String paramString, boolean paramBoolean) throws PIMException {

    }

    public void renameCategory(String paramString1, String paramString2) throws PIMException {

    }

    public int maxCategories() {
        return 0;
    }

    public boolean isSupportedField(int paramInt) {
        return false;
    }

    public int[] getSupportedFields() {
        return new int[0];
    }

    public boolean isSupportedAttribute(int paramInt1, int paramInt2) {
        return false;
    }

    public int[] getSupportedAttributes(int paramInt) {
        return new int[0];
    }

    public boolean isSupportedArrayElement(int paramInt1, int paramInt2) {
        return false;
    }

    public int[] getSupportedArrayElements(int paramInt) {
        return new int[0];
    }

    public int getFieldDataType(int paramInt) {
        return 0;
    }

    public String getFieldLabel(int paramInt) {
        return null;
    }

    public String getAttributeLabel(int paramInt) {
        return null;
    }

    public String getArrayElementLabel(int paramInt1, int paramInt2) {
        return null;
    }

    public int maxValues(int paramInt) {
        return 0;
    }

    public int stringArraySize(int paramInt) {
        return 0;
    }
}
