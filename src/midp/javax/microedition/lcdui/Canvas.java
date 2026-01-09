package javax.microedition.lcdui;

import com.nokia.mid.ui.CanvasGraphicsItem;
import com.nokia.mid.ui.CanvasItem;
import com.nokia.mid.ui.TextEditor;
import emulator.AppSettings;
import emulator.Emulator;
import emulator.KeyMapping;
import emulator.Settings;
import emulator.graphics2D.IImage;
import emulator.ui.IScreen;

import java.util.ArrayList;

public abstract class Canvas extends Displayable {
	public static final int UP = 1;
	public static final int DOWN = 6;
	public static final int LEFT = 2;
	public static final int RIGHT = 5;
	public static final int FIRE = 8;
	public static final int GAME_A = 9;
	public static final int GAME_B = 10;
	public static final int GAME_C = 11;
	public static final int GAME_D = 12;
	public static final int KEY_NUM0 = 48;
	public static final int KEY_NUM1 = 49;
	public static final int KEY_NUM2 = 50;
	public static final int KEY_NUM3 = 51;
	public static final int KEY_NUM4 = 52;
	public static final int KEY_NUM5 = 53;
	public static final int KEY_NUM6 = 54;
	public static final int KEY_NUM7 = 55;
	public static final int KEY_NUM8 = 56;
	public static final int KEY_NUM9 = 57;
	public static final int KEY_STAR = 42;
	public static final int KEY_POUND = 35;
	protected int m_keyStates;
	private Graphics graphics;
	private int vKeyStates;
	private final ArrayList<CanvasItem> nokiaCanvasItems;
	private CanvasItem tappedCanvasItem;

	protected Canvas() {
		super();
		this.setFullScreenMode(false);
		nokiaCanvasItems = new ArrayList<CanvasItem>();
	}

	public void _invokePaint(IImage buffer, IImage xray) {
		if (!AppSettings.xrayView) xray = null;
		if (graphics == null) {
			graphics = new Graphics(buffer, xray);
		}
		graphics._reset(buffer, xray);
		this.paint(graphics);
		graphics._reset(buffer, xray); // paintTicker fix
		this._paintOverlay(graphics);
	}

	public void _invokePaint(IImage buffer, IImage xray, int x, int y, int w, int h) {
		if (!AppSettings.xrayView) xray = null;
		if (graphics == null) {
			graphics = new Graphics(buffer, xray);
		}
		graphics._reset(buffer, xray);
		graphics.setClip(x, y, w, h);
		this.paint(graphics);
		graphics._reset(buffer, xray); // paintTicker fix
		this._paintOverlay(graphics);
	}

	protected void _paintOverlay(Graphics graphics) {
		super._paintTicker(graphics);
		super._paintSoftMenu(graphics);
		for (CanvasItem i: nokiaCanvasItems) {
			if (!i.isVisible()) continue;
			// TODO z-position
			if (i instanceof CanvasGraphicsItem) {
				graphics.setClip(i.getPositionX(), i.getPositionY(), i.getWidth(), i.getHeight());
				graphics.translate(-i.getPositionX(), -i.getPositionY());
				((CanvasGraphicsItem) i)._invokePaint(graphics);
				graphics._reset();
				continue;
			}
			if (i instanceof TextEditor) {
				((TextEditor) i)._invokePaint(graphics);
			}
		}
	}

	public void _invokeKeyReleased(final int n) {
		int i = 1 << this.getGameAction(n);
		if ((m_keyStates & i) != 0)
			m_keyStates &= ~i;

		i = getKeyBit(n);
		if ((vKeyStates & i) != 0)
			vKeyStates &= ~i;

		this.keyReleased(n);
	}

	public void _invokeKeyPressed(final int n) {
		this.m_keyStates |= 1 << this.getGameAction(n);
		this.vKeyStates |= getKeyBit(n);
		this.keyPressed(n);
	}

	public void _invokeKeyRepeated(final int n) {
		this.keyRepeated(n);
	}

	public void _invokeHideNotify() {
		this.hideNotify();
	}

	public void _invokeShowNotify() {
		this.showNotify();
		IScreen s = Emulator.getEmulator().getScreen();
		if (this.w != s.getWidth() || this.h != getActualHeight()) {
			_invokeSizeChanged(s.getWidth(), s.getHeight());
		}
	}

