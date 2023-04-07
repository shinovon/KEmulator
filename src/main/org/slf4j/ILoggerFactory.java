package org.slf4j;

public abstract interface ILoggerFactory
{
  public abstract Logger getLogger(String paramString);
}
