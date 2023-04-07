package org.slf4j;

public abstract interface IMarkerFactory
{
  public abstract Marker getMarker(String paramString);
  
  public abstract boolean exists(String paramString);
  
  public abstract boolean detachMarker(String paramString);
  
  public abstract Marker getDetachedMarker(String paramString);
}
