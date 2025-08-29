package com.nttdocomo.io;

import java.io.IOException;

public abstract interface RandomAccessible
{
  public abstract long getSize()
    throws IOException;
  
  public abstract long getPosition()
    throws IOException;
  
  public abstract void setPosition(long paramLong)
    throws IOException;
  
  public abstract void setPositionRelative(long paramLong)
    throws IOException;
}
