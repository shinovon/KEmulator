package net.rim.device.api.system;

public abstract interface KeyListener
  extends KeypadListener
{
  public abstract boolean keyChar(char paramChar, int paramInt1, int paramInt2);
  
  public abstract boolean keyDown(int paramInt1, int paramInt2);
  
  public abstract boolean keyUp(int paramInt1, int paramInt2);
  
  public abstract boolean keyRepeat(int paramInt1, int paramInt2);
  
  public abstract boolean keyStatus(int paramInt1, int paramInt2);
}
