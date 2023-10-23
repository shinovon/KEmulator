package com.nec.graphics;

import javax.microedition.lcdui.Image;

public final class ImageMap
{
  public ImageMap() {}
  
  public ImageMap(int cw, int ch)
    throws IllegalArgumentException
  {}
  
  public ImageMap(int cw, int ch, int width, int height, byte[] data, Image[] images)
    throws NullPointerException, IllegalArgumentException
  {}
  
  public void setImageMap(int i, int j, byte[] abyte0, Image[] aimage)
    throws NullPointerException, IllegalArgumentException
  {}
  
  public void setWindow(int i, int j, int k, int l)
    throws IllegalArgumentException
  {}
  
  public void moveWindow(int i, int j)
    throws IllegalArgumentException
  {}
  
  public void setTransparent(int i, int j)
    throws IllegalArgumentException
  {}
  
  public void setTransparent(int i)
    throws ArrayIndexOutOfBoundsException
  {}
  
  public void scrollWindow(int i, int j)
    throws IllegalArgumentException
  {}
}
