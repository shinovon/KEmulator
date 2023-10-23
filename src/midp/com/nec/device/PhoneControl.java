package com.nec.device;

public final class PhoneControl
{
  public static final int DEV_BACKLIGHT = 0;
  public static final int ATTR_BACKLIGHT_OFF = 1;
  public static final int ATTR_BACKLIGHT_ON = 2;
  public static final int DEV_VIBRATION = 3;
  public static final int ATTR_VIBRATION_OFF = 4;
  public static final int ATTR_VIBRATION_1_ON = 5;
  public static final int ATTR_VIBRATION_2_ON = 6;
  public static final int ATTR_VIBRATION_3_ON = 7;
  
  public static final void setAttribute(int i, int j)
    throws IllegalArgumentException
  {}
  
  public static final int getAttribute(int dev)
    throws IllegalArgumentException
  {
    return 0;
  }
  
  public static final boolean isAvailable(int dev)
    throws IllegalArgumentException
  {
    return false;
  }
  
  private static boolean _isDevice(int dev)
  {
    return false;
  }
  
  private static boolean _isAttribute(int attr)
  {
    return false;
  }
}
