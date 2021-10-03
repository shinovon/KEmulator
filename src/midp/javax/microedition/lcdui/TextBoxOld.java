package javax.microedition.lcdui;

import emulator.*;

public class TextBoxOld extends Screen
{
    private TextField aTextField1305;
    
    public TextBoxOld(final String s, final String s2, final int n, final int n2) {
        super(s);
        this.aTextField1305 = new TextField(null, s2, n, n2);
        super.items.add(this.aTextField1305);
        this.aTextField1305.screen = this;
        this.aTextField1305.isTextBox = true;
        this.aTextField1305.aBoolean177 = true;
        this.aTextField1305.inFocus = true;
        this.aTextField1305.screen.setItemCommands(this.aTextField1305);
    }
    
    protected void focusCaret() {
        this.aTextField1305.screen.setItemCommands(this.aTextField1305);
        Emulator.getEmulator().getScreen().getCaret().foucsItem(this.aTextField1305, super.bounds[0] + 4, Screen.fontHeight4 + 4);
    }
    
    public String getString() {
        return this.aTextField1305.getString();
    }
    
    public void setString(final String string) {
        this.aTextField1305.setString(string);
    }
    
    public int getChars(final char[] array) {
        return this.aTextField1305.getChars(array);
    }
    
    public void setChars(final char[] array, final int n, final int n2) {
        this.aTextField1305.setChars(array, n, n2);
    }
    
    public void insert(final String s, final int n) {
        this.aTextField1305.insert(s, n);
    }
    
    public void insert(final char[] array, final int n, final int n2, final int n3) {
        this.aTextField1305.insert(array, n, n2, n3);
    }
    
    public void delete(final int n, final int n2) {
        this.aTextField1305.delete(n, n2);
    }
    
    public int getMaxSize() {
        return this.aTextField1305.getMaxSize();
    }
    
    public int setMaxSize(final int maxSize) {
        return this.aTextField1305.setMaxSize(maxSize);
    }
    
    public int size() {
        return this.aTextField1305.size();
    }
    
    public int getCaretPosition() {
        return this.aTextField1305.getCaretPosition();
    }
    
    public void setConstraints(final int constraints) {
        this.aTextField1305.setConstraints(constraints);
    }
    
    public int getConstraints() {
        return this.aTextField1305.getConstraints();
    }
    
    public void setInitialInputMode(final String initialInputMode) {
        this.aTextField1305.setInitialInputMode(initialInputMode);
    }
    
    public void setTitle(final String title) {
        super.setTitle(title);
    }
    
    public void setTicker(final Ticker ticker) {
        super.setTicker(ticker);
    }
    
    protected void paint(final Graphics graphics) {
        this.layout();
        this.aTextField1305.paint(graphics);
    }
    
    protected void layout() {
        this.aTextField1305.layout();
        this.aTextField1305.bounds[1] = Screen.fontHeight4;
    }
    
}
