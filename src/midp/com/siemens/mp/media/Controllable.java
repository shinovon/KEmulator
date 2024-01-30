package com.siemens.mp.media;

public abstract interface Controllable {
    public abstract Control[] getControls();

    public abstract Control getControl(String paramString);
}
