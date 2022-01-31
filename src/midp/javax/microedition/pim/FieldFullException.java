package javax.microedition.pim;

public class FieldFullException
  extends RuntimeException
{
  private int j = -1;
  
  public FieldFullException() {}
  
  public FieldFullException(String paramString)
  {
    this(paramString, -1);
  }
  
  public FieldFullException(String paramString, int paramInt)
  {
    super(paramString);
    this.j = paramInt;
  }
  
  public int getField()
  {
    return this.j;
  }
}
