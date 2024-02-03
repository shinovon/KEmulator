package emulator.graphics3D.micro3d;

import javax.microedition.lcdui.*;
import emulator.graphics3D.*;
import emulator.*;

public final class Graphics3DImpl
{
    private Graphics g2d;
    private IGraphics3D g3d;
    
    public Graphics3DImpl() {
        super();
        this.g2d = null;
        this.g3d = Emulator.getEmulator().getGraphics3D();
    }
    
    public boolean bound() {
        return this.g2d != null;
    }
    
    public synchronized void bind(Graphics g) throws IllegalStateException, NullPointerException {
        if (this.g2d != null) {
            throw new IllegalStateException("Target already bound");
        }
        if (g == null) {
            throw new NullPointerException();
        }
        this.g3d.v3bind(g);
        this.g2d = g;
    }
    
    public synchronized void release(Graphics graphics) throws IllegalArgumentException, NullPointerException {
        if (graphics != this.g2d) {
            throw new IllegalArgumentException("Unknown target");
        }
        this.g3d.v3release(graphics);
        this.g2d = null;
    }
    
    public void flush() {
        this.g3d.v3flush();
    }
}
