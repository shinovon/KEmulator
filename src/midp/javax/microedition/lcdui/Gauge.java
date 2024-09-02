package javax.microedition.lcdui;

public class Gauge extends Item {
	public static final int INDEFINITE = -1;
	public static final int CONTINUOUS_IDLE = 0;
	public static final int INCREMENTAL_IDLE = 1;
	public static final int CONTINUOUS_RUNNING = 2;
	public static final int INCREMENTAL_UPDATING = 3;
	private int max;
	private int value;
	private boolean interactive;

	private long lastIncrementTime;
	private boolean continuosDir;
	private int continuosValue;
	private static long continousIncTime = 1;

	public Gauge(String label, boolean interactive, int maxValue, int initialValue) {
		super(label);
		value = initialValue;
		max = maxValue;
		this.interactive = interactive;
	}

	public void setLabel(String label) {
		super.setLabel(label);
	}

	public void setLayout(int layout) {
		super.setLayout(layout);
	}

	public void addCommand(Command cmd) {
		super.addCommand(cmd);
	}

	public void setItemCommandListener(ItemCommandListener l) {
		super.setItemCommandListener(l);
	}

	public void setPreferredSize(int w, int h) {
		super.setPreferredSize(w, h);
	}

	public void setDefaultCommand(Command cmd) {
		super.setDefaultCommand(cmd);
	}

	public void setValue(int value) {
		this.value = value;
		repaintForm();
	}

	public int getValue() {
		return value;
	}

	public void setMaxValue(int maxValue) {
		max = maxValue;
		repaintForm();
	}

	public int getMaxValue() {
		return max;
	}

	public boolean isInteractive() {
		return interactive;
	}

	protected void paint(Graphics g, int x, int y, int w, int h) {
		super.paint(g, x, y, w, h);
		final Font font = Item.font;
		g.setFont(font);

		h = font.getHeight();
		int yoff = 0;
		if (label != null) {
			g.drawString(label, x, y, 0);
			yoff += h + 4;
		}

		if (!interactive) {
			int max = this.max;
			int val = this.value;
			if (max == -1) {
				switch (value) {
					case -1: {
						break;
					}
					default: //TODO
					case CONTINUOUS_RUNNING: {
						max = 50;
						if (!continuosDir)
							continuosValue++;
						else
							continuosValue--;
						if (continuosValue >= max || continuosValue < 0) {
							continuosDir = !continuosDir;
						}
						val = continuosValue;
						break;
					}
				}
			}
			g.setColor(0xababab);
			g.drawRect(x, y + yoff, w, h);
			if (max <= 0 || val < 0) return;
			g.setColor(0x0000ff);
			g.fillRect(x + 2, y + yoff + 2, (int) ((double) w * ((double) val / (double) max)) - 3, h - 3);
		} else {
			int xoff1 = Item.font.stringWidth("0") + 2;
			int xoff2 = Item.font.stringWidth(Integer.toString(max)) + 2;
			g.drawString("0", x + 2, y + yoff, 0);
			g.drawString(Integer.toString(max), x + w - xoff2 + 2, y + yoff, 0);
			w -= xoff1 + xoff2;
			g.setColor(0xababab);
			g.drawRect(x + xoff1, y + yoff, w, h);
			int val = value;
			if (max <= 0 || val < 0) return;
			g.setColor(0x0000ff);
			g.fillRect(x + 2 + xoff1, y + yoff + 2, (int) ((double) w * ((double) val / (double) max)) - 3, h - 3);
		}
	}

	public void layout(Row row) {
		super.layout(row);
		final Font font = Item.font;
		if (!interactive) {
			int n2 = font.getHeight() + 4;
			if (label != null) n2 += font.getHeight() + 4;
			bounds[H] = Math.min(n2, screen.bounds[H]);
		} else {
			int n2 = font.getHeight() + 4;
			if (label != null) n2 += font.getHeight() + 4;
			bounds[H] = Math.min(n2, screen.bounds[H]);
		}
	}

	public int getMinimumWidth() {
		return Item.font.stringWidth("Some example text") + 6;
	}

	public int getMinimumHeight() {
		return (Item.font.getHeight() + 4) * (hasLabel() ? 2 : 1);
	}

	boolean isFocusable() {
		return true;
	}

	boolean keyScroll(int key, boolean repeat) {
		if (interactive) {
			if (key == Canvas.LEFT) {
				if (--value < 0) {
					value = 0;
					return false;
				}
				notifyStateChanged();
				return true;
			}
			if (key == Canvas.RIGHT) {
				if (++value > max) {
					value = max;
					return false;
				}
				notifyStateChanged();
				return true;
			}
		}
		return false;
	}
}
