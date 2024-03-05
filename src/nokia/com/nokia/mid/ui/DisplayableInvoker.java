package com.nokia.mid.ui;

import javax.microedition.lcdui.Displayable;

public abstract class DisplayableInvoker {
    private static DisplayableInvoker sInvoker;

    protected static void setInvoker(DisplayableInvoker aInvoker) {
        sInvoker = aInvoker;
    }

    public static DisplayableInvoker getInvoker() {
        throw new RuntimeException("Not implemented yet.");
    }

    public abstract void setToBeDisposed(int paramInt, Displayable paramDisplayable);
}
