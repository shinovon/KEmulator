package javax.microedition.lcdui;

import emulator.Emulator;
import emulator.lcdui.LCDUIUtils;
import emulator.lcdui.TextUtils;

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
	private int caretX;
	private int caretY;
	protected boolean isTextBox;
	private boolean updateFocus;
	private boolean swtFocused;

	public TextField(final String label, final String text, final int maxSize, final int constraints) {
		super(label);
		this.string = text;
		this.maxSize = maxSize;
		this.constraints = constraints;
	}

	public String getString() {
		if (string == null) {
			return "";
		}
		return this.string;
	}

	public void setString(String s) {
		this.string = s == null ? "" : s;
		if (swtFocused)
			Emulator.getEmulator().getScreen().getCaret().updateText(this, string);
		layoutForm();
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

	public int setMaxSize(final int size) {
		maxSize = size;
		if (string != null && string.length() > size) {
			setString(string.substring(0, size - 1));
		}
		return maxSize;
	}

	public int size() {
		if (this.string == null) {
			return 0;
		}
		return this.string.length();
	}

	public int getCaretPosition() {
		if (!swtFocused) return 0;
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

	void focus() {
		super.focus();
		Emulator.getEmulator().getScreen().getCaret().focusItem(this, this.caretX, this.caretY);
		swtFocused = true;
		updateFocus = true;
	}

	void defocus() {
		if (focused || !updateFocus) {
			Emulator.getEmulator().getScreen().getCaret().defocusItem(this);
			swtFocused = false;
		}
		super.defocus();
		updateFocus = true;
	}

	void hidden() {
		if (focused || !updateFocus) {
			Emulator.getEmulator().getScreen().getCaret().defocusItem(this);
			swtFocused = false;
		}
		updateFocus = true;
	}

	void paint(Graphics g, int x, int y, int w, int h) {
		if (!this.isTextBox) {
			super.paint(g, x, y, w, h);
		} else {
			g.setColor(LCDUIUtils.foregroundColor);
		}
		int yo = y;
		if (labelArr != null && labelArr.length > 0) {
			g.setFont(labelFont);
			for (int i = 0; i < labelArr.length; ++i) {
				g.drawString(labelArr[i], x + 4, yo + 2, 0);
				yo += labelFont.getHeight() + 4;
			}
		}
		if (focused) g.setColor(0xFF808080);
		g.drawRect(x + 2, yo, w - 4, bounds[H] - yo + y - 2);
		g.setFont(Screen.font);
		if (focused) g.setColor(0xFF837DF0);
		if ((caretX != x + 3 || caretY != yo + 1 || updateFocus) && focused && swtFocused) {
			updateFocus = false;
			caretX = x + 3;
			caretY = yo + 1;
			Emulator.getEmulator().getScreen().getCaret().focusItem(this, this.caretX, this.caretY);
		}
		if (textArr.length == 0) return;
		g.drawString(this.textArr[0], x + 4, yo + 2, 0);
	}

	void layout(Row row) {
		super.layout(row);
		int n = 4;
		final int availableWidth = row.getAvailableWidth(screen.bounds[W]) - 8;
		if (hasLabel()) {
			labelArr = TextUtils.textArr(label, labelFont, availableWidth, availableWidth);
			n = 4 + (labelFont.getHeight() + 4) * labelArr.length;
		} else {
			labelArr = null;
		}
		final Font aFont173 = Screen.font;
		this.textArr = TextUtils.textArr((this.string == null) ? "" : this.string, aFont173, availableWidth, availableWidth);
		bounds[H] = Math.max(getMinimumHeight(), Math.min(n + (aFont173.getHeight() + 4), screen.bounds[H]));
	}

	public int getMinimumWidth() {
		return Item.font.stringWidth("Something") + 6;
	}

	public int getMinimumHeight() {
		return (Item.font.getHeight() + 4) * (hasLabel() ? 2 : 1);
	}

	boolean isFocusable() {
		return true;
	}

	boolean keyScroll(int key, boolean repeat) {
		return key == Canvas.LEFT || key == Canvas.RIGHT;
	}

	public int _getTextAreaWidth() {
		return screen.bounds[W] - 5;
	}

	public int _getTextAreaHeight() {
		return Screen.font.getHeight() + 5;
	}

	 public boolean _isUneditable() {
		return (constraints & UNEDITABLE) == UNEDITABLE;
	 }

	public void _setString(String text) {
		this.string = text;
		layoutForm();
		notifyStateChanged();
	}

	public void _swtFocusLost() {
		swtFocused = false;
	}

	protected void _itemApplyCommand() {
		focus();
	}
	
	public void setPreferredSize(int width, int height) {
	}
}
