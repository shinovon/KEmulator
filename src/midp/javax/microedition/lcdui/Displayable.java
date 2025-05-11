package javax.microedition.lcdui;

import emulator.*;
import emulator.debug.Profiler;
import emulator.lcdui.LCDUIUtils;
import emulator.media.capture.CapturePlayerImpl;
import emulator.ui.CommandsMenuPosition;
import emulator.ui.IScreen;
import emulator.ui.TargetedCommand;

import java.util.Arrays;
import java.util.Vector;

public class Displayable {
	public static final int X = 0;
	public static final int Y = 1;
	public static final int W = 2;
	public static final int H = 3;
	String title;
	Vector commands;
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
	boolean forceUpdateSize;

	private Command leftCommand;
	private Command rightCommand;
	private final Vector<Command> menuCommands = new Vector<Command>();
	final Object lock = new Object();

	public Displayable() {
		super();
		this.focusedItem = null;
		this.cmdListener = null;
		this.commands = new Vector();
		IScreen s = Emulator.getEmulator().getScreen();
		this.w = s.getWidth();
		this.h = s.getHeight();
		if (this instanceof Canvas) {
			this.bounds = new int[]{0, 0, this.w, this.h - Screen.fontHeight4};
		} else {
			this.bounds = new int[]{0, Screen.fontHeight4, this.w - 4, this.h - Screen.fontHeight4};
		}
	}

	public int getWidth() {
		return bounds[W];
	}

	public int getHeight() {
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
		updateCommands();
	}

	protected void removeItemCommands(final Item item) {
		if (item == null || item != focusedItem) return;
		this.focusedItem = null;
		updateCommands();
	}

	protected void updateCommands() {
		leftCommand = null;
		rightCommand = null;
		Command ok = null;
		menuCommands.clear();

		Command[] arr = (Command[]) commands.toArray(new Command[0]);
		Arrays.sort(arr);
		menuCommands.addAll(Arrays.asList(arr));

		for (Command cmd : arr) {
			int type = cmd.getCommandType();
			switch (type) {
				case Command.OK: {
					if (ok != null) continue;
					ok = cmd;
					menuCommands.remove(cmd);
					menuCommands.insertElementAt(cmd, 0);
					break;
				}
				case Command.BACK:
				case Command.EXIT:
				case Command.CANCEL:
				case Command.STOP: {
					if (rightCommand != null) continue;
					rightCommand = cmd;
					menuCommands.remove(cmd);
					break;
				}
			}
		}
		if (rightCommand == null && menuCommands.size() > 1) {
			Command cmd = menuCommands.lastElement();
			rightCommand = cmd;
			menuCommands.remove(cmd);
		}

		String leftLabel = "", rightLabel = "";
		if (hasMenuOnLeft()) {
			leftLabel = UILocale.get("LCDUI_MENU_COMMAND", "Menu");
		} else if (!menuCommands.isEmpty()) {
			leftLabel = menuCommands.get(0).getLabel();
		} else if (leftCommand != null) {
			leftLabel = leftCommand.getLabel();
		}
		if (rightCommand != null) {
			rightLabel = rightCommand.getLabel();
		}
		Emulator.getEmulator().getScreen().setLeftSoftLabel(leftLabel);
		Emulator.getEmulator().getScreen().setRightSoftLabel(rightLabel);
	}

	private boolean hasMenuOnLeft() {
		int count = menuCommands.size();
		if (focusedItem != null && !focusedItem.commands.isEmpty())
			count += focusedItem.commands.size();
		return count > 1;
	}

	protected boolean isCommandsEmpty() {
		return this.commands.isEmpty();
	}

	public void addCommand(final Command command) {
		if (command == null || this.commands.contains(command)) {
			return;
		}
		this.commands.add(command);
		if (isShown()) {
			this.updateCommands();
		}
	}

	public void removeCommand(final Command command) {
		if (this.commands.contains(command)) {
			this.commands.remove(command);
			if (isShown()) {
				updateCommands();
			}
		}
	}

	protected Command getLeftSoftCommand() {
		if (focusedItem != null && !focusedItem.commands.isEmpty()) {
			return focusedItem.commands.get(0);
		}
		if (!menuCommands.isEmpty()) {
			return menuCommands.get(0);
		}
		return leftCommand;
	}

	protected Command getRightSoftCommand() {
		return rightCommand;
	}

