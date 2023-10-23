package com.nec.graphics;

import javax.microedition.lcdui.Canvas;

public abstract class NxCanvas
  extends Canvas
{
  public static final int NX_KEY_UP = -1;
  public static final int NX_KEY_DOWN = -2;
  public static final int NX_KEY_LEFT = -3;
  public static final int NX_KEY_RIGHT = -4;
  public static final int NX_KEY_FIRE = -5;
  
  public int getPressedKeys()
  {
    return 0;
  }
}
