package javax.microedition.lcdui;

import java.util.*;

import javax.microedition.media.CapturePlayerImpl;

import emulator.*;
import emulator.lcdui.*;

public abstract class Screen extends Displayable
{
    static final Font aFont173;
    static final int anInt180;
    static final int anInt181;
    Vector aVector443;
    int anInt182;
    int anInt349;
    private long aLong23;
    
    Screen() {
        this("");
    }
    
    Screen(final String s) {
        super();
        super.aString25 = ((s == null) ? "" : s);
        this.aVector443 = new Vector();
        this.anInt182 = 0;
        this.anInt349 = -1;
    }
    
    public void invokeKeyPressed(final int n) {
        final long currentTimeMillis;
        if ((currentTimeMillis = System.currentTimeMillis()) - this.aLong23 < 100L) {
            return;
        }
        this.aLong23 = currentTimeMillis;
        if (super.aBoolean18) {
            if (n >= 49 && n <= 57) {
                final int n2;
                if ((n2 = n - 49 + 1) < super.aVector26.size()) {
                    super.aCommandListener19.commandAction((Command)super.aVector26.get(n2), this);
                    super.aBoolean18 = false;
                }
            }
            else if (n == Keyboard.method595(1)) {
                if (super.anInt28 > 0) {
                    --super.anInt28;
                }
            }
            else if (n == Keyboard.method595(6)) {
                if (super.anInt28 < super.aVector26.size() - 2) {
                    ++super.anInt28;
                }
            }
            else {
                final int n3;
                if (n == Keyboard.method595(8) && (n3 = super.anInt28 + 1) < super.aVector26.size()) {
                    super.aCommandListener19.commandAction((Command)super.aVector26.get(n3), this);
                    super.aBoolean18 = false;
                }
            }
            this.refreshSoftMenu();
            return;
        }
        if (super.anItem20 != null && super.anItem20 instanceof CustomItem && ((CustomItem)super.anItem20).callTraverse(n)) {
            return;
        }
        if (super.anItem20 != null && n == Keyboard.method595(8)) {
            super.anItem20.itemApplyCommand();
            return;
        }
        if (n == Keyboard.method595(1)) {
            if (this.aVector443.size() > 0) {
                final int index = this.aVector443.indexOf(super.anItem20);
                if (super.anItem20 != null) {
                    if (!super.anItem20.scrollUp()) {
                        return;
                    }
                    super.anItem20.defocus();
                }
                Screen screen;
                Vector vector;
                int n4;
                if (index == -1) {
                    screen = this;
                    vector = this.aVector443;
                    n4 = this.aVector443.size() - 1;
                }
                else if (index > 0) {
                    screen = this;
                    vector = this.aVector443;
                    n4 = index - 1;
                }
                else {
                    screen = this;
                    vector = this.aVector443;
                    n4 = 0;
                }
                screen.anItem20 = (Item) vector.get(n4);
                super.anItem20.focus();
                if (!super.anItem20.aBoolean177) {
                    this.anInt182 = this.aVector443.indexOf(super.anItem20);
                    this.anInt349 = -1;
                }
            }
        }
        else if (n == Keyboard.method595(6) && this.aVector443.size() > 0) {
            final int index2 = this.aVector443.indexOf(super.anItem20);
            if (super.anItem20 != null) {
                if (!super.anItem20.scrollDown()) {
                    return;
                }
                super.anItem20.defocus();
            }
            Screen screen2;
            Vector vector2;
            int n5;
            if (index2 == -1) {
                screen2 = this;
                vector2 = this.aVector443;
                n5 = 0;
            }
            else if (index2 < this.aVector443.size() - 1) {
                screen2 = this;
                vector2 = this.aVector443;
                n5 = index2 + 1;
            }
            else {
                screen2 = this;
                vector2 = this.aVector443;
                n5 = this.aVector443.size() - 1;
            }
            screen2.anItem20 = (Item) vector2.get(n5);
            super.anItem20.focus();
            if (!super.anItem20.aBoolean177) {
                this.anInt182 = -1;
                this.anInt349 = this.aVector443.indexOf(super.anItem20);
            }
        }
    }
    
    public void invokeKeyReleased(final int n) {
    }
    