	public boolean handleSoftKeyAction(final int n, final boolean b) {
		if (cmdListener == null && this instanceof Canvas) {
			return false;
		}
		boolean fix = Settings.motorolaSoftKeyFix || Settings.softbankApi;
		if (KeyMapping.isLeftSoft(n)) {
			if (hasMenuOnLeft()) {
				if (b) {
					Vector<TargetedCommand> cmds = buildAllCommands();
					Emulator.getEmulator().getScreen().showCommandsList(cmds, CommandsMenuPosition.CommandsButton, 0, 0);
				}
				return !fix;
			}

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
			return leftSoftCommand != null && !fix;
		}
		if (KeyMapping.isRightSoft(n)) {
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
			return rightSoftCommand != null && !fix;
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
		if (isShown()) {
			Emulator.getEmulator().getScreen().updateTitle();
		}
	}

	protected void sizeChanged(final int n, final int n2) {
	}

	public void _invokeSizeChanged(int w, int h) {
		_invokeSizeChanged(w, h, true);
	}

	void _invokeSizeChanged(int w, int h, boolean b) {
		if (this.w != w || this.h != h || forceUpdateSize) {
			forceUpdateSize = false;
			this.w = w;
			this.h = h;
			if (!fullScreen) {
				h -= (ticker == null ? Screen.fontHeight4 : Screen.fontHeight4 * 2);
			}
			if (!(this instanceof Canvas)) {
				w -= 4;
			}
			bounds[W] = w;
			bounds[H] = h;
			if (b) sizeChanged(w, h);
		}
		repaintScreen();
	}

	public Ticker getTicker() {
		return this.ticker;
	}

	public void setTicker(final Ticker ticker) {
		synchronized (lock) {
			this.ticker = ticker;
			this.tickerX = this.w;
			updateSize(true);
		}
	}

	protected void _paintTicker(final Graphics graphics) {
		synchronized (lock) {
			if (ticker == null) {
				if (!fullScreen && this instanceof Canvas) {
					graphics.setColor(-1);
					graphics.fillRect(0, this.bounds[H], w, Screen.fontHeight4);
				}
				return;
			}
			LCDUIUtils.drawTickerBackground(graphics, 0, Screen.fontHeight4 + this.bounds[H] - 1, this.w, Screen.fontHeight4);
			String t = ticker.getString();
			if (t == null) return;
			graphics.setFont(Screen.font);
			graphics.drawString(t, this.tickerX, Screen.fontHeight4 + this.bounds[H] - 1 + 2, 0);
			this.tickerX -= 5;
			if (this.tickerX < -Screen.font.stringWidth(t)) {
				this.tickerX = this.w;
			}
		}
	}

	void repaintScreen() {
		if (!isShown()) return;
		int n;
		if (this instanceof Canvas) {
			n = EventQueue.EVENT_PAINT;
		} else {
			if (!(this instanceof Screen)) {
				return;
			}
			n = EventQueue.EVENT_SCREEN;
		}
		try {
			Emulator.getEventQueue().queue(n);
		} catch (Exception ignored) {
		}
	}

	void repaintScreen(Item item) {
		repaintScreen();
	}

	protected void _paintSoftMenu(final Graphics graphics) {
		CapturePlayerImpl.draw(graphics, Emulator.getCurrentDisplay().getCurrent());
	}

	public static synchronized void _fpsLimiter(boolean b) {
		if (b && Settings.speedModifier == 1 && Settings.frameRate <= 120) {
			long elapsed = System.nanoTime() - lastFrameTime;
			long var2 = (long) ((MILLI_TO_NANO * 1000) / Settings.frameRate);

			long delta = var2 - elapsed;
			if (delta > 0) {
				try {
					Thread.sleep(delta / MILLI_TO_NANO/*, (int) (delta % MILLI_TO_NANO)*/);
				} catch (Exception ignored) {
				}
			}
		}
		lastFrameTime = System.nanoTime();

		if (b) ++framesCount;
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
				} catch (Exception ignored) {
				}
				Settings.aLong1235 += System.currentTimeMillis() - currentTimeMillis;
			}
			--Settings.steps;
		}
	}

	public static void _resetXRayGraphics() {
		Graphics.resetXRayCache();
	}

	protected void _shown() {
		updateSize(false);
	}

	static {
		Displayable.lastFrameTime = System.nanoTime();
		Displayable.lastFpsUpdateTime = Displayable.lastFrameTime;
		Displayable.framesCount = 0;
	}

	Vector<TargetedCommand> buildAllCommands() {
		Vector<TargetedCommand> cmds = new Vector<>();
		buildItemCommands(cmds, focusedItem);
		buildScreenCommands(cmds);
		return cmds;
	}

	static void buildItemCommands(Vector<TargetedCommand> cmds, Item target) {
		if (target == null)
			return;
		Vector<Command> targetCommands = target.commands;
		for (Command cmd : targetCommands) {
			cmds.add(new TargetedCommand(cmd, target));
		}
	}

	void buildScreenCommands(Vector<TargetedCommand> cmds) {
		for (Command cmd : menuCommands) {
			cmds.add(new TargetedCommand(cmd, this));
		}
	}

	void updateSize(boolean force) {
		IScreen s = Emulator.getEmulator().getScreen();
		if (force) {
			forceUpdateSize = true;
			_invokeSizeChanged(s.getWidth(), s.getHeight(), false);
			forceUpdateSize = true;
		}
		if (Emulator.getCurrentDisplay().getCurrent() != this) return;
		Emulator.getEventQueue().sizeChanged(s.getWidth(), s.getHeight());
	}
}