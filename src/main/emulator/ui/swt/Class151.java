package emulator.ui.swt;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

final class Class151 extends MouseAdapter {
	private final KeyPad aClass161_1396;

	Class151(final KeyPad aClass161_1396) {
		super();
		this.aClass161_1396 = aClass161_1396;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1396, 16);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1396, 16);
	}
}