	int getActualHeight() {
		int h = Emulator.getEmulator().getScreen().getHeight();
		if (!fullScreen) {
			h -= (ticker == null ? Screen.fontHeight4 : Screen.fontHeight4 * 2);
		}
		return h;
	}

	public void _invokeSizeChanged(int w, int h) {
		if (!fullScreen) {
			h -= (ticker == null ? Screen.fontHeight4 : Screen.fontHeight4 * 2);
		}
		if (this.w != w || this.h != h || forceUpdateSize) {
			this.w = bounds[W] = w;
			this.h = bounds[H] = h;
			sizeChanged(w, h);
			if (!Settings.dontRepaintOnSetCurrent) repaint();
		}
	}

	protected abstract void paint(final Graphics g);

	protected void keyReleased(final int n) {
	}

	protected void keyPressed(final int n) {
	}

	protected void keyRepeated(final int n) {
	}

	public void repaint() {
		repaint(0, 0, w, h);
	}

	public void repaint(final int x, final int y, final int w, final int h) {
		if (this != Emulator.getCurrentDisplay().getCurrent()) {
			return;
		}
		Emulator.getEventQueue().queueRepaint(x, y, w, h);
	}

	public void serviceRepaints() {
		if (this != Emulator.getCurrentDisplay().getCurrent()) {
			return;
		}
		Emulator.getEventQueue().serviceRepaints();
	}

	protected void hideNotify() {
	}

	protected void showNotify() {
	}

	public int getWidth() {
		return w;
	}

	public void setFullScreenMode(final boolean b) {
		if (!AppSettings.ignoreFullScreen) {
			if (b == fullScreen) return;
			fullScreen = b;
			updateSize(true);
		} else if (!fullScreen) {
			fullScreen = true;
			updateSize(true);
		}
	}

	protected void sizeChanged(final int n, final int n2) {
	}

	protected void pointerPressed(final int n, final int n2) {
	}

	protected void pointerReleased(final int n, final int n2) {
	}

	protected void pointerDragged(final int n, final int n2) {
	}

	public void invokePointerPressed(final int x, final int y) {
		CanvasItem i = getCanvasItemAt(x, y);
		if (i != null) {
			tappedCanvasItem = i;
			if (i instanceof TextEditor) {
				((TextEditor) i).setFocus(true);
			}
			return;
		}
		this.pointerPressed(x, y);
	}

	public void invokePointerReleased(final int x, final int y) {
		if (tappedCanvasItem != null) {
			tappedCanvasItem = null;
			return;
		}
		this.pointerReleased(x, y);
	}

