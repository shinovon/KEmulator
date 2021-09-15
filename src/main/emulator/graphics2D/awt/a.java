package emulator.graphics2D.awt;

import emulator.graphics2D.*;
import java.awt.*;
import java.awt.image.*;

/**
 * FontAWT
 */
public final class a implements IFont
{
    private Font font;
    private Font emoji;
    private FontMetrics metrics;
	private FontMetrics emojimetrics;
    
    public a(final String s, final int n, final int n2) {
        super();
        this.font = new Font(s, n2, n);
        this.emoji = new Font("Segoe UI Emoji", n2, n);
        this.metrics = new BufferedImage(1, 1, 1).getGraphics().getFontMetrics(this.font);
        this.emojimetrics = new BufferedImage(1, 1, 1).getGraphics().getFontMetrics(this.emoji);
    }
    
    public final int stringWidth(final String s) {
		if(b.isEmojiString(s)) { 
			return emojimetrics.stringWidth(s);
		}
        return this.metrics.stringWidth(s);
    }
    public final int emojistringWidth(final String s) {
		return emojimetrics.stringWidth(s);
    }
    
    public final Font getAWTFont() {
        return this.font;
    }
    
    public final int charWidth(final char c) {
        return this.metrics.charWidth(c);
    }
    
    public final int getHeight() {
        return this.metrics.getHeight();
    }
    
    public final int getAscent() {
        return this.metrics.getAscent();
    }

	public Font getEmojiFont() {
		return emoji;
	}
}
