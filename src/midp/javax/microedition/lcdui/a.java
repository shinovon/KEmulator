package javax.microedition.lcdui;

import emulator.lcdui.*;
/**
 * ChoiceGroupItem
 */
final class a
{
    boolean sel;
    boolean aBoolean424;
    String string;
    Image image;
    Font font;
    int[] anIntArray421;
    ChoiceGroup aChoiceGroup422;
    String[] aStringArray423;
    
    a(final String aString418, final Image anImage419, final Font aFont420, final ChoiceGroup aChoiceGroup422) {
        super();
        this.string = aString418;
        this.image = anImage419;
        this.font = aFont420;
        this.aChoiceGroup422 = aChoiceGroup422;
        this.anIntArray421 = new int[4];
        this.aBoolean424 = true;
    }
    
    protected final void method211(final Graphics graphics, final boolean b) {

        graphics.setColor(-16777216);
        final boolean b2 = this.aChoiceGroup422.choiceType == 4;
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
            emulator.lcdui.a.method180(graphics, n2, n + 3, this.sel, this.aChoiceGroup422.choiceType);
        }
        final Font font = (this.font != null) ? this.font : Screen.aFont173;
        graphics.setFont(font);
        if(this.aStringArray423 != null)
        for (int i = 0; i < this.aStringArray423.length; ++i) {
            graphics.drawString(this.aStringArray423[i], ((i == 0 && this.aChoiceGroup422.choiceType != 3 && !b3) ? (n2 + 10) : n2) + 4, n, 0);
            n += font.getHeight() + 4;
        }
    }
    
    protected final void method212() {
        this.anIntArray421[0] = 0;
        this.anIntArray421[1] = 0;
        this.anIntArray421[2] = this.aChoiceGroup422.anIntArray21[2];
        final int n2;
        final int n = (n2 = this.aChoiceGroup422.getPreferredWidth() - 8) - 12;
        final Font font = (this.font != null) ? this.font : Screen.aFont173;
        this.aStringArray423 = c.method175(this.string, font, n, n2);
        this.anIntArray421[3] = (font.getHeight() + 4) * this.aStringArray423.length;
    }
    
    public String toString() {
    	return string;
    }
}
