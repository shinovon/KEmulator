package javax.microedition.lcdui;

import java.util.*;

import emulator.*;
import emulator.lcdui.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;

public abstract class Screen extends Displayable {
	static final Font font;
	static final int fontHeight;
	static final int fontHeight4;
	final Vector items;
	private long lastPressTime;
	int scroll;

	Screen() {
		this("");
	}

	Screen(final String s) {
		super();
		super.title = ((s == null) ? "" : s);
		this.items = new Vector();
	}

	public void _invokeKeyPressed(final int n) {
		if (swtContent != null) return;
		final long currentTimeMillis;
		if ((currentTimeMillis = System.currentTimeMillis()) - this.lastPressTime < 100L) {
			return;
		}
		this.lastPressTime = currentTimeMillis;
		if (super.menuShown) {
			if (n >= 49 && n <= 57) {
				final int n2;
				if ((n2 = n - 49 + 1) < super.commands.size()) {
					super.cmdListener.commandAction((Command) super.commands.get(n2), this);
					super.menuShown = false;
				}
			} else if (n == KeyMapping.getArrowKeyFromDevice(Canvas.UP)) {
				if (super.anInt28 > 0) {
					--super.anInt28;
				}
			} else if (n == KeyMapping.getArrowKeyFromDevice(Canvas.DOWN)) {
				if (super.anInt28 < super.commands.size() - 2) {
					++super.anInt28;
				}
			} else {
				final int n3;
				if (n == KeyMapping.getArrowKeyFromDevice(Canvas.FIRE) && (n3 = super.anInt28 + 1) < super.commands.size()) {
					super.cmdListener.commandAction((Command) super.commands.get(n3), this);
					super.menuShown = false;
				}
			}
			this.repaintScreen();
			return;
		}
		if (focusedItem != null && n == KeyMapping.getArrowKeyFromDevice(Canvas.FIRE)) {
			focusedItem.itemApplyCommand();
			return;
		}
		if (n == KeyMapping.getArrowKeyFromDevice(Canvas.UP)) {
			keyScroll(Canvas.UP, false);
		} else if (n == KeyMapping.getArrowKeyFromDevice(Canvas.DOWN)) {
			keyScroll(Canvas.DOWN, false);
		} else if (n == KeyMapping.getArrowKeyFromDevice(Canvas.LEFT)) {
			keyScroll(Canvas.LEFT, false);
		} else if (n == KeyMapping.getArrowKeyFromDevice(Canvas.RIGHT)) {
			keyScroll(Canvas.RIGHT, false);
		}
	}

	public void invokeKeyRepeated(final int n) {
		if (swtContent != null) return;
		if (n == KeyMapping.getArrowKeyFromDevice(Canvas.UP)) {
			keyScroll(Canvas.UP, true);
		} else if (n == KeyMapping.getArrowKeyFromDevice(Canvas.DOWN)) {
			keyScroll(Canvas.DOWN, true);
		} else if (n == KeyMapping.getArrowKeyFromDevice(Canvas.LEFT)) {
			keyScroll(Canvas.LEFT, true);
		} else if (n == KeyMapping.getArrowKeyFromDevice(Canvas.RIGHT)) {
			keyScroll(Canvas.RIGHT, true);
		}
	}

	protected void keyScroll(int key, boolean repeat) {
	}

	public void _invokeKeyReleased(final int n) {
	}

	public boolean _invokePointerPressed(final int x, final int y) {
		if (swtContent != null) return false;
		if (super.menuShown) {
			final int n3 = super.w >> 1;
			final int anInt181 = Screen.fontHeight4;
			final int n5;
			final int n4 = (n5 = super.commands.size() - 1) * anInt181;
			final int n6 = n3 - 1;
			final int n7 = super.h - n4 - 1;
			final int[] array;
			if (BoundsUtils.collides(array = new int[]{n6, n7, n3, n4}, x, y)) {
				array[0] = n6;
				array[1] = n7;
				array[2] = n3;
				array[3] = anInt181;
				int[] array2;
				int n8;
				for (int i = 0; i < n5; ++i, array2 = array, n8 = 1, array2[n8] += anInt181) {
					if (BoundsUtils.collides(array, x, y)) {
						super.cmdListener.commandAction((Command) super.commands.get(i + 1), this);
						super.menuShown = false;
						return true;
					}
				}
			}
		}
		return false;
	}

