package emulator.graphics3D.micro3d;

import javax.microedition.lcdui.*;
import emulator.graphics3D.*;
import emulator.*;

public final class d
{
    private Graphics aGraphics1;
    private IGraphics3D anIGraphics3D2;
    
    public d() {
        super();
        this.aGraphics1 = null;
        this.anIGraphics3D2 = Emulator.getEmulator().getGraphics3D();
    }
    
    public final boolean bound() {
        return this.aGraphics1 != null;
    }
    
    public final synchronized void a(final Graphics aGraphics1) throws IllegalStateException, NullPointerException {
        if (this.aGraphics1 != null) {
            throw new IllegalStateException("Target already bound");
        }
        if (aGraphics1 == null) {
            throw new NullPointerException();
        }
        this.anIGraphics3D2.v3bind(aGraphics1);
        this.aGraphics1 = aGraphics1;
    }
    
    public final synchronized void b(final Graphics graphics) throws IllegalArgumentException, NullPointerException {
        if (graphics != this.aGraphics1) {
            throw new IllegalArgumentException("Unknown target");
        }
        this.anIGraphics3D2.v3release(graphics);
        this.aGraphics1 = null;
    }
    
    public final void a() {
        this.anIGraphics3D2.v3flush();
    }
}
