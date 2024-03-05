package com.nokia.mid.ui.lcdui;

import javax.microedition.lcdui.Display;

public abstract interface DisplayStateListener {
    public abstract void displayActive(Display paramDisplay);

    public abstract void displayInactive(Display paramDisplay);
}
