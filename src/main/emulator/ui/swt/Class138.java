package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class138 extends MouseAdapter {
	private final KeyPad aClass161_1297;

	Class138(final KeyPad aClass161_1297) {
		super();
		this.aClass161_1297 = aClass161_1297;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1297, 9);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1297, 9);
	}
}
