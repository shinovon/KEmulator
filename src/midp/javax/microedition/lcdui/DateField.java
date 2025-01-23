package javax.microedition.lcdui;

import emulator.Emulator;
import emulator.lcdui.c;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateField extends Item {
	public static final int DATE = 1;
	public static final int TIME = 2;
	public static final int DATE_TIME = 3;

	private static int width1 = Item.font.stringWidth("00") + 2;
	private static int width2 = Item.font.stringWidth("0000") + 2;
	private static int width3 = Item.font.stringWidth(":") + 2;

	private int mode;
	private Date date;
	private int pos;
	private boolean updateFocus;
	private int caretX, caretY;
	private StringBuffer buffer;
	private boolean typing;

	public DateField(final String s, final int n) {
		this(s, n, null);
	}

	public DateField(final String s, final int inputMode, final TimeZone timeZone) {
		super(s);
		this.setInputMode(inputMode);
		this.setDate(new Date());
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(final Date aDate348) {
		this.date = aDate348;
		repaintForm();
	}

	public int getInputMode() {
		return this.mode;
	}

	public void setInputMode(final int anInt349) {
		if (anInt349 != 1 && anInt349 != 2 && anInt349 != 3) {
			throw new IllegalArgumentException();
		}
		this.mode = anInt349;
		repaintForm();
	}

	void focus() {
		super.focus();
		Emulator.getEmulator().getScreen().getCaret().focusItem(this, caretX, caretY);
		updateFocus = true;
	}

	void defocus() {
		if (focused || !updateFocus) Emulator.getEmulator().getScreen().getCaret().defocusItem(this);
		if (focused) _input('\0');
		super.defocus();
		updateFocus = true;
	}

	void hidden() {
		if (focused || !updateFocus) Emulator.getEmulator().getScreen().getCaret().defocusItem(this);
		updateFocus = true;
	}

	synchronized void paint(Graphics g, int x, int y, int w, int h) {
		super.paint(g, x, y, w, h);

		g.setColor(0);
		int yo = y;
		if (labelArr != null && labelArr.length > 0) {
			g.setFont(Item.font);
			for (int i = 0; i < labelArr.length; ++i) {
				g.drawString(labelArr[i], x + 4, yo + 2, 0);
				yo += Item.font.getHeight() + 4;
			}
		}
		if (focused) g.setColor(-8355712);
		g.drawRect(2, yo, w - 4, bounds[H] - yo + y - 2);
		g.setFont(Screen.font);
		g.setColor(0);
		int fh = Screen.font.getHeight();

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		int xo = x + 4;
		int cx = xo;
		int pos = this.pos;
		String s = "";
		if ((mode & DATE) != 0) {
			if (focused && pos == 0) {
				if (typing) {
					s = buffer.toString();
				} else {
					g.setColor(-11178603);
					g.fillRect(xo, yo, width1, fh);
					g.setColor(-1);
				}
			}
			if (!focused || pos != 0 || !typing) {
				s = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
				if (s.length() < 2) s = "0".concat(s);
			}
			g.drawString(s, xo, yo, 0);
			if (pos == 0) cx = xo + Screen.font.stringWidth(s);
			xo += width1;
			g.setColor(0);
			g.drawString(".", xo, yo, 0);
			xo += width3;

			if (focused && pos == 1) {
				if (typing) {
					s = buffer.toString();
				} else {
					g.setColor(-11178603);
					g.fillRect(xo, yo, width1, fh);
					g.setColor(-1);
				}
			}
			if (!focused || pos != 1 || !typing) {
				s = Integer.toString(c.get(Calendar.MONTH) + 1);
				if (s.length() < 2) s = "0".concat(s);
			}
			g.drawString(s, xo, yo, 0);
			if (pos == 1) cx = xo + Screen.font.stringWidth(s);
			xo += width1;
			g.setColor(0);
			g.drawString(".", xo, yo, 0);
			xo += width3;

			if (focused && pos == 2) {
				if (typing) {
					s = buffer.toString();
				} else {
					g.setColor(-11178603);
					g.fillRect(xo, yo, width2, fh);
					g.setColor(-1);
				}
			}
			if (!focused || pos != 2 || !typing) {
				s = Integer.toString(c.get(Calendar.YEAR));
				while (s.length() < 4) s = "0".concat(s);
			}
			g.drawString(s, xo, yo, 0);
			if (pos == 2) cx = xo + Screen.font.stringWidth(s);
			xo += width2 + width3 + 8;
			pos -= 3;
		}
		if ((mode & TIME) != 0) {
			g.setColor(0);
			if (focused && pos == 0) {
				if (typing) {
					s = buffer.toString();
				} else {
					g.setColor(-11178603);
					g.fillRect(xo, yo, width1, fh);
					g.setColor(-1);
				}
			}
			if (!focused || pos != 0 || !typing) {
				s = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
				if (s.length() < 2) s = "0".concat(s);
			}
			g.drawString(s, xo, yo, 0);
			if (pos == 0) cx = xo + Screen.font.stringWidth(s);
			xo += width1;
			g.setColor(0);
			g.drawString(":", xo, yo, 0);
			xo += width3;

			if (focused && pos == 1) {
				if (typing) {
					s = buffer.toString();
				} else {
					g.setColor(-11178603);
					g.fillRect(xo, yo, width1, fh);
					g.setColor(-1);
				}
			}
			if (!focused || pos != 1 || !typing) {
				s = Integer.toString(c.get(Calendar.MINUTE));
				if (s.length() < 2) s = "0".concat(s);
			}
			g.drawString(s, xo, yo, 0);
			if (pos == 1) cx = xo + Screen.font.stringWidth(s);
		}
		if ((caretX != cx || caretY != yo + 2 || updateFocus) && focused) {
			updateFocus = false;
			caretX = cx;
			caretY = yo + 2;
			Emulator.getEmulator().getScreen().getCaret().focusItem(this, caretX, caretY);
		}
	}

	void layout(Row row) {
		super.layout(row);
		int n = 4;
		final int availableWidth = row.getAvailableWidth(screen.bounds[W]) - 8;
		if (hasLabel()) {
			labelArr = c.textArr(label, Item.font, availableWidth, availableWidth);
			n = 4 + (Item.font.getHeight() + 4) * labelArr.length;
		} else {
			labelArr = null;
		}
		bounds[H] = Math.min(n + Screen.font.getHeight() , screen.bounds[3]);
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
		if (key == Canvas.LEFT) {
			if (--pos < 0) {
				pos = 0;
				return false;
			}
			_input('\0');
			return true;
		} else if (key == Canvas.RIGHT) {
			int m = mode == DATE_TIME ? 5 : (mode == DATE ? 3 : 2);
			if (++pos >= m) {
				pos = m - 1;
				return false;
			}
			_input('\0');
			return true;
		}
		return false;
	}

	private static int monthDays(int month, int year) {
		switch (month) {
			case Calendar.FEBRUARY:
				return year % 400 == 0 || (year % 100 != 0 && year % 4 == 0) ? 29 : 28;
			case Calendar.APRIL:
			case Calendar.JUNE:
			case Calendar.SEPTEMBER:
			case Calendar.NOVEMBER:
				return 30;
			default:
				return 31;
		}
	}

	public synchronized void _input(char n) {
		int pos = this.pos;
		if (mode == TIME) pos += 3;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (!typing && n != 0) {
			typing = true;
			buffer = new StringBuffer().append(n);
		} else if (buffer != null) {
			if (n != 0) buffer.append(n);
			if (pos == 2) {
				if (buffer.length() >= 4 || n == 0) {
					int i = Integer.parseInt(buffer.toString());
					c.set(Calendar.DAY_OF_MONTH,
							Math.min(c.get(Calendar.DAY_OF_MONTH),
							monthDays(c.get(Calendar.MONTH), i)));
					c.set(Calendar.YEAR, i);
					buffer = null;
					typing = false;
					setDate(c.getTime());
				}
			} else if (buffer.length() >= 2 || n == 0) {
				int i = Math.max(0, Integer.parseInt(buffer.toString()));
				switch (pos) {
					case 0:
						i = Math.min(Math.max(1, i), monthDays(c.get(Calendar.MONTH), c.get(Calendar.YEAR)));
						c.set(Calendar.DAY_OF_MONTH, i);
						break;
					case 1:
						i = Math.max(1, Math.min(i, 12)) - 1;
						c.set(Calendar.DAY_OF_MONTH,
								Math.min(c.get(Calendar.DAY_OF_MONTH),
								monthDays(i, c.get(Calendar.YEAR))));
						c.set(Calendar.MONTH, i);
						break;
					case 3:
						i = Math.min(i, 23);
						c.set(Calendar.HOUR_OF_DAY, i);
						c.set(Calendar.SECOND, 0);
						break;
					case 4:
						i = Math.min(i, 59);
						c.set(Calendar.MINUTE, i);
						c.set(Calendar.SECOND, 0);
						break;
				}
				buffer = null;
				typing = false;
				setDate(c.getTime());
			}
		}
		repaintForm();
	}
}
