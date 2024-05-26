package javax.microedition.amms.control;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public abstract interface EffectControl extends Control {
	public static final int SCOPE_LIVE_ONLY = 1;
	public static final int SCOPE_RECORD_ONLY = 2;
	public static final int SCOPE_LIVE_AND_RECORD = 3;

	public abstract void setEnabled(boolean paramBoolean);

	public abstract boolean isEnabled();

	public abstract void setScope(int paramInt) throws MediaException;

	public abstract int getScope();

	public abstract void setEnforced(boolean paramBoolean);

	public abstract boolean isEnforced();

	public abstract void setPreset(String paramString);

	public abstract String getPreset();

	public abstract String[] getPresetNames();
}
