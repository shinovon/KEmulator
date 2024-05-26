package javax.microedition.pim;

import java.util.Enumeration;

public abstract interface PIMList {
	public static final String UNCATEGORIZED = null;

	public abstract String getName();

	public abstract void close()
			throws PIMException;

	public abstract Enumeration items()
			throws PIMException;

	public abstract Enumeration items(PIMItem paramPIMItem)
			throws PIMException;

	public abstract Enumeration items(String paramString)
			throws PIMException;

	public abstract Enumeration itemsByCategory(String paramString)
			throws PIMException;

	public abstract String[] getCategories()
			throws PIMException;

	public abstract boolean isCategory(String paramString)
			throws PIMException;

	public abstract void addCategory(String paramString)
			throws PIMException;

	public abstract void deleteCategory(String paramString, boolean paramBoolean)
			throws PIMException;

	public abstract void renameCategory(String paramString1, String paramString2)
			throws PIMException;

	public abstract int maxCategories();

	public abstract boolean isSupportedField(int paramInt);

	public abstract int[] getSupportedFields();

	public abstract boolean isSupportedAttribute(int paramInt1, int paramInt2);

	public abstract int[] getSupportedAttributes(int paramInt);

	public abstract boolean isSupportedArrayElement(int paramInt1, int paramInt2);

	public abstract int[] getSupportedArrayElements(int paramInt);

	public abstract int getFieldDataType(int paramInt);

	public abstract String getFieldLabel(int paramInt);

	public abstract String getAttributeLabel(int paramInt);

	public abstract String getArrayElementLabel(int paramInt1, int paramInt2);

	public abstract int maxValues(int paramInt);

	public abstract int stringArraySize(int paramInt);
}
