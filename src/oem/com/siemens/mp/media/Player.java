package com.siemens.mp.media;

public abstract interface Player
		extends Controllable {
	public static final int UNREALIZED = 100;
	public static final int REALIZED = 200;
	public static final int PREFETCHED = 300;
	public static final int STARTED = 400;
	public static final int CLOSED = 0;
	public static final long TIME_UNKNOWN = -1L;

	public abstract void realize()
			throws MediaException;

	public abstract void prefetch()
			throws MediaException;

	public abstract void start()
			throws MediaException;

	public abstract void stop()
			throws MediaException;

	public abstract void deallocate();

	public abstract void close();

	public abstract void setTimeBase(TimeBase paramTimeBase)
			throws MediaException;

	public abstract TimeBase getTimeBase();

	public abstract long setMediaTime(long paramLong)
			throws MediaException;

	public abstract long getMediaTime();

	public abstract int getState();

	public abstract long getDuration();

	public abstract String getContentType();

	public abstract void setLoopCount(int paramInt);

	public abstract void addPlayerListener(PlayerListener paramPlayerListener);

	public abstract void removePlayerListener(PlayerListener paramPlayerListener);
}
