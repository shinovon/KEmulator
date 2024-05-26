package com.sprintpcs.media;

public interface PlayerListener {
	public static final int AUDIO_DEVICE_UNAVAILABLE = 0;
	public static final int END_OF_DATA = 1;
	public static final int ERROR = 2;
	public static final int STARTED = 3;
	public static final int STOPPED = 4;
	public static final int PAUSED = 5;
	public static final int RESUME = 6;
	public static final int PREEMPTED = 7;

	public void playerUpdate(int var1, Object var2);
}
