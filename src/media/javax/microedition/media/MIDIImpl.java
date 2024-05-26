package javax.microedition.media;

import emulator.media.EmulatorMIDI;

import javax.microedition.media.control.MIDIControlImpl;

public class MIDIImpl implements Player {
	Control[] controls;

	private int state = Player.UNREALIZED;
	private Control midiControl;

	public MIDIImpl() {
		super();
		this.midiControl = new MIDIControlImpl(this);
		this.controls = new Control[]{midiControl};
	}

	public void addPlayerListener(final PlayerListener playerListener) throws IllegalStateException {
		if (state == Player.CLOSED) throw new IllegalStateException();
	}

	public void close() {
		state = Player.CLOSED;
	}

	public void deallocate() throws IllegalStateException {
		if (state == Player.CLOSED) throw new IllegalStateException();
	}

	public String getContentType() {
		return null;
	}

	public long getDuration() {
		return 0L;
	}

	public long getMediaTime() {
		return 0L;
	}

	public int getState() {
		return state;
	}

	public void prefetch() throws IllegalStateException, MediaException {
		if (state == Player.CLOSED) throw new IllegalStateException();
		if (state < Player.PREFETCHED) {
			state = Player.PREFETCHED;
		}
		try {
			EmulatorMIDI.initDevice();
		} catch (Exception e) {
			e.printStackTrace();
			throw new MediaException(e);
		}
	}

	public void realize() throws IllegalStateException, MediaException {
		if (state == Player.CLOSED) throw new IllegalStateException();
		if (state < Player.REALIZED) {
			state = Player.REALIZED;
		}
	}

	public void removePlayerListener(final PlayerListener playerListener) throws IllegalStateException {
		if (state == Player.CLOSED) throw new IllegalStateException();
	}

	public void setLoopCount(final int n) {
	}

	public long setMediaTime(final long n) {
		return 0L;
	}

	public void start() throws IllegalStateException, MediaException {
		if (state == Player.CLOSED) throw new IllegalStateException();
		state = Player.STARTED;
	}

	public void stop() throws IllegalStateException, MediaException {
		if (state == Player.CLOSED) throw new IllegalStateException();
		state = Player.PREFETCHED;
	}

	public Control getControl(final String s) {
		if (s.equals("MIDIControl")) {
			return this.midiControl;
		}
		return null;
	}

	public Control[] getControls() {
		return this.controls;
	}

	public TimeBase getTimeBase() {
		return null;
	}

	public void setTimeBase(final TimeBase timeBase) throws MediaException {
	}
}
