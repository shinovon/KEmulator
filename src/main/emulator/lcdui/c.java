package emulator.lcdui;

import java.util.*;
import javax.microedition.lcdui.*;

public final class c
{
    public c() {
        super();
    }

    private static String[] method174(String var0, char var1) {
       char[] var2 = var0.toCharArray();
       int var3 = 0;
       ArrayList var4 = null;

       for(int var5 = 0; var5 < var2.length; ++var5) {
          if(var2[var5] == var1) {
             if(var4 == null) {
                var4 = new ArrayList();
             }

             var4.add(new String(var2, var3, var5 - var3));
             var3 = var5 + 1;
          }
       }

       if(var4 == null) {
          return new String[]{var0};
       } else {
          if(var3 < var2.length) {
             var4.add(new String(var2, var3, var2.length - var3));
          }

          return (String[])((String[])var4.toArray(new String[var4.size()]));
       }
    }

    public static String[] textArr(String var0, Font var1, int var2, int var3) {
    	if(var0 == null) return new String[0];
       if(var2 > 0 && var3 > 0) {
          boolean var4 = var0.indexOf(10) != -1;
          if(var1.stringWidth(var0) <= var2) {
             return var4?method174(var0, '\n'):new String[]{var0};
          } else {
             ArrayList var6 = new ArrayList();
             if(!var4) {
                method176(var0, var1, var2, var3, var6);
             } else {
                char[] var7 = var0.toCharArray();
                int var8 = 0;

                for(int var9 = 0; var9 < var7.length; ++var9) {
                   if(var7[var9] == 10 || var9 == var7.length - 1) {
                      String var11 = var9 == var7.length - 1?new String(var7, var8, var9 + 1 - var8):new String(var7, var8, var9 - var8);
                      if(var1.stringWidth(var11) <= var2) {
                         var6.add(var11);
                      } else {
                         method176(var11, var1, var2, var3, var6);
                      }

                      var8 = var9 + 1;
                      var2 = var3;
                   }
                }
             }

             return (String[])((String[])var6.toArray(new String[var6.size()]));
          }
       } else {
          return new String[]{var0};
       }
    }

    private static void method176(String var0, Font var1, int var2, int var3, ArrayList var4) {
       char[] var5 = var0.toCharArray();
       int var6 = 0;
       int var7 = 0;
       int var8 = 0;

       while(true) {
          while(var7 < var5.length) {
             if((var8 += var1.charWidth(var5[var7])) > var2) {
                int var9 = var7;

                while(var5[var9] != 32) {
                   --var9;
                   if(var9 < var6) {
                      var9 = var7;
                      break;
                   }
                }

                var4.add(new String(var5, var6, var9 - var6));
                var6 = var5[var9] != 32 && var5[var9] != 9?var9:var9 + 1;
                var8 = 0;
                var7 = var6;
                var2 = var3;
             } else {
                ++var7;
             }
          }

          var4.add(new String(var5, var6, var7 - var6));
          return;
       }
    }

}
