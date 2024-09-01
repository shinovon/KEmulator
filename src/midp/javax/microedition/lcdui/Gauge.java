package javax.microedition.lcdui;

public class Gauge extends Item {
	public static final int INDEFINITE = -1;
	public static final int CONTINUOUS_IDLE = 0;
	public static final int INCREMENTAL_IDLE = 1;
	public static final int CONTINUOUS_RUNNING = 2;
	public static final int INCREMENTAL_UPDATING = 3;
	private int max;
	private int value;
	private boolean interact;

	private long lastIncrementTime;
	private boolean continuosDir;
	private int continuosValue;
	private static long continousIncTime = 1;

	public Gauge(String label, boolean interactive, int maxValue, int initialValue) {
		super(label);
		value = initialValue;
		max = maxValue;
		interact = interactive;
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

	public void setPreferredSize(int width, int height) {
		super.setPreferredSize(width, height);
	}

	public void setDefaultCommand(Command cmd) {
		super.setDefaultCommand(cmd);
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setMaxValue(int maxValue) {
		max = maxValue;
	}

	public int getMaxValue() {
		return max;
	}

	public boolean isInteractive() {
		return interact;
	}

	protected void paint(Graphics g, int x, int y, int w, int h) {
		super.paint(g, x, y, w, h);
		final Font font = Item.font;
		g.setFont(font);

		h = font.getHeight();
		int off = 2;
		int yoff = 0;
		if (label != null) {
			g.drawString(label, x, y, 0);
			yoff += h + 4;
		}

		if (!interact) {
			int max = this.max;
			int val = this.value;
			if (max == -1) {
				switch (value) {
					case -1: {
						break;
					}
					default:
						//TODO INCREMENTAL
					case CONTINUOUS_RUNNING: {
						max = 50;
						//if(System.currentTimeMillis() - lastIncrementTime > continousIncTime) {
						if (!continuosDir)
							continuosValue++;
						else
							continuosValue--;
						//lastIncrementTime = System.currentTimeMillis();
						//}
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
			g.setColor(0x0000ff);
			g.fillRect(x + off, y + yoff + off, (int) ((double) w * ((double) val / (double) max)) - off - 1, h - off - 1);
		} else {
			//TODO
			g.drawString("UNIMPLEMENTED", x, y + yoff, 0);
		}
	}

	public void layout(Row row) {
		super.layout(row);
		final Font font = Item.font;
		if (!interact) {
			int n2 = font.getHeight() + 4;
			if (label != null) n2 += font.getHeight() + 4;
			super.bounds[H] = Math.min(n2, super.screen.bounds[H]);
		} else {
			//TODO
			int n2 = font.getHeight() + 4;
			if (label != null) n2 += font.getHeight() + 4;
			super.bounds[H] = Math.min(n2, super.screen.bounds[H]);
		}
	}

	public int getPreferredWidth() {
		return -1;
	}

	public int getMinimumHeight() {
		final Font font = Item.font;
		return (font.getHeight() + 4) * (hasLabel() ? 2 : 1);
	}

	boolean isFocusable() {
		return true;
	}
}