    public void invokePointerPressed(final int n, final int n2) {
        if (super.aBoolean18) {
            final int n3 = super.w >> 1;
            final int anInt181 = Screen.anInt181;
            final int n5;
            final int n4 = (n5 = super.aVector26.size() - 1) * anInt181;
            final int n6 = n3 - 1;
            final int n7 = super.h - n4 - 1;
            final int[] array;
            if (emulator.lcdui.b.method172(array = new int[] { n6, n7, n3, n4 }, n, n2)) {
                array[0] = n6;
                array[1] = n7;
                array[2] = n3;
                array[3] = anInt181;
                int[] array2;
                int n8;
                for (int i = 0; i < n5; ++i, array2 = array, n8 = 1, array2[n8] += anInt181) {
                    if (emulator.lcdui.b.method172(array, n, n2)) {
                        super.aCommandListener19.commandAction((Command)super.aVector26.get(i + 1), this);
                        super.aBoolean18 = false;
                        return;
                    }
                }
            }
            return;
        }
        if (super.anItem20 != null && super.anItem20 instanceof ChoiceGroup && ((ChoiceGroup)super.anItem20).aBoolean542) {
            super.anItem20.pointerPressed(n, n2);
            return;
        }
        if (this.aVector443.size() > 0) {
            int j = 0;
            while (j < this.aVector443.size()) {
                final Item anItem20;
                if ((anItem20 = (Item) this.aVector443.get(j)).aBoolean177 && emulator.lcdui.b.method172(anItem20.anIntArray21, n, n2)) {
                    if (anItem20 == super.anItem20) {
                        super.anItem20.pointerPressed(n, n2);
                        return;
                    }
                    if (super.anItem20 != null) {
                        super.anItem20.defocus();
                    }
                    (super.anItem20 = anItem20).focus();
                }
                else {
                    ++j;
                }
            }
        }
    }
    
    public void invokePointerReleased(final int n, final int n2) {
    }
    
    public void invokePointerDragged(final int n, final int n2) {
    }
    
    protected abstract void paint(final Graphics p0);
    
    public void invokePaint(final Graphics graphics) {
        Displayable.resetXRayGraphics();
        final int color = graphics.getColor();
        final int strokeStyle = graphics.getStrokeStyle();
        final Font font = graphics.getFont();
        graphics.setFont(Screen.aFont173);
        graphics.setStrokeStyle(0);
        emulator.lcdui.a.method177(graphics, 0, 0, super.w, super.h, false);
        this.drawTitleBar(graphics);
        this.paint(graphics);
        this.drawScrollBar(graphics);
        this.paintTicker(graphics);
        this.paintSoftMenu(graphics);
        graphics.setColor(color);
        graphics.setFont(font);
        graphics.setStrokeStyle(strokeStyle);
    }
    
    protected void drawTitleBar(final Graphics graphics) {
        final int n;
        final String value = String.valueOf(n = ((super.anItem20 != null) ? (this.aVector443.indexOf(super.anItem20) + 1) : this.aVector443.size()));
        final int n2 = (Screen.anInt181 >> 1) - 1;
        final int stringWidth = Screen.aFont173.stringWidth(super.aString25);
        final int stringWidth2 = Screen.aFont173.stringWidth(value);
        final int n3 = (super.w - stringWidth >> 1) + 2;
        final int n4 = super.w - stringWidth2 - 2;
        graphics.setColor(8617456);
        graphics.fillRect(2, n2, (super.w - stringWidth >> 1) - 2, 2);
        graphics.fillRect(n3 + stringWidth + 2, n2, n4 - n3 - stringWidth - 4, 2);
        graphics.setColor(-16777216);
        graphics.setFont(Screen.aFont173);
        graphics.drawString(super.aString25, n3, 1, 0);
        graphics.drawString(value, n4, 1, 0);
    }
    
    protected void drawScrollBar(final Graphics graphics) {
        emulator.lcdui.a.method179(graphics, super.anIntArray21[2] + 1, Screen.anInt181 - 1, 2, super.anIntArray21[3] - 2, this.aVector443.size(), (super.anItem20 != null) ? this.aVector443.indexOf(super.anItem20) : -1);
    }
    
    static {
        aFont173 = Font.getDefaultFont();
        anInt180 = Screen.aFont173.getHeight();
        anInt181 = Screen.anInt180 + 4;
    }
}
