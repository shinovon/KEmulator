package javax.microedition.lcdui;

import emulator.lcdui.*;

public class StringItem extends Item
{
    private String aString25;
    private int anInt349;
    private Font aFont358;
    private String[] aStringArray359;
    
    public StringItem(final String s, final String s2) {
        this(s, s2, 0);
    }
    
    public StringItem(final String s, final String aString25, final int anInt349) {
        super(s);
        if (anInt349 < 0 || anInt349 > 2) {
            throw new IllegalArgumentException();
        }
        this.aString25 = aString25;
        this.anInt349 = anInt349;
        this.aFont358 = null;
    }
    
    public String getText() {
        return this.aString25;
    }
    
    public void setText(final String aString25) {
        this.aString25 = aString25;
    }
    
    public int getAppearanceMode() {
        return this.anInt349;
    }
    
    public void setFont(final Font aFont358) {
        this.aFont358 = aFont358;
    }
    
    public Font getFont() {
        return this.aFont358;
    }
    
    public void setPreferredSize(final int n, final int n2) {
        super.setPreferredSize(n, n2);
    }
    
    protected void paint(final Graphics graphics) {
        super.paint(graphics);
        final Font font = (this.aFont358 != null) ? this.aFont358 : Screen.aFont173;
        int n = super.anIntArray21[1];
        graphics.setFont(font);
        for (int i = this.getcurPage(); i < this.aStringArray359.length; ++i) {
            graphics.drawString(this.aStringArray359[i], super.anIntArray21[0] + 4, n + 2, 0);
            if ((n += font.getHeight() + 4) > super.aScreen176.anIntArray21[3]) {
                return;
            }
        }
    }
    
    protected void layout() {
        super.layout();
        final Font font = (this.aFont358 != null) ? this.aFont358 : Screen.aFont173;
        final int n = this.getPreferredWidth() - 8;
        this.aStringArray359 = c.method175((super.aString172 != null) ? (super.aString172 + " " + this.aString25) : this.aString25, font, n, n);
        final int n3;
        int n2 = (n3 = font.getHeight() + 4) * this.aStringArray359.length;
        super.anIntArray179 = null;
        final int n4 = super.aScreen176.anIntArray21[3] / n3 * n3;
        if (n2 > super.aScreen176.anIntArray21[3] && n4 > 0) {
            int n5 = n2 / n4;
            if (n2 % n4 != 0) {
                ++n5;
            }
            super.anIntArray179 = new int[n5];
            for (int i = 0; i < n5; ++i) {
                super.anIntArray179[i] = i * n4 / n3;
            }
            if (super.anInt182 >= super.anIntArray179.length) {
                super.anInt182 = 0;
            }
            n2 = (this.aStringArray359.length - super.anIntArray179[super.anInt182]) * n3;
        }
        super.anIntArray21[3] = Math.min(n2, super.aScreen176.anIntArray21[3]);
    }
}
