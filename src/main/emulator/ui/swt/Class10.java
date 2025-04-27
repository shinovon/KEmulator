package emulator.ui.swt;

import emulator.Emulator;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

final class Class10 implements Runnable {
	private final M3GViewUI.Refresher a;

	Class10(final M3GViewUI.Refresher aClass64_567) {
		super();
		this.a = aClass64_567;
	}

	public final void run() {
		try {
			if (a.ui.canvas != null) {
				((EmulatorScreen) Emulator.getEmulator().getScreen()).pollKeyboard(a.ui.canvas);
			}
			if (M3GViewUI.method232(a.ui)) {
				if (!M3GViewUI.method242(a.ui)) {
					M3GViewUI.method511(a.ui);
					a.ui.aBoolean909 = true;
					return;
				}
			} else {
				final Rectangle clientArea = a.ui.canvas.getClientArea();
				final GC gc;
				(gc = new GC(a.ui.canvas)).setBackground(Display.getCurrent().getSystemColor(2));
				gc.fillRectangle(clientArea);
				gc.setForeground(Display.getCurrent().getSystemColor(3));
				gc.drawString("M3GView init .....", clientArea.width >> 2, clientArea.height >> 2, true);
				gc.dispose();
			}

		} catch (Exception ignored) {}

	}
}
