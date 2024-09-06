package com.siemens.mp.media;

public class NativePlayer implements Player {
	public Control[] getControls() {
		return new Control[0];
	}

	public Control getControl(String paramString) {
		return null;
	}

	public void realize() throws MediaException {

	}

	public void prefetch() throws MediaException {

	}

	public void start() throws MediaException {

	}

	public void stop() throws MediaException {

	}

	public void deallocate() {

	}

	public void close() {

	}

	public void setTimeBase(TimeBase paramTimeBase) throws MediaException {

	}

	public TimeBase getTimeBase() {
		return null;
	}

	public long setMediaTime(long paramLong) throws MediaException {
		return 0;
	}

	public long getMediaTime() {
		return 0;
	}

	public int getState() {
		return 0;
	}

	public long getDuration() {
		return 0;
	}

	public String getContentType() {
		return null;
	}

	public void setLoopCount(int paramInt) {

	}

	public void addPlayerListener(PlayerListener paramPlayerListener) {

	}

	public void removePlayerListener(PlayerListener paramPlayerListener) {

	}
}
