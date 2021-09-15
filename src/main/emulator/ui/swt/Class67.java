package emulator.ui.swt;

import emulator.ui.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Canvas;

import javax.microedition.lcdui.*;
import emulator.lcdui.*;
import org.eclipse.swt.events.*;

public final class Class67 implements ICaret
{
    private static final Font aFont837;
    private Canvas aCanvas838;
    private Caret aCaret839;
    private float aFloat840;
    private int anInt841;
    private int anInt843;
    private int anInt844;
    private int anInt845;
    private TextField aTextField842;
    private int anInt846;
    private int anInt847;
    private int anInt848;
    
    public Class67(final Canvas aCanvas838) {
        super();
        this.aCanvas838 = aCanvas838;
        (this.aCaret839 = new Caret(this.aCanvas838, 0)).setVisible(false);
        this.aFloat840 = 1.0f;
    }
    
    public final int getCaretPosition() {
        return this.anInt846;
    }
    
    public final void method468(final int anInt841, final int anInt842) {
        this.anInt841 = anInt841;
        this.anInt843 = anInt842;
        this.aCaret839.setLocation((int)(anInt841 * this.aFloat840), (int)(anInt842 * this.aFloat840));
    }
    
    public final void method469(final float aFloat840) {
        this.aFloat840 = aFloat840;
        if (this.aCaret839.isVisible()) {
            this.aCaret839.setSize((int)aFloat840, (int)(Class67.aFont837.getBaselinePosition() * aFloat840));
            this.method468(this.anInt841, this.anInt843);
        }
    }
    
    public final void foucsItem(final TextField aTextField842, final int anInt844, final int anInt845) {
        this.aTextField842 = aTextField842;
        this.anInt844 = anInt844;
        this.anInt845 = anInt845;
        this.anInt846 = 0;
        this.anInt847 = 0;
        this.anInt848 = 0;
        Class146.syncExec(new Class121(this));
    }
    
    public final void defoucsItem(final TextField textField) {
        this.aTextField842 = null;
        Class146.syncExec(new Class115(this));
    }

    public final void method474(int var1, int var2) {
       if(this.aTextField842 != null) {
          int var3 = aFont837.getHeight() + 4;
          int var4 = (var2 - this.anInt845) / var3;
          int var5 = this.aTextField842.getPreferredWidth() - 8;
          String[] var7;
          if((var7 = c.method175(this.aTextField842.getString() == null?"":this.aTextField842.getString(), aFont837, var5, var5)) != null && var4 >= 0 && var4 < var7.length) {
             int var8 = var7[var4].length();
             int var9 = 0;
             int var10 = 0;
             this.anInt847 = 0;

             int var11;
             for(var11 = var1 - this.anInt844; this.anInt847 <= var8; ++this.anInt847) {
                var10 = var9;
                if((var9 = aFont837.substringWidth(var7[var4], 0, this.anInt847)) >= var11) {
                   break;
                }
             }

             int var12 = (var9 - var10) / 2;
             Class67 var10000;
             int var10001;
             int var10002;
             if(var11 >= var10 + var12) {
                var10000 = this;
                var10001 = this.anInt844;
                var10002 = var9;
             } else {
                --this.anInt847;
                var10000 = this;
                var10001 = this.anInt844;
                var10002 = var10;
             }

             var10000.method468(var10001 + var10002, this.anInt845 + var4 * var3);
             this.anInt848 = var4;
             this.anInt847 = Math.min(Math.max(0, this.anInt847), var8);
             var10000 = this;
             var10001 = this.anInt847;

             while(true) {
                var10000.anInt846 = var10001;
                if(var4 <= 0) {
                   return;
                }

                var10000 = this;
                --var4;
                var10001 = this.anInt846 + var7[var4].length();
             }
          }
       }
    }

