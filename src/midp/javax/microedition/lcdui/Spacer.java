package javax.microedition.lcdui;

public class Spacer extends Item
{
    private int anInt349;
    private int anInt28;
    
    public Spacer(final int n, final int n2) {
        super(null);
        this.setMinimumSize(n, n2);
    }
    
    public void setMinimumSize(final int anInt349, final int anInt350) {
        this.anInt349 = anInt349;
        this.anInt28 = anInt350;
    }
    
    public void addCommand(final Command command) {
        throw new IllegalStateException();
    }
    
    public void setDefaultCommand(final Command command) {
        throw new IllegalStateException();
    }
    
    public void setLabel(final String s) {
        throw new IllegalStateException();
    }
    
    protected void paint(final Graphics graphics) {
        super.paint(graphics);
    }
    
    protected void layout() {
        super.layout();
        super.bounds[2] = Math.min(this.anInt349 + 4, super.screen.bounds[2]);
        super.bounds[3] = Math.min(this.anInt28 + 4, super.screen.bounds[3]);
    }
}
