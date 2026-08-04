package com.nttdocomo.io;

import java.io.DataOutput;
import java.io.IOException;

public class FileDataOutput
  implements DataOutput, RandomAccessible
{
  
  public long getSize()
    throws IOException
  {
    return 0L;
  }
  
  public long getPosition()
    throws IOException
  {
    return 0L;
  }
  
  public void setPosition(long paramLong)
    throws IOException
  {}
  
  public void setPositionRelative(long paramLong)
    throws IOException
  {}
  
  public void truncate(long paramLong)
    throws IOException
  {}
  
  public void flush()
    throws IOException
  {}
  
  public void close()
    throws IOException
  {}
  
  public void writeString(String paramString)
    throws IOException
  {}
  
  public void writeString(String paramString, int paramInt)
    throws IOException
  {}
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {}
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {}
  
  public void write(int paramInt)
    throws IOException
  {}
  
  public void writeBoolean(boolean paramBoolean)
    throws IOException
  {}
  
  public void writeByte(int paramInt)
    throws IOException
  {}
  
  public void writeBytes(String paramString)
    throws IOException
  {}
  
  public void writeChar(int paramInt)
    throws IOException
  {}
  
  public void writeChars(String paramString)
    throws IOException
  {}
  
  public void writeDouble(double paramDouble)
    throws IOException
  {}
  
  public void writeFloat(float paramFloat)
    throws IOException
  {}
  
  public void writeInt(int paramInt)
    throws IOException
  {}
  
  public void writeLong(long paramLong)
    throws IOException
  {}
  
  public void writeShort(int paramInt)
    throws IOException
  {}
  
  public void writeUTF(String paramString)
    throws IOException
  {}
}
