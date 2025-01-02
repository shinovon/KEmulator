package com.jblend.media;

public abstract class MediaPlayer {

	public static final int NO_DATA = 0;
	public static final int READY = 1;
	public static final int PLAYING = 2;
	public static final int PAUSED = 3;
	public static final int ERROR = 0x10000;

	protected static final int REAL_WIDTH = 0;
	protected static final int REAL_HEIGHT = 0;

	public abstract void setData(MediaData data);

	public abstract void play();

	public abstract void play(boolean isRepeat);

	public abstract void play(int count);

	public abstract void stop();

	public abstract void pause();

	public abstract void resume();

	public abstract int getState();

	public abstract void addMediaPlayerListener(MediaPlayerListener l);

	public abstract void removeMediaPlayerListener(MediaPlayerListener l);

//	protected static void addNativeMediaEventDispatcher(com.jblend.io.j2me.events.NativeMediaEventDispatcher dispatcher) {
//
//	}

}
