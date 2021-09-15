package com.nokia.mid.s40.location;

import com.nokia.mid.impl.policy.PolicyAccess;
import javax.microedition.location.Landmark;
import javax.microedition.location.LandmarkException;
import javax.microedition.location.LandmarkStore;

public final class LocationUtil
{
  static LandmarkAccessorIF laIF = null;
  static LandmarkStoreAccessorIF lsaIF = null;
  static boolean granted = PolicyAccess.isManufacturerSigned0();
  
  public static int getLandmarkID(Landmark object)
    throws SecurityException, LandmarkException
  {
    if (!granted) {
      throw new SecurityException("Permission Denied");
    }
    if (object == null) {
      throw new LandmarkException("Invalid landmark");
    }
    if (laIF != null) {
      return laIF.getLandmarkID(object);
    }
    throw new LandmarkException("No landmark ID available");
  }
  
  public static int getLandmarkStoreID(LandmarkStore object)
    throws SecurityException, LandmarkException
  {
    if (!granted) {
      throw new SecurityException("Permission Denied");
    }
    if (object == null) {
      throw new LandmarkException("Invalid landmark store");
    }
    if (lsaIF != null) {
      return lsaIF.getLandmarkStoreID(object);
    }
    throw new LandmarkException("No landmark store ID available");
  }
  
  public static void setLandmarkAccessorIF(LandmarkAccessorIF object)
    throws SecurityException
  {
    if (granted) {
      laIF = object;
    } else {
      throw new SecurityException("Permission Denied");
    }
  }
  
  public static void setLandmarkStoreAccessorIF(LandmarkStoreAccessorIF object)
    throws SecurityException
  {
    if (granted) {
      lsaIF = object;
    } else {
      throw new SecurityException("Permission Denied");
    }
  }
  
  public static abstract interface LandmarkStoreAccessorIF
  {
    public abstract int getLandmarkStoreID(LandmarkStore paramLandmarkStore);
  }
  
  public static abstract interface LandmarkAccessorIF
  {
    public abstract int getLandmarkID(Landmark paramLandmark)
      throws LandmarkException;
  }
}
