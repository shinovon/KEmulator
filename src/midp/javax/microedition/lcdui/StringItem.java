package javax.microedition.lcdui;

import emulator.graphics2D.IFont;
import emulator.lcdui.*;

public class StringItem extends Item
{
    private String text;
    private int mode;
    private Font font;
    private String[] textArr;
    
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
    
    protected void paint(final Graphics graphics) {
        super.paint(graphics);
        
        final Font font = (this.font != null) ? this.font : Screen.aFont173;
        int n = super.anIntArray21[1];
        graphics.setFont(font);

        if(mode == BUTTON) {
        	//кнопка
        	String str = null;
        	// определение строки
        	if(textArr != null) {
        		str = textArr[0];
        	}
        	if(str == null) {
        		str = text;
        	}
        	if(str == null) {
        		str = "...";
        	}
        	int k = super.anIntArray21[0];
	        graphics.drawString(str, k + 4, n + 1, 0);
	        // определение размера строки
	        int j = 0;
	        IFont f = graphics.getImpl().getFont();
	        if(f != null)
	        	j = f.stringWidth(str);
	        if(j > 0) {
	        	int h = font.getHeight();
	        	//очертания кнопки
	        	int o = graphics.getColor();
	        	graphics.setColor(0xababab);
	        	k = k + 2;
	        	int lx = k + 2 + j + 1;
	        	int ly = n + h + 3;
	        	graphics.drawLine(k + 1, ly, lx, ly); 
	        	graphics.drawLine(lx, ly, lx, n + 1); 
	        	graphics.setColor(o);
	        	graphics.drawRect(k, n, j + 4, h + 4); 
	        }
        } else {
	        for (int i = this.getcurPage(); i < this.textArr.length; ++i) {
	            graphics.drawString(this.textArr[i], super.anIntArray21[0] + 4, n + 2, 0);
	            if ((n += font.getHeight() + 4) > super.screen.anIntArray21[3]) {
	                return;
	            }
	        }
        }
    }
    
    protected void layout() {
        super.layout();
        final Font font = (this.font != null) ? this.font : Screen.aFont173;
        final int n = this.getPreferredWidth() - 8;
        this.textArr = c.method175((super.label != null) ? (super.label + " " + this.text) : this.text, font, n, n);
        final int n3;
        int n2 = (n3 = font.getHeight() + 4) * this.textArr.length;
        super.anIntArray179 = null;
        final int n4 = super.screen.anIntArray21[3] / n3 * n3;
        if (n2 > super.screen.anIntArray21[3] && n4 > 0) {
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
        super.anIntArray21[3] = Math.min(n2, super.screen.anIntArray21[3]);
    }
}
