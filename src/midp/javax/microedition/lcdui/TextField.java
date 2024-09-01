package javax.microedition.lcdui;

import emulator.*;
import emulator.lcdui.*;

public class TextField extends Item {
	public static final int ANY = 0;
	public static final int EMAILADDR = 1;
	public static final int NUMERIC = 2;
	public static final int PHONENUMBER = 3;
	public static final int URL = 4;
	public static final int DECIMAL = 5;
	public static final int PASSWORD = 65536;
	public static final int UNEDITABLE = 131072;
	public static final int SENSITIVE = 262144;
	public static final int NON_PREDICTIVE = 524288;
	public static final int INITIAL_CAPS_WORD = 1048576;
	public static final int INITIAL_CAPS_SENTENCE = 2097152;
	public static final int CONSTRAINT_MASK = 65535;
	private String string;
	private int maxSize;
	private int constraints;
	private String[] textArr;
	private int anInt29;
	private int anInt30;
	protected boolean isTextBox;

	public TextField(final String s, final String aString25, final int anInt349, final int anInt350) {
		super(s);
		this.string = aString25;
		this.maxSize = anInt349;
		this.constraints = anInt350;
	}

	public String getString() {
		return this.string;
	}

	public void setString(final String aString25) {
		this.string = aString25;
	}

	public int getChars(final char[] array) {
		if (this.string == null) {
			return 0;
		}
		final char[] charArray;
		System.arraycopy(charArray = this.string.toCharArray(), 0, array, 0, charArray.length);
		return charArray.length;
	}

	public void setChars(final char[] array, final int n, final int n2) {
		final char[] array2 = new char[n2];
		System.arraycopy(array, n, array2, 0, n2);
		this.setString(new String(array2));
	}

	public void insert(final String s, final int n) {
		final String aString25 = this.string;
		this.setString(aString25.substring(0, n) + s + aString25.substring(n));
	}

	public void insert(final char[] array, final int n, final int n2, final int n3) {
		final char[] array2 = new char[n2];
		System.arraycopy(array, n, array2, 0, n2);
		this.insert(new String(array2), n3);
	}

	public void delete(final int n, final int n2) {
		final String aString25 = this.string;
		this.setString(aString25.substring(0, n) + aString25.substring(n + n2));
	}

	public int getMaxSize() {
		return this.maxSize;
	}

	public int setMaxSize(final int anInt349) {
		return this.maxSize = anInt349;
	}

	public int size() {
		if (this.string == null) {
			return 0;
		}
		return this.string.length();
	}

	public int getCaretPosition() {
		return Emulator.getEmulator().getScreen().getCaret().getCaretPosition();
	}

	public void setConstraints(final int anInt28) {
		this.constraints = anInt28;
	}

	public int getConstraints() {
		return this.constraints;
	}

	public void setInitialInputMode(final String s) {
	}

	protected void focus() {
		super.focus();
		Emulator.getEmulator().getScreen().getCaret().focusItem(this, this.anInt29, this.anInt30);
	}

	protected void defocus() {
		super.defocus();
		Emulator.getEmulator().getScreen().getCaret().defocusItem(this);
	}

	protected void paint(Graphics g, int x, int y, int w, int h) {
		if (!this.isTextBox) {
			super.paint(g, x, y, w, h);
		} else {
			g.setColor(-16777216);
		}
		int n = y;
		if (super.labelArr != null && super.labelArr.length > 0) {
			g.setFont(Item.font);
			for (int i = 0; i < super.labelArr.length; ++i) {
				g.drawString(super.labelArr[i], x + 4, n + 2, 0);
				n += Item.font.getHeight() + 4;
			}
		}
		final int n3 = super.bounds[3] - n + y - 2;
		if (super.inFocus) {
			g.setColor(-8355712);
		}
		g.drawRect(2, n - 2, super.bounds[2] - 4, n3);
		g.setFont(Screen.font);
		if (super.inFocus) {
			g.setColor(8617456);
		}
		this.anInt29 = x + 4;
		this.anInt30 = n + 4;
		for (int j = 0; j < this.textArr.length; ++j) {
			g.drawString(this.textArr[j], x + 4, n + 2, 0);
			if ((n += Screen.font.getHeight() + 4) > super.screen.bounds[3]) {
				return;
			}
		}
	}

	protected void layout(Row row) {
		super.layout(row);
		int n = 4;
		final int n2 = row.getAvailableWidth(screen.bounds[W]) - 8;
		if (super.label != null) {
			super.labelArr = c.textArr(super.label, Item.font, n2, n2);
			n = 4 + (Item.font.getHeight() + 4) * super.labelArr.length;
		} else {
			super.labelArr = null;
		}
		final Font aFont173 = Screen.font;
		this.textArr = c.textArr((this.string == null) ? "" : this.string, aFont173, n2, n2);
		super.bounds[3] = Math.min(n + (aFont173.getHeight() + 4) * this.textArr.length, super.screen.bounds[3]);
	}

	public int getMinimumHeight() {
		final Font font = Item.font;
		return (font.getHeight() + 4) * (hasLabel() ? 2 : 1);
	}

	boolean isFocusable() {
		return true;
	}
}
