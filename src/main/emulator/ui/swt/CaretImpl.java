package emulator.ui.swt;

import emulator.Emulator;
import emulator.ui.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Canvas;

import javax.microedition.lcdui.*;

import emulator.lcdui.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Transform;

public final class CaretImpl implements ICaret {
    private static final Font font;
    private Canvas swtCanvas;
    private Caret swtCaret;
    private float zoom;
    private int caretLocX;
    private int caretLocY;
    private int caretX;
    private int caretY;
    private TextField lcduiTextField;
    private int caretPosition;
    private int caretCol;
    private int caretRow;

    public CaretImpl(final Canvas aCanvas838) {
        super();
        this.swtCanvas = aCanvas838;
        (this.swtCaret = new Caret(this.swtCanvas, 0)).setVisible(false);
        this.zoom = 1.0f;
    }

    public final int getCaretPosition() {
        return this.caretPosition;
    }

    public final void a(Transform paramTransform, int paramInt) {
    }

    public final void setCaretLocation(final int x, final int y) {
        this.caretLocX = x;
        this.caretLocY = y;
        int[] i = ((EmulatorScreen) Emulator.getEmulator().getScreen()).transformPointer(x, y);
        swtCaret.setLocation(i[0], i[1]);
    }

    public final void setWindowZoom(final float aFloat840) {
        this.zoom = aFloat840;
        if (this.swtCaret.isVisible()) {
            this.swtCaret.setSize((int) aFloat840, (int) (CaretImpl.font.getBaselinePosition() * aFloat840));
            this.setCaretLocation(this.caretLocX, this.caretLocY);
        }
    }

    public final void focusItem(final TextField aTextField842, final int anInt844, final int anInt845) {
        this.lcduiTextField = aTextField842;
        this.caretX = anInt844;
        this.caretY = anInt845;
        this.caretPosition = 0;
        this.caretCol = 0;
        this.caretRow = 0;
        EmulatorImpl.syncExec(new Class121(this));
    }

    public final void defocusItem(final TextField textField) {
        this.lcduiTextField = null;
        EmulatorImpl.syncExec(new Class115(this));
    }

    public final void mouseDown(int x, int y) {
        if (this.lcduiTextField != null) {
            int textHeight = font.getHeight() + 4;
            int line = (y - this.caretY) / textHeight;
            int w = this.lcduiTextField.getPreferredWidth() - 8;
            String[] arr;
            if ((arr = c.textArr(this.lcduiTextField.getString() == null ? "" : this.lcduiTextField.getString(), font, w, w)) != null
                    && line >= 0 && line < arr.length) {
                int lineLength = arr[line].length();
                int var9 = 0;
                int var10 = 0;
                this.caretCol = 0;

                int var11;
                for (var11 = x - this.caretX; this.caretCol <= lineLength; ++this.caretCol) {
                    var10 = var9;
                    if ((var9 = font.substringWidth(arr[line], 0, this.caretCol)) >= var11) {
                        break;
                    }
                }

                int var12 = (var9 - var10) / 2;
                int var10002;
                if (var11 >= var10 + var12) {
                    var10002 = var9;
                } else {
                    --this.caretCol;
                    var10002 = var10;
                }

                this.setCaretLocation(caretX + var10002, this.caretY + line * textHeight);
                this.caretRow = line;
                this.caretCol = Math.min(Math.max(0, this.caretCol), lineLength);
                int var10001 = this.caretCol;

                while (true) {
                    this.caretPosition = var10001;
                    if (line <= 0) {
                        return;
                    }

                    --line;
                    var10001 = this.caretPosition + arr[line].length();
                }
            }
        }
    }

