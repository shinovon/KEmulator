package emulator.media.capture;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;

public class CaptureItem extends CustomItem {

	private CapturePlayerImpl pl;

	CaptureItem(CapturePlayerImpl pl) {
		super("Capture");
		this.pl = pl;
	}

	protected int getMinContentWidth() {
		return 1;
	}

	protected int getMinContentHeight() {
		return 1;
	}

	protected int getPrefContentWidth(int p0) {
		return pl.getSourceWidth();
	}

	protected int getPrefContentHeight(int p0) {
		return pl.getSourceHeight();
	}

	protected void paint(Graphics g, int w, int h) {
		pl.paint(g, w, h);
	}

}
