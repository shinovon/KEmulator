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
	private int mode = -1;
    
	private long lastIncrementTime;
	private boolean continuosDir;
	private int continuosValue;
	private static long continousIncTime = 100;
	
    public Gauge(String label, boolean interactive, int maxValue, int initialValue) {
        super(label);
        if(max == INDEFINITE) {
        	mode = initialValue;
        }
    	value = initialValue;
    	max = maxValue;
    	interact = interactive;
    }
    
    public void setLabel(String label) {
    	super.setLabel(label);
    }
    
    public void setLayout(int layout) {
    	super.setLayout(layout);
    }
    
    public void addCommand(Command cmd) {
    	super.addCommand(cmd);
    }
    
    public void setItemCommandListener(ItemCommandListener l) {
    	super.setItemCommandListener(l);
    }
    
    public void setPreferredSize(int width, int height) {
    	super.setPreferredSize(width, height);
    }
    
    public void setDefaultCommand(Command cmd) {
    	super.setDefaultCommand(cmd);
    }
    
    public void setValue(int value) {
    	if(max == -1) {
    		mode = value;
    		return;
    	}
    	this.value = value;
    }
    
    public int getValue() {
    	if(mode != -1) return mode;
        return value;
    }
    
    public void setMaxValue(int maxValue) {
    	max = maxValue;
    }
    
    public int getMaxValue() {
        return max;
    }
    
    public boolean isInteractive() {
        return interact;
    }
    
    protected void paint(Graphics g) {
        super.paint(g);
        final Font font = (this.font != null) ? this.font : Screen.font;
        g.setFont(font);
    	if(!interact) {
    		int max = this.max;
    		int val = this.value;
    		switch(mode) {
    		case -1: {
    			break;
    		}
    		default:
    			//TODO INCREMENTAL
    		case CONTINUOUS_RUNNING: {
    			System.out.println("continous");
    			max = 100;
				if(System.currentTimeMillis() - lastIncrementTime > continousIncTime) {
	    			if(continuosDir)
	    				continuosValue++;
	    			else
	    				continuosValue--;
					lastIncrementTime = System.currentTimeMillis();
    			}
    			if(continuosValue >= max || continuosValue <= 0) {
    				continuosDir = !continuosDir;
    			}
    			val = continuosValue;
    			break;
    		}
    		}
        	int x = super.bounds[X];
            int y = super.bounds[Y];
	        int w = super.bounds[W];
	        int h = font.getHeight();
    		if(label != null)
            	g.drawString(label, x, y, 0);
        	g.setColor(0xababab);
        	g.drawRect(x, y + h + 4, w, h); 
        	g.setColor(0x0000ff);
        	int off = 2;
        	g.fillRect(x + off, y + h + 4 + off, (int)((double)w * ((double)val/(double)max)) - off - 1, h - off - 1); 
    	} else {
    		//TODO
    	}
    }
    
    public void layout() {
    	super.layout();
    	if(!interact) {
	        final Font font = (this.font != null) ? this.font : Screen.font;
	        int n2 = font.getHeight() + 4;
	        if(label != null) n2 += font.getHeight() + 4;
	        super.bounds[H] = Math.min(n2, super.screen.bounds[H]);
    	} else {
    		//TODO
    	}
    }
}
