package javax.microedition.lcdui;

import emulator.*;

public class TextBox extends Screen {
    private TextField textField;

    public TextBox(final String s, final String s2, final int n, final int n2) {
        super(s);
        this.textField = new TextField(null, s2, n, n2);
        super.items.add(this.textField);
        this.textField.screen = this;
        this.textField.isTextBox = true;
        this.textField.shownOnForm = true;
        this.textField.inFocus = true;
        this.textField.screen.setItemCommands(this.textField);
    }

    protected void focusCaret() {
        this.textField.screen.setItemCommands(this.textField);
        Emulator.getEmulator().getScreen().getCaret().focusItem(this.textField, super.bounds[X] + 4, Screen.fontHeight4 + 4);
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
        return this.textField.getConstraints();
    }

    public void setInitialInputMode(final String initialInputMode) {
        this.textField.setInitialInputMode(initialInputMode);
    }

    public void setTitle(final String title) {
        super.setTitle(title);
    }

    public void setTicker(final Ticker ticker) {
        super.setTicker(ticker);
    }

    protected void paint(final Graphics graphics) {
        this.layout();
        this.textField.paint(graphics);
    }

    protected void layout() {
        this.textField.layout();
        this.textField.bounds[Y] = Screen.fontHeight4;
    }

    protected void defocus() {
        this.textField.defocus();
    }

}
