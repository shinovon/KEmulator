package org.lwjgl.input;

public class InfdevMouse implements Mouse.EmptyCursorGrabListener {
    static {
    }

    public int getX() {
        return Mouse.getAbsoluteX();
    }

    public int getY() {
        return Mouse.getAbsoluteY();
    }

    @Override
    public void onGrab(boolean grabbing) {

    }
}