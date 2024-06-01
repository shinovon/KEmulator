package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class148 extends MouseAdapter {
	private final KeyPad aClass161_1393;

	Class148(final KeyPad aClass161_1393) {
		super();
		this.aClass161_1393 = aClass161_1393;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1393, 1);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1393, 1);
	}
}
