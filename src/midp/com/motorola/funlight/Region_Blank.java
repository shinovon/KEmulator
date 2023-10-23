package com.motorola.funlight;

class Region_Blank
  implements Region
{
  public int getID()
  {
    return 0;
  }
  
  public int setColor(int i)
  {
    return 1;
  }
  
  public int setColor(byte byte0, byte byte1, byte byte2)
  {
    return 1;
  }
  
  public int getColor()
  {
    return 0;
  }
  
  public int getControl()
  {
    return 1;
  }
  
  public void releaseControl() {}
  
  public String toString()
  {
    return "Blank";
  }
}
