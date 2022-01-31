package javax.microedition.pim;

public abstract interface PIMItem
{
  public static final int BINARY = 0;
  public static final int BOOLEAN = 1;
  public static final int DATE = 2;
  public static final int INT = 3;
  public static final int STRING = 4;
  public static final int STRING_ARRAY = 5;
  public static final int ATTR_NONE = 0;
  public static final int EXTENDED_FIELD_MIN_VALUE = 16777216;
  public static final int EXTENDED_ATTRIBUTE_MIN_VALUE = 16777216;
  
  public abstract PIMList getPIMList();
  
  public abstract void commit()
    throws PIMException;
  
  public abstract boolean isModified();
  
  public abstract int[] getFields();
  
  public abstract byte[] getBinary(int paramInt1, int paramInt2);
  
  public abstract void addBinary(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3, int paramInt4);
  
  public abstract void setBinary(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, int paramInt4, int paramInt5);
  
  public abstract long getDate(int paramInt1, int paramInt2);
  
  public abstract void addDate(int paramInt1, int paramInt2, long paramLong);
  
  public abstract void setDate(int paramInt1, int paramInt2, int paramInt3, long paramLong);
  
  public abstract int getInt(int paramInt1, int paramInt2);
  
  public abstract void addInt(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void setInt(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract String getString(int paramInt1, int paramInt2);
  
  public abstract void addString(int paramInt1, int paramInt2, String paramString);
  
  public abstract void setString(int paramInt1, int paramInt2, int paramInt3, String paramString);
  
  public abstract boolean getBoolean(int paramInt1, int paramInt2);
  
  public abstract void addBoolean(int paramInt1, int paramInt2, boolean paramBoolean);
  
  public abstract void setBoolean(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
  
  public abstract String[] getStringArray(int paramInt1, int paramInt2);
  
  public abstract void addStringArray(int paramInt1, int paramInt2, String[] paramArrayOfString);
  
  public abstract void setStringArray(int paramInt1, int paramInt2, int paramInt3, String[] paramArrayOfString);
  
  public abstract int countValues(int paramInt);
  
  public abstract void removeValue(int paramInt1, int paramInt2);
  
  public abstract int getAttributes(int paramInt1, int paramInt2);
  
  public abstract void addToCategory(String paramString)
    throws PIMException;
  
  public abstract void removeFromCategory(String paramString);
  
  public abstract String[] getCategories();
  
  public abstract int maxCategories();
}
