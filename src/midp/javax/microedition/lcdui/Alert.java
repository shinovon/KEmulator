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
    private String[] textArr;
    Displayable aDisplayable835;
	private Gauge gauge;
    
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
        return gauge;
    }
    
    public void setIndicator(final Gauge gauge) {
    	if(gauge.isInteractive()) throw new IllegalArgumentException();
    	this.gauge = gauge;
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
    
    protected void paint(Graphics g) {
        final int n = super.bounds[W] - 8;
        this.textArr = c.textArr(this.aString172, Screen.font, n, n);
        g.setColor(-16777216);
        int h = Screen.fontHeight4;
        for (int i = 0; i < this.textArr.length; ++i) {
            g.drawString(this.textArr[i], 4, h + 2, 0);
            h += Screen.font.getHeight() + 4;
        }
        if(gauge != null) {
        	gauge.screen = this;
        	gauge.bounds[X] = 0;
        	gauge.bounds[Y] = h;
        	gauge.bounds[W] = bounds[W];
        	gauge.paint(g);
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
