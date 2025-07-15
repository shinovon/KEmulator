package javax.microedition.lcdui;

import emulator.Emulator;
import emulator.graphics2D.IFont;

public class Font {
	IFont font;
	int face;
	int size;
	int style;
	public static final int STYLE_PLAIN = 0;
	public static final int STYLE_BOLD = 1;
	public static final int STYLE_ITALIC = 2;
	public static final int STYLE_UNDERLINED = 4;
	public static final int SIZE_SMALL = 8;
	public static final int SIZE_MEDIUM = 0;
	public static final int SIZE_LARGE = 16;
	public static final int FACE_SYSTEM = 0;
	public static final int FACE_MONOSPACE = 32;
	public static final int FACE_PROPORTIONAL = 64;
	static Font defaultFont;
	public static final int FONT_STATIC_TEXT = 0;
	public static final int FONT_INPUT_TEXT = 1;

	public Font(final int face, final int style, final int size) {
		super();
		this.face = face;
		this.size = size;
		this.style = style;
		int mappedSize = 0;
		switch (size) {
			case SIZE_MEDIUM: {
				mappedSize = Emulator.getEmulator().getProperty().getFontMediumSize();
				break;
			}
			case SIZE_LARGE: {
				mappedSize = Emulator.getEmulator().getProperty().getFontLargeSize();
				break;
			}
			case SIZE_SMALL:
			default: {
				mappedSize = Emulator.getEmulator().getProperty().getFontSmallSize();
				break;
			}
		}
		int filteredStyle;
		if (((filteredStyle = this.style) & STYLE_UNDERLINED) != 0x0) {
			filteredStyle &= 0xFFFFFFFB;
		}
		this.font = Emulator.getEmulator().newFont(face, mappedSize, filteredStyle);
	}

	public Font deriveFont(int size) {
		int anInt1316;
		if (((anInt1316 = this.style) & 0x4) != 0x0) {
			anInt1316 &= 0xFFFFFFFB;
		}
		this.font = Emulator.getEmulator().newFont(face, size, anInt1316);
		return this;
	}

	public Font deriveFont(int style, int height) {
		this.style = 0;
		this.font = Emulator.getEmulator().newCustomFont(height, 0);
		return this;
	}

	public IFont getImpl() {
		return this.font;
	}

	public static Font getFont(final int n, final int n2, final int n3) {
		return new Font(n, n2, n3);
	}

	public static Font getDefaultFont() {
		if (Font.defaultFont == null) {
			Font.defaultFont = getFont(0, 0, 8);
		}
		return Font.defaultFont;
	}

	public int charWidth(final char c) {
		return this.font.charWidth(c);
	}

	public int charsWidth(final char[] array, final int n, final int n2) {
		return this.font.stringWidth(new String(array, n, n2));
	}

	public int substringWidth(final String s, final int n, final int n2) {
		return this.font.stringWidth(s.substring(n, n + n2));
	}

	public int stringWidth(final String s) {
		return this.font.stringWidth(s);
	}

	public int getHeight() {
		return this.font.getHeight();
	}

	public int getStyle() {
		return this.style;
	}

	public int getSize() {
		return this.size;
	}

	public int getFace() {
		return this.face;
	}

	public boolean isPlain() {
		return this.style == 0;
	}

	public boolean isBold() {
		return (this.style & STYLE_BOLD) == STYLE_BOLD;
	}

	public boolean isItalic() {
		return(this.style & STYLE_ITALIC) == STYLE_ITALIC;
	}

	public boolean isUnderlined() {
		return (this.style & STYLE_UNDERLINED) == STYLE_UNDERLINED;
	}

	public int getBaselinePosition() {
		return this.font.getAscent();
	}

	public static Font getFont(final int n) {
		return getDefaultFont();
	}

	static {
		Font.defaultFont = null;
	}
}
