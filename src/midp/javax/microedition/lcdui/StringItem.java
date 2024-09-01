package javax.microedition.lcdui;

import emulator.lcdui.*;

public class StringItem extends Item {
	private String text;
	private int mode;
	Font font;
	String[] textArr;
	private int maxTextWidth;

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
		queueLayout();
	}

	public int getAppearanceMode() {
		return this.mode;
	}

	public void setFont(final Font aFont358) {
		this.font = aFont358;
		queueLayout();
	}

	public Font getFont() {
		return this.font;
	}

	public void setPreferredSize(final int n, final int n2) {
		super.setPreferredSize(n, n2);
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
			int textWidth = font.stringWidth(str);
			if (hasLabel()) {
				g.setFont(Item.font);
				String label = this.label.trim();
				String[] tmp = c.textArr(label, Item.font, w, w);
				g.drawString(label, x + 4, yo, 0);
				yo += Item.font.getHeight() + 4;
			}
			g.setFont(font);
			g.drawString(str, x + 4, yo, 0);
			int fh = font.getHeight();
			int o = g.getColor();
			g.setColor(0xababab);
			int lx = x + w - 6;
			int ly = yo + h + 3 - 1;
//			g.drawLine(x + 1, ly, lx, ly);
//			g.drawLine(lx, ly, lx, yo + 1);
			g.setColor(o);
			g.drawRect(x + 2, yo, w - 4, h - 2);
		} else {
			g.setFont(font);
			if (row >= textArr.length) return;
			g.drawString(textArr[row], x, y, 0);
		}

//		if (mode == BUTTON) {
//			//кнопка
//			String str = null;
//			// определение строки
//			if (textArr != null) {
//				str = textArr[0];
//			}
//			if (str == null) {
//				str = text;
//			}
//			if (str == null) {
//				str = "...";
//			}
//			// определение размера строки
//			int j = 0;
//			if (font != null && str != null)
//				j = font.stringWidth(str);
//
//			if ((isLayoutExpand() && isLayoutAlignDefault()) || isLayoutCenter()) {
//				bx = (bw - j) / 2 - 2;
//				g.drawString(str, bx + 4, by + 1, 0);
//			} else if (isLayoutLeft() || isLayoutDefault()) {
//				g.drawString(str, bx + 4, by + 1, 0);
//			} else if (isLayoutRight()) {
//				//bx = (bw - j) - 8;
//				g.drawString(str, bx + 4, by + 1, 0);
//			}
//			if (j > 0) {
//				if (isLayoutExpand()) {
//					j = bounds[W] - 8;
//					bx = super.bounds[X];
//				}
//				int h = font.getHeight();
//				//очертания кнопки
//				int o = g.getColor();
//				g.setColor(0xababab);
//				bx = bx + 2;
//				int lx = bx + 2 + j + 1;
//				int ly = by + h + 3;
//				g.drawLine(bx + 1, ly, lx, ly);
//				g.drawLine(lx, ly, lx, by + 1);
//				g.setColor(o);
//				g.drawRect(bx, by, j + 4, h + 4);
//			}
//		} else {
//			for (int i = this.getcurPage(); i < this.textArr.length; ++i) {
//				int x = super.bounds[X] + 4;
//				String s = textArr[i];
//				int w = bounds[W];
//				int tw = font.stringWidth(s);
//				if (isLayoutCenter()) x = ((w - x) - tw) / 2 + x;
//				else if (isLayoutRight()) x = ((w - x) - tw) + x;
//				g.drawString(s, x, by + 2, 0);
//				if ((by += font.getHeight() + 4) > super.screen.bounds[H]) {
//					return;
//				}
//			}
//		}
	}

	protected void layout(Row row) {
		super.layout(row);
		final Font font = (this.font != null) ? this.font : Screen.font;
		final int screenWidth = screen.bounds[W] - 8;
		int availableWidth = Math.min(super.getPreferredWidth(), row.getAvailableWidth(screenWidth));
		int[] maxw = new int[1];
		this.textArr = c.textArr((hasLabel() && mode != BUTTON) ? (super.label + "\n" + this.text) : this.text, font, availableWidth, screenWidth, maxw);
		if (textArr.length == 1) maxTextWidth = maxw[0] + 4;
		else if (textArr.length == 0) maxTextWidth = 4;
		else {
			maxTextWidth = screenWidth + 4;
		}
		final int fh = font.getHeight() + 4;
		if (mode == BUTTON) {
			super.bounds[H] = fh + (hasLabel() ? Item.font.getHeight() + 4 : 0);
		} else {
			super.bounds[H] = fh * this.textArr.length;
		}
	}

	public int getMinimumWidth() {
		final Font font = (this.font != null) ? this.font : Screen.font;
		return font.stringWidth("...");
	}

	public int getMinimumHeight() {
		final Font font = (this.font != null) ? this.font : Screen.font;
		return (font.getHeight() + 4);
	}

	public int getPreferredWidth() {
		return isLayoutExpand() ? super.getPreferredWidth() - 1 : maxTextWidth;
	}

	boolean isFocusable() {
		return commands.size() > 0;
	}

	int getRowWidth(int row) {
		if (textArr == null) return 0;
		final Font font = (this.font != null) ? this.font : Screen.font;
		return font.stringWidth(textArr[row]);
	}
}
