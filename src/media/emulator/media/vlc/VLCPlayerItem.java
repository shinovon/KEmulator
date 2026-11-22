package emulator.media.vlc;

import emulator.media.capture.CapturePlayerImpl;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;

public class VLCPlayerItem extends CustomItem {

	private final VLCPlayerImpl player;

	VLCPlayerItem(VLCPlayerImpl player) {
		super("");
		this.player = player;
	}

	protected int getMinContentWidth() {
		return 1;
	}

	protected int getMinContentHeight() {
		return 1;
	}

	protected int getPrefContentWidth(int p0) {
		return player.width;
	}

	protected int getPrefContentHeight(int p0) {
		return player.height;
	}

	protected void paint(Graphics g, int w, int h) {
		if (player == null)
			return;
		player.paint(g, 0, 0, w, h);
	}

	protected void update() {
		super.repaint();
	}
}
