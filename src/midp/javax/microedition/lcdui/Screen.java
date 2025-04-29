package javax.microedition.lcdui;

import emulator.Emulator;
import emulator.KeyMapping;
import emulator.lcdui.LCDUIUtils;
import emulator.lcdui.TextUtils;
import emulator.ui.IScreen;
import emulator.ui.swt.EmulatorScreen;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;

import java.util.Vector;

public abstract class Screen extends Displayable {
	static final Font font = Font.getDefaultFont();
	static final int fontHeight = font.getHeight();
	static final int fontHeight4 = fontHeight + 4;
	final Vector items;
	//	private long lastPressTime;
	int scroll;

	Composite swtContent;
	private Rectangle swtContentArea;
	private boolean swtInitialized;

	static KeyListener swtKeyListener = new SwtKeyListener();

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
//		final long currentTimeMillis;
//		if ((currentTimeMillis = System.currentTimeMillis()) - this.lastPressTime < 100L) {
//			return;
//		}
//		this.lastPressTime = currentTimeMillis;
		if (n == KeyMapping.getArrowKeyFromDevice(Canvas.UP)) {
			_keyScroll(Canvas.UP, false);
			return;
		}
		if (n == KeyMapping.getArrowKeyFromDevice(Canvas.DOWN)) {
			_keyScroll(Canvas.DOWN, false);
			return;
		}
		if (n == KeyMapping.getArrowKeyFromDevice(Canvas.LEFT)) {
			_keyScroll(Canvas.LEFT, false);
			return;
		}
		if (n == KeyMapping.getArrowKeyFromDevice(Canvas.RIGHT)) {
			_keyScroll(Canvas.RIGHT, false);
			return;
		}
		if (focusedItem != null && focusedItem instanceof CustomItem) {
			((CustomItem) focusedItem).keyPressed(n);
			return;
		}
		if (focusedItem != null && n == KeyMapping.getArrowKeyFromDevice(Canvas.FIRE)) {
			focusedItem._itemApplyCommand();
			return;
		}
	}

	public void _invokeKeyRepeated(final int n) {
		if (swtContent != null) return;
		if (n == KeyMapping.getArrowKeyFromDevice(Canvas.UP)) {
			_keyScroll(Canvas.UP, true);
			return;
		}
		if (n == KeyMapping.getArrowKeyFromDevice(Canvas.DOWN)) {
			_keyScroll(Canvas.DOWN, true);
			return;
		}
		if (n == KeyMapping.getArrowKeyFromDevice(Canvas.LEFT)) {
			_keyScroll(Canvas.LEFT, true);
			return;
		}
		if (n == KeyMapping.getArrowKeyFromDevice(Canvas.RIGHT)) {
			_keyScroll(Canvas.RIGHT, true);
			return;
		}
		if (focusedItem != null && focusedItem instanceof CustomItem) {
			((CustomItem) focusedItem).keyRepeated(n);
			return;
		}
	}

	protected void _keyScroll(int key, boolean repeat) {
	}

	public void _invokeKeyReleased(final int n) {
		if (n == KeyMapping.getArrowKeyFromDevice(Canvas.UP)
				|| n == KeyMapping.getArrowKeyFromDevice(Canvas.DOWN)
				|| n == KeyMapping.getArrowKeyFromDevice(Canvas.LEFT)
				|| n == KeyMapping.getArrowKeyFromDevice(Canvas.RIGHT)) {
			return;
		}
		if (focusedItem != null && focusedItem instanceof CustomItem) {
			((CustomItem) focusedItem).keyReleased(n);
			return;
		}
	}

	public boolean _invokePointerPressed(final int x, final int y) {
		if (swtContent != null) return false;
		return false;
	}

	public void _invokePointerReleased(final int n, final int n2) {
	}

	public void _invokePointerDragged(final int n, final int n2) {
	}

	protected abstract void _paint(final Graphics p0);

	public void _invokePaint(final Graphics graphics) {
		if (swtContent != null) return;
		Displayable._resetXRayGraphics();
		final int color = graphics.getColor();
		final int strokeStyle = graphics.getStrokeStyle();
		final Font font = graphics.getFont();
		graphics.setFont(Screen.font);
		graphics.setStrokeStyle(0);
		LCDUIUtils.drawDisplayableBackground(graphics, 0, 0, super.w, super.h, false);
		this._drawTitleBar(graphics);
		this._paint(graphics);
		this._drawScrollBar(graphics);
		this._paintTicker(graphics);
		this._paintSoftMenu(graphics);
		graphics.setColor(color);
		graphics.setFont(font);
		graphics.setStrokeStyle(strokeStyle);
	}

	protected void _drawTitleBar(final Graphics graphics) {
		if (swtContent != null) return;
		String title = super.title == null ? "" : super.title.trim();
		final int n;
		final String value = String.valueOf(n = ((focusedItem != null) ? (this.items.indexOf(focusedItem) + 1) : this.items.size()));
		final int n2 = (Screen.fontHeight4 >> 1) - 1;
		final int stringWidth2 = Screen.font.stringWidth(value);
		int w = super.w - stringWidth2 - 16 - Screen.font.stringWidth("...");
		if (w > 16) {
			String[] s = TextUtils.textArr(title, Screen.font, w, w);
			title = s.length == 0 ? "" : (s.length != 1 ? s[0] + "..." : s[0]);
		}
		final int stringWidth = Screen.font.stringWidth(title);
		final int n3 = (super.w - stringWidth >> 1) + 2;
		final int n4 = super.w - stringWidth2 - 2;
		graphics.setColor(8617456);
		graphics.fillRect(2, n2, (super.w - stringWidth >> 1) - 2, 2);
		graphics.fillRect(n3 + stringWidth + 2, n2, n4 - n3 - stringWidth - 4, 2);
		graphics.setColor(LCDUIUtils.foregroundColor);
		graphics.setFont(Screen.font);
		graphics.drawString(title, n3, 1, 0);
		graphics.drawString(value, n4, 1, 0);
	}

	protected void sizeChanged(final int w, final int h) {
	}

	void _invokeSizeChanged(int w, int h, boolean b) {
		if (swtContent != null) {
			swtContent.getDisplay().asyncExec(this::_swtUpdateSizes);
			Emulator.getEmulator().getScreen().repaint();
			return;
		}
		super._invokeSizeChanged(w, h, b);
	}

	public int getWidth() {
		if (swtContentArea != null) {
			return swtContentArea.width;
		}
		return bounds[W];
	}

	public int getHeight() {
		if (swtContentArea != null) {
			return swtContentArea.height;
		}
		return bounds[H];
	}

	protected void _drawScrollBar(final Graphics graphics) {
		LCDUIUtils.drawScrollbar(graphics, bounds[W] + 1, Screen.fontHeight4 - 1, 2, bounds[H] - 2, this.items.size(), (focusedItem != null) ? this.items.indexOf(focusedItem) : -1);
	}

	public int _repaintInterval() {
		return -1;
	}

	Composite _constructSwtContent(int style) {
		Composite c = new Composite(getSwtParent(), SWT.NONE);
		_setSwtStyle(c);
		return c;
	}

	void constructSwt() {
		syncExec(new Runnable() {
			public void run() {
				swtContent = _constructSwtContent(SWT.NONE);
				swtContent.setVisible(false);
				swtContentArea = _layoutSwtContent();
			}
		});
	}

	Rectangle _layoutSwtContent() {
		Rectangle area = getSwtParent().getClientArea();
		swtContent.setBounds(0, 0, area.width, area.height);
		return swtContent.getClientArea();
	}

	public Composite _getSwtContent() {
		return swtContent;
	}

	protected void finalize() throws Throwable {
		syncExec(() -> {
			if (swtContent != null && !swtContent.isDisposed()) {
				swtContent.dispose();
			}
		});
		super.finalize();
	}

	static Composite getSwtParent() {
		return ((EmulatorScreen) Emulator.getEmulator().getScreen()).getCanvas();
	}

	public void _swtHidden() {
	}

	public void _swtShown() {
		if (swtContent != null && !swtContent.isDisposed()) {
			_swtUpdateSizes();
		}
	}

	public void _swtUpdateSizes() {
		Rectangle newArea = _layoutSwtContent();
		if (swtContentArea == null || !swtInitialized
				|| newArea.width != swtContentArea.width
				|| newArea.height != swtContentArea.height) {
			swtInitialized = true;
			swtContentArea = newArea;
			_swtResized(newArea.width, newArea.height);
		}
	}

	public void _swtResized(int w, int h) {
		if (this instanceof List) return;
		swtContent.setFont(Font.getDefaultSWTFont(true));
	}

	void _setSwtStyle(Control c) {
		int color = LCDUIUtils.backgroundColor;
		c.setBackground(new Color(null, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF));
		color = LCDUIUtils.foregroundColor;
		c.setForeground(new Color(null, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF));

		c.setFont(Font.getDefaultSWTFont(!(this instanceof List)));
	}

	static class SwtKeyListener implements KeyListener {

		public void keyPressed(KeyEvent keyEvent) {
			if (keyEvent.keyCode == SWT.F11 || keyEvent.keyCode == 16777261 || keyEvent.keyCode == 16777259) {
				((EmulatorScreen) Emulator.getEmulator().getScreen()).keyPressed(keyEvent);
				return;
			}
			int n = keyEvent.keyCode & 0xFEFFFFFF;
			Displayable d = Emulator.getCurrentDisplay().getCurrent();
			String r;
			if ((keyEvent.character >= 33 && keyEvent.character <= 90)
					|| (r = KeyMapping.replaceKey(n)) == null) return;
			n = Integer.parseInt(r);
			if (KeyMapping.isLeftSoft(n) || KeyMapping.isRightSoft(n)) {
				d.handleSoftKeyAction(n, true);
			}
		}

		public void keyReleased(KeyEvent keyEvent) {
		}
	}
}