package javax.microedition.lcdui.game;

import emulator.*;
import javax.microedition.lcdui.*;

public abstract class GameCanvas extends Canvas
{
    public static final int UP_PRESSED = 2;
    public static final int DOWN_PRESSED = 64;
    public static final int LEFT_PRESSED = 4;
    public static final int RIGHT_PRESSED = 32;
    public static final int FIRE_PRESSED = 256;
    public static final int GAME_A_PRESSED = 512;
    public static final int GAME_B_PRESSED = 1024;
    public static final int GAME_C_PRESSED = 2048;
    public static final int GAME_D_PRESSED = 4096;
    private Graphics graphics;

    protected GameCanvas(final boolean b) {
        super();
    }
    
    protected Graphics getGraphics() {
        return graphics = new Graphics(Emulator.getEmulator().getScreen().getBackBufferImage(), Emulator.getEmulator().getScreen().getXRayScreenImage());
    }
    
    public int getKeyStates() {
        return super.m_keyStates;
    }
    
    public void paint(final Graphics graphics) {
        graphics.getImpl().drawImage(Emulator.getEmulator().getScreen().getBackBufferImage(), 0, 0);
    }
    
    public void flushGraphics(final int n, final int n2, final int n3, final int n4) {
        this.method220();
    }
    
    public void flushGraphics() {
        this.method220();
    }
    
    private void method220() {
        if(this != Emulator.getCanvas()) return;
        Displayable.checkForSteps();
        Displayable.fpsLimiter();
        if(graphics == null) getGraphics();
        this.paintSoftMenu(graphics);
        Emulator.getEventQueue().queueGraphicsFlush();
        Emulator.getEventQueue().waitGameRepaint();
        Displayable.resetXRayGraphics();
    }
}
