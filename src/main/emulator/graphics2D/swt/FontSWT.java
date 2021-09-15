package emulator.graphics2D.swt;

import emulator.graphics2D.*;
import org.eclipse.swt.graphics.*;

public final class FontSWT implements IFont
{
    private Font font;
    private GC gc;
    
    public FontSWT(final String s, final int n, final int n2) {
        super();
        this.font = new Font((Device)null, s, n, n2);
        (this.gc = new GC((Drawable)new Image((Device)null, 1, 1))).setFont(this.font);
    }
    
    public final void finalize() {
        if (this.font != null && !this.font.isDisposed()) {
            this.font.dispose();
        }
        if (this.gc != null && !this.gc.isDisposed()) {
            this.gc.dispose();
        }
    }
    
    public final Font method297() {
        return this.font;
    }
    
    public final int charWidth(final char c) {
        return this.gc.getCharWidth(c);
    }
    
    public final int stringWidth(final String s) {
    	//int i = 0;
    	//for(char c: s.toCharArray()) {
    	//	i += this.gc.getCharWidth(c);
    	//}
    	//return i;
        return this.gc.stringExtent(s).x;
    }
    
    public final int getHeight() {
        return this.gc.getFontMetrics().getHeight();
    }
    
    public final int getAscent() {
        return this.gc.getFontMetrics().getAscent();
    }
}
