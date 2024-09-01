package javax.microedition.lcdui;

import emulator.lcdui.*;

public class ImageItem extends Item {
	public static final int LAYOUT_DEFAULT = 0;
	public static final int LAYOUT_LEFT = 1;
	public static final int LAYOUT_RIGHT = 2;
	public static final int LAYOUT_CENTER = 3;
	public static final int LAYOUT_NEWLINE_BEFORE = 256;
	public static final int LAYOUT_NEWLINE_AFTER = 512;
	private Image image;
	private String altText;
	private int appearanceMode;
	private int width;

	public ImageItem(final String s, final Image image, final int n, final String s2) {
		this(s, image, n, s2, 0);
	}

	public ImageItem(final String s, final Image anImage427, final int anInt178, final String aString25, final int anInt179) {
		super(s);
		if (anInt179 < 0 || anInt179 > 2) {
			throw new IllegalArgumentException();
		}
		this.image = anImage427;
		super.layout = anInt178;
		this.altText = aString25;
		this.appearanceMode = anInt179;
	}

	public Image getImage() {
		return this.image;
	}

	public void setImage(final Image anImage427) {
		this.image = anImage427;
		queueLayout();
	}

	public String getAltText() {
		return this.altText;
	}

	public void setAltText(final String aString25) {
		this.altText = aString25;
	}

	public int getLayout() {
		return super.getLayout();
	}

	public void setLayout(final int layout) {
		super.setLayout(layout);
	}

	public int getAppearanceMode() {
		return this.appearanceMode;
	}

	protected void paint(final Graphics g, int x, int y, int w, int h) {
		super.paint(g, x, y, w, h);
		if (super.labelArr != null && super.labelArr.length > 0) {
			g.setFont(Item.font);
			for (String s : super.labelArr) {
				g.drawString(s, x + 4, y + 2, 0);
				y += Item.font.getHeight() + 4;
			}
		}
		if (image != null) g.drawImage(this.image, x, y, 0);
	}

	protected void layout(Row row) {
		super.layout(row);
		int n = 0;
		final int n2 = row.getAvailableWidth(screen.bounds[W]);
		int[] tw = new int[1];
		if (super.label != null) {
			super.labelArr = c.textArr(super.label, Item.font, n2, n2, tw);
			n = (Item.font.getHeight() + 4) * super.labelArr.length;
		} else {
			super.labelArr = null;
		}
		int iw = 0;
		if (this.image != null) {
			iw = image.getWidth();
			n += this.image.getHeight() + 4;
		}
		width = Math.max(iw + 2, tw[0] + 5);
		super.bounds[H] = Math.min(n, super.screen.bounds[H]);
	}

	public int getPreferredWidth() {
		return width;
	}

	public int getMinimumWidth() {
		return Math.max(Item.font.stringWidth("..."), width);
	}

	public int getMinimumHeight() {
		return bounds[H];
	}

	boolean isFocusable() {
		return commands.size() > 0;
	}
}
