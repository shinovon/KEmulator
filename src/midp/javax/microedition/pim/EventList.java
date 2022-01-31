package javax.microedition.pim;

import java.util.Enumeration;

public abstract interface EventList
  extends PIMList
{
  public static final int ENDING = 1;
  public static final int OCCURRING = 2;
  public static final int STARTING = 0;
  
  public abstract Event createEvent();
  
  public abstract Event importEvent(Event paramEvent);
  
  public abstract void removeEvent(Event paramEvent)
    throws PIMException;
  
  public abstract Enumeration items(int paramInt, long paramLong1, long paramLong2, boolean paramBoolean)
    throws PIMException;
  
  public abstract int[] getSupportedRepeatRuleFields(int paramInt);
}
