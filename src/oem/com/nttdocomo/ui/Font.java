package com.nttdocomo.ui;

public class Font {
	public static final int TYPE_DEFAULT = 0;
	public static final int TYPE_HEADING = 1;
	public static final int FACE_SYSTEM = 1895825408;
	public static final int FACE_MONOSPACE = 1912602624;
	public static final int FACE_PROPORTIONAL = 1929379840;
	public static final int STYLE_PLAIN = 1880096768;
	public static final int STYLE_BOLD = 1880162304;
	public static final int STYLE_ITALIC = 1880227840;
	public static final int STYLE_BOLDITALIC = 1880293376;
	public static final int SIZE_SMALL = 1879048448;
	public static final int SIZE_MEDIUM = 1879048704;
	public static final int SIZE_LARGE = 1879048960;
	protected javax.microedition.lcdui.Font impl;
	protected static Font defaultFont;

	protected Font(final javax.microedition.lcdui.Font impl) {
		this.impl = impl;
	}

	public javax.microedition.lcdui.Font getImpl() {
		return this.impl;
	}

	public static Font getFont(final int n) {
		int n2;
		if ((n & 0x72000000) != 0x0) {
			n2 = 32;
		} else if ((n & 0x73000000) != 0x0) {
			n2 = 64;
		} else {
			n2 = 0;
		}
		int n3;
		if ((n & 0x70130000) != 0x0) {
			n3 = 3;
		} else if ((n & 0x70120000) != 0x0) {
			n3 = 2;
		} else if ((n & 0x70110000) != 0x0) {
			n3 = 1;
		} else {
			n3 = 0;
		}
		int n4;
		if ((n & 0x70000300) != 0x0) {
			n4 = 16;
		} else if ((n & 0x70000200) != 0x0) {
			n4 = 0;
		} else {
			n4 = 8;
		}
		return new Font(javax.microedition.lcdui.Font.getFont(n2, n3, n4));
	}

	public static Font getFont(int n, int n2) {
		return getFont(n);
	}

	public static Font getDefaultFont() {
		if (Font.defaultFont == null) {
			Font.defaultFont = new Font(javax.microedition.lcdui.Font.getDefaultFont());
		}
		return Font.defaultFont;
	}

	public static void setDefaultFont(final Font defaultFont) {
		Font.defaultFont = defaultFont;
	}

	public int getAscent() {
		return this.impl.getImpl().getAscent();
	}

	public int getDescent() {
		return this.impl.getImpl().getDescent();
	}

	public int getHeight() {
		return this.impl.getHeight();
	}

	public int stringWidth(final String s) {
		return this.impl.stringWidth(s);
	}

	public int getBBoxWidth(final String s) {
		return this.stringWidth(s);
	}

	public int getBBoxHeight(final String s) {
		return this.getHeight();
	}

	public int getLineBreak(final String s, final int n, final int n2, final int n3) {
		int n4 = 0;
		for (int i = 0; i < n2; ++i) {
			n4 += this.impl.charWidth(s.charAt(n + i));
			if (n4 > n3) {
				return n + i;
			}
		}
		return n + n2;
	}
}
