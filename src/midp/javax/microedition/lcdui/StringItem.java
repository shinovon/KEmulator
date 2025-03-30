package javax.microedition.lcdui;

import emulator.lcdui.LCDUIUtils;
import emulator.lcdui.TextUtils;

public class StringItem extends Item {
	private String text;
	private int mode;
	Font font;
	String[] textArr;
	private int width;

	private int textColor = LCDUIUtils.foregroundColor;
	private int focusableTextColor = LCDUIUtils.highlightedForegroundColor;
	private int focusedColor = LCDUIUtils.highlightedForegroundColor;
	private int focusedBackgroundColor = LCDUIUtils.highlightedBackgroundColor;

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
		layoutForm();
	}

	public int getAppearanceMode() {
		return this.mode;
	}

	public void setFont(final Font aFont358) {
		this.font = aFont358;
		layoutForm();
	}

	public Font getFont() {
		return this.font;
	}

	public void setPreferredSize(final int w, final int h) {
		super.setPreferredSize(w, h);
	}

	void paint(Graphics g, int x, int y, int w, int h, int row) {
//		super.paint(g, x, y, w, h);

		final Font font = (this.font != null) ? this.font : Screen.font;

		if (mode == BUTTON) {
			if (focused) {
				g.setColor(focusedBackgroundColor);
				g.fillRect(x + 2, y + 2, w - 4, h - 4);
			}
			String str = null;
			if (textArr != null && textArr.length != 0) str = textArr[0];
			if (str == null) str = text;
			if (str == null) str = "...";
			int yo = y;
			if (hasLabel()) {
				g.setFont(labelFont);
				g.setColor(focused ? focusedColor : labelColor);
				String label = this.label.trim();
				String[] tmp = TextUtils.textArr(label, labelFont, w, w);
				g.drawString(tmp[0], x + 2, yo, 0);
				yo += labelFont.getHeight() + 4;
			}
			g.setFont(font);
			g.setColor(focused ? focusedColor : textColor);
			int textWidth = font.stringWidth(str);
			g.drawString(str, x + (w - textWidth) / 2, yo, 0);
			g.setColor(LCDUIUtils.borderColor);
			int lx = x + w - 3;
			int ly = yo + h - yo + y - 3;
			g.drawLine(x + 2, ly, lx, ly);
			g.drawLine(lx, ly, lx, yo + 1);
			g.setColor(focused ? focusedColor : LCDUIUtils.foregroundColor);
			g.drawRect(x + 2, yo, w - 4, h - yo + y - 2);
		} else {
			if (focused) {
				g.setColor(focusedBackgroundColor);
				g.fillRect(x + 1, y + 1, w - 2, h - 2);
				g.setColor(focusedColor);
				g.drawRect(x, y + 1, w, h - 2);
			}
			if (isSizeLocked() || hasLabel()) {
				if (row != 0) return;
				g.setFont(labelFont);
				g.setColor(focused ? focusedColor : labelColor);
				if (labelArr != null) for (String s : labelArr) {
					g.drawString(s, x, y, 0);
					y += labelFont.getHeight() + 4;
				}
				g.setFont(font);
				for (String s : textArr) {
					g.drawString(s, x, y, 0);
					y += font.getHeight() + 2;
				}
				return;
			}
			if (labelArr != null) {
				g.setFont(labelFont);
				g.setColor(focused ? focusedColor : labelColor);
				if (row < labelArr.length) {
					g.drawString(labelArr[row], x + 1, y, 0);
					return;
				}
				row -= labelArr.length;
			}
			g.setFont(font);
			g.setColor(focused ? focusedColor : (isFocusable() ? focusableTextColor : textColor));
			if (textArr == null || row >= textArr.length) return;
			g.drawString(textArr[row], x + 1, y, 0);
		}
	}

	void layout(Row row) {
		super.layout(row);
		final Font font = (this.font != null) ? this.font : Screen.font;
		int maxWidth = screen.bounds[W] - 8;
		int preferredWidth = this.preferredWidth != -1 ? super.getPreferredWidth() : maxWidth;
		int availableWidth = Math.min(preferredWidth, row.getAvailableWidth(maxWidth));
		if (isSizeLocked() || hasLabel()) maxWidth = availableWidth;

		int min = getMinimumWidth();
		if (availableWidth < min) availableWidth = min;
		if (maxWidth < min) maxWidth = min;

		int[] maxw = new int[1];
		int w = 0;
		if (hasLabel()) {
			if (mode == BUTTON) {
				String[] a = TextUtils.textArr(label.trim(), labelFont, maxWidth, maxWidth, maxw);
				if (a.length != 0) w = labelFont.stringWidth(a[0].trim());
			} else {
				labelArr = TextUtils.textArr(label, labelFont, maxWidth, maxWidth, maxw);
				if (labelArr.length != 0) w = maxw[0] + 4;
			}
		} else {
			labelArr = null;
		}
		String s = text;
		if (s.startsWith("\n")) s = s.substring(1);
		if (s.endsWith("\n") && !_hasLayout(Item.LAYOUT_NEWLINE_AFTER)) {
			s = s.substring(0, s.length() - 1);
		}
		textArr = TextUtils.textArr(s, font, availableWidth, maxWidth, maxw);
		final int fh = font.getHeight() + 2;
		if (mode == BUTTON) {
			width = Math.max(w, Math.min(maxWidth, textArr.length != 0 ? font.stringWidth(textArr[0]) + 10 : 4));
			textArr = textArr.length != 0 ? new String[] { textArr[0] } : textArr;
			bounds[H] = fh + 2 + (hasLabel() ? labelFont.getHeight() + 4 : 0);
		} else {
			width = Math.max(w, textArr.length != 0 ? maxw[0] : 4);
			bounds[H] = fh * textArr.length
					+ (labelArr != null ? (labelFont.getHeight() + 4) * labelArr.length : 0);
		}
	}

	public int getMinimumWidth() {
		final Font font = (this.font != null) ? this.font : Screen.font;
		return font.stringWidth("...") + 2;
	}

	public int getMinimumHeight() {
		final Font font = (this.font != null) ? this.font : Screen.font;
		return (font.getHeight() + 2) + (hasLabel() ? labelFont.getHeight() + 4 : 0);
	}

	public int getPreferredWidth() {
		return super.isSizeLocked() && preferredWidth != -1 ? super.getPreferredWidth() : width;
	}

	boolean isFocusable() {
		return commands.size() > 0;
	}

	int getRowWidth(int row) {
		if (labelArr != null) {
			if (row < labelArr.length)
				return labelFont.stringWidth(labelArr[row]) + 2;
			row -= labelArr.length;
		}
		if (textArr == null) return 0;
		return ((font != null) ? font : Screen.font).stringWidth(textArr[row]);
	}

	int getRowHeight(int row) {
		if (labelArr != null && row < labelArr.length) {
			return labelFont.getHeight() + 4;
		}
		return ((font != null) ? font : Screen.font).getHeight() + 2;
	}

	int getRowsCount() {
		return (textArr == null ? 0 : textArr.length) + ((labelArr == null ? 0 : labelArr.length));
	}

	public void _setColor(int type, int color) {
		switch (type) {
			case 0:
				textColor = focusableTextColor = color;
				break;
			case 1:
				focusedColor = color;
				break;
			case 2:
				focusedBackgroundColor = color;
				break;
		}
	}
}
