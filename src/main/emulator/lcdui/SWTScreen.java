/*
Copyright (c) 2025 Arman Jussupgaliyev
*/
package emulator.lcdui;

import emulator.Emulator;
import emulator.KeyMapping;
import emulator.Settings;
import emulator.graphics2D.CopyUtils;
import emulator.graphics2D.swt.FontSWT;
import emulator.ui.swt.EmulatorScreen;
import emulator.ui.swt.SWTFrontend;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import java.awt.image.BufferedImage;

public class SWTScreen {

	Composite swtContent;
	private Rectangle swtContentArea;
	private boolean swtInitialized;

	static KeyListener swtKeyListener = new SwtKeyListener();

	static void syncExec(Runnable r) {
		SWTFrontend.syncExec(r);
	}

	static void safeSyncExec(Runnable r) {
		try {
			SWTFrontend.syncExec(r);
		} catch (SWTException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else {
				throw new RuntimeException(cause);
			}
		}
	}

	static Composite getSwtParent() {
		return ((EmulatorScreen) Emulator.getEmulator().getScreen()).getCanvas();
	}

	Composite _constructSwtContent(int style) {
		Composite c = new Composite(getSwtParent(), SWT.NONE);
		_setSwtStyle(c);
		return c;
	}

	void constructSwt() {
		syncExec(new Runnable() {
			public void run() {
				swtContent = _constructSwtContent(SWT.NONE);
				swtContent.setVisible(false);
				swtContentArea = _layoutSwtContent();
			}
		});
	}

	Rectangle _layoutSwtContent() {
		Rectangle area = getSwtParent().getClientArea();
		swtContent.setBounds(0, 0, area.width, area.height);
		return swtContent.getClientArea();
	}

	public Composite getSwtContent() {
		return swtContent;
	}

	public void swtHidden() {
	}

	public void swtShown() {
		if (swtContent != null && !swtContent.isDisposed()) {
			swtUpdateSizes();
		}
	}

	public void swtUpdateSizes() {
		Rectangle newArea = _layoutSwtContent();
		if (swtContentArea == null || !swtInitialized
				|| newArea.width != swtContentArea.width
				|| newArea.height != swtContentArea.height) {
			swtInitialized = true;
			swtContentArea = newArea;
			swtResized(newArea.width, newArea.height);
		}
	}

	public void swtResized(int w, int h) {
		if (this instanceof ListSWT) return;
		swtContent.setFont(getDefaultSWTFont(true));
	}

	void _setSwtStyle(Control c) {
		int color = LCDUIUtils.backgroundColor;
		c.setBackground(new Color(null, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF));
		color = LCDUIUtils.foregroundColor;
		c.setForeground(new Color(null, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF));

		c.setFont(getDefaultSWTFont(!(this instanceof ListSWT)));
	}

	static class SwtKeyListener implements KeyListener {

		public void keyPressed(KeyEvent keyEvent) {
			if (keyEvent.keyCode == SWT.F11 || keyEvent.keyCode == 16777261 || keyEvent.keyCode == 16777259) {
				((EmulatorScreen) Emulator.getEmulator().getScreen()).keyPressed(keyEvent);
				return;
			}
			int n = keyEvent.keyCode & 0xFEFFFFFF;
			Displayable d = Emulator.getCurrentDisplay().getCurrent();
			String r;
			if ((keyEvent.character >= 33 && keyEvent.character <= 90)
					|| (r = KeyMapping.replaceKey(n)) == null) return;
			n = Integer.parseInt(r);
			if (KeyMapping.isLeftSoft(n) || KeyMapping.isRightSoft(n)) {
				d.handleSoftKeyAction(n, true);
			}
		}

		public void keyReleased(KeyEvent keyEvent) {
		}
	}

	static org.eclipse.swt.graphics.Font getSWTFont(Font font, boolean autoScale) {
		if (font == null) return null;
		if (Settings.g2d == 0) {
			return ((FontSWT) font.getImpl()).getSWTFont();
		} else {
			float zoom = (autoScale ? ((EmulatorScreen) Emulator.getEmulator().getScreen()).getZoom() : 1f) * 0.68f;
			return new org.eclipse.swt.graphics.Font(SWTFrontend.getDisplay(),
					Emulator.getEmulator().getProperty().getDefaultFontName(),
					Math.max(2, (int) (font.getHeight() * zoom) - 1),
					font.getStyle() & ~Font.STYLE_UNDERLINED);
		}
	}

	static org.eclipse.swt.graphics.Font getDefaultSWTFont(boolean autoScale) {
		float zoom = (autoScale ? ((EmulatorScreen) Emulator.getEmulator().getScreen()).getZoom() : 1f) * 0.68f;
		return new org.eclipse.swt.graphics.Font(SWTFrontend.getDisplay(),
				Emulator.getEmulator().getProperty().getDefaultFontName(),
				Math.max(2, (int) (Font.getDefaultFont().getHeight() * zoom) - 1), 0);
	}

	public int getWidth() {
		return swtContentArea.width;
	}

	public int getHeight() {
		return swtContentArea.height;
	}

	protected void finalize() {
		syncExec(() -> {
			if (swtContent != null && !swtContent.isDisposed()) {
				swtContent.dispose();
			}
		});
	}

	static org.eclipse.swt.graphics.Image getSWTImage(Image img) {
		// TODO
		if (img == null) return null;
		if (Settings.g2d == 0) {
			return (org.eclipse.swt.graphics.Image) img._getImpl().getNative();
		}
		BufferedImage b = (BufferedImage) img._getImpl().getNative();
		return new org.eclipse.swt.graphics.Image(SWTFrontend.getDisplay(), CopyUtils.toSwt(b));
	}
}
