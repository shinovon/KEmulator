package emulator.graphics2D.awt;

import emulator.graphics2D.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * FontAWT
 */
public final class a implements IFont
{
    private Font font;
    //private Font emoji;
    private FontMetrics metrics;
	//private FontMetrics emojimetrics;
	private static Font nokiaFont;
    
    public a(final String s, final int size, final int style, boolean height) {
        super();
        Map<TextAttribute, Object> attributes = new HashMap<>();
        if(s.equals("Nokia")) {
        	if(nokiaFont == null) {
        		try {
					nokiaFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/res/Nokia.ttf"));
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        	this.font = nokiaFont.deriveFont(style, size);
        } else {
        	this.font = new Font(s, style, size);
        }
        //this.emoji = new Font("Segoe UI Emoji", style, size);
        metrics();
        if(height && metrics.getHeight() != size) {
        	float f = ((float)metrics.charWidth('W') / (float)metrics.getHeight()) * (float)size;
        	font = font.deriveFont(f);
        	metrics();
        }
        //this.emojimetrics = new BufferedImage(1, 1, 1).getGraphics().getFontMetrics(this.emoji);
    }
    
    private void metrics() {
        this.metrics = new BufferedImage(1, 1, 1).getGraphics().getFontMetrics(this.font);
    }
    
    public final int stringWidth(final String s) {
		/*if(b.isEmojiString(s)) { 
			return emojimetrics.stringWidth(s);
		}*/
        return this.metrics.stringWidth(s);
    }
   /* public final int emojistringWidth(final String s) {
		return emojimetrics.stringWidth(s);
    }*/
    
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

	/*public Font getEmojiFont() {
		return emoji;
	}*/
}
