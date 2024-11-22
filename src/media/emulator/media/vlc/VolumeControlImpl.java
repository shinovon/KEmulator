package emulator.media.vlc;

import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

class VolumeControlImpl implements VolumeControl {

	private final VLCPlayerImpl player;

	VolumeControlImpl(VLCPlayerImpl player) {
		this.player = player;
	}

	public int getLevel() {
		return 0;
	}

	public boolean isMuted() {
			throw new IllegalStateException();
	}

	public int setLevel(int p0) {
			throw new IllegalStateException();
	}

	public void setMute(boolean p0) {
			throw new IllegalStateException();
	}
}