package javax.microedition.media;

public interface Player extends Controllable {
	public static final int CLOSED = 0;
	public static final int UNREALIZED = 100;
	public static final int REALIZED = 200;
	public static final int PREFETCHED = 300;
	public static final int STARTED = 400;
	public static final long TIME_UNKNOWN = -1L;

	void addPlayerListener(final PlayerListener p0) throws IllegalStateException;

	void close();

	void deallocate() throws IllegalStateException;

	String getContentType();

	long getDuration();

	long getMediaTime();

	int getState();

	void prefetch() throws IllegalStateException, MediaException;

	void realize() throws IllegalStateException, MediaException;

	void removePlayerListener(final PlayerListener p0) throws IllegalStateException;

	void setLoopCount(final int p0);

	long setMediaTime(final long p0) throws MediaException;

	void start() throws IllegalStateException, MediaException;

	void stop() throws IllegalStateException, MediaException;

	void setTimeBase(final TimeBase p0) throws MediaException;

	TimeBase getTimeBase();
}
