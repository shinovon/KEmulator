package emulator.ui.swt;

import emulator.ui.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Canvas;

import javax.microedition.lcdui.*;
import emulator.lcdui.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Transform;

public final class CaretImpl implements ICaret
{
    private static final Font font;
    private Canvas swtCanvas;
    private Caret swtCaret;
    private float zoom;
    private int caretLocX;
    private int caretLocY;
    private int caretX;
    private int caretY;
	private Transform jdField_a_of_type_OrgEclipseSwtGraphicsTransform;
	private int e;
    private TextField lcduiTextField;
    private int caretPosition;
    private int anInt847;
    private int anInt848;
    
    public CaretImpl(final Canvas aCanvas838) {
        super();
        this.swtCanvas = aCanvas838;
        (this.swtCaret = new Caret(this.swtCanvas, 0)).setVisible(false);
        this.zoom = 1.0f;
    }
    
    public final int getCaretPosition() {
        return this.caretPosition;
    }
    
    public final void a(Transform paramTransform, int paramInt)
    {
      this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform = paramTransform;
      this.e = paramInt;
    }
    
    public final void setCaretLocation(final int x, final int y) {
        this.caretLocX = x;
        this.caretLocY = y;
        float[] arrayOfFloat;
        (arrayOfFloat = new float[2])[0] = x;
        arrayOfFloat[1] = y;
        this.jdField_a_of_type_OrgEclipseSwtGraphicsTransform.transform(arrayOfFloat);

        swtCaret.setLocation((int)(arrayOfFloat[0] * this.zoom), (int)(arrayOfFloat[1] * this.zoom));
        //this.swtCaret.setLocation((int)(x * this.zoom), (int)(y * this.zoom));
    }
    
    public final void setWindowZoom(final float aFloat840) {
        this.zoom = aFloat840;
        if (this.swtCaret.isVisible()) {
            this.swtCaret.setSize((int)aFloat840, (int)(CaretImpl.font.getBaselinePosition() * aFloat840));
            this.setCaretLocation(this.caretLocX, this.caretLocY);
        }
    }
    
    public final void foucsItem(final TextField aTextField842, final int anInt844, final int anInt845) {
        this.lcduiTextField = aTextField842;
        this.caretX = anInt844;
        this.caretY = anInt845;
        this.caretPosition = 0;
        this.anInt847 = 0;
        this.anInt848 = 0;
        EmulatorImpl.syncExec(new Class121(this));
    }
    
    public final void defoucsItem(final TextField textField) {
        this.lcduiTextField = null;
        EmulatorImpl.syncExec(new Class115(this));
    }

    public final void method474(int var1, int var2) {
       if(this.lcduiTextField != null) {
          int var3 = font.getHeight() + 4;
          int var4 = (var2 - this.caretY) / var3;
          int var5 = this.lcduiTextField.getPreferredWidth() - 8;
          String[] var7;
          if((var7 = c.textArr(this.lcduiTextField.getString() == null?"":this.lcduiTextField.getString(), font, var5, var5)) != null && var4 >= 0 && var4 < var7.length) {
             int var8 = var7[var4].length();
             int var9 = 0;
             int var10 = 0;
             this.anInt847 = 0;

             int var11;
             for(var11 = var1 - this.caretX; this.anInt847 <= var8; ++this.anInt847) {
                var10 = var9;
                if((var9 = font.substringWidth(var7[var4], 0, this.anInt847)) >= var11) {
                   break;
                }
             }

             int var12 = (var9 - var10) / 2;
             CaretImpl var10000;
             int var10001;
             int var10002;
             if(var11 >= var10 + var12) {
                var10000 = this;
                var10001 = this.caretX;
                var10002 = var9;
             } else {
                --this.anInt847;
                var10000 = this;
                var10001 = this.caretX;
                var10002 = var10;
             }

             var10000.setCaretLocation(var10001 + var10002, this.caretY + var4 * var3);
             this.anInt848 = var4;
             this.anInt847 = Math.min(Math.max(0, this.anInt847), var8);
             var10000 = this;
             var10001 = this.anInt847;

             while(true) {
                var10000.caretPosition = var10001;
                if(var4 <= 0) {
                   return;
                }

                var10000 = this;
                --var4;
                var10001 = this.caretPosition + var7[var4].length();
             }
          }
       }
    }

    public final void method470(KeyEvent var1) {
       if(this.lcduiTextField != null) {
          int var2 = this.lcduiTextField.getPreferredWidth() - 8;
          String var3;
          String[] var4;
          if((var4 = c.textArr(var3 = this.lcduiTextField.getString() == null?"":this.lcduiTextField.getString(), font, var2, var2)) != null && this.anInt848 >= 0 && this.anInt848 < var4.length) {
             int var5 = font.getHeight() + 4;
             String var6;
             int var7 = (var6 = var4[this.anInt848]).length();
             if(var1.character == 0) {
                label131: {
                   CaretImpl var10000;
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

                   var3 = var3.substring(0, this.caretPosition - 1) + var3.substring(this.caretPosition, var3.length());
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

                   var3 = var3.substring(0, this.caretPosition) + var3.substring(this.caretPosition + 1, var3.length());
                }
                break;
             default:
                if(var1.character >= 32 && var3.length() < this.lcduiTextField.getMaxSize()) {
                   try {
                      var3 = var3.substring(0, this.caretPosition) + var1.character + var3.substring(this.caretPosition, var3.length());
                      if(var1.character != 32 || var3.charAt(this.caretPosition + 1) == 32) {
                         if(this.anInt847 == var7 && this.anInt848 < var4.length - 1) {
                            ++this.anInt848;
                            var7 = (var6 = var4[this.anInt848]).length();
                            this.anInt847 = 0;
                            var8 = var6.substring(0, this.anInt847);
                            var6.substring(this.anInt847, var7);
                         }

                         var8 = var8 + var1.character;
                         ++this.anInt847;
                         if(font.stringWidth(var8) > var2) {
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

             int var10 = font.stringWidth(var8);
             this.setCaretLocation(this.caretX + var10, this.caretY + this.anInt848 * var5);
             if(var1.character != 0) {
                this.lcduiTextField.setString(var3);
             }

             this.caretPosition = this.anInt847;

             for(int var11 = 0; var11 < this.anInt848; ++var11) {
                this.caretPosition += var4[var11].length();
             }

          }
       }
    }

    
    static int caretX(final CaretImpl class67) {
        return class67.caretX;
    }
    
    static int caretY(final CaretImpl class67) {
        return class67.caretY;
    }
    
    static Caret caret(final CaretImpl class67) {
        return class67.swtCaret;
    }
    
    static float caretFloat(final CaretImpl class67) {
        return class67.zoom;
    }
    
    static {
        font = Font.getDefaultFont();
    }
}
