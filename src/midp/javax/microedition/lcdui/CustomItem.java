package javax.microedition.lcdui;

import emulator.Emulator;
import emulator.KeyMapping;
import emulator.lcdui.c;

public abstract class CustomItem extends Item {
	protected static final int TRAVERSE_HORIZONTAL = 1;
	protected static final int TRAVERSE_VERTICAL = 2;
	protected static final int KEY_PRESS = 4;
	protected static final int KEY_RELEASE = 8;
	protected static final int KEY_REPEAT = 16;
	protected static final int POINTER_PRESS = 32;
	protected static final int POINTER_RELEASE = 64;
	protected static final int POINTER_DRAG = 128;
	protected static final int NONE = 0;
	private Image img;
	private Graphics g;
	int[] anIntArray429;
	private boolean wasHidden = true;
	private int lastWidth, lastHeight;

	protected CustomItem(final String s) {
		super(s);
		this.anIntArray429 = new int[4];
	}

	public int getGameAction(final int n) {
		int n2 = n;
		switch (n) {
			case 49: {
				n2 = 9;
				break;
			}
			case 51: {
				n2 = 10;
				break;
			}
			case 55: {
				n2 = 11;
				break;
			}
			case 57: {
				n2 = 12;
				break;
			}
			default: {
				if (n == KeyMapping.getArrowKeyFromDevice(1)) {
					n2 = 1;
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(6)) {
					n2 = 6;
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(2)) {
					n2 = 2;
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(5)) {
					n2 = 5;
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(8)) {
					n2 = 8;
					break;
				}
			}
		}
		return n2;
	}

	protected final int getInteractionModes() {
		return 255;
	}

	protected abstract int getMinContentWidth();

	protected abstract int getMinContentHeight();

	protected abstract int getPrefContentWidth(final int p0);

	protected abstract int getPrefContentHeight(final int p0);

	protected void sizeChanged(final int w, final int h) {
	}

	protected final void invalidate() {
		repaintForm();
	}

	protected abstract void paint(final Graphics p0, final int p1, final int p2);

	protected final void repaint() {
		repaintForm();
	}

	protected final void repaint(final int n, final int n2, final int n3, final int n4) {
		repaint();
	}

	protected boolean traverse(final int n, final int n2, final int n3, final int[] array) {
		return false;
	}

	protected void traverseOut() {
	}

	protected void keyPressed(final int n) {
		if (n == KeyMapping.getArrowKeyFromDevice(Canvas.FIRE)
				&& itemCommandListener != null && defaultCommand != null) {
			Emulator.getEventQueue().commandAction(defaultCommand, this);
		}
	}

	protected void keyReleased(final int n) {
	}

	protected void keyRepeated(final int n) {
	}

	protected void pointerPressed(final int n, final int n2) {
	}

	protected void pointerReleased(final int n, final int n2) {
	}

	protected void pointerDragged(final int n, final int n2) {
	}

	protected void showNotify() {
	}

	protected void hideNotify() {
	}

	void hidden() {
		super.hidden();
		if (img == null) return;
		g.dispose();
		g = null;
		img.dispose();
		img = null;
		try {
			wasHidden = true;
			hideNotify();
		} catch (Exception ignored) {}
	}

	void paint(final Graphics graphics, int x, int y, int w, int h) {
		super.paint(graphics, x, y, w, h);
		if (wasHidden) {
			try {
				showNotify();
			} catch (Exception ignored) {}
			wasHidden = false;
		}
		if (img == null) {
			img = Image.createImage(Emulator.getEmulator().getScreen().getWidth(), Emulator.getEmulator().getScreen().getHeight());
			g = this.img.getGraphics();
		}
		this.g.setColor(-1);
		this.g.fillRect(0, 0, super.screen.w, super.screen.h);
		this.g.setColor(0);
		final int n = x + 2;
		int n2 = y + 2;
		final int prefContentWidth = this.getPrefContentWidth(w);
		final int prefContentHeight = this.getPrefContentHeight(h);
		this.paint(this.g, prefContentWidth, prefContentHeight);
		if (super.labelArr != null && super.labelArr.length > 0) {
			graphics.setFont(Item.font);
			for (int i = 0; i < super.labelArr.length; ++i) {
				graphics.drawString(super.labelArr[i], x + 4, n2 + 2, 0);
				n2 += Item.font.getHeight() + 4;
			}
		}
		graphics.drawRegion(img, 0, 0,
				Math.min(img.getWidth(), Math.min(w, prefContentWidth)),
				Math.min(img.getHeight(), Math.min(h-n2+y, prefContentHeight)), 0,
				n, n2, 0);
	}

	void layout(Row row) {
		super.layout(row);
		int n = 0;
		int w = Math.min(row.getAvailableWidth(screen.bounds[W]), this.getPreferredWidth() - 8);
		if (hasLabel()) {
			super.labelArr = c.textArr(super.label, Item.font, w, w);
			n = (Item.font.getHeight() + 4) * super.labelArr.length;
		} else {
			super.labelArr = null;
		}
		int h = getPrefContentHeight(bounds[H]);
		bounds[H] = n + (h + 4);

		w = getPreferredWidth();
		if (lastWidth != w || lastHeight != h) {
			try {
				sizeChanged(w, h);
			} catch (Exception ignored) {}
			lastWidth = w;
			lastHeight = h;
		}
	}

	protected boolean callTraverse(final int n) {
		if (screen == null) return false;
		if (this.anIntArray429 == null) return false;
		return this.traverse(this.getGameAction(n), super.screen.w, super.screen.h, this.anIntArray429);
	}

	boolean isFocusable() {
		return true;
	}
}
