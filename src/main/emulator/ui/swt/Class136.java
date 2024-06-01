package emulator.ui.swt;

import org.eclipse.swt.events.*;

final class Class136 extends MouseAdapter {
	private final KeyPad aClass161_1295;

	Class136(final KeyPad aClass161_1295) {
		super();
		this.aClass161_1295 = aClass161_1295;
	}

	public final void mouseUp(final MouseEvent mouseEvent) {
		KeyPad.method837(this.aClass161_1295, 10);
	}

	public final void mouseDown(final MouseEvent mouseEvent) {
		KeyPad.method840(this.aClass161_1295, 10);
	}
}
