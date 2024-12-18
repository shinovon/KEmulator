package com.nokia.microedition.media;

import javax.microedition.media.Control;
import java.io.IOException;

public abstract interface SeekControl
		extends Control {
	public abstract void seek(int paramInt)
			throws IOException;

	public abstract void close();
}
