package com.j_phone.media;

public abstract interface MediaPlayerListener {
	public static final int PLAYED = 0;
	public static final int STOPPED = 1;
	public static final int PAUSED = 2;

	public abstract void mediaStateChanged(int paramInt);
}
