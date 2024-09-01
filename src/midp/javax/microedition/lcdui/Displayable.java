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
	int anInt28;
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
	protected boolean nonFullScreen;
	private static final Object frameLock = new Object();
	private static final long MILLI_TO_NANO = 1000000L;

	protected Composite swtContent;
	private Rectangle swtContentArea;
	private boolean swtInitialized;
	private Menu swtMenu;
	private SelectionListener swtMenuSelectionListener = new SwtMenuSelectionListener();
	private MenuListener swtMenuListener = new SwtMenuListener();

	public Displayable() {
		super();
		this.focusedItem = null;
		this.cmdListener = null;
		this.commands = new Vector();
		this.w = Emulator.getEmulator().getScreen().getWidth();
		this.h = Emulator.getEmulator().getScreen().getHeight();
		this.bounds = new int[]{0, Screen.fontHeight4, this.w - 4, this.h - Screen.fontHeight4};
	}

	public int getWidth() {
		if (swtContentArea != null) {
			return swtContentArea.width;
		}
		return this.w;
	}

	public int getHeight() {
		if (swtContentArea != null) {
			return swtContentArea.height;
		}
		return this.h;
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
				this.updateCommands();
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
					// XXX
					if (this.cmdListener != null)
						this.cmdListener.commandAction(leftSoftCommand, this);
					else ((Alert) this).close();
				} else if (this.focusedItem != null && this.focusedItem.commands.contains(leftSoftCommand)) {
					this.focusedItem.itemCommandListener.commandAction(leftSoftCommand, this.focusedItem);
				} else if (this.cmdListener != null) {
					this.cmdListener.commandAction(leftSoftCommand, this);
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
					this.anInt28 = 0;
					if (swtMenu != null) {
						showSwtMenu();
					} else {
						this.repaintScreen();
					}
				}
			} else {
				final Command rightSoftCommand = this.getRightSoftCommand();
				if (b && rightSoftCommand != null) {
					Emulator.getEmulator().getLogStream().println("Right command: " + rightSoftCommand);
					if (this instanceof Alert && rightSoftCommand == Alert.DISMISS_COMMAND) {
						// XXX
						if (this.cmdListener != null)
							this.cmdListener.commandAction(rightSoftCommand, this);
						else ((Alert) this).close();
					} else if (this.focusedItem != null && this.focusedItem.commands.contains(rightSoftCommand)) {
						this.focusedItem.itemCommandListener.commandAction(rightSoftCommand, this.focusedItem);
					} else if (this.cmdListener != null) {
						this.cmdListener.commandAction(rightSoftCommand, this);
					}
				}
			}
			return !Settings.motorolaSoftKeyFix;
		}
		return false;
	}

	public void callCommandAction(Command command) {
		if (cmdListener != null && command != null) {
			// TODO queue
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

	public void invokeSizeChanged(final int w, final int h) {
		IScreen s = Emulator.getEmulator().getScreen();
		this.w = s.getWidth();
		this.h = s.getHeight();
		if (swtContent != null) {
			syncExec(new Runnable() {
				public void run() {
					swtUpdateSizes();
				}
			});
		}
		this.sizeChanged(w, h);
		Emulator.getEventQueue().queueRepaint();
	}

	public Ticker getTicker() {
		return this.ticker;
	}

	public void setTicker(final Ticker ticker) {
		this.ticker = ticker;
		bounds[H] = h - (this.ticker == null ? Screen.fontHeight4 : Screen.fontHeight4 * 2);
		this.tickerX = this.w;
	}

	protected void paintTicker(final Graphics graphics) {
		if (ticker == null) {
			if (nonFullScreen) {
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

	protected void paintSoftMenu(final Graphics graphics) {
		CapturePlayerImpl.draw(graphics, Emulator.getCurrentDisplay().getCurrent());
		final int translateX = graphics.getTranslateX();
		final int translateY = graphics.getTranslateY();
		/*
		if (Emulator.screenBrightness < 100) {
			graphics.translate(-translateX, -translateY);
			int alpha = (int) (((double) (100 - Emulator.screenBrightness) / 100d) * 255d);
			// System.out.println(Integer.toHexString(alpha << 24));
			DirectGraphicsInvoker.getDirectGraphics(graphics).setARGBColor(alpha << 24);
			graphics.fillRect(0, 0, getWidth(), getHeight());
			graphics.translate(translateX, translateY);
		}
		 */
		if (!this.menuShown || swtMenu != null) {
			return;
		}
		final int clipX = graphics.getClipX();
		final int clipY = graphics.getClipY();
		final int clipWidth = graphics.getClipWidth();
		final int clipHeight = graphics.getClipHeight();
		graphics.translate(-translateX, -translateY);
		graphics.setClip(0, 0, this.w, this.h);
		final int n = this.w >> 1;
		final int anInt181 = Screen.fontHeight4;
		final int n3;
		final int n2 = (n3 = this.commands.size() - 1) * anInt181;
		final int n4 = n - 1;
		int n5 = this.h - n2 - 1;
		a.method177(graphics, n4, n5, n, n2, true);
		for (int i = 0; i < n3; ++i, n5 += anInt181) {
			graphics.setColor(-16777216);
			if (i == this.anInt28) {
				a.method178(graphics, n4, n5, n, anInt181);
			}
			graphics.drawString(i + 1 + "." + ((Command) this.commands.get(i + 1)).getLongLabel(), n4 + 4, n5 + 2, 0);
		}
		graphics.translate(translateX, translateY);
		graphics.setClip(clipX, clipY, clipWidth, clipHeight);
	}

	public static void fpsLimiter() {
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

	public static void checkForSteps(Object lock) {
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

	public static void resetXRayGraphics() {
		Graphics.resetXRayCache();
	}


	void constructSwt() {
		syncExec(new Runnable() {
			public void run() {
				swtContent = constructSwtContent(SWT.NONE);
				swtMenu = new Menu(swtContent);
				swtContent.setMenu(swtMenu);
				swtContentArea = layoutSwtContent();
			}
		});
	}

	protected Composite constructSwtContent(int style) {
		return new Composite(getSwtParent(), SWT.NONE);
	}

	protected Rectangle layoutSwtContent() {
		Rectangle area = getSwtParent().getClientArea();
		swtContent.setBounds(0, 0, area.width, area.height);
		return swtContent.getClientArea();
	}

	public Composite getSwtContent() {
		return swtContent;
	}

	protected void finalize() throws Throwable {
		syncExec(new Runnable() {
			public void run() {
				if (!swtContent.isDisposed()) {
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

	protected void shown() {
	}

	static {
		Displayable.lastFrameTime = System.nanoTime();
		Displayable.lastFpsUpdateTime = Displayable.lastFrameTime;
		Displayable.framesCount = 0;
	}

	public void swtHidden() {
	}

	public void swtShown() {
		if (swtContent != null && !swtContent.isDisposed()) {
			swtUpdateSizes();
		} else if (swtMenu == null) {
			swtInitMenu();
		}
	}

	void swtUpdateSizes() {
		Rectangle newArea = layoutSwtContent();
		if(swtContentArea == null || !swtInitialized
				|| newArea.width != swtContentArea.width
				|| newArea.height != swtContentArea.height)
		{
			swtInitialized = true;
			swtContentArea = newArea;
			swtResized(newArea.width, newArea.height);
		}
	}

	void swtInitMenu() {
		if (swtMenu != null) return;
		swtMenu = new Menu(getSwtParent());
		swtMenu.addMenuListener(swtMenuListener);
		getSwtParent().setMenu(swtMenu);
	}

	void showSwtMenu() {
		syncExec(new Runnable() {
			@Override
			public void run() {
				swtUpdateMenuCommands();
				Point p = ((EmulatorScreen) Emulator.getEmulator().getScreen()).getMenuLocation();
				swtMenu.setLocation(p);
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

	void swtUpdateMenuCommands() {
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

	public void swtResized(int w, int h) {

	}

	public boolean getLeftRightLanguage() {
		return true;
	}

	public boolean swtIsShown() {
		return isShown();
	}

	Composite getContentComp()
	{
		return swtContent;
	}

	class SwtMenuSelectionListener implements SelectionListener {

		public void widgetSelected(SelectionEvent e) {
			try {
				Command c = (Command) e.widget.getData();
				callCommandAction(c);
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