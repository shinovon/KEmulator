package emulator.media.capture;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;

public class CaptureItem extends CustomItem {

	private CapturePlayerImpl pl;
	int w, h;

	CaptureItem(CapturePlayerImpl pl) {
		super("Capture");
		this.pl = pl;
		w = pl.getSourceWidth();
		h = pl.getSourceHeight();
	}

	protected int getMinContentWidth() {
		return 1;
	}

	protected int getMinContentHeight() {
		return 1;
	}

	protected int getPrefContentWidth(int p0) {
		return w;
	}

	protected int getPrefContentHeight(int p0) {
		return h;
	}

	protected void paint(Graphics g, int w, int h) {
		pl.paint(g, w, h);
	}

}
