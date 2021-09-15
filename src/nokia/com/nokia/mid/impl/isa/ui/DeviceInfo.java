package com.nokia.mid.impl.isa.ui;

public class DeviceInfo
{
  public static final int KEY_STOP = -23;
  public static final int KEY_NEXT_FORWARD = -22;
  public static final int KEY_PREVIOUS_REWIND = -21;
  public static final int KEY_PLAY = -20;
  public static final int KEY_END = -11;
  public static final int KEY_SEND = -10;
  public static final int KEY_SOFT_RIGHT = -7;
  public static final int KEY_SOFT_LEFT = -6;
  public static final int KEY_SOFT_MIDDLE = -5;
  public static final int KEY_SCROLL_RIGHT = -4;
  public static final int KEY_SCROLL_LEFT = -3;
  public static final int KEY_SCROLL_DOWN = -2;
  public static final int KEY_SCROLL_UP = -1;
  public static final int KEY_BACKSPACE = 8;
  public static final int KEY_LINE_FEED = 10;
  public static final int KEY_SPACE = 32;
  public static final int KEY_POUND = 35;
  public static final int KEY_STAR = 42;
  public static final int KEY_NUM0 = 48;
  public static final int KEY_NUM1 = 49;
  public static final int KEY_NUM2 = 50;
  public static final int KEY_NUM3 = 51;
  public static final int KEY_NUM4 = 52;
  public static final int KEY_NUM5 = 53;
  public static final int KEY_NUM6 = 54;
  public static final int KEY_NUM7 = 55;
  public static final int KEY_NUM8 = 56;
  public static final int KEY_NUM9 = 57;
  public static final int KEY_SOFT_SELECT = -5;
  public static final int JAM_DISPLAY_MODE_SCREEN = 0;
  public static final int JAM_DISPLAY_MODE_TIMED_ALERT = 1;
  public static final int JAM_DISPLAY_MODE_CANVAS = 2;
  public static final int JAM_DISPLAY_MODE_FULL_CANVAS = 3;
  
  static
  {
    updateDeviceColors();
    updateDeviceEvents();
  }
  
  public static boolean isColor()
  {
    return true;
  }
  
  public static int numColors()
  {
    return 65536;
  }
  
  public static int numAlphaLevels()
  {
    return 256;
  }
  
  public static boolean hasRepeatEvents()
  {
    return false;
  }
  
  public static boolean hasPointerEvents()
  {
    return true;
  }
  
  public static boolean hasPointerMotionEvents()
  {
    return true;
  }
  
  public static int getNumSoftButtons()
  {
    return 3;
  }
  
  public static int getKeyCodeForSB(int paramInt)
  {
    if (paramInt == 0) {
      return -6;
    }
    if (paramInt == 2) {
      return -7;
    }
    throw new IllegalArgumentException("Invalid softkey number: " + paramInt);
  }
  
  public static boolean isSoftkey(int paramInt)
  {
    return (paramInt == -6) || (paramInt == -7) || (paramInt == -5);
  }
  
  public static boolean isKeypadKey(int paramInt)
  {
    return (-11 != paramInt) && (-10 != paramInt) && (-7 != paramInt) && (-6 != paramInt) && (-5 != paramInt) && (-4 != paramInt) && (-3 != paramInt) && (-2 != paramInt) && (-1 != paramInt);
  }
  
  public static boolean isCharAddingKey(int paramInt)
  {
    return (10 == paramInt) || (32 == paramInt) || (42 == paramInt) || (48 == paramInt) || (49 == paramInt) || (50 == paramInt) || (51 == paramInt) || (52 == paramInt) || (53 == paramInt) || (54 == paramInt) || (55 == paramInt) || (56 == paramInt) || (57 == paramInt);
  }
  
  public static String getDateFormatString()
  {
    return eH;
  }
  
  public static String getTimeFormatString()
  {
    return eI;
  }
  
  public static boolean is24HoursClock()
  {
    return nativeIs24HoursClock();
  }
  
  private static String eH = System.getProperty("com.nokia.mid.dateformat");
  private static String eI = System.getProperty("com.nokia.mid.timeformat");
  
  public static int getDisplayWidth(int paramInt) {
	  return 0;
  }
  
  public static int getDisplayHeight(int paramInt) {
	  return 0;
  }
  
  public static int getTickerPollInterval() {
	  return 0;
  }
  
  public static int getTickerIncrement() {
	  return 0;
  }
  
  private static void updateDeviceColors() {
	  
  }
  
  private static void updateDeviceEvents() {
	  
  }
  
  public static boolean nativeIs24HoursClock() {
	  return true;
  }
}
