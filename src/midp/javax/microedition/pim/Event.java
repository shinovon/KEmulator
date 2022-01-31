package javax.microedition.pim;

public abstract interface Event
  extends PIMItem
{
  public static final int ALARM = 100;
  public static final int CLASS = 101;
  public static final int CLASS_CONFIDENTIAL = 200;
  public static final int CLASS_PRIVATE = 201;
  public static final int CLASS_PUBLIC = 202;
  public static final int END = 102;
  public static final int LOCATION = 103;
  public static final int NOTE = 104;
  public static final int REVISION = 105;
  public static final int START = 106;
  public static final int SUMMARY = 107;
  public static final int UID = 108;
  
  public abstract RepeatRule getRepeat();
  
  public abstract void setRepeat(RepeatRule paramRepeatRule);
}
