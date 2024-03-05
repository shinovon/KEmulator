package com.nokia.microedition.media;

import java.io.IOException;
import javax.microedition.media.Control;

public abstract interface SeekControl
        extends Control {
    public abstract void seek(int paramInt)
            throws IOException;

    public abstract void close();
}
