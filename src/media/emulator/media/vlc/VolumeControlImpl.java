package emulator.media.vlc;

import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

class VolumeControlImpl implements VolumeControl {

	private final VLCPlayerImpl player;

	VolumeControlImpl(VLCPlayerImpl player) {
		this.player = player;
	}

	public int getLevel() {
		if (player.released || player.mediaPlayer == null)
			throw new IllegalStateException();
		if (isMuted()) return 0;
		return player.volume = player.mediaPlayer.audio().volume();
	}

	public boolean isMuted() {
		if (player.released || player.mediaPlayer == null)
			throw new IllegalStateException();
		return player.mediaPlayer.audio().isMute();
	}

	public int setLevel(int p0) {
		if (player.released || player.mediaPlayer == null)
			throw new IllegalStateException();
		if (isMuted()) return 0;
		player.mediaPlayer.audio().setVolume(p0);
		player.notifyListeners(PlayerListener.VOLUME_CHANGED, player.volume = player.mediaPlayer.audio().volume());
		return player.volume;
	}

	public void setMute(boolean p0) {
		if (player.released || player.mediaPlayer == null)
			throw new IllegalStateException();
		player.mediaPlayer.audio().setMute(p0);
	}
}