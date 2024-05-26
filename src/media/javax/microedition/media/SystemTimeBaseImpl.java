package javax.microedition.media;

final class SystemTimeBaseImpl implements TimeBase {
	long time;

	public SystemTimeBaseImpl() {
		super();
		this.time = System.currentTimeMillis();
	}

	public final long getTime() {
		return (System.currentTimeMillis() - this.time) * 1000L;
	}
}
