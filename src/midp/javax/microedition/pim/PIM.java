package javax.microedition.pim;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public abstract class PIM
{
  public static final int CONTACT_LIST = 1;
  public static final int EVENT_LIST = 2;
  public static final int TODO_LIST = 3;
  public static final int READ_ONLY = 1;
  public static final int WRITE_ONLY = 2;
  public static final int READ_WRITE = 3;
  
  public static PIM getInstance()
  {
	  //TODO
    return null;
  }
  
  public abstract PIMList openPIMList(int paramInt1, int paramInt2)
    throws PIMException;
  
  public abstract PIMList openPIMList(int paramInt1, int paramInt2, String paramString)
    throws PIMException;
  
  public abstract String[] listPIMLists(int paramInt);
  
  public abstract PIMItem[] fromSerialFormat(InputStream paramInputStream, String paramString)
    throws PIMException, UnsupportedEncodingException;
  
  public abstract void toSerialFormat(PIMItem paramPIMItem, OutputStream paramOutputStream, String paramString1, String paramString2)
    throws PIMException, UnsupportedEncodingException;
  
  public abstract String[] supportedSerialFormats(int paramInt);
}
