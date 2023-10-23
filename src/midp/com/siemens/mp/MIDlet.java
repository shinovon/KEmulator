package com.siemens.mp;

import javax.microedition.io.ConnectionNotFoundException;

public class MIDlet
{
  private static String[] supportedProtocols;
  
  public static void notifyDestroyed() {}
  
  public static void notifyPaused() {}
  
  public static String getAppProperty(String s)
  {
    return null;
  }
  
  public static final boolean platformRequest(String s)
    throws ConnectionNotFoundException, NotAllowedException
  {
    return false;
  }
  
  public static String[] getSupportedProtocols()
  {
    return supportedProtocols;
  }
}
