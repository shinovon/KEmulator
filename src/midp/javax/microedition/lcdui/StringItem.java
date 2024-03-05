package javax.microedition.lcdui;

import emulator.graphics2D.IFont;
import emulator.lcdui.*;

public class StringItem extends Item {
    private String text;
    private int mode;
    private Font font;
    private String[] textArr;
    private int maxTextWidth;

    public StringItem(final String label, final String text) {
        this(label, text, 0);
    }

    public StringItem(final String s, final String text, final int mode) {
        super(s);
        if (mode < 0 || mode > 2) {
            throw new IllegalArgumentException();
        }
        this.text = text;
        this.mode = mode;
        this.font = null;
    }

    public String getText() {
        return this.text;
    }

    public void setText(final String aString25) {
        this.text = aString25;
    }

    public int getAppearanceMode() {
        return this.mode;
    }

    public void setFont(final Font aFont358) {
        this.font = aFont358;
    }

    public Font getFont() {
        return this.font;
    }

    public void setPreferredSize(final int n, final int n2) {
        super.setPreferredSize(n, n2);
    }

    protected void paint(final Graphics g) {
        super.paint(g);
        int by = super.bounds[Y];
        int bx = super.bounds[X];
        int bw = super.bounds[W];

        final Font font = (this.font != null) ? this.font : Screen.font;
        g.setFont(font);

        if (mode == BUTTON) {
            //кнопка
            String str = null;
            // определение строки
            if (textArr != null) {
                str = textArr[0];
            }
            if (str == null) {
                str = text;
            }
            if (str == null) {
                str = "...";
            }
            // определение размера строки
            int j = 0;
            if (font != null && str != null)
                j = font.stringWidth(str);

            if ((isLayoutExpand() && isLayoutAlignDefault()) || isLayoutCenter()) {
                bx = (bw - j) / 2 - 2;
                g.drawString(str, bx + 4, by + 1, 0);
            } else if (isLayoutLeft() || isLayoutDefault()) {
                g.drawString(str, bx + 4, by + 1, 0);
            } else if (isLayoutRight()) {
                //bx = (bw - j) - 8;
                g.drawString(str, bx + 4, by + 1, 0);
            }
            if (j > 0) {
                if (isLayoutExpand()) {
                    j = bounds[W] - 8;
                    bx = super.bounds[X];
                }
                int h = font.getHeight();
                //очертания кнопки
                int o = g.getColor();
                g.setColor(0xababab);
                bx = bx + 2;
                int lx = bx + 2 + j + 1;
                int ly = by + h + 3;
                g.drawLine(bx + 1, ly, lx, ly);
                g.drawLine(lx, ly, lx, by + 1);
                g.setColor(o);
                g.drawRect(bx, by, j + 4, h + 4);
            }
        } else {
            for (int i = this.getcurPage(); i < this.textArr.length; ++i) {
                int x = super.bounds[X] + 4;
                String s = textArr[i];
                int w = bounds[W];
                int tw = font.stringWidth(s);
                if (isLayoutCenter()) x = ((w - x) - tw) / 2 + x;
                else if (isLayoutRight()) x = ((w - x) - tw) + x;
                g.drawString(s, x, by + 2, 0);
                if ((by += font.getHeight() + 4) > super.screen.bounds[H]) {
                    return;
                }
            }
        }
    }

    protected void layout() {
        super.layout();
        final Font font = (this.font != null) ? this.font : Screen.font;
        final int n = this.getPreferredWidth() - 8;
        int[] maxw = new int[1];
        this.textArr = c.textArr((super.label != null) ? (super.label + " " + this.text) : this.text, font, n, n, maxw);
        if (textArr.length == 1) maxTextWidth = maxw[0] + 4;
        else if (textArr.length == 0) maxTextWidth = 4;
        else {
            maxTextWidth = n + 4;
        }
        if (mode == BUTTON && isLayoutRight()) {
            int j = 0;
            if (font != null && textArr != null && textArr.length > 0)
                j = font.stringWidth(textArr[0]);
            bounds[X] = (bounds[W] - j) - 8;
        }
        final int n3;
        int n2 = (n3 = font.getHeight() + 4) * this.textArr.length;
        super.anIntArray179 = null;
        final int n4 = super.screen.bounds[H] / n3 * n3;
        if (n2 > super.screen.bounds[H] && n4 > 0) {
            int n5 = n2 / n4;
            if (n2 % n4 != 0) {
                ++n5;
            }
            super.anIntArray179 = new int[n5];
            for (int i = 0; i < n5; ++i) {
                super.anIntArray179[i] = i * n4 / n3;
            }
            if (super.currentPos >= super.anIntArray179.length) {
                super.currentPos = 0;
            }
            n2 = (this.textArr.length - super.anIntArray179[super.currentPos]) * n3;
        }
        super.bounds[H] = Math.min(n2, super.screen.bounds[H]);
    }

    public int getItemWidth() {
        return isLayoutExpand() ? this.getPreferredWidth() - 1 : maxTextWidth;
    }

    public boolean allowNextItemPlaceSameRow() {
        return !isLayoutExpand();
    }
}
