package javax.microedition.lcdui;

import emulator.*;
import emulator.lcdui.*;

public class Alert extends Screen {
	public static final Command DISMISS_COMMAND;
	public static final int FOREVER = -2;
	int timeout;
	int time;
	String string;
	private String[] textArr;
	Displayable lastDisplayed;
	private Gauge gauge;
	private long timeShown;

	public Alert(final String s) {
		this(s, null, null, null);
	}

	public Alert(final String s, final String aString172, final Image image, final AlertType alertType) {
		super(s);
		this.string = aString172;
		final int n = getDefaultTimeout();
		this.time = n;
		this.timeout = n;
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
		return 1000;
	}

	public int getTimeout() {
		return this.timeout;
	}

	public void setTimeout(final int n) {
		if (n < 0 && n != -2) {
			throw new IllegalArgumentException("time should be positive");
		}
		this.timeout = n;
		this.time = n;
	}

	protected void shown() {
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
		Emulator.getCurrentDisplay().setCurrent(this.lastDisplayed);
	}

	protected void paint(Graphics g) {
		final int n = super.bounds[W] - 8;
		this.textArr = c.textArr(this.string, Screen.font, n, n);
		g.setColor(-16777216);
		int h = Screen.fontHeight4;
		for (int i = 0; i < this.textArr.length; ++i) {
			g.drawString(this.textArr[i], 4, h + 2, 0);
			h += Screen.font.getHeight() + 4;
		}
		if (gauge != null) {
			gauge.screen = this;
			gauge.bounds[X] = 0;
			gauge.bounds[Y] = h;
			gauge.bounds[W] = bounds[W];
			gauge.paint(g);
		}
		if (this.time > 0 && this.timeout != -2) {
			if (System.currentTimeMillis() - timeShown > time) {
				this.close();
			}
		}
	}

	static {
		DISMISS_COMMAND = new Command("OK", 4, 0);
	}
}
