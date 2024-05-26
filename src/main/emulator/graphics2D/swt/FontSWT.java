package emulator.graphics2D.swt;

import emulator.graphics2D.*;
import emulator.ui.swt.EmulatorImpl;
import org.eclipse.swt.graphics.*;

public final class FontSWT implements IFont {
	private Font font;
	private GC gc;

	public FontSWT(String s, int n, int n2) {
		this(s, n, n2, false);
	}

	public FontSWT(final String s, final int size, final int n2, boolean height) {
		super();
		this.font = new Font(null, s, size, n2);
		metrics();
		if (height && getHeight() != size) {
			float f = ((float) charWidth('W') / (float) getHeight()) * (float) size;
			font.dispose();
			this.font = new Font(null, s, (int) f, n2);
			metrics();
		}
	}

	private void metrics() {
		if (gc != null && !gc.isDisposed())
			gc.dispose();
		(this.gc = new GC(new Image(null, 1, 1))).setFont(this.font);


	}


	public final void finalize() {
		EmulatorImpl.asyncExec(() -> {
			try {
				if (font != null && !font.isDisposed()) {
					font.dispose();
				}
				if (gc != null && !gc.isDisposed()) {
					gc.dispose();
				}
			} catch (Exception ignored) {
			}
		});
	}

	public final Font method297() {
		return this.font;
	}

	public final int charWidth(final char c) {
		return this.gc.getCharWidth(c);
	}

	public final int stringWidth(final String s) {
		//int i = 0;
		//for(char c: s.toCharArray()) {
		//	i += this.gc.getCharWidth(c);
		//}
		//return i;
		return this.gc.stringExtent(s).x;
	}

	public final int getHeight() {
		return this.gc.getFontMetrics().getHeight();
	}

	public final int getAscent() {
		return this.gc.getFontMetrics().getAscent();
	}

	public final int getLeading() {
		return this.gc.getFontMetrics().getLeading();
	}
}
