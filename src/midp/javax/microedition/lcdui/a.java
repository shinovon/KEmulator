package javax.microedition.lcdui;

import emulator.lcdui.*;

/**
 * ChoiceGroupItem
 */
final class a {
    boolean sel;
    boolean aBoolean424;
    String string;
    Image image;
    Font font;
    int[] bounds;
    ChoiceGroup choice;
    String[] str;

    a(final String aString418, final Image anImage419, final Font aFont420, final ChoiceGroup aChoiceGroup422) {
        super();
        this.string = aString418;
        this.image = anImage419;
        this.font = aFont420;
        this.choice = aChoiceGroup422;
        this.bounds = new int[4];
        this.aBoolean424 = true;
    }

    protected final void method211(final Graphics graphics, final boolean b) {

        graphics.setColor(-16777216);
        final boolean b2 = this.choice.choiceType == 4;
        final boolean b3 = this.choice.aBoolean542 && this.choice.anIntArray179 != null;
        int anInt28 = 0;
        int anInt29 = 0;
        Label_0103:
        {
            if (b2) {
                if (!b3) {
                    anInt28 = this.choice.bounds[1];
                    anInt29 = this.choice.anInt28;
                    break Label_0103;
                }
                anInt28 = this.choice.anInt28;
            } else {
                anInt28 = this.choice.bounds[1];
            }
            anInt29 = this.bounds[1];
        }
        int n = anInt28 + anInt29;
        final int n2 = this.bounds[0] + 4;
        if (b) {
            emulator.lcdui.a.method178(graphics, this.bounds[0] + 1, n - 2, this.bounds[2] - 2, this.bounds[3]);
        }
        if (!b2 || !b3) {
            emulator.lcdui.a.method180(graphics, n2, n + 3, this.sel, this.choice.choiceType);
        }
        final Font font = (this.font != null) ? this.font : Screen.font;
        graphics.setFont(font);
        if (this.str != null)
            for (int i = 0; i < this.str.length; ++i) {
                graphics.drawString(this.str[i], ((i == 0 && this.choice.choiceType != 3 && !b3) ? (n2 + 10) : n2) + 4, n, 0);
                n += font.getHeight() + 4;
            }
    }

    protected final void method212() {
        this.bounds[0] = 0;
        this.bounds[1] = 0;
        this.bounds[2] = this.choice.bounds[2];
        final int n2;
        final int n = (n2 = this.choice.getPreferredWidth() - 8) - 12;
        final Font font = (this.font != null) ? this.font : Screen.font;
        this.str = c.textArr(this.string, font, n, n2);
        this.bounds[3] = (font.getHeight() + 4) * this.str.length;
    }

    public String toString() {
        return string;
    }
}
