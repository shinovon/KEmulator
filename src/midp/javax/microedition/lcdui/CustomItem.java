package javax.microedition.lcdui;

import emulator.*;
import emulator.lcdui.*;

public abstract class CustomItem extends Item
{
    protected static final int TRAVERSE_HORIZONTAL = 1;
    protected static final int TRAVERSE_VERTICAL = 2;
    protected static final int KEY_PRESS = 4;
    protected static final int KEY_RELEASE = 8;
    protected static final int KEY_REPEAT = 16;
    protected static final int POINTER_PRESS = 32;
    protected static final int POINTER_RELEASE = 64;
    protected static final int POINTER_DRAG = 128;
    protected static final int NONE = 0;
    private Image anImage427;
    private Graphics aGraphics428;
    int[] anIntArray429;
    
    protected CustomItem(final String s) {
        super(s);
        this.anIntArray429 = new int[4];
        this.anImage427 = Image.createImage(Emulator.getEmulator().getScreen().getWidth(), Emulator.getEmulator().getScreen().getHeight());
        this.aGraphics428 = this.anImage427.getGraphics();
    }
    
    public int getGameAction(final int n) {
        int n2 = 0;
        int n3 = 0;
        switch (n) {
            case 49: {
                n3 = 9;
                break;
            }
            case 51: {
                n3 = 10;
                break;
            }
            case 55: {
                n3 = 11;
                break;
            }
            case 57: {
                n3 = 12;
                break;
            }
            default: {
                if (n == Keyboard.method595(1)) {
                    n3 = 1;
                    break;
                }
                if (n == Keyboard.method595(6)) {
                    n3 = 6;
                    break;
                }
                if (n == Keyboard.method595(2)) {
                    n3 = 2;
                    break;
                }
                if (n == Keyboard.method595(5)) {
                    n3 = 5;
                    break;
                }
                if (n == Keyboard.method595(8)) {
                    n3 = 8;
                    break;
                }
                return n2;
            }
        }
        n2 = n3;
        return n2;
    }
    
    protected final int getInteractionModes() {
        return 255;
    }
    
    protected abstract int getMinContentWidth();
    
    protected abstract int getMinContentHeight();
    
    protected abstract int getPrefContentWidth(final int p0);
    
    protected abstract int getPrefContentHeight(final int p0);
    
    protected void sizeChanged(final int n, final int n2) {
    }
    
    protected final void invalidate() {
    }
    
    protected abstract void paint(final Graphics p0, final int p1, final int p2);
    
    protected final void repaint() {
    }
    
    protected final void repaint(final int n, final int n2, final int n3, final int n4) {
    }
    
    protected boolean traverse(final int n, final int n2, final int n3, final int[] array) {
        return false;
    }
    
    protected void traverseOut() {
    }
    
    protected void keyPressed(final int n) {
    }
    
    protected void keyReleased(final int n) {
    }
    
    protected void keyRepeated(final int n) {
    }
    
    protected void pointerPressed(final int n, final int n2) {
    }
    
    protected void pointerReleased(final int n, final int n2) {
    }
    
    protected void pointerDragged(final int n, final int n2) {
    }
    
    protected void showNotify() {
    }
    
    protected void hideNotify() {
    }
    
    protected void paint(final Graphics graphics) {
        this.aGraphics428.setColor(-1);
        this.aGraphics428.fillRect(0, 0, super.aScreen176.w, super.aScreen176.h);
        this.aGraphics428.setColor(0);
        final int n = super.anIntArray21[0] + 2;
        int n2 = super.anIntArray21[1] + 2;
        final int prefContentWidth = this.getPrefContentWidth(super.anIntArray21[2]);
        final int prefContentHeight = this.getPrefContentHeight(super.anIntArray21[3]);
        this.paint(this.aGraphics428, prefContentWidth, prefContentHeight);
        super.paint(graphics);
        if (super.aStringArray175 != null && super.aStringArray175.length > 0) {
            graphics.setFont(Item.aFont173);
            for (int i = 0; i < super.aStringArray175.length; ++i) {
                graphics.drawString(super.aStringArray175[i], super.anIntArray21[0] + 4, n2 + 2, 0);
                n2 += Item.aFont173.getHeight() + 4;
            }
        }
        graphics.setClip(n, n2, prefContentWidth, prefContentHeight);
        graphics.drawImage(this.anImage427, n, n2, 0);
        graphics.setClip(0, 0, super.aScreen176.w, super.aScreen176.h);
    }
    
    protected void layout() {
        super.layout();
        int n = 0;
        final int n2 = this.getPreferredWidth() - 8;
        if (super.aString172 != null) {
            super.aStringArray175 = c.method175(super.aString172, Item.aFont173, n2, n2);
            n = 0 + (Item.aFont173.getHeight() + 4) * super.aStringArray175.length;
        }
        else {
            super.aStringArray175 = null;
        }
        super.anIntArray21[3] = Math.min(n + (this.getPrefContentHeight(super.anIntArray21[3]) + 4), super.aScreen176.anIntArray21[3]);
    }
    
    protected boolean callTraverse(final int n) {
        return this.traverse(this.getGameAction(n), super.aScreen176.w, super.aScreen176.h, this.anIntArray429);
    }
}
