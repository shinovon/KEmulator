package javax.microedition.lcdui;

import emulator.*;
import org.eclipse.swt.widgets.Text;

public class TextBox extends Screen {
//	private TextField textField;

	Text swtText;

	public TextBox(final String s, final String s2, final int n, final int n2) {
		super(s);
		constructSwt();
	}

	protected void focusCaret() {
	}

	public String getString() {
		return this.textField.getString();
	}

	public void setString(final String string) {
		this.textField.setString(string);
	}

	public int getChars(final char[] array) {
		return this.textField.getChars(array);
	}

	public void setChars(final char[] array, final int n, final int n2) {
		this.textField.setChars(array, n, n2);
	}

	public void insert(final String s, final int n) {
		this.textField.insert(s, n);
	}

	public void insert(final char[] array, final int n, final int n2, final int n3) {
		this.textField.insert(array, n, n2, n3);
	}

	public void delete(final int n, final int n2) {
		this.textField.delete(n, n2);
	}

	public int getMaxSize() {
		return this.textField.getMaxSize();
	}

	public int setMaxSize(final int maxSize) {
		return this.textField.setMaxSize(maxSize);
	}

	public int size() {
		return this.textField.size();
	}

	public int getCaretPosition() {
		return this.textField.getCaretPosition();
	}

	public void setConstraints(final int constraints) {
		this.textField.setConstraints(constraints);
	}

	public int getConstraints() {
//		return this.textField.getConstraints();
	}

	public void setInitialInputMode(final String initialInputMode) {
//		this.textField.setInitialInputMode(initialInputMode);
	}

	public void setTitle(final String title) {
		super.setTitle(title);
	}

	public void setTicker(final Ticker ticker) {
		super.setTicker(ticker);
	}

	protected void paint(final Graphics graphics) {
	}

	protected void layout() {
	}

	protected void defocus() {
	}

}
