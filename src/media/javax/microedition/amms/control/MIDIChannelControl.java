package javax.microedition.amms.control;

import javax.microedition.media.Control;

public abstract interface MIDIChannelControl extends Control {
	public abstract Control getChannelControl(String paramString, int paramInt);

	public abstract Control[] getChannelControls(int paramInt);
}
