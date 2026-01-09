package javax.microedition.lcdui;

import emulator.AppSettings;
import emulator.Emulator;
import emulator.graphics2D.IFont;

import java.io.IOException;
import java.io.InputStream;

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
				mappedSize = AppSettings.fontMediumSize;
				break;
			}
			case SIZE_LARGE: {
				mappedSize = AppSettings.fontLargeSize;
				break;
			}
			case SIZE_SMALL:
			default: {
				mappedSize = AppSettings.fontSmallSize;
				break;
			}
		}
		int filteredStyle;
		if (((filteredStyle = this.style) & STYLE_UNDERLINED) != 0x0) {
			filteredStyle &= 0xFFFFFFFB;
		}
		this.font = Emulator.getEmulator().newFont(face, mappedSize, filteredStyle);
	}

	private Font(int face, int size, int style, IFont impl)  {
		this.face = face;
		this.size = size;
		this.style = style;
		this.font = impl;
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

	public static Font _getNokiaUiFont(int face, int style, int height) {
		int filteredStyle;
		if (((filteredStyle = style) & 0x4) != 0x0) {
			filteredStyle &= 0xFFFFFFFB;
		}
		IFont font = Emulator.getEmulator().newCustomFont(face, height, filteredStyle, true);
		return new Font(face, style, Font.SIZE_MEDIUM, font);
	}

	// MIDP 3.0

	public static Font createFont(InputStream fontData) throws IOException {
		int size = Emulator.getEmulator().getProperty().getFontMediumSize();
		IFont font = Emulator.getEmulator().loadFont(fontData, size);
		if (font == null) {
			return getDefaultFont();
		}
		return new Font(FACE_SYSTEM, size, SIZE_MEDIUM, font);
	}

	public static Font getFont(String name, int style, int pixelSize) {
		int filteredStyle;
		if (((filteredStyle = style) & 0x4) != 0x0) {
			filteredStyle &= 0xFFFFFFFB;
		}
		if (pixelSize <= 0) {
			pixelSize = Emulator.getEmulator().getProperty().getFontMediumSize();
		}
		IFont font = Emulator.getEmulator().newFont(name, pixelSize, filteredStyle);
		return new Font(FACE_SYSTEM, style, SIZE_MEDIUM, font);
	}

	public Font deriveFont(int pixelSize) {
		return deriveFont(style, pixelSize);
	}

	public Font deriveFont(int style, int pixelSize) {
		this.style = style;
		int filteredStyle;
		if (((filteredStyle = this.style) & 0x4) != 0x0) {
			filteredStyle &= 0xFFFFFFFB;
		}
		if (pixelSize <= 0) {
			pixelSize = Emulator.getEmulator().getProperty().getFontMediumSize();
		}
		IFont font = Emulator.getEmulator().newCustomFont(face, pixelSize, filteredStyle, false);
		return new Font(face, style, size, font);
	}

	public String getFamily() {
		return font.getFamily();
	}

	public String getName() {
		return font.getName();
	}

	public String getFontName() {
		return font.getFontName();
	}

	public int getPixelSize() {
		return font.getPixelSize();
	}

	public int getAscent() {
		return font.getAscent();
	}

	public int getDescent() {
		return font.getDescent();
	}

	public int getMaxAscent() {
		return font.getMaxAscent();
	}

	public int getMaxDescent() {
		return font.getMaxDescent();
	}

	public int getLeading() {
		return font.getLeading();
	}
}