    public final void method470(KeyEvent var1) {
       if(this.aTextField842 != null) {
          int var2 = this.aTextField842.getPreferredWidth() - 8;
          String var3;
          String[] var4;
          if((var4 = c.method175(var3 = this.aTextField842.getString() == null?"":this.aTextField842.getString(), aFont837, var2, var2)) != null && this.anInt848 >= 0 && this.anInt848 < var4.length) {
             int var5 = aFont837.getHeight() + 4;
             String var6;
             int var7 = (var6 = var4[this.anInt848]).length();
             if(var1.character == 0) {
                label131: {
                   Class67 var10000;
                   int var10001;
                   label118: {
                      switch(var1.keyCode) {
                      case 16777219:
                         var10000 = this;
                         var10001 = this.anInt847 - 1;
                         break;
                      case 16777220:
                         var10000 = this;
                         var10001 = this.anInt847 + 1;
                         break;
                      case 16777221:
                      case 16777222:
                      default:
                         break label118;
                      case 16777223:
                         var10000 = this;
                         var10001 = 0;
                         break;
                      case 16777224:
                         var10000 = this;
                         var10001 = var7;
                      }

                      var10000.anInt847 = var10001;
                   }

                   label112: {
                      if(this.anInt847 < 0) {
                         if(this.anInt848 <= 0) {
                            var10000 = this;
                            var10001 = 0;
                            break label112;
                         }

                         --this.anInt848;
                         var7 = (var6 = var4[this.anInt848]).length();
                      } else {
                         if(this.anInt847 <= var7) {
                            break label131;
                         }

                         if(this.anInt848 < var4.length - 1) {
                            ++this.anInt848;
                            var7 = (var6 = var4[this.anInt848]).length();
                            var10000 = this;
                            var10001 = 0;
                            break label112;
                         }
                      }

                      var10000 = this;
                      var10001 = var7;
                   }

                   var10000.anInt847 = var10001;
                }
             }

             String var8 = var6.substring(0, this.anInt847);
             var6.substring(this.anInt847, var7);
             String var9 = null;
             switch(var1.character) {
             case '\b':
                if(this.anInt847 == 0) {
                   if(this.anInt848 > 0) {
                      --this.anInt848;
                      var7 = (var6 = var4[this.anInt848]).length();
                      this.anInt847 = var7;
                      var8 = var6.substring(0, this.anInt847);
                      var6.substring(this.anInt847, var7);
                   }
                } else {
                   var8 = var6.substring(0, --this.anInt847);
                   if(this.anInt847 == 0 && this.anInt848 > 0) {
                      --this.anInt848;
                      var7 = (var6 = var4[this.anInt848]).length();
                      this.anInt847 = var7;
                      var8 = var6.substring(0, this.anInt847);
                      var6.substring(this.anInt847, var7);
                   }

                   var3 = var3.substring(0, this.anInt846 - 1) + var3.substring(this.anInt846, var3.length());
                }
             case '\t':
             case '\n':
             case '\r':
                break;
             case '\u007f':
                if(this.anInt847 == var7) {
                   if(this.anInt848 < var4.length - 1) {
                      ++this.anInt848;
                      var7 = (var6 = var4[this.anInt848]).length();
                      this.anInt847 = 0;
                      var8 = var6.substring(0, this.anInt847);
                      var6.substring(this.anInt847, var7);
                   }
                } else {
                   if(var6.substring(this.anInt847 + 1, var7).length() == 0 && this.anInt847 == 0 && this.anInt848 > 0) {
                      --this.anInt848;
                      var7 = (var6 = var4[this.anInt848]).length();
                      this.anInt847 = var7;
                      var8 = var6.substring(0, this.anInt847);
                      var6.substring(this.anInt847, var7);
                   }

                   var3 = var3.substring(0, this.anInt846) + var3.substring(this.anInt846 + 1, var3.length());
                }
                break;
             default:
                if(var1.character >= 32 && var3.length() < this.aTextField842.getMaxSize()) {
                   try {
                      var3 = var3.substring(0, this.anInt846) + var1.character + var3.substring(this.anInt846, var3.length());
                      if(var1.character != 32 || var3.charAt(this.anInt846 + 1) == 32) {
                         if(this.anInt847 == var7 && this.anInt848 < var4.length - 1) {
                            ++this.anInt848;
                            var7 = (var6 = var4[this.anInt848]).length();
                            this.anInt847 = 0;
                            var8 = var6.substring(0, this.anInt847);
                            var6.substring(this.anInt847, var7);
                         }

                         var8 = var8 + var1.character;
                         ++this.anInt847;
                         if(aFont837.stringWidth(var8) > var2) {
                            var9 = "";
                            var8 = var9 + var1.character;
                            this.anInt847 = 1;
                            ++this.anInt848;
                         }
                      }
                   } catch (Exception var12) {
                      ;
                   }
                }
             }

             int var10 = aFont837.stringWidth(var8);
             this.method468(this.anInt844 + var10, this.anInt845 + this.anInt848 * var5);
             if(var1.character != 0) {
                this.aTextField842.setString(var3);
             }

             this.anInt846 = this.anInt847;

             for(int var11 = 0; var11 < this.anInt848; ++var11) {
                this.anInt846 += var4[var11].length();
             }

          }
       }
    }

    
    static int method471(final Class67 class67) {
        return class67.anInt844;
    }
    
    static int method475(final Class67 class67) {
        return class67.anInt845;
    }
    
    static Caret method472(final Class67 class67) {
        return class67.aCaret839;
    }
    
    static float method473(final Class67 class67) {
        return class67.aFloat840;
    }
    
    static {
        aFont837 = Font.getDefaultFont();
    }
}
