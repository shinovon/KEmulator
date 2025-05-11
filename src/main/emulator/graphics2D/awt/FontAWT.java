package emulator.graphics2D.awt;

import emulator.graphics2D.IFont;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * FontAWT
 */
public final class FontAWT implements IFont {
	private Font font;
	private FontMetrics metrics;
	private static Font nokiaFont, s60Font;

	public FontAWT(final String s, final int size, final int style, boolean height) {
		super();
		if ("Series 60".equals(s)) {
			if (s60Font == null) {
				try {
					s60Font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/res/s60snr.ttf"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			this.font = s60Font.deriveFont(style, size);
		} else if ("Nokia".equals(s)) {
			if (nokiaFont == null) {
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
		metrics();
		if (height && metrics.getHeight() != size) {
			float f = ((float) metrics.charWidth('W') / (float) metrics.getHeight()) * (float) size;
			font = font.deriveFont(f);
			metrics();
		}
	}

	private void metrics() {
		this.metrics = new BufferedImage(1, 1, 1).getGraphics().getFontMetrics(this.font);
	}

	public final int stringWidth(final String s) {
		return this.metrics.stringWidth(s);
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

	public final int getDescent() {
		return this.metrics.getAscent();
	}
}
