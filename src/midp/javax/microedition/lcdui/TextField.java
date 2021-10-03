package javax.microedition.lcdui;

import emulator.*;
import emulator.lcdui.*;

public class TextField extends Item
{
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
    private String aString25;
    private int anInt349;
    private int anInt28;
    private String[] aStringArray359;
    private int anInt29;
    private int anInt30;
    protected boolean isTextBox;
    
    public TextField(final String s, final String aString25, final int anInt349, final int anInt350) {
        super(s);
        this.aString25 = aString25;
        this.anInt349 = anInt349;
        this.anInt28 = anInt350;
    }
    
    public String getString() {
        return this.aString25;
    }
    
    public void setString(final String aString25) {
        this.aString25 = aString25;
    }
    
    public int getChars(final char[] array) {
        if (this.aString25 == null) {
            return 0;
        }
        final char[] charArray;
        System.arraycopy(charArray = this.aString25.toCharArray(), 0, array, 0, charArray.length);
        return charArray.length;
    }
    
    public void setChars(final char[] array, final int n, final int n2) {
        final char[] array2 = new char[n2];
        System.arraycopy(array, n, array2, 0, n2);
        this.setString(new String(array2));
    }
    
    public void insert(final String s, final int n) {
        final String aString25 = this.aString25;
        this.setString(aString25.substring(0, n) + s + aString25.substring(n));
    }
    
    public void insert(final char[] array, final int n, final int n2, final int n3) {
        final char[] array2 = new char[n2];
        System.arraycopy(array, n, array2, 0, n2);
        this.insert(new String(array2), n3);
    }
    
    public void delete(final int n, final int n2) {
        final String aString25 = this.aString25;
        this.setString(aString25.substring(0, n) + aString25.substring(n + n2));
    }
    
    public int getMaxSize() {
        return this.anInt349;
    }
    
    public int setMaxSize(final int anInt349) {
        return this.anInt349 = anInt349;
    }
    
    public int size() {
        if (this.aString25 == null) {
            return 0;
        }
        return this.aString25.length();
    }
    
    public int getCaretPosition() {
        return Emulator.getEmulator().getScreen().getCaret().getCaretPosition();
    }
    
    public void setConstraints(final int anInt28) {
        this.anInt28 = anInt28;
    }
    
    public int getConstraints() {
        return this.anInt28;
    }
    
    public void setInitialInputMode(final String s) {
    }
    
    protected void focus() {
        super.focus();
        Emulator.getEmulator().getScreen().getCaret().foucsItem(this, this.anInt29, this.anInt30);
    }
    
    protected void defocus() {
        super.defocus();
        Emulator.getEmulator().getScreen().getCaret().defoucsItem(this);
    }
    
    protected void paint(final Graphics graphics) {
        if (!this.isTextBox) {
            super.paint(graphics);
        }
        else {
            graphics.setColor(-16777216);
        }
        int n = super.bounds[1];
        int n2 = super.bounds[1];
        if (super.aStringArray175 != null && super.aStringArray175.length > 0) {
            graphics.setFont(Item.font);
            for (int i = 0; i < super.aStringArray175.length; ++i) {
                graphics.drawString(super.aStringArray175[i], super.bounds[0] + 4, n + 2, 0);
                n += Item.font.getHeight() + 4;
            }
            n2 = n - 2;
        }
        final int n3 = super.bounds[3] - n + super.bounds[1] - 2;
        if (super.inFocus) {
            graphics.setColor(-8355712);
        }
        graphics.drawRect(2, n2, super.bounds[2] - 4, n3);
        graphics.setFont(Screen.font);
        if (super.inFocus) {
            graphics.setColor(8617456);
        }
        this.anInt29 = super.bounds[0] + 4;
        this.anInt30 = n + 4;
        for (int j = 0; j < this.aStringArray359.length; ++j) {
            graphics.drawString(this.aStringArray359[j], super.bounds[0] + 4, n + 2, 0);
            if ((n += Screen.font.getHeight() + 4) > super.screen.bounds[3]) {
                return;
            }
        }
    }
    
    protected void layout() {
        super.layout();
        int n = 4;
        final int n2 = this.getPreferredWidth() - 8;
        if (super.label != null) {
            super.aStringArray175 = c.textArr(super.label, Item.font, n2, n2);
            n = 4 + (Item.font.getHeight() + 4) * super.aStringArray175.length;
        }
        else {
            super.aStringArray175 = null;
        }
        final Font aFont173 = Screen.font;
        this.aStringArray359 = c.textArr((this.aString25 == null) ? "" : this.aString25, aFont173, n2, n2);
        super.bounds[3] = Math.min(n + (aFont173.getHeight() + 4) * this.aStringArray359.length, super.screen.bounds[3]);
    }
}
