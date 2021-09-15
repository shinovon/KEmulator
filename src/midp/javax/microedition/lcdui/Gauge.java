package javax.microedition.lcdui;

public class Gauge extends Item
{
    public static final int INDEFINITE = -1;
    public static final int CONTINUOUS_IDLE = 0;
    public static final int INCREMENTAL_IDLE = 1;
    public static final int CONTINUOUS_RUNNING = 2;
    public static final int INCREMENTAL_UPDATING = 3;
	private int max;
	private int value;
	private boolean interact;
    
    public Gauge(final String s, final boolean b, final int n, final int n2) {
        super(s);
    	value = n2;
    	max = n;
    	interact = b;
    }
    
    public void setLabel(final String s) {
    	super.setLabel(s);
    }
    
    public void setLayout(final int n) {
    	super.setLayout(n);
    }
    
    public void addCommand(final Command command) {
    	super.addCommand(command);
    }
    
    public void setItemCommandListener(final ItemCommandListener itemCommandListener) {
    	super.setItemCommandListener(itemCommandListener);
    }
    
    public void setPreferredSize(final int n, final int n2) {
    	super.setPreferredSize(n, n2);
    }
    
    public void setDefaultCommand(final Command command) {
    	super.setDefaultCommand(command);
    }
    
    public void setValue(final int n) {
    	value = n;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setMaxValue(final int n) {
    	max = n;
    }
    
    public int getMaxValue() {
        return max;
    }
    
    public boolean isInteractive() {
        return interact;
    }
    
    protected void paint(final Graphics graphics) {
        super.paint(graphics);
    }
}
