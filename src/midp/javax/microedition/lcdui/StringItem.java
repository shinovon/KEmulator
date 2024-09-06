package javax.microedition.lcdui;

import emulator.lcdui.*;

public class StringItem extends Item {
	private String text;
	private int mode;
	Font font;
	String[] textArr;
	private int width;

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

	protected void paint(Graphics g, int x, int y, int w, int h, int row) {
		super.paint(g, x, y, w, h);

		final Font font = (this.font != null) ? this.font : Screen.font;

		if (mode == BUTTON) {
			String str = null;
			if (textArr != null) str = textArr[0];
			if (str == null) str = text;
			if (str == null) str = "...";
			int yo = y;
			if (hasLabel()) {
				g.setFont(Item.font);
				String label = this.label.trim();
				String[] tmp = c.textArr(label, Item.font, w, w);
				g.drawString(tmp[0], x + 2, yo, 0);
				yo += Item.font.getHeight() + 4;
			}
			g.setFont(font);
			int textWidth = font.stringWidth(str);
			g.drawString(str, x + (w - textWidth) / 2, yo, 0);
			int o = g.getColor();
			g.setColor(0xababab);
			int lx = x + w - 3;
			int ly = yo + h - yo + y - 3;
			g.drawLine(x + 1, ly, lx, ly);
			g.drawLine(lx, ly, lx, yo + 1);
			g.setColor(o);
			g.drawRect(x + 1, yo, w - 3, h - yo + y - 2);
		} else {
			g.setFont(font);
			if (isFocusable()) g.setColor(-11178603);
			if (isSizeLocked()) {
				if (row != 0) return;
				for (String s : textArr) {
					g.drawString(s, x + 2, y, 0);
					y += font.getHeight() + 4;
				}
				return;
			}
			if (textArr == null || row >= textArr.length) return;
			g.drawString(textArr[row], x + 1, y, 0);
		}
	}

	protected void layout(Row row) {
		super.layout(row);
		final Font font = (this.font != null) ? this.font : Screen.font;
		int preferredWidth = super.getPreferredWidth();
		int maxWidth = screen.bounds[W] - 8;
		int availableWidth = Math.min(preferredWidth, row.getAvailableWidth(maxWidth));
		if (isSizeLocked()) maxWidth = availableWidth;
		int[] maxw = new int[1];
		// TODO label layout
		String s = (hasLabel() && mode != BUTTON) ? (super.label + "\n" + this.text) : this.text;
		this.textArr = c.textArr(s, font, availableWidth, maxWidth, maxw);
		width = textArr.length != 0 ? maxw[0] + 4 : 4;
		final int fh = font.getHeight() + 4;
		if (mode == BUTTON) {
			width = Math.min(maxWidth, font.stringWidth(textArr[0]) + 10);
			textArr = new String[] { textArr[0] };
			bounds[H] = fh + (hasLabel() ? Item.font.getHeight() + 4 : 0);
		} else {
			bounds[H] = fh * this.textArr.length;
		}
	}

	public int getMinimumWidth() {
		final Font font = (this.font != null) ? this.font : Screen.font;
		return font.stringWidth("...") + 2;
	}

	public int getMinimumHeight() {
		final Font font = (this.font != null) ? this.font : Screen.font;
		return (font.getHeight() + 4) + (hasLabel() ? Item.font.getHeight() + 4 : 0);
	}

	public int getPreferredWidth() {
		return _hasLayout(LAYOUT_EXPAND) || isSizeLocked() ? super.getPreferredWidth() : width;
	}

	boolean isFocusable() {
		return commands.size() > 0;
	}

	int getRowWidth(int row) {
		if (textArr == null) return 0;
		final Font font = (this.font != null) ? this.font : Screen.font;
		return font.stringWidth(textArr[row]) + 2;
	}

	int getRowHeight(int row) {
		final Font font = (this.font != null) ? this.font : Screen.font;
		return font.getHeight() + 4;
	}

	int getRowsCount() {
		return textArr == null ? 0 : textArr.length;
	}
}
