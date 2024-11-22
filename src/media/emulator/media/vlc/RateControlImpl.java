package emulator.media.vlc;

import javax.microedition.media.control.RateControl;

class RateControlImpl implements RateControl {

	private final VLCPlayerImpl player;

	RateControlImpl(VLCPlayerImpl player) {
		this.player = player;
	}

	private static final int MAX_RATE = 200000;
	private static final int MIN_RATE = 50000;

	public int getMaxRate() {
		return MAX_RATE;
	}

	public int getMinRate() {
		return MIN_RATE;
	}

	public int getRate() {
		return 0;
	}

	public int setRate(int millirate) {
		return getRate();
	}
}