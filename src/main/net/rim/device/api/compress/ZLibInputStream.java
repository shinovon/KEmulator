package net.rim.device.api.compress;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class ZLibInputStream
  extends InflaterInputStream
{
  public ZLibInputStream(InputStream paramInputStream)
    throws IOException
  {
    this(paramInputStream, false, paramInputStream.available());
  }
  
  public ZLibInputStream(InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    this(paramInputStream, paramBoolean, paramInputStream.available());
  }
  
  public ZLibInputStream(InputStream paramInputStream, boolean paramBoolean, int paramInt)
    throws IOException
  {
    super(paramInputStream, new Inflater(paramBoolean), paramInt);
  }
}
