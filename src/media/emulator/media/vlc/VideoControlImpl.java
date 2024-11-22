package emulator.media.vlc;

import javax.imageio.ImageIO;
import javax.microedition.media.MediaException;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class VideoControlImpl implements VideoControl {

	private final VLCPlayerImpl player;

	VideoControlImpl(VLCPlayerImpl player) {
		this.player = player;
	}

	public int getDisplayHeight() {
		return player.width;
	}

	public int getDisplayWidth() {
		return player.height;
	}

	public int getDisplayX() {
		return player.displayX;
	}

	public int getDisplayY() {
		return player.displayY;
	}

	public void setDisplayFullScreen(boolean b) {
		player.fullscreen = b;
	}

	public void setDisplayLocation(int x, int y) {
		player.displayX = x;
		player.displayY = y;
	}

	public void setDisplaySize(int w, int h) {
		if (player.width == w && player.height == h) return;
		player.width = w;
		player.height = h;
		player.notifyListeners(PlayerListener.SIZE_CHANGED, this);
	}

	public void setVisible(boolean b) {
		player.visible = b;
	}

	public int getSourceHeight() {
		return player.sourceHeight;
	}

	public int getSourceWidth() {
		return player.sourceWidth;
	}

	public byte[] getSnapshot(String p0) throws MediaException {
		return null;
	}


	public Object initDisplayMode(int p0, Object p1) {
		if (p0 == 0) {
			player.isItem = true;
			return player.getItem();
		}
		player.canvas = p1;
		return null;
	}
}