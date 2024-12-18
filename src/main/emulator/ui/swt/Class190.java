package emulator.ui.swt;

import emulator.Settings;
import emulator.graphics2D.awt.ImageAWT;
import emulator.graphics2D.swt.ImageSWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

final class Class190 implements PaintListener {
	private final Property aClass38_1483;

	Class190(final Property aClass38_1483) {
		super();
		this.aClass38_1483 = aClass38_1483;
	}

	public final void paintControl(final PaintEvent paintEvent) {
		if (Property.method356(this.aClass38_1483) == null) {
			Property.method369(this.aClass38_1483, 1);
		}
		if (Settings.g2d == 0) {
			((ImageSWT) Property.method356(this.aClass38_1483)).method12(paintEvent.gc, 0, 0);
			return;
		}
		if (Settings.g2d == 1) {
			((ImageAWT) Property.method356(this.aClass38_1483)).copyToScreen(paintEvent.gc);
		}
	}
}
