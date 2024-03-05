package com.nokia.mid.ui;

import javax.microedition.lcdui.Graphics;

public abstract class CanvasGraphicsItemPainter {
    protected CanvasGraphicsItem iItem;

    protected abstract int getHandle();

    protected abstract void Repaint(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

    protected void Repaint(Graphics aGraphics) {
        this.iItem.Repaint(aGraphics);
    }
}
