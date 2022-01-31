package javax.microedition.pim;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class RepeatRule
{
  public static final int APRIL = 1048576;
  public static final int AUGUST = 16777216;
  public static final int COUNT = 32;
  public static final int DAILY = 16;
  public static final int DAY_IN_MONTH = 1;
  public static final int DAY_IN_WEEK = 2;
  public static final int DAY_IN_YEAR = 4;
  public static final int DECEMBER = 268435456;
  public static final int END = 64;
  public static final int FEBRUARY = 262144;
  public static final int FIFTH = 16;
  public static final int FIFTHLAST = 512;
  public static final int FIRST = 1;
  public static final int FOURTH = 8;
  public static final int FOURTHLAST = 256;
  public static final int FREQUENCY = 0;
  public static final int FRIDAY = 2048;
  public static final int INTERVAL = 128;
  public static final int JANUARY = 131072;
  public static final int JULY = 8388608;
  public static final int JUNE = 4194304;
  public static final int LAST = 32;
  public static final int MARCH = 524288;
  public static final int MAY = 2097152;
  public static final int MONDAY = 32768;
  public static final int MONTH_IN_YEAR = 8;
  public static final int MONTHLY = 18;
  public static final int NOVEMBER = 134217728;
  public static final int OCTOBER = 67108864;
  public static final int SATURDAY = 1024;
  public static final int SECOND = 2;
  public static final int SECONDLAST = 64;
  public static final int SEPTEMBER = 33554432;
  public static final int SUNDAY = 65536;
  public static final int THIRD = 4;
  public static final int THIRDLAST = 128;
  public static final int THURSDAY = 4096;
  public static final int TUESDAY = 16384;
  public static final int WEDNESDAY = 8192;
  public static final int WEEK_IN_MONTH = 16;
  public static final int WEEKLY = 17;
  public static final int YEARLY = 19;
  int k = -1;
  int l = -1;
  long m = -1L;
  int n = -1;
  int o = -1;
  int p = -1;
  
  public void addExceptDate(long paramLong) {}
  
  public Enumeration dates(long paramLong1, long paramLong2, long paramLong3)
  {
    if (paramLong2 > paramLong3) {
      throw new IllegalArgumentException("subsetBeginning greater than subsetEnding");
    }
    return new DateEnumeration(paramLong1, paramLong2, paramLong3);
  }
  
  public final boolean equals(Object paramObject)
  {
      throw new Error();
  }
  
  public long getDate(int paramInt)
  {
    if (paramInt != 64) {
      throw new IllegalArgumentException("Invalid field");
    }
    if (this.m == -1L) {
      throw new FieldEmptyException("No value set for this field", paramInt);
    }
    return this.m;
  }
  
  public Enumeration getExceptDates()
  {
    return new Enumeration()
    {
      public boolean hasMoreElements()
      {
        return false;
      }
      
      public Object nextElement()
      {
        throw new NoSuchElementException();
      }
    };
  }
  
  public int[] getFields()
  {
    int[] arrayOfInt = new int[(this.k == -1 ? 0 : 1) + (this.l == -1 ? 0 : 1) + (this.n == -1 ? 0 : 1) + (this.o == -1 ? 0 : 1) + (this.p == -1 ? 0 : 1) + (this.m == -1L ? 0 : 1)];
    
    int i = 0;
    if (this.k != -1)
    {
      arrayOfInt[0] = 0;
      i++;
    }
    if (this.l != -1)
    {
      arrayOfInt[i] = 128;
      i++;
    }
    if (this.n != -1)
    {
      arrayOfInt[i] = 2;
      i++;
    }
    if (this.o != -1)
    {
      arrayOfInt[i] = 8;
      i++;
    }
    if (this.p != -1)
    {
      arrayOfInt[i] = 1;
      i++;
    }
    if (this.m != -1L) {
      arrayOfInt[i] = 64;
    }
    return arrayOfInt;
  }
  
  public int getInt(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      if (this.k == -1) {
        throw new FieldEmptyException("No value set for this field", paramInt);
      }
      return this.k;
    case 128: 
      if (this.l == -1) {
        throw new FieldEmptyException("No value set for this field", paramInt);
      }
      return this.l;
    case 2: 
      if (this.n == -1) {
        throw new FieldEmptyException("No value set for this field", paramInt);
      }
      return this.n;
    case 8: 
      if (this.o == -1) {
        throw new FieldEmptyException("No value set for this field", paramInt);
      }
      return this.o;
    case 1: 
      if (this.p == -1) {
        throw new FieldEmptyException("No value set for this field", paramInt);
      }
      return this.p;
    }
    throw new IllegalArgumentException("Field not supported");
  }
  
  public void removeExceptDate(long paramLong) {}
  
  public void setDate(int paramInt, long paramLong)
  {
    if (paramInt != 64) {
      throw new IllegalArgumentException("Invalid field");
    }
    this.m = paramLong;
  }
  
  public void setInt(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    case 0: 
      switch (paramInt2)
      {
      case 16: 
      case 18: 
      case 19: 
        if (this.l != -1) {
          throw new IllegalArgumentException("An interval is set but not supported with this frequency");
        }
        this.k = paramInt2;
        return;
      case 17: 
        this.k = paramInt2;
        return;
      }
      throw new IllegalArgumentException("Invalid value");
    case 128: 
      switch (this.k)
      {
      case -1: 
      case 17: 
        if ((paramInt2 == 1) || (paramInt2 == 2)) {
          this.l = paramInt2;
        } else {
          throw new IllegalArgumentException("Invalid value");
        }
        break;
      case 16: 
      case 18: 
      case 19: 
        throw new IllegalArgumentException("INTERVAL is only supported for WEEKLY repeat rules.");
      }
      break;
    case 2: 
      if (((paramInt2 & 0x10000) != 65536) && ((paramInt2 & 0x8000) != 32768) && ((paramInt2 & 0x4000) != 16384) && ((paramInt2 & 0x2000) != 8192) && ((paramInt2 & 0x1000) != 4096) && ((paramInt2 & 0x800) != 2048) && ((paramInt2 & 0x400) != 1024)) {
        throw new IllegalArgumentException("DAY_IN_WEEK's value must be based on the RepeatRule constants (e.g., RepeatRule.SUNDAY)");
      }
      this.n = paramInt2;
      return;
    case 8: 
      if ((paramInt2 != 131072) && (paramInt2 != 262144) && (paramInt2 != 524288) && (paramInt2 != 1048576) && (paramInt2 != 2097152) && (paramInt2 != 4194304) && (paramInt2 != 8388608) && (paramInt2 != 16777216) && (paramInt2 != 33554432) && (paramInt2 != 67108864) && (paramInt2 != 134217728) && (paramInt2 != 268435456)) {
        throw new IllegalArgumentException("MONTH_IN_YEAR must be a constant from RepeatRule (name of month)");
      }
      this.o = paramInt2;
      return;
    case 1: 
      if ((paramInt2 < 1) || (paramInt2 > 31)) {
        throw new IllegalArgumentException("Incorrect number of days for DAY_IN_MONTH");
      }
      this.p = paramInt2;
      return;
    default: 
      throw new IllegalArgumentException("Field not supported");
    }
  }
  
  class DateEnumeration
    implements Enumeration
  {
    private Date a = null;
    private long b;
    private Calendar calendar;
    private boolean c = true;
    private int d = 0;
    private boolean e = false;
    
    DateEnumeration(long paramLong1, long paramLong2, long paramLong3)
    {
      if (((RepeatRule.this.m != -1L) && (paramLong1 >= RepeatRule.this.m)) || (RepeatRule.this.k == -1)) {
        return;
      }
      this.calendar = Calendar.getInstance();
      
      this.b = ((RepeatRule.this.m != -1L) && (RepeatRule.this.m < paramLong3) ? RepeatRule.this.m : paramLong3);
      this.a = new Date(paramLong1);
      if (paramLong1 >= paramLong2)
      {
        this.c = false;
        this.a = a(this.a);
        this.c = true;
      }
      while ((this.a != null) && (this.a.getTime() < paramLong2)) {
        this.a = a(this.a);
      }
    }
    
    public boolean hasMoreElements()
    {
      return this.a != null;
    }
    
    public Object nextElement()
    {
      if (this.a == null) {
        throw new NoSuchElementException();
      }
      Date localDate = new Date(this.a.getTime());
      
      this.a = a(this.a);
      return localDate;
    }
    
    private Date a(Date paramDate)
    {
        throw new Error();
    }
    
    private Date a(Date paramDate, int paramInt)
    {
        throw new Error();
    }
    
    private int getTomorrowRR(int paramInt)
    {
      throw new Error();
    }
    
    private int a(int paramInt1, int paramInt2)
    {
        throw new Error();
    }
    
    private int[] a(int[] paramArrayOfInt, int paramInt)
    {
      paramArrayOfInt[0] += paramInt;
      while ((paramInt = a(paramArrayOfInt[1], paramArrayOfInt[2])) < paramArrayOfInt[0])
      {
        paramArrayOfInt[0] -= paramInt;
        paramArrayOfInt[1] += 1;
        if (paramArrayOfInt[1] > 11)
        {
          paramArrayOfInt[1] = 0;
          paramArrayOfInt[2] += 1;
        }
      }
      return paramArrayOfInt;
    }
    
    private void setCalendar(int[] paramArrayOfInt)
    {
      this.calendar.set(5, 1);
      this.calendar.set(2, paramArrayOfInt[1]);
      this.calendar.set(1, paramArrayOfInt[2]);
      this.calendar.set(5, paramArrayOfInt[0]);
    }
  }
}