	public void invokePointerDragged(final int x, final int y) {
		if (tappedCanvasItem != null) {
			return;
		}
		this.pointerDragged(x, y);
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
				if (n == KeyMapping.getArrowKeyFromDevice(UP) || n == 50) {
					n3 = 1;
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(DOWN) || n == 56) {
					n3 = 6;
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(LEFT) || n == 52) {
					n3 = 2;
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(RIGHT) || n == 54) {
					n3 = 5;
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(FIRE) || n == 53) {
					n3 = 8;
					break;
				}
				return n2;
			}
		}
		n2 = n3;
		return n2;
	}

	public int getKeyCode(final int n) {
		int r = 0;
		switch (n) {
			case 1: {
				r = KeyMapping.getArrowKeyFromDevice(UP);
				break;
			}
			case 6: {
				r = KeyMapping.getArrowKeyFromDevice(DOWN);
				break;
			}
			case 2: {
				r = KeyMapping.getArrowKeyFromDevice(LEFT);
				break;
			}
			case 5: {
				r = KeyMapping.getArrowKeyFromDevice(RIGHT);
				break;
			}
			case 8: {
				r = KeyMapping.getArrowKeyFromDevice(FIRE);
				break;
			}
			case 9: {
				r = KEY_NUM1;
				break;
			}
			case 10: {
				r = KEY_NUM3;
				break;
			}
			case 11: {
				r = KEY_NUM7;
				break;
			}
			case 12: {
				r = KEY_NUM9;
				break;
			}
			default:
				break;
		}
		return r;
	}

	public String getKeyName(final int n) {
		if (n == 8) return "Backspace";
		if (n == 32) return "Space";
		if (n >= 32 && n <= 126) {
			return String.valueOf((char) n);
		}
		String s = "";
		switch (n) {
			case KEY_NUM0: {
				s = "0";
				break;
			}
			case KEY_NUM1: {
				s = "1";
				break;
			}
			case KEY_NUM2: {
				s = "2";
				break;
			}
			case KEY_NUM3: {
				s = "3";
				break;
			}
			case KEY_NUM4: {
				s = "4";
				break;
			}
			case KEY_NUM5: {
				s = "5";
				break;
			}
			case KEY_NUM6: {
				s = "6";
				break;
			}
			case KEY_NUM7: {
				s = "7";
				break;
			}
			case KEY_NUM8: {
				s = "8";
				break;
			}
			case KEY_NUM9: {
				s = "9";
				break;
			}
			case KEY_STAR: {
				s = "*";
				break;
			}
			case KEY_POUND: {
				s = "#";
				break;
			}
			default: {
				if (n == KeyMapping.getArrowKeyFromDevice(UP)) {
					s = "Up";
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(DOWN)) {
					s = "Down";
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(LEFT)) {
					s = "Left";
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(RIGHT)) {
					s = "Right";
					break;
				}
				if (n == KeyMapping.getArrowKeyFromDevice(FIRE)) {
					s = "Select";
					break;
				}
				return s;
			}
		}
		return s;
	}

	public boolean hasPointerEvents() {
		return AppSettings.hasPointerEvents;
	}

	public boolean hasPointerMotionEvents() {
		return AppSettings.hasPointerEvents;
	}

	public boolean hasRepeatEvents() {
		return AppSettings.enableKeyRepeat;
	}

	public boolean isDoubleBuffered() {
		return true;
	}

	// Copyright Yury Kharchenko
	private int getKeyBit(int vKey) {
		switch (getGameAction(vKey)) {
			case UP:
				return 1 << 12; // 12 Up
			case LEFT:
				return 1 << 13; // 13 Left
			case RIGHT:
				return 1 << 14; // 14 Right
			case DOWN:
				return 1 << 15; // 15 Down
			case FIRE:
				return 1 << 16; // 16 Select
			case GAME_C:
				return 1 << 19; // 19 Softkey 3
		}
		switch (vKey) {
			case KEY_NUM0:
				return 1; //  0 0
			case KEY_NUM1:
				return 1 << 1; //  1 1
			case KEY_NUM2:
				return 1 << 2; //  2 2
			case KEY_NUM3:
				return 1 << 3; //  3 3
			case KEY_NUM4:
				return 1 << 4; //  4 4
			case KEY_NUM5:
				return 1 << 5; //  5 5
			case KEY_NUM6:
				return 1 << 6; //  6 6
			case KEY_NUM7:
				return 1 << 7; //  7 7
			case KEY_NUM8:
				return 1 << 8; //  8 8
			case KEY_NUM9:
				return 1 << 9; //  9 9
			case KEY_STAR:
				return 1 << 10; // 10 *
			case KEY_POUND:
				return 1 << 11; // 11 #
			default:
				if (KeyMapping.isLeftSoft(vKey))
					return 1 << 17; // 17 Softkey 1
				if (KeyMapping.isRightSoft(vKey))
					return 1 << 18; // 18 Softkey 2
				return 0;
		}
	}

	public int _getKeyStatesVodafone() {
		return vKeyStates;
	}

	public void _removeNokiaCanvasItem(CanvasItem i) {
		nokiaCanvasItems.remove(i);
	}

	public void _addNokiaCanvasItem(CanvasItem i) {
		nokiaCanvasItems.add(i);
	}

	private CanvasItem getCanvasItemAt(int x, int y) {
		if (nokiaCanvasItems.isEmpty()) return null;
		for (CanvasItem i : nokiaCanvasItems) {
			if (!i.isVisible()) continue;
			int ix = i.getPositionX();
			int iy = i.getPositionY();
			if (x >= ix && x <= ix + i.getWidth() && y >= iy && y <= iy + i.getHeight())
				return i;
		}
		return null;
	}

	void _defocus() {
		super._defocus();
		if (nokiaCanvasItems.isEmpty()) return;
		for (CanvasItem i : nokiaCanvasItems) {
			if (!i.isVisible()) continue;
			if (i instanceof TextEditor)
				((TextEditor) i).setFocus(false);
		}
	}
}