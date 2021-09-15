package emulator.ui.swt;

import java.io.*;
import java.nio.charset.*;
import java.util.regex.*;
import java.util.*;

final class Class9 implements Comparator
{
    private final int a;
    private final Class110 aC;
    
    Class9(final Class110 aClass110_566, final int anInt565) {
        super();
        this.aC = aClass110_566;
        this.a = anInt565;
    }
    
    public final int compare(final Object var1, final Object var2) {
    	/*
        int n = 0;
        while (true) {
            int compareTo = 0;
            Label_0091: {
                int n2 = 0;
                int n3 = 0;
                switch (this.anInt565) {
                    case 0: {
                        compareTo = ((String)o).compareTo((String) o2);
                        break Label_0091;
                    }
                    case 1: {
                        n2 = Class110.method629(this.aClass110_566).method866(o);
                        n3 = Class110.method629(this.aClass110_566).method866(o2);
                        break;
                    }
                    case 2: {
                        n2 = Class110.method629(this.aClass110_566).method867(o);
                        n3 = Class110.method629(this.aClass110_566).method867(o2);
                        break;
                    }
                    default: {
                        if (Class110.method664(this.aClass110_566).getSortDirection() == 128) {
                            return n;
                        }
                        return -n;
                    }
                }
                compareTo = n2 - n3;
            }
            n = compareTo;
            continue;
        }*//*
    	int i = 0;
        switch (this.anInt565)
        {
        case 0: 
          break;
        case 1: 
          break;
        case 2: 
          i = aClass110_566.a(this.jdField_a_of_type_EmulatorUiSwtBQ).f(paramObject1) - aClass110_566.method664(aClass110_566).f(paramObject2);
        }
        if (Class110.method664(this.aClass110_566).getSortDirection() == 128) {
          return i;
        }
        return -i;*/
/*
        int n = 0;
        int compareTo = 0;
    	 int n2 = 0;
         int n3 = 0;
         switch (this.a) {
             case 0: {
                 compareTo = ((String)o).compareTo((String) o2);
             }
             case 1: {
                 n2 = Class110.method629(this.aClass110_566).method866(o);
                 n3 = Class110.method629(this.aClass110_566).method866(o2);
                 break;
             }
             case 2: {
                 n2 = Class110.method629(this.aClass110_566).method867(o);
                 n3 = Class110.method629(this.aClass110_566).method867(o2);
                 break;
             }
             default: {
                 if (Class110.method664(this.aClass110_566).getSortDirection() == 128) {
                     return n;
                 }
                 return -n;
             }
         }
         compareTo = n2 - n3;
         return compareTo;*/
    	int var10000;
        int var3;
        label20: {
           var3 = 0;
           int var10001;
           switch(this.a) {
           case 0:
              var10000 = ((String)var1).compareTo((String) var2);
              break label20;
           case 1:
              var10000 = Class110.method629(this.aC).method866(var1);
              var10001 = Class110.method629(this.aC).method866(var2);
              break;
           case 2:
              var10000 = Class110.method629(this.aC).method867(var1);
              var10001 = Class110.method629(this.aC).method867(var2);
              break;
           default:
              return Class110.method664(this.aC).getSortDirection() == 128?var3:-var3;
           }

           var10000 -= var10001;
        }

        var3 = var10000;
        return Class110.method664(this.aC).getSortDirection() == 128?var3:-var3;

    }
}
