package javax.microedition.lcdui;

import emulator.*;
import emulator.lcdui.*;

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

	protected CustomItem(final String s) {
		super(s);
		this.anIntArray429 = new int[4];
	}

	public int getGameAction(final int n) {
		int n2 = 0;
		int n3 = 0;
		switch (n) {
			case 49: {
				n3 = 9;
				break;
			}
			case 51: {
				n3 = 10;
				break;
			}
			case 55: {
				n3 = 11;
				break;
			}
			case 57: {
				n3 = 12;
				break;
			}
			default: {
				if (n == KeyMapping.getArrowKeyFromDevice(1)) {
					n3 = 1;
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(6)) {
					n3 = 6;
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(2)) {
					n3 = 2;
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(5)) {
					n3 = 5;
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(8)) {
					n3 = 8;
					break;
				}
				return n2;
			}
		}
		n2 = n3;
		return n2;
	}

	protected final int getInteractionModes() {
		return 255;
	}

	protected abstract int getMinContentWidth();

	protected abstract int getMinContentHeight();

	protected abstract int getPrefContentWidth(final int p0);

	protected abstract int getPrefContentHeight(final int p0);

	protected void sizeChanged(final int n, final int n2) {
	}

	protected final void invalidate() {
	}

	protected abstract void paint(final Graphics p0, final int p1, final int p2);

	protected final void repaint() {
	}

	protected final void repaint(final int n, final int n2, final int n3, final int n4) {
	}

	protected boolean traverse(final int n, final int n2, final int n3, final int[] array) {
		return false;
	}

	protected void traverseOut() {
	}

	protected void keyPressed(final int n) {
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

	void updateHidden() {
		if (img == null) return;
		g.dispose();
		g = null;
		img.dispose();
		img = null;
	}

	protected void paint(final Graphics graphics, int x, int y, int w, int h) {
		if (img == null) {
			img = Image.createImage(Emulator.getEmulator().getScreen().getWidth(), Emulator.getEmulator().getScreen().getHeight());
			g = this.img.getGraphics();
		}
		this.g.setColor(-1);
		this.g.fillRect(0, 0, super.screen.w, super.screen.h);
		this.g.setColor(0);
		final int n = super.bounds[0] + 2;
		int n2 = super.bounds[1] + 2;
		final int prefContentWidth = this.getPrefContentWidth(super.bounds[2]);
		final int prefContentHeight = this.getPrefContentHeight(super.bounds[3]);
		this.paint(this.g, prefContentWidth, prefContentHeight);
		super.paint(g, x, y, w, h);
		if (super.labelArr != null && super.labelArr.length > 0) {
			graphics.setFont(Item.font);
			for (int i = 0; i < super.labelArr.length; ++i) {
				graphics.drawString(super.labelArr[i], super.bounds[0] + 4, n2 + 2, 0);
				n2 += Item.font.getHeight() + 4;
			}
		}
		graphics.setClip(n, n2, prefContentWidth, prefContentHeight);
		graphics.drawImage(this.img, n, n2, 0);
		graphics.setClip(0, 0, super.screen.w, super.screen.h);
	}

	protected void layout(Row row) {
		super.layout(row);
		int n = 0;
		final int n2 = Math.min(row.getAvailableWidth(screen.bounds[W]), this.getPreferredWidth() - 8);
		if (super.label != null) {
			super.labelArr = c.textArr(super.label, Item.font, n2, n2);
			n = (Item.font.getHeight() + 4) * super.labelArr.length;
		} else {
			super.labelArr = null;
		}
		super.bounds[H] = Math.min(n + (this.getPrefContentHeight(super.bounds[H]) + 4), super.screen.bounds[H]);
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
