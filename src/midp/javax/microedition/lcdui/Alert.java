package javax.microedition.lcdui;

import emulator.Emulator;
import emulator.UILocale;
import emulator.lcdui.LCDUIUtils;
import emulator.lcdui.TextUtils;

public class Alert extends Screen {
	public static final Command DISMISS_COMMAND;
	public static final int FOREVER = -2;
	int timeout;
	String string;
	private String[] textArr;
	Displayable lastDisplayed;
	private Gauge gauge;
	private long timeShown = System.currentTimeMillis();;

	public Alert(final String s) {
		this(s, null, null, null);
	}

	public Alert(final String s, final String aString172, final Image image, final AlertType alertType) {
		super(s);
		this.string = aString172;
		this.timeout = getDefaultTimeout();
		this.lastDisplayed = Display.current;
		super.addCommand(Alert.DISMISS_COMMAND);
	}

	public void addCommand(final Command command) {
		if (command == Alert.DISMISS_COMMAND) {
			return;
		}
		super.removeCommand(Alert.DISMISS_COMMAND);
		super.addCommand(command);
	}

	public void removeCommand(final Command command) {
		super.removeCommand(command);
		if (this.isCommandsEmpty()) {
			super.addCommand(Alert.DISMISS_COMMAND);
		}
	}

	public int getDefaultTimeout() {
		return 2000;
	}

	public int getTimeout() {
		return this.timeout;
	}

	public void setTimeout(final int n) {
		if (n < 0 && n != -2) {
			throw new IllegalArgumentException("time should be positive");
		}
		this.timeout = n;
	}

	protected void _shown() {
		super._shown();
		try {
			Emulator.getEventQueue().waitRepaint();
			repaintScreen();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		timeShown = System.currentTimeMillis();
	}

	public Image getImage() {
		return null;
	}

	public void setImage(final Image image) {
	}

	public Gauge getIndicator() {
		return gauge;
	}

	public void setIndicator(final Gauge gauge) {
		if (gauge != null && gauge.isInteractive())
			throw new IllegalArgumentException();
		this.gauge = gauge;
	}

	public String getString() {
		return this.string;
	}

	public void setString(final String string) {
		this.string = string;
	}

	public void setType(final AlertType alertType) {
	}

	protected void close() {
		Emulator.getCurrentDisplay().setCurrent(lastDisplayed);
	}

	protected void _paint(Graphics g) {
		final int n = super.bounds[W] - 8;
		this.textArr = TextUtils.textArr(this.string, Screen.font, n, n);
		g.setColor(LCDUIUtils.foregroundColor);
		int h = Screen.fontHeight4 - scroll;
		g.setClip(bounds[X], bounds[Y], bounds[W], bounds[H]);
		for (int i = 0; i < this.textArr.length; ++i) {
			g.drawString(this.textArr[i], 4, h + 2, 0);
			h += Screen.fontHeight4;
		}
		if (gauge != null) {
			gauge.screen = this;
			gauge.bounds[X] = 0;
			gauge.bounds[Y] = h;
			gauge.paint(g, 2, h, bounds[W] - 4, 0/*gauge.getMinimumWidth()*/);
		}
		if (lastDisplayed != null
				&& timeShown != 0
				&& timeout >= 0
				&& commands.size() <= 1
				&& System.currentTimeMillis() - timeShown > timeout) {
			close();
		}
	}

	public int _repaintInterval() {
		return gauge != null || timeout >= 0 || ticker != null ? 500 : -1;
	}

	protected void _keyScroll(int key, boolean repeat) {
		if (key == Canvas.UP) {
			scroll = Math.max(0, scroll - Screen.fontHeight4);
			repaintScreen();
			timeout = -2;
			return;
		}
		if (key == Canvas.DOWN) {
			scroll = Math.min(textArr != null ? Screen.fontHeight4 * textArr.length : 0, scroll + Screen.fontHeight4);
			repaintScreen();
			timeout = -2;
		}
	}

	static {
		DISMISS_COMMAND = new Command(UILocale.get("LCDUI_ALERT_DISMISS_COMMAND", "OK"), 4, 0);
	}
}
