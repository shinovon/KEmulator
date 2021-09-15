package javax.microedition.lcdui.game;

import javax.microedition.lcdui.*;

public abstract class Layer
{
    int anInt598;
    int anInt600;
    int anInt601;
    int anInt602;
    boolean aBoolean599;
    
    Layer() {
        super();
        this.aBoolean599 = true;
    }
    
    Layer(final int n, final int n2) {
        super();
        this.aBoolean599 = true;
        this.method335(n);
        this.method336(n2);
    }
    
    public void setPosition(final int anInt598, final int anInt599) {
        this.anInt598 = anInt598;
        this.anInt600 = anInt599;
    }
    
    public void move(final int n, final int n2) {
        this.anInt598 += n;
        this.anInt600 += n2;
    }
    
    public final int getX() {
        return this.anInt598;
    }
    
    public final int getY() {
        return this.anInt600;
    }
    
    public final int getWidth() {
        return this.anInt601;
    }
    
    public final int getHeight() {
        return this.anInt602;
    }
    
    public void setVisible(final boolean aBoolean599) {
        this.aBoolean599 = aBoolean599;
    }
    
    public final boolean isVisible() {
        return this.aBoolean599;
    }
    
    public abstract void paint(final Graphics p0);
    
    final void method335(final int anInt601) {
        if (anInt601 < 0) {
            throw new IllegalArgumentException();
        }
        this.anInt601 = anInt601;
    }
    
    final void method336(final int anInt602) {
        if (anInt602 < 0) {
            throw new IllegalArgumentException();
        }
        this.anInt602 = anInt602;
    }
}
