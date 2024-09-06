package javax.microedition.lcdui;

import emulator.lcdui.*;

/**
 * ChoiceGroupItem
 */
final class a {
	boolean sel;
	boolean aBoolean424;
	String string;
	Image image;
	Font font;
	int[] bounds;
	ChoiceGroup choice;
	String[] str;

	a(final String aString418, final Image anImage419, final Font aFont420, final ChoiceGroup aChoiceGroup422) {
		super();
		this.string = aString418;
		this.image = anImage419;
		this.font = aFont420;
		this.choice = aChoiceGroup422;
		this.bounds = new int[4];
		this.aBoolean424 = true;
	}

	protected final void method211(final Graphics graphics, final boolean b, int y) {
		graphics.setColor(-16777216);
		final boolean b2 = this.choice.choiceType == Choice.POPUP;
		int anInt28 = y;
		int anInt29 = b2 ? choice.popupY : bounds[1];
		int yo = anInt28 + anInt29;
		final int x = this.bounds[0] + 4;
		if (b) {
			emulator.lcdui.a.method178(graphics, this.bounds[0] + 1, yo - 2, this.bounds[2] - 2, this.bounds[3]);
		}
		emulator.lcdui.a.method180(graphics, x, yo + 3, this.sel, this.choice.choiceType);
		final Font font = (this.font != null) ? this.font : Screen.font;
		graphics.setFont(font);
		if (image != null) {
			graphics.drawRegion(image, 0, 0,
					Math.min(image.getWidth(), 16), Math.min(image.getHeight(), 16), 0,
					x + 16, yo + 2, 0);
		}
		if (this.str != null)
			for (int i = 0; i < this.str.length; ++i) {
				graphics.drawString(this.str[i],
						((i == 0 && this.choice.choiceType != 3) ? (x + 10) : x) + 4
								+ (i == 0 && image != null ? 20 : 0), yo, 0);
				yo += font.getHeight() + 4;
			}
	}

	protected final void method212() {
		this.bounds[0] = 0;
		this.bounds[1] = 0;
		this.bounds[2] = this.choice.bounds[2];
		final int n2 = this.choice.getPreferredWidth() - 8;
		final int n = n2 - 12 - (image != null ? 20 : 0);
		final Font font = (this.font != null) ? this.font : Screen.font;
		this.str = c.textArr(this.string, font, n, n2);
		this.bounds[3] = (font.getHeight() + 4) * this.str.length;
	}

	public String toString() {
		return string;
	}
}
