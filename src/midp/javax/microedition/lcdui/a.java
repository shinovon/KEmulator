package javax.microedition.lcdui;

import emulator.lcdui.*;

final class a
{
    boolean aBoolean417;
    boolean aBoolean424;
    String aString418;
    Image anImage419;
    Font aFont420;
    int[] anIntArray421;
    ChoiceGroup aChoiceGroup422;
    String[] aStringArray423;
    
    a(final String aString418, final Image anImage419, final Font aFont420, final ChoiceGroup aChoiceGroup422) {
        super();
        this.aString418 = aString418;
        this.anImage419 = anImage419;
        this.aFont420 = aFont420;
        this.aChoiceGroup422 = aChoiceGroup422;
        this.anIntArray421 = new int[4];
        this.aBoolean424 = true;
    }
    
    protected final void method211(final Graphics graphics, final boolean b) {
        graphics.setColor(-16777216);
        final boolean b2 = this.aChoiceGroup422.anInt349 == 4;
        final boolean b3 = this.aChoiceGroup422.aBoolean542 && this.aChoiceGroup422.anIntArray179 != null;
        int anInt28 = 0;
        int anInt29 = 0;
        Label_0103: {
            if (b2) {
                if (!b3) {
                    anInt28 = this.aChoiceGroup422.anIntArray21[1];
                    anInt29 = this.aChoiceGroup422.anInt28;
                    break Label_0103;
                }
                anInt28 = this.aChoiceGroup422.anInt28;
            }
            else {
                anInt28 = this.aChoiceGroup422.anIntArray21[1];
            }
            anInt29 = this.anIntArray421[1];
        }
        int n = anInt28 + anInt29;
        final int n2 = this.anIntArray421[0] + 4;
        if (b) {
            emulator.lcdui.a.method178(graphics, this.anIntArray421[0] + 1, n - 2, this.anIntArray421[2] - 2, this.anIntArray421[3]);
        }
        if (!b2 || !b3) {
            emulator.lcdui.a.method180(graphics, n2, n + 3, this.aBoolean417, this.aChoiceGroup422.anInt349);
        }
        final Font font = (this.aFont420 != null) ? this.aFont420 : Screen.aFont173;
        graphics.setFont(font);
        for (int i = 0; i < this.aStringArray423.length; ++i) {
            graphics.drawString(this.aStringArray423[i], ((i == 0 && this.aChoiceGroup422.anInt349 != 3 && !b3) ? (n2 + 10) : n2) + 4, n, 0);
            n += font.getHeight() + 4;
        }
    }
    
    protected final void method212() {
        this.anIntArray421[0] = 0;
        this.anIntArray421[1] = 0;
        this.anIntArray421[2] = this.aChoiceGroup422.anIntArray21[2];
        final int n2;
        final int n = (n2 = this.aChoiceGroup422.getPreferredWidth() - 8) - 12;
        final Font font = (this.aFont420 != null) ? this.aFont420 : Screen.aFont173;
        this.aStringArray423 = c.method175(this.aString418, font, n, n2);
        this.anIntArray421[3] = (font.getHeight() + 4) * this.aStringArray423.length;
    }
}
