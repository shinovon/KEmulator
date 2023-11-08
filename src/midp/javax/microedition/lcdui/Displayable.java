package javax.microedition.lcdui;

import java.util.*;

import javax.microedition.media.CapturePlayerImpl;

import emulator.*;
import emulator.debug.*;
import emulator.lcdui.a;

public class Displayable {
	public static final int X = 0;
	public static final int Y = 1;
	public static final int W = 2;
	public static final int H = 3;
	String title;
	Vector commands;
	boolean aBoolean18;
	int anInt28;
	CommandListener cmdListener;
	Item selectedItem;
	int w;
	int h;
	int[] bounds;
	Ticker ticker;
	int tickerX;
	private static long lastFrameTime;
	private static long lastFpsUpdateTime;
	private static int framesCount;

	public Displayable() {
		super();
		this.selectedItem = null;
		this.cmdListener = null;
		this.commands = new Vector();
		this.w = Emulator.getEmulator().getScreen().getWidth();
		this.h = Emulator.getEmulator().getScreen().getHeight();
		this.bounds = new int[] { 0, Screen.fontHeight4, this.w - 4, this.h - Screen.fontHeight4 };
	}

	public int getWidth() {
		return this.w;
	}

	public int getHeight() {
		return this.h;
	}

	public String getTitle() {
		return this.title;
	}

	public boolean isShown() {
		return Display.current == this;
	}

	protected void defocus() {
		if (this.selectedItem != null) {
			this.selectedItem.defocus();
			this.selectedItem = null;
		}
	}

	protected void setItemCommands(final Item anItem20) {
		this.selectedItem = anItem20;
		if (anItem20.itemCommands.size() > 0) {
			for (int i = 0; i < anItem20.itemCommands.size(); ++i) {
				this.addCommand((Command) anItem20.itemCommands.get(i));
			}
		}
	}

	protected void removeItemCommands(final Item item) {
		if (item == null) {
			return;
		}
		if (item.itemCommands.size() > 0) {
			for (int i = 0; i < item.itemCommands.size(); ++i) {
				this.removeCommand((Command) item.itemCommands.get(i));
			}
		}
		this.selectedItem = null;
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
		if (Keyboard.isLeftSoft(n)) {
			final Command leftSoftCommand = this.getLeftSoftCommand();
			if (b && leftSoftCommand != null) {
				Emulator.getEmulator().getLogStream().println("Left command: " + leftSoftCommand);
				if (this instanceof Alert && leftSoftCommand == Alert.DISMISS_COMMAND) {
					// XXX
					if (this.cmdListener != null)
						this.cmdListener.commandAction(leftSoftCommand, this);
					else ((Alert) this).close();
				} else if (this.selectedItem != null && this.selectedItem.itemCommands.contains(leftSoftCommand)) {
					this.selectedItem.itemCommandListener.commandAction(leftSoftCommand, this.selectedItem);
				} else if (this.cmdListener != null) {
					this.cmdListener.commandAction(leftSoftCommand, this);
				}
			}
			return true;
		}
		if (Keyboard.isRightSoft(n)) {
			if (this.commands.size() > 2) {
				if (b && this.aBoolean18) {
					this.aBoolean18 = false;
					this.refreshSoftMenu();
				} else if (b) {
					this.aBoolean18 = true;
					this.anInt28 = 0;
					this.refreshSoftMenu();
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
					} else if (this.selectedItem != null && this.selectedItem.itemCommands.contains(rightSoftCommand)) {
						this.selectedItem.itemCommandListener.commandAction(rightSoftCommand, this.selectedItem);
					} else if (this.cmdListener != null) {
						this.cmdListener.commandAction(rightSoftCommand, this);
					}
				}
			}
			return true;
		}
		return false;
	}

	public void setCommandListener(final CommandListener aCommandListener19) {
		this.cmdListener = aCommandListener19;
	}

	public void setTitle(final String aString25) {
		this.title = aString25;
	}

	protected void sizeChanged(final int n, final int n2) {
	}
    
    public void invokeSizeChanged(final int n, final int n2) {
        w = Emulator.getEmulator().getScreen().getWidth();
        h = Emulator.getEmulator().getScreen().getHeight();
        this.sizeChanged(n, n2);
        Emulator.getEventQueue().queueRepaint();
    }

	public Ticker getTicker() {
		return this.ticker;
	}

	public void setTicker(final Ticker aTicker22) {
		this.ticker = aTicker22;
		final int[] anIntArray21 = this.bounds;
		final int n = H;
		int n2;
		int anInt181;
		if (this.ticker == null) {
			n2 = this.h;
			anInt181 = Screen.fontHeight4;
		} else {
			n2 = this.h;
			anInt181 = Screen.fontHeight4 * 2;
		}
		anIntArray21[n] = n2 - anInt181;
		this.tickerX = this.w;
	}

	protected void paintTicker(final Graphics graphics) {
		if (this.ticker == null) {
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

	void refreshSoftMenu() {
		EventQueue j;
		int n;
		if (this instanceof Canvas) {
			j = Emulator.getEventQueue();
			n = 1;
		} else {
			if (!(this instanceof Screen)) {
				return;
			}
			j = Emulator.getEventQueue();
			n = 4;
		}
		j.queue(n);
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
		if (!this.aBoolean18) {
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
	/*
	protected static void fpsLimiter() {
	    if (k.f == 1 && k.d <= 50) {
	        final long n = System.currentTimeMillis() - Displayable.aLong23;
	        final long n2 = 1000 / k.d;
	        try {
	            Thread.sleep(n2 - n);
	        }
	        catch (Exception ex) {}
	    }
	    Displayable.aLong23 = System.currentTimeMillis();
	    ++Displayable.anInt24;
	    if (Displayable.aLong23 - Displayable.aLong27 > 2000L) {
	        Profiler.FPS = (int)((Displayable.anInt24 * 1000 + 500) / (Displayable.aLong23 - Displayable.aLong27));
	        Displayable.aLong27 = Displayable.aLong23;
	        Displayable.anInt24 = 0;
	    }
	}
	*/

	public static void fpsLimiter() {
		if (Settings.f == 1 && Settings.frameRate <= 120) {
			long var0 = System.currentTimeMillis() - lastFrameTime;
			long var2 = (long) (1000 / Settings.frameRate);
			if(var2 - var0 > 0) { 
				try {
					Thread.sleep(var2 - var0);
				} catch (Exception var4) {
					;
				}
			}
		}

		lastFrameTime = System.currentTimeMillis();
		++framesCount;
		long l = lastFrameTime - lastFpsUpdateTime;
		if (l > 2000L) {
			Profiler.FPS = (int) ((long) (framesCount * 1000 + 500) / l);
			lastFpsUpdateTime = lastFrameTime;
			framesCount = 0;
		}

	}

	public static void checkForSteps() {
		if (Settings.e >= 0) {
			if (Settings.e == 0) {
				final long currentTimeMillis = System.currentTimeMillis();
				try {
					while (Settings.e == 0) {
						Thread.sleep(250L);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				Settings.aLong1235 += System.currentTimeMillis() - currentTimeMillis;
			}
			--Settings.e;
		}
	}

	public static void resetXRayGraphics() {
		Graphics.resetXRayCache();
	}

	static {
		Displayable.lastFrameTime = System.currentTimeMillis();
		Displayable.lastFpsUpdateTime = Displayable.lastFrameTime;
		Displayable.framesCount = 0;
	}
}
