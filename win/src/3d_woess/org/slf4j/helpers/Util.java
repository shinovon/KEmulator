package org.slf4j.helpers;

import java.io.PrintStream;

public class Util
{
  public static final void report(String msg, Throwable t)
  {
    System.err.println(msg);
    System.err.println("Reported exception:");
    t.printStackTrace();
  }
  
  public static final void report(String msg)
  {
  }
}
