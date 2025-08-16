package com.nttdocomo.io;

import java.io.DataInput;
import java.io.IOException;

public class FileDataInput
  implements DataInput, RandomAccessible
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
  
  public void close()
    throws IOException
  {}
  
  public String readString()
    throws IOException
  {
    return null;
  }
  
  public String readString(int paramInt)
    throws IOException
  {
    return null;
  }
  
  public boolean readBoolean()
    throws IOException
  {
    return false;
  }
  
  public byte readByte()
    throws IOException
  {
    return 0;
  }
  
  public char readChar()
    throws IOException
  {
    return '\000';
  }
  
  public double readDouble()
    throws IOException
  {
    return 0.0D;
  }
  
  public float readFloat()
    throws IOException
  {
    return 0.0F;
  }
  
  public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {}
  
  public void readFully(byte[] paramArrayOfByte)
    throws IOException
  {}
  
  public int readInt()
    throws IOException
  {
    return 0;
  }
  
  public String readLine()
    throws IOException
  {
    return null;
  }
  
  public long readLong()
    throws IOException
  {
    return 0L;
  }
  
  public short readShort()
    throws IOException
  {
    return 0;
  }
  
  public int readUnsignedByte()
    throws IOException
  {
    return 0;
  }
  
  public int readUnsignedShort()
    throws IOException
  {
    return 0;
  }
  
  public String readUTF()
    throws IOException
  {
    return null;
  }
  
  public int skipBytes(int paramInt)
    throws IOException
  {
    return 0;
  }
}
