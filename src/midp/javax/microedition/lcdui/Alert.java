package javax.microedition.lcdui;

import emulator.*;
import emulator.lcdui.*;

public class Alert extends Screen
{
    public static final Command DISMISS_COMMAND;
    public static final int FOREVER = -2;
    int anInt24;
    int anInt178;
    String aString172;
    private String[] aStringArray175;
    Displayable aDisplayable835;
    
    public Alert(final String s) {
        this(s, null, null, null);
    }
    
    public Alert(final String s, final String aString172, final Image image, final AlertType alertType) {
        super(s);
        this.aString172 = aString172;
        final int n = 1000;
        this.anInt178 = n;
        this.anInt24 = n;
        this.aDisplayable835 = Display.current;
        super.addCommand(Alert.DISMISS_COMMAND);
    }
    
    public void addCommand(final Command command) {
        if (command == Alert.DISMISS_COMMAND) {
            return;
        }
        super.removeCommand(Alert.DISMISS_COMMAND);
        super.addCommand(command);
    }
    
    public void removeCommand(final Command command) {
        super.removeCommand(command);
        if (this.isCommandsEmpty()) {
            super.addCommand(Alert.DISMISS_COMMAND);
        }
    }
    
    public int getDefaultTimeout() {
        return 1000;
    }
    
    public int getTimeout() {
        return this.anInt24;
    }
    
    public void setTimeout(final int n) {
        if (n < 0 && n != -2) {
            throw new IllegalArgumentException("time should be positive");
        }
        this.anInt24 = n;
        this.anInt178 = n;
    }
    
    public Image getImage() {
        return null;
    }
    
    public void setImage(final Image image) {
    }
    
    public Gauge getIndicator() {
        return null;
    }
    
    public void setIndicator(final Gauge gauge) {
    }
    
    public String getString() {
        return this.aString172;
    }
    
    public void setString(final String aString172) {
        this.aString172 = aString172;
    }
    
    public void setType(final AlertType alertType) {
    }
    
    protected void close() {
        Emulator.getCurrentDisplay().setCurrent(this.aDisplayable835);
    }
    
    protected void paint(final Graphics graphics) {
        final int n = super.anIntArray21[2] - 8;
        this.aStringArray175 = c.method175(this.aString172, Screen.aFont173, n, n);
        graphics.setColor(-16777216);
        int anInt181 = Screen.anInt181;
        for (int i = 0; i < this.aStringArray175.length; ++i) {
            graphics.drawString(this.aStringArray175[i], 4, anInt181 + 2, 0);
            anInt181 += Screen.aFont173.getHeight() + 4;
        }
        if (this.anInt178 > 0) {
            this.anInt178 -= 100;
            return;
        }
        if (this.anInt24 != -2) {
            this.close();
        }
    }
    
    static {
        DISMISS_COMMAND = new Command("OK", 4, 0);
    }
}