    public final void keyPressed(KeyEvent var1) {
        if (this.lcduiTextField != null) {
            int var2 = this.lcduiTextField.getPreferredWidth() - 8;
            String var3;
            String[] var4;
            if ((var4 = c.textArr(var3 = this.lcduiTextField.getString() == null ? "" : this.lcduiTextField.getString(), font, var2, var2)) != null && this.caretRow >= 0 && this.caretRow < var4.length) {
                int var5 = font.getHeight() + 4;
                String var6;
                int var7 = (var6 = var4[this.caretRow]).length();
                if (var1.character == 0) {
                    label131:
                    {
                        CaretImpl var10000;
                        int var10001;
                        label118:
                        {
                            switch (var1.keyCode) {
                                case 16777219:
                                    var10000 = this;
                                    var10001 = this.caretCol - 1;
                                    break;
                                case 16777220:
                                    var10000 = this;
                                    var10001 = this.caretCol + 1;
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

                            var10000.caretCol = var10001;
                        }

                        label112:
                        {
                            if (this.caretCol < 0) {
                                if (this.caretRow <= 0) {
                                    var10000 = this;
                                    var10001 = 0;
                                    break label112;
                                }

                                --this.caretRow;
                                var7 = (var6 = var4[this.caretRow]).length();
                            } else {
                                if (this.caretCol <= var7) {
                                    break label131;
                                }

                                if (this.caretRow < var4.length - 1) {
                                    ++this.caretRow;
                                    var7 = (var6 = var4[this.caretRow]).length();
                                    var10000 = this;
                                    var10001 = 0;
                                    break label112;
                                }
                            }

                            var10000 = this;
                            var10001 = var7;
                        }

                        var10000.caretCol = var10001;
                    }
                }

                String var8 = var6.substring(0, this.caretCol);
                var6.substring(this.caretCol, var7);
                String var9 = null;
                switch (var1.character) {
                    case '\b':
                        if (this.caretCol == 0) {
                            if (this.caretRow > 0) {
                                --this.caretRow;
                                var7 = (var6 = var4[this.caretRow]).length();
                                this.caretCol = var7;
                                var8 = var6.substring(0, this.caretCol);
                                var6.substring(this.caretCol, var7);
                            }
                        } else {
                            var8 = var6.substring(0, --this.caretCol);
                            if (this.caretCol == 0 && this.caretRow > 0) {
                                --this.caretRow;
                                var7 = (var6 = var4[this.caretRow]).length();
                                this.caretCol = var7;
                                var8 = var6.substring(0, this.caretCol);
                                var6.substring(this.caretCol, var7);
                            }

                            var3 = var3.substring(0, this.caretPosition - 1) + var3.substring(this.caretPosition);
                        }
                    case '\t':
                    case '\n':
                    case '\r':
                        break;
                    case '\u007f':
                        if (this.caretCol == var7) {
                            if (this.caretRow < var4.length - 1) {
                                ++this.caretRow;
                                var7 = (var6 = var4[this.caretRow]).length();
                                this.caretCol = 0;
                                var8 = var6.substring(0, this.caretCol);
                                var6.substring(this.caretCol, var7);
                            }
                        } else {
                            if (var6.substring(this.caretCol + 1, var7).length() == 0 && this.caretCol == 0 && this.caretRow > 0) {
                                --this.caretRow;
                                var7 = (var6 = var4[this.caretRow]).length();
                                this.caretCol = var7;
                                var8 = var6.substring(0, this.caretCol);
                                var6.substring(this.caretCol, var7);
                            }

                            var3 = var3.substring(0, this.caretPosition) + var3.substring(this.caretPosition + 1);
                        }
                        break;
                    default:
                        if (var1.character >= 32 && var3.length() < this.lcduiTextField.getMaxSize()) {
                            try {
                                var3 = var3.substring(0, this.caretPosition) + var1.character + var3.substring(this.caretPosition);
                                if (var1.character != 32 || var3.charAt(this.caretPosition + 1) == 32) {
                                    if (this.caretCol == var7 && this.caretRow < var4.length - 1) {
                                        ++this.caretRow;
                                        var7 = (var6 = var4[this.caretRow]).length();
                                        this.caretCol = 0;
                                        var8 = var6.substring(0, this.caretCol);
                                        var6.substring(this.caretCol, var7);
                                    }

                                    var8 = var8 + var1.character;
                                    ++this.caretCol;
                                    if (font.stringWidth(var8) > var2) {
                                        var9 = "";
                                        var8 = var9 + var1.character;
                                        this.caretCol = 1;
                                        ++this.caretRow;
                                    }
                                }
                            } catch (Exception ignored) {}
                        }
                }

                int var10 = font.stringWidth(var8);
                this.setCaretLocation(this.caretX + var10, this.caretY + this.caretRow * var5);
                if (var1.character != 0) {
                    this.lcduiTextField.setString(var3);
                    this.lcduiTextField.notifyStateChanged();
                }

                this.caretPosition = this.caretCol;

                for (int var11 = 0; var11 < this.caretRow; ++var11) {
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
