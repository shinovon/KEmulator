package javax.microedition.lcdui;

import emulator.Emulator;
import emulator.lcdui.a;

import java.util.Vector;

public abstract class Item {
	public static final int X = 0;
	public static final int Y = 1;
	public static final int W = 2;
	public static final int H = 3;
	public static final int LAYOUT_DEFAULT = 0;
	public static final int LAYOUT_LEFT = 1;
	public static final int LAYOUT_RIGHT = 2;
	public static final int LAYOUT_CENTER = 3;
	public static final int LAYOUT_TOP = 16;
	public static final int LAYOUT_BOTTOM = 32;
	public static final int LAYOUT_VCENTER = 48;
	public static final int LAYOUT_NEWLINE_BEFORE = 256;
	public static final int LAYOUT_NEWLINE_AFTER = 512;
	public static final int LAYOUT_SHRINK = 1024;
	public static final int LAYOUT_EXPAND = 2048;
	public static final int LAYOUT_VSHRINK = 4096;
	public static final int LAYOUT_VEXPAND = 8192;
	public static final int LAYOUT_2 = 16384;
	public static final int PLAIN = 0;
	public static final int HYPERLINK = 1;
	public static final int BUTTON = 2;
	static final int anInt24 = 32563;
	static final Font font = Font.getFont(0, 1, 8);
	int[] bounds;
	boolean inFocus;
	boolean shownOnForm;
	Command aCommand174;
	public ItemCommandListener itemCommandListener;
	public Vector commands;
	String label;
	String[] labelArr;
	Screen screen;
	int layout;
	int preferredW = -1;
	int preferredH = -1;
	int[] anIntArray179;
	int currentPos;

	Item(String label) {
		this.label = label;
		this.screen = null;
		this.commands = new Vector();
		bounds = new int[4];
	}

	public void setLabel(String label) {
		this.label = label;
		queueLayout();
	}

	public String getLabel() {
		return this.label;
	}

	public int getLayout() {
		return this.layout;
	}

	public void setLayout(int layout) {
		if ((layout & ~anInt24) != 0) {
			throw new IllegalArgumentException();
		} else {
			this.layout = layout;
		}
	}

	protected boolean isLayoutDefault() {
		return layout == 0;
	}

	protected boolean isLayoutAlignDefault() {
		return (layout & LAYOUT_CENTER) == 0;
	}

	protected boolean isLayoutLeft() {
		return (layout & LAYOUT_CENTER) == LAYOUT_LEFT;
	}

	protected boolean isLayoutCenter() {
		return (layout & LAYOUT_CENTER) == LAYOUT_CENTER;
	}

	protected boolean isLayoutRight() {
		return (layout & LAYOUT_CENTER) == LAYOUT_RIGHT;
	}

	protected boolean isLayoutExpand() {
		return (layout & LAYOUT_EXPAND) == LAYOUT_EXPAND;
	}

	public void addCommand(Command command) {
		if (this.screen instanceof Alert) {
			throw new IllegalStateException();
		} else if (command == null) {
			throw new NullPointerException();
		} else if (!this.commands.contains(command)) {
			int i;
			for (i = 0; i < this.commands.size(); ++i) {
				Command command2 = (Command) this.commands.get(i);
				if (command.getCommandType() > command2.getCommandType()
						|| command.getCommandType() == command2.getCommandType()
						&& command.getPriority() <= command2.getPriority()) {
					break;
				}
			}

			this.commands.add(i, command);
			if (this.screen != null && Emulator.getCurrentDisplay().getCurrent() == this.screen) {
				this.screen.updateCommands();
			}

		}
	}

	public void removeCommand(Command command) {
		if (this.commands.contains(command)) {
			if (command == this.aCommand174) {
				this.aCommand174 = null;
			}

			this.commands.remove(command);
			if (this.screen != null && Emulator.getCurrentDisplay().getCurrent() == this.screen) {
				this.screen.updateCommands();
			}
		}

	}

	public void setItemCommandListener(ItemCommandListener itemCommandListener) {
		if (this.screen instanceof Alert) {
			throw new IllegalStateException();
		} else {
			this.itemCommandListener = itemCommandListener;
		}
	}

	protected void itemApplyCommand() {
		if (this.itemCommandListener != null && this.aCommand174 != null) {
			this.itemCommandListener.commandAction(this.aCommand174, this);
		}

	}

	public int getPreferredWidth() {
		return this.preferredW != -1 ? this.preferredW : bounds[W];
	}

	public int getPreferredHeight() {
		return this.preferredH != -1 ? this.preferredH : bounds[H];
	}

	public void setPreferredSize(int anInt180, int anInt181) {
		if (this.screen instanceof Alert) {
			throw new IllegalStateException();
		} else {
			this.preferredW = anInt180;
			this.preferredH = anInt181;
		}
		if (screen != null) {
			((Form) screen).queueLayout(this);
		}
	}

	public int getMinimumWidth() {
		return 0;
	}

	public int getMinimumHeight() {
		return 0;
	}

	public void setDefaultCommand(Command aCommand174) {
		if (this.screen instanceof Alert) {
			throw new IllegalStateException();
		} else {
			if ((this.aCommand174 = aCommand174) != null) {
				this.addCommand(aCommand174);
			}

		}
	}

	public void notifyStateChanged() {
		if (screen == null) return;
		if (this.screen instanceof Form) {
			if (((Form) this.screen).itemStateListener == null) return;
			((Form) this.screen).itemStateListener.itemStateChanged(this);
		}
	}

	protected void focus() {
		this.inFocus = true;
		if (this.screen != null) {
			this.screen.setItemCommands(this);
		}

	}

	protected void defocus() {
		this.inFocus = false;
		if (this.screen != null) {
			this.screen.removeItemCommands(this);
		}

	}

	protected void paint(Graphics g, int x, int y, int w, int h, int line) {
		paint(g, x, y, w, h);
	}

	protected void paint(Graphics g, int x, int y, int w, int h) {
		g.setColor(-16777216);
		if (this.inFocus) {
//			int w = getPreferredWidth() > 0 ? getPreferredWidth() + 1 : bounds[W];
			a.method178(g, x, y, w, h);
		}

	}

	protected void layout(Row row) {
		bounds[X] = 0;
		bounds[Y] = 0;
		bounds[W] = this.screen.bounds[W];
		bounds[H] = Screen.fontHeight4;
	}

	protected int getcurPage() {
		return this.anIntArray179 != null && this.anIntArray179.length > 0 ? this.anIntArray179[this.currentPos] : 0;
	}

	protected boolean scrollUp() {
		if (this.anIntArray179 != null && this.anIntArray179.length > 0 && this.currentPos > 0) {
			--this.currentPos;
			return false;
		} else {
			return true;
		}
	}

	protected boolean scrollDown() {
		if (this.anIntArray179 != null && this.anIntArray179.length > 0
				&& this.currentPos < this.anIntArray179.length - 1) {
			++this.currentPos;
			return false;
		} else {
			return true;
		}
	}

	protected void pointerPressed(int n, int n2) {
	}

	void updateHidden() {

	}

	public boolean hasLayout(int l) {
		return (layout & l) != 0;
	}

	boolean isFocusable() {
		return false;
	}

	boolean hasLabel() {
		return label != null && !label.isEmpty();
	}


	void queueLayout() {
		if (screen != null) {
			((Form) screen).queueLayout(this);
		}
	}
}
