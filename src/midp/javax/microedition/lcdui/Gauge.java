package javax.microedition.lcdui;

import emulator.lcdui.LCDUIUtils;

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
		this.interactive = interactive;
		setMaxValue(maxValue);
		setValue(initialValue);
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
		if (max == INDEFINITE) {
			if (value < 0) value = 0;
			else if (value > INCREMENTAL_UPDATING) value = INCREMENTAL_UPDATING;
		} else {
			if (value < 0) value = 0;
			else if (value > max) value = max;
		}
		this.value = value;
		repaintForm();
	}

	public int getValue() {
		return value;
	}

	public void setMaxValue(int maxValue) {
		if ((interactive && maxValue == INDEFINITE)
			|| (maxValue != INDEFINITE && maxValue <= 0)) {
			throw new IllegalArgumentException();
		}
		if (maxValue != max) {
			value = 0;
		}
		if (maxValue != INDEFINITE) {
			if (value > maxValue) {
				value = maxValue;
			}
		}
		max = maxValue;
		repaintForm();
	}

	public int getMaxValue() {
		return max;
	}

	public boolean isInteractive() {
		return interactive;
	}

	void paint(Graphics g, int x, int y, int w, int h) {
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
			w -= 4;
			g.setColor(LCDUIUtils.borderColor);
			g.drawRect(x + 2, y + yoff, w, h);
			if (max <= 0 || val < 0) return;
			g.setColor(LCDUIUtils.gaugeColor);
			g.fillRect(x + 4, y + yoff + 2, (int) ((double) w * ((double) val / (double) max)) - 3, h - 3);
		} else {
			int xoff1 = Item.font.stringWidth("0") + 2;
			int xoff2 = Item.font.stringWidth(Integer.toString(max)) + 2;
			g.drawString("0", x + 2, y + yoff, 0);
			g.drawString(Integer.toString(max), x + w - xoff2 + 2, y + yoff, 0);
			w -= xoff1 + xoff2;
			g.setColor(LCDUIUtils.borderColor);
			g.drawRect(x + xoff1, y + yoff, w, h);
			int val = value;
			if (max <= 0 || val < 0) return;
			g.setColor(LCDUIUtils.gaugeColor);
			g.fillRect(x + 2 + xoff1, y + yoff + 2, (int) ((double) w * ((double) val / (double) max)) - 3, h - 3);
		}
	}

	void _layout(Row row) {
		super._layout(row);
		final Font font = Item.font;
		if (!interactive) {
			int n2 = font.getHeight() + 4;
			if (label != null) n2 += font.getHeight() + 4;
			bounds[H] = n2;
		} else {
			int n2 = font.getHeight() + 4;
			if (label != null) n2 += font.getHeight() + 4;
			bounds[H] = n2;
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
