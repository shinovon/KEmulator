package emulator.media.vlc;

import javax.microedition.media.control.StopTimeControl;

class StopTimeControlImpl implements StopTimeControl {

	private final VLCPlayerImpl player;
	StopTimeControlImpl(VLCPlayerImpl player) {
		this.player = player;
	}

	@Override
	public long getStopTime() {
		return player.stopTime;
	}

	@Override
	public void setStopTime(long p0) {
		player.stopTime = p0;
	}
}