	public void _invokePointerReleased(final int n, final int n2) {
	}

	public void _invokePointerDragged(final int n, final int n2) {
	}

	protected abstract void paint(final Graphics p0);

	public void _invokePaint(final Graphics graphics) {
		if (swtContent != null) return;
		Displayable._resetXRayGraphics();
		final int color = graphics.getColor();
		final int strokeStyle = graphics.getStrokeStyle();
		final Font font = graphics.getFont();
		graphics.setFont(Screen.font);
		graphics.setStrokeStyle(0);
		emulator.lcdui.a.method177(graphics, 0, 0, super.w, super.h, false);
		this.drawTitleBar(graphics);
		this.paint(graphics);
		this.drawScrollBar(graphics);
		this.paintTicker(graphics);
		this.paintSoftMenu(graphics);
		graphics.setColor(color);
		graphics.setFont(font);
		graphics.setStrokeStyle(strokeStyle);
	}

	protected void drawTitleBar(final Graphics graphics) {
		if (swtContent != null) return;
		String title = super.title;
		if (title == null)
			title = "";
		final int n;
		final String value = String.valueOf(n = ((focusedItem != null) ? (this.items.indexOf(focusedItem) + 1) : this.items.size()));
		final int n2 = (Screen.fontHeight4 >> 1) - 1;
		final int stringWidth = Screen.font.stringWidth(title);
		final int stringWidth2 = Screen.font.stringWidth(value);
		final int n3 = (super.w - stringWidth >> 1) + 2;
		final int n4 = super.w - stringWidth2 - 2;
		graphics.setColor(8617456);
		graphics.fillRect(2, n2, (super.w - stringWidth >> 1) - 2, 2);
		graphics.fillRect(n3 + stringWidth + 2, n2, n4 - n3 - stringWidth - 4, 2);
		graphics.setColor(-16777216);
		graphics.setFont(Screen.font);
		graphics.drawString(title, n3, 1, 0);
		graphics.drawString(value, n4, 1, 0);
	}

	protected void sizeChanged(final int w, final int h) {
		this.w = w;
		this.h = h;
		this.bounds = new int[]{0, Screen.fontHeight4, this.w - 4, this.h - Screen.fontHeight4};
	}

	protected void drawScrollBar(final Graphics graphics) {
		emulator.lcdui.a.method179(graphics, bounds[W] + 1, Screen.fontHeight4 - 1, 2, bounds[H] - 2, this.items.size(), (focusedItem != null) ? this.items.indexOf(focusedItem) : -1);
	}

	void swtUpdateMenuCommands(boolean item) {
		if (focusedItem == null || !item) {
			super.swtUpdateMenuCommands(false);
			return;
		}
		for (MenuItem mi: swtMenu.getItems()) {
			mi.dispose();
		}
		if (focusedItem instanceof ChoiceGroup && ((ChoiceGroup) focusedItem).choiceType == Choice.POPUP) {
			for (int i = 0; i < ((ChoiceGroup) focusedItem).items.size(); i++) {
				String s = ((ChoiceGroup) focusedItem).getString(i);
				MenuItem mi = new MenuItem(swtMenu, SWT.RADIO);
				mi.addSelectionListener(swtMenuSelectionListener);
				mi.setData(focusedItem);
				if (((ChoiceGroup) focusedItem).isSelected(i)) mi.setSelection(true);
				mi.setText(s);
			}
		} else {
			for (int i = 0; i < focusedItem.commands.size(); i++) {
				Command c = (Command) focusedItem.commands.get(i);
				MenuItem mi = new MenuItem(swtMenu, SWT.PUSH);
				mi.addSelectionListener(swtMenuSelectionListener);
				mi.setData(new Object[] {c, focusedItem});
				mi.setText(c.getLongLabel());
			}
		}
	}

	static {
		font = Font.getDefaultFont();
		fontHeight = Screen.font.getHeight();
		fontHeight4 = Screen.fontHeight + 4;
	}
}