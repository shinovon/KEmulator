package com.nttdocomo.io;

import com.nttdocomo.fs.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileEntity
{
  protected FileEntity(File paramFile, int paramInt)
    throws IOException
  {}
  
  public synchronized void close()
    throws IOException
  {}
  
  public void setBufferSize(int paramInt)
    throws IOException
  {}
  
  public int getBufferSize()
    throws IOException
  {
    return 0;
  }
  
  public FileDataInput openDataInput()
    throws IOException
  {
    return null;
  }
  
  public FileDataOutput openDataOutput()
    throws IOException
  {
    return null;
  }
  
  public InputStream openInputStream()
    throws IOException
  {
    return null;
  }
  
  public OutputStream openOutputStream()
    throws IOException
  {
    return null;
  }
}
