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
		if (player.released || player.mediaPlayer == null)
			throw new IllegalStateException();
		return (int) (player.mediaPlayer.status().rate() * 100000);
	}

	public int setRate(int millirate) {
		if (player.released || player.mediaPlayer == null)
			throw new IllegalStateException();
		if (millirate > MAX_RATE) millirate = MAX_RATE;
		if (millirate < MIN_RATE) millirate = MIN_RATE;
		player.mediaPlayer.controls().setRate(millirate / 100000F);
		return getRate();
	}
}