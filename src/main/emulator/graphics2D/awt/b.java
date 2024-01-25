package emulator.graphics2D.awt;

import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.*;

import emulator.Settings;
import emulator.graphics2D.*;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Transform;

/**
 * Graphics2DAWT
 */
public final class b implements IGraphics2D
{
    Graphics2D g;
    int w;
    int h;
    private int anInt12;
	private a font;
    
    public b(final BufferedImage bufferedImage) {
        super();
        (g = bufferedImage.createGraphics()).setColor(new Color(0, 0, 0));
        this.w = bufferedImage.getWidth();
        this.h = bufferedImage.getHeight();
        //гладкий шрифт всегда
        if(Settings.textAntiAliasing)
			g.setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //гладкая отрисовка в настрах
        if(Settings.awtAntiAliasing)
			g.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
    }
    
    public final Graphics2D g() {
        return g;
    }
    
    public final void setAlpha(final int n) {
        g.setComposite(AlphaComposite.getInstance(3, n / 255.0f));
    }
    
    public final void clipRect(final int n, final int n2, final int n3, final int n4) {
        g.clipRect(n, n2, n3, n4);
    }
    
    public final void drawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        g.drawArc(n, n2, n3 + 1, n4 + 1, n5, n6);
    }
    
    public final void drawLine(final int n, final int n2, final int n3, final int n4) {
        g.drawLine(n, n2, n3, n4);
    }
    
    public final void drawRect(final int n, final int n2, final int n3, final int n4) {
        g.drawRect(n, n2, n3, n4);
    }
    
    public final void drawRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        g.drawRoundRect(n, n2, n3, n4, n5, n6);
    }
    
    public final void fillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        g.fillArc(n, n2, n3, n4, n5, n6);
    }
    
    public final void fillRect(final int n, final int n2, final int n3, final int n4) {
        g.fillRect(n, n2, n3, n4);
    }
    
    public final void fillRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        g.fillRoundRect(n, n2, n3, n4, n5, n6);
    }
    
    public final void fillPolygon(final int[] array) {
        final int n;
        final int[] array2 = new int[n = array.length >> 1];
        final int[] array3 = new int[n];
        for (int i = 0; i < n; ++i) {
            array2[i] = array[i << 1];
            array3[i] = array[(i << 1) + 1];
        }
        g.fillPolygon(array2, array3, n);
    }
    
    public final void drawPolygon(final int[] array) {
        final int n;
        final int[] array2 = new int[n = array.length >> 1];
        final int[] array3 = new int[n];
        for (int i = 0; i < n; ++i) {
            array2[i] = array[i << 1];
            array3[i] = array[(i << 1) + 1];
        }
        g.drawPolygon(array2, array3, n);
    }
    
    public static boolean isEmojiChar(char c) {
    	return (c >= '\u2011' && c <= '\u26FF') || (c >= '\u2700' && c <= '\u27BF') || (c >= '\uE000' && c <= '\uF8FF');
    }
    
    public static boolean isEmojiString(String s) {
    	return s.contains("\u26a0") || s.contains("\u26a1") || s.contains("\ud83d") || s.contains("\ud83e") || s.contains("\ud83c") || (s.length() > 1 && isEmojiChar(s.charAt(1))) || (s.length() > 0 && isEmojiChar(s.charAt(0)));
    }
    
    public final void drawString(final String s, final int x, final int y) {
    	//s.matches("([\u2700-\u27BF]|[\uE000-\uF8FF]|\uD83C[\uDC00-\uDFFF]|\uD83D[\uDC00-\uDFFF]|[\u2011-\u26FF]|\uD83E[\uDD10-\uDDFF])")
    	/*if(isEmojiString(s)) {
	        try {
	    		Font f = g.getFont();
	    		char[] chars = s.toCharArray();
	    		int _x = x;
	    		for(int i = 0; i < chars.length;) {
	    			//if(chars[i] == '\ud83d' && i + 2 < chars.length && chars[i + 2] == '\ud83c') {
	    			//	StringBuilder sb = new StringBuilder();
	    			//	String _s = sb.append(chars[i]).append(chars[i + 1]).append(chars[i + 2]).append(chars[i + 3]).toString();
	    	    	//	g.setFont(font.getEmojiFont());
	    			//	g.drawString(_s, _x, y);
	    			//	_x += font.emojistringWidth(_s);
	    			//	g.setFont(f);
	    			//	i += 4;
	    			//} else 
	    			if(chars[i] == '\ud83d' || chars[i] == '\ud83e') {
	    				String _s = "";
	    				StringBuilder sb = new StringBuilder();
	    				if(i + 2 < chars.length && chars[i + 2] == '\ud83c') {
		    				_s = sb.append(chars[i]).append(chars[i + 1]).append(chars[i + 2]).append(chars[i + 3]).toString();
		    				i += 4;
		    			} else {
		    				_s = sb.append(chars[i]).append(chars[i + 1]).toString();
		    				i += 2;
	    				}
	    	    		g.setFont(font.getEmojiFont());
	    				g.drawString(_s, _x, y);
	    				_x += font.emojistringWidth(_s);
	    				g.setFont(f);
	    			} else if(isEmojiChar(chars[i])){
	    	    		g.setFont(font.getEmojiFont());
	    	    		String _s = "" + chars[i];
	    				g.drawString(_s, _x, y);
	    				_x += font.emojistringWidth(_s);
	    				g.setFont(f);
	    				i += 1;
	    			} else {
	    				int j = 0;
	    				for(; j + i < chars.length && chars[j + i] != '\ud83d' && chars[i] != '\ud83e' && !isEmojiChar(chars[j + i]); j++);
	    				String _s = s.substring(i, j + i);
	    				g.drawString(_s, _x, y);
	    				_x += font.stringWidth(_s);
	    				i += j;
	    			}
	    		}
        	} catch (Throwable e) {
        		e.printStackTrace();
        	}
    	} else */g.drawString(s, x, y);
    	/*
    	boolean b = false;
    	Font f = null;
    	if(isEmojiString(s)) {
    		b = true;
    		f = g.getFont();
    		g.setFont(font.getEmojiFont());
    	}
        g.drawString(s, n, n2);
        if(b) g.setFont(f);
        */
    }
    
    public final void drawImage(final IImage image, final int n, final int n2) {
        g.drawImage(((d)image).getBufferedImage(), n, n2, null);
    }
    
    public final void drawImage(final IImage image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        g.drawImage(((d)image).getBufferedImage(), n5, n6, n5 + n7, n6 + n8, n, n2, n + n3, n2 + n4, null);
    }
    
    public final void setClip(final int n, final int n2, final int n3, final int n4) {
        g.setClip(n, n2, n3, n4);
    }
    
    public final void setColor(final int n, final boolean b) {
        g.setColor(new Color(n, b));
    }
    
    public final void setColor(final int n, final int n2, final int n3) {
        g.setColor(new Color(n, n2, n3));
    }
    
    public final void setFont(final IFont font) {
    	this.font = (a) font;
        g.setFont(((a)font).getAWTFont());
    }
    
    public final void setStrokeStyle(final int anInt12) {
        Label_0061: {
            Graphics2D graphics2D;
            BasicStroke stroke;
            if (anInt12 == 0) {
                graphics2D = g;
                stroke = new BasicStroke(1.0f);
            }
            else {
                if (anInt12 != 1) {
                    break Label_0061;
                }
                final BasicStroke basicStroke = new BasicStroke(1.0f, 0, 0, 10.0f, new float[] { 3.0f, 3.0f }, 0.0f);
                graphics2D = g;
                stroke = basicStroke;
            }
            graphics2D.setStroke(stroke);
        }
        this.anInt12 = anInt12;
    }
    
    public final int getStrokeStyle() {
        return this.anInt12;
    }
    
    public final int getClipHeight() {
        if (g.getClipBounds() == null) {
            return this.h;
        }
        return g.getClipBounds().height;
    }
    
    public final int getClipWidth() {
        if (g.getClipBounds() == null) {
            return this.w;
        }
        return g.getClipBounds().width;
    }
    
    public final int getClipX() {
        if (g.getClipBounds() == null) {
            return 0;
        }
        return g.getClipBounds().x;
    }
    
    public final int getClipY() {
        if (g.getClipBounds() == null) {
            return 0;
        }
        return g.getClipBounds().y;
    }
    
    public final int getColor() {
        return g.getColor().getRGB();
    }
    
    public final int getColorRed() {
        return g.getColor().getRed();
    }
    
    public final int getColorGreen() {
        return g.getColor().getGreen();
    }
    
    public final int getColorBlue() {
        return g.getColor().getBlue();
    }
    
    public final float[] RGBtoHSB(final int n, final int n2, final int n3) {
        return Color.RGBtoHSB(n, n2, n3, null);
    }
    
    public final ITransform getTransform() {
        return new c(g.getTransform());
    }
    
    public final void setTransform(final ITransform transform) {
        g.setTransform(((c)transform).method8());
    }
    
    public final void transform(final ITransform transform) {
        g.transform(((c)transform).method8());
    }
    
    public final void translate(final int x, final int y) {
        g.translate(x, y);
    }

	public IFont getFont() {
		return font;
	}
}
