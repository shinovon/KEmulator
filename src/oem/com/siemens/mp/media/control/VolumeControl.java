package com.siemens.mp.media.control;

import com.siemens.mp.media.Control;

public abstract interface VolumeControl
		extends Control {
	public abstract void setMute(boolean paramBoolean);

	public abstract boolean isMuted();

	public abstract int setLevel(int paramInt);

	public abstract int getLevel();
}
