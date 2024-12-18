package emulator.ui.swt;

import emulator.Settings;
import emulator.graphics2D.awt.ImageAWT;
import emulator.graphics2D.swt.ImageSWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

final class Class195 implements PaintListener {
	private final Property aClass38_1499;

	Class195(final Property aClass38_1499) {
		super();
		this.aClass38_1499 = aClass38_1499;
	}

	public final void paintControl(final PaintEvent paintEvent) {
		if (Property.method382(this.aClass38_1499) == null) {
			Property.method369(this.aClass38_1499, 4);
		}
		if (Settings.g2d == 0) {
			((ImageSWT) Property.method382(this.aClass38_1499)).copyToScreen(paintEvent.gc);
			return;
		}
		if (Settings.g2d == 1) {
			((ImageAWT) Property.method382(this.aClass38_1499)).copyToScreen(paintEvent.gc);
		}
	}
}
