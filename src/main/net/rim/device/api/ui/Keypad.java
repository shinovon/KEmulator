package net.rim.device.api.ui;

import emulator.Keyboard;

public class Keypad
{
  public static final int KEY_SEND = 0;
  public static final int KEY_ESCAPE = 1;
  
  public static int key(int paramInt)
  {
    if (Keyboard.isLeftSoft(paramInt)) {
      return 0;
    }
    if (Keyboard.isRightSoft(paramInt)) {
      return 1;
    }
    return paramInt;
  }
}
