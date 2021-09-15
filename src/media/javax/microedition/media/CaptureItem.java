package javax.microedition.media;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;

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

	protected void paint(Graphics g, int p1, int p2) {
		pl.paint(g);
		
	}

}
