package javax.microedition.lcdui;

import java.util.*;

import emulator.media.capture.CapturePlayerImpl;

import emulator.*;
import emulator.debug.*;
import emulator.lcdui.a;
import emulator.ui.IScreen;
import emulator.ui.swt.EmulatorImpl;
import emulator.ui.swt.EmulatorScreen;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class Displayable {
	public static final int X = 0;
	public static final int Y = 1;
	public static final int W = 2;
	public static final int H = 3;
	String title;
	Vector commands;
	boolean menuShown;
	CommandListener cmdListener;
	Item focusedItem;
	int w;
	int h;
	int[] bounds;
	Ticker ticker;
	int tickerX;
	private static long lastFrameTime;
	private static long lastFpsUpdateTime;
	private static int framesCount;
	boolean fullScreen;
	private static final long MILLI_TO_NANO = 1000000L;

	Composite swtContent;
	private Rectangle swtContentArea;
	private boolean swtInitialized;
	Menu swtMenu;
	SelectionListener swtMenuSelectionListener = new SwtMenuSelectionListener();
	private MenuListener swtMenuListener = new SwtMenuListener();
	boolean forceUpdateSize;

	public Displayable() {
		super();
		this.focusedItem = null;
		this.cmdListener = null;
		this.commands = new Vector();
		IScreen s = Emulator.getEmulator().getScreen();
		this.w = s.getWidth();
		this.h = s.getHeight();
		this.bounds = new int[]{0, Screen.fontHeight4, this.w - 4, this.h - Screen.fontHeight4};
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

	public String getTitle() {
		return this.title;
	}

	public boolean isShown() {
		return Display.current == this;
	}

	protected void defocus() {
		if (this.focusedItem != null) {
			this.focusedItem.defocus();
			this.focusedItem = null;
		}
	}

	protected void setItemCommands(final Item item) {
		this.focusedItem = item;
		if (item.commands.size() > 0) {
			for (int i = 0; i < item.commands.size(); ++i) {
				this.addCommand((Command) item.commands.get(i));
			}
		}
	}

	protected void removeItemCommands(final Item item) {
		if (item == null) {
			return;
		}
		if (item.commands.size() > 0) {
			for (int i = 0; i < item.commands.size(); ++i) {
				this.removeCommand((Command) item.commands.get(i));
			}
		}
		this.focusedItem = null;
	}

	protected void updateCommands() {
		final String commandLeft = (this.commands.size() > 0) ? this.getLeftSoftCommand().getLongLabel() : "";
		final String commandRight = (this.commands.size() > 2) ? "Menu"
				: ((this.commands.size() < 2) ? "" : this.getRightSoftCommand().getLongLabel());
		Emulator.getEmulator().getScreen().setCommandLeft(commandLeft);
		Emulator.getEmulator().getScreen().setCommandRight(commandRight);
	}

	protected boolean isCommandsEmpty() {
		return this.commands.isEmpty();
	}

	public void addCommand(final Command command) {
		if (command == null || this.commands.contains(command)) {
			return;
		}
		int i;
		for (i = 0; i < this.commands.size(); ++i) {
			if (command.getCommandType() == 7) {
				break;
			}
			final Command command2;
			if ((command2 = (Command) this.commands.get(i)).getCommandType() != 7) {
				if (command.getCommandType() > command2.getCommandType()) {
					break;
				}
				if (command.getCommandType() == command2.getCommandType()
						&& command.getPriority() < command2.getPriority()) {
					break;
				}
			}
		}
		this.commands.add(i, command);
		if (Emulator.getCurrentDisplay().getCurrent() == this) {
			this.updateCommands();
		}
	}

	public void removeCommand(final Command command) {
		if (this.commands.contains(command)) {
			this.commands.remove(command);
			if (Emulator.getCurrentDisplay().getCurrent() == this) {
				updateCommands();
			}
		}
	}

	protected Command getLeftSoftCommand() {
		if (this.commands.size() > 0) {
			return (Command) this.commands.get(0);
		}
		return null;
	}

	protected Command getRightSoftCommand() {
		if (this.commands.size() > 1) {
			return (Command) this.commands.get(1);
		}
		return null;
	}

	public boolean handleSoftKeyAction(final int n, final boolean b) {
		if (this.cmdListener == null && this instanceof Canvas) {
			return false;
		}
		if (KeyMapping.isLeftSoft(n)) {
			final Command leftSoftCommand = this.getLeftSoftCommand();
			if (b && leftSoftCommand != null) {
				Emulator.getEmulator().getLogStream().println("Left command: " + leftSoftCommand);
				if (this instanceof Alert && leftSoftCommand == Alert.DISMISS_COMMAND) {
					if (cmdListener != null) {
						Emulator.getEventQueue().commandAction(leftSoftCommand, this);
					} else ((Alert) this).close();
				} else if (this.focusedItem != null && this.focusedItem.commands.contains(leftSoftCommand)) {
					Emulator.getEventQueue().commandAction(leftSoftCommand, this.focusedItem);
				} else if (this.cmdListener != null) {
					Emulator.getEventQueue().commandAction(leftSoftCommand, this);
				}
			}
			return !Settings.motorolaSoftKeyFix;
		}
		if (KeyMapping.isRightSoft(n)) {
			if (this.commands.size() > 2) {
				if (b && this.menuShown) {
					this.menuShown = false;
					if (swtMenu != null) {
						hideSwtMenu();
					} else {
						this.repaintScreen();
					}
				} else if (b) {
					this.menuShown = true;
					if (swtMenu != null) {
						showSwtMenu(false, -1, -1);
					} else {
						repaintScreen();
					}
				}
			} else {
				final Command rightSoftCommand = this.getRightSoftCommand();
				if (b && rightSoftCommand != null) {
					Emulator.getEmulator().getLogStream().println("Right command: " + rightSoftCommand);
					if (this instanceof Alert && rightSoftCommand == Alert.DISMISS_COMMAND) {
						if (this.cmdListener != null) {
							Emulator.getEventQueue().commandAction(rightSoftCommand, this);
						} else ((Alert) this).close();
					} else if (this.focusedItem != null && this.focusedItem.commands.contains(rightSoftCommand)) {
						Emulator.getEventQueue().commandAction(rightSoftCommand, this.focusedItem);
					} else if (this.cmdListener != null) {
						Emulator.getEventQueue().commandAction(rightSoftCommand, this);
					}
				}
			}
			return !Settings.motorolaSoftKeyFix;
		}
		return false;
	}

	public void _callCommandAction(Command command) {
		if (cmdListener != null && command != null) {
			cmdListener.commandAction(command, this);
		}
	}

	public void setCommandListener(final CommandListener listener) {
		this.cmdListener = listener;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	protected void sizeChanged(final int n, final int n2) {
	}

	public void _invokeSizeChanged(int w, int h) {
		IScreen s = Emulator.getEmulator().getScreen();
		if (swtContent != null) {
			asyncExec(new Runnable() {
				public void run() {
					swtUpdateSizes();
				}
			});
			s.repaint();
			return;
		}
		if (this.w != w || this.h != h || forceUpdateSize) {
			forceUpdateSize = false;
			this.w = w;
			this.h = h;
			if (!fullScreen) {
				h -= (ticker == null ? Screen.fontHeight4 : Screen.fontHeight4 * 2);
			}
			sizeChanged(bounds[W] = this.w - 4, bounds[H] = h);
		}
		repaintScreen();
	}

	public Ticker getTicker() {
		return this.ticker;
	}

	public void setTicker(final Ticker ticker) {
		this.ticker = ticker;
		this.tickerX = this.w;
		updateSize(true);
	}

	protected void _paintTicker(final Graphics graphics) {
		if (ticker == null) {
			if (!fullScreen && this instanceof Canvas) {
				graphics.setColor(-1);
				graphics.fillRect(0, this.bounds[H], w, Screen.fontHeight4);
			}
			return;
		}
		a.method181(graphics, 0, Screen.fontHeight4 + this.bounds[H] - 1, this.w, Screen.fontHeight4);
		graphics.setFont(Screen.font);
		graphics.drawString(this.ticker.getString(), this.tickerX, Screen.fontHeight4 + this.bounds[H] - 1 + 2, 0);
		this.tickerX -= 5;
		if (this.tickerX < -Screen.font.stringWidth(this.ticker.getString())) {
			this.tickerX = this.w;
		}
	}

	void repaintScreen() {
		int n;
		if (this instanceof Canvas) {
			n = EventQueue.EVENT_PAINT;
		} else {
			if (!(this instanceof Screen)) {
				return;
			}
			n = EventQueue.EVENT_SCREEN;
		}
		Emulator.getEventQueue().queue(n);
	}

	protected void _paintSoftMenu(final Graphics graphics) {
		CapturePlayerImpl.draw(graphics, Emulator.getCurrentDisplay().getCurrent());
	}

	public static void _fpsLimiter() {
		if (Settings.speedModifier == 1 && Settings.frameRate <= 120) {
			long elapsed = System.nanoTime() - lastFrameTime;
			long var2 = (long) ((MILLI_TO_NANO * 1000) / Settings.frameRate);

			long delta = var2 - elapsed;
			if (delta > 0) {
				try {
					Thread.sleep(delta / MILLI_TO_NANO/*, (int) (delta % MILLI_TO_NANO)*/);
				} catch (Exception ignored) {}
			}
		}
		lastFrameTime = System.nanoTime();

		++framesCount;
		long l = lastFrameTime - lastFpsUpdateTime;
		if (l >= 2000L * MILLI_TO_NANO) {
			Profiler.FPS = (int) (((framesCount * 1000L + 500) * MILLI_TO_NANO) / l);
			lastFpsUpdateTime = lastFrameTime;
			framesCount = 0;
		}
	}

	public static void _checkForSteps(Object lock) {
		if (Settings.steps >= 0) {
			if (Settings.steps == 0) {
				final long currentTimeMillis = System.currentTimeMillis();
				try {
					while (Settings.steps == 0) {
						if (lock == null) Thread.sleep(50);
						else synchronized (lock) {
							lock.wait(50L);
						}
					}
				} catch (Exception ignored) {}
				Settings.aLong1235 += System.currentTimeMillis() - currentTimeMillis;
			}
			--Settings.steps;
		}
	}

	public static void _resetXRayGraphics() {
		Graphics.resetXRayCache();
	}


	void constructSwt() {
		syncExec(new Runnable() {
			public void run() {
				swtContent = _constructSwtContent(SWT.NONE);
				swtMenu = new Menu(swtContent);
				swtContent.setMenu(swtMenu);
				swtContentArea = _layoutSwtContent();
			}
		});
	}

	protected Composite _constructSwtContent(int style) {
		return new Composite(getSwtParent(), SWT.NONE);
	}

	protected Rectangle _layoutSwtContent() {
		Rectangle area = getSwtParent().getClientArea();
		swtContent.setBounds(0, 0, area.width, area.height);
		return swtContent.getClientArea();
	}

	public Composite _getSwtContent() {
		return swtContent;
	}

	protected void finalize() throws Throwable {
		syncExec(new Runnable() {
			public void run() {
				if (swtContent != null && !swtContent.isDisposed()) {
					swtContent.dispose();
				}
			}
		});
		super.finalize();
	}

	static Composite getSwtParent() {
		return ((EmulatorScreen) Emulator.getEmulator().getScreen()).getCanvas();
	}

	static void syncExec(Runnable r) {
		EmulatorImpl.syncExec(r);
	}

	static void asyncExec(Runnable r) {
		EmulatorImpl.asyncExec(r);
	}

	static void safeSyncExec(Runnable r) {
		try {
			EmulatorImpl.syncExec(r);
		} catch (SWTException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else {
				throw new RuntimeException(cause);
			}
		}
	}

	protected void _shown() {
		updateSize(false);
	}

	static {
		Displayable.lastFrameTime = System.nanoTime();
		Displayable.lastFpsUpdateTime = Displayable.lastFrameTime;
		Displayable.framesCount = 0;
	}

	public void _swtHidden() {
	}

	public void _swtShown() {
		if (swtContent != null && !swtContent.isDisposed()) {
			swtUpdateSizes();
		} else if (swtMenu == null) {
			swtInitMenu();
		}
	}

	void swtUpdateSizes() {
		Rectangle newArea = _layoutSwtContent();
		if(swtContentArea == null || !swtInitialized
				|| newArea.width != swtContentArea.width
				|| newArea.height != swtContentArea.height)
		{
			swtInitialized = true;
			swtContentArea = newArea;
			_swtResized(newArea.width, newArea.height);
		}
	}

	void swtInitMenu() {
		if (swtMenu != null) return;
		swtMenu = new Menu(getSwtParent());
		swtMenu.addMenuListener(swtMenuListener);
		getSwtParent().setMenu(swtMenu);
	}

	void showSwtMenu(final boolean item, final int x, final int y) {
		syncExec(new Runnable() {
			@Override
			public void run() {
				swtUpdateMenuCommands(item);
				if (x != -2 && y != -2) {
					Point p;
					EmulatorScreen s = ((EmulatorScreen) Emulator.getEmulator().getScreen());
					if (x != -1 || y != -1) {
						int[] t = s.transformCaret(x, y);
						p = s.getCanvas().toDisplay(new Point(t[0], t[1]));
					} else {
						p = s.getMenuLocation();
					}
					swtMenu.setLocation(p);
				}
				swtMenu.setVisible(true);
			}
		});
	}

	void hideSwtMenu() {
		syncExec(new Runnable() {
			@Override
			public void run() {
				swtMenu.setVisible(false);
			}
		});
	}

	void swtUpdateMenuCommands(boolean item) {
		for (MenuItem mi: swtMenu.getItems()) {
			mi.dispose();
		}
		for (int i = 1; i < commands.size(); i++) {
			Command cmd = (Command) commands.get(i);
			MenuItem mi = new MenuItem(swtMenu, SWT.PUSH);
			mi.addSelectionListener(swtMenuSelectionListener);
			mi.setData(cmd);
			mi.setText(cmd.getLongLabel());
		}
	}

	public void _swtResized(int w, int h) {

	}

	void updateSize(boolean force) {
		if (force) forceUpdateSize = true;
		if (Emulator.getCurrentDisplay().getCurrent() != this) return;
		IScreen s = Emulator.getEmulator().getScreen();
		Emulator.getEventQueue().sizeChanged(s.getWidth(), s.getHeight());
	}

	class SwtMenuSelectionListener implements SelectionListener {

		public void widgetSelected(SelectionEvent e) {
			try {
				Object o = (Object) e.widget.getData();
				if (o instanceof Command) {
					Emulator.getEventQueue().commandAction((Command) o, Displayable.this);
				} else if (o instanceof ChoiceGroup) {
					((ChoiceGroup) o).select(((MenuItem) e.widget).getText());
				} else if (o instanceof Object[]) {
					Command c = (Command) ((Object[]) o)[0];
					Item item = (Item) ((Object[]) o)[1];
					Emulator.getEventQueue().commandAction(c, item);
				}
			} catch (Exception ignored) {}
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}

	class SwtMenuListener implements MenuListener {

		public void menuHidden(MenuEvent menuEvent) {
			menuShown = false;
		}

		public void menuShown(MenuEvent menuEvent) {
			menuShown = true;
		}
	}
}